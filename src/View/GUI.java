package View;

import ViewModel.Manager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static javax.swing.JOptionPane.showMessageDialog;

public class GUI {

    public String inputPath;
    public String outputPath;
    boolean stemming;

    Manager manager;

    public GUI() {

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
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);

                int rVal = fileChooser.showOpenDialog(null);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    queryText.setText(fileChooser.getSelectedFile().toString());
                    inputQuery = fileChooser.getSelectedFile().toString();
                    setInputPath(inputQuery);
                    //manager.setPathForCorpus(inputPath);
                    System.out.println("The Input Is: " + inputQuery);
                }
            }
        });



        JCheckBox semanticCheckBox = new JCheckBox("Allow Semantic");
        semanticCheckBox.setBounds(150, 250, 120, 25);
        panel.add(semanticCheckBox);

        JButton resultsButton = new JButton("Show Results");
        resultsButton.setBounds(280, 250, 120, 25);
        panel.add(resultsButton);
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
                    manager.setPathForPostingFile(outputPath);
                }
            }
        });


        // Creating stemming checkBox
        JCheckBox stemmingCheckBox = new JCheckBox("Allow stemming");
        stemmingCheckBox.setBounds(10, 80, 120, 25);
        panel.add(stemmingCheckBox);

        // Show Entity
        JButton entityLabel = new JButton("Show entities");
        entityLabel.setBounds(10, 250, 120, 25);
        panel.add(entityLabel);


        // Creating zero button
        JButton zeroButton = new JButton("Zero");
        zeroButton.setBounds(10, 120, 80, 25);
        zeroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getOutputPath() == null || getOutputPath().length() == 0) {
                    showMessageDialog(null, "The output path is empty! \n Please Browse a new path.");
                } else {
                    File file = null;
                    if (stemmingCheckBox.isSelected()) {
                        file = new File(getOutputPath() + "\\With Stemming");
                    } else {
                        file = new File(getOutputPath() + "\\Without Stemming");
                    }
                    if (file.list().length == 0)
                    {
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
                }
            }
        });

        panel.add(zeroButton);

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
                        manager.run();
                    }
                } catch (NullPointerException e1) {
                    e1.toString();
                }
            }
        });
        panel.add(loadDicButton);


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


