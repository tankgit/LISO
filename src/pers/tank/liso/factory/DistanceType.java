package pers.tank.liso.factory;

import pers.tank.liso.factory.distances.CityBlock;
import pers.tank.liso.factory.distances.Distance;
import pers.tank.liso.factory.distances.Euclidean;
import pers.tank.liso.factory.distances.HistogramIntersection;

import java.util.ArrayList;

/**
 * Created by derek on 3/22/17.
 */
public class DistanceType {
    private ArrayList<Class<? extends Distance>> distanceClasses;

    public static final int
        EUCLIDEAN=1,
        CITY_BLOCK=2,
        HISTOGRAM_INTERSECTION=3;


    public DistanceType(int[] type){
        distanceClasses=new ArrayList<>(type.length);
        for (int t : type) {
            switch (t) {
                case EUCLIDEAN:
                    distanceClasses.add(Euclidean.class);
                    break;
                case CITY_BLOCK:
                    distanceClasses.add(CityBlock.class);
                    break;
                case HISTOGRAM_INTERSECTION:
                    distanceClasses.add(HistogramIntersection.class);
            }
        }
    }

    public ArrayList<Class<? extends Distance>> getDistanceClasses(){
        return this.distanceClasses;
    }
}
