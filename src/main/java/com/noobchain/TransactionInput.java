package com.noobchain;

// Represents the input of a transaction — a reference to a previous unspent output (UTXO).
public class TransactionInput {

    public String transactionOutputId; // ID of the UTXO being spent
    public TransactionOutput UTXO;     // The actual unspent output

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}