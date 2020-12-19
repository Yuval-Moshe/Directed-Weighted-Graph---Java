package api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

     /**
      * This class implements the directed_weighted_graph interface which is a weighted directed graph with the following class variables:
      *  - HashMap<Integer, node_info> _nodes  - Represents the nodes in the graph as a hashmap which maps each node to his unique key.
      *  - HashMap<Integer, HashMap<Integer,edge_data>> _edges_fromNode  - maps for each nodes in the graph an Hashmap which store all the edges
      *    in the graph which are directed **from** the node (meaning: curr_node -> other_node), also holds the edge itself  in the
      *    the following way <src_node_key , <dest_node_key , edge_from_src_to_dest >>. (Example: if there is an edge from node with key 3 to
      *    node with key 5 with , than the HashMap will map the following: <3, <5, edge(3->5)>> ).
      *  - HashMap<Integer, HashMap<Integer,edge_data>> _edges_toNode  - maps for each nodes in the graph an Hashmap which store all the edges
      *    in the graph which are directed **to** the node (meaning: other_node -> curr_node), also holds the edge itself in the
      *    the following way <dest_node_key , <src_node_key , edge_from_src_to_dest >>.(Example: if there is an edge from node with key 3 to
      *    node with key 5 , than the HashMap will map the following: <5, <3, edge(3->5)>> ).
      *    ** The following double structured Hashmap (_edges_fromNode & _edges_toNode) was chosen to support O(k) removal of nodes from this graph, when k
      *    is the degree of this node.
      *  - int _edgeSize = number of edges in the graph.
      *  - int _mc = number of actions preformed on the the graph.
      * **/

public class DWGraph_DS implements directed_weighted_graph {

    private HashMap<Integer, node_data> _nodes;
    private HashMap<Integer, HashMap<Integer,edge_data>> _edges_fromNode;
    private HashMap<Integer, HashMap<Integer,edge_data>> _edges_toNode;
    private int _edgeSize;
    private int _mc;

    /** Constructor **/
    public DWGraph_DS(){
        _nodes = new HashMap<Integer, node_data>();
        _edges_fromNode = new HashMap<Integer, HashMap<Integer, edge_data>>();
        _edges_toNode = new HashMap<Integer, HashMap<Integer, edge_data>>();
        _edgeSize = 0;
        _mc = 0;
   }

   /**
    * Copy Constructor
    * @param other graph
    * **/
   public DWGraph_DS(directed_weighted_graph other){
       _nodes = new HashMap<Integer, node_data>();
       _edges_fromNode = new HashMap<Integer, HashMap<Integer, edge_data>>();
       _edges_toNode = new HashMap<Integer, HashMap<Integer, edge_data>>();
       for (node_data node : other.getV()) {
           _nodes.put(node.getKey(), node);
           HashMap<Integer, edge_data> curr_edges_fromTo = new HashMap<>();
           HashMap<Integer, edge_data> curr_edges_toFrom = new HashMap<>();
           _edges_fromNode.put(node.getKey(),curr_edges_fromTo);
           _edges_toNode.put(node.getKey(), curr_edges_toFrom);
       }
       for (node_data node : other.getV()){
            for (edge_data edge : other.getE(node.getKey())) {
                _edges_fromNode.get(edge.getSrc()).put(edge.getDest(),edge);
                _edges_toNode.get(edge.getDest()).put(edge.getSrc(), edge);
            }
        }
        _edgeSize = other.edgeSize();
        _mc = 0;
   }

    /**
     * Returns the node assigned to the provided key by returning the mapped node to the key in _nodes
     * @param key
     * @return The node assigned to that key, null if the node doesn't exists in this graph.
     * **/
    @Override
    public node_data getNode(int key) {
        return _nodes.get(key);
    }

    /**
     * Returns the edge_date which represents the edge from src to dest, by returning the mapped value in _edges_fromNode to the provided src and dest.
     * @param src - the key of the source node where the edge is directed from.
     * @param dest - the key of the destination node where the edge is directed to.
     * @return the edge from src to node, null if doesn't exists.
     * **/
    @Override
    public edge_data getEdge(int src, int dest) {
        if (_edges_fromNode.containsKey(src)) {
            return _edges_fromNode.get(src).get(dest);
        }
        return null;
    }

    /**
     * Adds the provided node_data param to this graph by mapping his key to the node in _nodes, and initialzing him with an empty HashMap in both
     * _edges_fromNode & edges_toNode.
     * @param n - the node_data to add to this graph.
     * **/
    @Override
    public void addNode(node_data n) {
        _nodes.put(n.getKey(),n);
        HashMap<Integer, edge_data> curr_edges_fromTo = new HashMap<>();
        HashMap<Integer, edge_data> curr_edges_toFrom = new HashMap<>();
        _edges_fromNode.put(n.getKey(), curr_edges_fromTo);
        _edges_toNode.put(n.getKey(),curr_edges_toFrom);
        _mc++;
    }

    /**
     * Connects 2 nodes in the graph (if both exists and are not the same) by an edge from src to dest with the positive w (if w is positive),
     * by first creating an EdgeData variable from src to dest with weight w and then mapping src<dest<edge>> in edges_fromNode and dest<src<edge>> in edges_toNode.
     * @param src - the key of the source node.
     * @param dest - the key of the destination node.
     * @param w - the weight of the edge.
     * **/
    @Override
    public void connect(int src, int dest, double w) {
        if (src != dest && _nodes.containsKey(src) && _nodes.containsKey(dest) && w>0){
            edge_data edge =  new EdgeData(src, dest, w);
            _edges_fromNode.get(src).put(dest, edge);
            _edges_toNode.get(dest).put(src, edge);
            _edgeSize++;
            _mc++;
        }
    }

    /**
     * Returns a collection of all the nodes in the graph by returning the _nodes Hashmap values.
     * @return a collection of all the nodes in the graph.
     * **/
    @Override
    public Collection<node_data> getV() {
        return _nodes.values();
    }

    /**
     * Returns a collection of all the edges in the graph which are directed from the node with the provided node_id (if exists), by returning the values
     * of the internal Hashmap mapped to this node if.
     * @param node_id - the key of the node.
     * @return collection of all the edges from node_id, an empty ArrayList<edge_data> if the node is not in the grpah.
     * **/
    @Override
    public Collection<edge_data> getE(int node_id) {
        if (_nodes.containsKey(node_id)) {
            return _edges_fromNode.get(node_id).values();
        }
        return new ArrayList<edge_data>(); //empty collection
    }

    /**
     * Removes the node which is assigned to the following key (if exists) by doing 3 steps:
     * First, removing all the edges which are directed from him to other nodes, meaning for each edge which is
     * directed from this node (node -> other node), go to the edges_toNode to the other node's key, and removing the mapping
     * of this current node.
     * Second, do the exact opposite, meaning remove each edge which is directed to this node (other node -> node), by going to
     * the edges_fromNode to the other node's key, and removing the mapping of this current node.
     * Third, removes the node from edges_fromNode and from nodes
     *  @param key - the key of the node.
     *  @return the node that has been removed
     * **/
    @Override
    public node_data removeNode(int key) {
        node_data curr = _nodes.get(key);
        if(curr!=null) {
            for (int ni_fromTo : _edges_fromNode.get(key).keySet()) {
                _edges_toNode.get(ni_fromTo).remove(key);
                _edgeSize--;
                _mc++;
            }
            for (int ni_toFrom : _edges_toNode.get(key).keySet()) {
                _edges_fromNode.get(ni_toFrom).remove(key);
                _edgeSize--;
                _mc++;
            }
            _edges_fromNode.remove(key);
            _edges_toNode.remove(key);
            _nodes.remove(key);
        }
        return curr;

    }

    /**
     * Removes the edge which is defined from src to dest (if exists) by going to edges_toNode to dest node, and removing
     * from its mapping the src node key. Then it will do the same on the opposite - means will go to edges_fromNode to src node,
     * and removing from its mapping the dest node key. Also, decreases edgeSize in 1.
     * @param src - edge source node
     * @param dest - edge destination node
     * @return the edge that has been removed
     */
    @Override
    public edge_data removeEdge(int src, int dest) {
        edge_data edge = getEdge(src, dest);
        if(edge!=null) {
            _edges_fromNode.get(src).remove(dest);
            _edges_toNode.get(dest).remove(src);
            _mc++;
            _edgeSize--;
        }
        return edge;
    }

    /**
     * toString method
     * @return a String which represent current graph's data. It contains all the connections that appear in the graph.
     */
    public String toString (){
        String s="";
        for(node_data node : _nodes.values()){
            for(edge_data ni : getE(node.getKey())){
                s+=ni.getSrc() + " and "+ ni.getDest() + " are connected by: "+ni.getWeight()+"\n";
            }
        }
        return s;
    }

    /**
     * returns number of nodes in the graph by returning the num of mapped keys in _nodes Hashmap
     * @return number of nodes
     */
    @Override
    public int nodeSize() {
        return _nodes.size();
    }

    /**
     * returns number of edges in the graph by returning _edgeSize variable
     * @return number of edges
     */
    @Override
    public int edgeSize() {
        return _edgeSize;
    }

    /**
     * returns the number of actions preformed on the graph by returning the _mc variable
     * @return number ofactions
     */
    @Override
    public int getMC() {
        return _mc;
    }
    public boolean equals(Object o) {
        directed_weighted_graph graph = (directed_weighted_graph) o;
        if (this._edgeSize != graph.edgeSize() || this.nodeSize() != graph.nodeSize()) {
            return false;
        }

        for (Integer key : this._nodes.keySet()) {
            for (Integer key2 : this._edges_fromNode.get(key).keySet()){
                if (graph.getEdge(key,key2)==null) {
                    return false;
                }
            }
        }
        return true;
    }
}
