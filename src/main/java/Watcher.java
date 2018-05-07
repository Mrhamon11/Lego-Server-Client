import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class Watcher {
    private final LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
    private final Set<Integer> activeManufacture = new HashSet<>();

    public Watcher(AmazonDynamoDB dynamo) {
        WatcherProcessor watcherThread = new WatcherProcessor(this, dynamo);
        watcherThread.start();
    }


    public synchronized void addToQueue(int setNumber) {
        this.queue.offer(setNumber);
        this.queue.notify();
    }

    public synchronized int popFromQueue() {
        if(this.queue.isEmpty()) {
            try {
                this.queue.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.queue.poll();
    }

    public synchronized void addToActiveManufacture(int setNumber) {
        this.activeManufacture.add(setNumber);
    }

    public synchronized boolean checkActiveManufacture(int setNumber) {
        return this.activeManufacture.contains(setNumber);
    }

    public synchronized void removeFromActiveManufacture(int setNumber) {
        this.activeManufacture.remove(setNumber);
    }
}
