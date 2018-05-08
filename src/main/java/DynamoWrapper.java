import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamoWrapper {
    private AmazonDynamoDB dynamo;

    public DynamoWrapper() {
        String username = "AKIAIZ2V3CI757PAQALQ";
        String password = "/mko5CSG+AemM5OrxLAB2w36mA8VCl8oYB+uLjVx";
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(username, password);
        AWSCredentialsProviderChain credentials = new AWSCredentialsProviderChain(new StaticCredentialsProvider(basicAWSCredentials));
        this.dynamo = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-2").withCredentials(credentials).build();
    }

    public synchronized int getPartInventory(String partNumber) {
        Map<String, AttributeValue> partNumMap = new HashMap<>();
        partNumMap.put("part_num", new AttributeValue(partNumber));
        GetItemRequest partRequest = new GetItemRequest("parts", partNumMap);
        return Integer.parseInt(dynamo.getItem(partRequest).getItem().get("Number").getN());
    }

    public synchronized int getSetInventory(String setNumber) {
        Map<String, AttributeValue> testGetMap = new HashMap<>();
        testGetMap.put("set_num", new AttributeValue(setNumber));
        GetItemRequest testGet = new GetItemRequest("sets", testGetMap);
        return Integer.parseInt(dynamo.getItem(testGet).getItem().get("Number").getS());
    }

    public synchronized List<Part> getSetSpec(String setNumber) {
        Map<String, AttributeValue> testGetMap = new HashMap<>();
        testGetMap.put("set_num", new AttributeValue(setNumber));
        GetItemRequest testGet = new GetItemRequest("sets", testGetMap);
        Map<String, AttributeValue> item = dynamo.getItem(testGet).getItem();
        String setSpecString = item.get("JSON").toString();
        String setSpecCommas = setSpecString.replaceAll("\\$", ",");
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Part>>() {}.getType();
        return gson.fromJson(setSpecCommas, listType);
    }

    public synchronized void putPartInventory(String partNumber, int quantity) {
        Map<String,AttributeValue> partKey = new HashMap<>();
        partKey.put("part_num", new AttributeValue(partNumber));
        Map<String,AttributeValueUpdate> updateNumber = new HashMap<>();
        AttributeValue newQuant = new AttributeValue();
        newQuant.setN("" + quantity);
        updateNumber.put("Number", new AttributeValueUpdate(newQuant, AttributeAction.PUT));
        UpdateItemRequest update = new UpdateItemRequest("parts", partKey, updateNumber);
        dynamo.updateItem(update);
    }

    public synchronized void putSetInventory(String setNumber, int newQuantity) {
        Map<String,AttributeValue> setKey = new HashMap<>();
        setKey.put("set_num", new AttributeValue(setNumber));
        Map<String,AttributeValueUpdate> updateNumber = new HashMap<>();
        AttributeValue newQuant = new AttributeValue();
        newQuant.setN("" + newQuantity);
        updateNumber.put("Number", new AttributeValueUpdate(newQuant, AttributeAction.PUT));
        UpdateItemRequest update = new UpdateItemRequest("sets", setKey, updateNumber);
        dynamo.updateItem(update);


    }
}
