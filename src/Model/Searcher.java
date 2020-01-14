package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import Model.Term.ITerm;

public class Searcher {

    /**
     * first string - term.
     * second string - "how many times appeared in the Corpus, in how many docs it was,the last doc where it appeared"
     * [term ; _ , _ , _ ]
     */
    HashMap<String, ITerm> dictionary;
	Parser parser;
	int amountOfPostingFiles;
	String pathPostingFiles;
	Ranker ranker;

	//ctor- receives the same parser from the doc parsing
	public Searcher(Parser _parser, HashMap<String, ITerm> _dictionary, HashMap<String, Document> _docsInfo, int _amountOfPostingFiles, String _pathPostingFiles,double avgDocLen)
	{
		this.parser = _parser;
		this.dictionary = _dictionary;
		this.amountOfPostingFiles=_amountOfPostingFiles;
		this.pathPostingFiles=_pathPostingFiles;
		this.ranker= new Ranker(_docsInfo,avgDocLen);
	}
	
	public List<Map.Entry<String, Double>> queryFromFile(String query,String description)
	{
		HashMap<String,Double> queryRes=query(query);
		HashMap<String,Double> descriptionRes=query(description);
		HashMap<String,Double> combine=new HashMap<String,Double>();
		
		for (Map.Entry<String,Double> descRes: descriptionRes.entrySet())
		{
			combine.put(descRes.getKey(), descRes.getValue()*0.3);
		}
		for (Map.Entry<String,Double> qRes: queryRes.entrySet())
		{
			if(combine.containsKey(qRes.getKey()))
			{
				combine.put(qRes.getKey(), combine.get(qRes.getKey()) +qRes.getValue()*0.7);
			}
			else
			{
				combine.put(qRes.getKey(), qRes.getValue()*0.7);
			}
		}
		
		return sortHash(combine);
	}


	public List<Map.Entry<String, Double>> queryFromTextBox(String query)
	{
		return sortHash(query(query));
	}

	private HashMap<String,Double> query(String query)
	{
		HashMap<String,ITerm> words=parser.parseDoc(query, "0");//word,count in query
		HashMap<String,HashMap<String,Integer>> allInfoPostingFile=new HashMap<String,HashMap<String,Integer>>();//word to hash of <file, count>
		String upperCase,lowerCase;
		HashSet<String> allDocs = new HashSet<String>();//all docs in result
		HashMap<String,Integer> docFrequency=new HashMap<String,Integer>();//df for each doc
		HashMap<String,Integer> QueryToWordsAsInDocs= new HashMap<String,Integer>();
		HashMap<String,Double> idfVal=new HashMap<String,Double>();//idf for each word in query

		for (Map.Entry<String,ITerm> word: words.entrySet())
		{
			upperCase = word.getKey().toString().toUpperCase();
			lowerCase = word.getKey().toString().toLowerCase();
			System.out.println("searching for:" + word.getKey().toString());
			if(this.dictionary.containsKey(lowerCase))
			{
            	System.out.println("found in lower "+ word.getKey());
            	AddTermInfo(lowerCase,word.getValue(),lowerCase,allDocs,allInfoPostingFile,docFrequency,QueryToWordsAsInDocs);
            	idfVal.put(lowerCase, dictionary.get(lowerCase).getIdf());
			}

			if(this.dictionary.containsKey(upperCase))
			{
            	System.out.println("found in upper "+word.getKey());
            	AddTermInfo(lowerCase,word.getValue(),upperCase,allDocs,allInfoPostingFile,docFrequency,QueryToWordsAsInDocs);
            	idfVal.put(upperCase, dictionary.get(upperCase).getIdf());
			}
		}

		return ranker.rank(QueryToWordsAsInDocs, allInfoPostingFile,allDocs,docFrequency,idfVal);

	}
	
	public List<Map.Entry<String, Double>> sortHash(HashMap<String,Double> hm)
	{
	      // Create a list from elements of HashMap 
        List<Map.Entry<String, Double> > list = 
               new LinkedList<Map.Entry<String, Double> >(hm.entrySet()); 
  
        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<String, Double> >() { 
            public int compare(Map.Entry<String, Double> o1,  
                               Map.Entry<String, Double> o2) 
            { 
                return (o2.getValue()).compareTo(o1.getValue()); 
            } 
        }); 
        
        return list.subList(0, 50);
	}
	
	public void AddTermInfo(String termLower,ITerm word,String termToSearch,HashSet<String> allDocs,HashMap<String,HashMap<String,Integer>> allInfoPostingFile
			,HashMap<String,Integer> docFrequency,HashMap<String,Integer> QueryToWordsAsInDocs )
	{
		Integer termHashCode = (Math.abs((termLower).hashCode() % this.amountOfPostingFiles));
		File tempFile = new File(pathPostingFiles+"\\"+termHashCode.toString()+".txt");
		if (tempFile.exists())
        {
			HashMap<String,Integer> infoPostingFile = getTermFromPostingFile(termToSearch, tempFile,allDocs);//, termsByHashCode.get(termHashCode));
        	if (infoPostingFile == null)
            {
                System.out.println("term in dict but not in files");
            }
            else
            {
            	docFrequency.put(termToSearch,this.dictionary.get(termToSearch).getNumOfAppearanceInDocs() );//word with df
            	QueryToWordsAsInDocs.put(termToSearch, word.getNumOfAppearanceInCorpus());// num of apearances in query
            	allInfoPostingFile.put(termToSearch,infoPostingFile);//term with hes posting list hash
            }
        }
	}

	//input: posting file, term to look in the file
	//output:posting list of that term: <file,tf in the file>
	public HashMap<String,Integer> getTermFromPostingFile(String term, File file, HashSet<String> allDocs)
	{
		try
		{
			HashMap<String,Integer> files=new HashMap<String,Integer>();
	        BufferedReader br = new BufferedReader(new FileReader(file));
	        String line;
	        while ((line = br.readLine()) != null) {
	        	if(!line.isEmpty() && term.charAt(0) ==  line.charAt(0))
	        	{
		            String[] lineSplit = line.split(":");
		            if (term.compareTo(lineSplit[0]) == 0)
		            {
		            	String[] docs = lineSplit[1].split(",");
		            	for (String docsLine :docs)
		            	{
		            		String[] docsSplit = docsLine.split("#");
		            		files.put(docsSplit[0],Integer.parseInt(docsSplit[1]));
		            		allDocs.add(docsSplit[0]);
		            	}
		            	 br.close();
		            	return files;
		            }
	        	}
	        }

	    }
		catch (Exception e)
		{
	        e.toString();
	        return null;
	    }
		return null;
	}



}
