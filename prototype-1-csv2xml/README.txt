==================================================================================
 The Tokyo Project is hosted on GitHub:
 https://github.com/eric-brechemier/Tokyo-Project
 
 Copyright (c) 2005-2007 Eric Bréchemier
 http://eric.brechemier.name
 Licensed under BSD License and/or MIT License.
 See: http://creativecommons.org/licenses/BSD/
==================================================================================

=========================================================
Title: Tokyo Project's Prototype 1 (csv2xml) README file
Last modified: 2007-12-19
By: Eric Bréchemier
Encoding: UTF-8
=========================================================

This directory contains the first prototype application developed to validate 
a processing chain on a simple use case: reordering lines in a csv file using XSLT.

This file describes the contents of this prototype. Additional details on the design
of this prototype are provided in the TokyoNautilus "PROCESS" Documentation, which takes
the development of this prototype as an example.

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

This prototype demonstrates how non-XML CSV data can be processed using XML tools, here an XSLT
transformation to reorder line elements, by using Tokyo Project to provide an XML view of this data.

The data is pipelined through different annotators, or TokyoNauts, until a conversion to SAX events
which form the input of the XSLT Transformation. SAX events resulting from the transformation are then
conveyed through a similar chain of TokyoNauts until a CSV file is produced in the end of the processing.
