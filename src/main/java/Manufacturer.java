public class Manufacturer extends Thread{
    private Watcher myWatcher;
    private int setNumber;

    public Manufacturer(Watcher myWatcher, int setNumber) {
        this.myWatcher = myWatcher;
        this.setNumber = setNumber;
    }

    @Override
    public void run() {
        //TODO Query DynamoDB for set spec
        for(Part in parts) {
            //TODO Obtain part lock
            int partQuantity = 0;
            //TODO Query Dynamo for part quantity
            if(partQuantity < (requisite * numberOfSets)) {
                //TODO Put to Dynamo DB partQuantity + 10000, some constant
                partQuantity += 10000;
            }
            //TODO Put to Dynamo current - (requisite * numberOfSets)
            //TODO release part lock
        }
        //TODO obtain set lock
        //TODO Query for current inventory
        //TODO put current + numberOfSets
        //TODO release set lock
        myWatcher.removeFromActiveManufacture(setNumber);

    }
}
