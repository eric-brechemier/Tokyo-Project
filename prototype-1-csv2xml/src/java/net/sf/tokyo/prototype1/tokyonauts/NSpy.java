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

import net.sf.tokyo.ITokyoNaut;

/**
 * Display the content of input data buffer and forward transparently.<br/>
 *
 */
public class NSpy implements ITokyoNaut
{
  public static final byte STYLE_HEX = 0;
  public static final byte STYLE_CHAR = 1;
  
  protected byte _logStyle;
  
  protected ITokyoNaut _source;
  
  // TODO: move constants to separate "VersionOne" class
  protected static final byte VERSION = 0;
  protected static final byte OFFSET = 1;
  protected static final byte LENGTH = 2;
  
  public NSpy(byte logStyle)
  {
    _logStyle = logStyle;
  }
  
  public NSpy()
  {
    //_logStyle = STYLE_CHAR;
    _logStyle = STYLE_HEX;
  }
  
  public boolean inTouch()
  {
    if(_source==null)
      return false;
    
    boolean result = _source.inTouch();
    System.out.println("Spy: inTouch: "+result);
    
    return result;
  }
  
  public void read(int[]meta, byte[] data)
  {
    final byte VERSION_ONE = 1;
    final byte STYLE_DEFAULT_BYTES_PER_LINE = 10;
    final byte STYLE_CHAR_BYTES_PER_LINE = 25;
    
    int bytesPerLine = STYLE_DEFAULT_BYTES_PER_LINE;
    if (_logStyle==STYLE_CHAR)
      bytesPerLine = STYLE_CHAR_BYTES_PER_LINE;
      
    try 
    {
      if (meta[VERSION] != VERSION_ONE)
        System.out.println("Warning: Unsupported version: "+VERSION_ONE);
      
      System.out.println("Spy offset: "+meta[OFFSET]+" length: "+meta[LENGTH]);
      
      if (_source != null)
        _source.read(meta,data);
      
      
      for(int i=0; i<data.length; i++)
      {
        if (i % bytesPerLine == 0)
        {
          System.out.print("\n"+formatHexNumber(i,2,'0')+": ");
        }
        
        switch(_logStyle)
        {
          case STYLE_CHAR:
            char c = (char)data[i];
            System.out.print( formatChar(c) );
            break;
            
          default:
            int val = data[i] & 0xFF;
            System.out.print(formatHexNumber(val,2,'0')+" ");
        }
        
      }
      System.out.println("\n");
      return;
    }
    catch(Exception e)
    {
      System.err.println("Error in NSpy.read(): "+e);
      return;
    }
  }
  
  protected String formatHexNumber(int value, int length, char padding)
  {
    String strValue = Integer.toHexString(value);
    while(strValue.length() < length)
      strValue = padding + strValue;
    return strValue;
  }
  
  protected String formatChar(char value)
  {
    if ( Character.isISOControl(value) )
      return "\u229D"; // circled dash
    else
      return ""+value;
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
    if (_source!=null)
      _source.unplug(); 
  }
  
}