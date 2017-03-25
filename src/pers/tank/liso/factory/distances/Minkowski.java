package pers.tank.liso.factory.distances;

/**
 * Created by derek on 3/23/17.
 */
public class Minkowski implements Distance{

    private int r;
    private double range;
    private int size=1;

    public Minkowski(int r){
        this.r=r;
        this.range=Math.pow(127,r);
    }

    @Override
    public double getDistance(byte[] src, byte[] dst) throws Exception {
        this.size=src.length;
        if(this.size==0){
            throw new Exception("Feature size cannot be 0.");
        }
        double dist=0;
        for (int i=0;i<src.length;i++){
            dist+=Math.pow(Math.abs(src[i]-dst[i]),r);
        }

        return normalize(dist);
    }

    @Override
    public double normalize(double dist) {
        return Math.pow(dist/(this.range*this.size),1.0/this.r)*100;
    }
}
