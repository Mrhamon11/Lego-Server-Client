import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class TestClient {
    public static void main(String[] args) {
        ArrayList<String> sets = getListOfSets();
        try(Socket connect = new Socket("localhost", 395);
            OutputStream os = connect.getOutputStream();
            PrintWriter pw = new PrintWriter(os, true);
            InputStreamReader isr = new InputStreamReader(connect.getInputStream());
            final BufferedReader br = new BufferedReader(isr);
            ){
            Random random = new Random();
            int setSize = sets.size();
            int requestNumber = 0;
            new Thread(() -> {
                while(true) {
                    try {
                        System.out.println(br.readLine());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            while(true) {
                Thread.sleep(50);
                int setIndex = random.nextInt(setSize);
                String setToGet = sets.get(setIndex);
                requestNumber++;
                pw.println(""+ requestNumber);
                pw.println("" + setToGet);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> getListOfSets() {
        ArrayList<String> sets = new ArrayList<>();
        try(FileReader frSets = new FileReader("sets.txt");
            BufferedReader setReader = new BufferedReader(frSets)) {
            String setNum;
            while((setNum = setReader.readLine()) != null) {
                sets.add(setNum);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sets;
    }
}
