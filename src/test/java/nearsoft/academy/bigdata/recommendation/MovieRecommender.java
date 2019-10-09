package nearsoft.academy.bigdata.recommendation;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private HashMap<String, Integer> HashProduct = new HashMap<String, Integer>();
    private HashMap<Integer, String> InvertedHashProduct = new HashMap<Integer, String>();
    private HashMap<String, Integer> HashUser = new HashMap<String, Integer>();
    private int users = 0, products = 0, reviews = 0;

    public MovieRecommender(String file) throws IOException{
        this.file = file;
        getData();
    }

    private void getData() throws IOException {
        int thisProduct =0, thisUser =0;
        Files.deleteIfExists(Paths.get("Result.csv"));
        File result = new File("Result.csv");
        InputStream fileReader = new GZIPInputStream(new FileInputStream(this.file));
        //BufferedReader br = new BufferedReader(new InputStreamReader(fileReader));
        FileWriter fileWriter = new FileWriter(result);
        //BufferedWriter bw = new BufferedWriter(fileWriter);
        String line;
        String[] sp;
        String key, value;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(fileReader));
             BufferedWriter bw = new BufferedWriter(fileWriter)) {
            System.out.println("Reading File: "+this.file);
            while((line = br.readLine()) != null) {
                if (line.length() >= 0) {
                    sp = line.split(":");
                    key = sp[0];
                    if ("product/productId".equals(key)) {
                        value = sp[1].trim();
                        if (!HashProduct.containsKey(value)){
                            HashProduct.put(value,products);
                            InvertedHashProduct.put(products,value);
                            thisProduct = HashProduct.get(value);
                            products++;
                        }else{
                            thisProduct = HashProduct.get(value);
                        }
                    }else if ("review/userId".equals(key)){
                        value = sp[1].trim();
                        if (!HashUser.containsKey(value)){
                            HashUser.put(value, users);
                            thisUser = HashUser.get(value);
                            users++;
                        }else{
                            thisUser = HashUser.get(value);
                        }
                    }else if ("review/score".equals(key)){
                        String score = sp[1].trim();
                        bw.write(thisUser + "," + thisProduct + "," + score + "\n");
                        reviews ++;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getTotalReviews() {
        return reviews;
    }

    public int getTotalProducts() {
        return products;
    }

    public int getTotalUsers() {
        return users;
    }

    public List<String> getRecommendationsForUser(String user) throws IOException, TasteException {
        DataModel model = new FileDataModel(new File("Result.csv"));
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

        int userValue = HashUser.get(user);

        List RecommendedProducts = new ArrayList<String>(3);
        List<RecommendedItem> recommendations = recommender.recommend(userValue,3);
        for (RecommendedItem recommendation : recommendations) {
            RecommendedProducts.add(InvertedHashProduct.get((int)recommendation.getItemID()));
        }
        return RecommendedProducts;
    }
}
