package pers.tank.liso.search;

/**
 * Created by derek on 3/21/17.
 */
public class SearchResult implements Comparable<SearchResult>{
    private int indexNum=-1;
    private double distance=-1;
    private double[] distanceSet;

    public SearchResult(int indexNum, double[] distanceSet, double distance){
        this.indexNum=indexNum;
        this.distanceSet=distanceSet;
        this.distance=distance;
    }

    public int getIndexNum(){
        return this.indexNum;
    }

    public double getDistance(){
        return this.distance;
    }

    public double[] getDistanceSet(){
        return this.distanceSet;
    }

    @Override
    public int compareTo(SearchResult o) {
        if(this.distance>o.getDistance()){
            return 1;
        }else if(this.distance<o.getDistance()){
            return -1;
        }
        return 0;
    }
}
