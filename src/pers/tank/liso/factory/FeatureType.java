package pers.tank.liso.factory;

import pers.tank.liso.factory.features.ColorHistogram;
import pers.tank.liso.factory.features.Feature;

import java.util.ArrayList;

/**
 * Created by derek on 3/22/17.
 */
public class FeatureType{

    private ArrayList<Class<? extends Feature>> featureClasses;

    public static final int
        F_COLOR_HIST=1;


    public FeatureType(int[] type){
        this.featureClasses=new ArrayList<>(type.length);
        for (int t : type) {
            switch (t) {
                case F_COLOR_HIST:
                    this.featureClasses.add(ColorHistogram.class);
            }
        }
    }

    public ArrayList<Class<? extends Feature>> getFeatureClasses(){
        return this.featureClasses;
    }
}
