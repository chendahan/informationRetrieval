package Model;

import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * this model took`s from : https://github.com/medallia/Word2VecJava/blob/master/src/main/java/com/medallia/word2vec/Word2VecModel.java
 */
public class Semantic {

    private Searcher searcher;
    //number of terms that we want the semantic model will return for us
    private static final int similarTermNumber = 3;

    //constructor
    public Semantic() {

        try {
            Word2VecModel word2VecModel = Word2VecModel.fromTextFile(new File("word2vec.c.output.model.txt"));
            searcher = word2VecModel.forSearch();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * this function using jar of Word2Vec Offline model
     * @param term - our term that we want to find similar terms for him
     * @return similar terms for @term
     */
    public HashMap<String,Double> getSimilarTerm(String term)
    {
        HashMap<String,Double> similarTerms  = new HashMap();
        try {
            List<Searcher.Match> matches = this.searcher.getMatches(term, similarTermNumber);
            for(Searcher.Match match: matches)
            {
                similarTerms.put(match.match(),match.distance());
            }
        } catch (Searcher.UnknownWordException e) {
        }
        return similarTerms;
    }
}
