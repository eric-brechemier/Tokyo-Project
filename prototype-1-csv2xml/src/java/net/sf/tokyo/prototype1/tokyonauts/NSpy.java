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

import java.util.Enumeration;
import java.util.Stack;
import net.sf.tokyo.ITokyoNaut;

/**
 * Display the content of input data buffer and forward transparently.<br/>
 *
 */
public class NSpy extends NCommonBase implements ITokyoNaut
{
  public static final byte STYLE_HEX = 0;
  public static final byte STYLE_CHAR = 1;
  public static final byte STYLE_DEFAULT = STYLE_HEX;
  
  protected byte _logStyle;
  
  protected int _level;
  protected Stack _contextPath;
  protected boolean _completed;
  
  public NSpy(byte logStyle)
  {
    _logStyle = logStyle;
    _level = -1;
    _contextPath = new Stack();
    _completed = false;
  }
  
  public NSpy()
  {
    this(STYLE_DEFAULT);
  }
  
  public boolean areWeThereYet()
  {
    return _completed;
  }
  
  public void filter(int[]meta, byte[] data)
  {
    try 
    {
      printSeparator();
      
      if (meta[VERSION] != VERSION_ONE)
        System.out.println("Spy> !!! WARNING: Unsupported version: "+VERSION_ONE);
      
      if (meta[EVENT]==END && meta[ITEM]==ITEM_DOCUMENT)
        _completed = true;
      
      _level += meta[EVENT];
      printContextStack(meta);
      
      printDataBuffer(data);
      
      printSeparator();
      
      long nsBefore = System.nanoTime();
      super.filter(meta,data);
      long nsAfter = System.nanoTime();
      printTimeDifference(nsAfter,nsBefore);
      
      printSeparator();
    }
    catch(Exception e)
    {
      System.err.println("Error in NSpy.filter(): "+e);
      e.printStackTrace();
      return;
    }
  }
  
  protected void printSeparator()
  {
    System.out.println("Spy> ~~~ "+System.nanoTime());
  } 
  
  protected void printDataBuffer(byte[] data)
  {
    final byte STYLE_DEFAULT_BYTES_PER_LINE = 10;
    final byte STYLE_CHAR_BYTES_PER_LINE = 25;
    
    final int ADDRESS_LENGTH = 4;
    final int VALUE_LENGTH = 2;
    
    int bytesPerLine = STYLE_DEFAULT_BYTES_PER_LINE;
    if (_logStyle==STYLE_CHAR)
      bytesPerLine = STYLE_CHAR_BYTES_PER_LINE;
    
    for(int i=0; i<data.length; i++)
    {
      if (i % bytesPerLine == 0)
      {
        System.out.print("\nSpy> "+formatHexNumber(i,ADDRESS_LENGTH,'0')+": ");
      }
      
      switch(_logStyle)
      {
        case STYLE_CHAR:
          char c = (char)data[i];
          System.out.print( formatChar(c) );
          break;
          
        default:
          int val = data[i] & 0xFF;
          System.out.print(formatHexNumber(val,VALUE_LENGTH,'0')+" ");
      }
      
    }
    System.out.println("\n");
  }  
  
  protected void printContextStack(int[] meta)
  {  
    System.out.println("Spy> Level: "+_level+" ("+ (meta[EVENT]>0?"+":"") + meta[EVENT]+")"
      +" Node: "+ formatType(meta[ITEM]));
    System.out.println("Spy> Offset: "+meta[OFFSET]+" Length: "+meta[LENGTH]);
    if (meta[EVENT]>=0)
    {
      if (meta[EVENT] > 1)
        System.out.println("WARNING: Trying to push more than one element on stack...");
        
      if (meta[EVENT]==1)
      {
        String pathDescription = new String("/"+_level+"::"+ formatType(meta[ITEM])+"["+meta[LENGTH]+"bytes]" );
        _contextPath.push(pathDescription);
      } 
      else // 0
      {
        String currentNode = (String) _contextPath.pop();
        _contextPath.push(currentNode+"+["+meta[LENGTH]+"bytes]");
      }
    }
    else
    {
      for (int i=meta[EVENT]; i<0; i++)
        _contextPath.pop();
    }
    
    System.out.print("Spy> Path: ");
    
    if ( _contextPath.empty() )
      System.out.print("(empty)");
    
    for (Enumeration path=_contextPath.elements(); path.hasMoreElements(); )
    {
      System.out.print( path.nextElement() );
    }
    System.out.print("\n");
  }
  
  protected void printTimeDifference(long nsAfter, long nsBefore)
  {
    long microsDiff = (nsAfter-nsBefore)/1000;
    long msDiff = microsDiff/1000;
    int microsRemain =  (int)(microsDiff%1000);
    System.out.println("Spy> Spying: "+_destination
      +" ("+msDiff+"."+formatDecNumber(microsRemain,3,'0')+"ms) ("+(nsAfter-nsBefore)+"ns)");  
  }
  
  protected String formatDecNumber(int value, int length, char padding)
  {
    String strValue = Integer.toString(value);
    while(strValue.length() < length)
      strValue = padding + strValue;
    return strValue;
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
  
  protected String formatType(int itemType)
  {
    switch(itemType)
    {
      case ITEM_DOCUMENT:
        return "document";
      
      case ITEM_DOCUMENT_CHILDREN:
        return "document.children";
        
      case ITEM_DOCUMENT_CHARACTER_ENCODING:
        return "document.character.encoding";
      
      case ITEM_ELEMENT:
        return "element";
      
      case ITEM_ELEMENT_NAMESPACE_NAME:
        return "element.namespace";
        
      case ITEM_ELEMENT_LOCAL_NAME:
        return "element.localname";
      
      case ITEM_ELEMENT_PREFIX:
        return "element.prefix";
      
      case ITEM_ELEMENT_CHILDREN:
        return "element.children";
        
      case ITEM_ELEMENT_ATTRIBUTES:
        return "element.attributes";
      
      case ITEM_ELEMENT_NAMESPACE_ATTRIBUTES:
        return "element.namespaces";
      
      case ITEM_ATTRIBUTE:
        return "attribute";
        
      case ITEM_ATTRIBUTE_NAMESPACE_NAME:
        return "attribute.namespaceName";  
      
      case ITEM_ATTRIBUTE_LOCAL_NAME:
        return "attribute.localName";
      
      case ITEM_ATTRIBUTE_PREFIX:
        return "attribute.prefix";
      
      case ITEM_ATTRIBUTE_NORMALIZED_VALUE:
        return "attribute.normalized.value";
      
      case ITEM_CHARACTERS:
        return "characters";
        
      case ITEM_CHARACTERS_CODE:
        return "characters.code";
      
      case ITEM_NAMESPACE:
        return "namespace";
        
      case ITEM_NAMESPACE_PREFIX:
        return "namespace.prefix";
        
      case ITEM_NAMESPACE_NAME:
        return "namespace.name";
      
      default:
        return "unknown![0x"+Integer.toHexString(itemType)+"]";
    }
  }
  
  public ITokyoNaut plug(ITokyoNaut destination)
  {
    return super.plug(destination);
  }
  
  public void unplug()
  {
    super.unplug(); 
  }
  
}