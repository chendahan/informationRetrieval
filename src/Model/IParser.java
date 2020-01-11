package Model;



import Model.Term.ITerm;

import java.util.HashMap;

public interface IParser {

    HashMap<String, ITerm> parseDoc(String doc_Text, String doc_Number);

}
