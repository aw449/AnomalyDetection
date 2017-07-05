import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Anthony Wong on 7/3/2017.
 */
public class AnomalyDetection {
    private static final Logger LOGGER = Logger.getLogger(AnomalyDetection.class.getName());
    private static int d;
    private static int t;
    static Graph socialNetwork;
    static StreamData streamData;
    static boolean historyRead = false;

    public AnomalyDetection(String outputFile){
        this.socialNetwork = new Graph();
        this.streamData = new StreamData(outputFile);

    }

    public static void socialNetworkChange(UserNode a, UserNode b){
        HashSet<UserNode> userChanges = new HashSet<>();
        userChanges.addAll(streamData.getSubscriberList(a));
        userChanges.addAll(streamData.getSubscriberList(b));

        Iterator<UserNode> changeIterator = userChanges.iterator();
        while(changeIterator.hasNext()){
            UserNode userNode = changeIterator.next();
            HashSet<UserNode> subscriberList = socialNetwork.BFS(userNode,d);

            if(!subscriberList.equals(streamData.getSubscriberList(userNode))){
                streamData.addSubscriberList(userNode,subscriberList);
                streamData.updateUserHistory(userNode,t);
            }
        }
    }

    public static void ProcessStream(String streamFile) throws FileNotFoundException{
        if(!historyRead){
            LOGGER.log(Level.WARNING,"Please run BuildHistory before running ProcessStream");
        }
        StreamParser streamParser = new StreamParser();

        InputStream inputstream = new FileInputStream(streamFile);
        BufferedInputStream bufferedStream = new BufferedInputStream(inputstream);

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(bufferedStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            while((line = bufferedReader.readLine()) != null) {
                BaseEvent base = streamParser.parseStream(line);
                if(base == null){
                    continue;
                }
                if (base.getEventType().equals("purchase")) {
                    PurchaseEvent purchaseEvent = (PurchaseEvent) base;

                    UserNode node = socialNetwork.getNode(purchaseEvent.getId());
                    if(node != null){
                        streamData.sendStreamData(node,purchaseEvent);
                    }

                } else if (base.getEventType().equals("unfriend") || base.getEventType().equals("befriend")) {
                    FriendEvent friendEvent = (FriendEvent) base;
                    UserNode firstNode = socialNetwork.getNode(friendEvent.getId1());
                    UserNode secondNode = socialNetwork.getNode(friendEvent.getId2());

                    if (base.getEventType().equals("befriend")) {
                        socialNetwork.addEdge(firstNode, secondNode);
                    } else {
                        socialNetwork.removeEdge(firstNode, secondNode);
                    }

                    socialNetworkChange(firstNode, secondNode);

                }
            }

        }catch (UnsupportedEncodingException ex){
            LOGGER.log(Level.WARNING,ex.toString(),ex);

        }catch (IOException ex){
            LOGGER.log(Level.WARNING,ex.toString(),ex);
        }
    }

    public static void BuildHistory(String batchFile) throws FileNotFoundException{
        JsonParser jsonParser = new JsonParser();
        StreamParser streamParser = new StreamParser();

        InputStream inputstream = new FileInputStream(batchFile);
        BufferedInputStream bufferedStream = new BufferedInputStream(inputstream);

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(bufferedStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String header = bufferedReader.readLine();
            JsonElement element = jsonParser.parse(header);

            JsonObject jObj = element.getAsJsonObject();

            if (jObj.has("D")) {
                d = jObj.get("D").getAsInt();
            }
            else{
                //Throw an error
                LOGGER.log(Level.WARNING, "Expected JSON Object with parameter D");
                return;
            }
            if (jObj.has("T")) {
                t = jObj.get("T").getAsInt();
            }
            else{
                //Throw an error
                LOGGER.log(Level.WARNING, "Expected JSON Object with parameter T");
                return;
            }
            String line;
            while((line = bufferedReader.readLine()) != null) {
                BaseEvent base = streamParser.parseStream(line);
                if(base == null){
                    continue;
                }
                if (base.getEventType().equals("purchase")) {
                    PurchaseEvent purchaseEvent = (PurchaseEvent) base;
                    UserNode newNode = socialNetwork.getNode(purchaseEvent.getId());

                    if (newNode == null) {
                        newNode = new UserNode(purchaseEvent.getId(),t);
                        socialNetwork.addNode(newNode);
                        streamData.addTopic(newNode);
                        streamData.attach(newNode, newNode);
                    }
                    streamData.sendStreamData(newNode,purchaseEvent);

                } else if (base.getEventType().equals("unfriend") || base.getEventType().equals("befriend")) {
                    FriendEvent friendEvent = (FriendEvent) base;
                    UserNode firstNode =  socialNetwork.getNode(friendEvent.getId1());
                    UserNode secondNode = socialNetwork.getNode(friendEvent.getId2());

                    if (firstNode == null) {
                        firstNode = new UserNode(friendEvent.getId1(), t);
                        socialNetwork.addNode(firstNode);
                        streamData.addTopic(firstNode);
                        streamData.attach(firstNode, firstNode);
                    }
                    if (secondNode == null) {
                        secondNode = new UserNode(friendEvent.getId2(), t);
                        socialNetwork.addNode(secondNode);
                        streamData.addTopic(secondNode);
                        streamData.attach(secondNode,secondNode);
                    }
                    if (base.getEventType().equals("befriend")) {
                        socialNetwork.addEdge(firstNode, secondNode);
                    } else {
                        socialNetwork.removeEdge(firstNode, secondNode);
                    }
                }
            }

            Iterator<UserNode> topicIterator = streamData.getObserverMap().keySet().iterator();
            while(topicIterator.hasNext()){
                UserNode userNode = topicIterator.next();
                HashSet<UserNode> subscriberList = socialNetwork.BFS(userNode,d);
                streamData.addSubscriberList(userNode,subscriberList);
                streamData.updateUserHistory(userNode,t);
            }

            historyRead = true;

        }catch(UnsupportedEncodingException ex){
            LOGGER.log(Level.WARNING,ex.toString(),ex);

        }catch(IOException ex){
            LOGGER.log(Level.WARNING,ex.toString(),ex);
        }
    }


    public static void main(String args[]) throws FileNotFoundException{

        if(args.length != 3){
            LOGGER.log(Level.WARNING,"This Program requires 3 filepaths, <batch_log.json> <stream_log.json> <flagged_purchases.json> ");
            return;
        }


        AnomalyDetection anomalyDetection = new AnomalyDetection(args[2]);
        BuildHistory(args[0]);
        ProcessStream(args[1]);

    }

}
