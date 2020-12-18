package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * This class is the main thread of the pokemon game, which is responsible for both the initialization of each scenario of the game and
 * also the movement of the agents on the graph, by using the following functions:
 *  - init() - initialize the graph to the current scenario (graph structure and initial pokemon placment) and the starting point
 *    for each one of the provided agents
 *  - MoveAgents() - the main function of the pokemon game, which on any function call, gets the game's current state (current agents location in time and
 *    pokemons location on the graph) and chosed for each one of the agents it next node by preforming the Hungarian algorithm.
 * The class is also holding the following class variables:
 *  - _frame - the current JFrame of the graph (graphic representation of the current game state)
 *  - _arena - stores the main "play-ground" of this graph, meaning the directed_weighted_graph of the following scenario, the agents and pokemons.
 *  - _dt - number of milliseconds that we set the Thread to sleep.
 *  - _id - current user's id.
 *  _scenario - current game level.
 *  _flag - true when the user needs to manually fill the details (id and scenario), and false when the details are given from the cmd (args array).
 * **/
public class Ex2 implements Runnable{
    private static MyFrame _frame;
    private static Arena _arena;
    private static long _dt;
    private static long _id;
    private static int _scenario;
    private static boolean _flag;

    public static void main(String[] args) {
        if(args.length==0) {
            _flag=true;
        }
        else{ //array length supposed to be 2, if not - will take two first elments in the arrayx
            _flag=false;
            _id=Long.parseLong(args[0]);
            _scenario=Integer.parseInt(args[1]);
        }
        Thread client = new Thread(new Ex2());
        client.start();
        }

    /**
     * The main method that runs the game till time ends.
     * In this method, if _flag is true, means that no data was provided from cmd, so the user needs to insert his id and
     * scenario number manually. After this, the game starts and graph(including all agents and pokemons movements) will be shown.
      */
    @Override
    public void run() {
        if(_flag) {
            ImageIcon icon = new ImageIcon("pokemon_image1.png");
            _id = Long.parseLong((String) JOptionPane.showInputDialog(null, "Insert your ID", "Pokemon Game", JOptionPane.INFORMATION_MESSAGE, icon,null,""));
            _scenario = Integer.parseInt((String) JOptionPane.showInputDialog(_frame, "Choose Level", "Pokemon Game", JOptionPane.QUESTION_MESSAGE, icon, null,""));
        }

        game_service game = Game_Server_Ex2.getServer(_scenario); // [0,23] levels
        //game.login(_id);
        init(game);
        game.startGame();
        _frame.setTitle("Pokemon Challenge");
        _frame.set_scenario(_scenario);
        _frame.set_id(_id);
        _dt = 130;

        List<CL_Agent> agents_list = Arena.getAgents(game.getAgents(), _arena.getGraph());

        while(game.isRunning()) {
            _frame.set_time(game.timeToEnd());
           moveAgants(game, agents_list);
            try {
                _frame.repaint();
                Thread.sleep(_dt);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        String res = game.toString();
        System.out.println("Game Results for User Id: "+_id+" in Level number: "+_scenario);
        System.out.println(res);
        System.exit(0);
    }

    /**
     * This function is used when ever we wish to move agents on the graph and choose their next destination and is assigning for each agent in the graph
     * is target pokemon in the following way:
     *   - first, we move the agents (based on their previous ChooseNextEdge selection) and stored the returned value of the current, most up to date
     *   information about the game in 'move' variable.
     *   - then, we use the returned value ('move') to update the current state of our agents (meaning current real time location & speed) using each agent's 'update'
     *   function, and also updating _arena to the current  status of all the pokemons in the graph (old and new ones).
     *   - after making sure all our components are up to date, we start landing the basis to our chooseNextEdge strategy:
     *      - first, we build a 2d [][] matrix, when number of rows is the number of agents in the game, and the number of columns is the number of pokemons in the game
     *      meaning: Matrix [num_of_agents][num_of_pokemons].
     *      - then for each agent a (when a is the index of the agent in the List of all the agents) and for each pokemon p (when p is the inex of the pokemon in
     *      the List of all the pokemons), we update the matrix at location [a][p] to the shortestPathDist from a.getSrc() -> p.getSrc(),
     *      meaning: matrix[a][p]=shortestPathDist(a.getSrc(),p.getSrc()) (we do so by using the _nodes_dist HashMap of _arena, which holds the dist between each
     *      2 nodes in the graph)
     *      - then, create a n*n squared matrix (matrix_2d), when n = Math.max(num_of_agents, num_of_pokemons) and copy the original matrix values to it, for
     *      every cell [i][j] in the matrix which is not initialized in our matrix [a][p], matrix_2d[i][j] = 0;
     *      - Now we have a 2d n*n matrix which in every cell [a][p] there is the shortest path between agents_list.get(a) and pokemons_list.get(p), and now
     *      we are going to use the implementation of the Hungarian Algorithm by :https://github.com/aalmi (view util folder) which is used to find the the best combination
     *      which creates the smallest sum of cell values, which each 2 cells in the selection are not in the same line or row, meaning:
     *      HungarianAlgorithm(2d_matrix) = { C in 2d_matrix | c ={i,j}, V c' in matrix, c'.i()!=c.i && c'.j!=c'j}. for further explanation: en.wikipedia.org/wiki/Hungarian_algorithm.
     *      - then, after using the Hungarian Algorithm to determine the combination of cells which creates the smallest su, we now have the shortest amount of steps
     *      needed in order to gather the most pokemons, now all is left is for each returned cell c = {i,j} in the Hungarian Algorithm list (actually formed as a [n][2] array)
     *      we need to send agents_list.get(i) in the next step to a path towards pokemons_list.get(j) (if i<agents_list.size() && j<pokemons_list.size().
     * @param game
     * @param agents_list
     * @return
     */
    private static void moveAgants(game_service game, List<CL_Agent> agents_list) {
        String move = game.move();
        _frame.set_agents_score(move);
        directed_weighted_graph graph = _arena.getGraph();
        _arena.setAgents(agents_list);
        String pokemons_string = game.getPokemons();
        List<CL_Pokemon> pokemons_list = Arena.json2Pokemons(pokemons_string);
        _arena.setPokemons(pokemons_list);
        int agents_size = agents_list.size();
        int pokemons_size = pokemons_list.size();
        dw_graph_algorithms dwg = new DWGraph_Algo();
        dwg.init(graph);
        JSONObject json_game = null;
        try {
            json_game = new JSONObject(move);
            JSONArray agents_json = json_game.getJSONArray("Agents");
            for(int i=0; i<agents_list.size();i++){
                CL_Agent curr_agent = agents_list.get(i);
                int curr_agent_id = agents_list.get(i).getID();
                for(int j=0; j<agents_json.length();j++){
                    JSONObject agent_line = agents_json.getJSONObject(j).getJSONObject("Agent");
                    if(agent_line.getInt("id")==curr_agent_id){
                        curr_agent.update(agents_json.getJSONObject(j).toString());
                        System.out.println(agents_json.getJSONObject(j).toString());
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int a = 0; a < _arena.getPokemons().size(); a++) {
            gameClient.Arena.updateEdge(_arena.getPokemons().get(a), graph);
        }
        double[][] matrix = new double[agents_size][pokemons_size];
        for (int a = 0; a < matrix.length; a++) {
            int agent_src = agents_list.get(a).getSrcNode();
            double agent_speed = agents_list.get(a).getSpeed();
            for (int p = 0; p < matrix[0].length; p++) {
                int pokemon_src = pokemons_list.get(p).get_edge().getSrc();
                matrix[a][p] = (_arena.get_nodes_dist().get(agent_src).get(pokemon_src)) / agent_speed;
            }
        }
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
        boolean flag = false;
        HungarianAlgorithm ha = new HungarianAlgorithm(matrix_2d);
        int [][] optimal = ha.findOptimalAssignment();
        for(int i=0; i<optimal.length; i++){
            int [] curr = optimal[i];
            if(curr[0]<pokemons_size && curr[1]<agents_size){
                CL_Agent agent = agents_list.get(curr[1]);
                CL_Pokemon pokemon = pokemons_list.get(curr[0]);
                if (agent.getSrcNode() == pokemon.get_edge().getSrc()) {
                    game.chooseNextEdge(agent.getID(), pokemon.get_edge().getDest());
                    flag = true;
                }
                else {
                    List<node_data> shortest_path = dwg.shortestPath(agent.getSrcNode(), pokemon.get_edge().getSrc());
                    game.chooseNextEdge(agent.getID(),shortest_path.get(1).getKey());
                }
            }
        }
        if (flag){
            _dt = 60;
            return;
        }
        _dt = 130;
    }

    /**
     * The following function is responsible for the initialization the game_service provided by the server according to the desired scenario, meaning it responsible to
     * initialize our _arena, _frame and among that to choose the first location of each agent in the graph, and it do so by following the next steps:
     *  - first we load the info from the game services by exacting the graph of the game and the initial pokemons location in a Json formatted strings.
     *  - then, in order to load the graph from the json string we create a dw_graph_algorithm (dwg), save the Json String in to a local file in the data folder and then
     *  load it back into dwg using the load function, now we can take the graph and set him as our _arena graph,
     *  - we now take the pokemons Json String and use the function Arena.updatePokemons which was provided by Boaz to place the pokemons on the graph according
     *  to the provided geo_location of them.
     *  - then in order to choose the initial location of the agents in the graph we use the following strategy:
     *  we first create in our _arena an hashmap of hashmap (_nodes_dits) which holds the shortestPathDist between each 2 nodes in the graph, meaning:
     *  HashMap<Integer<HashMap<Integer, Double>>, when <i<j<shortestPathDist(i,j))>.
     *  - Then we take the sum of all the path between each 2 nodes in the graph and divide it by the number of connected nodes in the graph to determine _areng.avgDist
     *  - the for each node in the graph which looks for the one with the most pokemons in the range of avg_dist, we create a list of all the nodes, store for each
     *  node in a HashMap the number of pokemons where for each pokemon p the distance from to current node to p.getSrc is bellow the agv_dist, meaning:
     *  if shortestPathDist(curr_node,p) < avg_dist, then num_of_pokemons_bellow_avg.get(curr_node)++ and pokemons_bellow_avg.get(curr_node).put(p).
     *  - then we add all the nodes into a list and sort them by the number of pokemons in close range (meaning below avg_dist) that they have using a
     *  Comparator we built.
     *  - we go through the list and we add the agents in the graph in n first place of the sorted list pf nodes  (n= number of agents in the scenario), meaning:
     *  for(int i=list.size(); i>=0 && i<agents_list.size(); i++) game.add(sorted_by_num_pokemons.get(i)).
     * @param game
     * @return
     *  **/
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
        _frame = new MyFrame("EX2 OOP");
        _frame.setSize(1000, 700);
        _frame.set_ar(_arena);
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
                _arena.get_nodes_dist().put(curr_key, curr_dist_from_nodes);
            }
            if(numOfConnection>0){
                _arena.set_avg_dist(sum_dist/(numOfConnection*2));
            }
            HashMap<Integer, Integer> num_of_pokemons_bellow_avg = new HashMap<>();
            HashMap<Integer, HashSet<Integer>> pokemons_bellow_avg = new HashMap<>();
            HashMap<Integer, Double> avg_dist_from_close_pokemons = new HashMap<>();

            for(node_data curr_node : graph.getV()){
                int pokemon_counter=0;
                double avg_dist_from_pokemons=0;
                HashSet<Integer> curr_pokemons = new HashSet<>();
                for(int i =0; i<pokemon_list.size(); i++){
                    CL_Pokemon pokemon = pokemon_list.get(i);
                    Integer src = pokemon.get_edge().getSrc();
                    if(_arena.get_nodes_dist().get(curr_node.getKey()).get(src)!=null || curr_node.getKey()==src) {
                        if(curr_node.getKey()==src) {
                            pokemon_counter++;
                            curr_pokemons.add(i);
                        }
                        else {
                            double dist = _arena.get_nodes_dist().get(curr_node.getKey()).get(src);
                            if (dist <= _arena.get_avg_dist()) {
                                List<node_data> shortestPath = dwg.shortestPath(curr_node.getKey(), src);
                                for (int t = 0; t < shortestPath.size() - 1; t++) {
                                    node_data node = shortestPath.get(t);
                                }
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
                for(Integer other_node_key : _arena.get_nodes_dist().get(curr_node.getKey()).keySet()){
                    double dist = _arena.get_nodes_dist().get(curr_node.getKey()).get(other_node_key);
                    if(dist<=_arena.get_avg_dist()){
                        connection_counter++;
                        curr_connections.add(other_node_key);
                    }
                }
                connections_bellow_avg.put(curr_node.getKey(),curr_connections);
                num_of_connection_bellow_avg.put(curr_node.getKey(),connection_counter);
            }
            List<Integer> ordered_by_num_of_pokemons = new ArrayList<>();
            ordered_by_num_of_pokemons.addAll(pokemons_bellow_avg.keySet());
            ordered_by_num_of_pokemons.sort(new ConnectionsComparator(num_of_pokemons_bellow_avg, avg_dist_from_close_pokemons));
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

    /**
     * This class is a comparator that checks the number of pokemons in smallest ragne (meaning below avg_dist).
     */
    private class ConnectionsComparator implements Comparator<Integer>{
        HashMap<Integer, Integer> _num_of_pokemons_bellow_avg;
        HashMap<Integer, Double> _avg_dist_from_all_pokemons;

        public ConnectionsComparator(HashMap<Integer, Integer> num_of_pokemons_bellow_avg, HashMap<Integer, Double> avg_dist_from_all_pokemons){
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
}