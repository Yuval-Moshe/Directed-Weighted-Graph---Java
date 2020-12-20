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
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class was taken from https://github.com/simon-pikalov/Ariel_OOP_2020/blob/master/Assignments/Ex2/src/gameClient/MyFrame.java
 * it was modified and improved according to our implementation
 * This class represents a GUI that represents a game on a graph
 */
public class MyFrame extends JFrame{
    private Arena _ar;
    private gameClient.util.Range2Range _w2f;
    private long _id;
    private int _scenario;
    private long _time;
    private HashMap<Integer, Double> _agents_score = new HashMap<>();

    /**
     * Constructor
     */
    MyFrame(String a) {
        super(a);
    }

    /**
     * This method enables the frame to be resizable, by adjusting the boundaries to the size of the updated frame.
     */
    private void updateFrame() {
        Range rx = new Range(20,this.getWidth()-30);
        Range ry = new Range(this.getHeight()-80,150);
        Range2D frame = new Range2D(rx,ry);
        directed_weighted_graph g = _ar.getGraph();
        _w2f = Arena.w2f(g,frame);
        this.setBackground(Color.darkGray);
    }

    /**
     * This method is responsible for making all updates in the graph to be shown in the frame, by
     * using all other draw methods (drawGraph, drawPokemons, drawAgents and drawInfo).
     * @param g
     * @return
     */
    public void paint(Graphics g) {
        int width = this.getWidth();
        int height = this.getHeight();
        Image background = createImage(width,height);
        Graphics backgroundGraphics = background.getGraphics();
        drawGraph(backgroundGraphics);
        drawPokemons(backgroundGraphics);
        drawAgents(backgroundGraphics);
        drawInfo(backgroundGraphics,_id,_scenario, _time);
        g.drawImage(background,0 ,0, this);
        updateFrame();
    }

    /**
     * This method shows on the screen of the game, which user is playing, in which level, and how
     * much time left till the end of the current level.
     * @param g - graphics of the frame
     * @param id - user id
     * @param game_level - current level
     * @param time - how many milliseconds left till the end of the level
     *
     */
    private void drawInfo(Graphics g, long id, int game_level, long time) {
        g.setColor( new Color(253, 251, 251, 255) );
        g.setFont(new Font("Tahoma", Font.BOLD, 14));
        g.drawString("Time to End: "+ time/1000, 30,60+0*20 );
        g.drawString("Player ID: "+ id, 30,60+1*20 );
        g.drawString("Level Number: " + game_level, 30,60+2*20 );
        int i = 0;
        for (Integer key : _agents_score.keySet()){
            g.drawString("Agent "+key + " score: "+_agents_score.get(key), 820,60+i*20 );
            i++;
        }
    }

    /**
     * This method makes the graph visual, it goes over all nodes and edges in the graph, and
     * places them in the frame by using drawNode and drawEdge methods.
     * @param g - graphics of the frame
     */
    private void drawGraph(Graphics g) {
        directed_weighted_graph gg = _ar.getGraph();
        Iterator<node_data> iter = gg.getV().iterator();
        while(iter.hasNext()) {
            node_data n = iter.next();
            g.setColor(Color.white);
            drawNode(n,5,g);
            Iterator<edge_data> itr = gg.getE(n.getKey()).iterator();
            while(itr.hasNext()) {
                edge_data e = itr.next();
                g.setColor(Color.white);
                drawEdge(e, g);
            }
        }
    }

    /**
     * This method is responsible for showing all pokemons by getting their info from _ar variable.
     * It goes over them with a while loop and place them in the frame by using pokemon data.
     * Pokemons with type less than 0 - are colored with orange.
     * Pokemons with type more than 0 - ware colored with green.
     * @param g - graphics of the frame
     */
    private void drawPokemons(Graphics g) {
        List<CL_Pokemon> fs = _ar.getPokemons();
        if(fs!=null) {
            Iterator<CL_Pokemon> itr = fs.iterator();
            while(itr.hasNext()) {
                CL_Pokemon f = itr.next();
                Point3D c = f.getLocation();
                int r=8;
                g.setColor(Color.green);
                if(f.getType()<0) {
                    g.setColor(Color.orange);}
                if(c!=null) {
                    geo_location fp = this._w2f.world2frame(c);
                    g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
                }
            }
        }
    }

    /**
     * This method is responsible for showing all agents by getting their info from _ar variable.
     * It goes over them with a while loop and place them in the frame by using agent data.
     * Agents are colored with red.
     * @param g - graphics of the frame
     */
    private void drawAgents(Graphics g) {
        List<CL_Agent> rs = _ar.getAgents();
        g.setColor(Color.red);
        int i=0;
        while(rs!=null && i<rs.size()) {
            geo_location c = rs.get(i).getLocation();
            int r=8;
            i++;
            if(c!=null) {
                geo_location fp = this._w2f.world2frame(c);
                g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
            }
        }
    }

    /**
     * This method place the input node in the graph, considering node's location.
     * @param n - the node_data to draw
     * @param r - gets an int which will be used to determine the width and height of the node
     * @param g - graphics of the frame
     */
    private void drawNode(node_data n, int r, Graphics g) {
        geo_location pos = n.getLocation();
        geo_location fp = this._w2f.world2frame(pos);
        g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
        g.drawString(""+n.getKey(), (int)fp.x(), (int)fp.y()-4*r);
    }

    /**
     * This method place the input edge in the graph, considering edge's src and dest locations.
     * @param e - the edge_data to draw
     * @param g - graphics of the frame
     */
    private void drawEdge(edge_data e, Graphics g) {
        directed_weighted_graph gg = _ar.getGraph();
        geo_location s = gg.getNode(e.getSrc()).getLocation();
        geo_location d = gg.getNode(e.getDest()).getLocation();
        geo_location s0 = this._w2f.world2frame(s);
        geo_location d0 = this._w2f.world2frame(d);
        g.drawLine((int)s0.x(), (int)s0.y(), (int)d0.x(), (int)d0.y());
    }

    /**
     * @return this _ar variable.
     */
    public Arena get_ar() {
        return _ar;
    }

    /**
     * This method allows setting updated arena to this frame.
     * @param ar - updated Arena
     */
    public void set_ar(Arena ar) {
        this._ar = ar;
        updateFrame();
    }

    /**
     * @return this _w2f variable.
     */
    public Range2Range get_w2f() {
        return _w2f;
    }

    /**
     * This method allows setting udated _w2f variable.
     * @param _w2f - updated Range2Range
     */
    public void set_w2f(Range2Range _w2f) {
        this._w2f = _w2f;
    }

    /**
     * @return user id.
     */
    public long get_id() {
        return _id;
    }

    /**
     * This method allows setting the current id of the user that is playing the game that is represented by this frame.
     * @param _id - user id
     */
    public void set_id(long _id) {
        this._id = _id;
    }

    /**
     * @return number of current level.
     */
    public int get_scenario() {
        return _scenario;
    }

    /**
     * This method allows setting the current level that is represented by this frame.
     * @param _scenario - the updated value of the level that is being played.
     */
    public void set_scenario(int _scenario) {
        this._scenario = _scenario;
    }

    /**
     * @return the time that is left till the end of the level that is represented by this frame.
     */
    public long get_time() {
        return _time;
    }

    /**
     * This method allows setting the _time value, which represents how much time left
     * till the end of the current level.
     * @param _time - the updated value of time till end of the level
     */
    public void set_time(long _time) {
        this._time = _time;
    }

    /**
     * This method gets a string and updates _agents_score by extracting the data from it
     * @param status - a string with agents data
     */
    public void set_agents_score(String status){
        try {
            JSONObject status_object = new JSONObject(status);
            JSONArray ags = status_object.getJSONArray("Agents");
            for(int i=0;i<ags.length();i++) {
                JSONObject agent = ags.getJSONObject(i).getJSONObject("Agent");
                int id = agent.getInt("id");
                double value = agent.getDouble("value");
                _agents_score.put(id,value);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
