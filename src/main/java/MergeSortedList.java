
import java.util.*;


public class MergeSortedList {
    /*
    * Merges k sorted arrays into one.
    * @params userPurchases, t
    * @return Returns a sorted array of the last t elements.
    */
    public LinkedList<PurchaseEvent> MergeLists(LinkedList<LinkedList<PurchaseEvent>> userPurchases, int t){
        int k = userPurchases.size();
        if(k == 0){
            return null;
        }
        int count = 0;

        ListIterator<LinkedList<PurchaseEvent>> purchaseIterator = userPurchases.listIterator();
        ArrayList<Iterator<PurchaseEvent>> userIteratorList = new ArrayList<Iterator<PurchaseEvent>>(k);
        Comparator<HeapNode> comparator = new PurchaseEventComparator();
        PriorityQueue<HeapNode> queue = new PriorityQueue<>(k, comparator);

        LinkedList<PurchaseEvent> result = new LinkedList<PurchaseEvent>();

        while(purchaseIterator.hasNext()){
            userIteratorList.add(purchaseIterator.next().descendingIterator());
        }
        //Initialize the priority queue
        for (int i = 0; i < userIteratorList.size(); i++) {
            if(userIteratorList.get(i).hasNext()){
                HeapNode heapNode = new HeapNode(userIteratorList.get(i).next(), i);
                queue.add(heapNode);
            }
        }

        while(count < t){
            HeapNode heapNode;
            if((heapNode = queue.poll()) == null){
                break;
            }

            result.add(0,heapNode.getPurchaseEvent());

            Iterator<PurchaseEvent> userIterator = userIteratorList.get(heapNode.getListPosition());
            if(userIterator.hasNext()){
                HeapNode newNode = new HeapNode(userIterator.next(),heapNode.getListPosition());
                queue.add(newNode);
            }
            count ++;
        }
        return result;
    }

    /*
    * Helper class used to associate the origin of a purchaseEvent to its iterator
    */
    private class HeapNode{
        private PurchaseEvent purchaseEvent;
        private int listPosition;

        HeapNode(PurchaseEvent purchaseEvent, int listPosition){
            this.purchaseEvent = purchaseEvent;
            this.listPosition = listPosition;
        }

        public PurchaseEvent getPurchaseEvent() {
            return purchaseEvent;
        }

        public int getListPosition() {
            return listPosition;
        }
    }
    /*
    * Custom comparator class used to compare purchaseEvents.  Sorts in descending order
    */
    public class PurchaseEventComparator implements Comparator<HeapNode>
    {
        @Override
        public int compare(HeapNode a,HeapNode b){
            if(a.getPurchaseEvent().getTimeStamp().getTime() < b.getPurchaseEvent().getTimeStamp().getTime()){
                return 1;
            }
            else if(a.getPurchaseEvent().getTimeStamp().getTime() > b.getPurchaseEvent().getTimeStamp().getTime()){
                return -1;
            }

            return 0;

        }
    }
}
