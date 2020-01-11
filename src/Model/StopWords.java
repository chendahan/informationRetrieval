package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;

public class StopWords {

    private HashSet<String> allStopWords;

    public StopWords StopWords(String path) {
        String pathOfStopWords = "";
        File directory = new File(path);
        String[] files = directory.list();
        for (String file : files) {
            if (!(new File(path + "\\" + file).isDirectory())) {
                pathOfStopWords = path + "\\" + file;
            }
        }
        allStopWords = new HashSet<>();
        File file = new File(pathOfStopWords);
        try {
            String st = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((st = br.readLine()) != null) {
                //gets the name of the text
                allStopWords.add(st);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("problem with the reading from the stopwords file!! in: " + file.getPath());
            return null;
        }
        return this;
    }
    
    public boolean IsStopWord(String str)
    {
    	return allStopWords.contains(str.toLowerCase());
    }

    public HashMap<String, Integer> removeStopWords(HashMap<String, Integer> listOfTerms) {
        HashMap<String, Integer> listOfTermsWithOutStopWords = new HashMap<>();
        for (String term : listOfTerms.keySet()) {
            if (!allStopWords.contains(term.toLowerCase())) {
                listOfTermsWithOutStopWords.put(term, listOfTerms.get(term));
            }
        }
        return listOfTermsWithOutStopWords;
//        for (String stopWord : allStopWords)
//        {
//            listOfTerms.remove(stopWord);
//        }
    }

}
