
package Model;

import Model.Term.*;
import javafx.util.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum types
{
	UNDEFINED,
	NUMBER_SMALLER_THAN_1K, // 0<=x<1K
	NUMBER_1K_TO_1M, // 1K<=x<1M
	NUMBER_1M_TO_1B, // 1M<=x<1B
	NUMBER_GREATER_THAN_1B, //x>=1B
	NUMBER_ENDS_WITH_$,
	NUMBER_ENDS_WITH_SIGN_PERCENTAGE,// 123%
	NUMBER_ENDS_WITH_SIGN_$, //23$
	NUMBER_FRACTION // 1/2
}

public class Parser implements IParser{

	StopWords stopWords;
	boolean  stemming;
	Stemmer stemmer;

	//Percentage
	Pattern patternPercent = Pattern.compile("[0-9]{1,}([\\.][0-9]{1,})?([ ]{1})?(percentage|percent|%)",Pattern.CASE_INSENSITIVE);//Number percent//Number percentage

	//Date
	Pattern patternDate = Pattern.compile("([0-3]{1}[0-9]{1}[ ])?(?:Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)( [0-3]{1}[0-9]{3}| [0-2]{1}[0-9]{1})?",Pattern.CASE_INSENSITIVE);//DD Month//Month DD//Month year


	//Price
	Pattern patternDollarSignBillionMillion = Pattern.compile("\\$(\\d{1,3},\\d{3}(,\\d{3})*)(\\.\\d*)?([ ](billion|million))?"
			+ "|\\$\\d+\\.?\\d*([ ](billion|million))?",Pattern.CASE_INSENSITIVE);//$price billion/$price million
	Pattern patternPrice = Pattern.compile("((\\d{1,3},\\d{3}(,\\d{3})*)(\\.\\[0-9]{1,})?|(\\d{1,})(\\.\\d{1,})?)( dollars|m dollars|bn dollars| billion U.S. dollars| million U.S. dollars| trillion U.S. dollars)"
			+"|((\\d{1,3},\\d{3}(,\\d{3})*)|(\\d{1,6}))( (\\d{1,}+\\/+\\d{1,}))?( dollars)",Pattern.CASE_INSENSITIVE);

	//expressions- (Word?)([-])?(number|word)[-](number|word)([-])?(word?)
	Pattern patternExpression= Pattern.compile("([A-Za-z]{2,}[-])?([A-Za-z]{2,}|(\\d{1,3},\\d{3}(,\\d{3})*)(\\.\\d*)?|[0-9]{1,}([\\.][0-9]{1,})?)[-]([A-Za-z]{2,}|(\\d{1,3},\\d{3}(,\\d{3})*)(\\.\\d*)?|[0-9]{1,}([\\.][0-9]{1,})?)([-][A-Za-z]{2,})?"); //Word-word-word/Word-word/Number-word/Word-Number/Number-number
	Pattern patternBetweenNumberAndNumber= Pattern.compile("(between )\\d{1,}( and )\\d{1,}", Pattern.CASE_INSENSITIVE);//Between number and number

	//numbers
	Pattern patternNumbers = Pattern.compile("((\\d{1,3},\\d{3}(,\\d{3})*)|([0-9]{1,}))(([\\.][0-9]{1,})|( (\\d{1,}+\\/+\\d{1,})))?( thousand| million| billion|( )?kg| kilogram)?",Pattern.CASE_INSENSITIVE);//numbers-(with comma\just numbers\with dot) (fractions)? /kg
	//Pattern patternNumbersThoMilBilKg = Pattern.compile("[0-9]{1,}([\\.][0-9]{1,})?( Thousand| Million| Billion|( )?KG|( )?kg|( )?Kg| kilogram)");//numbers and kilograms

	//Names
	Pattern patternEntity= Pattern.compile("[A-Z]{1}[a-z]{1,}[ ][A-Z]{1,}[a-z]{1,}([ |-][A-Z]{1}[a-z]{1,}([ |-][A-Z]{1,}[a-z]{1,})?)?");

	//phone number
	Pattern patternPhoneNumber= Pattern.compile("([(])?[0-9]{3}(-| )[0-9]{3}(-| )?[0-9]{4}([)])?");


	public Parser(String pathForCorpus, boolean _stemming)
	{
		stopWords = new StopWords();
		stopWords= stopWords.StopWords(pathForCorpus);
		stemming=_stemming;
		if(stemming)
		{
			stemmer = new Stemmer();
		}
	}
	//This function parse the input doc_text.
	//The output is a hashMap of all the parsed words:
	//first string is the term,int is the count.
	public HashMap<String,ITerm> parseDoc(String doc_Text, String doc_Number)
	{
		HashMap<String, ITerm> termsHash= new HashMap<>();
		StringBuffer sb1 = new StringBuffer(doc_Text);

		sb1 = parseNames(termsHash,sb1);
		sb1=parsePercent(termsHash, sb1);
		sb1=parseDate(termsHash,sb1);
		sb1=parsePrices(termsHash,sb1);
		sb1=parsePhoneNumber(termsHash,sb1);
		sb1=parseExpressions(termsHash,sb1);
		//sb1=parseKG(termsHash,sb1);
		sb1=parseNumbers(termsHash,sb1);
		parseEndWords(termsHash,sb1);
		//printHash(termsHash);

		return termsHash;
	}

	//parsePhoneNumber- input: stringBuffer of the text and hashMap to update
	//output:the new string buffer without the matches+ updates the the hash with the matched numbers
	//TODO- add as pattern
	StringBuffer parsePhoneNumber(HashMap<String,ITerm> terms_Hash, StringBuffer doc_Text)
	{
		StringBuffer sb1 = new StringBuffer();
		Matcher matcher  = patternPhoneNumber.matcher(doc_Text);
		boolean matched = false;
		while (matcher.find())
		{
			matched = true;
			addToHashNumber(terms_Hash,matcher.group());
			matcher.appendReplacement(sb1, " ");
		}

		if (matched)
		{
			matcher.appendTail(sb1);
			doc_Text.setLength(0);
			return sb1;
		}

		return doc_Text;
	}
	/*
        //parseKG- input: stringBuffer of the text and hashMap to update
        //output:the new string buffer without the matches+ updates the the hash with the matched kilograms
        StringBuffer parseKG(HashMap<String,Integer> terms_Hash, StringBuffer doc_Text)
        {
            StringBuffer sb1 = new StringBuffer() ;
            Matcher matcher  = patternKG.matcher(doc_Text);
            boolean matched=false;
            while (matcher.find())
            {
                matched=true;
                addToHash(terms_Hash,matcher.group());
                matcher.appendReplacement(sb1, "");
            }
            if(matched)
            {
                matcher.appendTail(sb1);
                doc_Text.setLength(0);
                return sb1;
            }

            return doc_Text;
        }
    */
	//parseNames- input: stringBuffer of the text and hashMap to update
	//output StringBuffer without the terms and  updates the the hash with the matched names and entities
	StringBuffer parseNames(HashMap<String,ITerm> terms_Hash, StringBuffer doc_Text)
	{
		StringBuffer sb1 = new StringBuffer() ;
		Matcher matcher  = patternEntity.matcher(doc_Text);
		while (matcher.find())
		{
			addToHashEntity(terms_Hash,"!"+matcher.group());
			matcher.appendReplacement(sb1, " ");
		}
		matcher.appendTail(sb1);

		doc_Text.setLength(0);
		return sb1;
	}

	//parseEndWords- input: stringBuffer of the text and hashMap to update
	//no output, just updates the the hash with the matched names and entities
	void parseEndWords(HashMap<String,ITerm> terms_Hash, StringBuffer doc_Text)
	{
		String[] allWords = doc_Text.toString().trim().replaceAll("\\(|\\)|\\]|\\[|\\\\|\\/|,|:|\\$|\\.|\\+|\\*|-|_|`|!|@|#|%|\\^|\"|&|;|\\?|'s", "").split(" ");
		int allWordsLength = allWords.length;
		if(stemming)
		{
			if(stopWords!=null)
			{
				for(int i = 0; i<allWordsLength; i++)
				{
					if(allWords[i].matches("[A-z]{2,}('([A-z]{1,}))?") && !stopWords.IsStopWord(allWords[i]))
					{
						addToHashTerm(terms_Hash,stemmer.Stemming(allWords[i]));
					}
				}
			}
			else
			{
				for(int i = 0; i<allWordsLength; i++)
				{
					if(allWords[i].matches("[A-z]{2,}('([A-z]{1,}))?"))
					{
						addToHashTerm(terms_Hash,stemmer.Stemming(allWords[i]));
					}
				}
			}
		}
		else
		{
			if(stopWords!=null)
			{
				for(int i = 0; i<allWordsLength; i++)
				{
					if(allWords[i].matches("[A-z]{2,}('([A-z]{1,}))?") && !stopWords.IsStopWord(allWords[i]))
					{
						addToHashTerm(terms_Hash,allWords[i]);
					}
				}
			}
			else
			{
				for(int i = 0; i<allWordsLength; i++)
				{
					if(allWords[i].matches("[A-z]{2,}('([A-z]{1,}))?"))
					{
						addToHashTerm(terms_Hash,allWords[i]);
					}
				}
			}

		}

		doc_Text.setLength(0);
	}

	//printHash- input: hashMap
	//printout the hash values
	void printHash(HashMap<String,Integer> terms_Hash)
	{
		for (String term: terms_Hash.keySet()){
			String key = term.toString();
			System.out.println("term is: "+key+" value is :"+terms_Hash.get(key));
		}
	}

	//parseExpressions- input: stringBuffer of the text and hashMap to update
	//output: the new text without the matches+updates the the hash with the matched expressions
	StringBuffer parseExpressions(HashMap<String,ITerm> terms_Hash, StringBuffer doc_Text)
	{
		StringBuffer sb1 = new StringBuffer(),sb2 = new StringBuffer();

		Matcher matcher=patternExpression.matcher(doc_Text);
		while (matcher.find())
		{
			String[] allWords = matcher.group().toString().split("-");
			for (int i=0; i<allWords.length; i++)
			{
				if(Character.isDigit(allWords[i].charAt(0)) )//adding number too
				{
					addToHashExpression(terms_Hash,allWords[i]);
				}
			}

			addToHashExpression(terms_Hash,matcher.group());
			matcher.appendReplacement(sb1, " ");
		}

		matcher.appendTail(sb1);
		matcher=patternBetweenNumberAndNumber.matcher(sb1);
		while (matcher.find())
		{
			String[] allWords = matcher.group().toString().split(" ");
			addToHashExpression(terms_Hash, matcher.group());
			addToHashExpression(terms_Hash,allWords[1]);
			addToHashExpression(terms_Hash,allWords[3]);
			matcher.appendReplacement(sb1, " ");
		}
		matcher.appendTail(sb2);

		sb1.setLength(0);
		doc_Text.setLength(0);
		return sb2;
	}

	//parseNumbers: input- hash and doc_text
	//output: add the matched numbers to the hash table and returns the new text without the matches
	StringBuffer parseNumbers(HashMap<String,ITerm>terms_Hash, StringBuffer doc_Text)
	{
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		Matcher matcher=patternNumbers.matcher(doc_Text);

		while (matcher.find())//( Thousand| Million| Billion|( )?KG|( )?kg| kilogram)
		{
			String[] allWords = matcher.group().toString().split(" ");
			if(allWords.length == 1)
			{
				if (allWords[0].toLowerCase().contains("kg"))
				{
					addToHashNumber(terms_Hash,allWords[0].substring(0, allWords[0].length()-2)+"kg");//kg
				}
				else
				{
					addpatternNumbersToHash(terms_Hash,allWords[0]);//number
				}

			}
			else if(allWords.length == 2)//n fraction- add as is/n Thousand/n Million/n Billion/n kg/n kilogram
			{
				if(allWords[1].equalsIgnoreCase("thousand"))
				{
					//System.out.println("match Thousand: "+matcher.group());
					addToHashNumber(terms_Hash,allWords[0]+"K");
				}
				else if(allWords[1].equalsIgnoreCase("million"))
				{
					//System.out.println("match Million: "+matcher.group());
					addToHashNumber(terms_Hash,allWords[0]+"M");
				}
				else if (allWords[1].equalsIgnoreCase("billion"))
				{
					//System.out.println("match Billion: "+matcher.group());
					addToHashNumber(terms_Hash,allWords[0]+"B");
				}
				else if (allWords[1].equalsIgnoreCase("kilogram") | allWords[1].equalsIgnoreCase("kg"))//kg|kilogram
				{
					//System.out.println("match kg: "+matcher.group());
					addToHashNumber(terms_Hash,allWords[0]+"kg");
				}
				else//fraction
				{
					//System.out.println("match num: "+matcher.group());
					addToHashNumber(terms_Hash,matcher.group());
				}

			}
			else if(allWords.length == 3)//n fraction Thousand/n fraction Million/n fraction Billion/n fraction KG/n fraction kg/n fraction Kg/n fraction kilogram ??
			{

				if(allWords[1].equalsIgnoreCase("thousand"))
				{
					//System.out.println("match Thousand: "+matcher.group());
					addToHashNumber(terms_Hash,allWords[0]+" "+allWords[1]+"K");
				}
				else if(allWords[1].equalsIgnoreCase("million"))
				{
					//System.out.println("match Million: "+matcher.group());
					addToHashNumber(terms_Hash,allWords[0]+" "+allWords[1]+"M");
				}
				else if (allWords[1].equalsIgnoreCase("billion"))
				{
					//System.out.println("match Billion: "+matcher.group());
					addToHashNumber(terms_Hash,allWords[0]+" "+allWords[1]+"B");
				}
				else if (allWords[1].equalsIgnoreCase("kilogram") | allWords[1].equalsIgnoreCase("kg"))//kg|kilogram
				{
					//System.out.println("match kg: "+matcher.group());
					addToHashNumber(terms_Hash,allWords[0]+" "+allWords[1]+"kg");
				}
			}
			matcher.appendReplacement(sb2, " ");
		}
		matcher.appendTail(sb2);

		sb1.setLength(0);
		doc_Text.setLength(0);

		return sb2;
	}

	//addpatternNumbersToHash- input: the matched term and hashMap to update
	//output: add the match to the hash according to the instruction
	void addpatternNumbersToHash(HashMap<String,ITerm> termsHash,String match )
	{
		Pair<types,Float> pair = checkNumSize(match);
		types type=pair.getKey();
		Float num=pair.getValue();
		match= upToThreeNubersAfterDot(match);
		if( type == types.NUMBER_SMALLER_THAN_1K)
		{
			addToHashNumber(termsHash,match);
		}
		else if(type == types.NUMBER_1K_TO_1M )
		{
			match=removeComma(match);
			BigDecimal number= new BigDecimal(match);
			number=number.divide(new BigDecimal(1000)).setScale(3, RoundingMode.DOWN);
			addToHashNumber(termsHash,number.toString()+"K");

		}
		else if(type == types.NUMBER_1M_TO_1B)
		{
			num=num/1000000;
			addToHashNumber(termsHash,String.format("%.3f", num)+"M");
		}
		else if(type == types.NUMBER_GREATER_THAN_1B)
		{
			num=num/1000000000;
			addToHashNumber(termsHash,String.format("%.3f", num)+"B");
		}
	}

	//addpatternNumbersCommasToHash- input: the matched term and hashMap to update
	//output: add the match to the hash according to the instruction
	void addpatternNumbersCommasToHash(HashMap<String,ITerm> termsHash,String match )
	{
		addpatternNumbersToHash(termsHash,match);
	}

	float getNumAsFloat(String num)
	{
		if(num.contains(","))
			num=removeComma(num);

		return Float.parseFloat(num);
	}


	//parsePrices- input: stringBuffer of the text and hashMap to update
	//output:the new string buffer without the matches+ updates the the hash with the matched prices
	StringBuffer parsePrices(HashMap<String,ITerm> terms_Hash, StringBuffer doc_Text)
	{
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();

		Matcher matcher  = patternDollarSignBillionMillion.matcher(doc_Text);//$price (million/bilion)?
		while (matcher.find())
		{
			String match= matcher.group();
			String[] allWords = matcher.group().toString().split(" ");

			if(allWords.length==1)
			{
				addpatternDollarSignToHash(terms_Hash, match.substring(1,match.length()));
			}
			else if(allWords[1].equalsIgnoreCase("million"))
			{
				addToHashNumber(terms_Hash,allWords[0].substring(1)+" M Dollars");
			}
			else//billion
			{
				addToHashNumber(terms_Hash,(int)(getNumAsFloat(allWords[0].substring(1))*1000)+" M Dollars");
			}
			matcher.appendReplacement(sb1, " ");
		}

		matcher.appendTail(sb1);

		matcher  = patternPrice.matcher(sb1);//Price fraction Dollars
		while (matcher.find())
		{
			String[] allWords = matcher.group().toString().split(" ");
			String num= removeComma(allWords[0]);
			if(allWords.length== 4)//Price billion U.S. dollars/Price million U.S. dollars/Price trillion U.S. dollars
			{
				switch (allWords[1])
				{
					case "billion":
						addToHashNumber(terms_Hash,num+"000 M Dollars");
						break;
					case "million":
						addToHashNumber(terms_Hash,allWords[0]+" M Dollars");
						break;
					case "trillion":
						addToHashNumber(terms_Hash,num+"000000 M Dollars");
						break;
				}
			}
			else if(allWords.length== 3)//Price fraction Dollars
			{
				if( checkNumSize(allWords[0]).getKey() == types.NUMBER_1K_TO_1M ||checkNumSize(allWords[0]).getKey() == types.NUMBER_SMALLER_THAN_1K )
				{
					addToHashNumber(terms_Hash,matcher.group());
				}
			}
			else if(allWords.length== 2)//Price[m] Dollars/Price[bn] Dollars/Price Dollars(more+less than million dollar )
			{
				if(allWords[0].toLowerCase().endsWith("m"))
				{
					addpatternMDollarToHash(terms_Hash,allWords[0]);
				}
				else if(allWords[0].toLowerCase().endsWith("bn"))
				{
					addpatternBnDollarToHash(terms_Hash,allWords[0]);
				}
				else
				{
					addpatternDollarSignToHash(terms_Hash,allWords[0]);
				}
			}
			matcher.appendReplacement(sb2, " ");
		}
		matcher.appendTail(sb2);

		sb1.setLength(0);
		doc_Text.setLength(0);

		return sb2;
	}

	//addpatternDollarSignToHash- input: input:string number and hashMap to update
	//output: add the match to the hash according to the instruction($price -> above 1M- save as price M Dollars, else save as price Dollars)
	void addpatternDollarSignToHash(HashMap<String,ITerm> termsHash,String match )
	{
		Pair<types,Float> pair = checkNumSize(match);
		types type=pair.getKey();
		float num=pair.getValue();
		if( type == types.NUMBER_GREATER_THAN_1B || type == types.NUMBER_1M_TO_1B)
		{
			addToHashNumber(termsHash,(int)(num/1000000)+" M Dollars");
		}
		else if(type == types.NUMBER_1K_TO_1M || type == types.NUMBER_SMALLER_THAN_1K)
		{
			addToHashNumber(termsHash,match+" Dollars");
		}
	}

	//addpatternMDollarToHash- input: the match (just the number+m) term and hashMap to update
	//output: add the match to the hash according to the instruction(Price m Dollars --> Price M Dollars)
	void addpatternMDollarToHash(HashMap<String,ITerm> termsHash,String match )
	{
		addToHashNumber(termsHash,match.substring(0,match.length()-1)+" M Dollars");
	}

	//addpatternBnDollarToHash- input: the matched term and hashMap to update
	//output: add the match to the hash according to the instruction(Price bn Dollars)
	void addpatternBnDollarToHash(HashMap<String,ITerm> termsHash,String match )
	{
		addToHashNumber(termsHash,match.substring(0,match.length()-2)+"000 M Dollars");
	}

	String removeComma(String num)
	{
		StringBuffer sb1 = new StringBuffer();

		Matcher matcher  = Pattern.compile("[,]").matcher(num);
		while (matcher.find())
		{
			matcher.appendReplacement(sb1, "");
		}
		matcher.appendTail(sb1);

		return sb1.toString();
	}

	String upToThreeNubersAfterDot(String num)
	{
		if(Pattern.matches("[0-9]{1,}[.][0-9]{4,}",num))
		{
			int idx=num.indexOf(".");
			return num.subSequence(0, idx+3).toString();
		}

		return num;
	}

	//input : hash and text
	//looking for a Percent match, adding the matches to the hashMap according to instructions
	StringBuffer parsePercent(HashMap<String,ITerm> terms_Hash, StringBuffer doc_Text)
	{
		StringBuffer sb1 = new StringBuffer();

		Matcher matcher  = patternPercent.matcher(doc_Text);
		while (matcher.find())
		{
			int space= (matcher.group().contains(" ") == true)? 1:0;
			if(matcher.group().endsWith("%"))//ends with %
			{
				addToHashNumber(terms_Hash, matcher.group().substring(0, matcher.group().length()-space-1)+"%");
			}
			else if(matcher.group().toLowerCase().endsWith("percent"))//percent
			{
				addToHashNumber(terms_Hash, matcher.group().substring(0, matcher.group().length()-space-"percent".length())+"%");
			}
			else//Percentage
			{
				addToHashNumber(terms_Hash, matcher.group().substring(0,matcher.group().length()-space-"Percentage".length())+"%");
			}
			matcher.appendReplacement(sb1, " ");
		}
		matcher.appendTail(sb1);

		doc_Text.setLength(0);

		return sb1;
	}

	//input : hash and text
	//looking for a Date match, adding the matches to the hashMap
	StringBuffer parseDate(HashMap<String,ITerm> terms_Hash, StringBuffer doc_Text)
	{
		StringBuffer sb1 = new StringBuffer();

		Matcher matcher  = patternDate.matcher(doc_Text);
		while (matcher.find())
		{
			String[] allWords = matcher.group().toString().split(" ");
			if(allWords.length>1)
			{
				if(allWords.length==2 && allWords[0].length()==2 || (allWords.length==3 && allWords[2].length()<4 ))//DD Month
				{
					addToHashNumber(terms_Hash,MonthToNum(allWords[1])+"-"+allWords[0]);
				}
				else if(allWords[1].length()==2)//Month DD
				{
					addToHashNumber(terms_Hash,MonthToNum(allWords[0])+"-"+allWords[1]);
				}
				else if(allWords.length==2) //Month Year
				{
					addToHashNumber(terms_Hash,allWords[1].substring(0, 4)+"-"+MonthToNum(allWords[0]));
				}
				else //full date- DD MM YY
				{
					String month=MonthToNum(allWords[1]);
					addToHashNumber(terms_Hash,month+"-"+allWords[0]);
					addToHashNumber(terms_Hash,allWords[2].substring(0,4)+"-" +month);
				}
				matcher.appendReplacement(sb1, " ");
			}

		}

		matcher.appendTail(sb1);

		doc_Text.setLength(0);

		return sb1;
	}

	String MonthToNum(String month)
	{
		switch (month.toLowerCase())
		{
			case "jan":
			case "january":
				return "01";
			case "feb":
			case "february":
				return "02";
			case "mar":
			case "march":
				return "03";
			case "apr":
			case "april":
				return "04";
			case "may":
				return "05";
			case "jun":
			case "june":
				return "06";
			case "jul":
			case "july":
				return "07";
			case "aug":
			case "august":
				return "08";
			case "sep":
			case "september":
				return "09";
			case "oct":
			case "october":
				return "10";
			case "nov":
			case "november":
				return "11";
			case "dec":
			case "december":
				return "12";
		}

		return "00";
	}

	//input- string number
	//output- pair- the number as a float and hes type from the enum
	private Pair<types,Float> checkNumSize(String number)
	{
		types ret= types.UNDEFINED;
		float num=0;
		if(number.contains(","))
			number=removeComma(number);

		try
		{
			num=Float.parseFloat(number);

			if(num<1000)
			{
				ret= types.NUMBER_SMALLER_THAN_1K;
			}
			else if(num<1000000)
			{
				ret= types.NUMBER_1K_TO_1M;
			}
			else if(num<1000000000)
			{
				ret= types.NUMBER_1M_TO_1B;
			}
			else
			{
				ret= types.NUMBER_GREATER_THAN_1B;
			}
		}
		catch (NumberFormatException e)
		{
			System.out.println("ERROR:checkNumSize - " + number);
		}

		return 	new Pair<types,Float>(ret,num);
	}

	//<editor-fold> des="Add To Hash Table By Type"
	void addToHashEntity(HashMap<String,ITerm> termsHash, String term )
	{
		Entity temp = new Entity(term,1,1,"");
		if(!termsHash.containsKey(temp.getTerm()))
		{
			//termsHash.put(term, new Entity(term,1,1,""));
			termsHash.put(temp.getTerm(),temp);
		}
		else
		{
			termsHash.get(temp.getTerm()).addNumOfAppearanceInCorpus(1);
		}
	}
	void addToHashExpression(HashMap<String,ITerm> termsHash, String term )
	{
		Expression temp = new Expression(term,1,1,"");
		if(!termsHash.containsKey(temp.getTerm()))
		{
			//termsHash.put(term, new Expression(term,1,1,""));
			termsHash.put(temp.getTerm(),temp);
		}
		else
		{
			termsHash.get(temp.getTerm()).addNumOfAppearanceInCorpus(1);
		}
	}
	void addToHashNumber(HashMap<String,ITerm> termsHash, String term )
	{
		NumberT temp = new NumberT(term,1,1,"");
		if(!termsHash.containsKey(temp.getTerm()))
		{
			//termsHash.put(term, new Number(term,1,1,""));
			termsHash.put(temp.getTerm(),temp);
		}
		else
		{
			termsHash.get(temp.getTerm()).addNumOfAppearanceInCorpus(1);
		}
	}

	void addToHashTerm(HashMap<String,ITerm> termsHash, String term )
	{
		Term temp = new Term(term,1,1,"");
		if(!termsHash.containsKey(temp.getTerm()))
		{
			termsHash.put(temp.getTerm(),temp);
		}
		else
		{
			termsHash.get(temp.getTerm()).addNumOfAppearanceInCorpus(1);
		}
	}
	//</editor-fold>
}


//
//package Model;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.text.DecimalFormat;
//import java.util.regex.Pattern;
//
//import javafx.util.Pair;
//
//import java.util.HashMap;
//import java.util.regex.Matcher;
//
//enum types
//{
//	UNDEFINED,
//	NUMBER_SMALLER_THAN_1K, // 0<=x<1K
//	NUMBER_1K_TO_1M, // 1K<=x<1M
//	NUMBER_1M_TO_1B, // 1M<=x<1B
//	NUMBER_GREATER_THAN_1B, //x>=1B
//	NUMBER_ENDS_WITH_$,
//	NUMBER_ENDS_WITH_SIGN_PERCENTAGE,// 123%
//	NUMBER_ENDS_WITH_SIGN_$, //23$
//	NUMBER_FRACTION // 1/2
//}
//
//public class Parser implements IParser{
//
//    StopWords stopWords;
//    boolean  stemming;
//    Stemmer stemmer;
//
//	//Percentage
//	Pattern patternPercent = Pattern.compile("[0-9]{1,}([\\.][0-9]{1,})?([ ]{1})?(percentage|percent|%)",Pattern.CASE_INSENSITIVE);//Number percent//Number percentage
//
//	//Date
//	Pattern patternDate = Pattern.compile("([0-3]{1}[0-9]{1}[ ])?(?:Jan(?:uary)?|Feb(?:ruary)?|Mar(?:ch)?|Apr(?:il)?|May|Jun(?:e)?|Jul(?:y)?|Aug(?:ust)?|Sep(?:tember)?|Oct(?:ober)?|Nov(?:ember)?|Dec(?:ember)?)( [0-3]{1}[0-9]{3}| [0-2]{1}[0-9]{1})?",Pattern.CASE_INSENSITIVE);//DD Month//Month DD//Month year
//
//
//	//Price
//	Pattern patternDollarSignBillionMillion = Pattern.compile("\\$(\\d{1,3},\\d{3}(,\\d{3})*)(\\.\\d*)?([ ](billion|million))?"
//			+ "|\\$\\d+\\.?\\d*([ ](billion|million))?",Pattern.CASE_INSENSITIVE);//$price billion/$price million
//	Pattern patternPrice = Pattern.compile("((\\d{1,3},\\d{3}(,\\d{3})*)(\\.\\[0-9]{1,})?|(\\d{1,})(\\.\\d{1,})?)( dollars|m dollars|bn dollars| billion U.S. dollars| million U.S. dollars| trillion U.S. dollars)"
//			+"|((\\d{1,3},\\d{3}(,\\d{3})*)|(\\d{1,6}))( (\\d{1,}+\\/+\\d{1,}))?( dollars)",Pattern.CASE_INSENSITIVE);
//
//	//expressions- (Word?)([-])?(number|word)[-](number|word)([-])?(word?)
//	Pattern patternExpression= Pattern.compile("([A-Za-z]{2,}[-])?([A-Za-z]{2,}|(\\d{1,3},\\d{3}(,\\d{3})*)(\\.\\d*)?|[0-9]{1,}([\\.][0-9]{1,})?)[-]([A-Za-z]{2,}|(\\d{1,3},\\d{3}(,\\d{3})*)(\\.\\d*)?|[0-9]{1,}([\\.][0-9]{1,})?)([-][A-Za-z]{2,})?"); //Word-word-word/Word-word/Number-word/Word-Number/Number-number
//	Pattern patternBetweenNumberAndNumber= Pattern.compile("(between )\\d{1,}( and )\\d{1,}", Pattern.CASE_INSENSITIVE);//Between number and number
//
//	//numbers
//	Pattern patternNumbers = Pattern.compile("((\\d{1,3},\\d{3}(,\\d{3})*)|([0-9]{1,}))(([\\.][0-9]{1,})|( (\\d{1,}+\\/+\\d{1,})))?( thousand| million| billion|( )?kg| kilogram)?",Pattern.CASE_INSENSITIVE);//numbers-(with comma\just numbers\with dot) (fractions)? /kg
//	//Pattern patternNumbersThoMilBilKg = Pattern.compile("[0-9]{1,}([\\.][0-9]{1,})?( Thousand| Million| Billion|( )?KG|( )?kg|( )?Kg| kilogram)");//numbers and kilograms
//
//	//Names
//	Pattern patternEntity= Pattern.compile("[A-Z]{1}[a-z]{1,}[ ][A-Z]{1,}[a-z]{1,}([ |-][A-Z]{1}[a-z]{1,}([ |-][A-Z]{1,}[a-z]{1,})?)?");
//
//	//phone number
//	Pattern patternPhoneNumber= Pattern.compile("([(])?[0-9]{3}(-| )[0-9]{3}(-| )?[0-9]{4}([)])?");
//
//
//	public Parser(String pathForCorpus, boolean _stemming)
//	{
//		stopWords = new StopWords();
//		stopWords= stopWords.StopWords(pathForCorpus);
//		stemming=_stemming;
//		if(stemming)
//		{
//			stemmer = new Stemmer();
//		}
//	}
//	//This function parse the input doc_text.
//	//The output is a hashMap of all the parsed words:
//	//first string is the term,int is the count.
//	public HashMap<String,Integer> parseDoc(String doc_Text, String doc_Number)
//	{
//		HashMap<String,Integer> termsHash= new HashMap<>();
//		StringBuffer sb1 = new StringBuffer(doc_Text);
//
//		sb1 = parseNames(termsHash,sb1);
//		sb1=parsePercent(termsHash, sb1);
//		sb1=parseDate(termsHash,sb1);
//		sb1=parsePrices(termsHash,sb1);
//		sb1=parsePhoneNumber(termsHash,sb1);
//		sb1=parseExpressions(termsHash,sb1);
//		//sb1=parseKG(termsHash,sb1);
//		sb1=parseNumbers(termsHash,sb1);
//		parseEndWords(termsHash,sb1);
//		//printHash(termsHash);
//
//		return termsHash;
//	}
//
//	//parsePhoneNumber- input: stringBuffer of the text and hashMap to update
//	//output:the new string buffer without the matches+ updates the the hash with the matched numbers
//	//TODO- add as pattern
//	StringBuffer parsePhoneNumber(HashMap<String,Integer> terms_Hash, StringBuffer doc_Text)
//	{
//		StringBuffer sb1 = new StringBuffer();
//		Matcher matcher  = patternPhoneNumber.matcher(doc_Text);
//		boolean matched = false;
//		while (matcher.find())
//		{
//			matched = true;
//			addToHash(terms_Hash,matcher.group());
//			matcher.appendReplacement(sb1, " ");
//		}
//
//		if (matched)
//		{
//			matcher.appendTail(sb1);
//			doc_Text.setLength(0);
//			return sb1;
//		}
//
//		return doc_Text;
//	}
///*
//	//parseKG- input: stringBuffer of the text and hashMap to update
//	//output:the new string buffer without the matches+ updates the the hash with the matched kilograms
//	StringBuffer parseKG(HashMap<String,Integer> terms_Hash, StringBuffer doc_Text)
//	{
//		StringBuffer sb1 = new StringBuffer() ;
//		Matcher matcher  = patternKG.matcher(doc_Text);
//		boolean matched=false;
//		while (matcher.find())
//		{
//			matched=true;
//			addToHash(terms_Hash,matcher.group());
//			matcher.appendReplacement(sb1, "");
//		}
//		if(matched)
//		{
//			matcher.appendTail(sb1);
//			doc_Text.setLength(0);
//			return sb1;
//		}
//
//		return doc_Text;
//	}
//*/
//	//parseNames- input: stringBuffer of the text and hashMap to update
//	//output StringBuffer without the terms and  updates the the hash with the matched names and entities
//	StringBuffer parseNames(HashMap<String,Integer> terms_Hash, StringBuffer doc_Text)
//	{
//		StringBuffer sb1 = new StringBuffer() ;
//		Matcher matcher  = patternEntity.matcher(doc_Text);
//		while (matcher.find())
//		{
//			addToHash(terms_Hash,"!"+matcher.group());
//			matcher.appendReplacement(sb1, " ");
//		}
//		matcher.appendTail(sb1);
//
//		doc_Text.setLength(0);
//		return sb1;
//	}
//
//	//parseEndWords- input: stringBuffer of the text and hashMap to update
//	//no output, just updates the the hash with the matched names and entities
//	void parseEndWords(HashMap<String,Integer> terms_Hash, StringBuffer doc_Text)
//	{
//		String[] allWords = doc_Text.toString().trim().replaceAll("\\(|\\)|\\]|\\[|\\\\|\\/|,|:|\\$|\\.|\\+|\\*|-|_|`|!|@|#|%|\\^|\"|&|;|\\?|'s", "").split(" ");
//		int allWordsLength = allWords.length;
//		if(stemming)
//		{
//			if(stopWords!=null)
//			{
//				for(int i = 0; i<allWordsLength; i++)
//				{
//					if(allWords[i].matches("[A-z]{2,}('([A-z]{1,}))?") && !stopWords.IsStopWord(allWords[i]))
//					{
//						addToHash(terms_Hash,stemmer.Stemming(allWords[i]));
//					}
//				}
//			}
//			else
//			{
//				for(int i = 0; i<allWordsLength; i++)
//				{
//					if(allWords[i].matches("[A-z]{2,}('([A-z]{1,}))?"))
//					{
//						addToHash(terms_Hash,stemmer.Stemming(allWords[i]));
//					}
//				}
//			}
//		}
//		else
//		{
//			if(stopWords!=null)
//			{
//				for(int i = 0; i<allWordsLength; i++)
//				{
//					if(allWords[i].matches("[A-z]{2,}('([A-z]{1,}))?") && !stopWords.IsStopWord(allWords[i]))
//					{
//						addToHash(terms_Hash,allWords[i]);
//					}
//				}
//			}
//			else
//			{
//				for(int i = 0; i<allWordsLength; i++)
//				{
//					if(allWords[i].matches("[A-z]{2,}('([A-z]{1,}))?"))
//					{
//						addToHash(terms_Hash,allWords[i]);
//					}
//				}
//			}
//
//		}
//
//		doc_Text.setLength(0);
//	}
//
//	//printHash- input: hashMap
//	//printout the hash values
//	void printHash(HashMap<String,Integer> terms_Hash)
//	{
//		for (String term: terms_Hash.keySet()){
//			String key = term.toString();
//			System.out.println("term is: "+key+" value is :"+terms_Hash.get(key));
//		}
//	}
//
//	//parseExpressions- input: stringBuffer of the text and hashMap to update
//	//output: the new text without the matches+updates the the hash with the matched expressions
//	StringBuffer parseExpressions(HashMap<String,Integer> terms_Hash, StringBuffer doc_Text)
//	{
//		StringBuffer sb1 = new StringBuffer(),sb2 = new StringBuffer();
//
//		Matcher matcher=patternExpression.matcher(doc_Text);
//		while (matcher.find())
//		{
//			String[] allWords = matcher.group().toString().split("-");
//			for (int i=0; i<allWords.length; i++)
//			{
//				if(Character.isDigit(allWords[i].charAt(0)) )//adding number too
//				{
//					addToHash(terms_Hash,allWords[i]);
//				}
//			}
//
//			addToHash(terms_Hash,matcher.group());
//			matcher.appendReplacement(sb1, " ");
//		}
//
//		matcher.appendTail(sb1);
//		matcher=patternBetweenNumberAndNumber.matcher(sb1);
//		while (matcher.find())
//		{
//			String[] allWords = matcher.group().toString().split(" ");
//			addToHash(terms_Hash, matcher.group());
//			addToHash(terms_Hash,allWords[1]);
//			addToHash(terms_Hash,allWords[3]);
//			matcher.appendReplacement(sb1, " ");
//		}
//		matcher.appendTail(sb2);
//
//		sb1.setLength(0);
//		doc_Text.setLength(0);
//		return sb2;
//	}
//
//	//parseNumbers: input- hash and doc_text
//	//output: add the matched numbers to the hash table and returns the new text without the matches
//	StringBuffer parseNumbers(HashMap<String,Integer>terms_Hash, StringBuffer doc_Text)
//	{
//		StringBuffer sb1 = new StringBuffer();
//		StringBuffer sb2 = new StringBuffer();
//		Matcher matcher=patternNumbers.matcher(doc_Text);
//
//		while (matcher.find())//( Thousand| Million| Billion|( )?KG|( )?kg| kilogram)
//		{
//			String[] allWords = matcher.group().toString().split(" ");
//			if(allWords.length == 1)
//			{
//				if (allWords[0].toLowerCase().contains("kg"))
//				{
//					addToHash(terms_Hash,allWords[0].substring(0, allWords[0].length()-2)+"kg");//kg
//				}
//				else
//				{
//					addpatternNumbersToHash(terms_Hash,allWords[0]);//number
//				}
//
//			}
//			else if(allWords.length == 2)//n fraction- add as is/n Thousand/n Million/n Billion/n kg/n kilogram
//			{
//				if(allWords[1].equalsIgnoreCase("thousand"))
//				{
//					//System.out.println("match Thousand: "+matcher.group());
//					addToHash(terms_Hash,allWords[0]+"K");
//				}
//				else if(allWords[1].equalsIgnoreCase("million"))
//				{
//					//System.out.println("match Million: "+matcher.group());
//					addToHash(terms_Hash,allWords[0]+"M");
//				}
//				else if (allWords[1].equalsIgnoreCase("billion"))
//				{
//					//System.out.println("match Billion: "+matcher.group());
//					addToHash(terms_Hash,allWords[0]+"B");
//				}
//				else if (allWords[1].equalsIgnoreCase("kilogram") | allWords[1].equalsIgnoreCase("kg"))//kg|kilogram
//				{
//					//System.out.println("match kg: "+matcher.group());
//					addToHash(terms_Hash,allWords[0]+"kg");
//				}
//				else//fraction
//				{
//					//System.out.println("match num: "+matcher.group());
//					addToHash(terms_Hash,matcher.group());
//				}
//
//			}
//			else if(allWords.length == 3)//n fraction Thousand/n fraction Million/n fraction Billion/n fraction KG/n fraction kg/n fraction Kg/n fraction kilogram ??
//			{
//
//				if(allWords[1].equalsIgnoreCase("thousand"))
//				{
//					//System.out.println("match Thousand: "+matcher.group());
//					addToHash(terms_Hash,allWords[0]+" "+allWords[1]+"K");
//				}
//				else if(allWords[1].equalsIgnoreCase("million"))
//				{
//					//System.out.println("match Million: "+matcher.group());
//					addToHash(terms_Hash,allWords[0]+" "+allWords[1]+"M");
//				}
//				else if (allWords[1].equalsIgnoreCase("billion"))
//				{
//					//System.out.println("match Billion: "+matcher.group());
//					addToHash(terms_Hash,allWords[0]+" "+allWords[1]+"B");
//				}
//				else if (allWords[1].equalsIgnoreCase("kilogram") | allWords[1].equalsIgnoreCase("kg"))//kg|kilogram
//				{
//					//System.out.println("match kg: "+matcher.group());
//					addToHash(terms_Hash,allWords[0]+" "+allWords[1]+"kg");
//				}
//			}
//			matcher.appendReplacement(sb2, " ");
//		}
//		matcher.appendTail(sb2);
//
//		sb1.setLength(0);
//		doc_Text.setLength(0);
//
//		return sb2;
//	}
//
//	//addpatternNumbersToHash- input: the matched term and hashMap to update
//	//output: add the match to the hash according to the instruction
//	void addpatternNumbersToHash(HashMap<String,Integer> termsHash,String match )
//	{
//		Pair<types,Float> pair = checkNumSize(match);
//		types type=pair.getKey();
//		Float num=pair.getValue();
//		match= upToThreeNubersAfterDot(match);
//		if( type == types.NUMBER_SMALLER_THAN_1K)
//		{
//			addToHash(termsHash,match);
//		}
//		else if(type == types.NUMBER_1K_TO_1M )
//		{
//			match=removeComma(match);
//			BigDecimal number= new BigDecimal(match);
//			number=number.divide(new BigDecimal(1000)).setScale(3, RoundingMode.DOWN);
//			addToHash(termsHash,number.toString()+"K");
//
//		}
//		else if(type == types.NUMBER_1M_TO_1B)
//		{
//			num=num/1000000;
//			addToHash(termsHash,String.format("%.3f", num)+"M");
//		}
//		else if(type == types.NUMBER_GREATER_THAN_1B)
//		{
//			num=num/1000000000;
//			addToHash(termsHash,String.format("%.3f", num)+"B");
//		}
//	}
//
//	//addpatternNumbersCommasToHash- input: the matched term and hashMap to update
//	//output: add the match to the hash according to the instruction
//	void addpatternNumbersCommasToHash(HashMap<String,Integer> termsHash,String match )
//	{
//		addpatternNumbersToHash(termsHash,match);
//	}
//
//	float getNumAsFloat(String num)
//	{
//		if(num.contains(","))
//			num=removeComma(num);
//
//		return Float.parseFloat(num);
//	}
//
//
//	//parsePrices- input: stringBuffer of the text and hashMap to update
//	//output:the new string buffer without the matches+ updates the the hash with the matched prices
//	StringBuffer parsePrices(HashMap<String,Integer> terms_Hash, StringBuffer doc_Text)
//	{
//		StringBuffer sb1 = new StringBuffer();
//		StringBuffer sb2 = new StringBuffer();
//
//		Matcher matcher  = patternDollarSignBillionMillion.matcher(doc_Text);//$price (million/bilion)?
//		while (matcher.find())
//		{
//			String match= matcher.group();
//			String[] allWords = matcher.group().toString().split(" ");
//
//			if(allWords.length==1)
//			{
//				addpatternDollarSignToHash(terms_Hash, match.substring(1,match.length()));
//			}
//			else if(allWords[1].equalsIgnoreCase("million"))
//			{
//				addToHash(terms_Hash,allWords[0].substring(1)+" M Dollars");
//			}
//			else//billion
//			{
//				addToHash(terms_Hash,(int)(getNumAsFloat(allWords[0].substring(1))*1000)+" M Dollars");
//			}
//			matcher.appendReplacement(sb1, " ");
//		}
//
//		matcher.appendTail(sb1);
//
//		matcher  = patternPrice.matcher(sb1);//Price fraction Dollars
//		while (matcher.find())
//		{
//			String[] allWords = matcher.group().toString().split(" ");
//			String num= removeComma(allWords[0]);
//			if(allWords.length== 4)//Price billion U.S. dollars/Price million U.S. dollars/Price trillion U.S. dollars
//			{
//				switch (allWords[1])
//				{
//					case "billion":
//						addToHash(terms_Hash,num+"000 M Dollars");
//						break;
//					case "million":
//						addToHash(terms_Hash,allWords[0]+" M Dollars");
//						break;
//					case "trillion":
//						addToHash(terms_Hash,num+"000000 M Dollars");
//						break;
//				}
//			}
//			else if(allWords.length== 3)//Price fraction Dollars
//			{
//				if( checkNumSize(allWords[0]).getKey() == types.NUMBER_1K_TO_1M ||checkNumSize(allWords[0]).getKey() == types.NUMBER_SMALLER_THAN_1K )
//				{
//					addToHash(terms_Hash,matcher.group());
//				}
//			}
//			else if(allWords.length== 2)//Price[m] Dollars/Price[bn] Dollars/Price Dollars(more+less than million dollar )
//			{
//				if(allWords[0].toLowerCase().endsWith("m"))
//				{
//					addpatternMDollarToHash(terms_Hash,allWords[0]);
//				}
//				else if(allWords[0].toLowerCase().endsWith("bn"))
//				{
//					addpatternBnDollarToHash(terms_Hash,allWords[0]);
//				}
//				else
//				{
//					addpatternDollarSignToHash(terms_Hash,allWords[0]);
//				}
//			}
//			matcher.appendReplacement(sb2, " ");
//		}
//		matcher.appendTail(sb2);
//
//		sb1.setLength(0);
//		doc_Text.setLength(0);
//
//		return sb2;
//	}
//
//	//addpatternDollarSignToHash- input: input:string number and hashMap to update
//	//output: add the match to the hash according to the instruction($price -> above 1M- save as price M Dollars, else save as price Dollars)
//	void addpatternDollarSignToHash(HashMap<String,Integer> termsHash,String match )
//	{
//		Pair<types,Float> pair = checkNumSize(match);
//		types type=pair.getKey();
//		float num=pair.getValue();
//		if( type == types.NUMBER_GREATER_THAN_1B || type == types.NUMBER_1M_TO_1B)
//		{
//			addToHash(termsHash,(int)(num/1000000)+" M Dollars");
//		}
//		else if(type == types.NUMBER_1K_TO_1M || type == types.NUMBER_SMALLER_THAN_1K)
//		{
//			addToHash(termsHash,match+" Dollars");
//		}
//	}
//
//	//addpatternMDollarToHash- input: the match (just the number+m) term and hashMap to update
//	//output: add the match to the hash according to the instruction(Price m Dollars --> Price M Dollars)
//	void addpatternMDollarToHash(HashMap<String,Integer> termsHash,String match )
//	{
//		addToHash(termsHash,match.substring(0,match.length()-1)+" M Dollars");
//	}
//
//	//addpatternBnDollarToHash- input: the matched term and hashMap to update
//	//output: add the match to the hash according to the instruction(Price bn Dollars)
//	void addpatternBnDollarToHash(HashMap<String,Integer> termsHash,String match )
//	{
//		addToHash(termsHash,match.substring(0,match.length()-2)+"000 M Dollars");
//	}
//
//	String removeComma(String num)
//	{
//		StringBuffer sb1 = new StringBuffer();
//
//		Matcher matcher  = Pattern.compile("[,]").matcher(num);
//		while (matcher.find())
//		{
//			matcher.appendReplacement(sb1, "");
//		}
//		matcher.appendTail(sb1);
//
//		return sb1.toString();
//	}
//
//	String upToThreeNubersAfterDot(String num)
//	{
//		if(Pattern.matches("[0-9]{1,}[.][0-9]{4,}",num))
//		{
//			int idx=num.indexOf(".");
//			return num.subSequence(0, idx+3).toString();
//		}
//
//		return num;
//	}
//
//	//input : hash and text
//	//looking for a Percent match, adding the matches to the hashMap according to instructions
//	StringBuffer parsePercent(HashMap<String,Integer> terms_Hash, StringBuffer doc_Text)
//	{
//		StringBuffer sb1 = new StringBuffer();
//
//		Matcher matcher  = patternPercent.matcher(doc_Text);
//		while (matcher.find())
//		{
//			int space= (matcher.group().contains(" ") == true)? 1:0;
//			if(matcher.group().endsWith("%"))//ends with %
//			{
//				addToHash(terms_Hash, matcher.group().substring(0, matcher.group().length()-space-1)+"%");
//			}
//			else if(matcher.group().toLowerCase().endsWith("percent"))//percent
//			{
//				addToHash(terms_Hash, matcher.group().substring(0, matcher.group().length()-space-"percent".length())+"%");
//			}
//			else//Percentage
//			{
//				addToHash(terms_Hash, matcher.group().substring(0,matcher.group().length()-space-"Percentage".length())+"%");
//			}
//			matcher.appendReplacement(sb1, " ");
//		}
//		matcher.appendTail(sb1);
//
//		doc_Text.setLength(0);
//
//		return sb1;
//	}
//
//	//input : hash and text
//	//looking for a Date match, adding the matches to the hashMap
//	StringBuffer parseDate(HashMap<String,Integer> terms_Hash, StringBuffer doc_Text)
//	{
//		StringBuffer sb1 = new StringBuffer();
//
//		Matcher matcher  = patternDate.matcher(doc_Text);
//		while (matcher.find())
//		{
//			String[] allWords = matcher.group().toString().split(" ");
//			if(allWords.length>1)
//			{
//				if(allWords.length==2 && allWords[0].length()==2 || (allWords.length==3 && allWords[2].length()<4 ))//DD Month
//				{
//					addToHash(terms_Hash,MonthToNum(allWords[1])+"-"+allWords[0]);
//				}
//				else if(allWords[1].length()==2)//Month DD
//				{
//					addToHash(terms_Hash,MonthToNum(allWords[0])+"-"+allWords[1]);
//				}
//				else if(allWords.length==2) //Month Year
//				{
//					addToHash(terms_Hash,allWords[1].substring(0, 4)+"-"+MonthToNum(allWords[0]));
//				}
//				else //full date- DD MM YY
//				{
//					String month=MonthToNum(allWords[1]);
//					addToHash(terms_Hash,month+"-"+allWords[0]);
//					addToHash(terms_Hash,allWords[2].substring(0,4)+"-" +month);
//				}
//				matcher.appendReplacement(sb1, " ");
//			}
//
//		}
//
//		matcher.appendTail(sb1);
//
//		doc_Text.setLength(0);
//
//		return sb1;
//	}
//
//	String MonthToNum(String month)
//	{
//		switch (month.toLowerCase())
//		{
//			case "jan":
//			case "january":
//				return "01";
//			case "feb":
//			case "february":
//				return "02";
//			case "mar":
//			case "march":
//				return "03";
//			case "apr":
//			case "april":
//				return "04";
//			case "may":
//				return "05";
//			case "jun":
//			case "june":
//				return "06";
//			case "jul":
//			case "july":
//				return "07";
//			case "aug":
//			case "august":
//				return "08";
//			case "sep":
//			case "september":
//				return "09";
//			case "oct":
//			case "october":
//				return "10";
//			case "nov":
//			case "november":
//				return "11";
//			case "dec":
//			case "december":
//				return "12";
//		}
//
//		return "00";
//	}
//
//	void addToHash(HashMap<String,Integer> termsHash, String term )
//	{
//		if(!termsHash.containsKey(term))
//		{
//			termsHash.put(term,1);
//		}
//		else
//		{
//			termsHash.put(term,termsHash.get(term)+1);
//		}
//	}
//
//	//input- string number
//	//output- pair- the number as a float and hes type from the enum
//	private Pair<types,Float> checkNumSize(String number)
//	{
//		types ret=types.UNDEFINED;
//		float num=0;
//		if(number.contains(","))
//			number=removeComma(number);
//
//		try
//		{
//			num=Float.parseFloat(number);
//
//			if(num<1000)
//			{
//				ret=types.NUMBER_SMALLER_THAN_1K;
//			}
//			else if(num<1000000)
//			{
//				ret=types.NUMBER_1K_TO_1M;
//			}
//			else if(num<1000000000)
//			{
//				ret=types.NUMBER_1M_TO_1B;
//			}
//			else
//			{
//				ret=types.NUMBER_GREATER_THAN_1B;
//			}
//		}
//		catch (NumberFormatException e)
//		{
//			System.out.println("ERROR:checkNumSize - " + number);
//		}
//
//		return 	new Pair<types,Float>(ret,num);
//	}
//}