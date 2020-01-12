import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ViewModel.Manager;


public class Main {

    public static void main(String[] args) {
        //GUI gui = new GUI();

//        Manager manager = new Manager();
//        manager.setStemming(false);
//        manager.setPathForCorpus("C:\\\\My Little Project\\\\corpus\\\\corpus");
//        //manager.setPathForCorpus("D:\\corpus2");
//        manager.setPathForPostingFile("C:\\\\My Little Project\\\\PostingFile");
//       // manager.run();
//        manager.loadDictionary(false);
//
//        long start = System.currentTimeMillis();
//        manager.searchQuery("coffee break BAPELA bapela");
//        long elapsedTime = System.currentTimeMillis() - start;
//        System.out.println("query time : "+ elapsedTime/1000F);

        Manager manager = new Manager();
        manager.setStemming(false);
        manager.setPathForCorpus("C:\\corpus");
        //manager.setPathForCorpus("D:\\corpus2");
        manager.setPathForPostingFile("C:\\PostingFile");
       // manager.run();
        manager.loadDictionary(false);
        
        long start = System.currentTimeMillis();   
        HashMap<String, List<Map.Entry<String, Double>>> res = manager.searchQueryFromFile("C:\\treceval\\queries.txt");
        try 
        {
        	int i=0;
            File file = new File("C:\\treceval\\results.txt");
            FileWriter writer = new FileWriter(file);
            
			for (Map.Entry<String, List<Map.Entry<String, Double>>> idQuery: res.entrySet())
			{
				i++;
	            for (Map.Entry<String, Double> docs : idQuery.getValue())
	            {
	                writer.write(idQuery.getKey() +" 0 "+docs.getKey()+" 3 42.38 mt\n");
	            }

	        } 
            
            writer.close();
        }
		catch (Exception e) 
		{
            System.out.println(e.toString()+" kkkk");
        }
		
        
        //manager.searchQuery("Falkland petroleum exploration");
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println("query time : "+ elapsedTime/1000F);
    }
}