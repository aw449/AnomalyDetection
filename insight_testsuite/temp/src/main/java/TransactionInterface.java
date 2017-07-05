/**
 * Created by Anthony Wong on 7/2/2017.
 */
import java.util.Date;

public interface TransactionInterface {}

class BaseEvent implements TransactionInterface{
    private String eventType;
    private Date timeStamp; //Figure out how to convert string to time

    public String getEventType(){
        return eventType;
    }

    public Date getTimeStamp(){
        return timeStamp;
    }


}


class PurchaseEvent extends BaseEvent{
    private int id;
    private double amount;

    public int getId(){
        return id;
    }

    public double getAmount(){
        return amount;
    }

}

class FriendEvent extends BaseEvent{
    private int id1;
    private int id2;


    public int getId1(){
        return id1;
    }

    public int getId2(){
        return id2;
    }

}

