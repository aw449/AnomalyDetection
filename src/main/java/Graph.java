import java.util.*;

/**
 * Adjacency List Graph representation
 */
public class Graph {
    public HashMap<UserNode,HashSet<UserNode>> graph;
    public HashMap<Integer, UserNode> idList;
    private int graphSize;
    public Graph(){
        this.graph = new HashMap<UserNode,HashSet<UserNode>>();
        this.idList = new HashMap<>();
        this.graphSize = 0;
    }

    /**
     * Adds a node to the graph and creates a new adjacency list
    */
    public void addNode(UserNode userNode){
        if(!hasNode(userNode)) {
            HashSet<UserNode> adjList = new HashSet<UserNode>();
            graph.put(userNode, adjList);
            idList.put(userNode.getId(), userNode);
        }
    }
    /**
     * Gets the UserNode Obj given its id
     * @param id
     * @return UserNode
    */
    public UserNode getNode(Integer id){
        return idList.get(id);
    }
    
    /**
     * Checks the graph if the Node exists
     * @param userNode
     * @return boolean
    */
    public boolean hasNode(UserNode userNode){
        return graph.containsKey(userNode);
    }
    
    /**
     * Adds an undirected edge to the graph
     * @param userNode u, userNode w
    */
    public void addEdge(UserNode u, UserNode w){
        HashSet<UserNode> uList;
        HashSet<UserNode> wList;
        if((uList = graph.get(u)) != null){
            uList.add(w);
        }
        else{
            addNode(u);
            uList = graph.get(u);
            uList.add(w);
        }
        if((wList = graph.get(w)) != null){
            wList.add(u);
        }
        else{
            addNode(w);
            wList = graph.get(w);
            wList.add(u);
        }
    }
    /**
     * Removes an undirected edge to the graph
     * @param userNode u, userNode w
    */
    public void removeEdge(UserNode u, UserNode w){
        HashSet<UserNode> uList;
        HashSet<UserNode> wList;
        if((uList = graph.get(u)) != null){
            uList.remove(w);
        }
        if((wList = graph.get(w)) != null){
            wList.remove(u);
        }
    }
    
    /**
     * Performs a level-aware BFS, stops after depth d and returns all nodes with d steps of the startnode
     * @param startnode, d
     * @return HashSet<UserNode>
    */
    public HashSet<UserNode> BFS(UserNode startNode, int d){
        if(startNode.getId()- 1 >= this.graph.size() && startNode.getId()-1 > graphSize){
            graphSize = startNode.getId();
        }
        else if(graphSize < this.graph.size()){
            graphSize = this.graph.size();
        }

        int level = 0;
        boolean[] visited = new boolean[this.graphSize];
        HashSet<UserNode> subscriberList = new HashSet<>();
        LinkedList<UserNode> queue = new LinkedList<>();
        visited[startNode.getId()-1] = true;
        queue.add(startNode);
        queue.add(null);
        while(level <= d){

            UserNode userNode = queue.poll();
            if(userNode == null ){
                level ++;
                queue.add(null);
                if(queue.peek() == null) break;
                else continue;
            }
            subscriberList.add(userNode);
            Iterator<UserNode> iterator = this.graph.get(userNode).iterator();
            while(iterator.hasNext()){
                UserNode nextNode = iterator.next();
                if(nextNode.getId() - 1 >= graphSize ){
                    graphSize = startNode.getId()-1;
                    visited = Arrays.copyOf(visited,nextNode.getId());
                }

                if(!visited[nextNode.getId()-1]){
                    visited[nextNode.getId()-1] = true;
                    queue.add(nextNode);
                    subscriberList.add(nextNode);
                }
            }
        }
        return subscriberList;
    }
}
