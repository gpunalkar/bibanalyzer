/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.acoli.informatik.uni.frankfurt.reranking;

import de.acoli.informatik.uni.frankfurt.crfformat.ReflexicaToCRFFormat;
import java.io.FileNotFoundException;

/**
 *
 * @author niko
 */
public class ReflexicaHTMLtoCRFFormatDemo {
    
    //public static String REFLEXICA_HTML = "input/reranker/Reflexica/115_Refs.utf8.htm";
    //public static String REFLEXICA_CRF = "input/reranker/Reflexica/115_Refs.utf8.htm.txt";
    
    public static String REFLEXICA_HTML = "input/reranker/Reflexica/References-out.utf8.htm";
    public static String REFLEXICA_CRF = "input/reranker/Reflexica/References-out.utf8.htm.txt";
    
    
    public static void main(String[] args) throws FileNotFoundException {
        
        // Convert Reflexica HTML to CRF format.
        System.out.println("Converting Reflexica HTML to CRF format...");
        ReflexicaToCRFFormat.convertReflexicaHTMLVisualizationToCRFOutput(REFLEXICA_HTML, REFLEXICA_CRF);
        System.out.println("... done.");
        
        
    }
    
    
    
}
