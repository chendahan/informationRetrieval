package Model.Term;

public class Term implements ITerm {

    String term;
    int numOfAppearanceInCorpus;
    int numOfAppearanceInDocs;
    String lastDocument;
    boolean upperCase;


    //Constructor
    public Term(String term, int numOfAppearanceInCorpus, int numOfAppearanceInDocs, String lastDocument)
    {
        if (term.charAt(0) >= 'A' && term.charAt(0) <= 'Z')
        {
            this.term = term.toUpperCase();
            this.upperCase = true;
        }
        else
        {
            this.term = term.toLowerCase();
            this.upperCase = false;
        }
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
        return "Term";
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

    public boolean isUpperCase() {
        return upperCase;
    }

    public void setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
    }

    public void changeFromUpperToLowerCase()
    {
        if (this.upperCase == true)
            this.upperCase = false;
    }

    public String getLastDocument() {
        return lastDocument;
    }

    public void setLastDocument(String lastDocument) {
        this.lastDocument = lastDocument;
    }
    //</editor-fold>
}
