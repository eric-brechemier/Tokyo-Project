/* ===============================================================
 The Tokyo Project is hosted on Sourceforge:
 http://sourceforge.net/projects/tokyo/
 
 Copyright (c) 2005-2008 Eric Bréchemier
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
  Copyright (c) 2005-2008 Eric Bréchemier <tokyo@eric.brechemier.name>
  
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

import net.sf.tokyo.ITokyoNaut;

/**
 * TokyoNaut decoding Unicode values by reading UTF-8 binary data.<br/>
 *
 * <p>
 *   <ul>
 *     <li><em>Role:</em> Filter</li>
 *     <li><em>Input Consumed:</em>
 *       {@link net.sf.tokyo.ITokyoNaut#TOKEN_BINARY TOKEN_BINARY} tokens 
 *       in {@link net.sf.tokyo.ITokyoNaut#LANGUAGE_BINARY LANGUAGE_BINARY}
 *       conform to UTF-8 as defined in RFC 3629
 *     </li>
 *     <li><em>Output Produced:</em>
 *       Unicode code point tokens
 *       in {@link net.sf.tokyo.ITokyoNaut#LANGUAGE_UNICODE_TEXT LANGUAGE_UNICODE_TEXT}
 *     </li>
 *     <li><em>Errors Produced: (* fatal errors)</em>
 *       <ul>
 *         <li><code>0x300*</code> - no preceding TokyoNaut available as Source</li>
 *         <li><code>0x301*</code> - unexpected data received upon termination of Source</li>
 *         <li><code>0x302*</code> - data expected from source TokyoNaut was not received</li>
 *         <li><code>0x3C0*</code> - invalid UTF-8 sequence: value not allowed as leading byte</li>
 *         <li><code>0x3C1*</code> - invalid UTF-8 sequence: value not allowed as payload</li>
 *         <li><code>0x3C2*</code> - invalid UTF-8 sequence: incomplete sequence, or longer than expected</li>
 *       </ul>
 *     </li>
 *   </ul>
 * </p>
 */
public class UTF8ToUnicodeNaut implements ITokyoNaut
{
  protected int[]  _meta;
  protected byte[] _data;
  
  protected int _offsetRead;
  protected int _lengthRead;
  
  protected int _unicode;
  protected int _lengthExpected;
  
  public UTF8ToUnicodeNaut()
  {
    _init();
  }
  
  protected void _init()
  {
    _meta = new int[7];
    _data = null;
    
    _offsetRead = 0;
    _lengthRead = -1;
    _unicode = 0;
    _lengthExpected = 0;
  }
  
  protected void _terminate()
  {
    _meta = null;
    _data = null;
  }
  
  protected boolean _isError(int[] meta)
  {
    return meta[LANGUAGE]==LANGUAGE_ERROR;
  }
  
  protected boolean _isInputEmpty()
  {
    return 
    (
          _data == null
      ||  _meta[OFFSET]+_meta[LENGTH] == _offsetRead+_lengthRead
    );
  }
  
  protected void _loadInputData(ITokyoNaut[] chain, int position, int[] meta)
  {
    _data = null;
    _offsetRead = 0;
    
    // TODO: double buffering of input data...
    
    if (position<0)
    {
      meta[LANGUAGE]=LANGUAGE_ERROR;
      meta[TOKEN]=0x300;
      return;
    } 
    
    boolean isCompleted = chain[position].areWeThereYet(chain,position,_meta);
    
    if ( isCompleted )
    {
      if (_data!=null)
      {
        meta[LANGUAGE]=LANGUAGE_ERROR;
        meta[TOKEN]=0x301;
      }
    }
    else
    {
      if (_data==null)
      {
        meta[LANGUAGE]=LANGUAGE_ERROR;
        meta[TOKEN]=0x302;
      }
    }
  }
  
  // Kept as a reference; to be removed after
  protected int _utf8ToUnicode(byte[] utf8, int offset, int length)
  {
    int[] mask = {0,0x7F,0x1F,0x0F,0x07};
    int code = utf8[offset] & mask[length];
    
    for(int i=1;i<length;i++)
    {
      code <<= 6;
      code |= utf8[offset+i]&0x3F;
    }
    
    return code;
  }
  
  protected void _newUnicodeChar(int[] meta)
  {
    meta[VERSION] = VERSION_NANA;
    meta[LANGUAGE] = LANGUAGE_UNICODE;
    meta[TOKEN] = _unicode;
    
    meta[OFFSET] = _offsetRead;
    meta[LENGTH] = _lengthRead;
    meta[LEFT]   = LEFT_START;
    meta[RIGHT]  = RIGHT_END;
    
    _offsetRead += _lengthRead;
    _unicode = 0;
    _lengthExpected = 0;
  }
  
  protected void _processNext(int[] meta)
  {
    final byte LENGTH_PAYLOAD_FOLLOWING = 6;
    
    final byte SIGNATURE_HEADER_LEADING1  = 0x00;
    final byte SIGNATURE_HEADER_FOLLOWING = 0x80;
    final byte SIGNATURE_HEADER_LEADING2  = 0xC0;
    final byte SIGNATURE_HEADER_LEADING3  = 0xE0;
    final byte SIGNATURE_HEADER_LEADING4  = 0xF0;
    
    final byte MASK_HEADER_LEADING1  = 0x80;
    final byte MASK_HEADER_FOLLOWING = 0xC0;
    final byte MASK_HEADER_LEADING2  = 0xE0;
    final byte MASK_HEADER_LEADING3  = 0xF0;
    final byte MASK_HEADER_LEADING4  = 0xF8;
    
    final byte MASK_PAYLOAD_LEADING1  = 0xFF-SIGNATURE_HEADER_LEADING1;
    final byte MASK_PAYLOAD_FOLLOWING = 0xFF-MASK_HEADER_FOLLOWING;
    final byte MASK_PAYLOAD_LEADING2  = 0xFF-MASK_HEADER_LEADING2;
    final byte MASK_PAYLOAD_LEADING3  = 0xFF-MASK_HEADER_LEADING3;
    final byte MASK_PAYLOAD_LEADING4  = 0xFF-MASK_HEADER_LEADING4;
    
    _lengthRead++;
    byte currentByte = _data[_offsetRead+_lengthRead];
    
    switch( _lengthRead )
    {
      case 1:
        if (currentByte & MASK_HEADER_LEADING1 == SIGNATURE_HEADER_LEADING1)
        {
          _unicode = currentByte;
          _newUnicodeChar(meta);
          return;
        }
        
        if (currentByte & MASK_HEADER_LEADING2 == SIGNATURE_HEADER_LEADING2)
        {
          _unicode = currentByte & MASK_PAYLOAD_LEADING2;
          
          return;
        }
         
        if (currentByte & MASK_HEADER_LEADING3 == SIGNATURE_HEADER_LEADING3)
        {
          _unicode = currentByte & MASK_PAYLOAD_LEADING3;
          return;
        }
        
        if (currentByte & MASK_HEADER_LEADING4 == SIGNATURE_HEADER_LEADING4)
        {
          _unicode = currentByte & MASK_PAYLOAD_LEADING4;
          return;
        }
        
        meta[LANGUAGE]=LANGUAGE_ERROR;
        meta[TOKEN]=0x3C0;
        return;
        
      default:
        if (currentByte & MASK_HEADER_FOLLOWING == SIGNATURE_HEADER_FOLLOWING)
        {
          _unicode <<= LENGTH_PAYLOAD_FOLLOWING;
          _unicode |= currentByte & MASK_PAYLOAD_FOLLOWING;
          
          if ( _lengthRead < _lengthExpected )
            return;
          
          if ( _lengthRead == _lengthExpected )
          {
            _newUnicodeChar(meta);
            return;
          }
          
          meta[LANGUAGE]=LANGUAGE_ERROR;
          meta[TOKEN]=0x3C2;
          return;
        }
        
        meta[LANGUAGE]=LANGUAGE_ERROR;
        meta[TOKEN]=0x3C1;
        return;
    }
      
    
    /*  Mapping of UTF-8 bytes to Unicode code points
    
    UTF-8 Bytes    Range of Code Point       Mapping
        1          [    U+00 ;     U+7F ]   code = utf8[0];
    
        2          [    U+80 ;    U+7FF ]   code = utf8[0]&0x1F;
                                            code <<= 6;
                                            code += utf8[1]&0x3F;
                                            
        3          [   U+800 ;   U+D7FF ]   code = utf8[0]&0x0F;
                   [  U+E000 ;   U+FFFF ]   code <<= 6;
                                            code += utf8[1]&0x3F;
                                            code <<= 6;
                                            code += utf8[2]&0x3F;
            Note:  [  U+D800 ;   U+DFFF ]   => ERROR
     
        4          [ U+10000 ; U+10FFFF ]   code = utf8[0]&0x07;
                                            code <<= 6;
                                            code += utf8[1]&0x3F;
                                            code <<= 6;
                                            code += utf8[2]&0x3F;
                                            code <<= 6;
                                            code += utf8[3]&0x3F;
                                            
        5+         [ U+110000 ; ...     ]   ERROR
    
    UTF-8 Bytes                             Unicode Code Point
 1: 0aaaaaaa                                00000000 00000000 0aaaaaaa
 2: 110aaabb 10cccccc                       00000000 00000aaa bbcccccc
 3: 1110aaaa 10bbbbcc 10dddddd              00000000 aaaabbbb ccdddddd
 4: 11110aaa 10bbcccc 10ddddee 10ffffff     000aaabb ccccdddd eeffffff
    
    Sample UTF-8 sequences:
    
    int[] sample00 = {0x41,0xE2,0x89,0xA2,0xCE,0x91,0x2E};
    int[] sample10 = {0xED,0x95,0x9C,0xEA,0xB5,0xAD,0xEC,0x96,0xB4};
    int[] sample20 = {0xE6,0x97,0xA5,0xE6,0x9C,0xAC,0xE8,0xAA,0x9E};
    int[] sample30 = {0xEF,0xBB,0xBF,0xF0,0xA3,0x8E,0xB4};
    
    */
    
    //_offsetRead = _meta[OFFSET];
    //_lengthRead = _meta[LENGTH];
    //System.arraycopy(_meta,0,meta,0,meta.length);
  }
  
  protected void _sendOutputData(ITokyoNaut[] chain, int position)
  {
    if (position<chain.length)
      chain[position].notYet(this,_data);
  }
  
  public boolean areWeThereYet(ITokyoNaut[] chain, int position, int[] meta)
  {
    if ( _isInputEmpty() )
    {
      _loadInputData(chain,position-1,meta);
      
      if ( _isError(meta) || _isInputEmpty() )
      {
        _terminate();
        return true;
      }
    }
    
    _processNext(meta);
    
    if ( _isError(meta) )
    {
      _terminate();
      return true;
    }
    
    _sendOutputData(chain,position+1);
    return false;
  }
  
  public void notYet(ITokyoNaut source, byte[] data)
  {
    _data = data;
  }
  
}