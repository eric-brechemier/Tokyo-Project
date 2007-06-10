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
 * Parse Csv Data in byte array and write data into an XML Pop array.<br/>
 * Expects UTF-8 encoded text with coma separated values, lines separated with
 * \n or \r\n.
 */
public abstract class NParseCsv implements ITokyoNaut
{
  
  public boolean inTouch()
  {
    return false;
  }
  
  public void read(int[]meta, byte[] data)
  {
    return;
  }
  
  public ITokyoNaut plug(ITokyoNaut source)
  {
    return source;
  }
  
  public void unplug()
  {
    
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  // TODO: REMOVE EVERYTHING BELOW...
  
  protected byte[] _namespace;
  protected byte[] _fileElementName;
  protected byte[] _lineElementName;
  protected byte[][] _fieldElementName;
  
  protected byte _state;
  
  protected final static byte STATE_START_OF_FILE = 0;
  protected final static byte STATE_START_OF_LINE = 1;
  protected final static byte STATE_FIELD_CHARS = 2;
  protected final static byte STATE_FIELD_SEPARATOR = 3;
  protected final static byte STATE_END_OF_LINE = 4;
  protected final static byte STATE_END_OF_FILE = 5;
  protected final static byte[] DEFAULT_FIELD_NAME = {'f','i','e','l','d'};
  
  protected byte _linePos;
  protected byte _fieldPos;
  protected byte _charPos;
  
  public NParseCsv()
  {
    this("http://tokyo.sf.net/csv","file","line",new String[]{"field"});
  }
  
  public NParseCsv(String namespace, String fileElementName, String lineElementName, String[] fieldElementName)
  {
    _namespace = namespace.getBytes();
    _fileElementName = fileElementName.getBytes();
    _lineElementName = lineElementName.getBytes();
    _fieldElementName = new byte[fieldElementName.length][];
    for(int i=0;i<_fieldElementName.length;i++)
    {
      _fieldElementName[i] = fieldElementName[i].getBytes();
    }
    
    _state = STATE_START_OF_FILE;
    _linePos = 0;
    _fieldPos = 0;
    _charPos = 0;
  }
  
  protected byte[] fieldElementName()
  {
    if (_fieldPos < _fieldElementName.length)
      return _fieldElementName[_fieldPos];
    else
      return DEFAULT_FIELD_NAME;
  }
  
  public ITokyoNaut morph(ITokyoNaut[] action, byte[][] data, int[] here)
  { 
    try
    {
      final byte SHIFT_IN = 0x0F; 
      final byte SHIFT_OUT = 0x0E;
      final byte XML_ROOT = '/';
      final byte XML_ELEMENT = '<';
      final byte XML_NAMESPACE = '#';
      final byte XML_TEXT = '.';
      
      // Use ByteArrayOutputStream instead?? (but buffer would be growing...
      // Simplify by doing only ONE SINGLE operation on morph...
      // Each action should use less than 255 bytes..... 
      // by cutting it into several outputs if necessary.
      // Sequence of work is to empty everything in input before asking for more.
      
      
      
      // HOW TO: Manage offset checks to avoid offset outside buffer limits
      // Section by section, check(section length)
      //   if buffer payload is big enough for this section
      //     ok
      //   else
      //     if first write => enlarge payload to fit this section
      //     else clean and get back
      //
      // Set state to "processing of next section"
      
      // Other Idea (used below): 
      // start section processing and rollback to rollbackOffset if not enough buffer
      // remains to complete it
      // rollbackOffset = offset;
      // bla...
      // offset = tryWrite(data,here,offset,byteValue);
      // offset = tryWrite(data,here,offset,byteArray,srcOffset,length);
      // offset = rollbackOrContinue(rollbackOffset)
      // if (offset == rollbackOffset)
      //   back...
      //
      // tryWrite and rollback can be later provided as part of Tools package
      
      
      // problem!! this is also the end condition!! which should be forwarded
      // difference can be done on the value itself, either 0? 5? or more.
      if(data[here[0]][HEAD] == data[here[0]][TAIL]) // no input data available
        return back(action,data,here);
    
      
      // TODO:
      // Manage end of file:
      // - implicit end (no content)
      // - explicit end (end of line followed by no content)
      // => have to detect no content
      // i.e. HEAD = TAIL
      
      if(_state == STATE_START_OF_FILE)
      {
        data[here[0]][0] = VERSION_ONE;
        data[here[0]][HEAD] = (byte)START_OFFSET;
        data[here[0]][TAIL] = data[here[0]][HEAD];
        
        if ( !canWrite(data,here,2 + 2 + _fileElementName.length + 2 + _namespace.length + 1) )
        {
          // going back seems useless here, should enlarge data[0] buffer instead
          System.out.println("ERROR: output buffer too small for start of file");
          return null;
        }
        
        write(data,here,SHIFT_IN);
        write(data,here,XML_ROOT);
        
        write(data,here,SHIFT_IN);
        write(data,here,XML_ELEMENT);
        writeArray(data,here,_fileElementName);
        
        write(data,here,SHIFT_IN);
        write(data,here,XML_NAMESPACE);
        writeArray(data,here,_namespace);
        write(data,here,SHIFT_OUT);
        
        _state = STATE_START_OF_LINE;
        return next(action,data,here);
      }
      
      if (data[here[0]][HEAD] == data[here[0]][TAIL])
      {
        if ( !canWrite(data,here,5) )
        {
          // going back seems useless here, should enlarge data[0] buffer instead
          System.out.println("ERROR: output buffer too small for end of file");
          return null;
        }
        
        write(data,here,SHIFT_OUT); // END TEXT
        write(data,here,SHIFT_OUT); // END FIELD ELEMENT
        
        write(data,here,SHIFT_OUT); // END LINE ELEMENT
        
        write(data,here,SHIFT_OUT); // END FILE ELEMENT
        write(data,here,SHIFT_OUT); // END XML ROOT
        
        _state = STATE_END_OF_FILE;
        return next(action,data,here);
      }
      
      
      if (_state == STATE_START_OF_LINE)
      {
        _linePos++;
        _fieldPos = 0;
        _charPos = 0; // INFO: char in line
        
        if ( !canWrite(data,here,2 + _lineElementName.length + 2 + fieldElementName().length + 2 ) )
        {
          // going back seems useless here, should enlarge data[0] buffer instead
          System.out.println("ERROR: output buffer too small for start of line");
          return null;
        }
        
        write(data,here,SHIFT_IN);
        write(data,here,XML_ELEMENT);
        writeArray(data,here,_lineElementName);
        
        write(data,here,SHIFT_IN);
        write(data,here,XML_ELEMENT);
        writeArray(data,here, fieldElementName() );
        
        write(data,here,SHIFT_IN);
        write(data,here,XML_TEXT);
        
        _state = STATE_FIELD_CHARS;
        return next(action,data,here);
      }
      
      while( (_state == STATE_FIELD_CHARS) && canRead(data,here) && canWrite(data,here) )
      {
        _charPos++; // INFO
        byte val = read(data,here);
        
        switch(val)
        {
          case ',':
            _state = STATE_FIELD_SEPARATOR;
            break;
          
          case '\r':
            break;
            
          case '\n':  
            _state = STATE_END_OF_LINE;
            break;
          
          default:
            write(data,here,val);
        }
        
        return next(action,data,here);
      }
      
      if (_state == STATE_FIELD_SEPARATOR)
      {
        _fieldPos++;
        checkpoint(data,here);
        
        write(data,here,SHIFT_OUT); // END TEXT
        write(data,here,SHIFT_OUT); // END FIELD ELEMENT
        
        write(data,here,SHIFT_IN);
        write(data,here,XML_ELEMENT);
        writeArray(data,here, fieldElementName() );
        
        write(data,here,SHIFT_IN);
        write(data,here,XML_TEXT);
        
        if( rollback(data,here) )
          return forward(action,data,here);
        
        _state = STATE_FIELD_CHARS;
      }
      
      if (_state == STATE_END_OF_LINE)
      {
        checkpoint(data,here);
        
        write(data,here,SHIFT_OUT); // END TEXT
        write(data,here,SHIFT_OUT); // END FIELD ELEMENT
        write(data,here,SHIFT_OUT); // END LINE ELEMENT
        
        if( rollback(data,here) )
          return forward(action,data,here);
        
        _state = STATE_START_OF_LINE;
      }
      
      return next(action,data,here);
      
      /*
      if (isStartOfFile)
      {
        // TODO: handle offset case limit by storing extra elements aside
        outData[offset++] = XmlPop.createRootNode();
        outData[offset++] = XmlPop.createElement(CSV_FILE,CSV_NAMESPACE);
        outData[offset++] = XmlPop.createDefaultNamespace(CSV_NAMESPACE);
        outData[offset++] = XmlPop.pop(1);
        outData[offset++] = XmlPop.createElement(CSV_LINE,CSV_NAMESPACE);
        isStartOfFile = false;
      }
      
      for (int i=0; i<bytesRead; i++)
      {
        switch(buf[i])
        {
          
          case '\n':
            isNewLine = true;
            
          case ',':
            String field = new String(buf,fieldStart,fieldLength,UTF8_ENCODING);
            System.out.println("new field: "+field);
            outData[offset++] = XmlPop.createElement(CSV_FIELD,CSV_NAMESPACE);
            outData[offset++] = XmlPop.createTextNode(field);
            
            fieldStart = i+1;
            fieldLength = 0;
            
            if(isNewLine)
            {
              outData[offset++] = XmlPop.pop(3);
              outData[offset++] = XmlPop.createElement(CSV_LINE,CSV_NAMESPACE);
              isNewLine = false;
            }
            else
            {
              outData[offset++] = XmlPop.pop(2);
            }
            break;
          
          case '\r':
            break;
            
          default:
            fieldLength++;
            System.out.println("Start: "+fieldStart+" Length: "+fieldLength+" Char: "+buf[i]);
        }
      }
      
      // TODO: handle End of File outside first run of morph
      
      String lastfield = new String(buf,fieldStart,fieldLength,UTF8_ENCODING);
      System.out.println("new field: "+lastfield);
      outData[offset++] = XmlPop.createElement(CSV_FIELD,CSV_NAMESPACE);
      outData[offset++] = XmlPop.createTextNode(lastfield);
      outData[offset++] = XmlPop.pop(5);
      
      index[0]++;
      return rules[index[0]];
      */
      
    }
    catch(Exception e)
    {
      System.err.println("Error in NParseCsv at Line ["+_linePos+"] Field ["+_fieldPos+"] Pos ["+_charPos+"]: "+e);
      System.err.println("State: "+_state+" Head: "+data[here[0]][HEAD]+" Tail: "+data[here[0]][TAIL]);
      System.err.println("Head: "+dstHead(data,here)+" (0x"+Integer.toHexString(data[here[0]][HEAD]&0xFF)+")");
      System.err.println("Tail: "+dstTail(data,here)+" (0x"+Integer.toHexString(data[here[0]][TAIL]&0xFF)+")");
      return null;
    }
  }
  
  
  /*
  // define basic operations on Queue: isEmpty, write, read
  // define byte read() and void write(byte) on byte[][] data, int[] here
  
  
  protected void tryPipe(byte[][] data, int[] here)
  {
    // read one from data[0]-1 
    
    // write it to data[0]
  }
  
  protected void tryWrite(byte[][] data, int[] here, int[] offset, byte byteValue)
  {
    if(offset[0]<data[here[0]].length)
      data[here[0]][offset[0]++] = byteValue;
  }
  
  protected void tryWriteArray(byte[][] data, int[] here, int[] offset, byte[] byteArray)
  {
    tryWriteArray(data, here, offset, byteArray, 0, byteArray.length);
  }
  
  protected void tryWriteArray(byte[][] data, int[] here, int[] offset, 
                              byte[] byteArray, int srcOffset, int length)
  {
    if( (offset[0]+length)<data[here[0]].length )
    {
      System.arraycopy(byteArray,srcOffset,data[here[0]],offset[0],length);
      offset[0]+=length;
    }
  }
  */
  
  protected final static byte NULL = 0;
  protected final static byte VERSION_ONE = 1;
  protected final static int HEAD = 1;
  protected final static int TAIL = 2;
  protected final static int HEAD_ROLLBACK = 3;
  protected final static int TAIL_ROLLBACK = 4;
  protected final static int START_OFFSET = 5;
  
  protected void checkpoint(byte[][] data, int[] here)
  {
    data[here[0]-1][HEAD_ROLLBACK] = data[here[0]-1][HEAD];
    data[here[0]][TAIL_ROLLBACK] = data[here[0]][TAIL];
  }
  
  // stuff to force rollback
  protected void stuff(byte[][] data, int[] here)
  {
    data[here[0]][TAIL] = (byte)data[here[0]].length;
  }
  
  // FOR STUFF & ROLLBACK...
  // may set limit to length+1 to allow exactly full buffer...
  
  protected boolean rollback(byte[][] data, int[] here)
  {
    if( data[here[0]][TAIL] < data[here[0]].length )
      return false;
      
    // could clean up to 0... just ignored here, data keeps all values set since checkpoint()
    data[here[0]-1][HEAD] = data[here[0]-1][HEAD_ROLLBACK];
    data[here[0]][TAIL] = data[here[0]][TAIL_ROLLBACK];
    return true;
  }
  
  protected int srcHead(byte[][] data, int[] here)
  {
    return data[here[0]-1][HEAD]&0xFF;
  }
  
  protected int srcTail(byte[][] data, int[] here)
  {
    return data[here[0]-1][TAIL]&0xFF;
  }
  
  protected int dstHead(byte[][] data, int[] here)
  {
    return data[here[0]][HEAD]&0xFF;
  }
  
  protected int dstTail(byte[][] data, int[] here)
  {
    return data[here[0]][TAIL]&0xFF;
  }
  
  protected boolean canRead(byte[][] data, int[] here)
  {
    return ( srcHead(data,here) < srcTail(data,here) );
  }
  
  protected boolean canWrite(byte[][] data, int[] here)
  {
    return ( dstTail(data,here) < data[here[0]].length );
  }
  
  protected boolean canWrite(byte[][] data, int[] here, int length)
  {
    return ( dstTail(data,here)+length < data[here[0]].length );
  }
  
  protected byte read(byte[][] data, int[] here)
  {
    System.out.println("DEBUG: read="+(char)data[here[0]-1][ srcHead(data,here) ]);
    
    byte result = data[here[0]-1][ srcHead(data,here) ];
    data[here[0]-1][HEAD]++;
    return result;
  }
  
  protected void writeArray(byte[][] data, int[] here, byte[] byteArray)
  {
    writeArray(data, here, byteArray, 0, byteArray.length);
  }
  
  protected void write(byte[][] data, int[] here, byte byteValue)
  {
    if ( dstTail(data,here) >= data[here[0]].length )
    {
      stuff(data,here);
      return;
    }
    
    data[here[0]][ dstTail(data,here) ] = byteValue;
    data[here[0]][TAIL]++;
  }
  
  protected void writeArray(byte[][] data, int[] here, byte[] byteArray, int srcOffset, int length)
  {
    if( (dstTail(data,here)+length) >= data[here[0]].length )
    {
      stuff(data,here);
      return;
    }
      
    System.arraycopy(byteArray,srcOffset,data[here[0]],dstTail(data,here),length);
    data[here[0]][TAIL]+=length;
  }
  
  /*
  protected boolean isAvailable(byte[][] data, int[] here, int length)
  {
    return ( data[here[0]-1][HEAD]+length < data[here[0]-1].length );
  }
  
  protected void copy(byte[][] data, int[] here, int length)
  {
    if ( !isAvailable(data,here,length) )
      return;
    
    
    write(data,here,data[here[0]],data[here[0]][TAIL],length);
    
  }
  */
  
  protected ITokyoNaut loop(ITokyoNaut[] action, byte[][] data, int[] here)
  {
    return action[here[0]];
  }
  
  protected ITokyoNaut back(ITokyoNaut[] action, byte[][] data, int[] here)
  {
    here[0]--;
    data[here[0]][HEAD]=0;
    return action[here[0]];
  }
  
  protected ITokyoNaut forward(ITokyoNaut[] action, byte[][] data, int[] here)
  {
    here[0]++;
    if (here[0] == action.length)
      return null;
    
    return action[here[0]];
  }
  
  
  protected ITokyoNaut next(ITokyoNaut[] action, byte[][] data, int[] here)
  {
    if( !canWrite(data,here) )
      return forward(action,data,here);
      
    if ( !canRead(data,here) )
      return back(action,data,here);
      
    // DEBUG
    //return forward(action,data,here);
    return loop(action,data,here);
  }
  
  
}