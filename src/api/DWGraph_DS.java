package api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


public class DWGraph_DS implements directed_weighted_graph {

    private HashMap<Integer, node_data> _nodes;
    private HashMap<Integer, HashMap<Integer,edge_data>> _edges_fromNode;
    private HashMap<Integer, HashMap<Integer,edge_data>> _edges_toNode;
    private int _edgeSize;
    private int _mc;

    public DWGraph_DS(){
        _nodes = new HashMap<Integer, node_data>();
        _edges_fromNode = new HashMap<Integer, HashMap<Integer, edge_data>>();
        _edges_toNode = new HashMap<Integer, HashMap<Integer, edge_data>>();
        _edgeSize = 0;
        _mc = 0;
   }

   public DWGraph_DS(directed_weighted_graph other){
        for (node_data node : other.getV()){
            _nodes.put(node.getKey(), node);
            HashMap<Integer, edge_data> curr_edges_fromTo = new HashMap<>();
            for (edge_data edge : other.getE(node.getKey())) {
                curr_edges_fromTo.put(edge.getDest(),edge);
                if(_edges_toNode.get(edge.getDest())==null){
                    HashMap<Integer, edge_data> dest_edges_toFrom = new HashMap<>();
                    dest_edges_toFrom.put(node.getKey(),edge);
                    _edges_toNode.put(edge.getDest(), dest_edges_toFrom);
                }
                else{
                    _edges_toNode.get(edge.getDest()).put(node.getKey(),edge);
                }
            }
            _edges_fromNode.put(node.getKey(), curr_edges_fromTo);
        }
        _edgeSize = other.edgeSize();
        _mc = 0;
   }

    @Override
    public node_data getNode(int key) {
        return _nodes.get(key);
    }

    @Override
    public edge_data getEdge(int src, int dest) {
        if (_edges_fromNode.containsKey(src)) {
            return _edges_fromNode.get(src).get(dest);
        }
        return null;
    }

    @Override
    public void addNode(node_data n) {
        _nodes.put(n.getKey(),n);
        HashMap<Integer, edge_data> curr_edges_fromTo = new HashMap<>();
        HashMap<Integer, edge_data> curr_edges_toFrom = new HashMap<>();
        _edges_fromNode.put(n.getKey(), curr_edges_fromTo);
        _edges_toNode.put(n.getKey(),curr_edges_toFrom);
        _mc++;
    }

    @Override
    public void connect(int src, int dest, double w) { // what about the same node?
        if (src != dest && _nodes.containsKey(src) && _nodes.containsKey(dest) && w>0){
            edge_data edge =  new EdgeData(src, dest, w);
            _edges_fromNode.get(src).put(dest, edge);
            _edges_toNode.get(dest).put(src, edge);
            _edgeSize++;
            _mc++;
        }
    }

    @Override
    public Collection<node_data> getV() {
        return _nodes.values();
    }

    @Override
    public Collection<edge_data> getE(int node_id) {
        if (_nodes.containsKey(node_id)) {
            return _edges_fromNode.get(node_id).values();
        }
        return new ArrayList<edge_data>(); //empty collection
    }

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
    public String toString (){
        String s="";
        for(node_data node : _nodes.values()){
            for(edge_data ni : getE(node.getKey())){
                s+=ni.getSrc() + " and "+ ni.getDest() + " are connected by: "+ni.getWeight()+"\n";
            }
        }
        return s;
    }


    @Override
    public int nodeSize() {
        return _nodes.size();
    }

    @Override
    public int edgeSize() {
        return _edgeSize;
    }

    @Override
    public int getMC() {
        return _mc;
    }
}
