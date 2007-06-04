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
 *
 */
package net.sf.tokyo.prototype1;

import net.sf.tokyo.ITokyoNaut;
import net.sf.tokyo.prototype1.tokyonauts.NReadFile;
import net.sf.tokyo.prototype1.tokyonauts.NParseCsv;
import net.sf.tokyo.prototype1.tokyonauts.NXslTransform;
import net.sf.tokyo.prototype1.tokyonauts.NWriteCsv;
import net.sf.tokyo.prototype1.tokyonauts.NWriteFile;
import net.sf.tokyo.prototype1.tokyonauts.NSpy;
import net.sf.tokyo.prototype1.tokyonauts.NSpyConsume;
import net.sf.tokyo.prototype1.tokyonauts.NSpyLoopBack;

public class ProtoOneMainLoop
{
  public static void main(String[] args)
  {
    if (args.length <3)
    {
      System.out.println("Usage: [ProtoOneMain] inCsvFilePath stylesheetFilePath outCsvFilePath");
      return;
    }
    String inCsvFilePath = args[0];
    String stylesheetFilePath = args[1];
    String outCsvFilePath = args[2];
    
    System.out.println("Starting ProtoOne:"
      + "\n  InCsv: " + inCsvFilePath
      + "\n  Xsl: " + stylesheetFilePath
      + "\n  OutCsv: " + outCsvFilePath
    );
    
    
    ITokyoNaut[] action =
    {
      new NReadFile(inCsvFilePath),
      new NSpy(NSpy.STYLE_HEX),
      new NParseCsv("http://tokyo.sf.net/proto1/countries/2007","world","country",new String[]{"name","capital","continent"}),
      new NSpyConsume(NSpy.STYLE_HEX)
    };
    
    /*
    ITokyoNaut[] action =
    {
      new NReadFile(inCsvFilePath),
      new NSpy(NSpy.STYLE_HEX),
      new NParseCsv("http://tokyo.sf.net/proto1/countries/2007","world","country",new String[]{"name","capital","continent"}),
      //new NSpyLoopBack(NSpy.STYLE_HEX,NSpyLoopBack.NO_LIMIT),
      new NSpyLoopBack(NSpy.STYLE_CHAR,10),
      new NXslTransform(stylesheetFilePath),
      new NSpy(),
      new NWriteCsv(),
      new NSpy(),
      new NWriteFile(outCsvFilePath)
    };
    */
    
    byte[][] data = new byte[action.length][255];
    
    try
    {
    
      int[] here = {0};
      ITokyoNaut current = action[here[0]];
      while (current != null)
      {
        System.out.println("Applying TokyoNaut "+here[0]+"...");
        current = current.morph(action,data,here);
      }
      
      System.out.println("ProtoOne completed.");
      
    }
    catch(Exception e)
    {
      System.err.println("Error in Main Loop: "+e);
    }
  }
  
}