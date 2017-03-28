package pers.tank.liso.factory.distances;

/**
 * Created by derek on 3/28/17.
 */
public class Hamming implements Distance {
    @Override
    public double getDistance(byte[] src, byte[] dst) throws Exception {
        int dist=0;
        for(int i=0;i<src.length;i++){
            dist+=binaryCount((byte) (src[i]^dst[i]));
        }
        return normalize((double)dist);
    }

    @Override
    public double normalize(double dist) {
        return dist/64*100;
    }

    private int binaryCount(byte b){
        int count=0;
        for(int i=0;i<8;i++){
            count+=b&1;
            b>>=1;
        }
        return count;
    }
}
