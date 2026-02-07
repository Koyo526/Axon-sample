# Axon Level ONE - Chapter 4 最小構成サンプル（Order） 設計書

> 本ドキュメントは、要件定義書（requirements.md）および仕様書（specification.md）に基づき、
> サンプルコードのクラス設計、パッケージ構成、処理フロー、依存関係を定義する。

---

## 1. コンポーネント一覧

| 区分 | クラス名 | 役割 | Axon アノテーション |
|------|---------|------|-------------------|
| Command | `CreateOrderCommand` | 注文作成の意図を表す | `@TargetAggregateIdentifier` |
| Event | `OrderCreatedEvent` | 注文作成の事実を表す | なし |
| Aggregate | `OrderAggregate` | Order の整合性境界 | `@Aggregate`, `@AggregateIdentifier`, `@CommandHandler`, `@EventSourcingHandler` |
| Controller | `OrderCommandController` | REST API → Command 変換 | なし（Spring MVC のアノテーション） |

---

## 2. パッケージ構成

```
src/main/java/
└── com/example/axonlevelone/
    ├── AxonLevelOneApplication.java          ... Spring Boot メインクラス
    └── order/
        ├── command/
        │   └── CreateOrderCommand.java       ... Command メッセージ
        ├── event/
        │   └── OrderCreatedEvent.java        ... Event メッセージ
        ├── aggregate/
        │   └── OrderAggregate.java           ... Aggregate Root
        └── controller/
            └── OrderCommandController.java   ... REST Controller

src/main/resources/
└── application.yml                           ... 設定ファイル
```

> パッケージ構成は Axon の概念（Command / Event / Aggregate）を
> ディレクトリ構造として視覚化することを目的としている。

---

## 3. クラス設計

### 3.1 CreateOrderCommand

```java
package com.example.axonlevelone.order.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CreateOrderCommand {

    @TargetAggregateIdentifier
    private final String orderId;
    private final String productName;

    // コンストラクタ（全フィールド）
    // getter（setter は不要 — Immutable）
}
```

**設計ポイント**
- `@TargetAggregateIdentifier` により、Axon が Command のルーティング先 Aggregate を特定する
- Command は Immutable とし、`final` フィールド + getter のみとする
- デフォルトコンストラクタは不要

---

### 3.2 OrderCreatedEvent

```java
package com.example.axonlevelone.order.event;

public class OrderCreatedEvent {

    private final String orderId;
    private final String productName;

    // コンストラクタ（全フィールド）
    // getter（setter は不要 — Immutable）
}
```

**設計ポイント**
- Event は「過去に起きた事実」であり、Immutable である
- Axon 固有のアノテーションは不要
- Event 名は過去形（`OrderCreated`）で表現する

---

### 3.3 OrderAggregate

```java
package com.example.axonlevelone.order.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aggregate
public class OrderAggregate {

    private static final Logger log = LoggerFactory.getLogger(OrderAggregate.class);

    @AggregateIdentifier
    private String orderId;
    private String productName;

    protected OrderAggregate() {
        // Axon による復元用（引数なしコンストラクタ）
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        log.info("Handling CreateOrderCommand: orderId={}, productName={}",
                 command.getOrderId(), command.getProductName());
        AggregateLifecycle.apply(new OrderCreatedEvent(
                command.getOrderId(), command.getProductName()));
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        log.info("Applying OrderCreatedEvent: orderId={}, productName={}",
                 event.getOrderId(), event.getProductName());
        this.orderId = event.getOrderId();
        this.productName = event.getProductName();
    }
}
```

**設計ポイント**

| 要素 | 説明 |
|------|------|
| `@Aggregate` | Axon にこのクラスが Aggregate Root であることを伝える |
| `@AggregateIdentifier` | Aggregate の一意識別子。Command ルーティングに使用される |
| `protected OrderAggregate()` | Axon が Event Sourcing で Aggregate を復元する際に使用する。開発者が直接呼ぶことはない |
| `@CommandHandler`（コンストラクタ） | 新規 Aggregate の生成を伴う Command を処理する。**Aggregate の生成 = コンストラクタ Command Handler** |
| `AggregateLifecycle.apply()` | Event を発行する。直後に同一 Aggregate 内の `@EventSourcingHandler` が呼ばれる |
| `@EventSourcingHandler` | Event から Aggregate の状態を復元する。**状態変更はここでのみ行う** |

**読者がつまずきやすいポイント（本文で解説する）**

1. **なぜ `new OrderAggregate()` を呼ばないのか？**
   → Axon Framework が Command Handler の呼び出しを管理するため
2. **なぜ Repository を自分で実装しないのか？**
   → `@Aggregate` + Spring Boot Starter により自動構成される
3. **なぜ `apply()` を呼ぶだけで状態が変わるのか？**
   → `apply()` が Event を発行し、同一トランザクション内で `@EventSourcingHandler` が呼ばれるため

---

### 3.4 OrderCommandController

```java
package com.example.axonlevelone.order.controller;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
public class OrderCommandController {

    private final CommandGateway commandGateway;

    public OrderCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping("/orders")
    public Map<String, String> createOrder(@RequestBody Map<String, String> request) {
        String orderId = UUID.randomUUID().toString();
        String productName = request.get("productName");

        commandGateway.sendAndWait(new CreateOrderCommand(orderId, productName));

        return Map.of(
            "orderId", orderId,
            "status", "CREATED"
        );
    }
}
```

**設計ポイント**

| 要素 | 説明 |
|------|------|
| `CommandGateway` | Axon が提供する Command 送信の窓口。Spring Boot Starter により自動注入される |
| `sendAndWait()` | Command を送信し、処理完了まで同期的に待つ。入門サンプルでは同期が分かりやすい |
| `Map<String, String>` | 最小構成のため、専用の DTO クラスは作成しない |
| UUID 生成 | Controller 層で `orderId` を生成し、Command に渡す |

> 注: 本番では専用の Request DTO / Response DTO を定義すべきだが、
> ファイル数を最小限に抑えるため `Map` を使用する。

---

## 4. 処理フロー

### 4.1 シーケンス図

```
Client          Controller              CommandGateway        OrderAggregate
  |                 |                        |                      |
  | POST /orders    |                        |                      |
  |---------------->|                        |                      |
  |                 | UUID生成               |                      |
  |                 | CreateOrderCommand作成  |                      |
  |                 |--- sendAndWait() ----->|                      |
  |                 |                        |--- @CommandHandler -->|
  |                 |                        |   (コンストラクタ)     |
  |                 |                        |                      |
  |                 |                        |   apply(OrderCreatedEvent)
  |                 |                        |                      |
  |                 |                        |   @EventSourcingHandler
  |                 |                        |   orderId = ...      |
  |                 |                        |   productName = ...  |
  |                 |                        |                      |
  |                 |                        |<-- 完了 -------------|
  |                 |<-- 完了 ---------------|                      |
  |  200 OK         |                        |                      |
  |  {orderId,      |                        |                      |
  |   status}       |                        |                      |
  |<----------------|                        |                      |
```

### 4.2 処理ステップ詳細

| # | 処理 | 担当 | 説明 |
|---|------|------|------|
| 1 | `POST /orders` 受信 | Controller | HTTP リクエストを受け取る |
| 2 | UUID 生成 | Controller | `UUID.randomUUID().toString()` で orderId を生成 |
| 3 | Command 生成 | Controller | `CreateOrderCommand(orderId, productName)` を作成 |
| 4 | Command 送信 | CommandGateway | `sendAndWait()` により Command Bus に送信 |
| 5 | Command 処理 | OrderAggregate | `@CommandHandler` コンストラクタが呼ばれる |
| 6 | Event 発行 | OrderAggregate | `AggregateLifecycle.apply(new OrderCreatedEvent(...))` |
| 7 | 状態更新 | OrderAggregate | `@EventSourcingHandler` で `orderId`, `productName` を設定 |
| 8 | レスポンス返却 | Controller | `{orderId, status: "CREATED"}` を返す |

---

## 5. 依存ライブラリ

### 5.1 build.gradle.kts（依存関係部分）

```kotlin
plugins {
    java
    id("org.springframework.boot") version "3.5.10"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.axonframework:axon-spring-boot-starter:4.10.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

### 5.2 依存関係の説明

| 依存 | 目的 |
|------|------|
| `spring-boot-starter-web` | REST API（`@RestController`, `@PostMapping` 等） |
| `axon-spring-boot-starter:4.10.3` | Axon Framework 本体 + Spring Boot 自動構成。CommandBus, EventBus, EventStore 等が自動設定される |
| `spring-boot-starter-test` | テスト用（本サンプルでは使用しないが、Spring Initializr のデフォルトとして残す） |

> `axon-spring-boot-starter` は推移的依存により以下を含む:
> - `axon-configuration` — Axon の自動構成
> - `axon-eventsourcing` — Event Sourcing サポート
> - `axon-spring` — Spring 統合
> - `axon-server-connector` — Axon Server 接続（設定で無効化する）

---

## 6. 設定ファイル

### 6.1 application.yml

```yaml
axon:
  axonserver:
    enabled: false
```

| 設定 | 値 | 説明 |
|------|-----|------|
| `axon.axonserver.enabled` | `false` | Axon Server への接続を無効化し、インメモリ Event Store を使用する |

> この1行の設定により、外部サービスなしで Axon Framework を動作させることができる。
> Event Store はインメモリとなり、アプリケーション再起動時にデータは消失する。

---

## 変更履歴

| 日付 | 内容 |
|------|------|
| 2026-02-07 | 初版作成（plan.md セクション 7〜10 + 依存関係・設定・フロー図を補完） |
