import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class WatcherProcessor extends Thread{
    private Watcher myWatcher;
    private AmazonDynamoDB dynamo;
    private Map<String,ReentrantLock> partLocks;
    private Map<String,ReentrantLock> setLocks;

    public WatcherProcessor(Watcher myWatcher, AmazonDynamoDB dynamo, Map<String,ReentrantLock> partLocks, Map<String,ReentrantLock> setLocks){
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
                int currentQuantity = 0;
                setLocks.get(setNumber).lock();
                //TODO query DynamoDB
                setLocks.get(setNumber).unlock();
                if(currentQuantity < threshold) {//TODO make config file of constants
                    myWatcher.addToActiveManufacture(setNumber);
                    Manufacturer manufactureSet = new Manufacturer(myWatcher, setNumber, dynamo, partLocks, setLocks);
                    manufactureSet.start();
                }


            }

        }
    }

}
