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
public class NWriteFile extends NCommonBase implements ITokyoNaut
{
  protected FileOutputStream _out;
  protected boolean _completed;
  
  public NWriteFile(String outputFilePath)
  {
    try 
    {
      _out = new FileOutputStream(outputFilePath);
      _completed = false;
    }
    catch(Exception e)
    {
      System.err.println("Error in NWriteFile(): "+e);
    }
  }
  
  public boolean areWeThereYet()
  {
    if (_out==null || _completed)
      return true;
    
    return false;
  }
  
  public void filter(int[]meta, byte[] data)
  {
    if(areWeThereYet() || meta[VERSION]!=VERSION_ONE)
      return;
    
    if (meta[EVENT]==END && meta[ITEM]==ITEM_DOCUMENT)
      _completed = true;
    
    try
    {
      _out.write(data,meta[OFFSET],meta[LENGTH]);
      
      super.filter(meta,data);
    }
    catch(IOException e)
    {
      System.err.println("Error in NWriteFile.filter(): "+e);
      return;
    }
    catch(IndexOutOfBoundsException iobe)
    {
      System.err.println("Wrong Index Range ["+meta[OFFSET]+","+meta[LENGTH]+"] in NWriteFile.filter(): "+iobe);
      return;
    }
    
  }
  
  public ITokyoNaut plug(ITokyoNaut destination)
  {
    return super.plug(destination);
  }
  
  public void unplug()
  {
    try
    {
      if (_out!=null)
      {
        _out.close();
        _out = null;
      }
      
      super.unplug();
    }
    catch(IOException e)
    {
      System.err.println("Error in NWriteFile.unplug(): "+e);
      return;
    }
    
    
  }
  
}