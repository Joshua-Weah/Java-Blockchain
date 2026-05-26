package com.noobchain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

// Main class that creates and validates the blockchain with transactions.
public class NoobChain {

    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();

    public static int difficulty = 3;
    public static float minimumTransaction = 0.1f;

    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;

    public static void main(String[] args) {
        // Register BouncyCastle as the security provider for ECDSA
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Create two wallets
        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        System.out.println("WalletA balance: " + walletA.getBalance());
        System.out.println("WalletB balance: " + walletB.getBalance());

        // Create genesis transaction — sends 100 coins to walletA
        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);
        genesisTransaction.transactionId = "0"; // Manually set genesis ID
        genesisTransaction.outputs.add(new TransactionOutput(
            genesisTransaction.recipient,
            genesisTransaction.value,
            genesisTransaction.transactionId
        ));
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        System.out.println("\nCreating and mining genesis block...");
        Block genesis = new Block("Genesis Block", "0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        // WalletA sends 40 coins to WalletB
        Block block1 = new Block("Block 1", blockchain.get(blockchain.size()-1).hash);
        System.out.println("\nWalletA balance: " + walletA.getBalance());
        System.out.println("WalletA sending 40 coins to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("WalletA balance: " + walletA.getBalance());
        System.out.println("WalletB balance: " + walletB.getBalance());

        // WalletB sends 20 coins back to WalletA
        Block block2 = new Block("Block 2", blockchain.get(blockchain.size()-1).hash);
        System.out.println("\nWalletB sending 20 coins to WalletA...");
        block2.addTransaction(walletB.sendFunds(walletA.publicKey, 20f));
        addBlock(block2);
        System.out.println("WalletA balance: " + walletA.getBalance());
        System.out.println("WalletB balance: " + walletB.getBalance());

        // WalletB tries to send more than it has
        Block block3 = new Block("Block 3", blockchain.get(blockchain.size()-1).hash);
        System.out.println("\nWalletB attempting to send 50 coins (insufficient funds)...");
        block3.addTransaction(walletB.sendFunds(walletA.publicKey, 50f));
        addBlock(block3);
        System.out.println("WalletA balance: " + walletA.getBalance());
        System.out.println("WalletB balance: " + walletB.getBalance());

        System.out.println("\nBlockchain valid: " + isChainValid());
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

    public static boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock  = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Hash mismatch on block " + i);
                return false;
            }

            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Chain broken at block " + i);
                return false;
            }

            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("Block " + i + " has not been mined");
                return false;
            }
        }
        return true;
    }
}