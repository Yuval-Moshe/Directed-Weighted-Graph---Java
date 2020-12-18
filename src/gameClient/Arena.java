package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import gameClient.util.Range2Range;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a multi Agents Arena which move on a graph and eat Pokemons.
 * We used https://github.com/simon-pikalov/Ariel_OOP_2020/blob/master/Assignments/Ex2/src/gameClient/Arena.java ,that
 * was written by boaz.benmoshe, and improved it according to our implementation.
 * We added 2 additional class variables:
 * - _nodes_dist - hashmap which its keys are all graph's nodes keys, and the value is consists of a second hashmap.
 *   In the second hashmap, the keys are all other nodes, and the value is the total shortest distance bwtween current node
 *   to other node.
 * - _avg_dist - contains the average distance between every 2 nodes in the graph.
 */

public class Arena {
    public static final double EPS = 0.001*0.001;
    private directed_weighted_graph _gg;
    private List<CL_Agent> _agents;
    private List<CL_Pokemon> _pokemons;
    private List<String> _info;
    private HashMap<Integer, HashMap<Integer, Double>> _nodes_dist;
    private double _avg_dist;


    /**
     * Constructor
     */
    public Arena() {
        _info = new ArrayList<String>();
        _nodes_dist = new HashMap<Integer, HashMap<Integer, Double>>();
    }

    /**
     * This method sets _pokemons to be the given list of pokemons
     * @param f - updated list of CL_Pokemon
     */
    public void setPokemons(List<CL_Pokemon> f) {
        this._pokemons = f;
    }

    /**
     * This method sets _agents to be the given list of agents
     * @param f - updated list of CL_Agent
     */
    public void setAgents(List<CL_Agent> f) {
        this._agents = f;
    }

    /**
     * This method sets _gg to be the given directed weighted graph
     * @param g - updated list of CL_Agent
     */
    public void setGraph(directed_weighted_graph g) { //init();}
        this._gg =g;
    }

    /**
     * @return list of agents
     */
    public List<CL_Agent> getAgents() {
        return _agents;
    }

    /**
     * @return list of pokemons
     */
    public List<CL_Pokemon> getPokemons() {
        return _pokemons;
    }

    /**
     * @return directed weighted graph
     */
    public directed_weighted_graph getGraph() {
        return _gg;
    }

    /**
     * This method gets a String and a graph, and returns a list of agents in the graph by extracting the information from the string.
     * @param str - a string with graph's information
     * @param gg - directed weighted fraph
     * @return a list of agents that are in the graph
     */
    public static List<CL_Agent> getAgents(String str, directed_weighted_graph gg) {
        ArrayList<CL_Agent> ans = new ArrayList<CL_Agent>();
        try {
            JSONObject json_object = new JSONObject(str);
            JSONArray ags = json_object.getJSONArray("Agents");
            for(int i=0;i<ags.length();i++) {
                CL_Agent c = new CL_Agent(gg,0);
                c.update(ags.get(i).toString());
                ans.add(c);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return ans;
    }

    /**
     * This method gets a String, and returns a list of pokemons in the graph by extracting information from the string.
     * @param str - a string with graph's information
     * @return an updated list of pokemons that are in the graph
     */
    public static ArrayList<CL_Pokemon> json2Pokemons(String str) {
        ArrayList<CL_Pokemon> ans = new  ArrayList<CL_Pokemon>();
        try {
            JSONObject json_object = new JSONObject(str);
            JSONArray ags = json_object.getJSONArray("Pokemons");
            for(int i=0;i<ags.length();i++) {
                JSONObject pp = ags.getJSONObject(i);
                JSONObject pk = pp.getJSONObject("Pokemon");
                int t = pk.getInt("type");
                double v = pk.getDouble("value");
                //double s = 0;//pk.getDouble("speed");
                String p = pk.getString("pos");
                CL_Pokemon f = new CL_Pokemon(new Point3D(p), t, v,null);
                ans.add(f);
            }
        }
        catch (JSONException e) {e.printStackTrace();}
        return ans;
    }

    /**
     * This method gets a pokemon, and a graph, and updates the pokemon current edge.
     * @param pokemon - CL_pokemon
     * @param g - directed weighted graph
     */
    public static void updateEdge(CL_Pokemon pokemon, directed_weighted_graph g) {
        Iterator<node_data> itr = g.getV().iterator();
        while(itr.hasNext()) {
            node_data v = itr.next();
            Iterator<edge_data> iter = g.getE(v.getKey()).iterator();
            while(iter.hasNext()) {
                edge_data edge = iter.next();
                boolean flag = isOnEdge(pokemon.getLocation(), edge,pokemon.getType(), g);
                if(flag) {
                    pokemon.set_edge(edge);
                    return;
                }
            }
        }
    }

    /**
     * This method gets 3 locations: of a pokemon, of src node and of a dest node, and checks if the pokemon appears on the edge
     * between src and dest.
     * @param p - geo_location of a pokemon
     * @param src - geo_location of src node
     * @param dest - geo_location of dest node
     * @return true if pokemon appears on edge, and return false if doesn't
     */
    private static boolean isOnEdge(geo_location p, geo_location src, geo_location dest) {
        boolean ans = false;
        double dist = src.distance(dest);
        double d1 = src.distance(p) + p.distance(dest);
        if(dist>d1-EPS) {ans = true;}
        return ans;
    }

    /**
     * This method gets pokemon location, src node key, dest node key and a graph. The method checks if the pokemon appears
     * on the edge between 2 given nodes in the given graph.
     * @param p - geo_location of a pokemon
     * @param s - src node key (src of an edge)
     * @param d - dest node key (dest of an edge)
     * @param g - directed_weighte graph
     * @return true if pokemon appears on edge, and return false if doesn't
     */
    private static boolean isOnEdge(geo_location p, int s, int d, directed_weighted_graph g) {
        geo_location src = g.getNode(s).getLocation();
        geo_location dest = g.getNode(d).getLocation();
        return isOnEdge(p,src,dest);
    }

    /**
     * This method gets pokemon location, and edge, the pokemon type and a graph. The method checks if the pokemon appears
     * on the edge from its src to its dest by using pokemon's type (if <0 - pokemon should be from big key to small key. if >0 pokemon
     * should be from small key to big key).
     * @param p - geo_location of a pokemon
     * @param e - edge_data
     * @param type - depends on the pokemon direction (from small to big or from big to small)
     * @param g - directed_weighte graph
     * @return true if pokemon appears on edge, and return false if doesn't
     */
    private static boolean isOnEdge(geo_location p, edge_data e, int type, directed_weighted_graph g) {
        int src = g.getNode(e.getSrc()).getKey();
        int dest = g.getNode(e.getDest()).getKey();
        if(type<0 && dest>src) {return false;}
        if(type>0 && src>dest) {return false;}
        return isOnEdge(p,src, dest, g);
    }

    /**
     * This method gets a graph and determines the Range2D of the graph by going over all graph's nodes and
     * returns the graph range.
     * @param g - directed_weighted_graph
     * @return updated Range2D
     */
    private static Range2D GraphRange(directed_weighted_graph g) {
        Iterator<node_data> itr = g.getV().iterator();
        double x0=0,x1=0,y0=0,y1=0;
        boolean first = true;
        while(itr.hasNext()) {
            geo_location p = itr.next().getLocation();
            if(first) {
                x0=p.x(); x1=x0;
                y0=p.y(); y1=y0;
                first = false;
            }
            else {
                if(p.x()<x0) {x0=p.x();}
                if(p.x()>x1) {x1=p.x();}
                if(p.y()<y0) {y0=p.y();}
                if(p.y()>y1) {y1=p.y();}
            }
        }
        Range xr = new Range(x0,x1);
        Range yr = new Range(y0,y1);
        return new Range2D(xr,yr);
    }

    /**
     * This method gets a graph and a Range2D, and determines the updated Range2D of the grpah by using GraphRange method.
     * @param g - directed_weighted_graph
     * @return updated Range2Range
     */
    public static Range2Range w2f(directed_weighted_graph g, Range2D frame) {
        Range2D world = GraphRange(g);
        Range2Range ans = new Range2Range(world, frame);
        return ans;
    }

    /**
     * @return the _nodes_dist - which contains all nodes in the graph and the shortest path distance from them to all other nodes
     * in the graph
     */
    public HashMap<Integer, HashMap<Integer, Double>> get_nodes_dist() {
        return _nodes_dist;
    }

    /**
     * @return the _avg_dist variable
     */
    public double get_avg_dist() {
        return _avg_dist;
    }

    /**
     * This method allows to set the _avg_dist variable, it means the average distance between every 2 nodes in the graph.
     * @param _avg_dist
     */
    public void set_avg_dist(double _avg_dist) {
        this._avg_dist = _avg_dist;
    }


}
