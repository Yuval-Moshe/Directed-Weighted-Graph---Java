package api;

public class EdgeData implements edge_data {

    private int _src;
    private int _dest;
    private double _weight;
    private String _info;
    private int _tag;

    public EdgeData(int src, int dest, double weight){
        _src = src;
        _dest = dest;
        _weight = weight;
        _info = "";
        _tag = 0;
    }

    @Override
    public int getSrc() {
        return _src;
    }

    @Override
    public int getDest() {
        return _dest;
    }

    @Override
    public double getWeight() {
        return _weight;
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
