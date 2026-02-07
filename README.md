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
    └── controller/
        ├── OrderCommandController.java       ... REST API エンドポイント
        └── dto/
            ├── CreateOrderRequest.java       ... リクエストボディ（record）
            ├── CreateOrderResponse.java      ... レスポンスボディ（record）
            └── OrderStatus.java              ... ステータス（enum）
```

### 処理フロー

```
POST /orders  →  Controller  →  CommandGateway  →  OrderAggregate
                                                      ├─ @CommandHandler（Command 処理）
                                                      ├─ apply(OrderCreatedEvent)
                                                      └─ @EventSourcingHandler（状態更新）
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
  -d '{"productName": "Coffee"}'
```

#### 期待するレスポンス

```json
{
  "orderId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "status": "CREATED"
}
```

> `orderId` は実行ごとに異なる UUID が返ります。

#### 期待するログ出力

アプリケーションのコンソールに以下の5行が順番に出力されます。
Command → Event → 状態更新の流れを番号付きで追うことができます。

```
INFO  c.e.a.o.c.OrderCommandController : [1] Received POST /orders: productName=Coffee
INFO  c.e.a.o.c.OrderCommandController : [2] Sending CreateOrderCommand: orderId=...
INFO  c.e.a.order.aggregate.OrderAggregate : [3] Command received, publishing OrderCreatedEvent: orderId=..., productName=Coffee
INFO  c.e.a.order.aggregate.OrderAggregate : [4] Event applied, aggregate state updated: orderId=..., productName=Coffee
INFO  c.e.a.o.c.OrderCommandController : [5] Order created successfully: orderId=..., status=CREATED
```

## ドキュメント

詳細な要件・仕様・設計は `aidlc-docs/inception/` を参照してください。

| ドキュメント | 内容 |
|-------------|------|
| [requirements.md](aidlc-docs/inception/requirements.md) | 要件定義書 |
| [specification.md](aidlc-docs/inception/specification.md) | 仕様書（API 仕様・ドメインモデル） |
| [design.md](aidlc-docs/inception/design.md) | 設計書（クラス設計・処理フロー） |

## License

[MIT](LICENSE)
