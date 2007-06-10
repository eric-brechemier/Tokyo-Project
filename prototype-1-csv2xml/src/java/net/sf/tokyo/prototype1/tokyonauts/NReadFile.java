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
package net.sf.tokyo.prototype1.tokyonauts;

import java.io.IOException;
import java.io.FileInputStream;
import net.sf.tokyo.ITokyoNaut;

/**
 * Read file and write data into a byte array buffer.<br/>
 *
 */
public class NReadFile implements ITokyoNaut
{
  protected FileInputStream _in;
  
  // TODO: move constants to separate "VersionOne" class
  protected static final byte OFFSET = 1;
  protected static final byte LENGTH = 2;
  
  public NReadFile(String inputFilePath)
  {
    try 
    {
      _in = new FileInputStream(inputFilePath);
    }
    catch(Exception e)
    {
      System.err.println("Error in NReadFile(): "+e);
    }
  }
  
  public boolean inTouch()
  {
    try
    {
      return _in.available() > 0;
    }
    catch(IOException e)
    {
      System.err.println("Error in NReadFile.available(): "+e);
      return false;
    }
  }
  
  public void read(int[]meta, byte[] data)
  {
    try
    {
      // TODO: handle buffer limits
      // setting meta[OFFSET] to data.length when buffer is full
      
      int actualLength = _in.read(data,meta[OFFSET],meta[LENGTH]);
      if (actualLength == -1)
      {
        meta[OFFSET] = -1;
        meta[LENGTH] = 0;
      }
      else
      {
        meta[OFFSET] += actualLength;
        meta[LENGTH] = actualLength;
      }
      return; 
    }
    catch(IOException e)
    {
      System.err.println("Error in NReadFile.read(): "+e);
      return;
    }
    
  }
  
  public ITokyoNaut plug(ITokyoNaut source)
  {
    return source;
  }
  
  public void unplug()
  {
    
  }
}