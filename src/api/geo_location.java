package api;

/**
 * This interface represents a geo location <x,y,z>, aka gameClient.util.Point3D
 */
public interface geo_location {
    public double x();
    public double y();
    public double z();
    public double distance(geo_location g);
}