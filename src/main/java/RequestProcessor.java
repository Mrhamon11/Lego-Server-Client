import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class RequestProcessor extends Thread {
    private int requestNumber;
    private String setNumber;
    private Watcher myWatcher;
    private PrintWriter clientWriter;
    private AmazonDynamoDB dynamo;
    private Map<String, ReentrantLock> setLocks;

    public RequestProcessor(int requestNumber, String setNumber, Watcher myWatcher, PrintWriter pw, AmazonDynamoDB dynamo, Map<String,ReentrantLock> setLocks) {
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
            //TODO query Dynamo for current inventory and store in currentInventory
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
        //TODO put to Dynamo current - 1
        setLocks.get(setNumber).unlock();
        myWatcher.addToQueue(setNumber);
        writeToClient();
    }

    public synchronized void writeToClient() {
        String clientResponse = "Request " + requestNumber + " has been fulfilled. Your order of set " + setNumber + " has shipped.";
        clientWriter.println(clientResponse);
    }
}
