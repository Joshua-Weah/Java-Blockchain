package com.noobchain;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.HashMap;
import java.util.Map;

// Represents a user's wallet — holds their public/private key pair.
public class Wallet {

    public PrivateKey privateKey; // Used to sign transactions (kept secret)
    public PublicKey publicKey;   // Used as the wallet's address (shared publicly)

    // Tracks unspent coins belonging to this wallet
    public HashMap<String, TransactionOutput> UTXOs = new HashMap<>();

    public Wallet() {
        generateKeyPair();
    }

    /**
     * Generates an elliptic curve key pair (ECDSA).
     * This gives the wallet a unique public address and private signing key.
     */
    private void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey  = keyPair.getPublic();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate key pair", e);
        }
    }

    /**
     * Checks the blockchain's UTXO pool and returns this wallet's balance.
     */
    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item :
                NoobChain.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            if (UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.id, UTXO);
                total += UTXO.value;
            }
        }
        return total;
    }

    /**
     * Creates and returns a new transaction sending coins to a recipient.
     */
    public Transaction sendFunds(PublicKey recipient, float value) {
        if (getBalance() < value) {
            System.out.println("Not enough funds to send. Transaction discarded.");
            return null;
        }

        // Gather enough UTXOs to cover the transaction
        java.util.ArrayList<TransactionInput> inputs = new java.util.ArrayList<>();
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if (total >= value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        // Remove spent UTXOs from wallet
        for (TransactionInput input : inputs) {
            UTXOs.remove(input.transactionOutputId);
        }

        return newTransaction;
    }
}