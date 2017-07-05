import java.util.LinkedList;

/**
 * Helper function to calculate mean, std
 */
public class StatisiticsHelper {
    LinkedList<PurchaseEvent> data;
    int size;

    public StatisiticsHelper(LinkedList<PurchaseEvent> data){
        this.data = data;
        size = data.size();
    }
    
    /**
    * Finds the mean
    * @param sum
    */
    public double getMean(double sum)
    {
        return sum/size;
    }
    
    /**
    * Finds the sum
    */
    public double getSum()
    {
        double sum = 0.0;
        for(PurchaseEvent pEvent : data){
            sum += pEvent.getAmount();
        }
        return sum;
    }

    /**
    * Finds the standard deviation
    */
    public double getSTD(double mean){
        double deviation = 0;
        for(PurchaseEvent pEvent: data){
            deviation += Math.pow((pEvent.getAmount() - mean),2);
        }
        return Math.sqrt(deviation/size);
    }
}
