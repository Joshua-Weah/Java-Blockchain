package com.noobchain;

import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

// Utility class providing cryptographic helper methods for the blockchain.
public class StringUtil {

    /**
     * Applies the SHA-256 hashing algorithm to a given string input.
     */
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply SHA-256", e);
        }
    }

    /**
     * Signs data with a private key using ECDSA.
     * Returns the signature as a byte array.
     */
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        try {
            Signature dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            dsa.update(input.getBytes());
            return dsa.sign();
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply ECDSA signature", e);
        }
    }

    /**
     * Verifies an ECDSA signature using the sender's public key.
     * Returns true if the signature is valid.
     */
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify ECDSA signature", e);
        }
    }

    /**
     * Converts a public or private key to a Base64 string.
     * Used when hashing transaction data.
     */
    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Calculates a Merkle root from a list of transactions.
     * Summarises all transactions in the block into a single hash.
     */
    public static String getMerkleRoot(java.util.ArrayList<Transaction> transactions) {
        int count = transactions.size();
        java.util.ArrayList<String> previousTreeLayer = new java.util.ArrayList<>();
        for (Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.transactionId);
        }
        java.util.ArrayList<String> treeLayer = previousTreeLayer;
        while (count > 1) {
            treeLayer = new java.util.ArrayList<>();
            for (int i = 1; i < previousTreeLayer.size(); i += 2) {
                treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }
        return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }
}