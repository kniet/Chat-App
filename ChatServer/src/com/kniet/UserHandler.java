package com.kniet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class UserHandler implements Runnable {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String name;
    public static List<UserHandler> users = new ArrayList<>();

    public UserHandler(Socket socket) {
        this.socket = socket;
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(),true);
            this.name = input.readLine();
            users.add(this);
            sendMessageToAll(name + " joined the chat");
        } catch (IOException e) {
            System.out.println("Oops: " + e.getMessage());
            closeAll();
        }
    }

    @Override
    public void run() {
        String message;
        try {
            while (socket.isConnected()) {
                message = input.readLine();
                if (message == null) {
                    break;
                }
                sendMessage(name + ": " + message);
            }
        } catch (IOException e) {
            System.out.println("Error " + e.getMessage());
        } finally {
            if (name != null) {
                synchronized (users) {
                    sendMessageToAll(name + " has left the chat");
                }
            }
            leaveChat();
        }
    }

    private void sendMessage(String messageToSend) {
        for (UserHandler userHandler : users) {
            if (userHandler.equals(this)) {
                continue;
            }
            userHandler.output.println(messageToSend);
        }
    }

    private synchronized void sendMessageToAll(String messageToSend) {
        for (UserHandler userHandler : users) {
            userHandler.output.println(messageToSend);
        }
    }

    public void leaveChat() {
        users.remove(this);
        closeAll();
    }

    public void closeAll() {
        try {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Problem with closing " + e.getMessage());
        }
    }
}
