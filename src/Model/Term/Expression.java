package Model.Term;

public class Expression implements ITerm {

    String term;
    int numOfAppearanceInCorpus;
    int numOfAppearanceInDocs;
    String lastDocument;

    //Constructor
    public Expression(String term, int numOfAppearanceInCorpus, int numOfAppearanceInDocs, String lastDocument)
    {
        this.term = term;
        this.numOfAppearanceInCorpus = numOfAppearanceInCorpus;
        this.numOfAppearanceInDocs = numOfAppearanceInDocs;
        this.lastDocument = lastDocument;
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
        return "Expression";
    }


    //<editor-fold> des="Getters And Setters"
    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getNumOfAppearanceInCorpus() {
        return numOfAppearanceInCorpus;
    }

    public void setNumOfAppearanceInCorpus(int namOfAppearanceInCorpus) {
        this.numOfAppearanceInCorpus = namOfAppearanceInCorpus;
    }

    public int getNumOfAppearanceInDocs() {
        return numOfAppearanceInDocs;
    }

    public void setNumOfAppearanceInDocs(int namOfAppearanceInDocs) {
        this.numOfAppearanceInDocs = namOfAppearanceInDocs;
    }

    public String getLastDocument() {
        return lastDocument;
    }

    public void setLastDocument(String lastDocument) {
        this.lastDocument = lastDocument;
    }
    //</editor-fold>
}
