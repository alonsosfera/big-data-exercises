package nearsoft.academy.bigdata.recommendation;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class MovieRecommender {
    private String file;
    private Hashtable<String, Integer> HashProduct = new Hashtable<String, Integer>();
    private Hashtable<String, Integer> HashUser = new Hashtable<String, Integer>();
    private int users =1, products =1, reviews = 0;

    public MovieRecommender(String file) throws IOException{
        this.file = file;
        getData();
    }

    public String getData() throws IOException {
        String thisProduct = null, thisUser = null;
        File result = new File("Result.csv");
        InputStream fileReader = new GZIPInputStream(new FileInputStream(this.file));
        BufferedReader br = new BufferedReader(new InputStreamReader(fileReader));
        FileWriter fileWriter = new FileWriter(result);
        BufferedWriter bw = new BufferedWriter(fileWriter);
        String line;
        String[] sp;
        String key, value;

        while((line = br.readLine()) != null) {
            if (line.length() >= 0) {
                sp = line.split(" ");
                key = sp[0];
                if (key.equals("product/productId:")) {
                    thisProduct = sp[1];
                    if (!HashProduct.containsKey(thisProduct)){
                        HashProduct.put(thisProduct,1);
                        products++;
                    }
                }else if (key.equals("review/userId:")){
                    thisUser = sp[1];
                    if (!HashUser.containsKey(thisUser)){
                        HashUser.put(thisUser,1);
                        users++;
                    }
                }else if (key.equals("review/score:")){
                    String score = sp[1];
                    bw.write(thisUser + "," + thisProduct + "," + score + "\n");
                    reviews ++;
                }
            }
        }
        br.close();
        bw.close();
        return result.getAbsolutePath();


    }

    public int getTotalReviews() {
        return 0;
    }

    public int getTotalProducts() {
        return 0;
    }

    public int getTotalUsers() {
        return 0;
    }

    public List<String> getRecommendationsForUser(String a141HP4LYPWMSR) {
        return null;
    }
}
