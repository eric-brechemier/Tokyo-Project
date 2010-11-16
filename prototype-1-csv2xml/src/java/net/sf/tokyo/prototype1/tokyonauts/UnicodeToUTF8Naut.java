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
 * TokyoNaut writing UTF-8 binary data encoded from integer Unicode values.<br/>
 *
 * <p>
 *   <ul>
 *     <li><em>Role:</em> Filter</li>
 *     <li><em>Input Consumed:</em>
 *       Unicode code point tokens
 *       in {@link net.sf.tokyo.ITokyoNaut#LANGUAGE_UNICODE_TEXT LANGUAGE_UNICODE_TEXT}
 *     </li>
 *     <li><em>Output Produced:</em>
 *       {@link net.sf.tokyo.ITokyoNaut#TOKEN_BINARY TOKEN_BINARY} tokens 
 *       in {@link net.sf.tokyo.ITokyoNaut#LANGUAGE_BINARY LANGUAGE_BINARY}
 *       conform to UTF-8 as defined in RFC 3629
 *     </li>
 *     <li><em>Errors Produced: (* fatal errors)</em>
 *       <ul>
 *         <li><code>0x400*</code> - no preceding TokyoNaut available as Source</li>
 *         <li><code>0x401*</code> - unexpected data received upon termination of Source</li>
 *         <li><code>0x402*</code> - data expected from source TokyoNaut was not received</li>
 *       </ul>
 *     </li>
 *   </ul>
 * </p>
 */
public class UnicodeToUTF8Naut implements ITokyoNaut
{
  protected int[]  _meta;
  protected byte[] _data;
  
  protected int _offsetRead;
  protected int _lengthRead;
  
  public UnicodeToUTF8Naut()
  {
    _init();
  }
  
  protected void _init()
  {
    _meta = new int[7];
    _data = null;
    
    _offsetRead = 0;
    _lengthRead = -1;
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
    _offsetRead = -1;
    _lengthRead = 0;
    
    if (position<0)
    {
      meta[LANGUAGE]=LANGUAGE_ERROR;
      meta[TOKEN]=0x400;
    } 
    
    boolean isCompleted = chain[position].areWeThereYet(chain,position,_meta);
    
    if ( isCompleted )
    {
      if (_data!=null)
      {
        meta[LANGUAGE]=LANGUAGE_ERROR;
        meta[TOKEN]=0x401;  
      }
    }
    else
    {
      if (_data==null)
      {
        meta[LANGUAGE]=LANGUAGE_ERROR;
        meta[TOKEN]=0x402;  
      }
    }
  }
  
  protected void _processNext(int[] meta)
  {
    /*  Mapping of Unicode code points to UTF-8 bytes
    
    Range of Code Point       UTF-8 Bytes   Mapping
    [    U+00 ;     U+7F ]        1         utf8[0] = (byte)code
    
    [    U+80 ;    U+7FF ]        2         utf8[0] = 0xC0 + (code>>6)
                                            utf8[1] = 0x80 + (code&0x3F)
                                            
    [   U+800 ;   U+D7FF ]        3         utf8[0] = 0xE0 + (code>>12)
                                            utf8[1] = 0x80 + ((code>>6)&0x3F)
                                            utf8[2] = 0x80 + (code&0x3F)
    
    [  U+D800 ;   U+DFFF ]      ERROR
    
    [  U+E000 ;   U+FFFF ]        3         same as 3 above
    
    [ U+10000 ; U+10FFFF ]        4         utf8[0] = 0xF0 + (code>>18)
                                            utf8[1] = 0x80 + ((code>>12)&0x3F)
                                            utf8[2] = 0x80 + ((code>>6)&0x3F)
                                            utf8[3] = 0x80 + (code&0x3F)
    
    [ U+110000 ; ...     ]        5+        ERROR
    
    Unicode Code Point              UTF-8 Bytes
    
 1: 00000000 00000000 0aaaaaaa      0aaaaaaa
 2: 00000000 00000aaa bbcccccc      110aaabb 10cccccc
 3: 00000000 aaaabbbb ccdddddd      1110aaaa 10bbbbcc 10dddddd
 4: 000aaabb ccccdddd eeffffff      11110aaa 10bbcccc 10ddddee 10ffffff
    
    */
    
    _offsetRead = _meta[OFFSET];
    _lengthRead = _meta[LENGTH];
    System.arraycopy(_meta,0,meta,0,meta.length);
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