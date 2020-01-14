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
    HashMap<String, Document> _docsInfo;

    public QueryResults(HashMap<String, List<Map.Entry<String, Double>>> resunsorted) {
        try
        {
            int i=0;
            File file = new File("C:\\My Little Project\\results.txt");
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
                    queryResultInArray[i] = new String[]{Integer.toString((i+1)),docs.getKey(),infoOnDocs.get(docs.getKey()).getMostFiveEntityString()};
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
                    queryResultInArray[i] = new String[]{Integer.toString(i),docs.getKey()};
                    i++;
                }
            }
        }
        return queryResultInArray;
    }

}
