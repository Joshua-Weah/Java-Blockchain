# Java Blockchain Project

A blockchain implementation in Java built from scratch, demonstrating core blockchain concepts including cryptographic hashing, proof-of-work mining, digital signatures, and a UTXO-based transaction model.

---

## What it does

- Creates a chain of blocks linked by SHA-256 hashes
- Mines blocks using proof-of-work (adjustable difficulty)
- Detects tampering — any change to a block invalidates the chain
- Generates wallets with ECDSA public/private key pairs
- Signs and verifies transactions cryptographically
- Tracks coin ownership using an Unspent Transaction Output (UTXO) model
- Sends coins between wallets with automatic change handling

---

## How to run

**Requirements:** Java 17+, Maven 3.x

```bash
# Clone the repo
git clone https://github.com/Joshua-Weah/Java-Blockchain.git
cd Java-Blockchain

# Run
mvn exec:java
```

**Expected output:**

WalletA balance: 0.0
WalletB balance: 0.0
Creating and mining genesis block...
Transaction added to block.
Block mined! Hash: 000...
WalletA balance: 100.0
WalletA sending 40 coins to WalletB...
WalletA balance: 60.0
WalletB balance: 40.0
...
Blockchain valid: true

---

## Project structure

```
src/main/java/com/noobchain/
├── NoobChain.java          # Entry point — creates wallets, blocks, and transactions
├── Block.java              # Block structure with hashing, mining, and transactions
├── Transaction.java        # Sends coins between wallets, verifies signatures
├── TransactionInput.java   # References an unspent output (UTXO) to spend
├── TransactionOutput.java  # Coins sent to a recipient as a result of a transaction
├── Wallet.java             # ECDSA key pair generation and balance tracking
└── StringUtil.java         # SHA-256 hashing, ECDSA signing/verification, Merkle root
```

---

## Key concepts

**SHA-256 Hashing** — Each block's hash is derived from its contents. Changing any data produces a completely different hash, breaking the chain.

**Proof of Work** — Blocks are only valid if their hash starts with a set number of leading zeros. Miners increment a `nonce` until this condition is met.

**ECDSA Signatures** — Transactions are signed with the sender's private key and verified with their public key, ensuring only the owner can spend their coins.

**UTXO Model** — Coin ownership is tracked as Unspent Transaction Outputs. Spending a UTXO removes it from the pool and creates new outputs for the recipient and any change.

**Merkle Root** — All transactions in a block are summarised into a single hash, included in the block's hash calculation.

---

## Built with

- Java 17
- Maven
- [Bouncy Castle](https://www.bouncycastle.org/) — cryptography library for ECDSA
- [Gson](https://github.com/google/gson) — JSON serialisation
