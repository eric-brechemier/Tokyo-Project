/* ===============================================================
 The Tokyo Project is hosted on Sourceforge:
 http://sourceforge.net/projects/tokyo/
 
 Copyright (c) 2005-2007 Eric Bréchemier
 http://eric.brechemier.name
 Licensed under BSD License and/or MIT License.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                         BSD License
                             ~~~
             http://creativecommons.org/licenses/BSD/
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                          MIT License
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  Copyright (c) 2005-2007 Eric Bréchemier <tokyo@eric.brechemier.name>
  
  Permission is hereby granted, free of charge, to any person
  obtaining a copy of this software and associated documentation
  files (the "Software"), to deal in the Software without
  restriction, including without limitation the rights to use,
  copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the
  Software is furnished to do so, subject to the following
  conditions:

  The above copyright notice and this permission notice shall be
  included in all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
  OTHER DEALINGS IN THE SOFTWARE.
================================================================== */
package net.sf.tokyo.prototype1;

import net.sf.tokyo.ITokyoNaut;

import net.sf.tokyo.prototype1.tokyonauts.FileToBinaryNaut;
import net.sf.tokyo.prototype1.tokyonauts.BinaryToFileNaut;

public class ProtoOneMainLoop
{
  public static void main(String[] args)
  {
    if (args.length <2)
    {
      System.out.println("Usage: [ProtoOneMain] inFilePath outDirPath");
      return;
    }
    String inFilePath = args[0];
    String outDirPath = args[1];
    String outFilePath = outDirPath + "/result.csv";
    
    System.out.println("Starting ProtoOne:"
      + "\n  In: " + inFilePath
      + "\n  Out: " + outFilePath
    );
    
    FileToBinaryNaut source = new FileToBinaryNaut(inFilePath);
    BinaryToFileNaut destination = new BinaryToFileNaut(outFilePath);
    
    source.plug(destination);
    
    int[] meta = new int[] 
      {
        ITokyoNaut.VERSION_NANA, 
        ITokyoNaut.LANGUAGE_BINARY, ITokyoNaut.TOKEN_SPARK,
        ITokyoNaut.LEFT_START, 0, 0, ITokyoNaut.RIGHT_END
      };
    byte[] data = new byte[10];
    
    int step = 0;
    final int STEP_LIMIT = 99;
    
    while(  !destination.areWeThereYet(meta,data)  &&  (step++ < STEP_LIMIT)  )
    {
      if ( meta[ITokyoNaut.VERSION] == ITokyoNaut.VERSION_NANA)
      {
        System.out.println
          (  
             "Step: "+step+"\n\t"
            +"Language: "+meta[ITokyoNaut.LANGUAGE]+"\n\t"
            +"Token: "+meta[ITokyoNaut.TOKEN]+"\n\t"
            +"Left Relation: "+(meta[ITokyoNaut.LEFT]==1?"+":"")+meta[ITokyoNaut.LEFT]+"\n\t"
            +"Fragment Offset: "+meta[ITokyoNaut.OFFSET]+"\n\t"
            +"Fragment Length: "+meta[ITokyoNaut.LENGTH]+"\n\t"
            +"Right Relation: "+meta[ITokyoNaut.RIGHT]+"\n"
          );
      }
    }
    
    source.unplug(destination);
  } 
}

/*
import net.sf.tokyo.prototype1.tokyonauts.BinaryToJavaCharsNaut;
import net.sf.tokyo.prototype1.tokyonauts.JavaCharsToUnicodeNaut;
import net.sf.tokyo.prototype1.tokyonauts.UnicodeToCsvNaut;
import net.sf.tokyo.prototype1.tokyonauts.XSLNaut;
import net.sf.tokyo.prototype1.tokyonauts.CsvToUnicodeNaut;
import net.sf.tokyo.prototype1.tokyonauts.UnicodeToJavaCharsNaut;
import net.sf.tokyo.prototype1.tokyonauts.JavaCharsToBinaryNaut;


    if (args.length <3)
    {
      System.out.println("Usage: [ProtoOneMain] inCsvFilePath xslFilePath outDir");
      return;
    }
    String inCsvFilePath = args[0];
    String xslFilePath = args[1];
    String outDirPath = args[2];
    String outCsvFilePath = outDirPath + "/result.csv";
    
    System.out.println("Starting ProtoOne:"
      + "\n  InCsv: " + inCsvFilePath
      + "\n  Xsl: " + xslFilePath
      + "\n  outDir: " + outDirPath
    );
    
    ITokyoNaut readFile = new FileToBinaryNaut(inCsvFilePath);
    ITokyoNaut parseText = new BinaryToJavaCharsNaut("UTF-8");
    ITokyoNaut parseUnicode = new JavaCharsToUnicodeNaut();
    ITokyoNaut parseCSV = new UnicodeToCsvNaut();
    ITokyoNaut xslTransform = new XSLNaut(xslFilePath);
    ITokyoNaut writeCSV = new CsvToUnicodeNaut();
    ITokyoNaut writeUnicode = new UnicodeToJavaCharsNaut();
    ITokyoNaut writeText = new JavaCharsToBinaryNaut("UTF-8");
    ITokyoNaut writeFile = new BinaryToFileNaut(outCsvFilePath);
    
    ITokyoNaut spyZero = new NSpy(NSpy.STYLE_CHAR);
    ITokyoNaut spyOne = new NSpy(NSpy.STYLE_CHAR);
    ITokyoNaut spyTwo = new NSpy(NSpy.STYLE_CHAR);
    
    byte[] data = new byte[20];
    int[] meta 
      = new int[]
      {
        ITokyoNaut.VERSION_NANA,
        ITokyoNaut.LANGUAGE_BINARY,
        ITokyoNaut.TOKEN_BINARY,
        ITokyoNaut.LEFT_START,
        0, 
        0,
        ITokyoNaut.RIGHT_CONTINUED
      };
    
    readFile
      //.plug(spyZero)
      .plug(parseText)
        .plug(parseUnicode)
          .plug(parseCSV)
            //.plug(spyOne)
            .plug(xslTransform)
            //.plug(spyTwo)
          .plug(writeCSV)
        .plug(writeUnicode)
      .plug(writeText)
    .plug(writeFile);
    
    int step = 0;
    final int STEP_LIMIT = 99;
    while(  !writeFile.areWeThereYet(meta,data)  &&  (step++ < STEP_LIMIT)  )
    {
      System.out.println("Running... Step "+step+" - "+meta[ITokyoNaut.LENGTH]+" byte(s) written.");
    }
    
    if (meta[ITokyoNaut.LANGUAGE]==ITokyoNaut.LANGUAGE_ERROR)
      System.err.println("Processing Terminated with Error Code: "+Integer.toHexString(meta[ITokyoNaut.TOKEN]));
    
    readFile
      //.plug(spyZero)
      .unplug(parseText)
        .unplug(parseUnicode)
          .unplug(parseCSV)
            //.plug(spyOne)
            .unplug(xslTransform)
            //.plug(spyTwo)
          .unplug(writeCSV)
        .unplug(writeUnicode)
      .unplug(writeText)
    .unplug(writeFile);
    */
    
    // TODO REMOVE EVERYTHING BELOW, AFTER GETTING BACK USEFUL STUFF (IF ANY)
    /*
    // XML to XML using XSLT "Same But Different" Transform //
    System.setProperty
      (
      "javax.xml.transform.TransformerFactory", 
      "net.sf.saxon.TransformerFactoryImpl"
      );
    
    // TODO: adapt code from SAXON TrAX samples
    
    TransformerFactory tfactory = TransformerFactory.newInstance();

        // Create a transformer for the stylesheet.
        Transformer transformer =
            tfactory.newTransformer(new StreamSource(xslID));

        // Transform the source XML to System.out.
        transformer.transform(new StreamSource(sourceID),
                              new StreamResult(new File("exampleSimple2.out")));
    */