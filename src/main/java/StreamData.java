import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public void attach(List<UserNode> idList, UserNode observer){
        for(UserNode id: idList){
            observerMap.get(id).add(observer);
        }
    }

    public void attach(UserNode id, UserNode observer){
        observerMap.get(id).add(observer);

    }

    public void detach(List<UserNode> idList, UserNode observer){
        for(UserNode id: idList){
            observerMap.get(id).remove(observer);
        }
    }

    public void detach(UserNode id, UserNode observer){
        observerMap.get(id).remove(observer);
    }

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

    public String parseToJson(PurchaseEvent purchaseEvent, double mean, double std){
       String result = String.format("{\"event_type\":\"%s\", \"timestamp\":\"%s\", \"id\": \"%s\", \"amount\": \"%.2f\", \"mean\": \"%.2f\", \"sd\": \"%.2f\"}",purchaseEvent.getEventType(),df.format(purchaseEvent.getTimeStamp()),purchaseEvent.getId(),purchaseEvent.getAmount(),mean,std);
       return result;
    }

    public boolean hasTopic(UserNode id){
        return observerMap.containsKey(id);
    }

    public void addTopic(UserNode id){
        if(!hasTopic(id)) {
            HashSet<UserNode> observerList = new HashSet<>();
            observerMap.put(id, observerList);
        }
    }

    public HashMap<UserNode, HashSet<UserNode>> getObserverMap(){
        return this.observerMap;
    }

    public void addSubscriberList(UserNode id, HashSet<UserNode> subscriberList){
        observerMap.put(id,subscriberList);
    }

    public HashSet<UserNode> getSubscriberList(UserNode id){
        return observerMap.get(id);
    }

    public void sendStreamData(UserNode id, PurchaseEvent purchaseEvent){
        this.purchaseEvent = purchaseEvent;
        this.notifyObservers(id);
    }

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
