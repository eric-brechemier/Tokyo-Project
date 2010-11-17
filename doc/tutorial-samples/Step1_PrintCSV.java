/*
 * The Tokyo Project is hosted on GitHub:
 * https://github.com/eric-brechemier/Tokyo-Project
 * 
 * Copyright (c) 2005-2006 Eric Bréchemier
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

import java.io.BufferedReader;
import java.io.FileReader;

public class Step1_PrintCSV
{
  public static void main(String[] args)
  {
    if (args.length==0)
    {
      System.out.println("Usage: Step1_PrintCSV csvFile1 csvFile2 ...");
      return;
    }
    
    System.out.println("Step1_PrintCSV: Start");
    
    for (int i=0;i<args.length; i++)
    {
      System.out.println("Step1_PrintCSV: Parsing "+args[i]+"...");
      parseAll(args[i]);
    }
    
    System.out.println("Step1_PrintCSV: End");
  }
  
  public static void parseAll(String fileName)
  {
    try
    {
      BufferedReader br = new BufferedReader( new FileReader(fileName) );
      int ch;
      int lineCount = 1;
      int fieldCount = 0;
      boolean isStartLine = true;
      boolean isStartField = true;
      boolean isEndOfFile = false;
      do
      {
        if (isStartLine)
        {
          System.out.print("\nLine "+lineCount+":");
          fieldCount=0;
          isStartField = true;
          lineCount++;
          isStartLine = false;
        }
        
        if (isStartField)
        {
          fieldCount++;
          System.out.print("\n\tField "+fieldCount+": ");
          isStartField = false;
        }
        
        ch = br.read();
        
        switch(ch)
        {
          case '\r':
            break;
            
          case '\n':
            isStartLine = true;
            break;
          
          case ',':
            isStartField = true;
            break;
          
          case -1:
            isEndOfFile = true;
            break;
          
          default:
            System.out.print((char)ch);
        
        }
        
      } while (!isEndOfFile);
    }
    catch(Exception e)
    {
      System.err.println("ERROR: "+ e);
      e.printStackTrace();
    }
    finally
    {
      System.out.flush();
    }
  }
  
}
