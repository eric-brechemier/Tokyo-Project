/* ===============================================================
 The Tokyo Project is hosted on GitHub:
 https://github.com/eric-brechemier/Tokyo-Project
 
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

import net.sf.tokyo.prototype1.tokyonauts.UTF8ToUnicodeNaut;
import net.sf.tokyo.prototype1.tokyonauts.UnicodeToUTF8Naut;

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
    
    UTF8ToUnicodeNaut utf8decoder = new UTF8ToUnicodeNaut();
    UnicodeToUTF8Naut utf8encoder = new UnicodeToUTF8Naut();
    
    ITokyoNaut[] chain = {source, utf8decoder, utf8encoder, destination};
    
    int[] meta = new int[7];
    
    int step = 0;
    final int STEP_LIMIT = 99;
    
    while(  (step++ < STEP_LIMIT)  &&  !destination.areWeThereYet(chain,chain.length-1,meta)  )
    {
      if ( meta[ITokyoNaut.VERSION] == ITokyoNaut.VERSION_NANA)
      {
        System.out.println
          (  
             "Step: "+step+"\n\t"
            +"Language: 0x"+Integer.toHexString(meta[ITokyoNaut.LANGUAGE])+"\n\t"
            +"Token: 0x"+Integer.toHexString(meta[ITokyoNaut.TOKEN])+"\n\t"
            +"Left Relation: "+(meta[ITokyoNaut.LEFT]==1?"+":"")+meta[ITokyoNaut.LEFT]+"\n\t"
            +"Fragment Offset: "+meta[ITokyoNaut.OFFSET]+"\n\t"
            +"Fragment Length: "+meta[ITokyoNaut.LENGTH]+"\n\t"
            +"Right Relation: "+meta[ITokyoNaut.RIGHT]+"\n"
          );
      }
      else
      {
        System.out.println
          (  
             "Step: "+step+"\n\t"
            +"Language: 0x"+Integer.toHexString(meta[ITokyoNaut.LANGUAGE])+"\n\t"
          );
      }
    }
    
    System.out.println("Tokyo ProtoOne completed at step: "+(step-1) );
    
    if ( (step-1)==STEP_LIMIT)
      System.out.println("Step limit reached: "+STEP_LIMIT);
    
    if (meta[ITokyoNaut.LANGUAGE] == ITokyoNaut.LANGUAGE_ERROR)
    {
      System.out.println
          (  
             "Final Error: \n\t"
            +"Code: 0x"+Integer.toHexString(meta[ITokyoNaut.TOKEN])+"\n\t"
            +"at Offset: "+meta[ITokyoNaut.OFFSET]+"\n\t"
            +"and Length: "+meta[ITokyoNaut.LENGTH]+"\n\t"
          );
    }
/*    

import net.sf.tokyo.prototype1.tokyonauts.BinaryToJavaCharNaut;
import net.sf.tokyo.prototype1.tokyonauts.JavaCharToUnicodeNaut;

    if (args.length <2)
    {
      System.out.println("Usage: [ProtoOneMain] inFilePath outDirPath");
      return;
    }
    String inFilePath = args[0];
    String outDirPath = args[1];
    String outFilePath = outDirPath + "/result.csv";
    final String ENCODING = "UTF-8";
    final int STEP_LIMIT = 20;
    
    System.out.println("Starting ProtoOne:"
      + "\n  In: " + inFilePath
      + "\n  Out: " + outFilePath
      + "\n  Encoding: "+ENCODING
      + "\n  Step Limit: "+STEP_LIMIT
    );
    
    MainLoopNaut mainLoop = new MainLoopNaut();
    
    ITokyoNaut[] chain = new ITokyoNaut[]
      {
        new FileToBinaryNaut(inFilePath),
        new BinaryToFileNaut(outFilePath),
        mainLoop
      };
    
    mainLoop.plug(chain,chain.length-1);
    
    int step = 0;
    boolean hasCompleted = false;
    while( (step++ < STEP_LIMIT) && !hasCompleted )
    {
      System.out.println("Are We There Yet?");
      hasCompleted = mainLoop.areWeThereYet() )
      
      if ( _meta!=null && _meta[VERSION] == VERSION_NANA)
      {
        System.out.println
          (  
             "Step: "+step
            +"\n\tData Item:"
            +"\n\tLanguage: "+getLanguageName(_meta[LANGUAGE])+formatMetaHex(_meta[LANGUAGE])
            +"\n\tToken: "+getTokenName(_meta[TOKEN])+formatMetaHex(_meta[TOKEN])
            +"\n\tLeft Relation: "+getRelationName(_meta[LEFT])+formatLeftRight(_meta[LEFT])
            +"\n\tFragment Offset: "+formatOffset(_meta[OFFSET])
            +"\n\tFragment Length: "+formatLength(_meta[LENGTH])
            +"\n\tRight Relation: "+getRelationName(_meta[RIGHT])+formatLeftRight(_meta[RIGHT])
          );
      }
    }
    
    System.out.println("Tokyo ProtoOne completed: "+ );
    
    //BinaryToJavaCharNaut textDecoder = new BinaryToJavaCharNaut(ENCODING);
    //JavaCharToUnicodeNaut charDecoder = new JavaCharToUnicodeNaut();
*/
    
    /*
    ITokyoNaut readFile = new FileToBinaryNaut(inCsvFilePath);
    ITokyoNaut parseText = new BinaryToJavaCharNaut("UTF-8");
    ITokyoNaut parseUnicode = new JavaCharToUnicodeNaut();
    ITokyoNaut parseCSV = new UnicodeToCsvNaut();
    ITokyoNaut xslTransform = new XSLNaut(xslFilePath);
    ITokyoNaut writeCSV = new CsvToUnicodeNaut();
    ITokyoNaut writeUnicode = new UnicodeToJavaCharNaut();
    ITokyoNaut writeText = new JavaCharToBinaryNaut("UTF-8");
    ITokyoNaut writeFile = new BinaryToFileNaut(outCsvFilePath);
    
    ITokyoNaut spyZero = new NSpy(NSpy.STYLE_CHAR);
    ITokyoNaut spyOne = new NSpy(NSpy.STYLE_CHAR);
    ITokyoNaut spyTwo = new NSpy(NSpy.STYLE_CHAR);
        
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
  } 
}
