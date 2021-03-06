package View;

import ViewModel.Manager;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.swing.JOptionPane.showMessageDialog;

public class GUI {

    public String inputPath;
    public String outputPath;
    boolean stemming;
    boolean dicIsLoaded;
    boolean isReseted = false;

    String inputQueryPath;
    String inputFreeQuery;
    boolean includeEntity;
    int q_ID;

    Manager manager;

    public GUI() {
        q_ID = 1;
        // Creating instance of JFrame
        JFrame frame = new JFrame("Our Little Google");
        // Setting the width and height of frame
        frame.setSize(430, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* Creating panel. This is same as a div tag in HTML
         * We can create several panels and add them to specific
         * positions in a JFrame. Inside panels we can add text
         * fields, buttons and other components.
         */
        JPanel panel = new JPanel();
        // adding panel to frame
        frame.add(panel);
        /* calling user defined method for adding components
         * to the panel.
         */
        placeComponents(panel);

        // Setting the frame visibility to true
        frame.setVisible(true);
        //initialization of manager
        this.manager = new Manager();
        dicIsLoaded =false;

    }

    public void placeComponents(JPanel panel) {
        panel.setLayout(null);
        // Creating JLabel
        JLabel inputLabel = new JLabel("Input path:");
        /* This method specifies the location and size
         * of component. setBounds(x, y, width, height)
         * here (x,y) are cordinates from the top left
         * corner and remaining two arguments are the width
         * and height of the component.
         */
        inputLabel.setBounds(10, 20, 80, 25);
        panel.add(inputLabel);

        // Creating text field for input
        JTextField inputText = new JTextField(20);
        inputText.setBounds(100, 20, 165, 25);
        panel.add(inputText);

        // Same process for output field.
        JLabel outputLabel = new JLabel("Output path:");
        outputLabel.setBounds(10, 50, 80, 25);
        panel.add(outputLabel);


        JTextField outputText = new JTextField(20);
        outputText.setBounds(100, 50, 165, 25);
        panel.add(outputText);


        JButton inputBrowseButton = new JButton("Browse");
        inputBrowseButton.setBounds(280, 20, 80, 25);
        panel.add(inputBrowseButton);

        //<editor-fold> des="query's">
        JButton freeQueryButton = new JButton("Click on me to enter a query");
        freeQueryButton.setBounds(100, 170, 200, 25);
        panel.add(freeQueryButton);
        freeQueryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField freeQuery = new JTextField();
                final JComponent[] inputs = new JComponent[] {
                        new JLabel("Enter Your Query"),
                        freeQuery,
                };
                int result = JOptionPane.showConfirmDialog(null, inputs, "Free Query", JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    inputFreeQuery = freeQuery.getText();
                    System.out.println("You entered " + freeQuery.getText() );
                } else {
                    System.out.println("User canceled / closed the dialog, result = " + result);
                }
            }
        });


        JLabel queryLabel = new JLabel("Query path:");
        queryLabel.setBounds(10, 210, 80, 25);
        panel.add(queryLabel);


        JTextField queryText = new JTextField(20);
        queryText.setBounds(100, 210, 165, 25);
        panel.add(queryText);


        JButton queryBrowseButton = new JButton("Browse");
        queryBrowseButton.setBounds(280, 210, 80, 25);
        panel.add(queryBrowseButton);

        queryBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String inputQuery = new String();
                JFileChooser fileChooser = new JFileChooser();

                // For Directory
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);

                int rVal = fileChooser.showOpenDialog(null);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    queryText.setText(fileChooser.getSelectedFile().toString());
                    inputQuery = fileChooser.getSelectedFile().toString();
                    setInputPath(inputQuery);
                    inputQueryPath = inputQuery;
                }
            }
        });



        //</editor-fold>

        // Creating input browse button

        inputBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();

                // For Directory
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                fileChooser.setAcceptAllFileFilterUsed(false);

                int rVal = fileChooser.showOpenDialog(null);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    inputText.setText(fileChooser.getSelectedFile().toString());
                    inputPath = fileChooser.getSelectedFile().toString();
                    setInputPath(inputPath);
                    manager.setPathForCorpus(inputPath);
                }
            }
        });


        // Creating input browse button

        JButton outputBrowseButton = new JButton("Browse");
        outputBrowseButton.setBounds(280, 50, 80, 25);
        panel.add(outputBrowseButton);

        outputBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();

                // For Directory
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);

                int rVal = fileChooser.showOpenDialog(null);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    outputText.setText(fileChooser.getSelectedFile().toString());
                    outputPath = fileChooser.getSelectedFile().toString();
                    setOutputPath(outputPath);
                    isReseted = false;
                    manager.setPathForPostingFile(outputPath);
                }
            }
        });


        // Creating stemming checkBox
        JCheckBox stemmingCheckBox = new JCheckBox("Allow stemming");
        stemmingCheckBox.setBounds(10, 80, 120, 25);
        panel.add(stemmingCheckBox);

        // Show Entity
        //JCheckBox entityCheckBox = new JCheckBox("Show Entities");
        //entityCheckBox.setBounds(10, 250, 120, 25);
        //panel.add(entityCheckBox);
        // Creating zero button
        JButton zeroButton = new JButton("Zero");
        zeroButton.setBounds(10, 120, 80, 25);
        zeroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getOutputPath() == null || getOutputPath().length() == 0) {
                    showMessageDialog(null, "The output path is empty! \n Please Browse a new path.");
                } else {
                    if (!isReseted) {
                        File file = null;
                        if (stemmingCheckBox.isSelected()) {
                            file = new File(getOutputPath() + "\\With Stemming");
                        } else {
                            file = new File(getOutputPath() + "\\Without Stemming");
                        }
                        if (file.list().length == 0) {
                            showMessageDialog(null, "The output path is empty!");
                        }
                        File[] files = file.listFiles();
                        if (files != null) { //some JVMs return null for empty dirs
                            for (File f : files) {
                                f.delete();
                            }
                        }
                        file.delete();
                        manager = null;
                        isReseted = true;
                    }
                    else
                    {
                        showMessageDialog(null, "Cannot reset twice");
                    }
                }
            }
        });

        panel.add(zeroButton);


        //Semantic
        JCheckBox semanticCheckBox = new JCheckBox("Allow Semantic");
        semanticCheckBox.setBounds(150, 250, 120, 25);
        panel.add(semanticCheckBox);

        JButton resultsButton = new JButton("Show Results");
        resultsButton.setBounds(280, 250, 120, 25);
        panel.add(resultsButton);

        resultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean toContinue = false;
                boolean freeQuery = false;
                String[][] resultFromFreeQuery = new String[50][3];
                //List<Map.Entry<String, Double>> resultFromFreeQuery;
                if ((inputQueryPath == null) && (inputFreeQuery == null)) {
                    showMessageDialog(null, "The input path or search bar is empty! \n Please Browse a new path for query");
                } else if (dicIsLoaded) {
                    toContinue = true;
                    if (inputPath != null && inputQueryPath.length() > 1) {
                        manager.searchQueryFromFile(inputQueryPath, semanticCheckBox.isSelected());
                    } else {
                        resultFromFreeQuery = manager.getResultOfFreeQueryInArray(inputFreeQuery, semanticCheckBox.isSelected(), q_ID);
                        q_ID++;
                        freeQuery = true;
                    }
                } else {
                    showMessageDialog(null, "Please load Dictionary or click on 'Start' before");
                }
                if (toContinue) {
                    manager.setShowEntity(true);
                    String[][] result;
                    if (freeQuery && resultFromFreeQuery != null) {
                        result = resultFromFreeQuery;
                    } else {
                        result = manager.getResultOfQueryInArray();
                    }

                    String[][] resultWithNoEntity = new String[result.length][2];
                    for (int i = 0; i < result.length; i++) {
                        for (int j = 0; j < 2; j++) {
                            resultWithNoEntity[i][j] = result[i][j];
                        }
                    }

                    if (result != null) {
                        JFrame frame = new JFrame("Results");

                        frame.setLayout(new BorderLayout());
                        String[] definition = new String[]{"Query Number", "Document Number"};
                        final JTable table = new JTable(resultWithNoEntity, definition);

                        JPanel btnPnl = new JPanel(new BorderLayout());
                        JPanel bottombtnPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));

                        JButton btn = new JButton("Show Entities");
                        bottombtnPnl.add(btn);

                        btnPnl.add(bottombtnPnl, BorderLayout.CENTER);
                        frame.add(table.getTableHeader(), BorderLayout.NORTH);

                        frame.add(table, BorderLayout.CENTER);
                        frame.add(btnPnl, BorderLayout.SOUTH);
                        table.getTableHeader().setReorderingAllowed(false);
                        frame.add(new JScrollPane(table));

                        btn.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int selectedRow = table.getSelectedRow();
                                if (selectedRow < 0) {
                                    showMessageDialog(null, "Please select a row");
                                } else {
                                    if (result[selectedRow][2] != null && result[selectedRow][2].length()  > 1)
                                    {
                                        String docToShow = result[selectedRow][2];
                                        String topFiveEntities = docToShow.replaceAll(",", "\n");
                                        showMessageDialog(null, topFiveEntities, "Top 5 Entities", JOptionPane.PLAIN_MESSAGE);
                                    }
                                    else
                                    {
                                        showMessageDialog(null, "There is no entities for this document");
                                    }
                                }
                            }
                        });
                        frame.pack();
                        frame.setVisible(true);
                    }
                }
            }
        });



        // Creating showDic button
        JButton showDicButton = new JButton("Show dictionary");
        showDicButton.setBounds(110, 120, 125, 25);
        showDicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[][] sortedDic = manager.getSortedDictionary();
                if (sortedDic != null) {
                    JFrame frame = new JFrame("Sorted Dictionary");
                    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                    frame.setPreferredSize(new Dimension(500, 500));
                    String[] definition = {"Term", "Amount of appearance in corpus"};
                    JTable dicTable = new JTable(sortedDic, definition);
                    dicTable.setBounds(200, 200, 200, 200);
                    frame.add(new JScrollPane(dicTable));
                    frame.pack();
                    frame.setVisible(true);
                }
            }
        });

        panel.add(showDicButton);

        // Creating loadDic button
        JButton loadDicButton = new JButton("Load dictionary");
        loadDicButton.setBounds(255, 120, 125, 25);
        loadDicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (manager != null && getOutputPath() != null) {
                    manager.loadDictionary(stemmingCheckBox.isSelected());
                    manager.setStemming(stemmingCheckBox.isSelected());
                    dicIsLoaded = true;
                    showMessageDialog(null, "The Dictionary is loaded");
                } else {
                    showMessageDialog(null, "The output path is empty! \n Please Browse a new path");
                }
            }
        });

        // Creating start button
        JButton startButton = new JButton("Start!");
        startButton.setBounds(280, 80, 80, 25);
        panel.add(startButton);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                System.out.println("here!");
                stemming = stemmingCheckBox.isSelected();
                setStemming(stemming);
                try {
                    if (getInputPath() == null || getOutputPath() == null) {
                        showMessageDialog(null, "The input or output path is empty!");
                    } else {
                        manager.setStemming(stemming);
                        dicIsLoaded = true;
                        manager.run();
                        showMessageDialog(null,"The time of the program is: " + manager.getTime() +"\n And number of unique terms is " + manager.getUnqieTerms());
                    }
                } catch (NullPointerException e1) {
                    e1.toString();
                }
            }
        });
        panel.add(loadDicButton);


        // save Result

        JButton saveResultBtn = new JButton("Save Results");
        saveResultBtn.setBounds(20, 250, 120, 25);
        panel.add(saveResultBtn);

        saveResultBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                // For Directory
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);
                int rVal = fileChooser.showOpenDialog(null);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    outputText.setText(fileChooser.getSelectedFile().toString());
                    String outputForResults = fileChooser.getSelectedFile().toString();
                    setOutputPath(outputForResults);
                    isReseted = false;
                    if (inputQueryPath != null && inputQueryPath.length()>1)
                    {
                        manager.saveResults(outputForResults,true);
                    }
                    else if(inputFreeQuery!= null && inputFreeQuery.length()>1)
                    {
                        manager.saveResults(outputForResults,false);
                    }
                    else
                    {
                        showMessageDialog(null, "Please click on Search first");
                    }

                }
            }
        });

    }

    //<editor-fold des="Setters and Getters>"
    public void setOutputPath(String path) {
        this.outputPath = path;
    }

    public void setInputPath(String path) {
        this.inputPath = path;
    }

    public void setStemming(boolean stemming) {
        this.stemming = stemming;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public String getInputPath() {
        return inputPath;
    }

    public boolean isStemming() {
        return stemming;
    }
    //</editor-fold>
}


