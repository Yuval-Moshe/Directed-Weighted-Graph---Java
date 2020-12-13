package api;

public class EdgeData implements edge_data {

    /**
     * This class implements the edge_data interface which represents edges in an weighted directed graph with the following class variables:
     * - int _src - The key of the source node of this edge, meaning the edge is directed from _src.
     * - int _dest - The key of the destination node of this edge, meaning the edge is directed to _dest.
     * - double _weight - the weight of this edge.
     * - String _info - an info about this edge.
     * - int _tag - a temporal tag assigned to this edge.
     */

    private int _src;
    private int _dest;
    private double _weight;
    private String _info;
    private int _tag;

    /**
     * Constructor with provided src, dest and weight params
     * @param src - the key of this edge source node.
     * @param dest - the key of this edge destination node.
     * @param weight - this edge weight.
     * **/
    public EdgeData(int src, int dest, double weight){
        _src = src;
        _dest = dest;
        _weight = weight;
        _info = "";
        _tag = 0;
    }

    /**
     * Returns the source node key (_src variable) of this edge.
     * @param
     * @return _src - the key of this edge source node.
     * **/
    @Override
    public int getSrc() {
        return _src;
    }

    /**
     * Returns the destination node key (_dest variable) of this edge.
     * @param
     * @return _dest - the key of this edge destination node.
     * **/
    @Override
    public int getDest() {
        return _dest;
    }

    /**
     * Returns the weight of this edge.
     * @param
     * @return _weight - this edge weight.
     * **/
    @Override
    public double getWeight() {
        return _weight;
    }

    /**
     * Returns the info variable of this edge.
     * @param
     * @return _info - this edge info.
     * **/
    @Override
    public String getInfo() {
        return _info;
    }

    /**
     * Sets the info of this edge to the provided String param.
     * @param s
     * @reutrn
     * **/
    @Override
    public void setInfo(String s) {
        _info = s;
    }

    /**
     * Returns the tag variable of this edge.
     * @param
     * @return _tag - this edge tag.
     * **/
    @Override
    public int getTag() {
        return _tag;
    }

    /**
     * Sets the tag of this edge to the provided int param.
     * @param t
     * @reutrn
     * **/
    @Override
    public void setTag(int t) {
        _tag = t;
    }

    /**
     * Checks of this node is equal to the provided edge_data param, by checking if each one of their class variable is equal
     * @param other
     * @reutrn True if equals, False if not.
     * **/
    public boolean equals (edge_data other){
        boolean flag = true;
        flag &= (_src == other.getSrc());
        flag &= (_dest == other.getDest());
        flag &= (_weight == other.getWeight());
        flag &= (_info == other.getInfo());
        flag &= (_tag == other.getTag());
        return flag;
    }
    }
