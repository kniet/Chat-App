package com.kniet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class ChatClient {

    private BufferedReader input;
    private PrintWriter output;
    private Socket socket;
    private String name;
    private static Scanner scanner = new Scanner(System.in);

    public BufferedReader getInput() {
        return input;
    }

    public void setInput(BufferedReader input) {
        this.input = input;
    }

    public PrintWriter getOutput() {
        return output;
    }

    public void setOutput(PrintWriter output) {
        this.output = output;
    }

    public ChatClient(Socket socket) {
        this.socket = socket;
        try {
            socket.setSoTimeout(5000);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
        }
    }

    public void start() {
        System.out.println("Enter your name: ");
        name = scanner.nextLine();
        if (name == null || name.equals("")) {
            closeAll();
            return;
        }
        output.println(name);
        System.out.println("Welcome " + name);
        System.out.println("Waiting for other users...");
        System.out.println("Type 'exit' to leave chat");
        System.out.println("-----------------------------");

        try {
            //Listen for incoming messages
            new Thread(() -> {
                String message;
                    try {
                        message = input.readLine();
                        while (message != null) {
                            message = input.readLine();
                            System.out.println(message);
                        }
                    } catch (SocketTimeoutException e) {
                        System.out.println("Server response timeout");
                        closeAll();
                        System.exit(1);
                    } catch (IOException e) {
                        System.out.println("Listener error " + e.getMessage());
                        closeAll();
                        System.exit(1);
                    }
            }).start();

            Thread.sleep(10000);
            //Send messages
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                if (message.equalsIgnoreCase("exit")) {
                    closeAll();
                    break;
                }
                output.println(message);

            }
        } catch (Exception e) {
            System.out.println("Send error " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {

            }
        }
        closeAll();
    }

    private void closeAll() {
        try {
            socket.close();
            input.close();
            output.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
