import org.apache.lucene.document.Document;
import org.opencv.core.Core;
import pers.tank.liso.factory.Transforms;
import pers.tank.liso.factory.distances.*;
import pers.tank.liso.factory.features.*;
import pers.tank.liso.index.Indexer;
import pers.tank.liso.search.*;

import java.util.ArrayList;


/**
 * Created by derek on 3/21/17.
 */
public class test {
    public static void main(String[] args) throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Feature[] features= new Feature[]{new ColorHistogram(),new DHash()};
        Transforms colorSpace=new Transforms(Transforms.COLOR_HSV);
        Indexer id=new Indexer(
                "imgs",
                "index",
                features,
                colorSpace);
        //id.createIndex();

        double[] featureWeights=new double[]{1,4};
        Distance[] distances=new Distance[]{new Euclidean(),new Hamming()};
        Searcher s=new Searcher(
                "index",
                colorSpace,
                90);
        s.setImage("imgs/1.jpg");
        s.setFeatureType(features,featureWeights);
        s.setDistanceType(distances);
        s.search();
        ArrayList<SearchResult> results=s.getResults();
        Document[] docs=s.getDocuments();
        for(int i=0;i<results.size();i++) {
            System.out.print(i + "\t|\t");
            System.out.print(String.format("%.2f", results.get(i).getDistance()) + "\t|\t");
            for (int j = 0,k=0; j < featureWeights.length; j++) {
                if(featureWeights[j]!=0){
                    System.out.print(String.format("%.2f", results.get(i).getDistanceSet()[k])+" x "+featureWeights[j]+"\t");
                    k++;
                }
            }
            System.out.print("|\t"+docs[i].getField("path").stringValue());
            System.out.println();
        }
        System.out.println();
        System.out.println("Search duration: "+s.getSearchTime()+" ms");
    }
}
