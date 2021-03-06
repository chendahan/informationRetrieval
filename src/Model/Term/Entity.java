package Model.Term;

public class Entity implements ITerm {

    String term;
    int numOfAppearanceInCorpus;
    int numOfAppearanceInDocs;
    String lastDocument;
    double idf;


    public Entity(String term, int numOfAppearanceInCorpus, int numOfAppearanceInDocs, String lastDocument)
    {
        this.term = term.toUpperCase();
        this.numOfAppearanceInCorpus = numOfAppearanceInCorpus;
        this.numOfAppearanceInDocs = numOfAppearanceInDocs;
        this.lastDocument = lastDocument;
    }
    
    //Constructor
    public Entity(String term, int numOfAppearanceInCorpus, int numOfAppearanceInDocs, String lastDocument,double idf)
    {
        this.term = term;
        this.numOfAppearanceInCorpus = numOfAppearanceInCorpus;
        this.numOfAppearanceInDocs = numOfAppearanceInDocs;
        this.lastDocument = lastDocument;
        this.idf = idf;
    }

    public Entity(String term, int numOfAppearanceInCorpus)
    {
        this.term = term.toUpperCase();
        this.numOfAppearanceInCorpus = numOfAppearanceInCorpus;
    }


    @Override
    public void addNumOfAppearanceInCorpus(int amount) {
        this.numOfAppearanceInCorpus = this.numOfAppearanceInCorpus + amount;
    }

    @Override
    public void addNumOfAppearanceInDoc(int amount) {
        this.numOfAppearanceInDocs = this.numOfAppearanceInDocs + amount;
    }

    @Override
    public String getInstance() {
        return "Entity";
    }

    //<editor-fold> des="Getters And Setters"
    @Override
    public String getTerm() {
        return term;
    }

    @Override
    public void setTerm(String term) {
        this.term = term;
    }

    @Override
    public int getNumOfAppearanceInCorpus() {
        return numOfAppearanceInCorpus;
    }

    @Override
    public void setNumOfAppearanceInCorpus(int namOfAppearanceInCorpus) {
        this.numOfAppearanceInCorpus = namOfAppearanceInCorpus;
    }

    @Override
    public int getNumOfAppearanceInDocs() {
        return numOfAppearanceInDocs;
    }

    @Override
    public void setNumOfAppearanceInDocs(int namOfAppearanceInDocs) {
        this.numOfAppearanceInDocs = namOfAppearanceInDocs;
    }

    @Override
    public String getLastDocument() {
        return lastDocument;
    }

    @Override
    public void setLastDocument(String lastDocument) {
        this.lastDocument = lastDocument;
    }
    //</editor-fold>

    @Override
	public double getIdf() {
		return idf;
	}

    @Override
	public void setIdf(double idf) {
		this.idf = idf;
	}
}
