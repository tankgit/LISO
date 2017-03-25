package pers.tank.liso.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import pers.tank.liso.factory.DistanceType;
import pers.tank.liso.factory.FeatureType;
import pers.tank.liso.factory.ImgProcess;
import pers.tank.liso.factory.Transforms;
import pers.tank.liso.factory.distances.Distance;
import pers.tank.liso.factory.features.Feature;
import pers.tank.liso.factory.features.ColorHistogram;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by derek on 3/20/17.
 */
public class Searcher {

    private int maxHit =10;
    private double maxDist=-1;
    private String indexPath;
    private Mat target;
    private byte[][] targetFeatures;
    private int docSize=0;
    private double[] featureWeight;
    private Feature[] features;
    private Distance[] distances;
    private Transforms colorSpace;
    private IndexReader indexReader;
    private ArrayList<SearchResult> results;
    private double searchTime=0;

    public Searcher(String indexPath,Transforms colorSpace) throws IOException {
        this.indexPath=indexPath;
        this.colorSpace=colorSpace;
        loadDocument();
    }

    public Searcher(String indexPath,Transforms colorSpace,int maxHit) throws Exception {
        this(indexPath,colorSpace);
        if(maxHit<=0){
            throw new Exception("maxHit must larger than 0.");
        }
        this.maxHit=maxHit;
    }

    private Mat loadImg(String path){
        return Imgcodecs.imread(path);
    }

    public void setImage(String path){
        this.target=loadImg(path);
    }

    public void setMaxHit(int maxHit) throws Exception {
        if(maxHit<=0){
            throw new Exception("maxHit must larger than 0.");
        }
        this.maxHit=maxHit;
    }

    public void setFeatureType() throws Exception {
        setFeatureType(new Feature[]{new ColorHistogram()},new double[]{1});
    }

    public void setFeatureType(Feature[] features, double[] featureWeights) throws Exception {
        this.features=features;
        if(this.features.length!=featureWeights.length){
            throw new Exception("The size of featureWeights must be matched with features.");
        }
        this.featureWeight=featureWeights;
    }

    public void setDistanceType(Distance[] distances) throws Exception {
        this.distances=distances;
        if(this.distances.length!=this.features.length){
            throw new Exception("The size of distances must be matched with features.");
        }
    }

    private void setTargetFeatures() throws IllegalAccessException, InstantiationException {
        this.targetFeatures=new byte[this.features.length][];
        for (int i=0;i<this.features.length;i++){
            this.features[i].extract(this.target,this.colorSpace);
            this.targetFeatures[i]=this.features[i].getFeature();
        }
    }

    private void loadDocument() throws IOException {
        Directory directory= FSDirectory.open(Paths.get(this.indexPath));
        this.indexReader= DirectoryReader.open(directory);
        this.docSize=this.indexReader.numDocs();
    }

    private double weightedDistance(double[] distanceSet){
        double dist=0;
        for(int i=0;i<distanceSet.length;i++){
            dist+=distanceSet[i]*featureWeight[i];
        }
        return dist;
    }

    //TODO: normalize the distance before set weights;
    public void search() throws Exception {
        if(this.docSize==0){
            throw new Exception("Documents not loaded.");
        }
        if(this.features==null){
            throw new Exception("Features unset.");
        }
        if(this.distances==null){
            throw new Exception("Distances unset");
        }
        this.setTargetFeatures();
        TreeSet<SearchResult> results=new TreeSet<>();
        this.searchTime=System.currentTimeMillis();
        results.clear();
        for(int i=0;i<this.docSize;i++){
            double[] distanceSet=new double[this.features.length];
            for(int j=0;j<this.features.length;j++) {
                byte[] docFeature = this.indexReader.document(i).getField(this.features[j].getClass().getSimpleName()).binaryValue().bytes;
                distanceSet[j] = this.features[j].getDistance(
                        targetFeatures[j],
                        docFeature,
                        this.distances[j]);
            }
            double distance =this.weightedDistance(distanceSet);
            if (results.size() < maxHit) {
                results.add(new SearchResult(i,distanceSet,distance));
                if (this.maxDist < distance) {
                    this.maxDist = distance;
                }
            } else if (this.maxDist > distance) {
                results.remove(results.last());
                results.add(new SearchResult(i,distanceSet,distance));
                this.maxDist = results.last().getDistance();
            }
        }

        this.results=new ArrayList<>(results.size());
        this.results.addAll(results);
        this.searchTime=System.currentTimeMillis()-this.searchTime;
    }

    public double getSearchTime(){
        return this.searchTime;
    }

    public Document[] getDocuments() throws Exception {
        Document[] documents=new Document[this.maxHit];
        for(int i=0;i<this.results.size();i++){
            documents[i]=this.indexReader.document(this.results.get(i).getIndexNum());
        }
        return documents;
    }

    public ArrayList<SearchResult> getResults(){
        return this.results;
    }
}
