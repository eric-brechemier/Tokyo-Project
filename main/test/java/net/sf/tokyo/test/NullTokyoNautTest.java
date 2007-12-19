/*
 * The Tokyo Project is hosted on Sourceforge:
 * http://sourceforge.net/projects/tokyo/
 * 
 * Copyright (c) 2005-2007 Eric Bréchemier
 * http://eric.brechemier.name
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 */
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
    while( !nullTokyoNautDst.areWeThereYet(meta,data) && (step++ < STEP_LIMIT)  )
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