# 🦖 Dino Ventures – Internal Wallet Service
**Author:** Pushpak Jain  

---

## 📖 Overview

A high-availability, closed-loop wallet service designed to manage virtual currencies (Gold Coins, Reward Points, etc.).

### Guarantees

- Strict ACID compliance  
- No lost transactions under high concurrency  
- Idempotent transaction execution  
- Fully auditable double-entry ledger  

Designed as if handling real money.

---

## 🚀 Running the Application

### Option 1: Docker (Recommended)

**Prerequisites**
- Docker
- Docker Compose

Run:

```bash
docker-compose up --build
```

This will:

- Start PostgreSQL 16
- Run Flyway migrations
- Seed initial wallets
- Start the Spring Boot app

App will be available at:

```
http://localhost:8080
```

---

## 🗄️ Database & Seed Script

The database is initialized using **Flyway migrations**.

On startup:

- Schema is created
- `wallets` table is created
- `transactions` table is created
- Seed data inserts:
  - System Treasury wallet
  - Player One wallet
  - Player Two wallet

This ensures the system is immediately testable.

---

## 🌟 Interactive UI

Open:

```
http://localhost:8080/
```

You can:

- Execute transactions
- View balances
- Test idempotency behavior

---

## 🛠️ Tech Stack & Why

**Java 21 (LTS)**  
- Strong concurrency support  
- Production-grade stability  

**Spring Boot 3.2**  
- Clean REST architecture  
- Declarative transaction management  

**PostgreSQL 16**  
- Strict ACID guarantees  
- Row-level locking (`SELECT FOR UPDATE`)  
- Ideal for financial systems  

**Docker**  
- One-command reproducibility  
- Consistent evaluation environment  

---

## 🧠 Concurrency Strategy

### 1️⃣ Double-Entry Ledger

Every transaction inserts an immutable record.  
Money is always moved (never created/destroyed).  
Provides full auditability.

---

### 2️⃣ Pessimistic Locking

```sql
SELECT * FROM wallets WHERE id = ? FOR UPDATE;
```

- Locks source and destination rows
- Prevents lost updates
- Ensures balance consistency

---

### 3️⃣ Deadlock Prevention

Locks are acquired in deterministic UUID order:

- Smaller UUID first
- Larger UUID second

This eliminates circular wait conditions.

---

### 4️⃣ Idempotency

API requires:

```
X-Idempotency-Key
```

If the same key is reused:

- Transaction is not executed again
- Cached response is returned

Prevents double-charging.

---

## 📡 API

### Execute Transaction

```
POST /api/v1/wallets/transactions
```

Headers:

```
Content-Type: application/json
X-Idempotency-Key: <unique-string>
```

Body:

```json
{
  "sourceWalletId": "44444444-4444-4444-4444-444444444444",
  "destinationWalletId": "55555555-5555-5555-5555-555555555555",
  "amount": 50.00,
  "type": "SPEND"
}
```

---

### Check Balance

```
GET /api/v1/wallets/{walletId}/balance
```

Response:

```json
{
  "walletId": "44444444-4444-4444-4444-444444444444",
  "currency": "GOLD_COINS",
  "balance": 450.00
}
```

---

## ✅ Design Guarantees

- Atomic transactions  
- Deadlock-free execution  
- Strong consistency  
- Fully auditable ledger  
- Idempotent payment execution  

---
