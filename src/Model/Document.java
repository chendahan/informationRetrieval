package Model;

import java.io.Serializable;

public class Document implements Serializable
{
	int numOfUniqueTerms;
	int numOfWords; 
	String mostCommonWord;
	int countMostCommon;
	
	public Document(int _numOfUniqueTerms, int _numOfWords, String _mostCommonWord, int _countMostCommon)
	{
		this.numOfUniqueTerms=_numOfUniqueTerms;
		this.numOfWords=_numOfWords;
		this.mostCommonWord=_mostCommonWord;
		this.countMostCommon=_countMostCommon;
	}
	
    @Override
    public String toString() {
        return numOfUniqueTerms+"#"+numOfWords+"#"+mostCommonWord+"#"+countMostCommon+"\n";
    }

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
	
}
