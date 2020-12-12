package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Ex2 implements Runnable{
    private static MyFrame _frame;
    private static Arena _arena;
    private static int counter =0;
    public static void main(String[] a) {
        Thread client = new Thread(new Ex2());
        client.start();
    }

    @Override
    public void run() {
        int scenario_num = 23;
        game_service game = Game_Server_Ex2.getServer(scenario_num); // you have [0,23] games
        //	int id = 999;
        //	game.login(id);
        String g = game.getGraph();
        String pks = game.getPokemons();
        directed_weighted_graph gg = game.getJava_Graph_Not_to_be_used();
        init(game);

        game.startGame();
        _frame.setTitle("Ex2 - OOP: (NONE trivial Solution) "+game.toString());
        int ind=0;
        long dt=100;

        while(game.isRunning()) {
            moveAgants(game);
            try {
                if(ind%1==0) {_frame.repaint();}
                Thread.sleep(dt);
                ind++;
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        String res = game.toString();

        System.out.println(res);
        System.exit(0);
    }
    /**
     * Moves each of the agents along the edge,
     * in case the agent is on a node the next destination (next edge) is chosen (randomly).
     * @param game
     * @param
     */
    private static void moveAgants(game_service game) {
        counter++;
        String lg = game.move();
        directed_weighted_graph graph = _arena.getGraph();
        List<CL_Agent> agents_list = Arena.getAgents(lg, graph);
        _arena.setAgents(agents_list);
        String pokemons_string =  game.getPokemons();
        List<CL_Pokemon> pokemons_list = Arena.json2Pokemons(pokemons_string);
        _arena.setPokemons(pokemons_list);
        for(int a = 0; a< _arena.getPokemons().size(); a++) {
            gameClient.Arena.updateEdge(_arena.getPokemons().get(a),graph);
        }

        dw_graph_algorithms dwq = new DWGraph_Algo();
        dwq.init(graph);
        CL_Agent [] pokemons_index_arr = new CL_Agent[pokemons_list.size()];
        List<CL_Pokemon> not_defined_pokemons = new ArrayList<>();
        for(int i=0; i<agents_list.size(); i++){
            int agent_src = agents_list.get(i).getSrcNode();
            for(int j=0; j<pokemons_list.size(); j++) {
                CL_Pokemon curr_pokemon = pokemons_list.get(j);
                int pokemon_src = curr_pokemon.get_edge().getSrc();
                if (_arena._nodes_dist.get(agent_src).get(pokemon_src) != null) {
                    if (_arena._nodes_dist.get(agent_src).get(pokemon_src) < _arena._avg_dist) {
                        if (pokemons_index_arr[j] == null) {
                            pokemons_index_arr[j] = agents_list.get(i);
                        }
                        else {
                            if (pokemons_index_arr[j].getSrcNode() != pokemon_src) {
                                double agent_to_pokemon = _arena._nodes_dist.get(agent_src).get(pokemon_src);
                                double curr_agent_to_pokemon = _arena._nodes_dist.get(pokemons_index_arr[j].getSrcNode()).get(pokemon_src);
                                if (agent_to_pokemon < curr_agent_to_pokemon) {
                                    pokemons_index_arr[j] = agents_list.get(i);
                                }
                            }
                        }
                    }
                }
                else{
                    if(agent_src==pokemon_src){
                        pokemons_index_arr[j] = agents_list.get(i);
                    }
                }

            }
        }
        List<CL_Agent> not_defined_agents = new ArrayList<>();
        for(int k=0; k<agents_list.size();k++) {
            boolean flag = false;
            CL_Agent curr_agent = agents_list.get(k);
            int closest_pokemon=-1;
            for (int i = 0; i < pokemons_index_arr.length; i++) {
                double min_dist = Double.POSITIVE_INFINITY;
                if (pokemons_index_arr[i] == curr_agent) {
                    if(curr_agent.getSrcNode()==pokemons_list.get(i).get_edge().getSrc()){
                        min_dist=0;
                        closest_pokemon=pokemons_list.get(i).get_edge().getDest();
                        flag = true;
                    }
                    else {
                        flag = true;
                        CL_Pokemon curr_pokemon = pokemons_list.get(i);
                        double curr_dist = _arena._nodes_dist.get(curr_agent.getSrcNode()).get(curr_pokemon.get_edge().getSrc());
                        if (curr_dist < min_dist) {
                            min_dist = curr_dist;
                            closest_pokemon = curr_pokemon.get_edge().getSrc();
                        }
                    }
                }

            }
            if(flag) {
                List<node_data> shortestPath = dwq.shortestPath(curr_agent.getSrcNode(), closest_pokemon);
                game.chooseNextEdge(curr_agent.getID(), shortestPath.get(1).getKey());
                System.out.println("Counter: "+counter+", Optimize: Agent :"+ curr_agent.getID()+", Dest is:"+curr_agent.getNextNode()+ " , Real Dest is: "+shortestPath.get(1).getKey());
                int real_key = curr_agent.getNextNode();
            }
            else{
                not_defined_agents.add(curr_agent);
            }
        }
        for (int i = 0; i < pokemons_index_arr.length; i++) {
            if(pokemons_index_arr[i]==null){
                not_defined_pokemons.add(pokemons_list.get(i));
            }
        }


        for(int i=0; i<not_defined_agents.size(); i++) {
            CL_Agent agent = not_defined_agents.get(i);
            int id = agent.getID();
            int dest = agent.getNextNode();
            int src = agent.getSrcNode();
            double v = agent.getValue();
            if(dest==-1) {
                dest = nextNode(graph, src, not_defined_pokemons);
                game.chooseNextEdge(agent.getID(), dest);
                int real_key = agent.getNextNode();
                System.out.println("Agent :"+ agent.getID()+", Dest is:"+agent.getNextNode()+ " , Real Dest is: "+dest);
                System.out.println("Agent: "+id+", val: "+v+"   turned to node: "+dest);
            }
        }
    }
    /**
     * a very simple random walk implementation!
     * @param g
     * @param src
     * @return
     */
    private static int nextNode(directed_weighted_graph g, int src, List<CL_Pokemon> pokemons_list) {

        dw_graph_algorithms graph_algo = new DWGraph_Algo();
        graph_algo.init(g);
        double min_dist = Double.POSITIVE_INFINITY;
        int ans=-1;
        for (CL_Pokemon pokemon : pokemons_list) {
            double shortest_path_dist = graph_algo.shortestPathDist(src, pokemon.get_edge().getSrc());
            if (shortest_path_dist == 0){
                System.out.println("This is the Answer:" + pokemon.get_edge().getDest());
                ans = pokemon.get_edge().getDest();
                HashMap<Integer, Double> map = new HashMap<>();
                map.put(src, g.getEdge(src,ans).getWeight());
                return ans;
            }
            else if (shortest_path_dist < min_dist && shortest_path_dist!=-1) {
                min_dist = shortest_path_dist;
                List<node_data> shortest_path = graph_algo.shortestPath(src, pokemon.get_edge().getSrc());
                node_data next_node = shortest_path.get(1);
                ans = next_node.getKey();
            }
        }
        System.out.println("This is the final Answer:" + ans);
        return ans;
    }

    private void init(game_service game) {
        String graph_string = game.getGraph();
        String fs = game.getPokemons();
        dw_graph_algorithms dwg = new DWGraph_Algo();
        try {
            FileWriter fileWriter = new FileWriter("data\\currGraph");
            fileWriter.write(graph_string);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dwg.load("data\\currGraph");
        directed_weighted_graph graph = dwg.getGraph();
        _arena = new Arena();
        _arena.setGraph(graph);
        System.out.println(graph);
        _arena.setPokemons(Arena.json2Pokemons(fs));
        _frame = new MyFrame("test Ex2");
        _frame.setSize(1000, 700);
        _frame.update(_arena);
        _frame.show();

        String game_info = game.toString();
        JSONObject game_object;
        try {
            game_object = new JSONObject(game_info);
            JSONObject gameServer = game_object.getJSONObject("GameServer");
            int agents = gameServer.getInt("agents");
            int src_node = 0;  // arbitrary node, you should start at one of the pokemon
            List<CL_Pokemon> pokemon_list =_arena.getPokemons();
            for(int a = 0; a< pokemon_list.size(); a++) {
                Arena.updateEdge(_arena.getPokemons().get(a),graph);
            }
            double sum_dist = 0;
            double numOfConnection =0;
            for(node_data curr_node : graph.getV()){
                int curr_key = curr_node.getKey();
                HashMap<Integer, Double> curr_dist_from_nodes = new HashMap<>();
                for(node_data other_node: graph.getV()) {
                    int other_key = other_node.getKey();
                    if (curr_node.getKey() != other_node.getKey()) {
                        double shortestPath_curr_to_other = dwg.shortestPathDist(curr_key, other_key);
                        curr_dist_from_nodes.put(other_key,shortestPath_curr_to_other);
                        if(shortestPath_curr_to_other!=-1){
                            sum_dist+=shortestPath_curr_to_other;
                            numOfConnection++;
                        }
                    }
                }
                _arena._nodes_dist.put(curr_key, curr_dist_from_nodes);
            }
            if(numOfConnection>0){
                _arena._avg_dist = sum_dist/(numOfConnection*2);}
            System.out.println(_arena._nodes_dist.toString());
            System.out.println(_arena._avg_dist);
//            HashMap<Integer,  Double> avg_dist_from_all_pokemons = new HashMap<>();
//            HashMap<Integer,  Integer> num_reachable_pokemons = new HashMap<>();
//
//            for(node_data curr_node : graph.getV()){
//                int curr_key = curr_node.getKey();
//                int numOfReachable =0;
//                double sum_dist = 0;
//                HashMap<Integer, Double> curr_dist_from_nodes = new HashMap<>();
//                for(int i =0; i<pokemon_list.size(); i++) {
//                    int src = pokemon_list.get(i).get_edge().getSrc();
//                    if (dwg.shortestPathDist(curr_key, src) != -1) {
//                        sum_dist += dwg.shortestPathDist(curr_key, src);
//                        numOfReachable++;
//                    }
//                }
//                num_reachable_pokemons.put(curr_key, numOfReachable);
//                avg_dist_from_all_pokemons.put(curr_key, sum_dist/pokemon_list.size());
//            }

            HashMap<Integer, Integer> num_of_pokemons_bellow_avg = new HashMap<>();
            HashMap<Integer, HashSet<Integer>> pokemons_bellow_avg = new HashMap<>();
            HashMap<Integer, Double> avg_dist_from_close_pokemons = new HashMap<>();

            System.out.println("Num Of Pokemons is: "+pokemon_list.size());
            for(node_data curr_node : graph.getV()){
                int pokemon_counter=0;
                double avg_dist_from_pokemons=0;
                HashSet<Integer> curr_pokemons = new HashSet<>();
                for(int i =0; i<pokemon_list.size(); i++){
                    CL_Pokemon pokemon = pokemon_list.get(i);
                    Integer src = pokemon.get_edge().getSrc();
                    if(_arena._nodes_dist.get(curr_node.getKey()).get(src)!=null || curr_node.getKey()==src) {
                        if(curr_node.getKey()==src) {
                            System.out.println("Src and Dest are the same "+src +", add: "+i);
                            pokemon_counter++;
                            curr_pokemons.add(i);
                        }
                        else {
                            double dist = _arena._nodes_dist.get(curr_node.getKey()).get(src);
                            if (dist <= _arena._avg_dist) {
                                System.out.println("Node is: " + curr_node.getKey() + ", Pokemon is:" + i + ", Src is:" + src + " Dest is: " + pokemon.get_edge().getDest() + ", Dist is:" + dist);
                                List<node_data> shortestPath = dwg.shortestPath(curr_node.getKey(), src);
                                for (int t = 0; t < shortestPath.size() - 1; t++) {
                                    node_data node = shortestPath.get(t);
                                    System.out.print(node.getKey() + "-> (" + dwg.getGraph().getEdge(node.getKey(), shortestPath.get(t + 1).getKey()).getWeight() + ") " + shortestPath.get(t + 1).getKey() + " ->");
                                }
                                System.out.println();
                                pokemon_counter++;
                                curr_pokemons.add(i);
                                avg_dist_from_pokemons+=dist;
                            }
                        }
                    }
                }
                avg_dist_from_close_pokemons.put(curr_node.getKey(), avg_dist_from_pokemons/pokemon_counter);
                pokemons_bellow_avg.put(curr_node.getKey(),curr_pokemons);
                num_of_pokemons_bellow_avg.put(curr_node.getKey(),pokemon_counter);
            }
            HashMap<Integer, Integer> num_of_connection_bellow_avg = new HashMap<>();
            HashMap<Integer, HashSet<Integer>> connections_bellow_avg = new HashMap<>();
            for(node_data curr_node : graph.getV()){
                int connection_counter=0;
                HashSet<Integer> curr_connections = new HashSet<>();
                for(Integer other_node_key : _arena._nodes_dist.get(curr_node.getKey()).keySet()){
                    double dist = _arena._nodes_dist.get(curr_node.getKey()).get(other_node_key);
                    if(dist<=_arena._avg_dist){
                        connection_counter++;
                        curr_connections.add(other_node_key);
                    }
                }
                connections_bellow_avg.put(curr_node.getKey(),curr_connections);
                num_of_connection_bellow_avg.put(curr_node.getKey(),connection_counter);
            }
            List<Integer> ordered_by_num_of_pokemons = new ArrayList<>();
            ordered_by_num_of_pokemons.addAll(pokemons_bellow_avg.keySet());
            ordered_by_num_of_pokemons.sort(new ConnectionsComperator(num_of_pokemons_bellow_avg, avg_dist_from_close_pokemons));
            System.out.println(num_of_pokemons_bellow_avg);
            System.out.println(ordered_by_num_of_pokemons);
            System.out.println(avg_dist_from_close_pokemons);
            HashSet<Integer> combined_connections = new HashSet<>();
            int list_size =ordered_by_num_of_pokemons.size();
            int head = ordered_by_num_of_pokemons.get(list_size-1);
            game.addAgent(head);
            combined_connections.addAll(connections_bellow_avg.get(head));
            int i=1;
            List<Integer> added = new ArrayList<>();
            added.add(head);
            for(int j=list_size-2; i<agents && j>=0; j--){
                if(!combined_connections.contains(ordered_by_num_of_pokemons.get(j))) {
                    game.addAgent(ordered_by_num_of_pokemons.get(j));
                    i++;
                    added.add(ordered_by_num_of_pokemons.get(j));
                    combined_connections.addAll(connections_bellow_avg.get(ordered_by_num_of_pokemons.get(j)));
                }

            }
            if(i<agents){
                ordered_by_num_of_pokemons.removeAll(added);
                list_size =ordered_by_num_of_pokemons.size();
                for(int j=list_size-1; i<agents && j>=0; j--){
                    game.addAgent(ordered_by_num_of_pokemons.get(j));
                    i++;
                }
            }
            System.out.println(connections_bellow_avg);
            System.out.println(ordered_by_num_of_pokemons);
            System.out.println(game.getAgents());
//            System.out.println(num_of_connection_bellow_avg.toString());
//            System.out.println(connections_bellow_avg.toString());
//            System.out.println(ordered_by_num_of_connections.toString());

//            List<Integer> ordered_by_num_of_connections = new ArrayList<>();
//            ordered_by_num_of_connections.addAll(num_of_connection_bellow_avg.keySet());
//            ordered_by_num_of_connections.sort(new ConnectionsComperator(num_of_connection_bellow_avg));
//            HashSet<Integer> combined_connections = new HashSet<>();
//            int list_size =ordered_by_num_of_connections.size();
//            int head = ordered_by_num_of_connections.get(list_size-1);
//            game.addAgent(head);
//            combined_connections.addAll(connections_bellow_avg.get(head));
//            int i=1;
//            List<Integer> added = new ArrayList<>();
//            added.add(head);
//            for(int j=list_size-2; i<agents && j>=0; j--){
//                if(!combined_connections.contains(ordered_by_num_of_connections.get(j))) {
//                    game.addAgent(ordered_by_num_of_connections.get(j));
//                    i++;
//                    added.add(ordered_by_num_of_connections.get(j));
//                    combined_connections.addAll(connections_bellow_avg.get(ordered_by_num_of_connections.get(j)));
//                }
//
//            }
//            if(i<agents){
//                ordered_by_num_of_connections.removeAll(added);
//                list_size =ordered_by_num_of_connections.size();
//                for(int j=list_size-1; i<agents && j>=0; j--){
//                    game.addAgent(ordered_by_num_of_connections.get(j));
//                    i++;
//                }
//            }
//            System.out.println(connections_bellow_avg);
//            System.out.println(ordered_by_num_of_connections);
//            System.out.println(game.getAgents());
////            System.out.println(num_of_connection_bellow_avg.toString());
////            System.out.println(connections_bellow_avg.toString());
////            System.out.println(ordered_by_num_of_connections.toString());
            Thread.sleep(100);

        }
        catch (JSONException | InterruptedException e) {e.printStackTrace();}
    }
    public class ConnectionsComperator implements Comparator<Integer>{
        HashMap<Integer, Integer> _num_of_pokemons_bellow_avg;
        HashMap<Integer, Double> _avg_dist_from_all_pokemons;

        public ConnectionsComperator(HashMap<Integer, Integer> num_of_pokemons_bellow_avg, HashMap<Integer, Double> avg_dist_from_all_pokemons){
            _num_of_pokemons_bellow_avg=num_of_pokemons_bellow_avg;
            _avg_dist_from_all_pokemons=avg_dist_from_all_pokemons;
        }

        @Override
        public int compare(Integer  o1, Integer o2) {
            if(_num_of_pokemons_bellow_avg.get(o1)==null){
                return -1;
            }
            else if(_num_of_pokemons_bellow_avg.get(o2)==null){
                return 1;
            }
            else if(_num_of_pokemons_bellow_avg.get(o1)<_num_of_pokemons_bellow_avg.get(o2)){
                return -1;
            }
            else if(_num_of_pokemons_bellow_avg.get(o1)>_num_of_pokemons_bellow_avg.get(o2)){
                return 1;
            }
            else{
                System.out.println("Here, "+o1 +" and "+o2 +" has the same number of pokemons");
                if(_avg_dist_from_all_pokemons.get(o1)>_avg_dist_from_all_pokemons.get(o2)){
                    System.out.println(o2 + " Won");
                    return -1;
                }
                else if(_avg_dist_from_all_pokemons.get(o1)<_avg_dist_from_all_pokemons.get(o2)){
                    System.out.println(o1 + " Won");
                    return 1;
                }
                return 0;
            }
        }

    }
    public class ConnectionsComperator2 implements Comparator<Integer>{
        HashMap<Integer, Double> _num_of_connection_bellow_avg;

        public ConnectionsComperator2(HashMap<Integer, Double> num_of_connection_bellow_avg){
            _num_of_connection_bellow_avg=num_of_connection_bellow_avg;
        }

        @Override
        public int compare(Integer  o1, Integer o2) {
            if(_num_of_connection_bellow_avg.get(o1)<_num_of_connection_bellow_avg.get(o2)){
                return -1;
            }
            else if(_num_of_connection_bellow_avg.get(o1)>_num_of_connection_bellow_avg.get(o2)){
                return 1;
            }
            else{
                return 0;
            }
        }

    }
}