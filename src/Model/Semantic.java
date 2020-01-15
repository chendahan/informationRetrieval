package Model;

import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Semantic {

    private Searcher searcher;
    private static final int similarTermNumber = 3;

    public Semantic() {

        try {
            Word2VecModel word2VecModel = Word2VecModel.fromTextFile(new File("word2vec.c.output.model.txt"));
            searcher = word2VecModel.forSearch();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

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
