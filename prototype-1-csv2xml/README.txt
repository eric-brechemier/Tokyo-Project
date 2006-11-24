=========================================================
Title: Tokyo Project's Prototype 1 (csv2xml) README file
Last modified: 2006-11-24
By: Eric Bréchemier
Encoding: UTF-8
=========================================================

This directory contains the first prototype application developed to validate 
a processing chain on a simple use case: reordering lines in a csv file using XSLT.

This file describes the contents of this prototype. Additional details on the design
of this prototype are provided in the TokyoNautilus "API" Documentation.

Directory structure
********************

 /src
    /java   - Java source files
    /xslt   - XSLT source files
 /lib       - required libraries
 /data      - input data
 /out
    /class  - compiled classes
    /data   - output data

The build.xml file at the root of the directory allows to build and run the prototype,
by running the "ant" command (requires Java JDK and Apache ANT).


Components of the different steps
**********************************

This prototype has been designed incrementally in 5 steps. This section describes the goal
and components of each of those steps.

Step 1: Parse CSV and print it on System.out
-------

Input:  sample.csv
Java:   net/sf/tokyo/prototype1/Step1_PrintCSV.java


Step 2: Parse XML File, reorder "lines" using XSLT and print result on System.out
-------

Input:  sample.xml
XSLT:   net.sf.tokyo.prototype1.orderCountriesByContinent.xsl
        net.sf.tokyo.prototype1.orderCountriesByContinent.impl.xsl


Step 3: Parse XML File, filter events and print log, reorder "lines" using XSLT and print result
-------

Input:  sample.xml
Java:   net/sf/tokyo/prototype1/Step3_SaxFilter.java
XSLT:   net.sf.tokyo.prototype1.orderCountriesByContinent.xsl
        net.sf.tokyo.prototype1.orderCountriesByContinent.impl.xsl


Step 4: Parse CSV File, get XML View through TokyoNautils API, reorder and print result
-------

Input:  sample.csv
Java:   net/sf/tokyo/prototype1/Step4_CsvReaderNaut.java            - Custom CSV Parser
        net/sf/tokyo/prototype1/Step4_CsvToSaxNaut.java             - SAX Adapter for CSV Parser events
XSLT:   net.sf.tokyo.prototype1.orderCountriesByContinent.xsl
        net.sf.tokyo.prototype1.orderCountriesByContinent.impl.xsl


Step 5: Compare two processing chains, CSV to CSV and XML to CSV with same reordering processing
-------

Input:  sample.csv / sample.xml
Java:   net/sf/tokyo/prototype1/Step5_Sax.java                      - Specification of SAX Adapter as TokyoNautilus
        net/sf/tokyo/prototype1/Step5_CsvReaderNaut.java            - Custom CSV Parser
        net/sf/tokyo/prototype1/Step5_CsvToSaxNaut.java             - SAX Adapter for CSV Parser events
        net/sf/tokyo/prototype1/Step5_SaxToCsvNaut.java             - SAX Adapter for CSV Serializer events
        net/sf/tokyo/prototype1/Step5_CsvWriterNaut.java            - Custom CSV Serializer
XSLT:   net.sf.tokyo.prototype1.orderCountriesByContinent.xsl
        net.sf.tokyo.prototype1.orderCountriesByContinent.impl.xsl


Conclusion
-----------

Running the complete prototype results in two identical files, result.xmlchain.csv and 
result.csvchain.csv, which illustrates the equivalence of the processing with CSV/XML input.
