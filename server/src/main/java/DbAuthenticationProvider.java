import java.sql.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class DbAuthenticationProvider implements AuthenticationProvider {
    private static Connection connection;
    private static Statement stmt;



    public  void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Users.db");
            stmt = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to connect to the database");
        }

    }

    public  void disconnect(){
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        try(ResultSet rs = stmt.executeQuery("select nickname from users where username = '"+login+"' and password = '" + password + "';")) {
            return rs.getString("nickname");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void changeNickname(String oldNickname, String newNickname) {
    throw new UnsupportedOperationException();
    }
}
