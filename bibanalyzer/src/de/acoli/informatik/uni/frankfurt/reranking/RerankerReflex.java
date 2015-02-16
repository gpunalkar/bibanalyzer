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
import java.util.Random;
import java.util.Scanner;

/**
 *
 * Use Reflex analysis and augment it with our Springer
 * bibanalyzer model where the output is not sufficiently annotated.
 *
 *
 *
 * @author niko
 */
public class RerankerReflex {

    // Select either of two CRF analyses (a sentence) at random.
    public static boolean RANDOM = false;
    public static boolean KEEP_REFLEXICA_HIGH_PRECISION_FIELDS_FROM_REFLEXICA = true;

    // Deprecated:
    // Reflexica is compared to:
    // 2: bibanalyzer Springer 
    // 3: bibanalyzer DBLP 
    // 4: Webservice Springer 
    // 5: Webservice DBLP 
    public static int ANALYSIS_TO_COMPARE_TO = 4;

    public static final String DIR = "/home/niko/Desktop/CRF_and_Reflexica/SPRINGER/chapters/";
    //public static final String DIR = "/home/niko/Desktop/CRF_and_Reflexica/DBLP/article/";
    public static final String LABEL_MATRIX = DIR + "label_matrix.txt";

    public static final String NEW_EVAL_OUTPUT = DIR + "evaluation_substitution/subst.txt";

    public static void main(String[] args) throws FileNotFoundException {
        
        rerankAnalyses(LABEL_MATRIX, NEW_EVAL_OUTPUT);
        
    }

    
    public static void rerankAnalyses(String labelMatrix, String output) throws FileNotFoundException {
    
    
        ArrayList<ArrayList<String>> tokenSentences = new ArrayList<>();
        ArrayList<ArrayList<String>> reflexicaSentences = new ArrayList<>();
        ArrayList<ArrayList<String>> bibanalyzerSpringerSentences = new ArrayList<>();

        Scanner s = new Scanner(new File(labelMatrix));

        ArrayList<String> tokenSentence = new ArrayList<String>();
        ArrayList<String> reflexicaSentence = new ArrayList<>();
        ArrayList<String> bibanalyzerSentence = new ArrayList<>();

        while (s.hasNextLine()) {
            String aLine = s.nextLine().trim();
            if (aLine.length() == 0) {

                // Add sentences to lists.
                tokenSentences.add(tokenSentence);
                reflexicaSentences.add(reflexicaSentence);
                bibanalyzerSpringerSentences.add(bibanalyzerSentence);
                // Reset lists.
                tokenSentence = new ArrayList<>();
                reflexicaSentence = new ArrayList<>();
                bibanalyzerSentence = new ArrayList<>();
            } else {
                //System.out.println(aLine);
                String[] split = aLine.split("\\t");
                String token = split[0];
                String reflexLabel = split[1];
                
                
                String bibanalyzerLabel = split[2];
                tokenSentence.add(token);
                reflexicaSentence.add(reflexLabel);
                bibanalyzerSentence.add(bibanalyzerLabel);

            }

        }

        s.close();

        // Now check every Reflexica sentence and see if it doesn't have enough annotations.
        // Say, more than 15 dummy annotations.
        // 27 seems to be optimal value for articles.
        // 14 for books!
        int numDummysInReflexica = 8; // TODO: Vary parameter values, make parameter estimation.

        System.out.println(reflexicaSentences.size() + " vs. " + bibanalyzerSpringerSentences.size());
        ArrayList<ArrayList<String>> substitutedSentences = new ArrayList<>();
        System.out.println(bibanalyzerSpringerSentences.get(1).get(5));
        int numSubstitutions = 0;
        for (int i = 0; i < reflexicaSentences.size(); i++) {
            ArrayList<String> aReflexicaSentence = reflexicaSentences.get(i);
            int numDummysInCurrentReflexicaSentence = 0;
            boolean substituteReflexicaSentence = false;
            for (String t : aReflexicaSentence) {
                if (t.equals("<dum>")) {
                    numDummysInCurrentReflexicaSentence++;
                    if (numDummysInCurrentReflexicaSentence > numDummysInReflexica) {
                        substituteReflexicaSentence = true;
                        break;
                    }
                }
            }

            if (RANDOM) {
                int randomNumber = randInt(1, 2);
                //System.out.println(randomNumber);
                if (randomNumber == 1) {
                    numSubstitutions++;
                    substitutedSentences.add(bibanalyzerSpringerSentences.get(i));
                } else {
                    substitutedSentences.add(reflexicaSentences.get(i));
                }

            } else {
                // Substitute this reflexica sentence by our Springer model analysis.
                if (substituteReflexicaSentence) {
                    numSubstitutions++;

                    ArrayList<String> bibanalyzerSpringerSentence = bibanalyzerSpringerSentences.get(i);

                    if (KEEP_REFLEXICA_HIGH_PRECISION_FIELDS_FROM_REFLEXICA) {
                        ArrayList<String> areflexicaSentence = reflexicaSentences.get(i);

                    // TODO:
                        // Keep the genereal structure of the bibanalyzer sentence,
                        // but if Reflexica found either of
                        // - FamilyName, ArticleTitle or JournalTitle (those have high precision)
                        // then re use the Reflexica tags instead of the bibanalyzer tags.
                        // Simply replace them.
                        for (int b = 0; b < bibanalyzerSpringerSentence.size(); b++) {
                            // Check the reflexica token for this springer token.
                            String reflToken = areflexicaSentence.get(b);
                            if (reflToken.equals("<ArticleTitle>")
                                    || reflToken.equals("<FamilyName>")
                                    || reflToken.equals("<Year>")
                                    || reflToken.equals("<JournalTitle>")
                                    || reflToken.equals("<Initials>")
                                    || reflToken.equals("<PublisherLocation>")
                                    || reflToken.equals("<PublisherName>")) {
                                // Substitute it.
                                bibanalyzerSpringerSentence.set(b, reflToken);
                            }
                        }
                    }

                    substitutedSentences.add(bibanalyzerSpringerSentence);
                } else {
                    // Keep it.
                    substitutedSentences.add(reflexicaSentences.get(i));
                }
            }

        }

        System.out.println("New substituted sentences: " + substitutedSentences.size());
        System.out.println("Number of substitutions: " + numSubstitutions);

        // Write gold and substituted sentence to new evaluation output file.
        PrintWriter w = new PrintWriter(new File(output));
        for (int i = 0; i < substitutedSentences.size(); i++) {
            ArrayList<String> aSubstSent = substitutedSentences.get(i);
            ArrayList<String> aTokenSent = tokenSentences.get(i);
            
            //System.out.println(aSubstSent.size() + "-" + aGoldSent.size());
            for (int j = 0; j < aSubstSent.size(); j++) {
                String substTok = aSubstSent.get(j);
                String aToken = aTokenSent.get(j);
                w.write(substTok + "\t" + aToken + "\n");
            }
            w.write("\n");
        }
        w.flush();
        w.close();

    }

    
    /**
     * 
     * @param min
     * @param max
     * @return 
     */
    public static int randInt(int min, int max) {

    // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
