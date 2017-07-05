import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
* Handles the usernode topic list and subscribers.
*/
public class StreamData {
    private static final Logger LOGGER = Logger.getLogger(AnomalyDetection.class.getName());
    private PurchaseEvent purchaseEvent;
    private HashMap<UserNode, HashSet<UserNode>> observerMap = new HashMap<>();
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public String outputFile;
    Gson gson;
    public StreamData(String outputFile){
        this.outputFile = outputFile;
        gson = new Gson();
    }
    
    /**
    * Adds a new subscriber to a list of topics
    */
    public void attach(List<UserNode> idList, UserNode observer){
        for(UserNode id: idList){
            observerMap.get(id).add(observer);
        }
    }
    /**
    * Adds a new subscriber to a single topic
    */
    public void attach(UserNode id, UserNode observer){
        observerMap.get(id).add(observer);

    }
    /**
    * Removes a subscriber from a list of topics
    */
    public void detach(List<UserNode> idList, UserNode observer){
        for(UserNode id: idList){
            observerMap.get(id).remove(observer);
        }
    }
    /**
    * Removes a subscriber to a single topic
    */
    public void detach(UserNode id, UserNode observer){
        observerMap.get(id).remove(observer);
    }
    /**
    * Pushes the new event to all subscribers of the specific id.
    */
    public void notifyObservers(UserNode id){
        Iterator<UserNode> observerIterator = observerMap.get(id).iterator();
        while(observerIterator.hasNext()){
            UserNode userNode = observerIterator.next();
            if(userNode.update(this.purchaseEvent)){
                String json = parseToJson(this.purchaseEvent, userNode.getMean(),userNode.getSTD());
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true));
                    writer.write(json);
                    writer.newLine();
                    writer.close();
                } catch(IOException ex){
                    LOGGER.log(Level.WARNING,ex.toString(),ex);
                }
            }
        }
    }
    
    /**
    * Parses the purchaseEvent java object back to Json and includes the mean and std
    */
    public String parseToJson(PurchaseEvent purchaseEvent, double mean, double std){
       String result = String.format("{\"event_type\":\"%s\", \"timestamp\":\"%s\", \"id\": \"%s\", \"amount\": \"%.2f\", \"mean\": \"%.2f\", \"sd\": \"%.2f\"}",purchaseEvent.getEventType(),df.format(purchaseEvent.getTimeStamp()),purchaseEvent.getId(),purchaseEvent.getAmount(),mean,std);
       return result;
    }
    /**
    * Returns true if the node is already a topic
    */
    public boolean hasTopic(UserNode id){
        return observerMap.containsKey(id);
    }
    
    /**
    * Makes a new topic of id node if it doesn't already exist
    */
    public void addTopic(UserNode id){
        if(!hasTopic(id)) {
            HashSet<UserNode> observerList = new HashSet<>();
            observerMap.put(id, observerList);
        }
    }

    public HashMap<UserNode, HashSet<UserNode>> getObserverMap(){
        return this.observerMap;
    }
    /**
    * Updates the subscriberlist of a given topic id
    */
    public void addSubscriberList(UserNode id, HashSet<UserNode> subscriberList){
        observerMap.put(id,subscriberList);
    }
    
    public HashSet<UserNode> getSubscriberList(UserNode id){
        return observerMap.get(id);
    }
    
    /**
    * Sends the new purchase event to the subscriber list of a UserNode topic
    */
    public void sendStreamData(UserNode id, PurchaseEvent purchaseEvent){
        this.purchaseEvent = purchaseEvent;
        this.notifyObservers(id);
    }
    
    /**
    * Function that pools all relevant nodes of a user's socialnetwork and computes the mean and std.
    */
    public void updateUserHistory(UserNode node, int t){

        HashSet<UserNode> subscriberList = getSubscriberList(node);
        if(subscriberList.size() == 1){
            return;
        }
        Iterator<UserNode> iterator = subscriberList.iterator();
        LinkedList<LinkedList<PurchaseEvent>> socialNetworkLists = new LinkedList<LinkedList<PurchaseEvent>>();

        while (iterator.hasNext()){
            UserNode subNode = iterator.next();

            if(!subNode.equals(node) && subNode.getUserHistory().size() != 0){

                socialNetworkLists.add(subNode.getUserHistory());
            }
        }
        if(socialNetworkLists.size() == 0){
            //No Data
            return;
        }
        MergeSortedList mergedSortedList = new MergeSortedList();
        LinkedList<PurchaseEvent> newSocialHistory = mergedSortedList.MergeLists(socialNetworkLists, t);
        StatisiticsHelper stats = new StatisiticsHelper(newSocialHistory);
        double sum = stats.getSum();
        double mean = stats.getMean(sum);
        double std = stats.getSTD(mean);
        node.updateSocialNetwork(newSocialHistory,mean,std,sum);
    }

}
