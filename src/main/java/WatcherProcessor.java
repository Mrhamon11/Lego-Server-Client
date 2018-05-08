import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class WatcherProcessor extends Thread{
    private Watcher myWatcher;
    private DynamoWrapper dynamo;
    private Map<String,ReentrantLock> partLocks;
    private Map<String,ReentrantLock> setLocks;
    private static final int inventoryThreshold = 50;

    public WatcherProcessor(Watcher myWatcher, DynamoWrapper dynamo, Map<String,ReentrantLock> partLocks, Map<String,ReentrantLock> setLocks){
        this.myWatcher = myWatcher;
        this.dynamo = dynamo;
        this.partLocks = partLocks;
        this.setLocks = setLocks;
    }

    @Override
    public void run() {
        while(true){
            String setNumber = this.myWatcher.popFromQueue();
            if(!myWatcher.checkActiveManufacture(setNumber)) {
                setLocks.get(setNumber).lock();
                int currentQuantity = dynamo.getSetInventory(setNumber);
                setLocks.get(setNumber).unlock();
                if(currentQuantity < inventoryThreshold) {
                    myWatcher.addToActiveManufacture(setNumber);
                    Manufacturer manufactureSet = new Manufacturer(myWatcher, setNumber, dynamo, partLocks, setLocks);
                    manufactureSet.start();
                }


            }

        }
    }

}
