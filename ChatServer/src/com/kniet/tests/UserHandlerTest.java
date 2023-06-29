package com.kniet.tests;

import com.kniet.UserHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserHandlerTest {
    private static final int PORT = 5000;
    private static final String HOST = "localhost";
    private static final String HOST_NAME = "TestUser";

    private Thread serverThread;
    private Socket userSocket;
    private UserHandler userHandler;
    private BufferedReader reader;
    private PrintWriter printWriter;
    private ServerSocket serverSocket;

    @BeforeEach
    void setUp() throws IOException {
        serverSocket = new ServerSocket(PORT);
        serverThread = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    userSocket = serverSocket.accept();
                    userHandler = new UserHandler(userSocket);
                    Thread thread = new Thread(userHandler);
                    thread.start();
                } catch (IOException e) {
                }
            }
        });
        serverThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Socket userSocket = new Socket(HOST, PORT);
        printWriter = new PrintWriter(userSocket.getOutputStream(),true);
        reader = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
        printWriter.println(HOST_NAME);

    }

    @AfterEach
    void tearDown() throws IOException {
        if (userSocket != null) {
            userSocket.close();
        }
        serverThread.interrupt();
        printWriter.close();
        reader.close();
        serverSocket.close();
    }

    @Test
    void testUserHandlerJoinChat() {
        //wait for server to send message
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<UserHandler> users = UserHandler.users;
        assertEquals(1, users.size());
    }

    @Test
    void testSendMessage() throws IOException {
        Socket user2Socket = new Socket(HOST, PORT);
        PrintWriter printWriter2 = new PrintWriter(user2Socket.getOutputStream(),true);
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(user2Socket.getInputStream()));
        printWriter2.println(HOST_NAME);
        reader2.readLine();
        printWriter.println("Hello!");

        assertEquals(HOST_NAME + ": Hello!", reader2.readLine());
    }

    @Test
    void testUserHandlerLeaveChat() throws IOException {
        userSocket.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(UserHandler.users.isEmpty());
    }

    @Test
    void testUserHandlerSendMessageToAll() throws IOException {
        Socket user2Socket = new Socket(HOST, PORT);
        PrintWriter printWriter2 = new PrintWriter(user2Socket.getOutputStream(),true);
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(user2Socket.getInputStream()));
        userSocket.close();

        printWriter2.println(HOST_NAME);

        reader2.readLine();

        assertEquals(HOST_NAME + " has left the chat", reader2.readLine());
    }
}