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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

import net.sf.tokyo.ITokyoNaut;
import net.sf.tokyo.prototype1.languages.IJavaChar;
import net.sf.tokyo.prototype1.tokyonauts.NCommonBase;

/**
 * TokyoNaut Filter receiving tokens with buffers of binary, and creating 
 * tokens with individual Java characters based on chosen character set.<br/>
 */
public class BinaryToJavaCharNaut extends NCommonBase implements ITokyoNaut, IJavaChar
{
  protected CharsetDecoder _decoder;
  protected ByteBuffer _inBytes;
  protected CharBuffer _outChars;
  protected int _charStartOffset;
  protected boolean _isUnderflow;
  
  public BinaryToJavaCharNaut(String charset)
  {
    try 
    {
      _decoder = Charset.forName(charset).newDecoder();
      _inBytes = null;
      _outChars = CharBuffer.allocate(2); // long enough for surrogate chars
      _outChars.flip(); // set limit to 0 to avoid reading array filled with initial 0 values
      _charStartOffset = 0;
      _isUnderflow = true;
    }
    catch(Exception e)
    {
      System.err.println("Error in BinaryToJavaCharNaut(): "+e);
    }
  }
  
  
  public boolean areWeThereYet(int[] meta, byte[] data)
  {
    if ( super.areWeThereYet(meta,data) || _decoder==null || _outChars==null )
      return true;
    
    do
    {
      if (  _isUnderflow  &&  ( !_outChars.hasRemaining() )  )
      {
        if ( _inBytes!=null && _inBytes.hasRemaining() )
        {
          meta[LANGUAGE]=LANGUAGE_BINARY;
          meta[TOKEN]=TOKEN_REMAIN;
          meta[OFFSET]=0;
          meta[LENGTH]=_inBytes.remaining();
          _inBytes.compact();
        }
        
        if ( _src.areWeThereYet(meta,data) )
        {
          // Finalize
          if (_inBytes!=null)
          {
            CoderResult lastResult = _decoder.decode(_inBytes,_outChars,true);
            if ( lastResult.isError() )
            {
              meta[LANGUAGE]=LANGUAGE_ERROR;
              meta[TOKEN]=0x30F;
            }
          }
          return true;
        }
        // Skip unknown tokens
        if ( meta[LANGUAGE]!=LANGUAGE_BINARY || meta[TOKEN]!=TOKEN_BINARY )
          return false;
      
        // Load input data
        if ( _inBytes==null || _inBytes.array()!=data )
        {
          _inBytes = ByteBuffer.wrap(data,meta[OFFSET],meta[LENGTH]);
        }
        else
        {
          _inBytes.position(meta[OFFSET]);
          _inBytes.limit(meta[OFFSET]+meta[LENGTH]);
        }
        
        _isUnderflow = false;
      }
      
      if ( !_outChars.hasRemaining() )
      {
        _charStartOffset = _inBytes.position();
        _outChars.clear(); // set full buffer as storage space, before writing
        CoderResult decodingResult = _decoder.decode(_inBytes,_outChars,false);
        _outChars.flip(); // flip is required before reading or checking remaining()
        
        if ( decodingResult.isError() )
        {
          meta[LANGUAGE]=LANGUAGE_ERROR;
          meta[TOKEN]=(decodingResult.isMalformed()? 0x311 : 0x312);
          meta[OFFSET]=_inBytes.position();
          meta[LENGTH]=decodingResult.length();
          _inBytes.position( _inBytes.position()+decodingResult.length() );
          return false;
        }
        
        if ( decodingResult.isUnderflow() )
          _isUnderflow = true;
      }
      
      if ( _outChars.hasRemaining() )
      {
        meta[LANGUAGE] = LANGUAGE_JAVA_CHAR;
        meta[TOKEN] = _outChars.get();;
        meta[LEFT] = LEFT_START;
        meta[RIGHT] = RIGHT_END;
        meta[OFFSET] = _charStartOffset;
        meta[LENGTH] = _inBytes.position()-_charStartOffset;
        return false;
      }
    }
    while(true);
  }
  
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
      System.err.println("Error resetting charset decoder in BinaryToJavaCharNaut#unplug() "+e);
    }
    
    return super.unplug(foe);
  }
}