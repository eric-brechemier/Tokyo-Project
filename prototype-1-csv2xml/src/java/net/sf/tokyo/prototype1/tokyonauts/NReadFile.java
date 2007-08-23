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
public class NReadFile extends NCommonBase implements ITokyoNaut
{
  protected FileInputStream _in;
  protected byte _state;
  
  protected static final byte STATE_UNSET = -1;
  protected static final byte STATE_OPENED = 0;
  protected static final byte STATE_AVAILABLE = 1;
  protected static final byte STATE_CLOSED = 2;
  
  public NReadFile(String inputFilePath)
  {
    _state = STATE_UNSET;
    
    try 
    {
      _in = new FileInputStream(inputFilePath);
      _state = STATE_OPENED;
    }
    catch(Exception e)
    {
      System.err.println("Error in NReadFile(): "+e);
    }
  }
  
  public boolean areWeThereYet()
  {
    switch(_state)
    {
      case STATE_OPENED:
      case STATE_AVAILABLE:
        return false;
      default:
        return true;
    }
  }
  
  public void filter(int[]meta, byte[] data)
  {
    if(areWeThereYet() || meta[VERSION]!=VERSION_ONE)
      return;
    
    try
    {
      meta[ITEM] = ITEM_DOCUMENT;
      switch(_state)
      {
        case STATE_OPENED:
          meta[EVENT] = START;
          _state = STATE_AVAILABLE;
          break;
        case STATE_AVAILABLE:
          if(_in.available() > 0)
          {
            meta[EVENT] = CONTINUATION;
          }
          else
          {
            meta[EVENT] = END;
            _state = STATE_CLOSED;
          }
          break;
        default: 
          // unexpected
          meta[EVENT] = ERROR;
          meta[ITEM] = 0x0F;
      }
      
      if(_state == STATE_AVAILABLE)
      {
        int maxLength = data.length - meta[OFFSET];
        meta[LENGTH] = _in.read(data,meta[OFFSET],maxLength);
      }
      else
      {
        meta[OFFSET] = 0;
        meta[LENGTH] = 0;
      }
      
      _destination.filter(meta,data);
    }
    catch(IOException e)
    {
      System.err.println("Error in NReadFile.filter(): "+e);
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
      _state = STATE_UNSET;
      
      if (_in != null)
      {
        _in.close();
        _in = null;
      }
      
      super.unplug();
    } 
    catch(Exception e)
    {
      System.err.println("Error in NReadFile.unplug(): "+e);
    }
  }
}