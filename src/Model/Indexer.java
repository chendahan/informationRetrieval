package Model;

import Model.Term.*;
import javafx.util.Pair;

import java.util.HashMap;

public class Indexer {

    //<editor-fold des="fields">
    /**
     * first string - term.
     * second string - "how many times appeared in the Corpus, in how many docs it was,the last doc where its appeared"
     * [term ; _ , _ , _ ]
     */
    HashMap<String, ITerm> dictionary;

    /**
     * first string - term
     * second string - "DOCID # how many times in that doc"
     * [term ; DOCID # _ , DOCID # _ ...]
     */
    private HashMap<String, HashMap<String, Integer>> postingFile;
    //</editor-fold>


    public Indexer() {
        this.dictionary = new HashMap<>();
        this.postingFile = new HashMap<>();
    }

    /**
     * if the word is in dictionary with big/small letters
     * we will check first if the word is with small letters - and if it does we will change the dictionary and posting to small letters!
     * we will add this word - (with the small letters) to dictionary and check if it is the same document
     */
    public HashMap<String, HashMap<String, Integer>> getPostingFileFromListOfTerms(HashMap<String, ITerm> listOfTerms, String docName) {
        boolean ifTheWordIsWithUpperLetters;
        //String[] splitedDic;
        String originalTermToUpper,originalTermToLower,originalTerm;
        Pair<String,Boolean> updateOrig;
        for (String term : listOfTerms.keySet())
        {
            ifTheWordIsWithUpperLetters = false;
            originalTermToUpper=term.toUpperCase();
            originalTermToLower=term.toLowerCase();

            updateOrig= updateOriginalTerm(term);
            originalTerm=updateOrig.getKey();
            ifTheWordIsWithUpperLetters=updateOrig.getValue();
            Integer updateNumberOfDocuments,updateNumberOfTimesInAllCorpus;
            if (dictionary.containsKey(originalTermToLower))//dictionary contains term in lower case
            {
                ITerm termPointer = dictionary.get(originalTermToLower);
                if (dictionary.get(originalTermToLower).getLastDocument().equals(docName))//dictionary contains term in lower case + the word is with upper case + prev doc containing is this doc
                {  //if its the same document
                    addExistingWordToPostingExistingFile(originalTermToLower,listOfTerms.get(term).getNumOfAppearanceInCorpus(),termPointer.getNumOfAppearanceInCorpus(),termPointer.getNumOfAppearanceInDocs(),docName,termPointer.getInstance());
                }
                else //new doc containing this term
                {
                    addExistingWordToPostingFileNewFile(originalTermToLower,listOfTerms.get(term).getNumOfAppearanceInCorpus(),termPointer.getNumOfAppearanceInCorpus(),termPointer.getNumOfAppearanceInDocs(),docName,termPointer.getInstance());
                }
            }
            else if (dictionary.containsKey(originalTermToUpper)) //dictionary contains term in upper case
            {
                ITerm termPointer = dictionary.get(originalTermToUpper);
                if (ifTheWordIsWithUpperLetters) //we got term with upper case
                {
                    if (dictionary.get(originalTermToUpper).getLastDocument().equals(docName))//dictionary contains term in upper case + the word is with upper case
                    {  //if its the same document
                        addExistingWordToPostingExistingFile(originalTermToUpper,listOfTerms.get(term).getNumOfAppearanceInCorpus(),termPointer.getNumOfAppearanceInCorpus(),termPointer.getNumOfAppearanceInDocs(),docName,termPointer.getInstance());
                    }
                    else //new doc containing this term
                    {
                        addExistingWordToPostingFileNewFile(originalTermToUpper,listOfTerms.get(term).getNumOfAppearanceInCorpus(),termPointer.getNumOfAppearanceInCorpus(),termPointer.getNumOfAppearanceInDocs(),docName,termPointer.getInstance());
                    }
                }
                else  //we got term with lower letters
                {
                    if (postingFile.containsKey(originalTermToUpper))
                    {
                        updateNumberOfTimesInAllCorpus = termPointer.getNumOfAppearanceInCorpus() + listOfTerms.get(term).getNumOfAppearanceInCorpus();
                        HashMap infoDocs = postingFile.get(originalTermToUpper);
                        if (dictionary.get(originalTermToUpper).getLastDocument().equals(docName))
                        {
                            //dictionary.put(originalTermToLower)
                            addToDictionaryByInstance(originalTermToLower,updateNumberOfTimesInAllCorpus,listOfTerms.get(term).getNumOfAppearanceInDocs(),docName,listOfTerms.get(term).getInstance());
                            //dictionary.put(originalTermToLower, updateNumberOfTimesInAllCorpus + "," + splitedDic[1] + "," + docName);
                            int amountBefore = postingFile.get(originalTermToUpper).get(docName);
                            int thisAmount = listOfTerms.get(term).getNumOfAppearanceInCorpus();
                            infoDocs.put(docName, amountBefore + thisAmount);
                            postingFile.put(originalTermToLower, infoDocs);
                        }
                        else
                        {
                            //updateNumberOfDocuments = (Integer.parseInt(splitedDic[1])) + 1;
                            //dictionary.put(originalTermToLower, updateNumberOfTimesInAllCorpus + "," + updateNumberOfDocuments + "," + docName);
                            addToDictionaryByInstance(originalTermToLower,updateNumberOfTimesInAllCorpus,listOfTerms.get(term).getNumOfAppearanceInDocs() + 1,docName,listOfTerms.get(term).getInstance());
                            infoDocs.put(docName, listOfTerms.get(term).getNumOfAppearanceInCorpus());
                            postingFile.put(originalTermToLower, infoDocs);
                        }
                    }
                    else
                    {
                        addToPostingFile(originalTermToLower, docName, listOfTerms.get(term).getNumOfAppearanceInCorpus());
                    }

                    //if the term we got now with lower letters and we got with upper
                    //removing the terms with upper letters from dictionary and posting file
                    postingFile.remove(originalTermToUpper);
                    dictionary.remove(originalTermToUpper);
                }
            }
            else //if we see the term for the first time
            {
                addToDictionary(originalTerm,listOfTerms.get(term).getNumOfAppearanceInCorpus(),docName, listOfTerms.get(term).getInstance());
                // addToDictionaryByInstance(originalTerm, listOfTerms.get(term).getNumOfAppearanceInCorpus(),1 , docName, listOfTerms.get(term).getInstance());
            }
        }

        return postingFile;
    }


    private void addToDictionaryByInstance(String term, int numOfAppearanceInCorpus,int numOfAppearanceInDocs, String lastDoc, String instance)
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

    Pair<String,Boolean> updateOriginalTerm(String originalTerm)
    {
        boolean ifTheWordIsWithUpperLetters=false;
        if (originalTerm.charAt(0) >= 'A' && originalTerm.charAt(0) <= 'Z' && (originalTerm.charAt(0) != '!'))//make all the term with upper letters if the first letter is upper
        {
            originalTerm = originalTerm.toUpperCase();
            ifTheWordIsWithUpperLetters = true;
        }
        else if (originalTerm.charAt(0) >= 'a' && originalTerm.charAt(0) <= 'z' && (originalTerm.charAt(0) != '!'))
        {
            originalTerm = originalTerm.toLowerCase();
        }
        else if (originalTerm.charAt(0) >= '0' && originalTerm.charAt(0) <= '9' && originalTerm.contains("-"))
        {
            //number - word
            int indexOfChar = originalTerm.indexOf('-');
            if (originalTerm.charAt(indexOfChar + 1) >= 'A' || originalTerm.charAt(indexOfChar + 1) <= 'Z') {
                originalTerm = originalTerm.toUpperCase();
            } else {
                originalTerm = originalTerm.toLowerCase();
            }
        }

        return new Pair<String,Boolean>(originalTerm,ifTheWordIsWithUpperLetters);

    }

    void addExistingWordToPostingFileNewFile(String termToAdd, int timesToAdd, int prevCountOfTimes, int prevCountOfDocs,String docName, String instance)
    {
        //if the word in dictionary was with upper/lower letters and we got with upper/lower letters too - new doc
        //int updateNumberOfTimesInAllCorpus = prevCountOfTimes + timesToAdd;
        //int updateNumberOfDocuments = prevCountOfDocs + 1;//new file
        dictionary.get(termToAdd).addNumOfAppearanceInCorpus(timesToAdd);
        dictionary.get(termToAdd).addNumOfAppearanceInDoc(1);
        //addToDictionaryByInstance(termToAdd,updateNumberOfTimesInAllCorpus,updateNumberOfDocuments,docName,instance);
        //dictionary.put(termToAdd, updateNumberOfTimesInAllCorpus + "," + updateNumberOfDocuments + "," + docName);
        if (postingFile.containsKey(termToAdd))
        {
            updatePostingFileIfNewFile(termToAdd, docName, timesToAdd);
        }
        else
        {
            addToPostingFile(termToAdd, docName, timesToAdd);
        }
    }

    void addExistingWordToPostingExistingFile(String termToAdd, int timesToAdd, int prevCountOfTimes, int prevCountOfDocs,String docName, String instance)
    {
        //if the word in dictionary was with upper/lower letters and we got with upper/lower letters too - new doc apearence
        //int updateNumberOfTimesInAllCorpus = prevCountOfTimes + timesToAdd;
        //int updateNumberOfDocuments = prevCountOfDocs;
        dictionary.get(termToAdd).addNumOfAppearanceInCorpus(timesToAdd);
        //addToDictionaryByInstance(termToAdd,updateNumberOfTimesInAllCorpus,updateNumberOfDocuments,docName,instance);
        //dictionary.put(termToAdd, updateNumberOfTimesInAllCorpus + "," + updateNumberOfDocuments + "," + docName);
        if (postingFile.containsKey(termToAdd))
        {
            updatePostingFileIfNewFile(termToAdd, docName, timesToAdd);
        }
        else
        {
            addToPostingFile(termToAdd, docName, timesToAdd);
        }
    }


    //<editor-fold des="adding to Dictionary>

    /**
     * adding new term to dictionary and after that to posting file
     * this function Refers to the situation that we found new term
     *
     * @param term                          - the term we want to add to dictionary and posting file
     * @param howManyTimesAppearedInThisDoc - the number of times that word was in the text
     * @param docName                       - ID of document
     */
    private void addToDictionary(String term, int howManyTimesAppearedInThisDoc, String docName, String instance) {
        // if the term is for the first time in the dictionary
        addToDictionaryByInstance(term,howManyTimesAppearedInThisDoc,1,docName,instance);
        //dictionary.put(term, howManyTimesAppearedInThisDoc + ",1," + docName + ",");
        addToPostingFile(term, docName, howManyTimesAppearedInThisDoc);
    }

    //</editor-fold>

    //<editor-fold des="add or update the posting file"

    /**
     * adding the term for the first time to posting file
     *
     * @param term                          - the term
     * @param docName                       - DocID
     * @param howManyTimesAppearedInThisDoc - the number of times that word was in this document
     */
    private void addToPostingFile(String term, String docName, Integer howManyTimesAppearedInThisDoc) {
        HashMap<String, Integer> temp = new HashMap<>();
        temp.put(docName, howManyTimesAppearedInThisDoc);
        postingFile.put(term, temp);
    }

    /**
     * This function update the posting file - if we got the same term in same document
     * this function Refers to the situation that we got the term in upper and lower letters in the same document
     *
     * @param term                          - term we checking
     * @param docName                       - the doc name we working on
     * @param howManyTimesAppearedInThisDoc - amount of term in document
     */
    private void updatePostingFileIfSameFile(String term, String docName, Integer howManyTimesAppearedInThisDoc) {
        Integer numberOfAppearing = postingFile.get(term).get(docName) + howManyTimesAppearedInThisDoc;
        postingFile.get(term).put(docName, numberOfAppearing);
    }

    /**
     * This function update the posting file - if we got the same term in new document
     * only adding to posting file new doc with amount of appearing in this document
     *
     * @param term                          - term we checking
     * @param docName                       - the doc name we working on
     * @param howManyTimesAppearedInThisDoc - amount of term in document
     */
    private void updatePostingFileIfNewFile(String term, String docName, Integer howManyTimesAppearedInThisDoc) {
        postingFile.get(term).put(docName, howManyTimesAppearedInThisDoc);
    }

    //</editor-fold>

    //<editor-fold des="initialization the posting file">

    /**
     * initialization posting file to free the memory
     */
    public void initNewPostingFile() {
        this.postingFile = new HashMap<>();
    }
    //</editor-fold>

    //<editor-fold des="Getters">

    /**
     * @return - Dictionary
     */
    public HashMap<String, ITerm> getDictionary() {
        return dictionary;
    }

    /**
     * @return - size of the dictionary
     */
    public int getSizeOfDictionary() {
        return dictionary.size();
    }

    /**
     * @return - posting file
     */
    public HashMap<String, HashMap<String, Integer>> getPostingFile() {
        return postingFile;
    }

    /**
     * @return - size of posting file
     */
    public int getSizeOfPostingFile() {
        return postingFile.size();
    }
    //</editor-fold>

    //<editor-fold des="Setters"
    public void setDictionary(HashMap<String, ITerm> dictionary) {
        this.dictionary = new HashMap<>();
        this.dictionary.putAll(dictionary);
    }
    //</editor-fold>
}


//package Model;
//
//import java.util.*;
//
//import javafx.util.Pair;
//
//public class Indexer {
//
//    //<editor-fold des="fields">
//    /**
//     * first string - term.
//     * second string - "how many times appeared in the Corpus, in how many docs it was,the last doc where its appeared"
//     * [term ; _ , _ , _ ]
//     */
//    HashMap<String, String> dictionary;
//
//    /**
//     * first string - term
//     * second string - "DOCID # how many times in that doc"
//     * [term ; DOCID # _ , DOCID # _ ...]
//     */
//    private HashMap<String, HashMap<String, Integer>> postingFile;
//    //</editor-fold>
//
//
//    public Indexer() {
//        this.dictionary = new HashMap<>();
//        this.postingFile = new HashMap<>();
//    }
//
//    /**
//     * if the word is in dictionary with big/small letters
//     * we will check first if the word is with small letters - and if it does we will change the dictionary and posting to small letters!
//     * we will add this word - (with the small letters) to dictionary and check if it is the same document
//     */
//    public HashMap<String, HashMap<String, Integer>> getPostingFileFromListOfTerms(HashMap<String, Integer> listOfTerms, String docName) {
//    	boolean ifTheWordIsWithUpperLetters;
//    	String[] splitedDic;
//    	String originalTermToUpper,originalTermToLower,originalTerm;
//    	Pair<String,Boolean> updateOrig;
//        for (String term : listOfTerms.keySet())
//        {
//            ifTheWordIsWithUpperLetters = false;
//            originalTermToUpper=term.toUpperCase();
//            originalTermToLower=term.toLowerCase();
//
//            updateOrig= updateOriginalTerm(term);
//            originalTerm=updateOrig.getKey();
//            ifTheWordIsWithUpperLetters=updateOrig.getValue();
//            Integer updateNumberOfDocuments,updateNumberOfTimesInAllCorpus;
//            if (dictionary.containsKey(originalTermToLower))//dictionary contains term in lower case
//            {
//                splitedDic = dictionary.get(originalTermToLower).split(",");
//                if (dictionary.get(originalTermToLower).split(",")[2].equals(docName))//dictionary contains term in lower case + the word is with upper case + prev doc containing is this doc
//                {  //if its the same document
//                	addExistingWordToPostingExistingFile(originalTermToLower,listOfTerms.get(term),splitedDic[0],splitedDic[1],docName);
//                }
//                else //new doc containing this term
//                {
//                	addExistingWordToPostingFileNewFile(originalTermToLower,listOfTerms.get(term),splitedDic[0],splitedDic[1],docName);
//                }
//            }
//            else if (dictionary.containsKey(originalTermToUpper)) //dictionary contains term in upper case
//            {
//                splitedDic = dictionary.get(originalTermToUpper).split(",");
//                if (ifTheWordIsWithUpperLetters) //we got term with upper case
//                {
//                    if (dictionary.get(originalTermToUpper).split(",")[2].equals(docName))//dictionary contains term in upper case + the word is with upper case
//                    {  //if its the same document
//                    	addExistingWordToPostingExistingFile(originalTermToUpper,listOfTerms.get(term),splitedDic[0],splitedDic[1],docName);
//                    }
//                    else //new doc containing this term
//                    {
//                    	addExistingWordToPostingFileNewFile(originalTermToUpper,listOfTerms.get(term),splitedDic[0],splitedDic[1],docName);
//                    }
//                }
//                else  //we got term with lower letters
//                {
//                	if (postingFile.containsKey(originalTermToUpper))
//                	{
//                        updateNumberOfTimesInAllCorpus = Integer.parseInt(splitedDic[0]) + listOfTerms.get(term);
//                        HashMap infoDocs = postingFile.get(originalTermToUpper);
//                        if (dictionary.get(originalTermToUpper).split(",")[2].equals(docName))
//                        {
//                            dictionary.put(originalTermToLower, updateNumberOfTimesInAllCorpus + "," + splitedDic[1] + "," + docName);
//                            int amountBefore = postingFile.get(originalTermToUpper).get(docName);
//                            int thisAmount = listOfTerms.get(term);
//                            infoDocs.put(docName, amountBefore + thisAmount);
//                            postingFile.put(originalTermToLower, infoDocs);
//                        }
//                        else
//                        {
//                            updateNumberOfDocuments = (Integer.parseInt(splitedDic[1])) + 1;
//                            dictionary.put(originalTermToLower, updateNumberOfTimesInAllCorpus + "," + updateNumberOfDocuments + "," + docName);
//                            infoDocs.put(docName, listOfTerms.get(term));
//                            postingFile.put(originalTermToLower, infoDocs);
//                        }
//                	}
//                	else
//                	{
//                		addToPostingFile(originalTermToLower, docName, listOfTerms.get(term));
//                	}
//
//                    //if the term we got now with lower letters and we got with upper
//                    //removing the terms with upper letters from dictionary and posting file
//                    postingFile.remove(originalTermToUpper);
//                    dictionary.remove(originalTermToUpper);
//                }
//            }
//            else //if we see the term for the first time
//            {
//                addToDictionary(originalTerm, listOfTerms.get(term), docName);
//            }
//        }
//
//        return postingFile;
//    }
//
//
//    Pair<String,Boolean> updateOriginalTerm(String originalTerm)
//    {
//    	boolean ifTheWordIsWithUpperLetters=false;
//        if (originalTerm.charAt(0) >= 'A' && originalTerm.charAt(0) <= 'Z' && (originalTerm.charAt(0) != '!'))//make all the term with upper letters if the first letter is upper
//        {
//            originalTerm = originalTerm.toUpperCase();
//            ifTheWordIsWithUpperLetters = true;
//        }
//        else if (originalTerm.charAt(0) >= 'a' && originalTerm.charAt(0) <= 'z' && (originalTerm.charAt(0) != '!'))
//        {
//            originalTerm = originalTerm.toLowerCase();
//        }
//        else if (originalTerm.charAt(0) >= '0' && originalTerm.charAt(0) <= '9' && originalTerm.contains("-"))
//        {
//            //number - word
//            int indexOfChar = originalTerm.indexOf('-');
//            if (originalTerm.charAt(indexOfChar + 1) >= 'A' || originalTerm.charAt(indexOfChar + 1) <= 'Z') {
//                originalTerm = originalTerm.toUpperCase();
//            } else {
//                originalTerm = originalTerm.toLowerCase();
//            }
//        }
//
//        return new Pair<String,Boolean>(originalTerm,ifTheWordIsWithUpperLetters);
//
//    }
//
//    void addExistingWordToPostingFileNewFile(String termToAdd, Integer timesToAdd, String prevCountOfTimes, String prevCountOfDocs,String docName)
//    {
//        //if the word in dictionary was with upper/lower letters and we got with upper/lower letters too - new doc
//        Integer updateNumberOfTimesInAllCorpus = Integer.parseInt(prevCountOfTimes) + timesToAdd;
//        Integer updateNumberOfDocuments = (Integer.parseInt(prevCountOfDocs)) + 1;//new file
//        dictionary.put(termToAdd, updateNumberOfTimesInAllCorpus + "," + updateNumberOfDocuments + "," + docName);
//        if (postingFile.containsKey(termToAdd))
//        {
//            updatePostingFileIfNewFile(termToAdd, docName, timesToAdd);
//        }
//        else
//        {
//            addToPostingFile(termToAdd, docName, timesToAdd);
//        }
//    }
//
//    void addExistingWordToPostingExistingFile(String termToAdd, Integer timesToAdd, String prevCountOfTimes, String prevCountOfDocs,String docName)
//    {
//        //if the word in dictionary was with upper/lower letters and we got with upper/lower letters too - new doc apearence
//        Integer updateNumberOfTimesInAllCorpus = Integer.parseInt(prevCountOfTimes) + timesToAdd;
//        Integer updateNumberOfDocuments = (Integer.parseInt(prevCountOfDocs));
//        dictionary.put(termToAdd, updateNumberOfTimesInAllCorpus + "," + updateNumberOfDocuments + "," + docName);
//        if (postingFile.containsKey(termToAdd))
//        {
//            updatePostingFileIfNewFile(termToAdd, docName, timesToAdd);
//        }
//        else
//        {
//            addToPostingFile(termToAdd, docName, timesToAdd);
//        }
//    }
//
//
//    //<editor-fold des="adding to Dictionary>
//
//    /**
//     * adding new term to dictionary and after that to posting file
//     * this function Refers to the situation that we found new term
//     *
//     * @param term                          - the term we want to add to dictionary and posting file
//     * @param howManyTimesAppearedInThisDoc - the number of times that word was in the text
//     * @param docName                       - ID of document
//     */
//    private void addToDictionary(String term, Integer howManyTimesAppearedInThisDoc, String docName) {
//        // if the term is for the first time in the dictionary
//        dictionary.put(term, howManyTimesAppearedInThisDoc + ",1," + docName + ",");
//        addToPostingFile(term, docName, howManyTimesAppearedInThisDoc);
//    }
//
//    //</editor-fold>
//
//    //<editor-fold des="add or update the posting file"
//
//    /**
//     * adding the term for the first time to posting file
//     *
//     * @param term                          - the term
//     * @param docName                       - DocID
//     * @param howManyTimesAppearedInThisDoc - the number of times that word was in this document
//     */
//    private void addToPostingFile(String term, String docName, Integer howManyTimesAppearedInThisDoc) {
//        HashMap<String, Integer> temp = new HashMap<>();
//        temp.put(docName, howManyTimesAppearedInThisDoc);
//        postingFile.put(term, temp);
//    }
//
//    /**
//     * This function update the posting file - if we got the same term in same document
//     * this function Refers to the situation that we got the term in upper and lower letters in the same document
//     *
//     * @param term                          - term we checking
//     * @param docName                       - the doc name we working on
//     * @param howManyTimesAppearedInThisDoc - amount of term in document
//     */
//    private void updatePostingFileIfSameFile(String term, String docName, Integer howManyTimesAppearedInThisDoc) {
//        Integer numberOfAppearing = postingFile.get(term).get(docName) + howManyTimesAppearedInThisDoc;
//        postingFile.get(term).put(docName, numberOfAppearing);
//    }
//
//    /**
//     * This function update the posting file - if we got the same term in new document
//     * only adding to posting file new doc with amount of appearing in this document
//     *
//     * @param term                          - term we checking
//     * @param docName                       - the doc name we working on
//     * @param howManyTimesAppearedInThisDoc - amount of term in document
//     */
//    private void updatePostingFileIfNewFile(String term, String docName, Integer howManyTimesAppearedInThisDoc) {
//        postingFile.get(term).put(docName, howManyTimesAppearedInThisDoc);
//    }
//
//    //</editor-fold>
//
//    //<editor-fold des="initialization the posting file">
//
//    /**
//     * initialization posting file to free the memory
//     */
//    public void initNewPostingFile() {
//        this.postingFile = new HashMap<>();
//    }
//    //</editor-fold>
//
//    //<editor-fold des="Getters">
//
//    /**
//     * @return - Dictionary
//     */
//    public HashMap<String, String> getDictionary() {
//        return dictionary;
//    }
//
//    /**
//     * @return - size of the dictionary
//     */
//    public int getSizeOfDictionary() {
//        return dictionary.size();
//    }
//
//    /**
//     * @return - posting file
//     */
//    public HashMap<String, HashMap<String, Integer>> getPostingFile() {
//        return postingFile;
//    }
//
//    /**
//     * @return - size of posting file
//     */
//    public int getSizeOfPostingFile() {
//        return postingFile.size();
//    }
//    //</editor-fold>
//
//    //<editor-fold des="Setters"
//    public void setDictionary(HashMap<String, String> dictionary) {
//        this.dictionary = dictionary;
//    }
//    //</editor-fold>
//}
