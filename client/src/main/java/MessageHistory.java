import java.io.*;
import java.nio.charset.StandardCharsets;

public class MessageHistory {

  public static String showHistory (String username){
      StringBuilder stringBuilder = new StringBuilder();
        try(InputStream in = new BufferedInputStream(new FileInputStream(username + "history.txt"))){
            {
                int x;
                while((x = in.read())!= -1){
                   stringBuilder.append((char)x);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

      return stringBuilder.toString();
  }


    public static void createAndWriteHistoryFile(String msg,String username){
       File historyFile = new File(username + "history.txt");
        try(OutputStream out = new BufferedOutputStream(new FileOutputStream(historyFile,true))){
            out.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
