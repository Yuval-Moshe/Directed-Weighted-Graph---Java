import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.Node;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class DWGraph_Algo implements dw_graph_algorithms {
    directed_weighted_graph _dwg;

    @Override
    public void init(directed_weighted_graph g) {
        _dwg = g;
    }

    @Override
    public directed_weighted_graph getGraph() {
        return _dwg;
    }

    @Override
    public directed_weighted_graph copy() {
        directed_weighted_graph g = new DWGraph_DS(_dwg);
        return g;
    }

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

    @Override
    public double shortestPathDist(int src, int dest) {
        return 0;
    }

    @Override
    public List<node_data> shortestPath(int src, int dest) {
        node_data src_node = _dwg.getNode(src);
        node_data dest_node = _dwg.getNode(dest);
        HashMap<Integer, node_data> prev = Dijkstra(src_node, dest_node);
        List<node_data> path = reconstructPath(prev, src_node, dest_node);
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


    public class NodeDataComp implements Comparator<node_data>{
        HashMap<Integer, Double> _minDist;

        public NodeDataComp (HashMap<Integer, Double> minDist){
            _minDist=minDist;
        }


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
}