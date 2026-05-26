package com.noobchain;

import java.security.PublicKey;

// Represents the output of a transaction — coins sent to a recipient.
public class TransactionOutput {

    public String id;
    public PublicKey recipient;  // The wallet this output belongs to
    public float value;          // Amount of coins
    public String parentTransactionId; // ID of the transaction that created this

    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(
            StringUtil.getStringFromKey(recipient) + value + parentTransactionId
        );
    }

    /**
     * Checks if this output belongs to the given public key (wallet address).
     */
    public boolean isMine(PublicKey publicKey) {
        return publicKey.equals(recipient);
    }
}