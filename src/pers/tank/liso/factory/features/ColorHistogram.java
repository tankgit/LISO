package pers.tank.liso.factory.features;

import org.opencv.core.Mat;
import pers.tank.liso.factory.Transforms;
import pers.tank.liso.factory.distances.Distance;

/**
 * Created by derek on 3/17/17.
 */
public class ColorHistogram implements Feature {

    private byte[] feature;
    @Override
    public void extract(Mat src,Transforms colorSpace) {
        Mat dst=new Mat();
        colorSpace.transform(src,dst);
        int[] groupNumber=colorSpace.getGroupSize();
        int[] groupLength=colorSpace.getGroupLength();
        double[] feature=new double[groupNumber[0]+groupNumber[1]+groupNumber[2]];
        for(int i=0;i<dst.size().height;i+=4){
            for(int j=0;j<dst.size().width;j+=4){
                feature[(int) dst.get(i,j)[0]/groupLength[0]]++;
                feature[groupNumber[0]+(int)dst.get(i,j)[1]/groupLength[1]]++;
                feature[groupNumber[0]+groupNumber[1]+(int) dst.get(i,j)[2]/groupLength[2]]++;
            }
        }
        this.feature=normalize(feature,dst.size().height*dst.size().width/16);
    }

    @Override
    public double getDistance(byte[] img1, byte[] img2, Distance distance) throws Exception {
        return distance.getDistance(img1,img2);
    }

    @Override
    public byte[] getFeature() {
        return this.feature;
    }

    private static byte[] normalize(double[] src,double pixels){
        byte[] dst=new byte[src.length];
        double s=127 /pixels;
        for(int i=0;i<src.length;i++){
            dst[i]=(byte)(src[i]*s);
        }
        return dst;
    }
}
