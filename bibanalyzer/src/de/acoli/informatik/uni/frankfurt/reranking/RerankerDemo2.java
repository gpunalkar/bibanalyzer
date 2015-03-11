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

import de.acoli.informatik.uni.frankfurt.crfformat.ReflexicaToCRFFormat;
import de.acoli.informatik.uni.frankfurt.visualization.CRFVisualizer;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 *
 * @author niko
 */
public class RerankerDemo2 {
    
    static String DIR = "input/reranker/Reflexica/1082References";
    
    // Reflex analysis.
    public static String REFLEXICA_CRF =  DIR + "/Reflexica/References-out.utf8.htm.txt";
    
    // Bibanalyzer analysis.
    public static String BIBANALYZER_CRF =  DIR + "/Bibanalyzer/References-out-bibanalyzer-tagged_combined.txt";
    
    
    // Accordances of the two analyses.
    public static String ACCORDANCE_DIR =  DIR + "/accordances/";
    // Label matrix.
    public static String LABEL_MATRIX =  DIR + "/Reranked/label.matrix";
    // New reranked output (Reflexica vs. CRF)
    public static String RERANKED_OUTPUT =  DIR + "/Reranked/rerankerout.txt";
    // New reranked output (pretty print HTML visualization)
    public static String RERANKED_OUTPUT_HTML =  DIR + "/Reranked/rerankerout.txt.html";
    
    
    
    /**
     * 
     * @param args
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        
        
        
        // Detect the common subset of references to be analyzed.
        ArrayList<Integer> referencesWithSameNumberOfTokens = CRFFormatsComparator.compareTwoAnalyses(REFLEXICA_CRF, BIBANALYZER_CRF);
        // Write them to accordance dir.
        CRFFormatsComparator.exportAccordances(referencesWithSameNumberOfTokens, ACCORDANCE_DIR, REFLEXICA_CRF, BIBANALYZER_CRF);
        
        
        // Generate label matrix for all references with the same number of tokens.
        // TODO: Allow for varying numbers of tokens.
        ArrayList<String> crfAnalyses = new ArrayList<>();
        crfAnalyses.add(REFLEXICA_CRF);
        crfAnalyses.add(BIBANALYZER_CRF);
        DifferentAnalysesIntoOneCRFFormatCombiner.generateLabelMatrix(ACCORDANCE_DIR, crfAnalyses, LABEL_MATRIX);
        
        
        // Combine their analyses into a better one. (Reranking mechanism).
        RerankerReflex.rerankAnalyses(LABEL_MATRIX, RERANKED_OUTPUT);
        // TODO: Postprocess and remove, e.g., <Title> tags within <Initials> and <FamilyName>...
        
        // Convert reranked result back to HTML.
        CRFVisualizer.visualizeCRFOutput(RERANKED_OUTPUT, RERANKED_OUTPUT_HTML);
        
        
    }
}
