package ViewModel;


import Model.*;
import Model.Term.*;
import com.sun.deploy.util.StringUtils;
import javafx.util.Pair;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Manager {


    //classes
    ReadFile fileReader;
    Indexer indexer;
    WritePostingFile writePostingFile;
    WriteDictionary writeDictionary;
    Parser parser;
    QueryResults queryResults;
    
    Searcher searcher;

    //variables
    String pathForPostingFile;
    String pathForCorpus;
    ArrayList<String> allFiles;
    String[][] sortedDictionary;
    HashMap<String, Document> docsInfo;
    int counterOfDocs = 0;
    boolean stemming;
    double avgDocLen=0;
    boolean showEntity;


    private static final int AMOUNT_OF_DOCS_IN_POSTING_FILE = 25000;

    public Manager() {
        indexer = new Indexer();
        writeDictionary = new WriteDictionary();
    }

    public void run() 
    {
    	int docsCounterIDF=0;
    	long start;
        docsInfo = new HashMap<String, Document>();
        fileReader = new ReadFile(pathForCorpus);
        allFiles = fileReader.getAllFiles();
        
        if (stemming) {
            this.pathForPostingFile = pathForPostingFile + "\\With Stemming";
            new File(pathForPostingFile).mkdirs();
            writePostingFile = new WritePostingFile(pathForPostingFile);
        } else {
            this.pathForPostingFile = pathForPostingFile + "\\Without Stemming";
            File file = new File(pathForPostingFile);
            file.mkdir();
            writePostingFile = new WritePostingFile(pathForPostingFile);
        }
        
        parser = new Parser(pathForCorpus, stemming,false);
        // create a pool of threads, 5 max jobs will execute in parallel
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        //run on all files
        start = System.currentTimeMillis();
        
        for (String file : allFiles) {
            HashMap<String, StringBuilder> allTextsFromTheFile = fileReader.getTextsFromTheFile(new File(file));
            for (String docID : allTextsFromTheFile.keySet()) {
                //parsing each doc
                HashMap<String, ITerm> listOfTerms = parser.parseDoc(allTextsFromTheFile.get(docID).toString(), docID);

                //building temp posting file on ram
                indexer.getPostingFileFromListOfTerms(listOfTerms, docID);

                //make a batch of document in posting file, each batch written to disk
                avgDocLen+=getInfoOnDoc(listOfTerms,docID);
                counterOfDocs++;
                docsCounterIDF++;
                if (counterOfDocs == AMOUNT_OF_DOCS_IN_POSTING_FILE) {
                    indexer.setDictionary((indexer.getDictionary()));
                    counterOfDocs = 0;
                    writePostingFile.putPostingFile(indexer.getPostingFile());
                    threadPool.execute(writePostingFile);
                    indexer.initNewPostingFile();
                }
            }
        }
        //if there is more unwritten posting file
        if (counterOfDocs > 0) {
            writePostingFile.putPostingFile(indexer.getPostingFile());
            threadPool.execute(writePostingFile);
        }
        // once you've submitted your last job to the service it should be shut down
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
        }
        avgDocLen=avgDocLen/(double)docsCounterIDF;
        indexer.calcIDF(docsCounterIDF);


        //update the dictionary for lower and upper letters in terms
        System.out.println("Before Update");
        indexer.setDictionary(updateDictionary(indexer.getDictionary()));
        System.out.println("After Update");
        //writing all the entity we got in corpus and check if the appear more then one time
        writePostingFile.writeTheEntity(indexer.getDictionary());
        //writing the dictionary to disk
        writeDictionary.setPathToWrite(pathForPostingFile, stemming, false);
        writeDictionary.run(indexer.getDictionary());

        //sort the dictionary
        sortByTerms();

        System.out.println("The amount of unique terms: " + indexer.getDictionary().size());
        writeInfoOnDocs();
        //calculate the time for program
        long elapsedTime = System.currentTimeMillis() - start;
        double elapsedTimeD = (double) elapsedTime;
        System.out.println("The time of program: " + (elapsedTimeD / 60000) + " Min");
        writeStopWords(parser.getStopWords());
    }


    public void writeStopWords(StopWords stopWords)
    {
        HashSet<String> stopWord = stopWords.getAllStopWords();
        StringBuilder stopWordsFile = new StringBuilder();
        for (String word : stopWord)
        {
            stopWordsFile.append(word).append("\n");
        }
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathForPostingFile + "\\stopwords.txt"));
            writer.write(stopWordsFile.toString());
            writer.close();
        }catch (Exception e)
        {
            e.toString();
        }

    }
    //<editor-fold des="Help Function For GUI>
    public void setShowEntity(boolean showEntity)
    {
        this.showEntity = showEntity;
    }

    //key = doc ID , lower and upper words ,  remove ! from entity
    //returns amount of words in file
    private int getInfoOnDoc(HashMap<String, ITerm> listOfTerms, String docName) {
        if (listOfTerms != null && listOfTerms.size() > 2) {
            HashMap<String,ITerm> Entity = new HashMap<>();
            int counterAmount = 0;
            String commonTerm = "";
            int maxTerm = 0;
            Iterator<ITerm> it = listOfTerms.values().iterator();
            while (it.hasNext()) {
                ITerm iTerm = ((ITerm) it.next());
                if (iTerm instanceof Entity)
                {
                    Entity.put((iTerm).getTerm().substring(1).toUpperCase(),iTerm);
                }
                counterAmount = counterAmount + iTerm.getNumOfAppearanceInCorpus();
                if (maxTerm < iTerm.getNumOfAppearanceInCorpus())
                {
                    maxTerm = iTerm.getNumOfAppearanceInCorpus();
                    commonTerm = iTerm.getTerm();
                }
            }
            Document document = new Document(listOfTerms.size(),counterAmount,commonTerm,maxTerm);
            document.setMostFivePopularEntities(Entity);
            docsInfo.put(docName,document);
            return counterAmount;
        }
        return 1;
    }

    private HashMap<String, Document> readInfoOnDocs() {
        HashMap<String, Document> hashDoc = new HashMap<String, Document>();
        Document doc;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(pathForPostingFile + "\\InfoOnDocs.txt"));
            String line;
            if ((line = reader.readLine()) != null) {
                this.avgDocLen = Double.parseDouble(line);
            }
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split("#");
                if(splitLine.length == 5)
                {
                    doc = new Document(Integer.parseInt(splitLine[1]), Integer.parseInt(splitLine[2]), splitLine[3], Integer.parseInt(splitLine[4]));
                }
                else
                {
                    doc = new Document(Integer.parseInt(splitLine[1]), Integer.parseInt(splitLine[2]), splitLine[3], Integer.parseInt(splitLine[4]));
                    for (int i = 5 ; i< splitLine.length ; i++)
                    {
                        String[] infoOnEntity = splitLine[i].split("\\$");
                        doc.setMostFivePopularEntities(new Entity(infoOnEntity[0],Integer.parseInt(infoOnEntity[1])));
                    }
                }
                hashDoc.put(splitLine[0], doc);
            }

            reader.close();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return hashDoc;
    }
    

    private void writeInfoOnDocs()
     {
         try{
        	 	BufferedWriter writer = new BufferedWriter(new FileWriter(pathForPostingFile + "\\InfoOnDocs.txt"));
        	 	 writer.write(Double.toString(this.avgDocLen)+"\n");
        	 	Set set = docsInfo.entrySet();
	            Iterator iterator = set.iterator();
	            while(iterator.hasNext()) {
	                Map.Entry entry = (Map.Entry)iterator.next();
	                writer.write(entry.getKey()+"#");
	                //System.out.println(entry.getKey());
                    writer.write(docsInfo.get(entry.getKey().toString()).toString());
	               // System.out.println(docsInfo.get(entry.getKey()));
	            }
	            writer.close();
             	
         }catch (Exception e)
         {
             e.toString();
         }

     }


    /**
     * return sorted dictionary: [0] - the term, [1] - the info on term
     *
     * @return - sorted dictionary in array
     */
    public String[][] getSortedDictionary() {
        sortByTerms();
        return sortedDictionary;
    }

    /**
     * This function is sorting the dictionary
     */
    public void sortByTerms() {
        HashMap<String, ITerm> dictionary = indexer.getDictionary();
        sortedDictionary = new String[indexer.getSizeOfDictionary()][2];
        // TreeMap to store values of HashMap
        TreeMap<String, ITerm> sorted = new TreeMap<>();

        // Copy all data from hashMap into TreeMap
        sorted.putAll(dictionary);

        // Display the TreeMap which is naturally sorted
        int i = 0;
        for (HashMap.Entry<String, ITerm> entry : sorted.entrySet()) {
            if (entry.getValue() instanceof Entity)
            {
                sortedDictionary[i][0] = entry.getKey().toUpperCase();
            }
            else
            {
                sortedDictionary[i][0] = entry.getKey();
            }
            sortedDictionary[i][1] = Integer.toString(entry.getValue().getNumOfAppearanceInCorpus());
            i++;
        }
    }

    /**
     * This function is loading the dictionary to memory
     */
    public void loadDictionary(boolean stemming) {
    	if(writeDictionary.pathToWrite() == null)
    	{
    		writeDictionary.setPathToWrite(pathForPostingFile, stemming, true);
    	}
        if(parser==null)
        {
        	parser= new Parser(writeDictionary.pathToWrite(), stemming,true);
        }
       
        this.pathForPostingFile=writeDictionary.pathToWrite();
        this.docsInfo = readInfoOnDocs();
        indexer.setDictionary(writeDictionary.loadDictionary());
        searcher=new Searcher(parser,indexer.getDictionary(),docsInfo,WritePostingFile.AMOUNT_OF_POSTING_FILES,writeDictionary.pathToWrite(),this.avgDocLen);
    }

    public void searchQueryFromFile(String path,boolean semantic)
   {
    	String query,description;
    	File queryFile=new File(path);
    	HashMap<String, Pair<String, String>> queries;
    	queries=ReadFile.readQueryFile(queryFile);
    	HashMap<String, List<Map.Entry<String, Double>>> rankedres = new HashMap<String, List<Map.Entry<String, Double>>>();///query id to list of ranked resultes
    	for(Map.Entry<String, Pair<String, String>> entry: queries.entrySet())
    	{
    		rankedres.put(entry.getKey(), this.searcher.queryFromFile(entry.getValue().getKey(),entry.getValue().getValue(),semantic));
    	}
       queryResults = new QueryResults(rankedres,pathForPostingFile);
   }

    public String[][] getResultOfQueryInArray()
    {
        return queryResults.getResultOfQueryInArray(docsInfo,showEntity);
    }
    
    public List<Map.Entry<String, Double>> searchQuery(String query,boolean semantic)
    {
        List<Map.Entry<String, Double>> q_Result = this.searcher.queryFromTextBox(query,semantic);
        //queryResults = new QueryResults(q_Result,pathForPostingFile);
        return q_Result;
    }

    public String[][] getResultOfFreeQueryInArray(String query,boolean semantic,int ID)
    {
        List<Map.Entry<String, Double>> q_Result = searchQuery(query,semantic);
        String[][] result = new String[q_Result.size()][3];
        int i=0;
        for (Map.Entry<String, Double> docName : q_Result)
        {
            result[i][0] = Integer.toString(ID);
            result[i][1] = docName.getKey();
            if (docsInfo.containsKey(docName) && docsInfo.get(docName).getMostFiveEntityString() != null && docsInfo.get(docName).getMostFiveEntityString().length()>1)
            {
                result[i][2] = docsInfo.get(docName).getMostFiveEntityString();
            }
            i++;
        }
        return result;
    }

    private HashMap<String, ITerm> updateDictionary(HashMap<String, ITerm> dictionary) {
        HashMap<String, ITerm> updatedDictionary = new HashMap<>();
        Set<String> allTerms = dictionary.keySet();
        for (String term : allTerms) {
            if (term.charAt(0) >= 'A' && term.charAt(0) <= 'Z' && dictionary.containsKey(term.toLowerCase())) {
                ITerm tempPtrLow = dictionary.get(term.toLowerCase());
                ITerm tempPtrUpper = dictionary.get(term.toUpperCase());
                int amountOfAppearance = tempPtrUpper.getNumOfAppearanceInCorpus() + tempPtrLow.getNumOfAppearanceInCorpus();
                int numberOfDocs = tempPtrUpper.getNumOfAppearanceInDocs() + tempPtrLow.getNumOfAppearanceInDocs();
                String instance = tempPtrLow.getInstance();
                if (instance.equals("Number")) {
                    updatedDictionary.put(term.toLowerCase(), new NumberT(term.toLowerCase(), amountOfAppearance, numberOfDocs, tempPtrLow.getLastDocument()));
                }
                //Expression
                else if (instance.equals("Expression")) {
                    updatedDictionary.put(term.toLowerCase(), new Expression(term.toLowerCase(), amountOfAppearance, numberOfDocs, tempPtrLow.getLastDocument()));
                }
                //Term
                else {
                    updatedDictionary.put(term.toLowerCase(), new Term(term.toLowerCase(), amountOfAppearance, numberOfDocs, tempPtrLow.getLastDocument()));
                }
            } else {
                updatedDictionary.put(term, dictionary.get(term));
            }
        }
        return updatedDictionary;
    }

    //</editor-fold>

    //<editor-fold des="Setters">

    /**
     * @param path - where we write the posting file
     */
    public void setPathForPostingFile(String path) {
        this.pathForPostingFile = path;
    }

    /**
     * @param stemming - set the stemming option
     */
    public void setStemming(boolean stemming) {
        this.stemming = stemming;
    }

    /**
     * @param path - from where to read our corpus
     */
    public void setPathForCorpus(String path) {
        this.pathForCorpus = path;
    }
    //</editor-fold>

}
