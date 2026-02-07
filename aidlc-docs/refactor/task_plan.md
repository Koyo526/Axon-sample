# タスク計画: GET /orders エンドポイントの追加

## 概要

Event Sourcing で保存された `OrderCreatedEvent` から状態を復元し、注文情報（productName）のリストを返す `GET /orders` エンドポイントを追加する。

## アーキテクチャ方針

CQRS パターンに従い、**Query 側（読み取り側）** のコンポーネントを新規追加する。

```
GET /orders → Controller → QueryGateway → @QueryHandler(Projection)
                                                ↑
                              @EventHandler が OrderCreatedEvent を購読して
                              インメモリの注文リストを更新
```

## パッケージ構成（追加分）

```
com.example.axonlevelone.order/
├── query/              # ★ 新規: Query クラス
│   └── GetOrdersQuery.java
├── projection/         # ★ 新規: Projection（Query Model）
│   └── OrderProjection.java
└── controller/
    ├── OrderCommandController.java  # 既存（変更なし）
    ├── OrderQueryController.java    # ★ 新規: Query 用 Controller
    └── dto/
        └── OrderSummary.java        # ★ 新規: レスポンス DTO
```

## ステップ

### Step 1: Query クラスの作成
- [x] `GetOrdersQuery.java` を `order/query/` パッケージに作成
- Axon の QueryGateway に送信するためのクエリオブジェクト
- 全件取得のためフィールドは不要（空クラス）

### Step 2: レスポンス DTO の作成
- [x] `OrderSummary.java` を `order/controller/dto/` に作成
- productName を保持するレスポンス用 DTO

[Question] レスポンスの形式について: `productName` のみのリスト（`List<String>`）を返すか、`orderId` と `productName` を含むオブジェクトのリスト（`List<OrderSummary>`）を返すか、どちらが望ましいですか？

[Answer]
`orderId` と `productName` を含むオブジェクトのリスト（`List<OrderSummary>`）を返す形にしましょう。
### Step 3: Projection（Query Model）の作成
- [x] `OrderProjection.java` を `order/projection/` パッケージに作成
- `@EventHandler` で `OrderCreatedEvent` を購読し、インメモリのリストに注文情報を蓄積
- `@QueryHandler` で `GetOrdersQuery` に対して注文リストを返却

### Step 4: Query 用 Controller の作成
- [x] `OrderQueryController.java` を `order/controller/` に作成
- `GET /orders` エンドポイントを定義
- `QueryGateway` を使って `GetOrdersQuery` を送信し、結果を返却

### Step 5: 動作確認
- [x] アプリケーションをビルド・起動して動作確認
  1. `POST /orders` で注文を数件作成
  2. `GET /orders` で注文リストが返ることを確認

### Step 6: README.md の更新
- [x] `README.md` にQuery側の情報を追加
  - プロジェクト構成に新規ファイルを追記
  - 処理フローに GET /orders の流れを追記
  - 動作確認セクションに GET /orders の curl 例を追記

## 備考

- Command 側の既存コードには一切変更を加えない（CQRS の分離原則）
- インメモリ Event Store を使用しているため、アプリ再起動で注文データはリセットされる
- Projection もインメモリ（`List` or `Map`）で保持する（DB 永続化はスコープ外）
