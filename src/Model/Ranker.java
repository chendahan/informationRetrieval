package Model;

import java.util.*;

public class Ranker {

	
    HashMap<String, Document> docsInfo;
    double  avgFileLength;
    double k,b; //for bm25
    int M; //number of docs in collection
    
    public Ranker(HashMap<String, Document> _docsInfo,double avgFileLength)
    {
    	this.docsInfo=_docsInfo;
    	this.M=_docsInfo.size();//docs in collection
    	this.k=0.5;//typically evaluated in the 0 to 3 range,optimal k in a range of 0.5-2.0
    	this.b=0.3;//b needs to be between 0 and 1,optimal b in a range of 0.3-0.9
    	this.avgFileLength=avgFileLength;
    }
    
    //input- query after parse - maps term from query to amount of time he appeared in the query
    //searcherResultes- containing Hash of String - term from query, mapped to Hash of search results :
    //file name and amount of times the term appeared in the file    
    //docFrequency- map between term from query to df
    //returns hashmap with rank
	@SuppressWarnings("unchecked")
	public HashMap<String,Double> rank(HashMap<String,Integer> queryAfterParse, HashMap<String,HashMap<String,Integer>> searcherResultes,HashSet<String> allDocs
			,HashMap<String,Integer> docFrequency,HashMap<String,Double> idfVal)
	{
		HashMap<String,Double> resultes= new HashMap<String,Double>();			
		Iterator iterDoc = allDocs.iterator();
		double idf,denominator=0,counter=0;
		double docCalc,countWordInDoc,denominatorDoc;
		String doc;
		
		System.out.println("allDocs size: "+ allDocs.size());
		
		while (iterDoc.hasNext())//for each doc in results
		{
			docCalc=0;
			doc=iterDoc.next().toString();
			denominatorDoc=this.k*(1-this.b+(this.b*((double)this.docsInfo.get(doc).getNumOfWords()/avgFileLength)));
			
			for (Map.Entry<String,Integer> word: queryAfterParse.entrySet())//for each word in query
			{
				if (searcherResultes.get((word.getKey())).containsKey(doc))//check if word in doc
				{
					idf = idfVal.get(word.getKey());				
					countWordInDoc=searcherResultes.get((word.getKey())).get(doc);//c(w,d)- count word in doc
					counter=countWordInDoc*(this.k+1);
					denominator=countWordInDoc+denominatorDoc; //wordindoc/doclength
					docCalc+=idf*(counter/denominator);
				}
			}
			resultes.put(doc, docCalc);
		}
		
		return resultes;
	}

}
