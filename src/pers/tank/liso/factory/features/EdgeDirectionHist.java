package pers.tank.liso.factory.features;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import pers.tank.liso.factory.ImgProcess;
import pers.tank.liso.factory.Transforms;
import pers.tank.liso.factory.distances.Distance;

/**
 * Created by derek on 5/31/17.
 */
public class EdgeDirectionHist implements Feature{
    private byte[] feature;
    private boolean accumulate=false;

    public EdgeDirectionHist(){}

    public EdgeDirectionHist(boolean accumulate){
        this.accumulate=accumulate;
    }

    @Override
    public void extract(Mat src, Transforms colorSpace) {
        Mat dst=new Mat();
        Imgproc.resize(src,dst,new Size(400,400));
        Imgproc.cvtColor(dst,dst,Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(dst,dst,new Size(5,5),1.0);
        double p0,p45,p90,p135,gradient;
        int[][] theta=new int[(int)(dst.size().height-2)][(int)(dst.size().width-2)];
        double T=0.7;
        double t1=0,t2=0;
        for (int i=1;i<dst.size().height-1;i++){
            for (int j=1;j<dst.size().width-1;j++){
                p0=dst.get(i,j+1)[0]-dst.get(i,j-1)[0];
                p45=dst.get(i-1,j+1)[0]-dst.get(i+1,j-1)[0];
                p90=dst.get(i-1,j)[0]-dst.get(i+1,j)[0];
                p135=dst.get(i-1,j-1)[0]-dst.get(i+1,j+1)[0];
                gradient=Math.sqrt(Math.pow(p0,2)+Math.pow(p45,2)+Math.pow(p90,2)+Math.pow(p135,2));
                if(gradient>T){
                    theta[i-1][j-1]= (int) (Math.atan(p0/p90)/Math.PI*180);
                }else{
                    theta[i-1][j-1]= 90;  //any value not in [-90,90)
                }
            }
        }
        int[] directions=new int[180];
        for (int i=0;i<dst.size().height-2;i++){
            for (int j=0;j<dst.size().width-2;j++){
                if(theta[i][j]!=90){
                    directions[theta[i][j]+90]+=1;
                }
            }
        }
        int[] dir=new int[60];
        int count=0;
        for (int i=0;i<60;i++){
            dir[i]=0;
            for (int j=0;j<3;j++){
                dir[i]+=directions[i*3+j];
            }
            count+=dir[i];
        }

        this.feature=normalize(dir,count);
    }

    @Override
    public double getDistance(byte[] src, byte[] dst, Distance distance) throws Exception {
        return distance.getDistance(src,dst);
    }

    @Override
    public byte[] getFeature() {
        return this.feature;
    }

    private static byte[] normalize(int[] src,int count){
        byte[] dst=new byte[src.length];
        double s=127.0 /count;
        for(int i=0;i<src.length;i++){
            dst[i]=(byte)(src[i]*s);
        }
        return dst;
    }
}
