import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestClient {
    public static void main(String[] args) {
        try(Socket connect = new Socket("localhost", 395);
            OutputStream os = connect.getOutputStream();
            PrintWriter pw = new PrintWriter(os, true);
            InputStreamReader isr = new InputStreamReader(connect.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            ){
            System.out.println("Sending to server");
            pw.println("3420");
            pw.println("9845-1");
            System.out.println(br.readLine());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
