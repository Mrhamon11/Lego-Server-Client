import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class Watcher {
    private final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final Set<String> activeManufacture = new HashSet<>();
    private Map<String,ReentrantLock> partLocks;
    private Map<String,ReentrantLock> setLocks;

    public Watcher(AmazonDynamoDB dynamo, Map<String,ReentrantLock> partLocks, Map<String,ReentrantLock> setLocks) {
        WatcherProcessor watcherThread = new WatcherProcessor(this, dynamo, partLocks, setLocks);
        watcherThread.start();
        this.partLocks = partLocks;
        this.setLocks = setLocks;
    }


    public synchronized void addToQueue(String setNumber) {
        this.queue.offer(setNumber);
        this.queue.notify();
    }

    public synchronized String popFromQueue() {
        if(this.queue.isEmpty()) {
            try {
                this.queue.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.queue.poll();
    }

    public synchronized void addToActiveManufacture(String setNumber) {
        this.activeManufacture.add(setNumber);
    }

    public synchronized boolean checkActiveManufacture(String setNumber) {
        return this.activeManufacture.contains(setNumber);
    }

    public synchronized void removeFromActiveManufacture(String setNumber) {
        this.activeManufacture.remove(setNumber);
    }
}
