## ユーザーストーリー計画 のプロンプト

あなたの役割: あなたは熟練したシステムアーキテクトです。
以下のタスクセクションに記載されているシステムを開発の設計とタスク管理を行います。ドキュメントはすべて日本語で生成してください。

作業の計画を立て、aidlc-docs/inception/task_plan.md ファイルにステップを記載してください。
各ステップにチェックボックスを付けてください。いずれかのステップで私の説明が必要な場合は、[Question] タグを付けて質問を追加し、私が回答を記入するための空の [Answer] タグを作成してください。
独自の判断や決定は行わないでください。計画を作成したら、私のレビューと承認を求めてください。
承認後、その計画を 1 ステップずつ実行できます。各ステップが完了したら、計画のチェックボックスに完了マークを付けてください。また、作業内容のレビューを求めてください。


あなたのタスク:
これから技術同人誌でAxonの入門書を書きます。
タイトルは『Axon Level ONE』です。こちらに掲載するサンプルコードを下記の要件をレビューした上で、要件・仕様・設計ドキュメントを作成してください。
（環境構築は未設定）

# Axon Level ONE — Chapter 4 最小構成サンプル（Order） 要件・仕様・設計

本ドキュメントは、技術同人誌「Axon Level ONE」の **Chapter 4: 最小構成で動かす Axon** で扱うサンプルコードについて、要件・仕様・設計を整理する。

---

## 1. 目的（このサンプルで達成したいこと）

### 1.1 学習ゴール
読者が以下を「体験」として理解できる状態を目指す。

- Axon Framework + Spring Boot の最小セットアップができる
- **Command → Event → 状態更新（Event Sourcing）** の流れを追える
- `@Aggregate` / `@CommandHandler` / `AggregateLifecycle.apply()` / `@EventSourcingHandler` の役割が分かる（完全理解でなくて良い）
- REST API から Command を投げ、動作を確認できる

### 1.2 スコープ方針（Level ONE）
- 本サンプルは **「動かして流れを見る」** ことが目的
- 本番向けの設計（永続化・分散・運用・監視・性能）は扱わない

---

## 2. スコープ（やること / やらないこと）

### 2.1 やること（In Scope）
- ドメイン：Order（注文）
- ユースケース：**注文を作成する**
- Axon の構成要素
    - Command（CreateOrderCommand）
    - Event（OrderCreatedEvent）
    - Aggregate（OrderAggregate）
- REST API で Command を発行する（Controller）
- 動作確認（ログ or レスポンスで確認）

### 2.2 やらないこと（Out of Scope）
以下は「入門の最小構成」から外す。

- Cancel / Update / Payment などの追加ユースケース
- Saga（Chapter 3 で概念のみ扱う想定）
- Query Model の永続化（DB）
- Event Store の永続化（Axon Server / JDBC / Mongo）
- 認証認可、例外設計、リトライ、冪等性などの実運用設計
- Distributed / Microservices / Messaging の詳細

---

## 3. 前提・環境

### 3.1 前提
- Java / Spring Boot の基礎知識がある
- REST API の呼び出し（curl など）ができる

### 3.2 開発環境（推奨）
- Java: 17 以上
- Spring Boot: 3.x 系
- Axon Framework: 4.x 系（Spring Boot Starter を利用）

> ※ Axon のバージョンやスターターの名称は執筆時点で変わる可能性があるため、章内では「使用した依存関係のスニペット」を明記する。

---

## 4. 要件（Requirements）

### 4.1 機能要件
- `POST /orders` により注文作成を行える
- 注文作成により **OrderCreatedEvent** が発行される
- Aggregate の状態が Event により設定される（Event Sourcing）

### 4.2 非機能要件（最小）
- 読者がローカルで 5〜10 分程度で動かせる
- コード量が少なく、追いやすい（4ファイル程度を目標）
- 説明対象が増えないよう、外部サービス依存を避ける

---

## 5. ドメイン仕様（Order）

### 5.1 ドメインモデル（最小）
- Order（注文）
    - `orderId` : String（UUID想定）
    - `productName` : String（簡易のため ID ではなく名称）

> 補足：本番では productId / quantity などが必要だが、Level ONE では削る。

---

## 6. API 仕様

### 6.1 注文作成 API
- Method: `POST`
- Path: `/orders`
- Request Body:
  ```json
  {
    "productName": "Coffee"
  }
  ```
- Response Body:
  ```json
  {
  "orderId": "a1b2c3d4-....",
  "status": "CREATED"
  }
  ```
### バリデーション方針（最小）
- productName が空なら 400 にする、または簡略のため省略しても良い
- 本では「本番ではValidationが必要」を一言添える程度で良い

## 7. コンポーネント設計

本サンプルでは、Axon Framework の最小構成を理解するために、必要最小限のコンポーネントのみを定義する。

### 7.1 主要コンポーネント一覧

| 区分 | クラス名 | 役割 |
|---|---|---|
| Command | `CreateOrderCommand` | 「注文を作成したい」という意図を表す |
| Event | `OrderCreatedEvent` | 「注文が作成された」という事実を表す |
| Aggregate | `OrderAggregate` | Order の整合性境界。Command を受け取り Event を発行し、Event から状態を復元する |
| Controller | `OrderCommandController` | REST API を受け取り、Command を送信する |

---

## 8. Axon における処理フロー

### 8.1 処理フロー概要

1. クライアントが `POST /orders` を呼び出す
2. Controller が `CreateOrderCommand` を生成する（UUID を付与）
3. `CommandGateway.sendAndWait()` により Command を送信する
4. Axon Framework が `OrderAggregate` の `@CommandHandler` を呼び出す
5. `AggregateLifecycle.apply(OrderCreatedEvent)` が実行される
6. `@EventSourcingHandler` が呼び出され、Aggregate の状態が更新される
7. Controller がレスポンスとして orderId を返却する

---

## 9. クラス設計

### 9.1 CreateOrderCommand

**責務**

- 注文作成の要求（意図）を表現する
- 必要最小限の情報のみを保持する

**主なフィールド**

- `orderId: String`
- `productName: String`

**設計上のポイント**

- Command は「やりたいこと」を表し、結果や状態を持たない
- バリデーションは最小限とし、本サンプルでは詳細な検証は行わない

---

### 9.2 OrderCreatedEvent

**責務**

- 実際に発生した事実を表現する
- Event Sourcing における「真実」として扱われる

**主なフィールド**

- `orderId: String`
- `productName: String`

**設計上のポイント**

- Event は過去の事実であり、変更されない（Immutable）
- Event 名は過去形で表現する

---

### 9.3 OrderAggregate

**責務**

- Order の整合性を保つ境界（Aggregate Root）
- Command を受け取り、適切な Event を発行する
- Event から自身の状態を復元する

**主なフィールド**

- `@AggregateIdentifier orderId: String`
- `productName: String`

**構成上のポイント**

- Axon による復元のため、引数なしコンストラクタを定義する
- 注文作成の `@CommandHandler` はコンストラクタに定義する
- 状態変更は直接行わず、`EventSourcingHandler` で反映する

**読者がつまずきやすいポイント（本文で解説する）**

- なぜ `new OrderAggregate()` を呼ばないのか
- なぜ Repository を自分で実装しないのか
- なぜ `apply()` を呼ぶだけで状態が変わるのか

---

### 9.4 OrderCommandController

**責務**

- HTTP リクエストを受け取り Command に変換する
- `CommandGateway` を通じて Command を送信する

**設計方針**

- Controller にビジネスロジックを持たせない
- Domain 層（Aggregate）への直接依存を避ける
- REST API と Command の責務を分離する

---

## 10. パッケージ / ディレクトリ構成
com.example.axonlevelone.order/
├─ command/
│ └─ CreateOrderCommand.java
├─ event/
│ └─ OrderCreatedEvent.java
├─ aggregate/
│ └─ OrderAggregate.java
└─ controller/
└─ OrderCommandController.java


> 本構成は、Axon の概念（Command / Event / Aggregate）を  
> ディレクトリ構造として視覚化することを目的としている。

---

## 11. 動作確認（受け入れ条件）

以下を満たしていれば、本章の目的は達成されている。

- アプリケーションが正常に起動する
- `POST /orders` により注文作成が成功する
- ログ等から以下が確認できる
  - `CreateOrderCommand` が処理された
  - `OrderCreatedEvent` が発行された
  - `@EventSourcingHandler` により状態が更新された

---

## 12. 次のステップ（Level TWO への導線）

本サンプルを拡張することで、次の学習につなげられる。

- 注文キャンセル（`CancelOrderCommand` / `OrderCancelledEvent`）の追加
- Query Model（Projection）を導入し CQRS を体験する
- Saga を使った複数 Aggregate にまたがる処理
- Axon Server や JDBC Event Store による永続化

> 本書では扱わないが、「次に学ぶ内容」として読者に示す。

---




