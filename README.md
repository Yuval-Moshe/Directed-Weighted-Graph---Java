## README

## Background <br />

This project is divided into 2 parts:<br />

<ins>First part</ins> - Implementation of an undirected weighted graph with the following classes: <br />
NodeData - implements the node_data interface, which is the interface of nodes in an undirected weighted graph. <br />
EdgeData - implements the edge_data interface, which is the interface of edges in an undirected weighted graph. <br />
DWGraph_DS - implements the directed_weighted_graph interface, which represents the graph itself. <br />
DWGraph_Algo - implements the dw_graph_algorithms interface, which allows performing algorithmic queries on a specific graph. <br />
The graph was realized by using HashMap data structures, and the operations were written by realizing Diexera algorithm (please see explanation in the algorithm itself). <br />
<ins>Second part</ins> - this part uses the structure and the algorithms that were developed in part one, to realize the "Pokemon Challenge" game, which is beeing played against server.

## How to run
In order to gain more specific imformation on how to run this project please view the attached wiki pages:
- [How to run - First Part](https://github.com/Yuval-Moshe/Ex2/wiki/How-To-Run---Weighted-&-Directed-Graph)
- [How to run - Second Part](https://github.com/Yuval-Moshe/Ex2/wiki/How-To-Run---Pokemon-Game)

##### Definitions
 * *directed graph - a set of nodes that are connected together, where all the edges are directed from one vertex to another
 * *weighted graph - edges have weight*
 
##### Example:
 
 
![dw_graph_image](https://user-images.githubusercontent.com/68948784/102025281-8a0af380-3d9f-11eb-8662-b426537caa95.png)
 
This graph contains 6 nodes and 9 edges (and it is a connected graph).<br />
shortestPathDist method between node 0 to node 5 will return **14** (4+2+2+6).<br />
shortestPath method between node 0 to node 5 will return the list **{0,1,3,4,5}**.<br />
Do notice that the shortest path depends on the sum of the weights of the edges between the 2 nodes
and not on the sum of the nodes between 2 nodes.

###### For more information please check the Wiki page
