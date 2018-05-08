import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Manufacturer extends Thread{
    private Watcher myWatcher;
    private String setNumber;
    private DynamoWrapper dynamo;
    private Map<String,ReentrantLock> partLocks;
    private Map<String,ReentrantLock> setLocks;
    private static final int numberOfSetsToAdd = 50;
    private static final int partsToAdd = 10000;

    public Manufacturer(Watcher myWatcher, String setNumber, DynamoWrapper dynamo, Map<String,ReentrantLock> partLocks, Map<String,ReentrantLock> setLocks) {
        this.myWatcher = myWatcher;
        this.setNumber = setNumber;
        this.dynamo = dynamo;
        this.partLocks = partLocks;
        this.setLocks = setLocks;
    }

    @Override
    public void run() {
        List<Part> partsList = dynamo.getSetSpec(setNumber);
        for(Part part : partsList) {
            partLocks.get(part.getPartNum()).lock();
            int partsStock = dynamo.getPartInventory(part.getPartNum());
            if(partsStock < (part.getQuantity() * numberOfSetsToAdd)) {
                partsStock += partsToAdd;
                dynamo.putPartInventory(part.getPartNum(),partsStock);
            }
            partsStock -= part.getQuantity() * numberOfSetsToAdd;
            dynamo.putPartInventory(part.getPartNum(),partsStock);
            partLocks.get(part.getPartNum()).unlock();
        }
        setLocks.get(setNumber).lock();
        int currentSets = dynamo.getSetInventory(setNumber);
        dynamo.putSetInventory(setNumber,currentSets + numberOfSetsToAdd);
        setLocks.get(setNumber).unlock();
        myWatcher.removeFromActiveManufacture(setNumber);

    }



}
