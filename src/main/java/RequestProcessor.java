import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

import java.io.IOException;
import java.io.PrintWriter;

public class RequestProcessor extends Thread {
    private int requestNumber;
    private int setNumber;
    private Watcher myWatcher;
    private PrintWriter clientWriter;
    private AmazonDynamoDB dynamo;

    public RequestProcessor(int requestNumber, int setNumber, Watcher myWatcher, PrintWriter pw, AmazonDynamoDB dynamo) {
        this.requestNumber = requestNumber;
        this.setNumber = setNumber;
        this.myWatcher = myWatcher;
        this.clientWriter = pw;
        this.dynamo = dynamo;
    }

    @Override
    public void run() {
        int currentInventory = 0;
        while (currentInventory <= 0) {
            //TODO obtain set lock
            //TODO query Dynamo for current inventory and store in currentInventory
            if(currentInventory <= 0) {
                try {
                    //TODO release set lock
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //TODO obtain set lock
        //TODO put to Dynamo current - 1
        //TODO release set lock
        myWatcher.addToQueue(setNumber);
        writeToClient();
    }

    public synchronized void writeToClient() {
        String clientResponse = "Request " + requestNumber + " has been fulfilled. Your order of set " + setNumber + " has shipped.";
        clientWriter.println(clientResponse);
    }
}
