/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.account3;

/**
 *
 * @author Student
 */
import java.util.Scanner;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;

public class Account3 {

    static String registeredUsername = "";
    static String registeredPassword = "";
    static String registeredPhone = "";
    static String firstName = "";
    static String lastName = "";

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("=== REGISTRATION ===");
        System.out.print("Enter your First Name:");
        firstName = input.nextLine();
        System.out.print("Enter your Last Name:");
        lastName = input.nextLine();
        System.out.print("Enter username:");
        String username = input.nextLine();

        if (checkUserName(username)) {
            System.out.println("Username successfully captured");
        } else {
            System.out.println("Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length");
            input.close();
            return;
        }

        // Password
        System.out.print("Enter password:");
        String password = input.nextLine();

        if (checkPasswordComplexity(password)) {
            System.out.println("Password successfully captured");
        } else {
            System.out.println("Password is not correctly formatted please ensure that the password contains at least eight characters, capital letter, number, and a special character");
            input.close();
            return;
        }

        System.out.print("Enter cell phone number:");
        String phone = input.nextLine();

        if (checkCellPhoneNumber(phone)) {
            System.out.println("Cell phone number successfully captured");
        } else {
            System.out.println("Cell phone number incorrectly formatted or does not contain an international code, please correct the number and try again");
            input.close();
            return;
        }

        // Store details
        registeredUsername = username;
        registeredPassword = password;
        registeredPhone = phone;

        System.out.println("Registration successful");

        // LOGIN
        System.out.println("\n=== LOGIN ===");

        System.out.print("Enter username:");
        String loginUser = input.nextLine();

        System.out.print("Enter password:");
        String loginPass = input.nextLine();

        boolean loginSuccess = loginUser(loginUser, loginPass);

        System.out.println(returnLoginStatus(loginSuccess));

        if (loginSuccess) {
            Message myMessageApp = new Message();
            myMessageApp.showMenu(input);
        }

        input.close();
    }

    // Username validation
    public static boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5 && username.length() > 0;
    }

    // Password validation
    public static boolean checkPasswordComplexity(String password) {
        if (password == null) return false;

        boolean length = password.length() >= 8;
        boolean capital = false;
        boolean number = false;
        boolean special = false;

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (Character.isUpperCase(c)) capital = true;
            else if (Character.isDigit(c)) number = true;
            else if ("!@#$%^&*".indexOf(c) >= 0) special = true;
        }

        return length && capital && number && special;
    }

    // Phonenumber validation
    public static boolean checkCellPhoneNumber(String number) {
        if (number == null) return false;
        return number.matches("^\\+27\\d{9}$");
    }

    public static boolean loginUser(String username, String password) {
        return username != null &&
                password != null &&
                username.equals(registeredUsername) &&
                password.equals(registeredPassword);
    }

    // display login message
    public static String returnLoginStatus(boolean success) {
        if (success) {
            return "Welcome " + firstName + " " + lastName + " it is great to see you";
        } else {
            return "Username or password is incorrect please try again";
        }
    }

    // Part 2
    static class Message {

        static int totalMessagesSent = 0;
        static String allMessagesData = "";

        static MessageStore store = new MessageStore();

        public void showMenu(Scanner input) {
            boolean running = true;

            System.out.println("\nWelcome to QuickChat.");

            while (running) {
                System.out.println("\nPlease choose an option:");
                System.out.println("1) Send Messages");
                System.out.println("2) Stored Message Menu (search, delete, reports)");
                System.out.println("3) Quit");
                System.out.print("Choice: ");

                String choice = input.nextLine();

                if (choice.equals("1")) {
                    System.out.print("How many messages do you wish to enter? ");
                    int numMessages = input.nextInt();
                    input.nextLine();

                    for (int i = 0; i < numMessages; i++) {
                        System.out.println("\n--- Message " + (i + 1) + " ---");

                        String recipient = "";
                        while (true) {
                            System.out.print("Enter recipient cell number (starting with +27, followed by 9 digits): ");
                            recipient = input.nextLine();
                            String validRecipient = checkRecipientCell(recipient);
                            if (validRecipient.equals("Valid")) {
                                break;
                            } else {
                                System.out.println("Invalid number. Must start with +27 followed by exactly 9 digits.");
                            }
                        }

                        // Message limit code here
                        String messageText = "";
                        while (true) {
                            System.out.print("Enter your message (max 250 characters): ");
                            messageText = input.nextLine();
                            if (messageText.length() <= 250 && messageText.length() > 0) {
                                System.out.println("Message sent");
                                break;
                            } else {
                                System.out.println("Please enter a message of less than 250 characters.");
                            }
                        }

                        String messageID = generateRandomID();
                        while (!checkMessageID(messageID)) {
                            messageID = generateRandomID();
                        }

                        String messageHash = createMessageHash(messageID, totalMessagesSent, messageText);
                        String actionResult = SentMessage(input);
                        System.out.println(actionResult);

                        if (actionResult.equals("Message successfully sent")) {
                            totalMessagesSent++;
                            store.addSentMessage(messageID, messageHash, recipient, messageText);

                            System.out.println("\n--- Sent Message Details ---");
                            System.out.println("Message ID: " + messageID);
                            System.out.println("Message Hash: " + messageHash);
                            System.out.println("Recipient: " + recipient);
                            System.out.println("Message: " + messageText);
                            System.out.println("----------------------------");

                            allMessagesData += "Message ID: " + messageID + "\n"
                                    + "Message Hash: " + messageHash + "\n"
                                    + "Recipient: " + recipient + "\n"
                                    + "Message: " + messageText + "\n\n";

                        } else if (actionResult.equals("Message successfully stored")) {
                            storeMessage(messageID, messageHash, firstName + " " + lastName, recipient, messageText);

                        } else {
                            store.addDisregardedMessage(messageText);
                        }
                    }

                    // Generate message summary
                    System.out.println("\n--- All Sent Messages Summary ---");
                    System.out.println(printMessages());
                    System.out.println("Total messages sent so far: " + returnTotalMessagess());

                } else if (choice.equals("2")) {
                    store.showStoredMessagesMenu(input);

                } else if (choice.equals("3")) {
                    running = false;
                    System.out.println("Goodbye!");
                } else {
                    System.out.println("Invalid option. Please try again.");
                }
            }
        }

        public String generateRandomID() {
            Random rand = new Random();
            StringBuilder id = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                id.append(rand.nextInt(10));
            }
            return id.toString();
        }

        public boolean checkMessageID(String messageID) {
            return messageID != null && messageID.length() <= 10;
        }

        public String checkRecipientCell(String cellNumber) {
            if (cellNumber != null && cellNumber.matches("^\\+27\\d{9}$")) {
                return "Valid";
            }
            return "Invalid";
        }

        public String createMessageHash(String messageID, int messageNumber, String message) {
            String firstTwoID = messageID.substring(0, 2);

            String[] words = message.split(" ");
            String firstWord = words[0];
            String lastWord = words[words.length - 1];

            String hash = firstTwoID + ":" + messageNumber + ":" + firstWord + lastWord;
            return hash.toUpperCase();
        }

        public String SentMessage(Scanner input) {
            System.out.println("\nWhat would you like to do with this message?");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.print("Choice: ");

            String choice = input.nextLine();

            if (choice.equals("1")) {
                return "Message successfully sent";
            } else if (choice.equals("2")) {
                return "Press 0 to delete the message";
            } else if (choice.equals("3")) {
                return "Message successfully stored";
            } else {
                return "Invalid choice, defaulting to disregard. Press 0 to delete the message";
            }
        }

        public String printMessages() {
            if (allMessagesData.equals("")) {
                return "No messages sent yet.";
            }
            return allMessagesData;
        }

        public int returnTotalMessagess() {
            return totalMessagesSent;
        }

        // is appended, and ] is restored — keeping the file valid at all times.
        public void storeMessage(String id, String hash, String sender, String recipient, String message) {
            String newObject = "  {\n" +
                    " \"MessageID\": \""   + id + "\",\n" +
                    " \"MessageHash\": \"" + hash + "\",\n" +
                    "  \"Sender\": \""+ sender + "\",\n" +
                    " \"Recipient\": \""+ recipient + "\",\n" +
                    " \"Message\": \""+ message + "\"\n"  +
                    "  }";

            try {
                java.io.File file = new java.io.File("stored_messages.json");

                if (!file.exists() || file.length() == 0) {
                    // First message — create the file as a fresh JSON array
                    FileWriter writer = new FileWriter(file, false);
                    writer.write("[\n" + newObject + "\n]\n");
                    writer.close();
                } else {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    reader.close();
                    String existing = content.toString().stripTrailing();
                    if (existing.endsWith("]")) {
                        existing = existing.substring(0, existing.length() - 1).stripTrailing();
                    }

                    FileWriter writer = new FileWriter(file, false);
                    writer.write(existing + ",\n" + newObject + "\n]\n");
                    writer.close();
                }

            } catch (IOException e) {
                System.out.println("An error occurred while saving to JSON.");
                e.printStackTrace();
            }
        }
    }

    // Part 3
    static class MessageStore {

        // Maximum number of messages each array can hold
        static final int MAX_MESSAGES = 100;
        //  Sent messages arrays
        static String[]sentMessages= new String[MAX_MESSAGES];
        static String[]messageHashes= new String[MAX_MESSAGES];
        static String[]messageIDs= new String[MAX_MESSAGES];
        static int sentCount= 0;
        static String[] disregardedMessages = new String[MAX_MESSAGES];
        static int disregardedCount= 0; // tracks how many disregarded messages are stored
        static String[]storedMessages= new String[MAX_MESSAGES];
        static String[]storedMessageIDs= new String[MAX_MESSAGES];
        static String[]storedMessageHashes = new String[MAX_MESSAGES];
        static String[]storedSenders= new String[MAX_MESSAGES];
        static String[]storedRecipients= new String[MAX_MESSAGES];
        static int storedCount= 0;

        // Populate sent arrays when a message is sent
        public void addSentMessage(String id, String hash, String recipient, String message) {
            if (sentCount < MAX_MESSAGES) {
                sentMessages[sentCount]  = message;
                messageHashes[sentCount] = hash;
                messageIDs[sentCount]    = id;
                sentCount++;
            }
        }

        // Populate disregarded array when a message is disregarded
        public void addDisregardedMessage(String message) {
            if (disregardedCount < MAX_MESSAGES) {
                disregardedMessages[disregardedCount] = message;
                disregardedCount++;
            }
        }

        public void loadStoredMessagesFromFile() {
            // Reset the stored arrays and counter before reloading from disk
            storedMessages= new String[MAX_MESSAGES];
            storedMessageIDs= new String[MAX_MESSAGES];
            storedMessageHashes= new String[MAX_MESSAGES];
            storedSenders= new String[MAX_MESSAGES];
            storedRecipients= new String[MAX_MESSAGES];
            storedCount= 0;

            try {
                BufferedReader reader = new BufferedReader(new FileReader("stored_messages.json"));
                String line;

                // Temporary holders for one message block at a time
                String tempID = "";
                String tempHash = "";
                String tempSender = "";
                String tempRecipient = "";
                String tempMessage = "";

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    if (line.equals("[") || line.equals("]")) {
                        continue;
                    }

                    if (line.contains("\"MessageID\"")) {
                        tempID = line.split("\"")[3];
                    }
                    if (line.contains("\"MessageHash\"")) {
                        tempHash = line.split("\"")[3];
                    }
                    if (line.contains("\"Sender\"")) {
                        tempSender = line.split("\"")[3];
                    }
                    if (line.contains("\"Recipient\"")) {
                        tempRecipient = line.split("\"")[3];
                    }
                    if (line.contains("\"Message\"")) {
                        tempMessage = line.split("\"")[3];
                    }

                    if ((line.equals("}") || line.equals("},")) && storedCount < MAX_MESSAGES) {
                        storedMessageIDs[storedCount]    = tempID;
                        storedMessageHashes[storedCount] = tempHash;
                        storedSenders[storedCount]       = tempSender.isEmpty() ? "Unknown" : tempSender;
                        storedRecipients[storedCount]    = tempRecipient;
                        storedMessages[storedCount]      = tempMessage;
                        storedCount++;

                        // Reset temp holders for the next block
                        tempID = ""; tempHash = ""; tempSender = "";
                        tempRecipient = ""; tempMessage = "";
                    }
                }

                reader.close();

            } catch (IOException e) {
                System.out.println("Could not read stored messages file.");
            }
        }

        // Menu option 2 - Stored Messages
        public void showStoredMessagesMenu(Scanner input) {
            boolean running = true;

            while (running) {
                System.out.println("\n=== STORED MESSAGES MENU ===");
                System.out.println("a) Display sender and recipient of all stored messages");
                System.out.println("b) Display the longest stored message");
                System.out.println("c) Search for a message by ID");
                System.out.println("d) Search for messages by recipient");
                System.out.println("e) Delete a message using message hash");
                System.out.println("f) Display full report of all stored messages");
                System.out.println("g) Back to main menu");
                System.out.print("Choice: ");

                String choice = input.nextLine();

                if (choice.equals("a")) {
                    displaySenderAndRecipient();
                } else if (choice.equals("b")) {
                    displayLongestMessage();
                } else if (choice.equals("c")) {
                    System.out.print("Enter message ID to search: ");
                    String searchID = input.nextLine();
                    searchByMessageID(searchID);
                } else if (choice.equals("d")) {
                    System.out.print("Enter recipient number to search: ");
                    String searchRecipient = input.nextLine();
                    searchByRecipient(searchRecipient);
                } else if (choice.equals("e")) {
                    System.out.print("Enter message hash to delete: ");
                    String hashToDelete = input.nextLine();
                    deleteByHash(hashToDelete);
                } else if (choice.equals("f")) {
                    displayFullReport();
                } else if (choice.equals("g")) {
                    running = false;
                } else {
                    System.out.println("Invalid option. Please try again.");
                }
            }
        }

        // a) Display both sender AND recipient for each stored message
        public void displaySenderAndRecipient() {
            loadStoredMessagesFromFile();

            if (storedCount == 0) {
                System.out.println("No stored messages found.");
                return;
            }

            System.out.println("\n--- Stored Message Senders and Recipients ---");
            for (int i = 0; i < storedCount; i++) {
                System.out.println("Message " + (i + 1) + " Sender: " + storedSenders[i] + " | Recipient: " + storedRecipients[i]);
            }
        }

        // Display the longest stored message
        public void displayLongestMessage() {
            loadStoredMessagesFromFile();

            if (storedCount == 0) {
                System.out.println("No stored messages found.");
                return;
            }

            int longestIndex = 0;
            for (int i = 1; i < storedCount; i++) {
                if (storedMessages[i].length() > storedMessages[longestIndex].length()) {
                    longestIndex = i;
                }
            }

            System.out.println("\nLongest stored message details:");
            System.out.println("Message ID: "   + storedMessageIDs[longestIndex]);
            System.out.println("Message Hash: " + storedMessageHashes[longestIndex]);
            System.out.println("Sender: "       + storedSenders[longestIndex]);
            System.out.println("Recipient: "    + storedRecipients[longestIndex]);
            System.out.println("Message: "      + storedMessages[longestIndex]);
        }

        //  Search for a message by ID and display recipient and message
        public void searchByMessageID(String searchID) {
            loadStoredMessagesFromFile();

            if (storedCount == 0) {
                System.out.println("No message IDs found.");
                return;
            }

            boolean found = false;
            for (int i = 0; i < storedCount; i++) {
                if (storedMessageIDs[i].equals(searchID)) {
                    System.out.println("\nMessage found:");
                    System.out.println("Recipient: " + storedRecipients[i]);
                    System.out.println("Message: "   + storedMessages[i]);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No message found with that ID.");
            }
        }

        //  Search for all messages stored for a particular recipient
        public void searchByRecipient(String recipient) {
            loadStoredMessagesFromFile();

            boolean found = false;
            System.out.println("\n--- Messages for " + recipient + " ---");

            for (int i = 0; i < storedCount; i++) {
                if (storedRecipients[i].equals(recipient)) {
                    System.out.println("Message: " + storedMessages[i]);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No messages found for that recipient.");
            }
        }

        public void deleteByHash(String hash) {
            loadStoredMessagesFromFile();

            if (storedCount == 0) {
                System.out.println("No message hashes found.");
                return;
            }

            int deleteIndex = -1;
            for (int i = 0; i < storedCount; i++) {
                if (storedMessageHashes[i].equals(hash)) {
                    deleteIndex = i;
                    break;
                }
            }

            if (deleteIndex == -1) {
                System.out.println("No message found with that hash.");
                return;
            }

            for (int i = deleteIndex; i < storedCount - 1; i++) {
                storedMessages[i]      = storedMessages[i + 1];
                storedMessageIDs[i]    = storedMessageIDs[i + 1];
                storedMessageHashes[i] = storedMessageHashes[i + 1];
                storedSenders[i]       = storedSenders[i + 1];
                storedRecipients[i]    = storedRecipients[i + 1];
            }


            storedMessages[storedCount - 1]      = null;
            storedMessageIDs[storedCount - 1]    = null;
            storedMessageHashes[storedCount - 1] = null;
            storedSenders[storedCount - 1]       = null;
            storedRecipients[storedCount - 1]    = null;
            storedCount--;

            System.out.println("Message successfully deleted.");

            rewriteStoredMessagesFile();
        }

        private void rewriteStoredMessagesFile() {
            try {
                FileWriter writer = new FileWriter("stored_messages.json", false);

                writer.write("[\n");

                for (int i = 0; i < storedCount; i++) {
                    String jsonObject = "  {\n" +
                            " \"MessageID\": \"" + storedMessageIDs[i] + "\",\n" +
                            " \"MessageHash\": \"" + storedMessageHashes[i] + "\",\n" +
                            " \"Sender\": \""  + storedSenders[i] + "\",\n" +
                            " \"Recipient\": \"" + storedRecipients[i]  + "\",\n" +
                            " \"Message\": \""  + storedMessages[i]   + "\"\n"  +
                            "  }";

                    // Add a comma after every object except the last one
                    if (i < storedCount - 1) {
                        writer.write(jsonObject + ",\n");
                    } else {
                        writer.write(jsonObject + "\n");
                    }
                }

                writer.write("]\n");
                writer.close();
            } catch (IOException e) {
                System.out.println("An error occurred while updating the stored messages file.");
                e.printStackTrace();
            }
        }

        //Stored messages report
        public void displayFullReport() {
            loadStoredMessagesFromFile();

            if (storedCount == 0) {
                System.out.println("No stored messages to display.");
                return;
            }

            System.out.println("\n=== FULL STORED MESSAGES REPORT ===");
            for (int i = 0; i < storedCount; i++) {
                System.out.println("--- Message " + (i + 1) + " ---");
                System.out.println("Message ID: "   + storedMessageIDs[i]);
                System.out.println("Message Hash: " + storedMessageHashes[i]);
                System.out.println("Sender: " + storedSenders[i]);
                System.out.println("Recipient: " + storedRecipients[i]);
                System.out.println("Message: " + storedMessages[i]);
                System.out.println("================================");
            }
        }
    }
}