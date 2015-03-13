/**
 * *****************************************************************************
 * Copyright (c) 2014 
 * Christian Chiarcos, Niko Schenk 
 * Applied Computational Linguistics Lab (ACoLi)
 * Goethe-Universität Frankfurt am Main 
 * http://acoli.cs.uni-frankfurt.de/en.html
 * Robert-Mayer-Straße 10
 * 60325 Frankfurt am Main
 * 
 * All rights reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Niko Schenk - initial API and
 * implementation.
 * *****************************************************************************
 */

package de.acoli.informatik.uni.frankfurt.reranking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * This program takes a list of numbers as input, each number representing a
 * "good" analysis which conforms to the GOLD annotation.
 *
 * Read in all the analyses and output a list of numbers which all have in
 * common!
 *
 *
 * Output:
 *
 * GOLD CRF WEBDEMO REFLEXICA
 *
 * <tag> <anotherTag> <yetAnotherTag> <...>
 * <tag2> <anotherTag2> <yetAnotherTag2> <..>
 * <..
 *
 *
 * @author niko
 */
public class DifferentAnalysesIntoOneCRFFormatCombiner {

    public static final String BIBTYPE = "article";

    public static final String LABEL_MATRIX_FILE = "/home/niko/Desktop/CRF_and_Reflexica/DBLP/" + BIBTYPE + "/label_matrix.txt";

    public static final String ACCORDANCE_DIR = "/home/niko/Desktop/CRF_and_Reflexica/DBLP/" + BIBTYPE + "/Accordance_with_gold/";

    public static final String GOLD_OUTPUT = "/home/niko/Desktop/CRF_and_Reflexica/DBLP/" + BIBTYPE + "/GOLD/2000_article_GOLD.txt";
    public static final String REFLEXICA_OUTPUT = "/home/niko/Desktop/CRF_and_Reflexica/DBLP/" + BIBTYPE + "/REFLEXICA/2000_Reflexica_output_CRF.txt";

    public static final String BIBANALYZER_OUTPUT_SPRINGER = "/home/niko/Desktop/CRF_and_Reflexica/DBLP/" + BIBTYPE + "/CRF_output/SPRINGER_tagged_combined.txt";
    public static final String BIBANALYZER_OUTPUT_DBLP = "/home/niko/Desktop/CRF_and_Reflexica/DBLP/" + BIBTYPE + "/CRF_output/DBLP_tagged_combined.txt";

    public static final String WEBSERVICE_OUTPUT_SPRINGER = "/home/niko/Desktop/CRF_and_Reflexica/DBLP/" + BIBTYPE + "/WEBSERVICE/WEBSERVICE_SPRINGER_tagged_combined.txt";
    public static final String WEBSERVICE_OUTPUT_DBLP = "/home/niko/Desktop/CRF_and_Reflexica/DBLP/" + BIBTYPE + "/WEBSERVICE/WEBSERVICE_DBLP_tagged_combined.txt";

    static ArrayList<String> crfAnalyses = new ArrayList<String>();

    static {
        crfAnalyses.add(GOLD_OUTPUT);
        crfAnalyses.add(REFLEXICA_OUTPUT);
        crfAnalyses.add(BIBANALYZER_OUTPUT_SPRINGER);
        crfAnalyses.add(BIBANALYZER_OUTPUT_DBLP);
        crfAnalyses.add(WEBSERVICE_OUTPUT_SPRINGER);
        crfAnalyses.add(WEBSERVICE_OUTPUT_DBLP);
    }

    public static void main(String[] args) throws FileNotFoundException {
        
        generateLabelMatrix(ACCORDANCE_DIR, crfAnalyses, LABEL_MATRIX_FILE);
        
        
    }
        
    
    /**
     * 
     * @param accordanceDir
     * @param crfAnalyses
     * @param labelMatrixOutput
     * @throws FileNotFoundException 
     */
    public static void generateLabelMatrix(String accordanceDir, ArrayList<String> crfAnalyses, String labelMatrixOutput) throws FileNotFoundException {
    
        ArrayList<File> accordances = new ArrayList<File>();
        collectFiles(accordances, accordanceDir);

        int numFiles = accordances.size();
        int[][] cube = new int[2000][numFiles];

        for (int i = 0; i < accordances.size(); i++) {
            File file = accordances.get(i);
            //System.out.println("analyzing " + file.getName());
            Scanner s = new Scanner(file);
            while (s.hasNextLine()) {
                String line = s.nextLine().trim();
                    int aReferenceNumber = Integer.parseInt(line);
                    cube[aReferenceNumber][i] = 1;
                
            }
            s.close();
        }

        // Print first twenty references and see which ones are equal.
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < cube[i].length; j++) {
           //     System.out.printf("%5d ", cube[i][j]);
            }
           // System.out.println();
        }

        ArrayList<Integer> allSame = new ArrayList<>();
        // Extract those which have the same token analysis.
        for (int i = 0; i < cube.length; i++) {
            boolean same = true;
            for (int j = 0; j < cube[i].length; j++) {
                if (cube[i][j] == 0) {
                    same = false;
                    break;
                }

            }
            if (same) {
                //System.out.println(i);
                allSame.add(i);
            }
            //System.out.println();
        }

        int numTokens = 0;
        // For every CRF analysis, add all references.
        ArrayList<ArrayList<ArrayList<String[]>>> allCrfAnalyses = new ArrayList<>();
        for (int i = 0; i < crfAnalyses.size(); i++) {
            String aCrfAnalysis = crfAnalyses.get(i);

            Scanner s = new Scanner(new File(aCrfAnalysis));
            ArrayList<ArrayList<String[]>> references = new ArrayList<>();
            ArrayList<String[]> tokens = new ArrayList<>();
            while (s.hasNextLine()) {
                String aLine = s.nextLine().trim();

                if (aLine.length() == 0) {
                    // Add sentence to list.
                    references.add(tokens);
                    tokens = new ArrayList<>();
                } else {
                    String[] tok = aLine.split("\\s");
                    tokens.add(tok);
                    numTokens++;
                }
            }
            s.close();
            allCrfAnalyses.add(references);
        }

        //System.out.println("Num tokens = " + numTokens);

        // Analysis, sentence, token-label-tuple.
    //    System.out.println(allCrfAnalyses.get(0).get(3).get(2)[1]);
    //    System.out.println(allCrfAnalyses.get(1).get(3).get(2)[1]);
//        System.out.println(allCrfAnalyses.get(2).get(3).get(2)[1]);
//        System.out.println(allCrfAnalyses.get(3).get(3).get(2)[1]);
//        System.out.println(allCrfAnalyses.get(4).get(3).get(2)[1]);
//        System.out.println(allCrfAnalyses.get(5).get(3).get(2)[1]);

        // We have numFiles + 1 CRF analyses
        String[][] labelMatrix = new String[numTokens][allCrfAnalyses.size()];
                // Print out only those which have the same tokens in common.

        String[] tokens = new String[numTokens];
        int tokCnt = 0;
        
        for (int i = 0; i < allCrfAnalyses.size(); i++) {
            
            int currentTokenIndex = 0;
            ArrayList<ArrayList<String[]>> aCrfAnalysis = allCrfAnalyses.get(i);
           // System.out.println("Num sentences: " + aCrfAnalysis.size());

            // For all sentences.
            for (int s = 0; s < aCrfAnalysis.size(); s++) {

                if (allSame.contains(s)) {

                    ArrayList<String[]> tokLab = aCrfAnalysis.get(s);
                    // For every label.
                    for (int l = 0; l < tokLab.size(); l++) {
                        String set = "nothing";
                        // That's the gold analysis.
                        if (i == 0) {
                            tokens[currentTokenIndex] = tokLab.get(l)[1];
                            //set = tokLab.get(l)[1];
                            set = tokLab.get(l)[0];
                            
                        } else {
                            set = tokLab.get(l)[0];
                        }
                        labelMatrix[currentTokenIndex][i] = set;
                        currentTokenIndex++;
                    }
                    labelMatrix[currentTokenIndex][i] = " ";
                    currentTokenIndex++;
                }
            }
        }

        
        // Add tokens column to the start of the matrix.
        //System.out.println(tokens.length + " <---");
        //System.out.println(tokens[0]);
        
        
        
        
        PrintWriter w = new PrintWriter(new File(labelMatrixOutput));
        // labelMatrix.length
        for (int i = 0; i < labelMatrix.length; i++) {
            for (int j = 0; j < labelMatrix[i].length; j++) {
                if (labelMatrix[i][j] == null) {
                    w.flush();
                    w.close();
                    return;
                }
                //System.out.print(labelMatrix[i][j] + "\t");
                if(j==0 && tokens[i]!=null) {
                    w.write(tokens[i] + "\t");
                }
                w.write(labelMatrix[i][j] + "\t");
            }
            //System.out.println();
            w.write("\n");
        }

        w.flush();
        w.close();

    }

    /**
     * Collect all (non-directory) files for a specific folder.
     *
     * @param fileList
     * @param path
     */
    private static void collectFiles(ArrayList<File> fileList, String path) {
        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) {
            return;
        }
        for (File f : list) {
            if (f.isDirectory()) {
                collectFiles(fileList, f.getAbsolutePath());
            } else {
                if(!f.getName().endsWith("~"))
                fileList.add(f);
            }
        }
    }

}
