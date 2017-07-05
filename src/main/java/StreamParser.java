/**
 * Created by Anthony Wong on 7/1/2017.
 */
import com.google.gson.*;
import com.google.gson.stream.JsonReader;


import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
* StreamParser manages the parsing of the stream of json objects into java objects with gson
*/
public class StreamParser {
    private static final Logger LOGGER = Logger.getLogger(AnomalyDetection.class.getName());
    private final static Gson gson = buildGson();

    private static Gson buildGson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingStrategy(new JavaCodeConventionStrategy());
        gsonBuilder.registerTypeAdapter(TransactionInterface.class, new TransactionInterfaceDeserializer());
        gsonBuilder.setDateFormat("yyyy-MM-dd hh:mm:ss");
        return gsonBuilder.create();
    }
    /**
    * Used because the json field names do not match with Java camelcase style
    */
    private static class JavaCodeConventionStrategy implements FieldNamingStrategy{

        @Override
        public String translateName(Field field){
            if(field.getName().equals("eventType")){
                return "event_type";
            }
            else if(field.getName().equals("timeStamp")){
                return "timestamp";
            }

            return field.getName();
        }
    }

    /**
    * Custom deserializer to map the different JSON objects that we are expecting
    */
    private static class TransactionInterfaceDeserializer implements JsonDeserializer<TransactionInterface> {

        @Override
        public TransactionInterface deserialize(JsonElement json, Type typeofT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObj = (JsonObject) json;
            JsonElement eventObj = jsonObj.get("event_type");

            if(eventObj != null){
                String eventVal = eventObj.getAsString();

                if(eventVal.equals("purchase"))
                    return context.deserialize(json, PurchaseEvent.class);
                else if(eventVal.equals("unfriend") || eventVal.equals("befriend")){
                    return context.deserialize(json,FriendEvent.class);
                }
                else
                   LOGGER.log(Level.WARNING, "Unknown JSON Format");
                    return null;
            }

            return null;
        }

    }


    public BaseEvent parseStream(String json){

        TransactionInterface transactionInterface = gson.fromJson(json,TransactionInterface.class);

        return (BaseEvent) transactionInterface;

    }

}
