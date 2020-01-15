package Model.Term;

public interface ITerm {



    public void addNumOfAppearanceInCorpus(int amount);
    public void addNumOfAppearanceInDoc(int amount);
    public String getInstance();

    public String getTerm();
    public int getNumOfAppearanceInCorpus();
    public int getNumOfAppearanceInDocs();
    public String getLastDocument();


    public void setTerm(String term);
    public void setNumOfAppearanceInCorpus(int namOfAppearanceInCorpus);
    public void setNumOfAppearanceInDocs(int namOfAppearanceInDocs);
    public void setLastDocument(String lastDocument);
	public double getIdf();
	public void setIdf(double idf);

}
