import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    //TODO lots of locks, static (sets, parts)


    public static void main(String[] args) {
        Map<String,ReentrantLock> setLocks = new HashMap<>();
        Map<String,ReentrantLock> partLocks = new HashMap<>();
        makeLocks(setLocks, partLocks);
        AmazonDynamoDB dynamoDB = startDynamoClient();
        Watcher myWatcher = new Watcher(dynamoDB);
        ServerSocket server = null;
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
                RequestProcessor processRequest = new RequestProcessor(rn, setNum, myWatcher, pw, dynamoDB, setLocks);
                processRequest.start();
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makeLocks(Map<String,ReentrantLock> setLocks, Map<String,ReentrantLock> partLocks) {
        try(FileReader frParts = new FileReader("parts.txt");
            FileReader frSets = new FileReader("sets.txt");
            BufferedReader partReader = new BufferedReader(frParts);
            BufferedReader setReader = new BufferedReader(frSets)
            ) {
            String partNum;
            String setNum;
            while((partNum = partReader.readLine()) != null) {
                partLocks.put(partNum, new ReentrantLock());
            }
            while((setNum = setReader.readLine()) != null) {
                setLocks.put(setNum, new ReentrantLock());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AmazonDynamoDB startDynamoClient() {
        String username = "AKIAIZ2V3CI757PAQALQ";
        String password = "/mko5CSG+AemM5OrxLAB2w36mA8VCl8oYB+uLjVx";
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(username, password);
        AWSCredentialsProviderChain credentials = new AWSCredentialsProviderChain(new StaticCredentialsProvider(basicAWSCredentials));
        return AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").withCredentials(credentials).build();
    }
}
