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
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

/**
 * Description:
 * 
 * 2nd experiment.
 * Out of a set of analyses, determine which one exhibits the greatest
 * label diversity.
 * 
 * 
 * But only for labels that we are interested in (ignore punctuation, e.g.)
 * 
 * 
 * Good example:
 * Jafar, S.A.: Capacity with Causal and Non-Causal Side Information - A Unified View. CoRR abs/cs/0511001 (2005). 
 * 
 * 
 *
 * @author niko
 */
public class RerankerGeneralAnalyses {
    
    // Select either of two CRF analyses (a sentence) at random.
    public static boolean RANDOM = false;
    public static boolean KEEP_REFLEXICA_HIGH_PRECISION_FIELDS_FROM_REFLEXICA = false;
    
    
    
    //public static final String DIR = "/home/niko/Desktop/CRF_and_Reflexica/SPRINGER/articles/";
    public static final String DIR = "/home/niko/Desktop/CRF_and_Reflexica/DBLP/article/";
    public static final String LABEL_MATRIX = DIR + "label_matrix.txt";
    
    public static final String NEW_EVAL_OUTPUT = DIR + "evaluation_substitution/subst_labeldiv.txt";
    
    public static void main(String[] args) throws FileNotFoundException {
        
        ArrayList<ArrayList<String>> goldSentences = new ArrayList<>();
        ArrayList<ArrayList<String>> reflexicaSentences = new ArrayList<>();
        ArrayList<ArrayList<String>> bibanalyzerSpringerSentences = new ArrayList<>();
        ArrayList<ArrayList<String>> bibanalyzerDBLPSentences = new ArrayList<>();
        ArrayList<ArrayList<String>> webserviceSpringerSentences = new ArrayList<>();
        ArrayList<ArrayList<String>> webserviceDBLPSentences = new ArrayList<>();
        
        
        Scanner s = new Scanner(new File(LABEL_MATRIX));
        
        ArrayList<String> goldSentence = new ArrayList<>();
        ArrayList<String> reflexicaSentence = new ArrayList<>();
        ArrayList<String> bibanalyzerSpringerSentence = new ArrayList<>();
        ArrayList<String> bibanalyzerDBLPSentence = new ArrayList<>();
        ArrayList<String> webserviceSpringerSentence = new ArrayList<>();
        ArrayList<String> webserviceDBLPSentence = new ArrayList<>();
        
        int numSentences = 0;
        int numLabels = 0;
        while(s.hasNextLine()) {
            String aLine = s.nextLine().trim();
            if(aLine.length()==0) {
                
                numSentences++;
                
                // Add sentences to lists.
                goldSentences.add(goldSentence);
                reflexicaSentences.add(reflexicaSentence);
                bibanalyzerSpringerSentences.add(bibanalyzerSpringerSentence);
                bibanalyzerDBLPSentences.add(bibanalyzerDBLPSentence);
                webserviceSpringerSentences.add(webserviceSpringerSentence);
                webserviceDBLPSentences.add(webserviceDBLPSentence);
                // Reset lists.
                goldSentence = new ArrayList<>();
                reflexicaSentence = new ArrayList<>();
                bibanalyzerSpringerSentence = new ArrayList<>();
                bibanalyzerDBLPSentence = new ArrayList<>();
                webserviceSpringerSentence = new ArrayList<>();
                webserviceDBLPSentence = new ArrayList<>();
            }
            else {
                
                numLabels++;
                //System.out.println(aLine);
                String[] split = aLine.split("\\t");
                String goldLabel = split[0];
                String reflexLabel = split[1];
                String bibanalyzerSpringerLabel = split[2];
                String bibanalyzerDBLPLabel = split[3];
                String webserviceSpringerLabel = split[4];
                String webserviceDBLPLabel = split[5];
                
                
                goldSentence.add(goldLabel);
                reflexicaSentence.add(reflexLabel);
                bibanalyzerSpringerSentence.add(bibanalyzerSpringerLabel);
                bibanalyzerDBLPSentence.add(bibanalyzerDBLPLabel);
                webserviceSpringerSentence.add(webserviceSpringerLabel);
                webserviceDBLPSentence.add(webserviceDBLPLabel);
                
            }
            
        }
        
        s.close();
        
        
        ArrayList<ArrayList<ArrayList<String>>> list = new ArrayList<>
                ();
        
        list.add(goldSentences);
        list.add(reflexicaSentences);
        list.add(bibanalyzerSpringerSentences);
        list.add(bibanalyzerDBLPSentences);
        list.add(webserviceSpringerSentences);
        list.add(webserviceDBLPSentences);
        
        
        
        
        int[][] labelDiversityCube = new int[numSentences][list.size()];
        // For every CRF analysis.
        for(int i = 0; i < list.size(); i++) {
            ArrayList<ArrayList<String>> aCrfAnalysis = list.get(i);
            // For every sentence.
            for(int j = 0; j < aCrfAnalysis.size(); j++) {
                ArrayList<String> aSentence = aCrfAnalysis.get(j);
                
                
                // Compute the diversity of fields for this sentenceanalysis.
                // For every token and its associated label.
                int numDifferentLabels = 0;
                HashSet<String> labelDiversity = new HashSet<>();
                for(int t = 0; t < aSentence.size(); t++) {
                    String aLabel = aSentence.get(t);
                    if(labelDiversity.contains(aLabel)) {
                        
                    }
                    else {
                        if(LabelMatrixEvaluatorSubstituted.labels.contains(aLabel)) {
                            numDifferentLabels++;
                        }
                    }
                    labelDiversity.add(aLabel);
                    
                }
                //System.out.print(numDifferentLabels +  " ");
                labelDiversityCube[j][i] = numDifferentLabels;
            }
            System.out.println();
        }
        
        System.out.println("label diversity cube:");
        // Print out label diversity matrix.
        for(int i = 0; i < labelDiversityCube.length; i++)
   {
       System.out.print("Sent " + (i+1) + ": " );
      for(int j = 0; j < labelDiversityCube[i].length; j++)
      {
         System.out.print(labelDiversityCube[i][j] + " ");
      }
      System.out.println();
   }
        
        
        
        ArrayList<ArrayList<String>> substitutedSentences = new ArrayList<>();
        
        // Get the analysis which has most variation:
        
        for (int i = 0; i < labelDiversityCube.length; i++) {
            int globalHightestLabelDiv = -1;
            int winnerAnalysis = -1;
            // IGNORE GOLD!! Start with index 1.
            for (int j = 1; j < labelDiversityCube[i].length; j++) {
                int currlabelDiv = labelDiversityCube[i][j];
                // TODO: what happens in case of ties ?
                // > instead of >=
                if(currlabelDiv >= globalHightestLabelDiv) {
                    globalHightestLabelDiv = currlabelDiv;
                    winnerAnalysis = j;
                }
            }
            System.out.println("Analysis won: " + winnerAnalysis + " (sentence " + (i+1) + ")");
            substitutedSentences.add(list.get(winnerAnalysis).get(i));
        }

        
        
        System.out.println("New substituted sentences: " + substitutedSentences.size());
        
        
        
        // Write gold and substituted sentence to new evaluation output file.
        PrintWriter w = new PrintWriter(new File(NEW_EVAL_OUTPUT));
        for(int i = 0; i < substitutedSentences.size(); i++) {
            ArrayList<String> aSubstSent = substitutedSentences.get(i);
            ArrayList<String> aGoldSent = goldSentences.get(i);
            //System.out.println(aSubstSent.size() + "-" + aGoldSent.size());
            for(int j = 0; j < aSubstSent.size(); j++) {
                String substTok = aSubstSent.get(j);
                String goldTok = aGoldSent.get(j);
                w.write(goldTok + "\t" + substTok + "\n");
            }
            w.write("\n");
        }
        w.flush();
        w.close();
        
        
        
    }
 
    
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

