package Model;

import Model.Term.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public class WriteDictionary {

    String pathToWrite;

    //Constructor
    public WriteDictionary() {
    }

    public String pathToWrite()
    {
        return pathToWrite;
    }
    /**
     * this function write the dictionary to disk
     *
     * @param dictionary - gets the dictionary we got
     */
    public void run(HashMap<String, ITerm> dictionary) {
        StringBuilder dictionaryToWrite = new StringBuilder();
        for (String term : dictionary.keySet()) {
            ITerm termPtr = dictionary.get(term);
            if(term.charAt(0) == '!')
            {
                dictionaryToWrite.append(term.substring(1))
                        .append(":").append(termPtr.getNumOfAppearanceInCorpus())
                        .append(",").append(termPtr.getNumOfAppearanceInDocs())
                        .append(",").append(termPtr.getLastDocument())
                        .append(",").append(termPtr.getInstance())
                        .append("\n");
            }
            else
            {
                dictionaryToWrite.append(term)
                        .append(":").append(termPtr.getNumOfAppearanceInCorpus())
                        .append(",").append(termPtr.getNumOfAppearanceInDocs())
                        .append(",").append(termPtr.getLastDocument())
                        .append(",").append(termPtr.getInstance())
                        .append("\n");
            }
        }
        try {
            File file = new File((pathToWrite + "\\Dictionary.txt"));
            FileWriter writer = new FileWriter(file);
            writer.write(dictionaryToWrite.toString());
            writer.close();
        } catch (Exception e) {
            System.out.println("Problem To Update The File: path12: " + pathToWrite + ", The dictionary");
        }
    }

    /**
     * this function read the dictionary from the disk
     *
     * @return - dictionary
     */
    public HashMap<String, ITerm> loadDictionary() {
        HashMap<String, ITerm> dictionary = new HashMap<>();
        File file = new File((this.pathToWrite + "\\Dictionary.txt"));
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitLine = line.split(":");
                String[] splitInfo = splitLine[1].split(",");
                addToDictionary(splitLine[0],Integer.parseInt(splitInfo[0]),Integer.parseInt(splitInfo[1]),splitInfo[2],splitInfo[3],dictionary);
            }
        } catch (Exception e) {
            e.toString();
        }
        return dictionary;
    }

    private void addToDictionary(String term, int numOfAppearanceInCorpus,int numOfAppearanceInDocs, String lastDoc, String instance , HashMap<String, ITerm> dictionary)
    {
        if (instance.equals("Entity"))
        {
            dictionary.put(term,new Entity(term,numOfAppearanceInCorpus,numOfAppearanceInDocs,lastDoc));
        }
        else if (instance.equals("Number"))
        {
            dictionary.put(term,new NumberT(term,numOfAppearanceInCorpus,numOfAppearanceInDocs,lastDoc));
        }
        //Expression
        else if (instance.equals("Expression"))
        {
            dictionary.put(term,new Expression(term,numOfAppearanceInCorpus,numOfAppearanceInDocs,lastDoc));
        }
        //Term
        else
        {
            dictionary.put(term,new Term(term,numOfAppearanceInCorpus,numOfAppearanceInDocs,lastDoc));
        }
    }


    /**
     * Setters
     *
     * @param path         - path to write / read from
     * @param stemming     - what dictionary we want stemming
     * @param needToChange - if we need to change the path
     */
    public void setPathToWrite(String path, boolean stemming, boolean needToChange) {
        if (stemming && needToChange) {
            this.pathToWrite = path + "\\With Stemming";
        } else if (needToChange) {
            this.pathToWrite = path + "\\Without Stemming";
        } else {
            this.pathToWrite = path;
        }

    }
}
