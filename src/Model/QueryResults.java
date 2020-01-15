package Model;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class QueryResults {

    TreeMap<String, List<Map.Entry<String, Double>>> res;
    int rows = 0;
    String writePath;

    //constructor
    public QueryResults(HashMap<String, List<Map.Entry<String, Double>>> resunsorted) {
        int i=0;
        res = new TreeMap<>(resunsorted);
        for (Map.Entry<String, List<Map.Entry<String, Double>>> idQuery: res.entrySet())
        {
            rows += idQuery.getValue().size();
            i++;

        }
    }

    /**
     * write the result format
     * @param resunsorted - HashMap key: query ID ,value: key - Doc ID, value - rank
     * @param postingPath - path where we want to save results
     */
    public void saveResults(HashMap<String, List<Map.Entry<String, Double>>> resunsorted, String postingPath) {
        try
        {
            int i=0;
            File file = new File(postingPath+"\\results.txt");
            FileWriter writer = new FileWriter(file);

            res = new TreeMap<>(resunsorted);

            for (Map.Entry<String, List<Map.Entry<String, Double>>> idQuery: res.entrySet())
            {
                rows += idQuery.getValue().size();
                i++;
                for (Map.Entry<String, Double> docs : idQuery.getValue())
                {
                    writer.write(idQuery.getKey() +" 0 "+docs.getKey()+" 3 42.38 mt\n");
                }

            }
            writer.close();
        }
        catch (Exception e)
        {
            System.out.println(e.toString()+" kkkk");
        }
    }


    /**
     * return the result in 2D array
     * @param infoOnDocs - all info that we got on documents
     * @param showEntity - if we need to show entities = always true
     * @return - 2D array String[i][0] - query ID, String[i][1] - doc ID, String[i][2] - Top 5 Entities
     */
    public String[][] getResultOfQueryInArray(HashMap<String, Document> infoOnDocs,boolean showEntity)
    {
        int col = 2;
        String[][] queryResultInArray = new String[rows][0];
        int i = 0;
        if(showEntity)
        {
            for (Map.Entry<String, List<Map.Entry<String, Double>>> idQuery: res.entrySet())
            {
                for (Map.Entry<String, Double> docs : idQuery.getValue())
                {
                    queryResultInArray[i] = new String[]{idQuery.getKey(),docs.getKey(),infoOnDocs.get(docs.getKey()).getMostFiveEntityString()};
                    i++;
                }

            }
        }
        else
        {
            for (Map.Entry<String, List<Map.Entry<String, Double>>> idQuery: res.entrySet())
            {
                for (Map.Entry<String, Double> docs : idQuery.getValue())
                {
                    queryResultInArray[i] = new String[]{idQuery.getKey(),docs.getKey()};
                    i++;
                }
            }
        }
        return queryResultInArray;
    }

}
