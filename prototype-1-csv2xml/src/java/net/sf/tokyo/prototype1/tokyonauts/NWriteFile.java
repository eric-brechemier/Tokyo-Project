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
import java.io.FileOutputStream;
import net.sf.tokyo.ITokyoNaut;


/**
 * Write byte array data into a file.<br/>
 *
 */
public class NWriteFile implements ITokyoNaut
{
  protected FileOutputStream _out;
  protected ITokyoNaut _source;
  
  // TODO: move constants to separate "VersionOne" class
  protected static final byte OFFSET = 1;
  protected static final byte LENGTH = 2;
  
  public NWriteFile(String outputFilePath)
  {
    try 
    {
      _out = new FileOutputStream(outputFilePath);
    }
    catch(Exception e)
    {
      System.err.println("Error in NWriteFile(): "+e);
    }
  }
  
  public boolean inTouch()
  {
    if (_source==null)
      return false;
    
    return _source.inTouch();
  }
  
  public void read(int[]meta, byte[] data)
  {
    if (_source==null)
    {
      meta[OFFSET] = -1;
      return;
    }
    
    try
    {
      int startOffset = meta[OFFSET];
      
      _source.read(meta,data);
      _out.write(data,startOffset,meta[LENGTH]);
      return;
    }
    catch(IOException e)
    {
      System.err.println("Error in NWriteFile.read(): "+e);
      return;
    }
    catch(IndexOutOfBoundsException iobe)
    {
      System.err.println("Wrong Index Range ["+meta[OFFSET]+","+meta[LENGTH]+"] in NWriteFile.read(): "+iobe);
      return;
    }
    
  }
  
  public ITokyoNaut plug(ITokyoNaut source)
  {
    if (_source!=null)
      _source.unplug();
    
    _source = source;
    return source;
  }
  
  public void unplug()
  {
    try
    {
      if (_out!=null)
        _out.close();
      
      if (_source!=null)
        _source.unplug();
    }
    catch(IOException e)
    {
      System.err.println("Error in NWriteFile.unplug(): "+e);
      return;
    }
    
    
  }
  
}