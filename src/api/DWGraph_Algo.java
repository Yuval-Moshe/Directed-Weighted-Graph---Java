package api;

import com.google.gson.*;
import gameClient.util.Point3D;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/** This class implements the dw_graph_algorithms interface which allows preforming complex algorithms on a
 * weighted, directed graph, with the following class variables:
 * directed_weighted_graph _dwg - the graph to preform the algorithms on.
 *  **/

public class DWGraph_Algo implements dw_graph_algorithms {
    directed_weighted_graph _dwg;

    /**
     * Initialize the graph to work on the provided weighted graph parameter
     * @param g - directed weighted graph
     * @return
     */
    @Override
    public void init(directed_weighted_graph g) {
        _dwg = g;
    }

    /**
     * Returns the weighted graph in this directed weighted graph algorithm's as _dwg.
     * @param
     * @return directed weighted graph
     */
    @Override
    public directed_weighted_graph getGraph() {
        return _dwg;
    }

    /** Returns a deep copy of this graph by sending him to a copy constructor of the DWGraph_DS class
     * @param
     * @return g - the copied directed_weighted_graph.
     */
    @Override
    public directed_weighted_graph copy() {
        if(_dwg != null) {
            directed_weighted_graph g = new DWGraph_DS(_dwg);
            return g;
        }
        return null;
    }

     /**
      * This function checks if the current directed_weighted_graph of this dw_graph_algorithms is a strongly connected graph, meaning if
      * for each 2 nodes in  the graph , node_a and node_b, there is a path from node_a to node_b, and a path from node_b to node_a.
      * The base assumption behind this function is this: if a directed weighted graph is a strongly connected graph, the following behaviour should happen:
      * when picking a random node in the graph (node_rnd) there should be a path from node_rnd to each other node in the graph, then, if you'll
      * reverse the graph (meaning, that for each edge from node_a to node_b in the graph, an edge from node_b to node_a will be
      * in the reversed graph), and if there is a path between node_rnd to each other node in the reversed graph as well - than the graph is
      * a strongly connected directed weighted graph,because then it will state that in the original graph, there is a path from each node to node_rnd.
      * So, the functions takes a random node, and adds to a HashMap all the nodes which are connected to him in some path, by preforming
      * the BFS algorithm.then, the functions build the reversed graph as described, and preform the same BFS algorithm. If the number of connected
      * nodes to node_rnd in the original graph is equal number of connected nodes to node_rnd in the reversed graph, and both are equals to the
      * number of all the nodes in the graph hence all the nodes are connected to the random node we chose, and therefore there is a path between
      * each 2 nodes in the graph, and the graph is strongly connected.
      * @param
      * @return True - if the graph is a  strongly connected graph, False - if it's not.
      */
    @Override
    public boolean isConnected() {
        boolean flag = false;
        directed_weighted_graph reversed = new DWGraph_DS();
        Collection<node_data> nodes = _dwg.getV();
        if(nodes.isEmpty()){
            return true;
        }
        Queue<node_data> q = new LinkedList<node_data>();
        node_data node = nodes.iterator().next();
        q.add(node);
        HashMap<Integer, Boolean> connected = new HashMap<Integer, Boolean>();
        connected.put(node.getKey(), true);
        reversed.addNode(node);
        while(!q.isEmpty()){
            node_data curr = q.poll();
            Collection<edge_data> curr_edges = _dwg.getE(curr.getKey());
            for(edge_data edge : curr_edges){
                reversed.addNode(_dwg.getNode(edge.getDest()));
                reversed.connect(edge.getDest(), curr.getKey(), edge.getWeight());
                if(connected.get(edge.getDest())==null){
                    q.add(_dwg.getNode(edge.getDest()));
                    connected.put(edge.getDest(),true);
                }
            }
        }
        if(connected.size()==nodes.size()){
            Collection<node_data> r_nodes = reversed.getV();
            Queue<node_data> r_q = new LinkedList<node_data>();
            node_data r_node = r_nodes.iterator().next();
            r_q.add(node);
            HashMap<Integer, Boolean> r_connected = new HashMap<Integer, Boolean>();
            r_connected.put(node.getKey(), true);
            while(!r_q.isEmpty()){
                node_data r_curr = r_q.poll();
                Collection<edge_data> curr_edges = reversed.getE(r_curr.getKey());
                for(edge_data edge : curr_edges){
                    if(r_connected.get(edge.getDest())==null){
                        r_q.add(reversed.getNode(edge.getDest()));
                        r_connected.put(edge.getDest(),true);
                    }
                }
            }
            if (r_connected.size() == r_nodes.size())
                flag = true;
        }

        return flag;

    }

    /**
     * Returns the shortest path distance between src and dest, by first preforming the shortestPath function between src and dest,
     * then if the returned List is empty, there is no path between src and dest and return -1, if the List isn't empty, go over all
     * the nodes in the list and add the sum of there edges weight (length=+getEdge(i,i+1), for each 0<i<n-1, n=shortestPath_list.size()).
     * @param src
     * @param dest
     * @return -1 if there is no path, else return the length of the shortest path.
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        List<node_data> path = shortestPath(src, dest);
        if(!(path == null)){
            double length=0;
            for(int i=0; i<path.size()-1; i++){
                length+=_dwg.getEdge(path.get(i).getKey(), path.get(i+1).getKey()).getWeight();
            }
            return length;
        }
        return -1;

    }

    /**
     * This function returns the shortest path between 2 nodes in a directed, weighted graph by preforming the Dijkstra() algorithm
     * function and the reconstructPath() function.
     * Dijkstra:
     * Initialization:
     *      - min_dist HashMap - which will hold for each node in the graph, the shortest path distance between him and the src node.
     *      - a priority queue pq, which is prioritized by the min_dist value of each node (using a NodeDataComp comparator class which built for the implementation
     *      of this functions and is implemented at the bottom of this class as a private inner class), and will be used to store the next node in the graph to check.
     *      - a Double var, dist to Infinity, which will store the current shortest distance from src to dest.
     *      - Visited Hashset, which will store all the already visited nodes
     *      - prev Hashmap the map the parent of each node which is the closest (by path weight) to the src node.
     * The steps:
     * - put src in min_dist, map him with the distance of 0, and add him to pq.
     * - Start going over the pq until empty, extract the head (marked as curr) of the pq and go over is neighbors (if his not already visited).
     * - For each neighbor, define the current distance from src (in the path that goes through curr), and check if the current distance
     * is shortest then the current shortest distance from src to path, if not - there is no point to continue with this neighbor.
     * - If so, check if the current neighbor is the dest node, if so, replace distance var with the ni_dist_from_src var.
     * - Check if the current neighbor as a parent node marked in the prev Hashmap,  if not - set curr as his parent and change the
     * map the neighbor's key to ni_dist_from_src in min_dist, if is does have a parent node, change the parent node and the min_dist value of the neighbor only if
     * ni_dist_from_src is smaller than the current mapped value to his key in the min_dist Hashmap.
     * - Add the neighbor to the pq.
     * Go thorough this process for each edge in the graph until the pq is empty and return the prev hashmap.
     *
     * reconstructPath:
     * This function takes the prev HashMap from the solve function and the src and dest nodes,
     * and is trying to construct a path between dest to src (the reversed way) by adding to a list the prev of dest,
     * and then the prev of the prev of dest, and so on, until it reached the src node, if it does - its the shortest path
     * between src and dest, if it can't reach the src node - there is no path between src and dest.
     * The function ends by reversing the path (constructed as an ArrayList) to make if from src to dest and not
     * dest to src.
     * @param src
     * @param dest
     * @return a list of nodes representing the shortest path from src to dest, if there isn't any return an empty list.
     * **/
    @Override
    public List<node_data> shortestPath(int src, int dest) {
        node_data src_node = _dwg.getNode(src);
        node_data dest_node = _dwg.getNode(dest);
        HashMap<Integer, node_data> prev = Dijkstra(src_node, dest_node);
        List<node_data> path = reconstructPath(prev, src_node, dest_node);
        if(path.isEmpty()){
            return null;
        }
        return path;
    }

    public HashMap<Integer, node_data> Dijkstra (node_data src, node_data dest){
        HashMap<Integer, Double> minDist = new HashMap<>();
        PriorityQueue<node_data> pq = new PriorityQueue<node_data>(new NodeDataComp(minDist));
        double dist = Double.POSITIVE_INFINITY;
        HashSet<Integer> visited = new HashSet<Integer>();
        HashMap<Integer, node_data> prev = new HashMap<Integer, node_data>();
        minDist.put(src.getKey(),0.0);
        pq.add(src);
        while (!pq.isEmpty()){
            node_data curr = pq.poll();
            int curr_key = curr.getKey();
            if(!visited.contains(curr_key) && curr!=dest){
                visited.add(curr_key);
                Collection<edge_data> curr_edges = _dwg.getE(curr_key);
                for(edge_data edge : curr_edges) {
                    int ni_key = edge.getDest();
                    node_data ni = _dwg.getNode(ni_key);
                    if (!visited.contains(ni_key)) {
                        double ni_dist_from_src = minDist.get(curr_key) + _dwg.getEdge(curr_key, ni_key).getWeight();
                        if (ni_dist_from_src < dist) {
                            if (ni == dest) {
                                if (ni_dist_from_src < dist) {
                                    dist = ni_dist_from_src;
                                }
                            }
                            if(prev.get(ni_key)==null){
                                minDist.put(ni_key,ni_dist_from_src);
                                prev.put(ni_key, curr);
                            }
                            else if(ni_dist_from_src<minDist.get(ni_key)) {
                                minDist.put(ni_key,ni_dist_from_src);
                                prev.put(ni_key, curr);
                            }
                            pq.add(ni);
                        }
                    }
                }
            }
        }
        return prev;
    }
    private List<node_data> reconstructPath (HashMap<Integer, node_data> prev, node_data src, node_data dest){
        List<node_data> path_temp = new ArrayList<node_data>();
        List<node_data> path = new ArrayList<node_data>();
        path_temp.add(dest);
        for(int i = dest.getKey(); prev.get(i)!=null; i=prev.get(i).getKey()){
            path_temp.add(prev.get(i));
        }
        if(!path_temp.isEmpty() && path_temp.get(path_temp.size()-1).getKey()==src.getKey()) {
            for (int i = path_temp.size() - 1; i >= 0; i--) {
                path.add(path_temp.get(i));
            }
        }
        return path;
    }

    /**
     * Saves the current directed_weighted_graph of this dw_graph_algorithms in json format to the provided file location represented by
     * the fle param
     * @param file
     * @return true if the file was successfully saved to the file, false otherwise.
     */
    @Override
    public boolean save(String file) {
        Gson gson = new Gson();
        JsonArray Edges = new JsonArray();
        JsonArray Nodes = new JsonArray();
        for(node_data node : _dwg.getV()){
            JsonObject curr_node = new JsonObject();
            String curr_position = node.getLocation().x()+", "+node.getLocation().y()+", "+node.getLocation().z();
            curr_node.addProperty("pos", curr_position);
            curr_node.addProperty("id", node.getKey());
            Nodes.add(curr_node);
            for(edge_data edge : _dwg.getE(node.getKey())){
                JsonObject curr_edge = new JsonObject();
                curr_edge.addProperty("src", edge.getSrc());
                curr_edge.addProperty("w", edge.getWeight());
                curr_edge.addProperty("dest", edge.getDest());
                Edges.add(curr_edge);
            }
        }
        JsonObject json = new JsonObject();
        json.add("Edges", Edges);
        json.add("Nodes", Nodes);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(json.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Loads a directed_weighted_graph from the provided location (in the file param) and if is successfully loaded, initializes the
     * directed_weighted_graph of this dw_graph_algorithms to be the loaded graph
     * @param file
     * @return true if successfully loaded and initialized, false otherwise.
     */
    @Override
    public boolean load(String file) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        try {
            jsonObject = gson.fromJson(new FileReader(file), JsonObject.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        directed_weighted_graph graph = new DWGraph_DS();
        JsonArray nodes = jsonObject.getAsJsonArray("Nodes");
        JsonArray edges = jsonObject.getAsJsonArray("Edges");
        if(nodes != null && edges!=null) {

            for (JsonElement node_element : nodes) {
                JsonObject node_object = node_element.getAsJsonObject();
                if (node_object.get("pos")!=null && node_object.get("id")!=null) {
                    geo_location gl = new Point3D(node_object.get("pos").getAsString());
                    int key = node_object.get("id").getAsInt();
                    node_data node = new NodeData(key, gl);
                    graph.addNode(node);
                }
                else {
                    return false;
                }
            }

            for (JsonElement edge_element : edges) {
                JsonObject edge_object = edge_element.getAsJsonObject();
                if (edge_object.get("src")!=null && edge_object.get("w")!=null && edge_object.get("dest")!=null) {
                    int src = edge_object.get("src").getAsInt();
                    double weight = edge_object.get("w").getAsDouble();
                    int dest = edge_object.get("dest").getAsInt();
                    graph.connect(src, dest, weight);
                }
                else{
                    return false;
                }
            }
            _dwg=graph;
        }
        else{
            return false;
        }
        return true;
    }


    /**
     * A private  Comparator class for the implementation of shortestPath()
     * **/
    private class NodeDataComp implements Comparator<node_data>{
        private HashMap<Integer, Double> _minDist;

        public NodeDataComp (HashMap<Integer, Double> minDist){
            _minDist=minDist;
        }

        /**
         *
         * @param o1
         * @param o2
         * @return
         */
        @Override
        public int compare(node_data o1, node_data o2) {
            if(_minDist.get(o1.getKey())<_minDist.get(o2.getKey())){
                return -1;
            }
            else if(_minDist.get(o1.getKey())>_minDist.get(o2.getKey())){
                return 1;
            }
            else{
                return 0;
            }
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DWGraph_Algo that = (DWGraph_Algo) o;
        return Objects.equals(_dwg, that._dwg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_dwg);
    }
}