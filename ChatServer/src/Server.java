import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void runServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("New user joined");

                UserHandler userHandler = new UserHandler(socket);
                Thread thread = new Thread(userHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Problem with running server " + e.getMessage());
        }
    }
}
