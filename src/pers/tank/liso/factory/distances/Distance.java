package pers.tank.liso.factory.distances;

/**
 * Created by derek on 3/22/17.
 */
public interface Distance {
    double getDistance(byte[] src, byte[] dst) throws Exception;
    double normalize(double dist);
}
