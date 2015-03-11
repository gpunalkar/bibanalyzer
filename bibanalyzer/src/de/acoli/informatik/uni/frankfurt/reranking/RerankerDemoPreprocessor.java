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

import de.acoli.informatik.uni.frankfurt.classifier.BibAnalyzer;
import de.acoli.informatik.uni.frankfurt.crfformat.ReflexicaToCRFFormat;
import de.acoli.informatik.uni.frankfurt.processing.ReferenceUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * Idea: Use Reference Manager tokenization
 * as input to BibAnalyzer pipeline.
 * 
 * -> Combine both analyses into one.
 * 
 * @author niko
 */
public class RerankerDemoPreprocessor {
    
    public static String REFLEXICA_HTML = "input/reranker/Reflexica/1082References/Reflexica/References-out.utf8.htm";
    public static String REFLEXICA_CRF = "input/reranker/Reflexica/1082References/Reflexica/References-out.utf8.htm.txt";
    public static String REFLEXICA_ONELINERS = "input/reranker/Reflexica/1082References/Reflexica/References-out.utf8.htm.oneliners.txt";
    
    
    
    
    public static void main(String[] args) throws FileNotFoundException {
        
        // 1. Convert Reflexica HTML to CRF token format.
        ReflexicaToCRFFormat.convertReflexicaHTMLVisualizationToCRFOutput(REFLEXICA_HTML, REFLEXICA_CRF);
    
        
        // 2. Generate one-line (plaintext) references from (Reflexica) CRF format.
        // ("untokenize").
        ArrayList<String> untokenizedReferences = ReferenceUtil.untokenize(REFLEXICA_CRF);
        PrintWriter w = new PrintWriter(new File(REFLEXICA_ONELINERS));
        
        for(String aOneliner : untokenizedReferences) {
            w.write(aOneliner + "\n");
        }
        w.flush();
        w.close();
        
        // 3. Run Bibanalyzer on these plaintext references.
        // TODO: Call manually.
        
        
        
        
        
        
        
        
        
    }
}
