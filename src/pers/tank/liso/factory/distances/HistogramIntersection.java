package pers.tank.liso.factory.distances;

/**
 * Created by derek on 3/23/17.
 */
public class HistogramIntersection implements Distance{
    @Override
    public double getDistance(byte[] src, byte[] dst) throws Exception {
        double dist=0;
        double s=0;
        for (int i=0;i<src.length;i++){
            dist+=Math.min(src[i],dst[i]);
            s+=src[i];
        }
        dist=1-dist/s;
        return normalize(dist);
    }

    @Override
    public double normalize(double dist) {
        return dist*100;
    }
}
