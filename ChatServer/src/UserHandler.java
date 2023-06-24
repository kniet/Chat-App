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
    public static List<UserHandler> users = new ArrayList<>();

    public UserHandler(Socket socket) {
        this.socket = socket;
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(),true);
            users.add(this);
        } catch (IOException e) {
            System.out.println("Oops: " + e.getMessage());
            closeAll();
        }
    }

    @Override
    public void run() {
        String message;
        while (socket.isConnected()) {
            try {
                message = input.readLine();
                sendMessage(message);
            } catch (IOException e) {
                System.out.println("Problem with sending message " + e.getMessage());
                closeAll();
                break;
            }
        }
    }

    private void sendMessage(String messageToSend) {

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
