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
    private static int _counter =0;
    public static void main(String[] a) {
        Thread client = new Thread(new Ex2());
        client.start();
    }

    @Override
    public void run() {
        int count = 0;
        int scenario_num = 3;
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
//            if (count < 200) {
//                if (count%2 == 0) {
//                    moveAgants(game);
//                }
//            }
//            else moveAgants(game);
            try {
                if(ind%1==0) {_frame.repaint();}
                Thread.sleep(dt);
                ind++;
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            count++;
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
        _counter++;
        String lg = game.move();
        directed_weighted_graph graph = _arena.getGraph();
        List<CL_Agent> curr_agents = Arena.getAgents(game.getAgents(), graph);
        List<CL_Agent> agents_list = Arena.getAgents(lg, graph);
        _arena.setAgents(agents_list);
        String pokemons_string = game.getPokemons();
        List<CL_Pokemon> pokemons_list = Arena.json2Pokemons(pokemons_string);
        _arena.setPokemons(pokemons_list);
        int agents_size = agents_list.size();
        int pokemons_size = pokemons_list.size();
        dw_graph_algorithms dwg = new DWGraph_Algo();
        dwg.init(graph);

        for (int a = 0; a < _arena.getPokemons().size(); a++) {
            gameClient.Arena.updateEdge(_arena.getPokemons().get(a), graph);
        }
        double[][] matrix = new double[agents_size][pokemons_size];
        for (int a = 0; a < matrix.length; a++) {
            int agent_src = agents_list.get(a).getSrcNode();
            double agent_speed = agents_list.get(a).getSpeed();
            for (int p = 0; p < matrix[0].length; p++) {
                int pokemon_src = pokemons_list.get(p).get_edge().getSrc();
                matrix[a][p] = (_arena._nodes_dist.get(agent_src).get(pokemon_src)) / agent_speed;
            }
        }
//       Hungarian
        int size = Math.max(agents_size,pokemons_size);
        double[][] matrix_2d = new double[size][size];
        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                if(i<matrix.length && j<matrix[0].length){
                    matrix_2d[i][j]=matrix[i][j];
                }
                else{
                    matrix_2d[i][j]=0;
                }
                }
            }
        HungarianAlgorithm ha = new HungarianAlgorithm(matrix_2d);
        int [][] optimal = ha.findOptimalAssignment();
        for(int i=0; i<optimal.length; i++){
            int [] curr = optimal[i];
            if(curr[0]<pokemons_size && curr[1]<agents_size){
                CL_Agent agent = agents_list.get(curr[1]);
                CL_Pokemon pokemon = pokemons_list.get(curr[0]);
                if (agent.getSrcNode() == pokemon.get_edge().getSrc()) {
                    game.chooseNextEdge(agent.getID(), pokemon.get_edge().getDest());
                    System.out.println("Agent equals Pokemon: "+agent.getID() + " src: "+ agent.getSrcNode()+"--> dest: "+pokemon.get_edge().getDest()+" Real: "+agent.getNextNode());
                    }
                else {
                List<node_data> shortest_path = dwg.shortestPath(agent.getSrcNode(), pokemon.get_edge().getSrc());
                game.chooseNextEdge(agent.getID(),shortest_path.get(1).getKey());
                System.out.println("Agent "+agent.getID() + " src: "+ agent.getSrcNode()+"--> dest: "+pokemon.get_edge().getSrc()+" Real: "+agent.getNextNode());
            }
            }
        }
        ///////////
        //Our Solution
//        int num = Math.min(agents_size, pokemons_size);
//        int index_a = -1, index_p = -1;
//        HashSet<Integer> forbidden_a = new HashSet<>();
//        HashSet<Integer> forbidden_p = new HashSet<>();
//        double min = Double.POSITIVE_INFINITY;
//        for (int i = 0; i < num; i++) {
//            for (int a = 0; a < matrix.length; a++) {
//                if (!forbidden_a.contains(a)) {
//                    for (int p = 0; p < matrix[0].length; p++) {
//                        if (!forbidden_p.contains(p)) {
//                            if (matrix[a][p] < min && matrix[a][p]!=-1) {
//                                min = matrix[a][p];
//                                index_a = a;
//                                index_p = p;
//                            }
//                        }
//                    }
//                }
//            }
//            System.out.println("Here: min is: "+ min+ "agent is at: "+agents_list.get(index_a).getSrcNode() + " going to: "+pokemons_list.get(index_p).get_edge().getSrc());
//            CL_Agent agent = agents_list.get(index_a);
//            CL_Pokemon pokemon = pokemons_list.get(index_p);
//            if (agent.getSrcNode() == pokemon.get_edge().getSrc()) {
//                game.chooseNextEdge(agent.getID(), pokemon.get_edge().getDest());
//                System.out.println("Agent equals Pokemon: "+agent.getID() + " src: "+ agent.getSrcNode()+"--> dest: "+pokemon.get_edge().getDest()+" Real: "+agent.getNextNode());
//            } else {
//                List<node_data> shortest_path = dwg.shortestPath(agent.getSrcNode(), pokemon.get_edge().getSrc());
//                game.chooseNextEdge(agent.getID(),shortest_path.get(1).getKey());
//                System.out.println("Agent "+agent.getID() + " src: "+ agent.getSrcNode()+"--> dest: "+pokemon.get_edge().getSrc()+" Real: "+agent.getNextNode());
//            }
//            forbidden_a.add(index_a);
//            forbidden_p.add(index_p);
//            index_a = -1;
//            index_p = -1;
//            min = Double.POSITIVE_INFINITY;
//        }
    }

    private void init(game_service game) {
        String graph_string = game.getGraph();
        String pokemons_string = game.getPokemons();
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
        _arena.setPokemons(Arena.json2Pokemons(pokemons_string));
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
                    if (curr_node.getKey() == other_node.getKey()){ //if nodes are the same
                        curr_dist_from_nodes.put(other_key,0.0);
                    }
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
                _arena._avg_dist = sum_dist/(numOfConnection*2);
            }
            System.out.println(_arena._nodes_dist.toString());
            System.out.println(_arena._avg_dist);
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

        }
        catch (JSONException e) {e.printStackTrace();}
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
                if(_avg_dist_from_all_pokemons.get(o1)>_avg_dist_from_all_pokemons.get(o2)){
                    return -1;
                }
                else if(_avg_dist_from_all_pokemons.get(o1)<_avg_dist_from_all_pokemons.get(o2)){
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