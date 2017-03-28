package pers.tank.liso.factory.features;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import pers.tank.liso.factory.Transforms;
import pers.tank.liso.factory.distances.Distance;
import pers.tank.liso.factory.distances.Hamming;

/**
 * Created by derek on 3/28/17.
 */
public class DHash implements Feature{
    private byte[] feature;

    @Override
    public void extract(Mat src, Transforms colorSpace) {
        Mat dst=new Mat();
        Imgproc.resize(src,dst,new Size(9,8));
        int[][] gray=new int[(int) dst.size().height][(int) dst.size().width];
        for(int i=0;i<dst.size().height;i++) {
            for (int j = 0; j < dst.size().width; j++) {
                gray[i][j]= (int) (dst.get(i,j)[0]*76+dst.get(i,j)[1]*151+dst.get(i,j)[2]*28)>>8;
            }
        }
        this.feature=new byte[8];
        for(int i=0;i<dst.size().height;i++) {
            for (int j = 0; j < dst.size().width-1; j++) {
                this.feature[i]<<=1;
                this.feature[i]+= (byte) (gray[i][j+1]-gray[i][j]>0?0:1);
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
