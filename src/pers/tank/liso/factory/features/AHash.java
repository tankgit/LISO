package pers.tank.liso.factory.features;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import pers.tank.liso.factory.Transforms;
import pers.tank.liso.factory.distances.Distance;

/**
 * Created by derek on 3/24/17.
 */
public class AHash implements Feature {
    private byte[] feature;
    private int N=8;

    @Override
    public void extract(Mat src, Transforms colorSpace) {
        Mat dst=new Mat();
        Imgproc.resize(src,dst,new Size(this.N,this.N));
        Imgproc.cvtColor(dst,dst,Imgproc.COLOR_RGB2GRAY);
        double mean=0;
        for (int i=0;i<this.N;i++){
            for (int j=0;j<this.N;j++){
                mean+=dst.get(i,j)[0];
            }
        }
        mean/=(this.N*this.N);
        this.feature=new byte[this.N];
        for (int i=0;i<this.N;i++){
            for(int j=0;j<this.N;j++){
                this.feature[i]<<=1;
                this.feature[i]+=(byte)(dst.get(i,j)[0]>mean?1:0);
            }
        }

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
