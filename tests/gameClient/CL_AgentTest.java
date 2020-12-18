package gameClient;

import api.*;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains tests for CL_Agent class:
 * - toJSON - checks that the agent data from a string is extracted correctly.
 * - getSrcNode - checks if the source node's key of an agent equals to the node key that returns from getSrcNode method.
 * - setgetValue - checks both setValue and getValue methods
 * - setgetNextNode - checks both setNextNode and getNextNode methods
 * - setgetCurrNode - checks both setCurrNode and getCurrNode methods
 * - setgetSpeed - checks both setSpee and getSpeed methods
 */
class CL_AgentTest {

    @Test
    void toJSON() {
        directed_weighted_graph dwg = new DWGraph_DS();
        int first_key =0;
        for(int i=0; i<5; i++){
            node_data node = new NodeData();
            dwg.addNode(node);
            if(i==0){
                first_key = dwg.getV().iterator().next().getKey();
            }
        }
        dwg.connect(first_key+2,first_key+0,5);
        dwg.connect(first_key+2,first_key+1,5);
        dwg.connect(first_key+2,first_key+3,5);
        dwg.connect(first_key+2,first_key+4,5);

        CL_Agent agent = new CL_Agent(dwg, first_key+2);
        String str = agent.toJSON();

        try {
            JSONObject status_object = new JSONObject(str);
            JSONObject agent_info = status_object.getJSONObject("Agent");
            int id = agent_info.getInt("id");
            double value = agent_info.getDouble("value");
            double _speed = agent_info.getDouble("speed");

            assertEquals(agent.getID(), id);
            assertEquals(agent.getValue(), value);
            assertEquals(agent.getSpeed(), _speed);
        }

        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    void getSrcNode() {
        directed_weighted_graph graph = new DWGraph_DS();
        node_data node = new NodeData();
        node_data node2 = new NodeData();
        int node2_key = node.getKey();
        graph.addNode(node);
        CL_Agent agent = new CL_Agent(graph, node2_key);
        assertEquals(node2_key, agent.getSrcNode());
    }



    @Test
    void setgetValue() {
        directed_weighted_graph dwg = new DWGraph_DS();
        int first_key =0;
        for(int i=0; i<5; i++){
            node_data node = new NodeData();
            dwg.addNode(node);
            if(i==0){
                first_key = dwg.getV().iterator().next().getKey();
            }
        }
        dwg.connect(first_key+2,first_key+0,5);
        dwg.connect(first_key+2,first_key+1,5);
        dwg.connect(first_key+2,first_key+3,5);
        dwg.connect(first_key+2,first_key+4,5);

        CL_Agent agent = new CL_Agent(dwg,first_key+3);
        agent.setValue(12.5);
        assertEquals(12.5, agent.getValue());
    }

    @Test
    void setgetNextNode() {
        directed_weighted_graph dwg = new DWGraph_DS();
        int first_key =0;
        for(int i=0; i<5; i++){
            node_data node = new NodeData();
            dwg.addNode(node);
            if(i==0){
                first_key = dwg.getV().iterator().next().getKey();
            }
        }
        dwg.connect(first_key+2,first_key+0,5);
        dwg.connect(first_key+2,first_key+1,5);
        dwg.connect(first_key+2,first_key+3,5);
        dwg.connect(first_key+2,first_key+4,5);

        CL_Agent agent = new CL_Agent(dwg,first_key+2);
        agent.setNextNode(first_key+3);
        assertEquals(first_key+3, agent.getNextNode());
    }


    @Test
    void setgetCurrNode() {
        directed_weighted_graph dwg = new DWGraph_DS();
        int first_key =0;
        for(int i=0; i<5; i++){
            node_data node = new NodeData();
            dwg.addNode(node);
            if(i==0){
                first_key = dwg.getV().iterator().next().getKey();
            }
        }
        dwg.connect(first_key+2,first_key+0,5);
        dwg.connect(first_key+2,first_key+1,5);
        dwg.connect(first_key+2,first_key+3,5);
        dwg.connect(first_key+2,first_key+4,5);

        CL_Agent agent = new CL_Agent(dwg,first_key+2);
        agent.setCurrNode(first_key+1);
        assertEquals(first_key+1, agent.get_curr_node().getKey());
    }

    @Test
    void setgetSpeed() {
        directed_weighted_graph dwg = new DWGraph_DS();
        int first_key =0;
        for(int i=0; i<5; i++){
            node_data node = new NodeData();
            dwg.addNode(node);
            if(i==0){
                first_key = dwg.getV().iterator().next().getKey();
            }
        }
        dwg.connect(first_key+2,first_key+0,5);
        dwg.connect(first_key+2,first_key+1,5);
        dwg.connect(first_key+2,first_key+3,5);
        dwg.connect(first_key+2,first_key+4,5);

        CL_Agent agent = new CL_Agent(dwg,first_key+2);
        agent.setSpeed(10);
        assertEquals(10, agent.getSpeed());
    }

}