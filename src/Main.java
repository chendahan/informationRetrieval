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
        manager.setPathForCorpus("C:\\corpus_MINI");
        //manager.setPathForCorpus("D:\\corpus2");
        manager.setPathForPostingFile("C:\\PostingFileMINI");
       // manager.run();
        manager.loadDictionary(false);
        
        long start = System.currentTimeMillis();   
        manager.searchQueryFromFile("C:\\treceval\\queries.txt");
        //manager.searchQuery("Falkland petroleum exploration");
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println("query time : "+ elapsedTime/1000F);
    }
}