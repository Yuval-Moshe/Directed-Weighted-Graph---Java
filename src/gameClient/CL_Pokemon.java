package gameClient;
import api.edge_data;
import gameClient.util.Point3D;

/**
 * This class was taken from https: https://github.com/simon-pikalov/Ariel_OOP_2020/blob/master/Assignments/Ex2/src/gameClient/CL_Pokemon.java
 * and was modified and improved regarding our implementation.
 * This class represents pokemons in a graph, when each pokemon contains:
 *  - _edge - pokemon's edge
 *  - _value - pokemon's value
 *  - _type - type < 0 (if the pokemon's src > pokemon's dest) | type > 0 (if the pokemon's dest < pokemon's src)
 *  - _pos - location of the pokemon (Point3D)
 */
public class CL_Pokemon {
    private edge_data _edge;
    private double _value;
    private int _type;
    private Point3D _pos;

    /**
     * Constructor
     */
    public CL_Pokemon(Point3D p, int type, double value, edge_data e) {
        _type = type;
        _value = value;
        set_edge(e);
        _pos = p;
    }

    /**
     * toString method
     * @return a string which contains pokemon data (value and type)
     */
    public String toString() {
        return "Pokemon:{v="+_value+", t="+_type+"}";
    }

    /**
     * @return the edge which the pokemon appears on (_edge_data variable)
     */
    public edge_data get_edge() {
        return _edge;
    }

    /**
     * This method allows set pokemon's edge
     * @param _edge - edge_data
     */
    public void set_edge(edge_data _edge) {
        this._edge = _edge;
    }

    /**
     * @return pokemon's geo location (_pos variable)
     */
    public Point3D getLocation() {
        return _pos;
    }

    /**
     * @return pokemon's type (_type variable)
     */
    public int getType() {
        return _type;
    }
}