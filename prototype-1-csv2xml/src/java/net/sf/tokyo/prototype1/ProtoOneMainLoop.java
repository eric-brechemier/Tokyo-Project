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
      System.out.println("Usage: [ProtoOneMain] inCsvFilePath stylesheetFilePath outDir");
      return;
    }
    String inCsvFilePath = args[0];
    String stylesheetFilePath = args[1];
    String outDirPath = args[2];
    
    System.out.println("Starting ProtoOne:"
      + "\n  InCsv: " + inCsvFilePath
      + "\n  Xsl: " + stylesheetFilePath
      + "\n  outDir: " + outDirPath
    );
    
    ITokyoNaut fileInput = new NReadFile(inCsvFilePath);
    
    String outCsvFilePath = outDirPath + "/01_result_basic.csv";
    ITokyoNaut fileOuput = new NWriteFile(outCsvFilePath);
    
    ITokyoNaut headTokyoNaut = fileInput;
    ITokyoNaut tailTokyoNaut = fileOuput;
    
    /* Basic Test: no spy */
    System.out.println("Basic Test: In=>Out");
    fileInput.plug(fileOuput);
    
    int[] meta = new int[]{1,0,200,0,0};
    byte[] data = new byte[255];
    
    while( tailTokyoNaut.areWeThereYet()==false )
    {
      headTokyoNaut.filter(meta,data);
    }
    headTokyoNaut.unplug();
    
    // ***
    
    /* Basic Test + wrapping spy */
    System.out.println("Basic Test + Spy: In=>Spy=>Out");
    fileInput = new NReadFile(inCsvFilePath);
    outCsvFilePath = outDirPath + "/02_result_with_spy.csv";
    fileOuput = new NWriteFile(outCsvFilePath);
    
    ITokyoNaut spyOne = new NSpy(NSpy.STYLE_CHAR);
    fileInput.plug(spyOne).plug(fileOuput);
    
    headTokyoNaut = fileInput;
    tailTokyoNaut = fileOuput;
    
    meta = new int[]{1,0,200,0,0};
    data = new byte[255];
    
    while( tailTokyoNaut.areWeThereYet()==false )
    {
      headTokyoNaut.filter(meta,data);
    }
    headTokyoNaut.unplug();
    
    
    /* Basic Test + 2 Spy Filters */
    System.out.println("Basic Test + 2 Spies: In=>Spy=>Out=>Spy");
    fileInput = new NReadFile(inCsvFilePath);
    outCsvFilePath = outDirPath + "/03_result_with_two_spies.csv";
    fileOuput = new NWriteFile(outCsvFilePath);
    
    spyOne = new NSpy(NSpy.STYLE_CHAR);
    ITokyoNaut spyTwo = new NSpy();
    fileInput.plug(spyOne).plug(fileOuput).plug(spyTwo);
    
    headTokyoNaut = fileInput;
    tailTokyoNaut = spyTwo;
    
    meta = new int[]{1,0,200,0,0};
    data = new byte[255];
    
    while( tailTokyoNaut.areWeThereYet()==false )
    {
      headTokyoNaut.filter(meta,data);
    }
    headTokyoNaut.unplug();
        
    // ***
    
    /* Read Csv... */
    System.out.println("Read CSV: In=>ParseCSV=>Spy=>Out=>Spy");
    fileInput = new NReadFile(inCsvFilePath);
    outCsvFilePath = outDirPath + "/04_result_parseCSV.csv";
    fileOuput = new NWriteFile(outCsvFilePath);
    
    spyOne = new NSpy(NSpy.STYLE_CHAR);
    spyTwo = new NSpy();
    ITokyoNaut csvReader = new NParseCsv();
    fileInput.plug(csvReader).plug(spyOne).plug(fileOuput).plug(spyTwo);
    
    headTokyoNaut = fileInput;
    tailTokyoNaut = fileOuput;
    
    meta = new int[]{1,0,200,0,0};
    data = new byte[255];
    
    while( tailTokyoNaut.areWeThereYet()==false )
    {
      headTokyoNaut.filter(meta,data);
    }
    headTokyoNaut.unplug();
    
    // ***
    
    /* Write Csv... */
    System.out.println("Read CSV: In=>ParseCSV=>WriteCSV=>Spy=>Out");
    fileInput = new NReadFile(inCsvFilePath);
    outCsvFilePath = outDirPath + "/05_result_writeCSV.csv";
    fileOuput = new NWriteFile(outCsvFilePath);
    
    spyOne = new NSpy(NSpy.STYLE_CHAR);
    csvReader = new NParseCsv();
    ITokyoNaut csvWriter = new NWriteCsv();
    fileInput.plug(csvReader).plug(csvWriter).plug(fileOuput).plug(spyOne);
    
    headTokyoNaut = fileInput;
    tailTokyoNaut = spyOne;
    
    meta = new int[]{1,0,200,0,0};
    data = new byte[255];
    
    while( tailTokyoNaut.areWeThereYet()==false )
    {
      headTokyoNaut.filter(meta,data);
    }
    headTokyoNaut.unplug();
    
    
    
    /* Complete chain ... to XML
    
    */
    
    /* Complete chain ... from XML
    
    */
    
    /* Complete chain in two parts + XSLT processing
    
    */
    
    /* Complete chain in one part 
     * by encapsulating XSLT processing in the middle
    fileInput.plug(csvReader).plug(spyOne).plug(xslTransform).plug(spyTwo).plug(csvWriter).plug(fileOuput);
    */
    
    
  }
  
}