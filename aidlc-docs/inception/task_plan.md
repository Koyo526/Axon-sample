# Axon Level ONE - Chapter 4 最小構成サンプル（Order） タスク計画

## 要件レビュー所見

plan.md の要件・仕様・設計をレビューした結果、以下の所見を共有します。

### 良い点
- 学習ゴールが明確で、スコープが適切に絞られている
- Command → Event → 状態更新の流れを4ファイルで表現する方針は入門書に最適
- 「やらないこと」が明確に定義されており、スコープクリープを防げる

### 確認・検討が必要な点

以下の質問への回答をお願いします。

---

## 質問事項

### Q1: Axon Framework のバージョン
[Question] plan.md では「Axon Framework: 4.x 系」とありますが、現時点での最新は **Axon Framework 4.10.x** です。特定のバージョンを指定しますか？それとも最新の 4.x を使用しますか？
[Answer] 4.10.x を明示指定します（例：4.10.3 など執筆時点の最新パッチ）。
理由：入門書は「同じ環境で再現できる」ことが最重要なので、“4.x最新”のような可変指定は避ける。

### Q2: Spring Boot のバージョン
[Question] Spring Boot 3.x 系とありますが、**3.2.x** または **3.3.x** など特定のバージョンの希望はありますか？（Java 17 以上の要件と整合します）
[Answer] Spring Boot は 3.3.x をパッチまで固定し、Java は 21 (LTS) を前提とします。

### Q3: ビルドツール
[Question] ビルドツールは **Gradle（Kotlin DSL）** と **Maven** のどちらを使用しますか？入門書の読者層を考慮すると Maven のほうが馴染みがあるかもしれませんが、いかがでしょうか？
[Answer]
Gradle（Kotlin DSL）を使用します。

### Q4: productName のバリデーション
[Question] plan.md のセクション 6.1 で「productName が空なら 400 にする、または簡略のため省略しても良い」とあります。最小構成の趣旨からすると **省略（バリデーションなし）** が適切と考えますが、どちらにしますか？
[Answer]
省略（バリデーションなし）とします。

### Q5: Event Store の方式
[Question] Out of Scope に「Event Store の永続化」が挙げられていますが、**Axon のインメモリ Event Store（デフォルト）** を使用する想定で合っていますか？（Axon Server も不要とする）
[Answer]
Axon のインメモリ Event Store（デフォルト）を使用します。

### Q6: ログ出力について
[Question] 動作確認の受け入れ条件で「ログ等から確認できる」とあります。Aggregate 内での `System.out.println` ではなく、**SLF4J Logger** を使ったログ出力にする想定でよろしいですか？
[Answer]
SLF4J Logger を使ったログ出力にします。

### Q7: テストコードのスコープ
[Question] plan.md にはテストコードへの言及がありません。最小構成の趣旨から **テストコードは作成しない**（動作確認は curl で行う）方針でよろしいですか？それとも最小限のテスト（Aggregate テスト等）を含めますか？
[Answer]
テストコードは作成しない方針でお願いします。動作確認は curl で行います。

### Q8: ドキュメント生成のスコープ
[Question] タスクでは「要件・仕様・設計ドキュメントを作成」とありますが、これは plan.md の内容をレビュー・補完したドキュメントを指しますか？それとも plan.md の内容をもとにコード実装も行いますか？
[Answer] このタスク計画では、まず plan.md を正式ドキュメント（aidlc-docs/inception/requirements.md）に落とすところまでをスコープにします。

---

## 回答サマリ

| # | 項目 | 決定事項 |
|---|------|----------|
| Q1 | Axon Framework | 4.10.x（パッチまで固定） |
| Q2 | Spring Boot / Java | Spring Boot 3.3.x / Java 21 (LTS) |
| Q3 | ビルドツール | Gradle（Kotlin DSL） |
| Q4 | バリデーション | 省略（なし） |
| Q5 | Event Store | インメモリ（デフォルト、Axon Server 不要） |
| Q6 | ログ出力 | SLF4J Logger |
| Q7 | テストコード | 作成しない（curl で動作確認） |
| Q8 | タスクスコープ | ドキュメント作成まで（コード実装は別タスク） |

---

## タスク計画（確定版）

Q8 の回答により、本タスクのスコープは **Phase 1: ドキュメント整備** のみとします。
Phase 2 以降（環境構築・コード実装・動作確認）は別タスクとして管理します。

### Phase 1: ドキュメント整備

- [x] **Step 1: 要件定義書の作成**
  - plan.md のセクション 1〜4 をベースに、Q1〜Q8 の回答を反映
  - 目的、スコープ、前提・環境、機能要件・非機能要件を正式に整理
  - 出力先: `aidlc-docs/inception/requirements.md`

- [x] **Step 2: 仕様書の作成**
  - plan.md のセクション 5〜6 をベースに補完
  - ドメインモデル仕様（Order エンティティの定義）
  - API 仕様（エンドポイント、リクエスト/レスポンス、ステータスコード）
  - 動作確認手順（curl コマンド例）
  - 出力先: `aidlc-docs/inception/specification.md`

- [x] **Step 3: 設計書の作成**
  - plan.md のセクション 7〜10 をベースに補完
  - コンポーネント一覧と責務
  - クラス設計（フィールド、アノテーション、メソッドシグネチャ）
  - パッケージ構成図
  - Axon 処理フロー図（シーケンス図）
  - 依存ライブラリ一覧（`build.gradle.kts` に記載する依存関係）
  - 出力先: `aidlc-docs/inception/design.md`

### Phase 2: 環境構築

- [x] **Step 4: プロジェクトの初期セットアップ**
  - Spring Boot 3.3.x + Java 21 + Gradle（Kotlin DSL）のプロジェクト雛形を作成
  - `build.gradle.kts` に Axon 4.10.3 依存関係を追加
  - `application.yml` で Axon Server 接続を無効化
  - `AxonLevelOneApplication.java` メインクラスを作成

### Phase 3: コード実装

- [ ] **Step 5: Command クラスの実装**
  - `CreateOrderCommand.java` を作成

- [ ] **Step 6: Event クラスの実装**
  - `OrderCreatedEvent.java` を作成

- [ ] **Step 7: Aggregate クラスの実装**
  - `OrderAggregate.java` を作成

- [ ] **Step 8: Controller クラスの実装**
  - `OrderCommandController.java` を作成

### Phase 4: 動作確認

- [ ] **Step 9: ビルド・起動確認**
  - アプリケーションが正常にビルド・起動することを確認

- [ ] **Step 10: API 動作確認**
  - `POST /orders` の curl による動作確認
  - ログ出力から Command → Event → 状態更新の流れを確認
