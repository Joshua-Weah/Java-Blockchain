package com.noobchain;

import java.util.ArrayList;
import java.util.Date;

// Represents a single block in the blockchain.
public class Block {

    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    public long timeStamp;
    public int nonce;

    public Block(String data, String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return StringUtil.applySha256(
            previousHash + Long.toString(timeStamp) +
            Integer.toString(nonce) + merkleRoot
        );
    }

    public void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block mined! Hash: " + hash);
    }

    /**
     * Adds a transaction to the block after validating it.
     * Genesis block transactions are exempt from validation.
     */
    public boolean addTransaction(Transaction transaction) {
        if (transaction == null) return false;

        // Process all non-genesis transactions
        if (!previousHash.equals("0")) {
            if (!transaction.processTransaction()) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }

        transactions.add(transaction);
        System.out.println("Transaction added to block.");
        return true;
    }
}