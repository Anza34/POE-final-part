/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.account3;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Student
 */
public class Account3Test {

    private Account3.MessageStore store;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        
        store = new Account3.MessageStore();
        Account3.MessageStore.sentCount = 0;
        Account3.MessageStore.disregardedCount = 0;
        Account3.MessageStore.storedCount = 0;
        
        Account3.MessageStore.sentMessages = new String[100];
        Account3.MessageStore.messageHashes = new String[100];
        Account3.MessageStore.messageIDs = new String[100];
        Account3.MessageStore.disregardedMessages = new String[100];
        Account3.MessageStore.storedMessages = new String[100];
        Account3.MessageStore.storedMessageIDs = new String[100];
        Account3.MessageStore.storedMessageHashes = new String[100];
        Account3.MessageStore.storedSenders = new String[100];
        Account3.MessageStore.storedRecipients = new String[100];

        try (FileWriter writer = new FileWriter("stored_messages.json", false)) {
            writer.write("[]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        store.addSentMessage("ID_1", "HASH_1", "+27834557896", "Did you get the cake?");
        store.addSentMessage("0838884567", "HASH_4", "+27838884567", "It is dinner time !");

        assertEquals("Did you get the cake?", Account3.MessageStore.sentMessages[0]);
        assertEquals("It is dinner time !", Account3.MessageStore.sentMessages[1]);
        assertEquals(2, Account3.MessageStore.sentCount);
    }

    @Test
    public void testDisplayTheLongestMessage() {
        Account3.MessageStore.storedMessages[0] = "Did you get the cake?";
        Account3.MessageStore.storedMessages[1] = "Where are you? You are late! I have asked you to be on time.";
        Account3.MessageStore.storedMessages[2] = "Yohoooo, I am at your gate.";
        Account3.MessageStore.storedMessages[3] = "It is dinner time !";
        
        Account3.MessageStore.storedMessageIDs[1] = "ID_2";
        Account3.MessageStore.storedMessageHashes[1] = "HASH_2";
        Account3.MessageStore.storedSenders[1] = "SenderName";
        Account3.MessageStore.storedRecipients[1] = "+27838884567";
        
        Account3.MessageStore.storedCount = 4;

        int longestIndex = 0;
        for (int i = 1; i < Account3.MessageStore.storedCount; i++) {
            if (Account3.MessageStore.storedMessages[i].length() > Account3.MessageStore.storedMessages[longestIndex].length()) {
                longestIndex = i;
            }
        }
        
        String longestMessage = Account3.MessageStore.storedMessages[longestIndex];

        assertEquals("Where are you? You are late! I have asked you to be on time.", longestMessage);
    }

    @Test
    public void testSearchForMessageID() {
        Account3.MessageStore.storedMessageIDs[0] = "0838884567";
        Account3.MessageStore.storedMessages[0] = "It is dinner time !";
        Account3.MessageStore.storedRecipients[0] = "+27838884567";
        Account3.MessageStore.storedCount = 1;

        boolean found = false;
        String foundMessage = "";
        for (int i = 0; i < Account3.MessageStore.storedCount; i++) {
            if (Account3.MessageStore.storedMessageIDs[i].equals("0838884567")) {
                foundMessage = Account3.MessageStore.storedMessages[i];
                found = true;
            }
        }

        assertTrue(found);
        assertEquals("It is dinner time !", foundMessage);
    }

    @Test
    public void testSearchMessagesByRecipient() {
        Account3.MessageStore.storedRecipients[0] = "+27838884567";
        Account3.MessageStore.storedMessages[0] = "Where are you? You are late! I have asked you to be on time.";
        
        Account3.MessageStore.storedRecipients[1] = "+27838884567";
        Account3.MessageStore.storedMessages[1] = "Ok, I am leaving without you.";
        
        Account3.MessageStore.storedRecipients[2] = "+27834484567";
        Account3.MessageStore.storedMessages[2] = "Yohoooo, I am at your gate.";
        
        Account3.MessageStore.storedCount = 3;

        StringBuilder systemReturns = new StringBuilder();
        
        for (int i = 0; i < Account3.MessageStore.storedCount; i++) {
            if (Account3.MessageStore.storedRecipients[i].equals("+27838884567")) {
                systemReturns.append(Account3.MessageStore.storedMessages[i]).append(" ");
            }
        }

        String expected = "Where are you? You are late! I have asked you to be on time. Ok, I am leaving without you. ";
        assertEquals(expected, systemReturns.toString());
    }

    @Test
    public void testDeleteMessageUsingMessageHash() {
        Account3.MessageStore.storedMessageHashes[0] = "TEST_HASH_2";
        Account3.MessageStore.storedMessages[0] = "Where are you? You are late! I have asked you to be on time.";
        Account3.MessageStore.storedCount = 1;

        String hashToDelete = "TEST_HASH_2";
        int deleteIndex = -1;
        String deletedMessageText = "";

        for (int i = 0; i < Account3.MessageStore.storedCount; i++) {
            if (Account3.MessageStore.storedMessageHashes[i].equals(hashToDelete)) {
                deleteIndex = i;
                deletedMessageText = Account3.MessageStore.storedMessages[i];
                break;
            }
        }

        if (deleteIndex != -1) {
            System.out.print("Message: \"" + deletedMessageText + "\" successfully deleted.");
            Account3.MessageStore.storedCount--;
        }

        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.", outContent.toString());
        assertEquals(0, Account3.MessageStore.storedCount);
    }

    @Test
    public void testDisplayReport() {
        store.addSentMessage("ID1", "HASH_1", "+27834557896", "Did you get the cake?");
        store.addSentMessage("ID4", "HASH_4", "+27838884567", "It is dinner time !");

        for (int i = 0; i < Account3.MessageStore.sentCount; i++) {
            System.out.println("Message Hash: " + Account3.MessageStore.messageHashes[i]);
            System.out.println("Recipient: " + Account3.MessageStore.sentMessages[i]);
            System.out.println("Message: " + Account3.MessageStore.sentMessages[i]);
        }

        String output = outContent.toString();
        
        assertTrue(output.contains("Message Hash: HASH_1"));
        assertTrue(output.contains("Did you get the cake?"));
        assertTrue(output.contains("Message Hash: HASH_4"));
        assertTrue(output.contains("It is dinner time !"));
    }
}