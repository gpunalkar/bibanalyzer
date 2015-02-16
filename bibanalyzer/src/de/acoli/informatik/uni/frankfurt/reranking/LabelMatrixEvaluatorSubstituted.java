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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

/**
 *
 * Evaluates the "substituted" label matrix.
 * Substituted means: Reranked (combined/reorganized sentences/labels for
 * a particular CRF analysis (sentence).
 *
 * @author niko
 */
public class LabelMatrixEvaluatorSubstituted {

    // Gold--Predicted.
   
    public static final String LABEL_MATRIX = "/home/niko/Desktop/CRF_and_Reflexica/DBLP/"
            + "article/" +
           // "evaluation_substitution/subst.txt";
           "evaluation_substitution/subst_labeldiv.txt";
           
    
    
    // Evaluate substitution. DO NOT MODIFY.
    public static final int CRF_ANALYSIS = 1;
    
    
    
    public static ArrayList<String> labels = new ArrayList<String>();

    static {
        
        // General.
        labels.add("<Year>");
        
        
        labels.add("<Pages>");
        //labels.add("<FirstPage>");
        //labels.add("<LastPage>");
        
        
        labels.add("<Initials>");
        labels.add("<Prefix>");
        labels.add("<FamilyName>");
        labels.add("<FirstName>");
        labels.add("<etal>");
        labels.add("<Suffix>");
        labels.add("<Particle>");
        labels.add("<Degrees>");
        labels.add("<InstitutionalAuthorName>");
        labels.add("<BibInstitutionalEditorName>");
        
        // Articles.
        labels.add("<ArticleTitle>");
        labels.add("<JournalTitle>");
        
        labels.add("<VolumeID>");
        labels.add("<IssueID>");
        
        
        
        labels.add("<Handle>");
        labels.add("<BibComments>");
        labels.add("<RefSource>");
        labels.add("<EquationSource>");
        
        
        labels.add("<Url>");
        
        
        
        // Books.
        labels.add("<BookTitle>");
        labels.add("<PublisherName>");
        labels.add("<PublisherLocation>");
        labels.add("<SeriesTitle>");
        
        
        labels.add("<Isbn>");
        labels.add("<Doi>");
        
        // Chapters.
        labels.add("<ChapterTitle>");
        labels.add("<NumberInSeries>");
        
        
        
        
    }

    // Reads in a predicted output Mallet file and evaluates accuracy.
    public static void main(String[] args) throws FileNotFoundException {
        
        
        // Extract all labels.
        HashSet<String> labelSet = new HashSet<String>();
        Scanner sL = new Scanner(new File(LABEL_MATRIX));
        while(sL.hasNextLine()) {
            //String l= sL.nextLine().split("\\t")[0];
            //labelSet.add(l.trim()); 
            String[] labelsOfLine = sL.nextLine().split("\\t");
            for(String l : labelsOfLine) { labelSet.add(l.trim()); }
        }
        sL.close();
        
        //System.out.println("labels");
        for(String l : labelSet) {
            //System.out.println(l);
        }
        
        
        

        //*********************************//
        // Evaluate overall accuracy.
        Scanner sAll = new Scanner(new File(LABEL_MATRIX));
        int correct = 0;
        int wrong = 0;
        int Labelcorrect = 0;
        int Labelwrong = 0;
        while (sAll.hasNextLine()) {
            String aLine = sAll.nextLine().trim();
            if (aLine.length() > 0) {
                String[] items = aLine.split("\\t");
                
                String gold = items[0];
                String predicted = items[CRF_ANALYSIS];
                
                
                // Make this additional check to only evaluate 
                // the labels that we have specified.
                if(labels.contains(gold)) {
                // Overall computation of accuracy!
                if (predicted.equalsIgnoreCase(gold)) {
                    Labelcorrect++;
                } else {
                    Labelwrong++;
                }
                }
                
                
                if (predicted.equalsIgnoreCase(gold)) {
                    correct++;
                } else {
                    wrong++;
                }
                
            }
        }
        sAll.close();
        System.out.println("### All tokens:");
        System.out.println("All labels: " + (correct + wrong));
        System.out.println("correct: " + correct + " wrong: " + wrong);
        System.out.print("Ratio: " + (double) correct / (correct + wrong) * 100);
        System.out.println("% accuracy.\n");
        
        System.out.println("### Only tokens where labels were specified.");
        System.out.println("All labels: " + (Labelcorrect + Labelwrong));
        System.out.println("correct: " + Labelcorrect + " wrong: " + Labelwrong);
        System.out.print("Ratio: " + (double) Labelcorrect / (Labelcorrect + Labelwrong) * 100);
        System.out.println("% accuracy.\n\n\n");

        
        
        //********** 
        // Evaluate accuracy measures for each label.
        
        for (String LABEL : labels) {
            Scanner s = new Scanner(new File(LABEL_MATRIX));


            int labelPredicted = 0; // all hits by program.
            int labelCorrect = 0; // real hit.
            int labelTagCount = 0; // should be title. (from gold) // num gold title tags.

            while (s.hasNextLine()) {
                String aLine = s.nextLine().trim();
                if (aLine.length() > 0) {
                    String[] items = aLine.split("\\t");
                
                    
                String gold = items[0];
                String predicted = items[CRF_ANALYSIS];
                //   System.out.println("pred: " + predicted + " gold: " + gold + " tok: " + token);

                    // <JournalTitle>.
                    // We have match! (gold equals predicted).
                    if (predicted.equalsIgnoreCase(gold) && gold.equalsIgnoreCase(LABEL)) {
                        labelPredicted++;
                        labelCorrect++;
                        labelTagCount++;
                    } // We predict "journaltitle" but it should not be "journaltitle".
                    else if (predicted.equalsIgnoreCase(LABEL) && !gold.equalsIgnoreCase(LABEL)) {
                        //System.out.println(aLine);;
                        labelPredicted++;
                    } // Gold is "journaltitle", but we predict something else.
                    else if (gold.equalsIgnoreCase(LABEL)) {
                        //System.out.println(aLine);
                        labelTagCount++;
                    } else {
                    // Something which does not involve "journaltitle".
                        // Not relevant for computations.
                        //System.out.println(aLine);
                    }

                    
                }
            }
            s.close();

            
            System.out.println("**** ");
            System.out.println(LABEL);
            double precision = (double) labelCorrect / labelPredicted;
            double recall = (double) labelCorrect / labelTagCount;
            double F1 = (double) 2 * (precision * recall) / (precision + recall);
            System.out.println("number of gold labels: \t" + labelTagCount);
            System.out.println("number of predictions: \t" + labelPredicted);
            System.out.println("correct matches: \t" + labelCorrect);

            System.out.println(" precision: " + precision + ", recall: " + recall + ", F1: " + F1);
            System.out.println();
        }
    }
}
