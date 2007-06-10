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
    
    ITokyoNaut csvOuput = new NWriteFile(outCsvFilePath);
    
    /* Basic Test: no change
    csvOuput
      .plug( new NReadFile(inCsvFilePath) );
    */
    
    /* Basic Test + Spy Filter */
    csvOuput
      .plug( new NSpy() )
      .plug( new NReadFile(inCsvFilePath) );

    
    /*
    csvOuput
      .plug( new NWriteCsv() )
      .plug( new NParseCsv() )
      .plug( new NReadFile(inCsvFilePath) );
    */
    
    /* Complete chain...
    csvOuput
      .plug( new NSpy()  )
      .plug( new NWriteCsv() )
      .plug( new NSpy() )
      .plug( new NXslTransform(stylesheetFilePath) )
      .plug( new NSpy() )
      .plug( new NParseCsv("http://tokyo.sf.net/proto1/countries/2007","world","country",new String[]{"name","capital","continent"}) )
      .plug( new NSpy(NSpy.STYLE_HEX) )
      .plug( new NReadFile(inCsvFilePath) );
    */
    
    int[] meta = new int[]{1,0,200,0,0};
    byte[] data = new byte[255];
    
    while( csvOuput.inTouch() )
    {
      csvOuput.read(meta,data);
      
    }
    csvOuput.unplug();
  }
  
}