package com.kniet;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(5000)){
            Server server = new Server(serverSocket);
            server.runServer();

        } catch (IOException e) {
            System.out.println("com.kniet.Server exception " + e.getMessage());
        }
    }
}