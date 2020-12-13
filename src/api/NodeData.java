package api;

import gameClient.util.Point3D;

    /**
     * This class implements the node_data interface which represents node in an weighted directed graph with the following class variables:
     * - static int counter - a static counter assigning each node with a unique key.
     * - int _key - the unique key of each node.
     * - geo_location _gl - a simple Point3D interface representing the location of each node in a 3D.
     * - double _weight - the weight of this node.
     * - String _info - the info stored in this node.
     * - int _tag - a temporal tag assigned to this node.
     */

public class NodeData implements node_data {
    private static int counter = 0;
    private int _key;
    private geo_location _gl;
    private double _weight;
    private String _info;
    private int _tag;

    /** Constructor **/
    public NodeData(){
        _key = counter++;
        _gl = new Point3D(0,0,0);;
        _weight = 0;
        _info ="";
        _tag = 0;
    }

    /**
     * Constructor with provided key and geo_location params
     * @param key - the node's key.
     * @param gl - the geo_location of this node.
     * **/
    public NodeData(int key, geo_location gl){
        _key = key;
        _gl = new Point3D(gl.x(),gl.y(),gl.z());
        _weight = 0;
        _info = "";
        _tag = 0;
    }

    /**
     * Returns the unique key variable of this node
     * @param
     * @return _key - this node's key.
     * **/
    @Override
    public int getKey() {
        return _key;
    }

    /**
     * Returns the geo_location variable of this node.
     * @param
     * @return _gl - this node's geo_location.
     * **/
    @Override
    public geo_location getLocation() {
        return _gl;
    }

    /**
     * Sets the geo_location of this node to the provided geo_location param
     * @param p
     * @reutrn
     * **/
    @Override
    public void setLocation(geo_location p) { //??
        _gl = p;
    }

    /**
     * Returns the weight variable of this node.
     * @param
     * @return _weight - the weight of this node.
     *  **/
    @Override
    public double getWeight() {
        return _weight;
    }

    /**
     * Sets the weight of this node to the provided double param.
     * @param w
     * @return
     * **/
    @Override
    public void setWeight(double w) {
        _weight = w;
    }

    /**
     * Returns the info variable of this node
     * @param
     * @return _info - the info stored in this node.
     * **/
    @Override
    public String getInfo() {
        return _info;
    }

    /**
     * Sets the info of this node to the provided String param.
     * @param s
     * @return
     * **/
    @Override
    public void setInfo(String s) {
        _info = s;
    }

    /**
     * Returns the temporal tag of this node.
     * @param
     * @return _tag - the tag of this node.
     * **/
    @Override
    public int getTag() {
        return _tag;
    }

    /**
     * Sets the tag of this node to the provided int param.
     * @param t
     * @return
     * **/
    @Override
    public void setTag(int t) {
        _tag = t;
    }

    public boolean equals(node_data other){
        return (_key == other.getKey() &&
                _gl == other.getLocation() &&
                _info == other.getInfo() &&
                _tag == other.getTag() &&
                _weight == other.getWeight());
    }
}
