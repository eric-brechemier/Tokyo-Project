/* ===============================================================
 The Tokyo Project is hosted on GitHub:
 https://github.com/eric-brechemier/Tokyo-Project
 
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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

import net.sf.tokyo.ITokyoNaut;

import net.sf.tokyo.prototype1.languages.ICSV;

/**
 * Parse Csv Data into Tokyo Project Version One meta/data.<br/>
 * Expects UTF-8 encoded text with coma separated values, lines separated with
 * \n or \r\n.
 */

public class NParseCsv extends NCommonBase implements ITokyoNaut, ICSV
{
  protected int _linePos;
  protected int _fieldPos;
  protected int _charPos;
  
  protected CharsetDecoder _decoder;
  protected ByteBuffer _inBytes;
  protected CharBuffer _outChars;
  
  protected boolean _isHeaderPresent;
  
  // Optional parameters: charset="US-ASCII", header="absent"
  public NParseCsv(String charset, boolean isHeaderPresent)
  {
    try
    {
      _decoder = Charset.forName(charset).newDecoder();
      _inBytes = null;
      _outChars = null;
    }
    catch(Exception e)
    {
      System.err.println("Error creating charset decoder for '"+charset+"' in NParseCsv(): "+e);
    }
    
    _isHeaderPresent = isHeaderPresent;
    _linePos = 0;
    _fieldPos = 0;
    _charPos = 0;
  }
  
  public NParseCsv()
  {
    this(DEFAULT_CHARSET,DEFAULT_HEADER);
  }
  
  public NParseCsv(String charset)
  {
    this(charset,DEFAULT_HEADER);
  }
  
  public NParseCsv(boolean isHeaderPresent)
  {
    this(DEFAULT_CHARSET,isHeaderPresent);
  }
  
  /*
  public boolean areWeThereYet(int[] meta, byte[] data)
  {
    if ( !_checkParams(meta,data) || _src==null || _decoder==null )
    {
      meta[LANGUAGE]=LANGUAGE_ERROR;
      meta[TOKEN]=0x300;
      return true;
    }
    
    // Skip unknown tokens
    if ( meta[LANGUAGE]!=LANGUAGE_BINARY || meta[TOKEN]!=TOKEN_BINARY )
      return false;
    
    if ( _inBytes==null || _inBytes.array()!=data  )
      _inBytes = ByteBuffer.wrap(data,meta[OFFSET],meta[LENGTH]);
    
    if (_outChars==null)
    {
      _outChars = CharBuffer.allocate(1); // for _inBytes DEBUG
      //_outChars = CharBuffer.allocate( _inBytes.capacity() );
      _outChars.flip(); // set limit to 0 to avoid reading array filled with initial 0 values
      System.out.println("DEBUG remain: "+_outChars.remaining());
    }
    
    if ( !_outChars.hasRemaining() && !_inBytes.hasRemaining() && _src.areWeThereYet(meta,data) )
    {
      CoderResult finalResult = _decoder.decode(_inBytes,_outChars,true);
      if ( finalResult.isError() )
      {
        meta[LANGUAGE]=LANGUAGE_ERROR;
        meta[TOKEN]=(finalResult.isMalformed()? 0x311 : 0x312);
        meta[LEFT]=LEFT_START;
        meta[OFFSET]=_inBytes.position();
        meta[LENGTH]=finalResult.length();
        meta[RIGHT]=RIGHT_END;  
      }
      return true;
    }
    
    System.out.println(  "DEBUG Cond1:"+ ( !_outChars.hasRemaining() )  );
    System.out.println(  "DEBUG Cond2:"+ ( _inBytes==null )  );
    
    if ( !_inBytes.hasRemaining() )
    {
      _inBytes.position(meta[OFFSET]);
      _inBytes.limit(meta[OFFSET]+meta[LENGTH]);
    }
    
    if ( !_outChars.hasRemaining() )
    {
      System.out.println("in0 pos="+_inBytes.position()+" remain="+_inBytes.remaining());
      _outChars.clear(); // set full buffer as storage space, before writing
      CoderResult result = _decoder.decode(_inBytes,_outChars,false);
      
      System.out.println("out pos="+_outChars.position()+" remain="+_outChars.remaining());
      _outChars.flip(); // flip before reading, or to avoid reading unset "remaining" data in case of error
      System.out.println("out.flip pos="+_outChars.position()+" remain="+_outChars.remaining());
      
      System.out.println("in pos="+_inBytes.position()+" remain="+_inBytes.remaining());
      if ( result.isError() )
      {
        if ( _outChars.hasRemaining() )
        {
          System.out.println("out char!! '"+_outChars.charAt(_outChars.position())+"'" );
          System.out.println("in char!! '"+_inBytes.get(_inBytes.position())+"'" );
          
          for (int i=0; i<_inBytes.capacity();i++)
            System.out.println("in - "+i+": "+_inBytes.get(i)+" '"+(char)_inBytes.get(i)+"'" );
          
          // _inBytes is positionned on first byte of error, to be read during next invocation
        }
        else
        {
          System.err.println("Error in NParseCsv#areWeThereYet(): "+result);
          meta[LANGUAGE]=LANGUAGE_ERROR;
          meta[TOKEN]=(result.isMalformed()? 0x301 : 0x302);
          meta[LEFT]=LEFT_START;
          meta[OFFSET]=_inBytes.position();
          meta[LENGTH]=result.length();
          meta[RIGHT]=RIGHT_END;
          
          _inBytes.position( _inBytes.position()+result.length() );
        }
        System.out.println("in2 pos="+_inBytes.position()+" remain="+_inBytes.remaining());
      }
      
      System.out.println("in3: "+(_inBytes==null?"null":("pos="+_inBytes.position()+" remain="+_inBytes.remaining()) ) );
        
      if ( meta[LANGUAGE]==LANGUAGE_ERROR )
        return false;
    }
    
    try
    {
      _loadmy(meta);
      meta[LANGUAGE] = LANGUAGE_UNICODE_TEXT;
      
      boolean hasFoundNewToken = false;
      while ( !hasFoundNewToken && _outChars.hasRemaining() )
      {
        // TODO create method readChar with the above, returning one character at a time
        // Check combination of characters to create tokens with unicode character value
        // Move this code to new NReadChar.java
        
        char chr = _outChars.get();
        
        int charCode = chr;
        System.out.println("DEBUG: "+chr);
        
        if ( Character.isHighSurrogate(chr) )
        {
          System.out.println("High surrogate...: "+chr);
        }
        else if ( Character.isLowSurrogate(char) )
        {
          System.out.println("Low surrogate...: "+chr);
        }
        
        meta[TOKEN] = ;
        meta[OFFSET] = 
        
      }
      
      _savemy(meta);
    }
    catch(Exception e)
    {
      System.err.println("Error in NParseCsv#areWeThereYet(): "+e);
      meta[LANGUAGE]=LANGUAGE_ERROR;
      meta[TOKEN]=0x302;
    }
    
    return false;
  }
  */
  
  public ITokyoNaut unplug(ITokyoNaut foe)
  {
    try
    {
      if (_decoder!=null)
        _decoder.reset();
      _decoder = null;
      _inBytes = null;
      _outChars = null;
    }
    catch(Exception e)
    {
      System.err.println("Error resetting charset decoder in NParseCsv#unplug() "+e);
    }
    
    return super.unplug(foe);
  }
  
  /*
  protected byte _state;
  protected byte[] _buffer;
  
  protected byte[] _namespace = "http://tokyo.sf.net/csv".getBytes();
  protected byte[] _fileElement = "file".getBytes();
  protected byte[] _lineElement = "line".getBytes();
  protected byte[] _fieldElement = "field".getBytes();
  
  protected final static byte STATE_UNSET = 0;
  protected final static byte STATE_START_OF_FILE = 1;
  protected final static byte STATE_START_OF_LINE = 2;
  protected final static byte STATE_FIELD_CHARS = 3;
  protected final static byte STATE_FIELD_SEPARATOR = 4;
  protected final static byte STATE_END_OF_LINE = 5;
  protected final static byte STATE_END_OF_FILE = 6;
  protected final static byte STATE_AFTER_THE_END = 7;
  
  protected byte _linePos;
  protected byte _fieldPos;
  protected byte _charPos;
  protected boolean _startOfText;
  
  public NParseCsv()
  {
     _state = STATE_START_OF_FILE;
    _linePos = 0;
    _fieldPos = 0;
    _charPos = 0;
    _startOfText = true;
  }
  
  public boolean areWeThereYet()
  {
    switch(_state)
    {
      case STATE_UNSET:
      case STATE_AFTER_THE_END:
        return true;
      default:
        return false;
    }
  }
  
  public void translate(int[]meta, byte[] data)
  {
    if(areWeThereYet() || meta[VERSION]!=VERSION_ONE)
      return;
    
    if (_buffer==null)
      _buffer = new byte[data.length];
    System.arraycopy(data,meta[OFFSET],_buffer,meta[OFFSET],meta[LENGTH]);
    
    int index = 0;
    int length = meta[LENGTH];
    
    if ( meta[EVENT] == END && meta[ITEM] == ITEM_DOCUMENT )
      _state = STATE_END_OF_FILE;
    
    boolean doLoop;
    do
    {
      doLoop = false;
      
      switch(_state)
      {
        case STATE_START_OF_FILE:
          
          // super.startDocument("http://tokyo.sf.net/csv",meta,data)
          // // super.push(ITEM_DOCUMENT,meta,data)
          meta[ITEM] = ITEM_DOCUMENT;
          meta[EVENT] = START;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
            
            // super.startElement("file",meta,data)
            // // super.push(TYPE_ELEMENT,meta,data)
            meta[ITEM] = ITEM_ELEMENT;
            meta[EVENT] = START;
            meta[OFFSET] = 0;
            meta[LENGTH] = 0;
            super.translate(meta,data);
              
              // // super.pushData(TYPE_NAMESPACE_URI,_namespace,meta,data)
              meta[ITEM] = ITEM_ELEMENT_NAMESPACE_NAME;
              meta[EVENT] = START;
              meta[OFFSET] = 0;
              meta[LENGTH] = _namespace.length;
              System.arraycopy(_namespace,0,data,meta[OFFSET],meta[LENGTH]);
              super.translate(meta,data);
           
              // // super.pop(meta,data)
              meta[ITEM] = ITEM_ELEMENT_NAMESPACE_NAME;
              meta[EVENT] = END;
              meta[OFFSET] = 0;
              meta[LENGTH] = 0;
              super.translate(meta,data);
              
              // // super.pushData(TYPE_ELEMENT_LOCAL_NAME,_fileElement,meta,data)
              meta[ITEM] = ITEM_ELEMENT_LOCAL_NAME;
              meta[EVENT] = START;
              meta[OFFSET] = 0;
              meta[LENGTH] = _fileElement.length;
              System.arraycopy(_fileElement,0,data,meta[OFFSET],meta[LENGTH]);
              super.translate(meta,data);
          
              // // super.pop(meta,data)
              meta[ITEM] = ITEM_ELEMENT_LOCAL_NAME;
              meta[EVENT] = END;
              meta[OFFSET] = 0;
              meta[LENGTH] = 0;
              super.translate(meta,data);
              
              meta[ITEM] = ITEM_ELEMENT_CHILDREN;
              meta[EVENT] = START;
              meta[OFFSET] = 0;
              meta[LENGTH] = 0;
              super.translate(meta,data);
              
          doLoop = true;
          _state = STATE_START_OF_LINE;
          break;
        
        case STATE_START_OF_LINE:
          _linePos++;
          _fieldPos = 0;
          _charPos = 0;
          _startOfText = true;
          
          // super.startElement("line",meta,data)
          meta[ITEM] = ITEM_ELEMENT;
          meta[EVENT] = START;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
            
            // // super.pushData(TYPE_NAMESPACE_URI,_namespace,meta,data)
            meta[ITEM] = ITEM_ELEMENT_NAMESPACE_NAME;
            meta[EVENT] = START;
            meta[OFFSET] = 0;
            meta[LENGTH] = _namespace.length;
            System.arraycopy(_namespace,0,data,meta[OFFSET],meta[LENGTH]);
            super.translate(meta,data);
         
            // // super.pop(meta,data)
            meta[ITEM] = ITEM_ELEMENT_NAMESPACE_NAME;
            meta[EVENT] = END;
            meta[OFFSET] = 0;
            meta[LENGTH] = 0;
            super.translate(meta,data);
              
            meta[ITEM] = ITEM_ELEMENT_LOCAL_NAME;
            meta[EVENT] = START;
            meta[OFFSET] = 0;
            meta[LENGTH] = _lineElement.length;
            System.arraycopy(_lineElement,0,data,meta[OFFSET],meta[LENGTH]);
            super.translate(meta,data);
            
            meta[ITEM] = ITEM_ELEMENT_LOCAL_NAME;
            meta[EVENT] = END;
            meta[OFFSET] = 0;
            meta[LENGTH] = 0;
            super.translate(meta,data);
            
            meta[ITEM] = ITEM_ELEMENT_CHILDREN;
            meta[EVENT] = START;
            meta[OFFSET] = 0;
            meta[LENGTH] = 0;
            super.translate(meta,data);
            
              // super.startElement("field",meta,data)
              meta[ITEM] = ITEM_ELEMENT;
              meta[EVENT] = START;
              meta[OFFSET] = 0;
              meta[LENGTH] = 0;
              super.translate(meta,data);
                
                // // super.pushData(TYPE_NAMESPACE_URI,_namespace,meta,data)
                meta[ITEM] = ITEM_ELEMENT_NAMESPACE_NAME;
                meta[EVENT] = START;
                meta[OFFSET] = 0;
                meta[LENGTH] = _namespace.length;
                System.arraycopy(_namespace,0,data,meta[OFFSET],meta[LENGTH]);
                super.translate(meta,data);
             
                // // super.pop(meta,data)
                meta[ITEM] = ITEM_ELEMENT_NAMESPACE_NAME;
                meta[EVENT] = END;
                meta[OFFSET] = 0;
                meta[LENGTH] = 0;
                super.translate(meta,data);
                
                meta[ITEM] = ITEM_ELEMENT_LOCAL_NAME;
                meta[EVENT] = START;
                meta[OFFSET] = 0;
                meta[LENGTH] = _fieldElement.length;
                System.arraycopy(_fieldElement,0,data,meta[OFFSET],meta[LENGTH]);
                super.translate(meta,data);
                
                meta[ITEM] = ITEM_ELEMENT_LOCAL_NAME;
                meta[EVENT] = END;
                meta[OFFSET] = 0;
                meta[LENGTH] = 0;
                super.translate(meta,data);
                
                meta[ITEM] = ITEM_ELEMENT_CHILDREN;
                meta[EVENT] = START;
                meta[OFFSET] = 0;
                meta[LENGTH] = 0;
                super.translate(meta,data);
                  
                  meta[ITEM] = ITEM_CHARACTERS;
                  meta[EVENT] = START;
                  meta[OFFSET] = 0;
                  meta[LENGTH] = 0;
                  super.translate(meta,data);
                  
          doLoop = true;
          _state = STATE_FIELD_CHARS;
          break;
          
        case STATE_FIELD_CHARS:
          meta[ITEM] = ITEM_CHARACTERS_CODE;
          meta[EVENT] = (_startOfText?START:CONTINUATION);
          meta[OFFSET] = 0;
          _startOfText = false;
          
          int textStart = index;
          meta[LENGTH] = 0;
          
          while( (_state == STATE_FIELD_CHARS) && (index<length) )
          {
            _charPos++;
            byte val = _buffer[index++];
            
            switch(val)
            {
              // additional separators can be put here...
              case '|':
              case ';':
              case ',':
                doLoop = true;
                _state = STATE_FIELD_SEPARATOR;
                break;
              
              case '\r':
                break;
                
              case '\n':
                doLoop = true;
                _state = STATE_END_OF_LINE;
                break;
              
              default:
                meta[LENGTH]++;
            }
          }
          
          if (meta[LENGTH]>0)
          {
            System.arraycopy(_buffer,textStart,data,meta[OFFSET],meta[LENGTH]);
            super.translate(meta,data);  
          }
          
          if (index<length)
            doLoop = true;
          break;
          
        case STATE_FIELD_SEPARATOR:
          // close CharactersCode
          meta[ITEM] = ITEM_CHARACTERS_CODE;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          // close Characters
          meta[ITEM] = ITEM_CHARACTERS;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          // close ElementChildren
          meta[ITEM] = ITEM_ELEMENT_CHILDREN;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          // close field Element
          meta[ITEM] = ITEM_ELEMENT;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          
          _fieldPos++;
          _startOfText = true;
          
          // super.startElement("field",meta,data)
          meta[ITEM] = ITEM_ELEMENT;
          meta[EVENT] = START;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
            
            // // super.pushData(TYPE_NAMESPACE_URI,_namespace,meta,data)
            meta[ITEM] = ITEM_ELEMENT_NAMESPACE_NAME;
            meta[EVENT] = START;
            meta[OFFSET] = 0;
            meta[LENGTH] = _namespace.length;
            System.arraycopy(_namespace,0,data,meta[OFFSET],meta[LENGTH]);
            super.translate(meta,data);
         
            // // super.pop(meta,data)
            meta[ITEM] = ITEM_ELEMENT_NAMESPACE_NAME;
            meta[EVENT] = END;
            meta[OFFSET] = 0;
            meta[LENGTH] = 0;
            super.translate(meta,data);
            
            meta[ITEM] = ITEM_ELEMENT_LOCAL_NAME;
            meta[EVENT] = START;
            meta[OFFSET] = 0;
            meta[LENGTH] = _fieldElement.length;
            System.arraycopy(_fieldElement,0,data,meta[OFFSET],meta[LENGTH]);
            super.translate(meta,data);
            
            meta[ITEM] = ITEM_ELEMENT_LOCAL_NAME;
            meta[EVENT] = END;
            meta[OFFSET] = 0;
            meta[LENGTH] = 0;
            super.translate(meta,data);
            
            meta[ITEM] = ITEM_ELEMENT_CHILDREN;
            meta[EVENT] = START;
            meta[OFFSET] = 0;
            meta[LENGTH] = 0;
            super.translate(meta,data);
              
              meta[ITEM] = ITEM_CHARACTERS;
              meta[EVENT] = START;
              meta[OFFSET] = 0;
              meta[LENGTH] = 0;
              super.translate(meta,data);
              
          doLoop = true;
          _state = STATE_FIELD_CHARS;
          break;
          
        case STATE_END_OF_LINE:
          // close CharactersCode
          meta[ITEM] = ITEM_CHARACTERS_CODE;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          // close Characters
          meta[ITEM] = ITEM_CHARACTERS;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          // close ElementChildren
          meta[ITEM] = ITEM_ELEMENT_CHILDREN;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          // close field Element
          meta[ITEM] = ITEM_ELEMENT;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          // close ElementChildren
          meta[ITEM] = ITEM_ELEMENT_CHILDREN;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          // close line Element
          meta[ITEM] = ITEM_ELEMENT;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          
          doLoop = true;
          _state = STATE_START_OF_LINE;
          break;
          
        case STATE_END_OF_FILE:
          // End of text(), close Children, <field>, close Children, <line>, 
          // close Children, <file>, close Children document
          
          // close CharactersCode
          meta[ITEM] = ITEM_CHARACTERS_CODE;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          // close Characters
          meta[ITEM] = ITEM_CHARACTERS;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          // close ElementChildren
          meta[ITEM] = ITEM_ELEMENT_CHILDREN;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          // close field Element
          meta[ITEM] = ITEM_ELEMENT;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          // close ElementChildren
          meta[ITEM] = ITEM_ELEMENT_CHILDREN;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          // close line Element
          meta[ITEM] = ITEM_ELEMENT;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          // close ElementChildren
          meta[ITEM] = ITEM_ELEMENT_CHILDREN;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          // close file Element
          meta[ITEM] = ITEM_ELEMENT;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          // close document
          meta[ITEM] = ITEM_DOCUMENT;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.translate(meta,data);
          
          _state = STATE_AFTER_THE_END;
          break;
          
        default: // STATE_AFTER_THE_END
          return;
      }
      
    } while(doLoop);
    
  }
  
  public ITokyoNaut plug(ITokyoNaut destination)
  {
    return super.plug(destination);
  }
  
  public void unplug()
  {
    _buffer = null;
    _state = STATE_UNSET;
    super.unplug();
  }
  */
}
