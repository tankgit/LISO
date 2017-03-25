package pers.tank.liso.factory.features;

import org.opencv.core.Mat;
import pers.tank.liso.factory.DistanceType;
import pers.tank.liso.factory.Transforms;
import pers.tank.liso.factory.distances.Distance;

/**
 * Created by derek on 3/16/17.
 */
public interface Feature {


    public void extract(Mat src,Transforms colorSpace);

    public double getDistance(byte[] src, byte[] dst, Distance distance) throws Exception;

    public byte[] getFeature();
}
