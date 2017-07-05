import java.util.LinkedList;

/**
 * Created by Anthony Wong on 7/5/2017.
 */
public class StatisiticsHelper {
    LinkedList<PurchaseEvent> data;
    int size;

    public StatisiticsHelper(LinkedList<PurchaseEvent> data){
        this.data = data;
        size = data.size();
    }

    public double getMean(double sum)
    {
        return sum/size;
    }

    public double getSum()
    {
        double sum = 0.0;
        for(PurchaseEvent pEvent : data){
            sum += pEvent.getAmount();
        }
        return sum;
    }


    public double getSTD(double mean){
        double deviation = 0;
        for(PurchaseEvent pEvent: data){
            deviation += Math.pow((pEvent.getAmount() - mean),2);
        }
        return Math.sqrt(deviation/size);
    }
}
