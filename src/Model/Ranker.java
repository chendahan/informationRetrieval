package Model;

import java.util.*;

public class Ranker {

	
    HashMap<String, Document> docsInfo;
    double k,b; //for bm25
    int M; //number of docs in collection
    
    public Ranker(HashMap<String, Document> _docsInfo)
    {
    	this.docsInfo=_docsInfo;
    	this.M=_docsInfo.size();//docs in collection
    	this.k=0.5;//typically evaluated in the 0 to 3 range,optimal k in a range of 0.5-2.0
    	this.b=0.5;//b needs to be between 0 and 1,optimal b in a range of 0.3-0.9 
    }
    
    //input- query after parse - maps term from query to amount of time he appeared in the query
    //searcherResultes- containing Hash of String - term from query, mapped to Hash of search results :
    //file name and amount of times the term appeared in the file    
    //docFrequency- map between term from query to df
    //returns top 50 ranked by order
	public List<String> rank(HashMap<String,Integer> queryAfterParse, HashMap<String,HashMap<String,Integer>> searcherResultes,HashSet<String> allDocs,HashMap<String,Integer> docFrequency)
	{
		List<String> resultes= new ArrayList<String>();	
		System.out.println("allDocs size: "+ resultes.size());
		
		//need to calc grade for each doc with query words
		Iterator iterDoc = allDocs.iterator();
		
		double docCalc;
		double countWordInDoc;
		String doc;
		while (iterDoc.hasNext())
		{
			//for each doc calc f(d,q)
			docCalc=0;
			doc=iterDoc.next().toString();
			//c(w,q)- words in common between the query and the doc
			for (Map.Entry<String,Integer> word: queryAfterParse.entrySet())//for each word in query
			{
				if (searcherResultes.get((word.getKey())).containsKey(doc))//check if word in doc
				{
					countWordInDoc=searcherResultes.get((word.getKey())).get(doc);//c(w,d)- count word in doc
					
					//word.getValue();//c(w,q)-count word int query
					docCalc+=word.getValue() * (((this.k+1)*countWordInDoc)/(countWordInDoc+this.k)) * Math.log((this.M+1)/docFrequency.get(word.getKey()));
				}
			}
			
			System.out.println("rank for "+doc+" is: "+docCalc);
		}
	
		
		return resultes;
	}

}
