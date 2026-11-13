# MarketGuard

**High-Throughput Order-Book Event Validation and Recovery Engine**  
Java 21 · Maven · JUnit 5 · Event-Driven Architecture

---

## Overview

MarketGuard is a Java 21 backend system designed to ingest, sequence, and validate stateful market order-book event streams. It enforces strict correctness invariants on `ADD`, `MODIFY`, and `CANCEL` events — the same event lifecycle that powers real-world exchange order books — and provides snapshot-based recovery to restore consistent state after missing or out-of-order events.

The project explores the systems engineering problems at the core of trading infrastructure: how do you guarantee that every event is processed exactly once, in the correct order, and that the resulting state is always consistent?

---

## Problem Statement

Order-book systems receive a continuous stream of events from exchanges. Each event carries a **sequence number** and must be applied in strict order. In practice, streams can contain:

- **Out-of-order events** — event 105 arrives before event 103
- **Duplicate events** — the same sequence number delivered twice
- **Gap events** — sequence numbers are missing entirely
- **Stale events** — a `MODIFY` or `CANCEL` referencing an order that does not exist

A system that processes these naively will produce a corrupted order book. MarketGuard detects and rejects all of the above, and uses a snapshot-and-replay mechanism to recover cleanly when gaps occur.

---

## Core Design

### `MarketEvent` — Immutable Event Carrier

Each event in the system is represented as an **immutable value object**. All fields are set at construction time and exposed only through read-only accessors. This ensures that once an event enters the processing pipeline, its data cannot be modified — eliminating an entire class of concurrency bugs.

```java
MarketEvent event = new MarketEvent(
    101,           // sequenceNumber
    501,           // orderId
    EventType.ADD, // event type
    3500,          // price (in minor units, e.g. pence)
    100            // quantity
);
```

**Fields:**

| Field            | Type        | Description                                      |
|------------------|-------------|--------------------------------------------------|
| `sequenceNumber` | `long`      | Monotonically increasing; used for ordering and gap detection |
| `orderId`        | `long`      | Identifies the order this event applies to       |
| `eventType`      | `EventType` | One of `ADD`, `MODIFY`, or `CANCEL`              |
| `price`          | `long`      | Order price in minor currency units              |
| `quantity`       | `int`       | Number of units in the order                     |

### `EventType` — Order Lifecycle

```
ADD     → Place a new order on the book
MODIFY  → Update the price or quantity of an existing order
CANCEL  → Remove an order from the book
```

Each transition has strict validity rules:
- `MODIFY` and `CANCEL` are only valid if the `orderId` exists in the current book state.
- An `ADD` for an already-existing `orderId` is a duplicate and must be rejected.

---

## Project Structure

```
MarketGuard/
├── src/
│   └── main/
│       └── java/
│           └── com/marketguard/
│               ├── EventType.java       # Enum: ADD, MODIFY, CANCEL
│               ├── MarketEvent.java     # Immutable event value object
│               └── Main.java           # Entry point / smoke test
├── pom.xml                              # Maven build (Java 21)
└── README.md
```

---

## Build and Run

**Requirements:** Java 21+, Maven 3.8+

```bash
# Clone
git clone https://github.com/ishcares/MarketGuard.git
cd MarketGuard

# Build
mvn compile

# Run the smoke test entry point
mvn exec:java
```

**Expected output:**
```
MarketEvent created successfully:
MarketEvent{sequenceNumber=101, orderId=501, eventType=ADD, price=3500, quantity=100}
```

---

## Roadmap

The current implementation provides the validated immutable data model. The following components are under active development:

- **`OrderBookValidator`** — Sequence number gap detection, duplicate rejection, and state-transition rule enforcement (`ADD` / `MODIFY` / `CANCEL` validity checks).
- **`OrderBook`** — In-memory order book state: maps `orderId → MarketEvent`, updated on each valid event.
- **`SnapshotManager`** — Periodic book state snapshots serialised to disk, enabling replay-based recovery when a sequence gap is detected.
- **`EventReplayEngine`** — Replays buffered events from a known-good snapshot to rebuild consistent state after a gap.
- **JUnit 5 test suite** — Covering boundary conditions: corrupt events, out-of-order streams, duplicate sequence numbers, stale `MODIFY`/`CANCEL` targeting non-existent orders.

---

## Design Principles

**Immutability first.** `MarketEvent` is a final, fully immutable value object. No setters, no mutation post-construction. This makes events safe to pass between threads, cache, snapshot, and replay without defensive copying.

**Explicit over implicit.** Each validation rule (gap detection, state transition validity, duplicate checking) is a named, testable unit. No silent swallowing of invalid events.

**Recovery by design.** The system is built from the start to handle the reality that event streams are unreliable. Snapshot-and-replay is not a bolt-on; it is part of the core processing model.

**Measurement over assumption.** Performance characteristics (throughput, latency under concurrent load) will be benchmarked and documented. Claims in this repository are grounded in test output, not intuition.

---

## Technologies

| Component        | Technology            |
|------------------|-----------------------|
| Language         | Java 21               |
| Build            | Maven 3.8+            |
| Testing          | JUnit 5               |
| Architecture     | Event-Driven, Immutable Value Objects, OOP |

---

## Author

**Ishita Chaurasia**  
[GitHub](https://github.com/ishcares) · [LinkedIn](https://linkedin.com/in/ishitachaurasia) · [Portfolio](https://ishcares.github.io/portfolio/)

