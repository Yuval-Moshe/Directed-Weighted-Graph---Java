package gameClient;

import api.*;
import gameClient.util.Point3D;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains tests for CL_Pokemon class:
 * - setget_edge - checks both set_edge and get_edge methods
 * - getLocation - checks the getLocation method
 * - getType - checks the getType method
 */
class CL_PokemonTest {

    @Test
    void setget_edge() {
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
        dwg.connect(first_key+1,first_key+4,5);

        Point3D point = new Point3D(2,3,0);
        edge_data edge = new EdgeData(first_key+2, first_key+1,6);
        int type = -1;
        double value = 5;
        CL_Pokemon pokemon = new CL_Pokemon(point, type, value, edge);
        edge_data updated_edge = new EdgeData(first_key+1, first_key+4,10);
        pokemon.set_edge(updated_edge);
        pokemon.set_edge(updated_edge);
        assertEquals(10, pokemon.get_edge().getWeight());
        assertEquals(updated_edge.getSrc(), pokemon.get_edge().getSrc());
    }

    @Test
    void getLocation() {
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
        dwg.connect(first_key+1,first_key+4,5);

        Point3D point = new Point3D(2,3,0);
        edge_data edge = new EdgeData(first_key+2, first_key+1,6);
        int type = -1;
        double value = 5;
        CL_Pokemon pokemon = new CL_Pokemon(point, type, value, edge);
        Point3D p_test = pokemon.getLocation();
        assertEquals(point.x(), p_test.x());
        assertEquals(point.y(), p_test.y());
        assertEquals(point.z(), p_test.z());
    }

    @Test
    void getType() {
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
        dwg.connect(first_key+1,first_key+4,5);

        Point3D point = new Point3D(2,3,0);
        edge_data edge = new EdgeData(first_key+2, first_key+1,6);
        int type = -1;
        double value = 5;
        CL_Pokemon pokemon = new CL_Pokemon(point, type, value, edge);
        assertEquals(-1, pokemon.getType());
    }
}