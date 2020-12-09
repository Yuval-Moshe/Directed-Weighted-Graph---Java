public class NodeData implements node_data {
    private static int counter = 0;
    private int _key;
    private geo_location _gl;
    private double _weight;
    private String _info;
    private int _tag;

    public NodeData(){
        _key = counter++;
        _gl = new Point3D(0,0,0);;
        _weight = 0;
        _info ="";
        _tag = 0;
    }

    public NodeData(int key, geo_location gl){
        _key = key;
        _gl = new Point3D(gl.x(),gl.y(),gl.z());
        _weight = 0;
        _info = "";
        _tag = 0;
    }

    @Override
    public int getKey() {
        return _key;
    }

    @Override
    public geo_location getLocation() {
        return _gl;
    }

    @Override
    public void setLocation(geo_location p) { //??
        _gl = p;
    }

    @Override
    public double getWeight() {
        return _weight;
    }

    @Override
    public void setWeight(double w) {
        _weight = w;
    }

    @Override
    public String getInfo() {
        return _info;
    }

    @Override
    public void setInfo(String s) {
        _info = s;
    }

    @Override
    public int getTag() {
        return _tag;
    }

    @Override
    public void setTag(int t) {
        _tag = t;
    }
}
