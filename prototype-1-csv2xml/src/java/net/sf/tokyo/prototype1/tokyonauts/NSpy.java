/* ===============================================================
 The Tokyo Project is hosted on Sourceforge:
 http://sourceforge.net/projects/tokyo/
 
 Copyright (c) 2005-2007 Eric Bréchemier
 http://eric.brechemier.name
 Licensed under BSD License and/or MIT License.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                         BSD License
                             ~~~
             http://creativecommons.org/licenses/BSD/
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                          MIT License
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  Copyright (c) 2005-2007 Eric Bréchemier <tokyo@eric.brechemier.name>
  
  Permission is hereby granted, free of charge, to any person
  obtaining a copy of this software and associated documentation
  files (the "Software"), to deal in the Software without
  restriction, including without limitation the rights to use,
  copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the
  Software is furnished to do so, subject to the following
  conditions:

  The above copyright notice and this permission notice shall be
  included in all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
  OTHER DEALINGS IN THE SOFTWARE.
================================================================== */
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
  protected Stack _contextPath;
  
  public NSpy(byte logStyle)
  {
    _logStyle = logStyle;
    _contextPath = new Stack();
  }
  
  public NSpy()
  {
    this(STYLE_DEFAULT);
  }
  
  public boolean areWeThereYet(int[]meta, byte[] data)
  {
    if (meta[VERSION] != ITokyoNaut.VERSION_NANA)
        System.out.println("Spy> !!! WARNING: Unsupported version: "+meta[VERSION]);
    
    if (_src==null)
      return true;
    
    printSeparator();
    long nsBefore = System.nanoTime();
    boolean result = _src.areWeThereYet(meta,data);
    long nsAfter = System.nanoTime();
    printTimeDifference(nsAfter,nsBefore);
    
    System.out.println("Spy> VERSION:  "+meta[ITokyoNaut.VERSION]);
    System.out.println("Spy> LANGUAGE: "+meta[ITokyoNaut.LANGUAGE]);
    System.out.println("Spy> TOKEN:    "+meta[ITokyoNaut.TOKEN]);
    System.out.println("Spy> LEFT:    "+meta[ITokyoNaut.LEFT]);
    System.out.println("Spy> OFFSET:  "+meta[ITokyoNaut.OFFSET]);
    System.out.println("Spy> LENGTH:  "+meta[ITokyoNaut.LENGTH]);
    System.out.println("Spy> RIGHT:   "+meta[ITokyoNaut.RIGHT]);
    
    printSeparator();
    printDataBuffer(data);
    printSeparator();
    
    return result;
  }
  
  protected void printSeparator()
  {
    System.out.println("Spy> ~~~ "+System.nanoTime());
  } 
  
  protected void printTimeDifference(long nsAfter, long nsBefore)
  {
    long microsDiff = (nsAfter-nsBefore)/1000;
    long msDiff = microsDiff/1000;
    int microsRemain =  (int)(microsDiff%1000);
    System.out.println("Spy> Spying: "+_src
      +" ("+msDiff+"."+formatDecNumber(microsRemain,3,'0')+"ms) ("+(nsAfter-nsBefore)+"ns)");  
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
  
  
  /*
  public void translate(int[]meta, byte[] data)
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
      super.translate(meta,data);
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
  
  */
  
}