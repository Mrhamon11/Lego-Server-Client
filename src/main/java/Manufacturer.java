import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Manufacturer extends Thread{
    private Watcher myWatcher;
    private String setNumber;
    private AmazonDynamoDB dynamo;
    private Map<String,ReentrantLock> partLocks;
    private Map<String,ReentrantLock> setLocks;

    public Manufacturer(Watcher myWatcher, String setNumber, AmazonDynamoDB dynamo, Map<String,ReentrantLock> partLocks, Map<String,ReentrantLock> setLocks) {
        this.myWatcher = myWatcher;
        this.setNumber = setNumber;
        this.dynamo = dynamo;
        this.partLocks = partLocks;
        this.setLocks = setLocks;
    }

    @Override
    public void run() {
        //TODO Query DynamoDB for set spec
        for(Part in parts) {
            //TODO obtain part lock
            int partQuantity = 0;
            //TODO Query Dynamo for part quantity
            if(partQuantity < (requisite * numberOfSets)) {
                //TODO Put to Dynamo DB partQuantity + 10000, some constant
                partQuantity += 10000;
            }
            //TODO Put to Dynamo current - (requisite * numberOfSets)
            //TODO release part lock
        }
        setLocks.get(setNumber).lock();
        //TODO Query for current inventory
        //TODO put current + numberOfSets
        setLocks.get(setNumber).unlock();
        myWatcher.removeFromActiveManufacture(setNumber);

    }

    private int getPartQuantity {

    }

    private int getSetQuantity {

    }

    private void putPartQuantity {

    }

    private void putSetQuantity {

    }

}
