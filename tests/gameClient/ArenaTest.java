package gameClient;

import api.*;
import gameClient.util.Point3D;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains tests for Arena class:
 * - setgetPokemons - checks both setPokemons and getPokemons methods
 * - setgetAgents - checks both setAgents and getAgents methods
 * - setgetGraph - checks both setGraph and getGraph methods
 * - get_nodes_dist - checks get_nodes_dist (hashmap variable)
 * - setget_avg_dist - checks both get_avg_dist and set_avg_dist (hashmap variable)

 */
class ArenaTest {

    @Test
    void setgetPokemons() {
        directed_weighted_graph dwg = new DWGraph_DS();
        int first_key = 0;
        for (int i = 0; i < 5; i++) {
            node_data node = new NodeData();
            dwg.addNode(node);
            if (i == 0) {
                first_key = dwg.getV().iterator().next().getKey();
            }
        }
        dwg.connect(first_key + 2, first_key + 0, 5);
        dwg.connect(first_key + 2, first_key + 1, 5);
        dwg.connect(first_key + 2, first_key + 3, 5);
        dwg.connect(first_key + 2, first_key + 4, 5);

        Arena arena = new Arena();
        List<CL_Pokemon> pokemons_lst = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            Point3D point = new Point3D(i, i+2, 0);
            edge_data edge = new EdgeData(first_key + i + 1, first_key, i*2.5);
            int type = -1;
            double value = 5;
            CL_Pokemon pokemon = new CL_Pokemon(point, type, value, edge);
            pokemons_lst.add(pokemon);
        }
        arena.setPokemons(pokemons_lst);
        boolean flag = arena.getPokemons().size() == pokemons_lst.size();
        assertTrue(flag);
    }

    @Test
    void setgetAgents() {
        directed_weighted_graph dwg = new DWGraph_DS();
        int first_key = 0;
        for (int i = 0; i < 5; i++) {
            node_data node = new NodeData();
            dwg.addNode(node);
            if (i == 0) {
                first_key = dwg.getV().iterator().next().getKey();
            }
        }
        dwg.connect(first_key + 2, first_key + 0, 5);
        dwg.connect(first_key + 2, first_key + 1, 5);
        dwg.connect(first_key + 2, first_key + 3, 5);
        dwg.connect(first_key + 2, first_key + 4, 5);

        Arena arena = new Arena();
        List<CL_Agent> agents_lst = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            CL_Agent agent = new CL_Agent(dwg,first_key+i);
            agents_lst.add(agent);
        }
        arena.setAgents(agents_lst);
        boolean flag = arena.getAgents().size() == agents_lst.size();
        assertTrue(flag);
    }

    @Test
    void setgetGraph() {
        directed_weighted_graph dwg = new DWGraph_DS();
        int first_key = 0;
        for (int i = 0; i < 5; i++) {
            node_data node = new NodeData();
            dwg.addNode(node);
            if (i == 0) {
                first_key = dwg.getV().iterator().next().getKey();
            }
        }
        dwg.connect(first_key + 2, first_key + 0, 5);
        dwg.connect(first_key + 2, first_key + 1, 5);
        dwg.connect(first_key + 2, first_key + 3, 5);
        dwg.connect(first_key + 2, first_key + 4, 5);

        Arena arena = new Arena();
        arena.setGraph(dwg);
        assertEquals(dwg, arena.getGraph());
    }

    @Test
    void get_nodes_dist() {
        directed_weighted_graph dwg = new DWGraph_DS();
        int first_key = 0;
        for (int i = 0; i < 2; i++) {
            node_data node = new NodeData();
            dwg.addNode(node);
            if (i == 0) {
                first_key = dwg.getV().iterator().next().getKey();
            }
        }
        dwg.connect(first_key + 0, first_key + 1, 5);
        dwg.connect(first_key + 1, first_key + 2, 6.3);

        Arena arena = new Arena();
        HashMap<Integer, Double> map = new HashMap<>();
        map.put(first_key+1,5.0);
        arena.get_nodes_dist().put(first_key+0,map);
        HashMap<Integer, Double> map2 = new HashMap<>();
        map2.put(first_key+2,6.3);
        arena.get_nodes_dist().put(first_key+1, map2);

        assertEquals(5,arena.get_nodes_dist().get(first_key+0).get(first_key+1));
        assertEquals(6.3,arena.get_nodes_dist().get(first_key+1).get(first_key+2));
    }

    @Test
    void setget_avg_dist() {
        directed_weighted_graph dwg = new DWGraph_DS();
        int first_key = 0;
        for (int i = 0; i < 5; i++) {
            node_data node = new NodeData();
            dwg.addNode(node);
            if (i == 0) {
                first_key = dwg.getV().iterator().next().getKey();
            }
        }
        dwg.connect(first_key + 2, first_key + 0, 5);
        dwg.connect(first_key + 2, first_key + 1, 5);
        dwg.connect(first_key + 2, first_key + 3, 5);
        dwg.connect(first_key + 2, first_key + 4, 5);

        Arena arena = new Arena();
        arena.set_avg_dist(5);
        assertEquals(5, arena.get_avg_dist());
    }
}