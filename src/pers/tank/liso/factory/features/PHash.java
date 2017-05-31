package pers.tank.liso.factory.features;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import pers.tank.liso.factory.Transforms;
import pers.tank.liso.factory.distances.Distance;

/**
 * Created by derek on 5/31/17.
 */
public class PHash implements Feature{
    private byte[] feature;
    private int N=32;
    private int S=8;    //S is no more than 8, since feature is byte type.
    private double n1=Math.pow(1.0/N,0.5);
    private double n2=Math.pow(2.0/N,0.5);

    @Override
    public void extract(Mat src, Transforms colorSpace) {
        Mat dst=new Mat();
        Imgproc.resize(src,dst,new Size(this.N,this.N));
        Imgproc.cvtColor(dst,dst,Imgproc.COLOR_RGB2GRAY);
        double mean=0;
        double[][] dct=new double[this.N][this.N];
        double pin=Math.PI/this.N;
        for (int u=0;u<this.N;u++){
            for (int v=0;v<this.N;v++){
                for(int i=0;i<this.N;i++){
                    for(int j=0;j<this.N;j++){
                        dct[u][v]+=dst.get(i,j)[0]*Math.cos((i+0.5)*pin*u)*Math.cos((j+0.5)*pin*v);
                    }
                }
                dct[u][v]*=(c(u)*c(v));
                mean+=dct[u][v];
            }
        }
        mean/=(N*N);
        this.feature=new byte[S];
        for(int u=0;u<this.S;u++){
            for(int v=0;v<this.S;v++){
                this.feature[u]<<=1;
                this.feature[u]+=(byte)(dct[u][v]>mean?1:0);
            }
        }

    }

    private double c(int u){
        return u==0?this.n1:this.n2;
    }

    @Override
    public double getDistance(byte[] src, byte[] dst, Distance distance) throws Exception {
        return distance.getDistance(src,dst);
    }

    @Override
    public byte[] getFeature() {
        return this.feature;
    }
}
