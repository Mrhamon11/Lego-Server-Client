import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    //TODO lots of locks, static (sets, parts)


    public static void main(String[] args) {
        makeLocks();
        AmazonDynamoDBClient dynamoDBClient = startDynamoClient();
        Watcher myWatcher = new Watcher();
        ServerSocket server = null;
        //Test
        Socket clientConnection = null;
        try {
            server = new ServerSocket();
            clientConnection = server.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(InputStreamReader is = new InputStreamReader(clientConnection.getInputStream());
            BufferedReader bs = new BufferedReader(is);
            PrintWriter pw = new PrintWriter(clientConnection.getOutputStream(),true)) {
            String requestNum = "";
            String setNum = "";
            while((requestNum = bs.readLine()) != null && (setNum = bs.readLine()) != null){ //TODO confirm end of streanm vs. waiting for input
                int rn = Integer.parseInt(requestNum);
                int sn = Integer.parseInt(setNum);
                RequestProcessor processRequest = new RequestProcessor(rn, sn, myWatcher,pw);
                processRequest.start();
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makeLocks() {
        //TODO make all of this locks
    }

    public static AmazonDynamoDBClient startDynamoClient() {
        AmazonDynamoDBClientBuilder dynamoDBBuilder = AmazonDynamoDBClient.builder();
        AmazonDynamoDBClient dynamoDB = dynamoDBBuilder.defaultClient();
    }
}
