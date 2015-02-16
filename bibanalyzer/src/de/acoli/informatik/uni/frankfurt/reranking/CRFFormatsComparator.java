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
 * Description:
 * Utility class to compare TWO CRF formats to see if they have
 * the exact same number of tokens and if not checks where they differ.
 * 
 * The functionality of this class can be used to compare the CRF analyses 
 * in a later step.
 * 
 * It returns an array of line numbers which are in accordance to the number
 * of tokens in a gold analysis.
 * 
 * @author niko
 */
public class CRFFormatsComparator {

    public static String BIBTYPE = "article";

    public static String GOLD_OUTPUT = "/home/niko/Desktop/CRF_and_Reflexica/DBLP/" + BIBTYPE + "/GOLD/2000_article_GOLD.txt";
    public static String REFLEXICA_OUTPUT = "/home/niko/Desktop/CRF_and_Reflexica/DBLP/" + BIBTYPE + "/REFLEXICA/2000_Reflexica_output_CRF.txt";

    public static String BIBANALYZER_OUTPUT_SPRINGER = "/home/niko/Desktop/CRF_and_Reflexica/DBLP/" + BIBTYPE + "/CRF_output/SPRINGER_tagged_combined.txt";
    public static String BIBANALYZER_OUTPUT_DBLP = "/home/niko/Desktop/CRF_and_Reflexica/DBLP/" + BIBTYPE + "/CRF_output/DBLP_tagged_combined.txt";

    public static String WEBSERVICE_OUTPUT_SPRINGER = "/home/niko/Desktop/CRF_and_Reflexica/DBLP/" + BIBTYPE + "/WEBSERVICE/WEBSERVICE_SPRINGER_tagged_combined.txt";
    public static String WEBSERVICE_OUTPUT_DBLP = "/home/niko/Desktop/CRF_and_Reflexica/DBLP/" + BIBTYPE + "/WEBSERVICE/WEBSERVICE_DBLP_tagged_combined.txt";

    
    /**
     * 
     * @param args
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {

        if(args.length==2) {
            GOLD_OUTPUT = args[0];
            WEBSERVICE_OUTPUT_DBLP = args[1];
        }
        
        compareTwoAnalyses(GOLD_OUTPUT, WEBSERVICE_OUTPUT_DBLP);
        
    }
        
    public static ArrayList<Integer> compareTwoAnalyses(String compareOne, String compareWith) throws FileNotFoundException {
    
        ArrayList<Integer> linesWithSameNumberOfTokens = new ArrayList<>();
        
        Scanner s1 = new Scanner(new File(compareOne));
        ArrayList<ArrayList<String[]>> references = new ArrayList<>();
        ArrayList<String[]> tokens = new ArrayList<>();
        while (s1.hasNextLine()) {
            String aLine = s1.nextLine().trim();

            if (aLine.length() == 0) {
                // Add sentence to list.
                references.add(tokens);
                tokens = new ArrayList<>();
            } else {
                String[] tok = aLine.split("\\s");
                tokens.add(tok);
            }
        }
        s1.close();
        for (int i = 0; i < references.size(); i++) {
            ArrayList<String[]> toks = references.get(i);
            for (String[] t : toks) {
                //System.out.print(t[1] + " ");
            }
            //System.out.println();
        }
        //System.out.println(references.size());

        Scanner s2 = new Scanner(new File(compareWith), "UTF-8");
        ArrayList<ArrayList<String[]>> references2 = new ArrayList<>();
        ArrayList<String[]> tokens2 = new ArrayList<>();
        while (s2.hasNextLine()) {
            String aLine2 = s2.nextLine().trim();
            if (aLine2.length() == 0) {
                // Add sentence to list.
                references2.add(tokens2);
                tokens2 = new ArrayList<>();
            } else {
                String[] tok2 = aLine2.split("\\s");
                tokens2.add(tok2);
            }
        }
        s2.close();
        for (int i = 0; i < references2.size(); i++) {
            ArrayList<String[]> toks2 = references2.get(i);
            for (String[] t : toks2) {
                //System.out.print(t[1] + " ");
            }
            //System.out.println();
        }
        //System.out.println(references2.size());

        // Compate the two.
        int notComparable = 0;
        System.out.println("Num references in 1: " + references.size() + " vs. num references in 2: " + references2.size() + "\n\n");
        if(references.size() != references2.size()) {
            System.out.println("Number of references in the two files is not equal.");
            System.exit(0);
        }
        
        
        for (int i = 0; i < references.size(); i++) {

            ArrayList<String[]> toksGOLD = references.get(i);
            ArrayList<String[]> toksOTHER = references2.get(i);

            if (toksGOLD.size() != toksOTHER.size()) {
                System.out.println("Length mismatch: sentence(" + i + "):");
                notComparable++;
                for (String[] t : toksGOLD) {
                    //    System.out.print(t[0] + " ");
                }
                //System.out.println();
                for (String[] t : toksOTHER) {
                    //    System.out.print(t[1] + " ");
                }
                //System.out.println();
                //System.out.println();
                // System.exit(0);
            } else {
                
                
                    linesWithSameNumberOfTokens.add(i);
                
                boolean atLeastOneTokenDifferent = false;
                // Everything alright.
                // Compare the two.
                // Check if they really have the exact same tokens!
                for (int g = 0; g < toksGOLD.size(); g++) {
                    // careful. we use  – whereas reflexica uses -
                    // and sometimes the other way round.
                    // Also check the quotes.
                    // – -   
                    // - –
                    // − –
                    // ’ '
                    // Gold annotations have their tokens at the first position.
                    // All the other annotations have their tokens at the second position.
                    String goldTok = toksGOLD.get(g)[0].replace("-", "–").replace("−", "–").replace("’", "'");
                    String otherTok = toksOTHER.get(g)[1].replace("-", "–").replace("−", "–").replace("’", "'");

                    if (!goldTok.equals(otherTok)) {
                        //System.out.println("->" + goldTok + "<-vs.->" + otherTok + "<-");
                        atLeastOneTokenDifferent = true;
                        //notComparable++;
                        break;
                    } else {
                        //System.out.print(goldTok + "-" + reflexTok);
                    }

                }

                // Everything alright.
                if (!atLeastOneTokenDifferent) {
                    //System.out.println(i);
                }
                
                
                
            }
        }
        System.out.println("\n\nnot comparable: " + notComparable);
        
        return linesWithSameNumberOfTokens;
    }

    
    
    /**
     * 
     * @param linesWithSameNumberOfTokens
     * @param exportDir
     * @param compareOne
     * @param compareWith
     * @throws FileNotFoundException 
     */
    public static void exportAccordances(ArrayList<Integer> linesWithSameNumberOfTokens, String exportDir, String compareOne, String compareWith) throws FileNotFoundException {
        String compareOneStr = compareOne;
        if(compareOne.contains("/"))
            compareOneStr = compareOne.substring(compareOne.lastIndexOf("/")+1);
        String compareWithStr = compareWith;
        if(compareWith.contains("/"))
            compareWithStr = compareWith.substring(compareWith.lastIndexOf("/")+1);
        
        PrintWriter w = new PrintWriter(new File(exportDir + "/accordances-" + compareOneStr + "-vs-" + compareWithStr));
        for(int l : linesWithSameNumberOfTokens) {
            w.write(l + "\n");
        }
        w.flush();
        w.close();
    }
    
    

}
