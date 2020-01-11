package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
	public Searcher(Parser _parser, HashMap<String, ITerm> _dictionary, HashMap<String, Document> _docsInfo, int _amountOfPostingFiles, String _pathPostingFiles)
	{
		this.parser = _parser;
		this.dictionary = _dictionary;
		this.amountOfPostingFiles=_amountOfPostingFiles;
		this.pathPostingFiles=_pathPostingFiles;
		this.ranker= new Ranker(_docsInfo);
	}

	public  List<String> query(String query)
	{
		HashMap<String,ITerm> words=parser.parseDoc(query, "0");//word,count in query
		HashMap<String,HashMap<String,Integer>> allInfoPostingFile=new HashMap<String,HashMap<String,Integer>>();//word to hash of <file, count>
		String upperCase,lowerCase;
		HashSet<String> allDocs = new HashSet<String>();//all docs in result
		HashMap<String,Integer> docFrequency=new HashMap<String,Integer>();//df for each doc
		HashMap<String,Integer> QueryToWordsAsInDocs= new HashMap<String,Integer>();

		for (Map.Entry<String,ITerm> word: words.entrySet())
		{
			upperCase = word.getKey().toString().toUpperCase();
			lowerCase = word.getKey().toString().toLowerCase();
			System.out.println("searching for:" + word.getKey().toString());
			if(this.dictionary.containsKey(lowerCase))
			{
            	System.out.println("found in lower "+ word.getKey());
            	AddTermInfo(lowerCase,word.getValue(),lowerCase,allDocs,allInfoPostingFile,docFrequency,QueryToWordsAsInDocs);
			}

			if(this.dictionary.containsKey(upperCase))
			{
            	System.out.println("found in upper "+word.getKey());
            	AddTermInfo(lowerCase,word.getValue(),upperCase,allDocs,allInfoPostingFile,docFrequency,QueryToWordsAsInDocs);
			}
		}


		return ranker.rank(QueryToWordsAsInDocs, allInfoPostingFile,allDocs,docFrequency);
	}
	
	public void AddTermInfo(String termLower,ITerm word,String termToSearch,HashSet<String> allDocs,HashMap<String,HashMap<String,Integer>> allInfoPostingFile,HashMap<String,Integer> docFrequency,HashMap<String,Integer> QueryToWordsAsInDocs )
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
	//output:posting list of thst term: <file,tf in the file>
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
