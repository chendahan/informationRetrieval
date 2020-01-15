package Model;

import Model.Term.Entity;
import Model.Term.ITerm;

import java.io.Serializable;
import java.util.*;

public class Document implements Serializable
{
	int numOfUniqueTerms;
	int numOfWords; 
	String mostCommonWord;
	int countMostCommon;
	ArrayList<ITerm> mostFivePopularEntities;

    /**
     * constructor
     * @param _numOfUniqueTerms - number of unique terms in this doc
     * @param _numOfWords - number of words in this doc
     * @param _mostCommonWord - the word that appear more the all
     * @param _countMostCommon - how many time the common word is appearing
     */
	public Document(int _numOfUniqueTerms, int _numOfWords, String _mostCommonWord, int _countMostCommon)
	{
		this.numOfUniqueTerms=_numOfUniqueTerms;
		this.numOfWords=_numOfWords;
		this.mostCommonWord=_mostCommonWord;
		this.countMostCommon=_countMostCommon;
		mostFivePopularEntities = new ArrayList<>();

	}

    /**
     * gets a list of entities and update the top 5.
     * @param Entity - list on entities
     */
	public void setMostFivePopularEntities(HashMap<String,ITerm> Entity)
	{
		PriorityQueue<ITerm> mostFivePopularEntitiesQu = new PriorityQueue<ITerm>(5, new Comparator<ITerm>() {
			@Override
			public int compare(ITerm o1, ITerm o2) {
				if ((o1.getNumOfAppearanceInCorpus()) < (o2.getNumOfAppearanceInCorpus()))
				{
					return 1;
				}
				else if(o1.getNumOfAppearanceInCorpus() == o2.getNumOfAppearanceInCorpus())
				{
					return 0;
				}
				else
					return -1;
			}
		});
		Iterator<ITerm> it = Entity.values().iterator();
		while (it.hasNext())
		{
			ITerm iTerm = ((ITerm) it.next());
			mostFivePopularEntitiesQu.add(iTerm);
		}
		for (int i=0; i<5 && i<mostFivePopularEntitiesQu.size() ; i++)
		{
			mostFivePopularEntities.add(mostFivePopularEntitiesQu.poll());
		}
	}

	/**
	 *
	 * @return the Top 4 Entities
	 */
	public String getMostFiveEntityString()
	{
		String st = "";
		int i =0;
		for (ITerm entity : mostFivePopularEntities)
		{
			i++;
			st += entity.getTerm() +": Rank: " +entity.getNumOfAppearanceInCorpus();
			if (i != mostFivePopularEntities.size())
			{
				st += " , ";
			}
		}
		return st;
	}

    @Override
    public String toString() {
	    String st = "";
	    st = st + numOfUniqueTerms+"#"+numOfWords+"#"+mostCommonWord+"#"+countMostCommon;
	    for (ITerm entity : mostFivePopularEntities)
        {
            st = st +  "#" +entity.getTerm().substring(1).toUpperCase() + "$" +entity.getNumOfAppearanceInCorpus();
        }
        st = st + "\n";
        return st;
    }

    //<editor-fold> des="Getters & Setters
	public int getNumOfUniqueTerms() {
		return numOfUniqueTerms;
	}

	public void setNumOfUniqueTerms(int numOfUniqueTerms) {
		this.numOfUniqueTerms = numOfUniqueTerms;
	}

	public int getNumOfWords() {
		return numOfWords;
	}

	public void setNumOfWords(int numOfWords) {
		this.numOfWords = numOfWords;
	}

	public String getMostCommonWord() {
		return mostCommonWord;
	}

	public void setMostCommonWord(String mostCommonWord) {
		this.mostCommonWord = mostCommonWord;
	}

	public int getCountMostCommon() {
		return countMostCommon;
	}

	public void setCountMostCommon(int countMostCommon) {
		this.countMostCommon = countMostCommon;
	}

	public void setMostFivePopularEntities(ITerm entity)
    {
        mostFivePopularEntities.add(entity);
    }
    //</editor-fold>
}
