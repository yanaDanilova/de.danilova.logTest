import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {

    private static final Logger log = LogManager.getLogger(Server.class);

    private int port;
    private List<ClientHandler> clients;
    private DbAuthenticationProvider authenticationProvider;


    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
        this.authenticationProvider = new DbAuthenticationProvider();
        this.authenticationProvider.connect();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on the port 8189.");
            log.info("Server is running on the port 8189.");
            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("Client is connected");
                log.info("Client is connected");
                ExecutorService executorService = Executors.newFixedThreadPool(10);
                new ClientHandler(this, socket, executorService);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Connection error");
        }finally {
            authenticationProvider.disconnect();
        }

    }


    public synchronized void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
        log.info("Client"+clientHandler.getUsername()+ "is connected ");
        broadcast("The client " + clientHandler.getUsername() + " logged in to the chat ");
        broadcastClientsList();
    }

    public synchronized void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
        broadcast("The client " + clientHandler.getUsername() + " exit the chat ");
        broadcastClientsList();
    }

    public synchronized void broadcastClientsList() {
        StringBuilder stringBuilder = new StringBuilder("/clients_list ");
        for(ClientHandler c : clients){
            stringBuilder.append(c.getUsername()).append(" ");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        String clientsList = stringBuilder.toString();
        for(ClientHandler clientHandler : clients){
            clientHandler.sendMessage(clientsList);
        }
    }

    public synchronized void broadcast(String message){
        for (ClientHandler clientHandler: clients){
            clientHandler.sendMessage(message);
        }

    }

    public synchronized void sendPrivateMsg(ClientHandler sender, String receiverUsername, String message){
        for(ClientHandler c : clients){
            if(c.getUsername().equals(receiverUsername)){
                c.sendMessage("From: " + sender.getUsername() + " Message: " + message);
                sender.sendMessage("To the user: " + receiverUsername + " Message: " + message);
                return;
            }
        }
        sender.sendMessage("Unable to send the message to user:" + receiverUsername + " There is no such user ");
    }

    public synchronized boolean isNickBusy(String username) {
        for (ClientHandler clientHandler: clients){
            if (clientHandler.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }
}





