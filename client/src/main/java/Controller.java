import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    TextField msgField, loginField;

    @FXML
    TextArea msgArea;

    @FXML
    HBox loginPanel, msgPanel;

    @FXML
    ListView<String> clientsList;

    @FXML
    PasswordField passwordField;

    @FXML
    Button loBtn;


    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;


    public void setUsername(String username) {
        this.username = username;
        boolean isUsernameNull = username == null;

        loginPanel.setVisible(isUsernameNull);
        loginPanel.setManaged(isUsernameNull);
        msgPanel.setVisible(!isUsernameNull);
        msgPanel.setManaged(!isUsernameNull);
        clientsList.setVisible(!isUsernameNull);
        clientsList.setManaged(!isUsernameNull);
        loBtn.setVisible(!isUsernameNull);
        loBtn.setManaged(!isUsernameNull);


    }

    public void login() {

        if (loginField.getText().isEmpty()) {
            showErrorAlert("Username cannot be empty!");
            return;
        }

        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            out.writeUTF("/login " + loginField.getText() + " " +passwordField.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void logout() {
        try {
            out.writeUTF("/logout ");
        } catch (IOException e) {
            e.printStackTrace();
        }

        login();
        msgArea.clear();
        loginField.clear();


    }

    private void connect() {

        try {
            socket = new Socket("localHost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            Thread thread = new Thread(() -> {
                try {
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith("/login_ok ")) {
                            setUsername(msg.split(" ")[1]);
                            msgArea.appendText(MessageHistory.showHistory(username));
                            break;
                        }
                        if (msg.startsWith("/login_failed ")) {
                            String couse = msg.split(" ", 2)[1];
                            msgArea.appendText(couse + "/n");
                        }
                    }
                    while (true) {
                        String msg = in.readUTF();
                        if (msg.startsWith("/")) {
                            if (msg.startsWith("/clients_list ")) {
                                String[] tokens = msg.split("\\s");

                                Platform.runLater(() -> {
                                    clientsList.getItems().clear();
                                    for (int i = 1; i < tokens.length; i++) {
                                        clientsList.getItems().add(tokens[i]);

                                    }
                                });
                            }
                            continue;
                        }

                        msgArea.appendText(msg + "\n");
                        MessageHistory.createAndWriteHistoryFile(msg, username);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
                }
            });
            thread.start();
        } catch (IOException e) {
            showErrorAlert("Unable to connect to the server");
        }
    }

    public void disconnect() {
        setUsername(null);
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(msgField.getText());
            msgField.clear();
            msgField.requestFocus();
        } catch (IOException e) {
            showErrorAlert("Unable to send message");
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUsername(null);
    }

    private void showErrorAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.setTitle("Chat");
        alert.setHeaderText(null);
        alert.showAndWait();

    }
}

