package com.kniet;

import java.io.IOException;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000)) {
            ChatClient chatClient = new ChatClient(socket);
            chatClient.start();
        } catch (IOException e) {
            System.out.println("Problem with connection " + e.getMessage());
        }
    }
}