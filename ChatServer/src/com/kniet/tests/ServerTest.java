package com.kniet.tests;

import com.kniet.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerTest {
    private Server server;
    private ServerSocket socket;

    @BeforeEach
    void setUp() {
        try {
            socket = new ServerSocket(5000);
            server = new Server(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testServerAcceptsConnections() {
        Thread serverThread = new Thread(() -> server.runServer());
        serverThread.start();

        try (Socket clientSocket = new Socket("localhost", 5000)){
            assertTrue(clientSocket.isConnected());
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverThread.interrupt();
    }

    @Test
    void testServerAcceptsMultipleConnections() {
        Thread serverThread = new Thread(() -> server.runServer());
        serverThread.start();

        try (Socket clientSocket1 = new Socket("localhost", 5000);
             Socket clientSocket2 = new Socket("localhost", 5000);
             Socket clientSocket3 = new Socket("localhost", 5000)){

            assertTrue(clientSocket1.isConnected());
            assertTrue(clientSocket2.isConnected());
            assertTrue(clientSocket3.isConnected());
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverThread.interrupt();
    }
}