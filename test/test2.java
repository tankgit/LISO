/**
 * Created by derek on 3/23/17.
 */
public class test2 {

    public static void main(String[] args) {
        byte a=(byte)129;
        byte b=(byte)127;
        System.out.println((int)a-(int)b);
    }

    public double getDistance(byte[] src, byte[] dst) throws Exception {
        double mean;
        double[][] A=new double[2][src.length];
        double[] X=new double[src.length];
        for (int i=0;i<src.length;i++){
            mean=(src[i]+dst[i])/2.0;
            A[0][i]=src[i]-mean;
            A[1][i]=dst[i]-mean;
            X[i]=src[i]-dst[i];
        }

        //covariance between src and dst.
        double[][] S=new double[src.length][src.length];
        for(int i=0;i<src.length;i++){
            for (int j=i;j<src.length;j++){
                S[i][j]=(A[0][j]*A[0][i]+A[1][j]*A[1][i])/2;
                if(i!=j){
                    S[j][i]=S[i][j];
                }
            }
        }

        //calculate distance
        double dist=0;
        double XS;
        for (int i=0;i<src.length;i++){
            XS=0.0;
            for (int j=0;j<src.length;j++){
                XS+=X[i]*S[j][i];
            }
            dist+=XS*X[i];
        }
        dist= Math.sqrt(dist);
        return 0;
    }
}
