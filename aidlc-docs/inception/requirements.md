# Axon Level ONE - Chapter 4 最小構成サンプル（Order） 要件定義書

> 本ドキュメントは、技術同人誌『Axon Level ONE』Chapter 4「最小構成で動かす Axon」で扱うサンプルコードの正式な要件定義書である。
> plan.md の内容をベースに、レビュー時の Q&A（Q1〜Q8）の回答を反映し確定した。

---

## 1. 目的

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

## 2. スコープ

### 2.1 やること（In Scope）

| 項目 | 内容 |
|------|------|
| ドメイン | Order（注文） |
| ユースケース | **注文を作成する**（1つのみ） |
| Axon 構成要素 | Command（CreateOrderCommand）、Event（OrderCreatedEvent）、Aggregate（OrderAggregate） |
| API | REST API で Command を発行する（Controller） |
| 動作確認 | curl によるリクエスト送信 + SLF4J ログ出力で確認 |

### 2.2 やらないこと（Out of Scope）

以下は「入門の最小構成」から明示的に除外する。

- Cancel / Update / Payment などの追加ユースケース
- Saga（Chapter 3 で概念のみ扱う想定）
- Query Model の永続化（DB）
- Event Store の永続化（Axon Server / JDBC / Mongo）
- 認証認可、例外設計、リトライ、冪等性などの実運用設計
- Distributed / Microservices / Messaging の詳細
- バリデーション（productName の空チェック等）
- テストコード（動作確認は curl で行う）

---

## 3. 前提・環境

### 3.1 読者の前提知識

- Java / Spring Boot の基礎知識がある
- REST API の呼び出し（curl など）ができる

### 3.2 開発環境（確定）

| 項目 | バージョン | 備考 |
|------|-----------|------|
| Java | **21**（LTS） | - |
| Spring Boot | **3.5.x**（パッチまで固定） | 執筆時点: 3.5.10 |
| Axon Framework | **4.10.x**（パッチまで固定） | 執筆時点: 4.10.3 |
| ビルドツール | **Gradle（Kotlin DSL）** | `build.gradle.kts` |
| Event Store | **インメモリ（Axon デフォルト）** | Axon Server 不要 |

> 注: 最終的なパッチバージョンは実装フェーズで動作確認の上、確定する。
> 上記は調査時点（2026年2月）の情報に基づく。

### 3.3 バージョン固定方針

入門書は「同じ環境で再現できる」ことが最重要であるため、以下の方針とする。

- Spring Boot / Axon Framework ともに **パッチバージョンまで固定**する
- `build.gradle.kts` に具体的なバージョン番号を明記する
- 章内で「使用した依存関係のスニペット」を提示する

---

## 4. 要件

### 4.1 機能要件

| ID | 要件 | 優先度 |
|----|------|--------|
| FR-01 | `POST /orders` により注文作成を行える | 必須 |
| FR-02 | 注文作成により **OrderCreatedEvent** が発行される | 必須 |
| FR-03 | Aggregate の状態が Event により設定される（Event Sourcing） | 必須 |
| FR-04 | ログ出力から Command → Event → 状態更新の流れが確認できる | 必須 |

### 4.2 非機能要件

| ID | 要件 | 優先度 |
|----|------|--------|
| NFR-01 | 読者がローカルで 5〜10 分程度で動かせる | 必須 |
| NFR-02 | コード量が少なく、追いやすい（**4ファイル**を目標） | 必須 |
| NFR-03 | 外部サービス依存を避ける（Axon Server 不要） | 必須 |
| NFR-04 | SLF4J Logger を使用したログ出力 | 必須 |

### 4.3 対象ファイル一覧（実装フェーズ）

本サンプルで作成するソースファイルは以下の 4 ファイルである。

| # | ファイル | 役割 |
|---|---------|------|
| 1 | `CreateOrderCommand.java` | Command：注文作成の意図 |
| 2 | `OrderCreatedEvent.java` | Event：注文作成の事実 |
| 3 | `OrderAggregate.java` | Aggregate：整合性境界 |
| 4 | `OrderCommandController.java` | Controller：REST API |

---

## 5. 受け入れ条件

以下をすべて満たしていれば、本章の目的は達成されている。

- [ ] アプリケーションが正常に起動する
- [ ] `POST /orders` により注文作成が成功し、レスポンスに `orderId` と `status: "CREATED"` が含まれる
- [ ] ログから以下が確認できる
  - `CreateOrderCommand` が処理された
  - `OrderCreatedEvent` が発行された
  - `@EventSourcingHandler` により状態が更新された

---

## 6. 次のステップへの導線（Level TWO）

本サンプルを拡張することで、次の学習につなげられる。

- 注文キャンセル（`CancelOrderCommand` / `OrderCancelledEvent`）の追加
- Query Model（Projection）を導入し CQRS を体験する
- Saga を使った複数 Aggregate にまたがる処理
- Axon Server や JDBC Event Store による永続化

> 本書では扱わないが、「次に学ぶ内容」として読者に示す。

---

## 変更履歴

| 日付 | 内容 |
|------|------|
| 2026-02-07 | 初版作成（plan.md + Q1〜Q8 回答を反映） |
