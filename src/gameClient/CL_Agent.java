package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import gameClient.util.Point3D;
import org.json.JSONObject;

/**
 * This class was taken from https://github.com/simon-pikalov/Ariel_OOP_2020/blob/master/Assignments/Ex2/src/gameClient/CL_Agent.java
 * and was modified and improved regarding our implementation.
 * This class represents agents in a graph, when each agent contains:
 *  - _id - agent's unique id
 *  - _pos - agent's geo location
 *  - _speed - agent's speed
 *  - _curr_edge - the current edge where the agent is on at the moment
 *  - _curr_node - the source of the _curr_edge
 *  - _gg - the directed weighted graph
 *  - _value - value of the agent (depends on the pokemon he ate in the graph)
 */
public class CL_Agent{
    private int _id;
    private geo_location _pos;
    private double _speed;
    private edge_data _curr_edge;
    private node_data _curr_node;
    private directed_weighted_graph _gg;
    private double _value;


    /**
     * Constructor
     */
    public CL_Agent(directed_weighted_graph g, int start_node) {
        _gg = g;
        setValue(0);
        this._curr_node = _gg.getNode(start_node);
        _pos = _curr_node.getLocation();
        _id = -1;
        setSpeed(0);
    }

    /**
     * This method updates an agent by extracting its data from a given string.
     * @param json - string with agent's data
     */
    public void update(String json) {
        JSONObject line;
        try {
            line = new JSONObject(json);
            JSONObject json_object = line.getJSONObject("Agent");
            int id = json_object.getInt("id");
            if(id==this.getID() || this.getID() == -1) {
                if(this.getID() == -1) {_id = id;}
                double speed = json_object.getDouble("speed");
                String p = json_object.getString("pos");
                Point3D pp = new Point3D(p);
                int src = json_object.getInt("src");
                int dest = json_object.getInt("dest");
                double value = json_object.getDouble("value");
                this._pos = pp;
                this.setCurrNode(src);
                this.setSpeed(speed);
                this.setNextNode(dest);
                this.setValue(value);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return agent's current source node's key
     */
    public int getSrcNode() {
        return this._curr_node.getKey();
    }

    /**
     * This method returns a string with all agent's data (including id, value, src, dest, speed and pos)
     * @return a String
     */
    public String toJSON() {
        int d = this.getNextNode();
        String ans = "{\"Agent\":{"
                + "\"id\":"+this._id+","
                + "\"value\":"+this._value+","
                + "\"src\":"+this._curr_node.getKey()+","
                + "\"dest\":"+d+","
                + "\"speed\":"+this.getSpeed()+","
                + "\"pos\":\""+_pos.toString()+"\""
                + "}"
                + "}";
        return ans;
    }

    /**
     * This method allows set agent current value
     * @param value - updated agent's value
     */
    public void setValue(double value) {
        _value = value;
    }

    public double getValue(){
        return _value;
    }

    /**
     * This method allows set agent's next node (its temporal destination node)
     * @param dest - key of the dest node
     * @return true if was updated successfully, false if not
     */
    public boolean setNextNode(int dest) {
        boolean ans = false;
        int src = this._curr_node.getKey();
        this._curr_edge = _gg.getEdge(src, dest);
        if(_curr_edge!=null) {
            ans=true;
        }
        else {
            _curr_edge = null;
        }
        return ans;
    }

    /**
     * This method returns the key of the next agent's node
     * @return key of the next agent's node - if it is -1, means curr edge is null
     */
    public int getNextNode() {
        if (_curr_edge==null) {
            return -1;
        }
        else {
            return _curr_edge.getDest();
        }
    }

    /**
     * This method allows set agent's current node (_curr_node variale)
     * @param src - updated node's key
     */
    public void setCurrNode(int src) {
        this._curr_node = _gg.getNode(src);
    }

    public node_data get_curr_node(){
        return _curr_node;
    }

    /**
     * @return agent's id (_id variable)
     */
    public int getID() {
        return this._id;
    }

    /**
     * @return agent's geo location (_pos variable)
     */
    public geo_location getLocation() {
        return _pos;
    }

    /**
     * @return agent's current speed (_speed variable)
     */
    public double getSpeed() {
        return _speed;
    }

    /**
     * This method allows set the speed of an agent
     * @param speed - updated agent's speed
     */
    public void setSpeed(double speed) {
        _speed = speed;
    }

    public String toString() {
        return toJSON();
    }

}
