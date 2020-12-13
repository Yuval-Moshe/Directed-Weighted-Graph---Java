## README

#Background
This project is divided into 2 parts:
First part - Implementation of an undirected weighted graph with the following classes:
NodeData - implements the node_data interface, which is the interface of nodes in an undirected weighted graph.
EdgeData - implements the edge_data interface, which is the interface of edges in an undirected weighted graph.
DWGraph_DS - implements the directed_weighted_graph interface, which represents the graph itself.
DWGraph_Algo - implements the dw_graph_algorithms interface, which allows performing algorithmic queries on a specific graph.
The graph was realized by using HashMap data structures, and the operations were written by realizing Diexera algorithm (please see explanation in the algorithm itself).
Second part - this part uses the structure and the algorithms that were developed in part one, to realize the "Pokemon Challenge" game, which is beeing played against server.

In order to run the game:
1. First use git clone:
  $ git clone https://github.com/Yuval-Moshe/EX2.git
2. //need to add a GUI, then explanation how to use.





##### Definitions
 * *directed graph - a set of nodes that are connected together, where all the edges are directed from one vertex to another
 * *weighted graph - edges have weight*
 
##### Example:
 
 ![](https://i0.wp.com/algorithms.tutorialhorizon.com/files/2018/03/Weighted-Graph.png?ssl=1)
 
 (![dw_graph_image](https://user-images.githubusercontent.com/68948784/102025281-8a0af380-3d9f-11eb-8662-b426537caa95.png)
 
This graph contains 6 nodes and 9 edges (and it is a connected graph).<br />
shortestPathDist method between node 0 to node 5 will return **14** (4+2+2+6).<br />
shortestPath method between node 0 to node 5 will return the list **{0,1,3,4,5}**.<br />
Do notice that the shortest path depends on the sum of the weights of the edges between the 2 nodes
and not on the sum of the nodes between 2 nodes.

###### For more information please check the Wiki page
