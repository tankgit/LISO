package pers.tank.liso.factory;

import org.opencv.core.Mat;

import static org.opencv.imgproc.Imgproc.*;
import static org.opencv.imgproc.Imgproc.cvtColor;

/**
 * Created by derek on 3/23/17.
 */
public class Transforms {

    public static final int
            COLOR_RGB=0,
            COLOR_HSV=1;

    private int type;

    private int[] colorRange=new int[3];

    private int[] groupNumber=new int[3];

    private int[] groupLength=new int[3];

    public Transforms(int type){
        this.type=type;
        switch (this.type){
            case COLOR_RGB:
                colorRange[0]=255;
                colorRange[1]=255;
                colorRange[2]=255;
                groupNumber[0]=16;
                groupNumber[1]=16;
                groupNumber[2]=16;
                break;
            case COLOR_HSV:
                colorRange[0]=180;
                colorRange[1]=255;
                colorRange[2]=255;
                groupNumber[0]=12;
                groupNumber[1]=16;
                groupNumber[2]=16;
                break;
        }
        for(int i=0;i<3;i++) {
            groupLength[i] = (colorRange[i]+1)/groupNumber[i];
        }
    }

    public void transform(Mat src,Mat dst){
        switch (this.type){
            case COLOR_RGB:
                break;
            case COLOR_HSV:
                BGR2HSV(src,dst);
                break;
        }
    }

    public int getType(){
        return this.type;
    }

    public int[] getColorRange(){
        return this.colorRange;
    }

    public int[] getGroupSize(){
        return this.groupNumber;
    }

    public int[] getGroupLength(){
        return this.groupLength;
    }

    private static void BGR2HSV(Mat src,Mat dst){
        cvtColor(src,dst,COLOR_BGR2HSV);
    }
}
