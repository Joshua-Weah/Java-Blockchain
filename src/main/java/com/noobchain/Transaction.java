package com.noobchain;

import java.security.*;
import java.util.ArrayList;

// Represents a transfer of coins from one wallet to another.
public class Transaction {

    public String transactionId;        // Unique hash of this transaction
    public PublicKey sender;            // Sender's public key (wallet address)
    public PublicKey recipient;         // Recipient's public key (wallet address)
    public float value;                 // Amount of coins to send
    public byte[] signature;            // Cryptographic proof the sender authorised this

    public ArrayList<TransactionInput> inputs;
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0;   // Counts transactions created so far

    public Transaction(PublicKey sender, PublicKey recipient, float value,
                       ArrayList<TransactionInput> inputs) {
        this.sender    = sender;
        this.recipient = recipient;
        this.value     = value;
        this.inputs    = inputs;
    }

    /**
     * Calculates the transaction's unique ID from its contents.
     */
    private String calculateHash() {
        sequence++;
        return StringUtil.applySha256(
            StringUtil.getStringFromKey(sender) +
            StringUtil.getStringFromKey(recipient) +
            Float.toString(value) +
            sequence
        );
    }

    /**
     * Signs the transaction with the sender's private key.
     * This proves the sender authorised the transfer without revealing their key.
     */
    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) +
                      StringUtil.getStringFromKey(recipient) +
                      Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    /**
     * Verifies the signature using the sender's public key.
     */
    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) +
                      StringUtil.getStringFromKey(recipient) +
                      Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    /**
     * Processes the transaction:
     * 1. Verifies the signature
     * 2. Gathers inputs from the UTXO pool
     * 3. Checks the sender has enough funds
     * 4. Creates outputs (coins to recipient + change back to sender)
     * 5. Removes spent inputs from the UTXO pool
     */
    public boolean processTransaction() {
        if (!verifySignature()) {
            System.out.println("Transaction signature failed to verify.");
            return false;
        }

        // Gather inputs from UTXO pool
        for (TransactionInput i : inputs) {
            i.UTXO = NoobChain.UTXOs.get(i.transactionOutputId);
        }

        // Check funds are sufficient
        if (getInputsValue() < NoobChain.minimumTransaction) {
            System.out.println("Transaction inputs are too small: " + getInputsValue());
            return false;
        }

        // Generate outputs
        float leftOver = getInputsValue() - value;
        transactionId = calculateHash();

        // Send value to recipient
        outputs.add(new TransactionOutput(recipient, value, transactionId));
        // Send change back to sender
        outputs.add(new TransactionOutput(sender, leftOver, transactionId));

        // Add outputs to UTXO pool
        for (TransactionOutput o : outputs) {
            NoobChain.UTXOs.put(o.id, o);
        }

        // Remove spent inputs from UTXO pool
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) continue;
            NoobChain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    public float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs) {
            if (i.UTXO == null) continue;
            total += i.UTXO.value;
        }
        return total;
    }

    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }
}