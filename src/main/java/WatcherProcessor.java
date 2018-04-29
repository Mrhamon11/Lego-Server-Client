import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class WatcherProcessor extends Thread{
    private Watcher myWatcher;

    public WatcherProcessor(Watcher myWatcher){
        this.myWatcher = myWatcher;
    }

    @Override
    public void run() {
        while(true){
            int setNumber = this.myWatcher.popFromQueue();
            if(!myWatcher.checkActiveManufacture(setNumber)) {
                int currentQuantity = 0;
                //TODO Obtain set lock
                //TODO query DynamoDB
                //TODO release set lock\
                if(currentQuantity < threshold) {//TODO make config file of constants
                    myWatcher.addToActiveManufacture(setNumber);
                    Manufacturer manufactureSet = new Manufacturer(myWatcher, setNumber);
                    manufactureSet.start();
                }


            }

        }
    }

}
