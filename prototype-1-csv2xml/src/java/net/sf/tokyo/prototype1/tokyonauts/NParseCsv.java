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
 * Parse Csv Data into Tokyo Project Version One meta/data.<br/>
 * Expects UTF-8 encoded text with coma separated values, lines separated with
 * \n or \r\n.
 */
public class NParseCsv extends NCommonBase implements ITokyoNaut
{
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
  
  public void filter(int[]meta, byte[] data)
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
          super.filter(meta,data);
            
            // super.startElement("file",meta,data)
            // // super.push(TYPE_ELEMENT,meta,data)
            meta[ITEM] = ITEM_ELEMENT;
            meta[EVENT] = START;
            meta[OFFSET] = 0;
            meta[LENGTH] = 0;
            super.filter(meta,data);
              
              // // super.pushData(TYPE_NAMESPACE_URI,_namespace,meta,data)
              meta[ITEM] = ITEM_ELEMENT_NAMESPACE_NAME;
              meta[EVENT] = START;
              meta[OFFSET] = 0;
              meta[LENGTH] = _namespace.length;
              System.arraycopy(_namespace,0,data,meta[OFFSET],meta[LENGTH]);
              super.filter(meta,data);
           
              // // super.pop(meta,data)
              meta[ITEM] = ITEM_ELEMENT_NAMESPACE_NAME;
              meta[EVENT] = END;
              meta[OFFSET] = 0;
              meta[LENGTH] = 0;
              super.filter(meta,data);
              
              // // super.pushData(TYPE_ELEMENT_LOCAL_NAME,_fileElement,meta,data)
              meta[ITEM] = ITEM_ELEMENT_LOCAL_NAME;
              meta[EVENT] = START;
              meta[OFFSET] = 0;
              meta[LENGTH] = _fileElement.length;
              System.arraycopy(_fileElement,0,data,meta[OFFSET],meta[LENGTH]);
              super.filter(meta,data);
          
              // // super.pop(meta,data)
              meta[ITEM] = ITEM_ELEMENT_LOCAL_NAME;
              meta[EVENT] = END;
              meta[OFFSET] = 0;
              meta[LENGTH] = 0;
              super.filter(meta,data);
              
              meta[ITEM] = ITEM_ELEMENT_CHILDREN;
              meta[EVENT] = START;
              meta[OFFSET] = 0;
              meta[LENGTH] = 0;
              super.filter(meta,data);
              
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
          super.filter(meta,data);
            
            // // super.pushData(TYPE_NAMESPACE_URI,_namespace,meta,data)
            meta[ITEM] = ITEM_ELEMENT_NAMESPACE_NAME;
            meta[EVENT] = START;
            meta[OFFSET] = 0;
            meta[LENGTH] = _namespace.length;
            System.arraycopy(_namespace,0,data,meta[OFFSET],meta[LENGTH]);
            super.filter(meta,data);
         
            // // super.pop(meta,data)
            meta[ITEM] = ITEM_ELEMENT_NAMESPACE_NAME;
            meta[EVENT] = END;
            meta[OFFSET] = 0;
            meta[LENGTH] = 0;
            super.filter(meta,data);
              
            meta[ITEM] = ITEM_ELEMENT_LOCAL_NAME;
            meta[EVENT] = START;
            meta[OFFSET] = 0;
            meta[LENGTH] = _lineElement.length;
            System.arraycopy(_lineElement,0,data,meta[OFFSET],meta[LENGTH]);
            super.filter(meta,data);
            
            meta[ITEM] = ITEM_ELEMENT_LOCAL_NAME;
            meta[EVENT] = END;
            meta[OFFSET] = 0;
            meta[LENGTH] = 0;
            super.filter(meta,data);
            
            meta[ITEM] = ITEM_ELEMENT_CHILDREN;
            meta[EVENT] = START;
            meta[OFFSET] = 0;
            meta[LENGTH] = 0;
            super.filter(meta,data);
            
              // super.startElement("field",meta,data)
              meta[ITEM] = ITEM_ELEMENT;
              meta[EVENT] = START;
              meta[OFFSET] = 0;
              meta[LENGTH] = 0;
              super.filter(meta,data);
                
                // // super.pushData(TYPE_NAMESPACE_URI,_namespace,meta,data)
                meta[ITEM] = ITEM_ELEMENT_NAMESPACE_NAME;
                meta[EVENT] = START;
                meta[OFFSET] = 0;
                meta[LENGTH] = _namespace.length;
                System.arraycopy(_namespace,0,data,meta[OFFSET],meta[LENGTH]);
                super.filter(meta,data);
             
                // // super.pop(meta,data)
                meta[ITEM] = ITEM_ELEMENT_NAMESPACE_NAME;
                meta[EVENT] = END;
                meta[OFFSET] = 0;
                meta[LENGTH] = 0;
                super.filter(meta,data);
                
                meta[ITEM] = ITEM_ELEMENT_LOCAL_NAME;
                meta[EVENT] = START;
                meta[OFFSET] = 0;
                meta[LENGTH] = _fieldElement.length;
                System.arraycopy(_fieldElement,0,data,meta[OFFSET],meta[LENGTH]);
                super.filter(meta,data);
                
                meta[ITEM] = ITEM_ELEMENT_LOCAL_NAME;
                meta[EVENT] = END;
                meta[OFFSET] = 0;
                meta[LENGTH] = 0;
                super.filter(meta,data);
                
                meta[ITEM] = ITEM_ELEMENT_CHILDREN;
                meta[EVENT] = START;
                meta[OFFSET] = 0;
                meta[LENGTH] = 0;
                super.filter(meta,data);
                  
                  meta[ITEM] = ITEM_CHARACTERS;
                  meta[EVENT] = START;
                  meta[OFFSET] = 0;
                  meta[LENGTH] = 0;
                  super.filter(meta,data);
                  
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
            super.filter(meta,data);  
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
          super.filter(meta,data);
          // close Characters
          meta[ITEM] = ITEM_CHARACTERS;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
          // close ElementChildren
          meta[ITEM] = ITEM_ELEMENT_CHILDREN;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
          // close field Element
          meta[ITEM] = ITEM_ELEMENT;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
          
          _fieldPos++;
          _startOfText = true;
          
          // super.startElement("field",meta,data)
          meta[ITEM] = ITEM_ELEMENT;
          meta[EVENT] = START;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
            
            // // super.pushData(TYPE_NAMESPACE_URI,_namespace,meta,data)
            meta[ITEM] = ITEM_ELEMENT_NAMESPACE_NAME;
            meta[EVENT] = START;
            meta[OFFSET] = 0;
            meta[LENGTH] = _namespace.length;
            System.arraycopy(_namespace,0,data,meta[OFFSET],meta[LENGTH]);
            super.filter(meta,data);
         
            // // super.pop(meta,data)
            meta[ITEM] = ITEM_ELEMENT_NAMESPACE_NAME;
            meta[EVENT] = END;
            meta[OFFSET] = 0;
            meta[LENGTH] = 0;
            super.filter(meta,data);
            
            meta[ITEM] = ITEM_ELEMENT_LOCAL_NAME;
            meta[EVENT] = START;
            meta[OFFSET] = 0;
            meta[LENGTH] = _fieldElement.length;
            System.arraycopy(_fieldElement,0,data,meta[OFFSET],meta[LENGTH]);
            super.filter(meta,data);
            
            meta[ITEM] = ITEM_ELEMENT_LOCAL_NAME;
            meta[EVENT] = END;
            meta[OFFSET] = 0;
            meta[LENGTH] = 0;
            super.filter(meta,data);
            
            meta[ITEM] = ITEM_ELEMENT_CHILDREN;
            meta[EVENT] = START;
            meta[OFFSET] = 0;
            meta[LENGTH] = 0;
            super.filter(meta,data);
              
              meta[ITEM] = ITEM_CHARACTERS;
              meta[EVENT] = START;
              meta[OFFSET] = 0;
              meta[LENGTH] = 0;
              super.filter(meta,data);
              
          doLoop = true;
          _state = STATE_FIELD_CHARS;
          break;
          
        case STATE_END_OF_LINE:
          // close CharactersCode
          meta[ITEM] = ITEM_CHARACTERS_CODE;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
          // close Characters
          meta[ITEM] = ITEM_CHARACTERS;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
          // close ElementChildren
          meta[ITEM] = ITEM_ELEMENT_CHILDREN;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
          // close field Element
          meta[ITEM] = ITEM_ELEMENT;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
          // close ElementChildren
          meta[ITEM] = ITEM_ELEMENT_CHILDREN;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
          // close line Element
          meta[ITEM] = ITEM_ELEMENT;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
          
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
          super.filter(meta,data);
          // close Characters
          meta[ITEM] = ITEM_CHARACTERS;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
          // close ElementChildren
          meta[ITEM] = ITEM_ELEMENT_CHILDREN;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
          // close field Element
          meta[ITEM] = ITEM_ELEMENT;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
          // close ElementChildren
          meta[ITEM] = ITEM_ELEMENT_CHILDREN;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
          // close line Element
          meta[ITEM] = ITEM_ELEMENT;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
          // close ElementChildren
          meta[ITEM] = ITEM_ELEMENT_CHILDREN;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
          // close file Element
          meta[ITEM] = ITEM_ELEMENT;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
          // close document
          meta[ITEM] = ITEM_DOCUMENT;
          meta[EVENT] = END;
          meta[OFFSET] = 0;
          meta[LENGTH] = 0;
          super.filter(meta,data);
          
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
  
}