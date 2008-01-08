/* ===============================================================
 The Tokyo Project is hosted on Sourceforge:
 http://sourceforge.net/projects/tokyo/
 
 Copyright (c) 2005-2008 Eric Bréchemier
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
  Copyright (c) 2005-2008 Eric Bréchemier <tokyo@eric.brechemier.name>
  
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
package net.sf.tokyo.test;

import net.sf.tokyo.ITokyoNaut;

/**
 * Simple coverage test of Tokyo API based on NullTokyoNaut.
 */
public class NullTokyoNautTest
{
  public static void main(String[] args)
  {
    ITokyoNaut nullTokyoNautSrc = new NullTokyoNaut();
    ITokyoNaut nullTokyoNautDst = new NullTokyoNaut();
    byte[] data = new byte[500];
    int[] meta 
      = new int[]
      {
        ITokyoNaut.VERSION_NANA,
        ITokyoNaut.LANGUAGE_BINARY,
        ITokyoNaut.TOKEN_SPARK,
        ITokyoNaut.LEFT_START,
        0, 
        0,
        ITokyoNaut.RIGHT_END
      };
    
    nullTokyoNautSrc.plug(nullTokyoNautDst);
    
    int step = 0;
    final int STEP_LIMIT = 3;
    while( (step++ < STEP_LIMIT) && !nullTokyoNautDst.areWeThereYet(meta,data) )
    {
      if ( meta[ITokyoNaut.VERSION] == ITokyoNaut.VERSION_NANA)
      {
        System.out.println
          (  
             "Step: "+step+"\n\t"
            +"Data Item:\n\t"
            +"Language: "+getLanguageName(meta[ITokyoNaut.LANGUAGE])+formatMetaHex(meta[ITokyoNaut.LANGUAGE])+"\n\t"
            +"Token: "+getTokenName(meta[ITokyoNaut.TOKEN])+formatMetaHex(meta[ITokyoNaut.TOKEN])+"\n\t"
            +"Left Relation: "+getRelationName(meta[ITokyoNaut.LEFT])+formatLeftRight(meta[ITokyoNaut.LEFT])+"\n\t"
            +"Fragment Offset: "+formatOffset(meta[ITokyoNaut.OFFSET])+"\n\t"
            +"Fragment Length: "+formatLength(meta[ITokyoNaut.LENGTH])+"\n\t"
            +"Right Relation: "+getRelationName(meta[ITokyoNaut.RIGHT])+formatLeftRight(meta[ITokyoNaut.RIGHT])
          );
      }
    }
    
    nullTokyoNautSrc.unplug(nullTokyoNautDst);
    
    System.out.println("Tokyo API coverage test complete.");
  }
  
  public static String getLanguageName(int language)
  {
    switch(language)
    {
      case ITokyoNaut.LANGUAGE_BINARY:
        return "Binary";
      
      case ITokyoNaut.LANGUAGE_UNICODE_TEXT:
        return "ERROR";
      
      case ITokyoNaut.LANGUAGE_ERROR:
        return "ERROR";
        
      default:
        return "Other Language ["+language+"]";
    }
  }
  
  public static String getTokenName(int token)
  {
    switch(token)
    {
      case ITokyoNaut.TOKEN_SPARK:
        return "Spark to Start!";
      
      case ITokyoNaut.TOKEN_BINARY:
        return "Binary";
      
      default:
        return "Other Token ["+token+"]";
    }
  }
  
  public static String getRelationName(int relation)
  {
    switch(relation)
    {
      case ITokyoNaut.LEFT_START:
        return "Token Start";
        
      case ITokyoNaut.LEFT_CONTINUED:
      //case ITokyoNaut.RIGHT_CONTINUED:
        return "Token Continued";
        
      case ITokyoNaut.RIGHT_END:
        return "Token End";
        
      default:
        return "Unknown Relationship!";
    }
  }
  
  public static String formatLeftRight(int meta)
  {
    return " ["+(meta==1?"+":"")+meta+"]";
  }
  
  public static String formatMetaHex(int meta)
  {
    return " [Ox"+Integer.toHexString(meta)+"]";
  }
  
  public static String formatOffset(int offset)
  {
    return "Ox"+Integer.toHexString(offset);
  }
  
  public static String formatLength(int length)
  {
    return length+(length>=2?" bytes":" byte");
  }
  
}