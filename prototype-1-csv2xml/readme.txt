=========================================================
Title: Tokyo Project's Prototype 1 (csv2xml) README file
Last modified: 2006-11-18
By: Eric Bréchemier
Encoding: UTF-8
=========================================================

This directory contains a prototype application developed to validate 
a possible process on a simple use case.

The latest version of this prototype will be based on a new simple pull API for "non-XML" data,
the TokyoNautilus API, composed of one interface ITokyoNaut.
See doc/API.txt for more details on this API:
https://svn.sourceforge.net/svnroot/tokyo/trunk/doc/API.txt

Prototype Description and Architecture:
***************************************

The cvs2xml prototype aims to demonstrate how TokyoNautilus API implementations
can be designed in a smooth and progressive way.

TODO: describe steps and components of the prototype



Directory structure:
 /src
    /java   - Java source files
    /xslt   - XSLT source files
 /lib       - required libraries
 /in
    /data   - input data
 /out
    /class  - compiled classes
    /data   - output data


History:
*******
                               
A first version of this prototype, based on XmlPull API (www.xmlpull.org/), 
had been successfully completed between August 15th and August 16th 2005, 
fulfilling the initial objectives: without any modification to Saxon processor code,
we could transform a custom format text file to a file in the same custom format,
using XSLT to reorder data entries representing countries by continent.
It required:
  - implementing custom parser and serializer
  - developping a wrapper for parser and serializer to adapt them to SAX2 API handled by Saxon
  - specify parser to use using command line option -x
  - specify serializer to use using output method attribute (inside XSLT stylesheet)
  
I usually separate XSLT transformations in two files: 
  - one for header, containing import/include definitions and output declaration
  - one for actual implementation
According to this convention, no modification was made in XSLT implementation.

Conclusion: The process defined as part of Tokyo Project allows Text-to-Text conversion.
**********
No major issue appeared during this experimentation as for Binary-to-Binary conversion, 
which will be demonstrated during the next phase of Tokyo Project.


QUICK START - STEP BY STEP
0) install Java JDK 1.4 or higher - http://java.sun.com/j2se/
1) install Apache Ant - http://ant.apache.org/
2) open command line window and change directory to the "prototype" directory
3) type ant to build and run the prototype application

Dependencies will be downloaded by "third-party" Ant script.

    
The goal of this prototype is to implement a parser for a 
simple text format and plug it as input for an XSLT processor.

File format:
([country],[capital city],[continent]\n)*

XML Mapping:
<file path="[full file path]">
  <country name="[country]" capital="[capital city]" continent="[continent]" />
</file>

MAP File:
<map>
  <MACRO id="COUNTRY-ELEMENT">
    <element name="country"></element>
  </MACRO>
    
  <rule from="*">
    <element name="file" to="start"/>
  </rule>
  
  <rule from="start">
    <go to="@path">
      <attribute name="path"><fullFilePath /></attribute>
    </go>
    <go to="country">
      <MACRO ref="COUNTRY-ELEMENT"/>
    </go>
  </rule>
  
  <rule from="country">
    <go to="@name">
      <attribute name="name"><csvValue end=","/></attribute>
    </go>
    <go to="@capital">
      <attribute name="capital"><csvValue end=","/></attribute>
    </go>
    <go to="@continent">
      <attribute name="continent"><csvValue end="\n"/></attribute>
    </go>
    <go to="country">
      <MACRO ref="COUNTRY-ELEMENT"/>
    </go>
  </rule>
  
</map>

AVOID DUPLICATES, USING MACRO OR: "|"

<map>
  <rule from="*">
    <element name="file" to="start"><go/></element>
  </rule>
  
  <rule from="start|country">
    <go to="country">
      <element name="country"><go/></element>
    </go>
  </rule>
  
  <rule from="start">
    <go to="@path">
      <attribute name="path"><fullFilePath /></attribute>
    </go>
  </rule>
  
  <rule from="country">
    <go to="@name">
      <attribute name="name"><csvValue end=","/></attribute>
    </go>
    <go to="@capital">
      <attribute name="capital"><csvValue end=","/></attribute>
    </go>
    <go to="@continent">
      <attribute name="continent"><csvValue end="\n"/></attribute>
    </go>
  </rule>
</map>

HOW TO REPRESENT OPTIONAL ELEMENTS???
e.g. add number of inhabitants (attribute/element), optional
=> ANY ELEMENT IS, BY DEFINITION/NATURE, OPTIONAL

TODO: (based on map file above)
 * implement two common actions
   - <attribute name="path">
     read / write XML attribute, value defined by child content
   - <element name="country">
     read write XML element, potential value defined by child content

 * implement two specific actions:
   - <fullFilePath /> codec: 
      reads full file path from input file
      creates new file for full file path string
   - <csvValue end=","/>, <csvValue end="\n"/> codec:
      read string value until end separator
      write string value and append end separator

NOTA:
How to handle additional parameters?

NOTA:
Additional representation needed in order to handle multiplicities? 
min/max? range? count? position?

DEVELOPMENT TASKS: ([ ] TODO [X] DONE)
[X] Prepare simple CSV file with text data on countries and capitals
[X] Write simple XSLT transform reordering countries by continent name/country name
[X] Write Custom Data Parser in Java implementing org.xml.sax.XMLReader
    (SAX2 Parser API, one of the possible parser API handled by Saxon)
[X] Write Custom Data Serializer in Java implementing org.xml.sax.ContentHandler
    (SAX2 Serializer API, one of the possible serializer API handled by Saxon)
[X] Choose either Stax (STreaming API for XML processing) or xmlpull.org API
    => xmlpull.org API is the simplest choice for under the hood - base implementation
    Stax API can be provided very easily on top of it (e.g. once it is part of Java JDK API)
    (see Notes on Stax below)
[X] Rewrite Custom Data Parser as Pull Parser and plug in an adapter to SAX Parser
    I used the SAX Driver class provided by xmlpull.org, but had to correct one bug
    (clearly indicated in modified source file) concerning default namespace prefix handling.
[X] Rewrite Custom Data Serializer as Pull Serializer and plug in an adapter to SAX Serializer
[X] write two different "XSLT headers" for XML with indent / Custom output
[X] Write conclusion

KNOWN LIMITATIONS
[>] Saxon does not output attributes for root element - to be investigated
     
NOTES ON STAX
* Stax is not part of Java JDK yet (as of JDK 1.5.0)
* the specification includes BEA logos, patents/copyrights and cloudy license agreements
* the specification includes no reference to other XML Pull APIs!!
  only SAX and DOM seem to pre-exist it...
* Stax defines 2 APIs cursor API and event iterator API built on top of the previous one
* The subset for J2ME includes only cursor API (not event) and requires 5+3 interfaces/classes
* Paser API seems very close to xmlpull one, with additional methods
* XML Pull Serializer API seems more simple (e.g. less methods for same usage)
* The interest of encapsulating cursor in event iterator is not obvious
* Principle of Event Serializer is adding some events (I find the "writing event" metaphor unclear)
* NOTA: (interesting related to Tokyo Project)
  Stax specification defines Virtual Data Sources
  "In many applications, the XML data is stored or accessed in formats 
   other than its serialized format. The consuming application of the data 
   is given an XML view of the data and can move a pointer around to randomly 
   access the data."
  And in "Future Work" indicates:
  "Virtual Data Sources" - The API could be used as a layer of virtual data sources.
   
REFERENCE DOCUMENTATION:
 * Custom Parser for Saxon 6.5.4
 http://saxon.sourceforge.net/saxon6.5.4/using-xsl.html#Command-line  (see -x option)
 
 * Custom Serializer for Saxon 6.5.4
 http://saxon.sourceforge.net/saxon6.5.4/xsl-elements.html#xsl:output

 * XML Pull Parser API
 http://www.xmlpull.org/v1/download/unpacked/doc/quick_intro.html
 
 * XML Pull Serializer API
 http://www.xmlpull.org/v1/download/unpacked/doc/quick_write.html
 
 * XML Pull adapter for SAX2
 http://www.xmlpull.org/v1/doc/faq.html#SAX2_DRIVER
 
 * On Using XML Pull Parsing Java APIs
 Aleksander Slomiski, 15 March 2004
 http://www.xmlpull.org/history/index.html
 
 * JSR 173: Streaming API for XML
 http://www.jcp.org/en/jsr/detail?id=173
 
 * Saxon: Anatomy of an XSLT processor
 Michael Kay, 01 Feb 2001, Updated 20 Apr 2005
 http://www-128.ibm.com/developerworks/xml/library/x-xslt2/
 