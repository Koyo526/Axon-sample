# Axon Level ONE - Chapter 4 最小構成サンプル（Order） 仕様書

> 本ドキュメントは、要件定義書（requirements.md）に基づき、ドメインモデルと API の仕様を定義する。

---

## 1. ドメインモデル仕様

### 1.1 Order（注文）

本サンプルで扱う唯一のドメインモデルである。

| フィールド | 型 | 説明 | 制約 |
|-----------|-----|------|------|
| `orderId` | `String` | 注文の一意識別子 | UUID 形式。システムが自動生成する |
| `productName` | `String` | 商品名 | 任意の文字列。バリデーションなし |

> 補足: 本番では `productId`（商品ID）、`quantity`（数量）、`price`（価格）などが必要だが、
> Level ONE では学習目的のため最小限に絞っている。

### 1.2 Order のライフサイクル（本サンプルのスコープ）

```
（存在しない） ──[CreateOrderCommand]──> CREATED
```

- 本サンプルでは `CREATED` 状態のみ存在する
- Cancel / Update などの状態遷移は Out of Scope

---

## 2. メッセージ仕様

Axon Framework におけるメッセージ（Command / Event）の仕様を定義する。

### 2.1 CreateOrderCommand

「注文を作成したい」という **意図** を表すメッセージ。

| フィールド | 型 | 説明 | 必須 |
|-----------|-----|------|------|
| `orderId` | `String` | 注文ID（UUID） | Yes |
| `productName` | `String` | 商品名 | Yes |

- `orderId` は Controller 層で `UUID.randomUUID().toString()` により生成する
- `@TargetAggregateIdentifier` を `orderId` に付与し、Axon がルーティング先の Aggregate を特定できるようにする

### 2.2 OrderCreatedEvent

「注文が作成された」という **事実** を表すメッセージ。

| フィールド | 型 | 説明 |
|-----------|-----|------|
| `orderId` | `String` | 注文ID |
| `productName` | `String` | 商品名 |

- Event は Immutable（不変）である
- Aggregate 内の `@CommandHandler` で `AggregateLifecycle.apply()` を通じて発行される

---

## 3. API 仕様

### 3.1 注文作成 API

| 項目 | 値 |
|------|-----|
| Method | `POST` |
| Path | `/orders` |
| Content-Type | `application/json` |

#### リクエストボディ

```json
{
  "productName": "Coffee"
}
```

| フィールド | 型 | 説明 | 必須 |
|-----------|-----|------|------|
| `productName` | `String` | 商品名 | Yes（バリデーションなし） |

> 注: `orderId` はリクエストに含めない。Controller が UUID を自動生成する。

#### レスポンス: 成功時

| 項目 | 値 |
|------|-----|
| HTTP Status | `200 OK` |
| Content-Type | `application/json` |

```json
{
  "orderId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "status": "CREATED"
}
```

| フィールド | 型 | 説明 |
|-----------|-----|------|
| `orderId` | `String` | 生成された注文ID（UUID） |
| `status` | `String` | 固定値 `"CREATED"` |

#### レスポンス: 異常時

本サンプルではエラーハンドリングを最小限とする。

| HTTP Status | 条件 | 備考 |
|-------------|------|------|
| `500 Internal Server Error` | Axon 内部でのエラー | Spring Boot デフォルトのエラーレスポンス |

> 注: 本番では適切なエラーハンドリング（400, 409 等）が必要だが、本サンプルでは省略する。

---

## 4. ログ出力仕様

動作確認のため、以下のタイミングで SLF4J Logger によるログを出力する。

| タイミング | クラス | ログレベル | 出力内容の例 |
|-----------|--------|-----------|-------------|
| Command 受信時 | `OrderAggregate` | `INFO` | `Handling CreateOrderCommand: orderId={}, productName={}` |
| Event 適用時 | `OrderAggregate` | `INFO` | `Applying OrderCreatedEvent: orderId={}, productName={}` |

> 注: ログはあくまで学習・動作確認用であり、本番ではログレベルや内容を適切に設計する必要がある。

---

## 5. 動作確認手順

### 5.1 前提

- アプリケーションが `localhost:8080` で起動していること

### 5.2 注文作成の確認

#### リクエスト

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"productName": "Coffee"}'
```

#### 期待するレスポンス

```json
{
  "orderId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "status": "CREATED"
}
```

> `orderId` は実行ごとに異なる UUID が返る。

#### 期待するログ出力

アプリケーションのコンソールに以下のようなログが出力される。

```
INFO  c.e.a.order.aggregate.OrderAggregate - Handling CreateOrderCommand: orderId=a1b2c3d4-..., productName=Coffee
INFO  c.e.a.order.aggregate.OrderAggregate - Applying OrderCreatedEvent: orderId=a1b2c3d4-..., productName=Coffee
```

### 5.3 確認チェックリスト

- [ ] `curl` コマンドが `200 OK` を返す
- [ ] レスポンスに `orderId`（UUID 形式）が含まれる
- [ ] レスポンスに `"status": "CREATED"` が含まれる
- [ ] コンソールログに `Handling CreateOrderCommand` が表示される
- [ ] コンソールログに `Applying OrderCreatedEvent` が表示される

---

## 変更履歴

| 日付 | 内容 |
|------|------|
| 2026-02-07 | 初版作成（plan.md セクション 5〜6 + 補完） |
