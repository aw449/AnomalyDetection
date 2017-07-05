

import java.util.LinkedList;
/**
* Represents a user in our social network
*/
public class UserNode{
    private int id;
    private int t;
    private LinkedList<PurchaseEvent> userHistory;
    private LinkedList<PurchaseEvent> socialNetworkHistory;
    private double mean = 0;
    private double std = 0;
    private double sum = 0;
    private double deviation = 0;
    private int multiplier = 3;

    public UserNode(int id,int t){
        this.id = id;
        this.t = t;
        this.userHistory = new LinkedList<>();
        this.socialNetworkHistory = new LinkedList<>();
        this.mean = 0;
        this.std = 0;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getMultiplier(){ return multiplier;}

    public void setMultiplier(int multiplier){
        this.multiplier = multiplier;
    }

    public double getMean(){ return mean; }

    public double getSTD(){ return std; }

    public LinkedList<PurchaseEvent> getUserHistory(){
        return this.userHistory;
    }

    public LinkedList<PurchaseEvent> getSocialNetworkHistory(){
        return this.socialNetworkHistory;
    }

    @Override
    public int hashCode(){
        return id;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == this){
            return true;
        }
        if(!(obj instanceof UserNode)){
            return false;
        }
        UserNode userNode = (UserNode) obj;
        return userNode.getId() == this.getId();
    }
    /**
    * Observer Update function.  Recieves updates based on its subscription list
    */
    public boolean update(PurchaseEvent purchaseEvent){
        if(purchaseEvent.getId() == this.id) {
            userHistory.add(purchaseEvent);
            if (userHistory.size() >= t) {
                userHistory.remove();
            }

            if(socialNetworkHistory.size() >= 2){
                if(purchaseEvent.getAmount() > this.mean + this.multiplier * this.std ){
                    return true;
                }
            }
            else
                return false;

        }else{

            socialNetworkHistory.add(purchaseEvent);
            this.sum += purchaseEvent.getAmount();
            if (socialNetworkHistory.size() >= t) {
                this.sum -= socialNetworkHistory.getFirst().getAmount();
                socialNetworkHistory.remove();
            }
            this.mean = sum/socialNetworkHistory.size();
            StatisiticsHelper stats = new StatisiticsHelper(socialNetworkHistory);
            this.std = stats.getSTD(mean);
        }

        return false;
    }

    /**
    * Called when the user's social network changes and it's social network transactions need to be recomputed 
    */
    public void updateSocialNetwork(LinkedList<PurchaseEvent> newSocialNetworkList, double mean, double std, double sum){
        this.socialNetworkHistory = newSocialNetworkList;
        this.mean = mean;
        this.std = std;
        this.sum = sum;
    }


}
