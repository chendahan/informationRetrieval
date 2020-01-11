package Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;


public class ReadFile implements IReadFile {

    private String rootPath;
    public String[] directories;
    ArrayList<String> allFiles;

    public ReadFile(String path) {
        rootPath = path;
        getListOfDirs(path);
        allFiles = new ArrayList<>();
        ReadAll();
    }

    /**
     * Reads all the files, then separate them to Doc`s and send the Doc`s to Parse
     * when they back the function send them to write function
     */
    @Override
    public void ReadAll() {
        for (int dirCounter = 0; dirCounter < GetNumOfDirs(); dirCounter++) {
            File currentDirectory = new File(directories[dirCounter]);
            if (!currentDirectory.isDirectory()) {
                continue;
            }
            ReadFromDirectory(currentDirectory);
        }
    }

    /**
     * Getting all files from a dir
     *
     * @param directory - the dir where is the files
     */
    private void ReadFromDirectory(File directory) {
        String dirName = directory.getPath();
        String[] docsInDir = directory.list();

        for (int i = 0; i < docsInDir.length; i++) {
            allFiles.add(dirName + "\\" + docsInDir[i]);
        }
    }

    /**
     * get the whole file and return the Texts from the file and the DOC ID
     *
     * @param file - getting the whole file/
     */
    public HashMap<String, StringBuilder> getTextsFromTheFile(File file) {
        HashMap<String, StringBuilder> allDocsInTheFile = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            String docName = "";
            while ((st = br.readLine()) != null) {
                //gets the name of the text

                if (st.contains("<DOCNO>")) {
                    String[] line = st.split(" ");
                    if (line.length == 3) 
                    {
                        docName = line[1];
                    } else 
                    {
                    	line = st.split(">");
                    	line = line[1].split("<");
                        docName = line[0];
                    }
                } else if (st.contains("<TEXT>")) {
                    StringBuilder onlyText = new StringBuilder();

                    while (!((st = br.readLine()).contains("</TEXT>"))) {
                        onlyText.append(st);
                        onlyText.append(" ");
                    }
                    allDocsInTheFile.put(docName, onlyText);
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println("problem with the reading from file!! in: " + file.getPath());
        }
        return allDocsInTheFile;
    }


    //<editor-fold des="Getters">

    /**
     * Fills the list of all Text files
     *
     * @param path -  to folder where all docs is existing
     */
    private String[] getListOfDirs(String path) {
        File files = new File(path);
        String[] names = files.list();
        directories = new String[names.length];

        for (int i = 0; i < names.length; i++) {
            directories[i] = path + "\\" + names[i];
        }
        return directories;
    }

    /**
     * @return - number of dirs in the path
     */
    public int GetNumOfDirs() {
        if (directories != null) {
            return directories.length;
        } else {
            System.out.println("There Is No List Of Dirs");
            return 0;
        }
    }

    /**
     * @return - all the files that we have in the corpus.
     */
    public ArrayList<String> getAllFiles() {
        return allFiles;
    }
    //</editor-fold>


}
