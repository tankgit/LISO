package pers.tank.liso.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
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
    private TreeSet<SearchResult> resultTree =new TreeSet<>();
    private ArrayList<SearchResult> results;
    private double searchTime=0;
    private double[] temW;

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
        if(features.length!=featureWeights.length){
            throw new Exception("The size of featureWeights must be matched with features.");
        }
        temW=normalizeWeights(featureWeights);
        int num=0;
        for (double aTemW : temW) {
            if (aTemW != 0) {
                num++;
            }
        }
        this.features=new Feature[num];
        this.featureWeight=new double[num];
        for (int i=0,j=0;i<temW.length;i++){
            if(temW[i]!=0){
                this.features[j]=features[i];
                this.featureWeight[j]=temW[i];
                j++;
            }
        }
    }

    public void setDistanceType(Distance[] distances) throws Exception {
        if(distances.length!=this.temW.length){
            throw new Exception("The size of distances must be matched with features.");
        }
        this.distances=new Distance[features.length];
        for(int i=0,j=0;i<temW.length;i++){
            if(temW[i]!=0){
                this.distances[j]=distances[i];
                j++;
            }
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
            if(featureWeight[i]==0.0){

            }
            dist+=distanceSet[i]*featureWeight[i];
        }
        return dist;
    }

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
        this.searchTime=System.currentTimeMillis();
        resultTree.clear();
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
            if (resultTree.size() < maxHit) {
                if(resultTree.contains(new SearchResult(i,distanceSet,distance))){
                    distance+=0.000001;   //if two result have the same score, the TreeSet can just store one result. So add a tiny number to make it different.
                }
                resultTree.add(new SearchResult(i,distanceSet,distance));
                if (this.maxDist < distance) {
                    this.maxDist = distance;
                }
            } else if (this.maxDist > distance) {
                resultTree.remove(resultTree.last());
                resultTree.add(new SearchResult(i,distanceSet,distance));
                this.maxDist = resultTree.last().getDistance();
            }
        }
        this.results=new ArrayList<>(resultTree.size());
        this.results.addAll(resultTree);
        this.searchTime=System.currentTimeMillis()-this.searchTime;
    }

    private double[] normalizeWeights(double[] featureWeight){
        double sum=0;
        for (double aFeatureWeight : featureWeight) {
            sum += aFeatureWeight;
        }
        for (int i=0;i<featureWeight.length;i++){
            featureWeight[i]=featureWeight[i]/sum;
        }
        return featureWeight;
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
