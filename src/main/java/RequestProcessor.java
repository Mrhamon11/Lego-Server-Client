import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class RequestProcessor extends Thread {
    private int requestNumber;
    private String setNumber;
    private Watcher myWatcher;
    private PrintWriter clientWriter;
    private DynamoWrapper dynamo;
    private Map<String, ReentrantLock> setLocks;

    public RequestProcessor(int requestNumber, String setNumber, Watcher myWatcher, PrintWriter pw, DynamoWrapper dynamo, Map<String,ReentrantLock> setLocks) {
        this.requestNumber = requestNumber;
        this.setNumber = setNumber;
        this.myWatcher = myWatcher;
        this.clientWriter = pw;
        this.dynamo = dynamo;
        this.setLocks = setLocks;
    }

    @Override
    public void run() {
        int currentInventory = 0;
        while (currentInventory <= 0) {
            setLocks.get(setNumber).lock();
            currentInventory = dynamo.getSetInventory(setNumber);
            if(currentInventory <= 0) {
                try {
                    setLocks.get(setNumber).unlock();
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        setLocks.get(setNumber).lock();
        dynamo.putSetInventory(setNumber,currentInventory - 1);
        setLocks.get(setNumber).unlock();
        myWatcher.addToQueue(setNumber);
        writeToClient();
    }

    public synchronized void writeToClient() {
        System.out.println("writing to client");
        String clientResponse = "Request " + requestNumber + " has been fulfilled. Your order of set " + setNumber + " has shipped.";
        clientWriter.println(clientResponse);
    }

}
