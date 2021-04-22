import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class ClientHandler {

    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;

    public String getUsername() {
        return username;
    }

    public ClientHandler(Server server, Socket socket, ExecutorService executorService) throws IOException {
        this.server = server;
        this.socket = socket;

        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        executorService.execute(() -> {
            try {
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/login ")) {
                        String[] tokens = msg.split("\\s+");
                        if(tokens.length != 3){
                            sendMessage("/login_failed Enter Login and Password");
                            continue;
                        }

                        String login = tokens[1];
                        String password = tokens[2];
                        String userNickname = server.getAuthenticationProvider().getNicknameByLoginAndPassword(login,password);
                        if(userNickname == null){
                            sendMessage("/login_failed Incorrect login/password entered");
                            continue;
                        }

                        if (server.isNickBusy(userNickname)) {
                            sendMessage("/login_failed The account is already use");
                            continue;
                        }

                        username = userNickname;
                        sendMessage("/login_ok " + username);
                        server.subscribe(this);
                        break;

                    }
                }
                while (true) {
                    String msg = in.readUTF();
                    if(msg.startsWith("/")){
                        executeCommand(msg);
                        continue;
                    }
                    server.broadcast(username + ":" + msg + "\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
                executorService.shutdown();
            }
        });
    }

    private void executeCommand(String cmd)  {
        if(cmd.startsWith("/w ")){
            String[] tokens = cmd.split("\\s+",3);
            if(tokens.length !=3){
                sendMessage("Server: Incorrect command");
                return;
            }
            server.sendPrivateMsg(this, tokens[1], tokens[2]);
        }
        if(cmd.startsWith("/change_nickname")){
            String[] tokens = cmd.split("\\s+");
            if(tokens.length !=2){
                sendMessage("Server: Incorrect command");
                return;
            }
            String newNickname = tokens[1];
            if(server.isNickBusy(newNickname)){
                sendMessage("This Nickname is already use");
                return;
            }
            server.getAuthenticationProvider().changeNickname(username,newNickname);
            username = newNickname;
            sendMessage("Nickname changed on "+ newNickname);
            server.broadcastClientsList();

        }
        if(cmd.startsWith("/logout ")){
            disconnect();
        }

    }

    public void sendMessage(String message)  {
           try{
               out.writeUTF(message);
           }catch (IOException e){
               disconnect();
           }
        }

        public void disconnect(){
        server.unsubscribe(this);
        if(socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
