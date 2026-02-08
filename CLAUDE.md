# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## プロジェクト概要

技術同人誌『Axon Level ONE』Chapter 4 向けの最小構成サンプルコード。Axon Framework + Spring Boot による **Command → Event → 状態更新（Event Sourcing）** の流れを体験的に学ぶためのプロジェクト。

## 技術スタック

- Java 21（OpenJDK Temurin-21.0.5+11）
- Spring Boot 3.x
- Axon Framework 4.x（Spring Boot Starter）
- インメモリ Event Store（Axon Server 不要）

## アーキテクチャ

CQRS + Event Sourcing パターンに基づく最小構成。処理フローは以下の通り:

```
REST API → Controller → CommandGateway → Aggregate(@CommandHandler)
  → AggregateLifecycle.apply() → @EventSourcingHandler → 状態更新
```

### パッケージ構成

```
com.example.axonlevelone.order/
├── command/        # Command クラス（意図を表現）
├── event/          # Event クラス（発生した事実を表現、Immutable）
├── aggregate/      # Aggregate（整合性境界、Command受理 → Event発行）
├── query/          # Query クラス（問い合わせを表現）
├── projection/     # Projection（Event → 読み取りモデル構築）
└── controller/     # REST Controller（HTTP → Command/Query 変換）
```

### 設計原則

- Controller にビジネスロジックを持たせない（Command への変換のみ）
- 状態変更は `@EventSourcingHandler` 内でのみ行う（`@CommandHandler` で直接変更しない）
- Command は「やりたいこと」、Event は「起きた事実（過去形）」として命名する
- Aggregate は引数なしコンストラクタが必須（Axon による復元に使用）

## ドキュメント構成

- `aidlc-docs/plan.md` — 要件・仕様・設計の元ドキュメント
- `aidlc-docs/inception/task_plan.md` — タスク計画（未回答の質問事項あり）

## スコープ

入門書のサンプルであるため、以下は意図的にスコープ外:
- DB 永続化、Axon Server、認証認可、例外設計、Saga、Query Model
- 本番向けの設計（分散・運用・監視・性能）

## GitHub CLI（gh コマンド）

- リモートリポジトリ: `Koyo526/Axon-sample`
- `gh` コマンドは認証済みで使用可能
- PR 作成・Issue 管理・リリース操作などに `gh` を使用する
- `git push` 後の PR 作成は `gh pr create` で行う
- ブランチ戦略: `main` をデフォルトブランチ、`develop` で開発を行う

## 言語

ドキュメントおよびコミュニケーションはすべて日本語で行う。