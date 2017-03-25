package pers.tank.liso.factory.features;

import org.opencv.core.Mat;
import pers.tank.liso.factory.Transforms;
import pers.tank.liso.factory.distances.Distance;

/**
 * Created by derek on 3/24/17.
 */
public class Hash implements Feature {
    @Override
    public void extract(Mat src, Transforms colorSpace) {

    }

    @Override
    public double getDistance(byte[] src, byte[] dst, Distance distance) throws Exception {
        return 0;
    }

    @Override
    public byte[] getFeature() {
        return new byte[0];
    }
}
