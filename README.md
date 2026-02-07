# Axon Level ONE - Sample Code

技術同人誌『Axon Level ONE』のサンプルコードリポジトリです。

## Chapter 4: 最小構成で動かす Axon

Axon Framework の **Command → Event → 状態更新（Event Sourcing）** の流れを、最小限のコードで体験するサンプルです。

### 技術スタック

| 項目 | バージョン |
|------|-----------|
| Java | 21（LTS） |
| Spring Boot | 3.5.10 |
| Axon Framework | 4.10.3 |
| ビルドツール | Gradle（Kotlin DSL） |
| Event Store | インメモリ（Axon Server 不要） |

### プロジェクト構成

```
axon-level-one/src/main/java/com/example/axonlevelone/
├── AxonLevelOneApplication.java              ... Spring Boot メインクラス
├── config/
│   └── AxonConfig.java                       ... InMemoryEventStorageEngine 設定
└── order/
    ├── command/
    │   └── CreateOrderCommand.java           ... 「注文を作成したい」という意図
    ├── event/
    │   └── OrderCreatedEvent.java            ... 「注文が作成された」という事実
    ├── aggregate/
    │   └── OrderAggregate.java               ... 整合性境界（Command 処理 + Event 発行）
    ├── query/
    │   └── GetOrdersQuery.java              ... 「注文一覧を取得したい」というクエリ
    ├── projection/
    │   └── OrderProjection.java             ... Query Model（Event → 読み取りモデル構築）
    └── controller/
        ├── OrderCommandController.java       ... Command 用 REST API（POST）
        ├── OrderQueryController.java         ... Query 用 REST API（GET）
        └── dto/
            ├── CreateOrderRequest.java       ... リクエストボディ（record）
            ├── CreateOrderResponse.java      ... レスポンスボディ（record）
            ├── OrderSummary.java             ... 注文一覧レスポンス（record）
            └── OrderStatus.java              ... ステータス（enum）
```

### 処理フロー

#### Command 側（書き込み）
```
POST /orders  →  Controller  →  CommandGateway  →  OrderAggregate
                                                      ├─ @CommandHandler（Command 処理）
                                                      ├─ apply(OrderCreatedEvent)
                                                      └─ @EventSourcingHandler（状態更新）
```

#### Query 側（読み取り）
```
GET /orders  →  Controller  →  QueryGateway  →  OrderProjection
                                                   ├─ @EventHandler（Event 購読 → リスト蓄積）
                                                   └─ @QueryHandler（クエリ応答）
```

## クイックスタート

### 前提条件

- Java 21 以上がインストールされていること

```bash
java -version
```

### ビルド

```bash
cd axon-level-one
./gradlew build
```

### アプリケーション起動

```bash
cd axon-level-one
./gradlew bootRun
```

起動に成功すると以下のログが表示されます。

```
Started AxonLevelOneApplication in X.XXX seconds
```

### 動作確認

別のターミナルを開き、以下の curl コマンドを実行します。

#### 注文作成

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"orderNumber": "ORD-001", "productName": "Coffee"}'
```

#### 期待するレスポンス

```json
{
  "orderNumber": "ORD-001",
  "status": "CREATED"
}
```

> HTTP ステータスコード: **201 Created**

#### 注文一覧取得

```bash
curl http://localhost:8080/orders
```

#### 期待するレスポンス（注文一覧）

```json
[
  {
    "orderNumber": "ORD-001",
    "productName": "Coffee"
  }
]
```

#### 期待するログ出力

アプリケーションのコンソールに以下の5行が順番に出力されます。
Command → Event → 状態更新の流れを番号付きで追うことができます。

```
INFO  c.e.a.o.c.OrderCommandController : [1] Received POST /orders: orderNumber=ORD-001, productName=Coffee
INFO  c.e.a.o.c.OrderCommandController : [2] Sending CreateOrderCommand: orderNumber=ORD-001
INFO  c.e.a.order.aggregate.OrderAggregate : [3] Command received, publishing OrderCreatedEvent: orderNumber=ORD-001, productName=Coffee
INFO  c.e.a.order.aggregate.OrderAggregate : [4] Event applied, aggregate state updated: orderNumber=ORD-001, productName=Coffee
INFO  c.e.a.o.c.OrderCommandController : [5] Order created successfully: orderNumber=ORD-001, status=CREATED
```

### 重複検知の動作確認

同じ `orderNumber` で再度リクエストを送ると、重複として検知されます。

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"orderNumber": "ORD-001", "productName": "Coffee"}'
```

#### 期待するレスポンス

```json
{
  "orderNumber": "ORD-001",
  "status": "ALREADY_PROCESSED"
}
```

> HTTP ステータスコード: **409 Conflict**

Axon Framework は同じ `orderNumber`（Aggregate 識別子）を持つ Aggregate が既に存在する場合、2つ目の作成を自動的に拒否します。これにより、アプリケーション側で重複チェックのロジックを書かなくても、冪等な注文作成が実現できます。

## ドキュメント

詳細な要件・仕様・設計は `aidlc-docs/inception/` を参照してください。

| ドキュメント | 内容 |
|-------------|------|
| [requirements.md](aidlc-docs/inception/requirements.md) | 要件定義書 |
| [specification.md](aidlc-docs/inception/specification.md) | 仕様書（API 仕様・ドメインモデル） |
| [design.md](aidlc-docs/inception/design.md) | 設計書（クラス設計・処理フロー） |

## License

[MIT](LICENSE)
