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

        Feature[] features= new Feature[]{new ColorHistogram()};
        Transforms colorSpace=new Transforms(Transforms.COLOR_RGB);
        Indexer id=new Indexer(
                "imgs",
                "index",
                features,
                colorSpace);
        id.createIndex();

        double[] featureWeights=new double[]{1};
        Distance[] distances=new Distance[]{new HistogramIntersection()};
        Searcher s=new Searcher(
                "index",
                colorSpace,
                40);
        s.setImage("imgs/1.jpg");
        s.setFeatureType(features,featureWeights);
        s.setDistanceType(distances);
        s.search();
        ArrayList<SearchResult> results=s.getResults();
        Document[] docs=s.getDocuments();
        for(int i=0;i<results.size();i++) {
            System.out.print(i + "\t|\t");
            System.out.print(String.format("%.2f", results.get(i).getDistance()) + "\t|\t");
            for (int j = 0; j < featureWeights.length; j++) {
                System.out.print(String.format("%.2f", results.get(i).getDistanceSet()[j])+" x "+featureWeights[j]+"\t");
            }
            System.out.print("|\t"+docs[i].getField("path").stringValue());
            System.out.println();
        }
        System.out.println();
        System.out.println("Search duration: "+s.getSearchTime()+" ms");
    }
}
