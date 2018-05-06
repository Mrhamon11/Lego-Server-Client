import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
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
                RequestProcessor processRequest = new RequestProcessor(rn, sn, myWatcher, pw);
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

    public static AmazonDynamoDB startDynamoClient() {
        String username = "AKIAIZ2V3CI757PAQALQ";
        String password = "/mko5CSG+AemM5OrxLAB2w36mA8VCl8oYB+uLjVx";
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(username, password);
        AWSCredentialsProviderChain credentials = new AWSCredentialsProviderChain(new StaticCredentialsProvider(basicAWSCredentials));
        return AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").withCredentials(credentials).build();
    }
}
