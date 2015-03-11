How to "rerank" two analysis A and B

A - Reflex
B - Bibanalyzer


1. Produce an HTML ReferenceManager output and convert it to (save it as) UTF-8

2. Run RerankerDemoPreprocessor.java and specify the path of the HTML file.
   The tool produces a) the CRF token format AND 
                     b) a one-line-separated file of plaintext references.
   
3. (Manually) run bibanalyzer on the one-line-separated file.
    Remove the last "dummy" reference from the output file. ("-... tagged_combined.txt")
   
4. Run RerankerDemo2.java and specify the paths to REFLEXICA_CRF and BIBANALYZER_CRF accordingly.