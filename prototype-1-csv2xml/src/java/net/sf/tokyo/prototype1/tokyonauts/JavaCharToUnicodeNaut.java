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

import net.sf.tokyo.ITokyoNaut;
import net.sf.tokyo.prototype1.languages.IJavaChar;
import net.sf.tokyo.prototype1.tokyonauts.NCommonBase;

/**
 * TokyoNaut Filter receiving Java Character tokens, and merging surrogate pairs
 * to create a flow of Unicode Character tokens.<br/>
 */
public class JavaCharToUnicodeNaut extends NCommonBase implements ITokyoNaut, IJavaChar
{
  public JavaCharToUnicodeNaut()
  {
  }
  
  public boolean areWeThereYet(int[] meta, byte[] data)
  {
    if ( super.areWeThereYet(meta,data) )
      return true;
    
    do
    {
      if ( _src.areWeThereYet(meta,data) )
        return true;
      
      // Skip unknown tokens
      if ( meta[LANGUAGE]!=LANGUAGE_JAVA_CHAR )
        return false;
      
      int codePoint;
      
      char high = (char)meta[TOKEN];
      char low;
      if ( Character.isHighSurrogate(high) )
      {
        // ...
        
        if ( _src.areWeThereYet(meta,data) )
        {
          meta[LANGUAGE]=LANGUAGE_ERROR;
          meta[TOKEN]=0x401;
          return true;
        }
        
        low = (char)meta[TOKEN];
        if ( !Character.isLowSurrogate(low) )
        {
          meta[LANGUAGE]=LANGUAGE_ERROR;
          meta[TOKEN]=0x402;
          return false;
        }
        
        codePoint = Character.toCodePoint(high,low);
        //meta[OFFSET] = ...;
        //meta[LENGTH] = ...;
      
      }
      else
      {
        codePoint = meta[TOKEN];
        //meta[OFFSET] = ...;
        //meta[LENGTH] = ...;
      
      }
      
      meta[LANGUAGE] = LANGUAGE_UNICODE_TEXT;
      meta[TOKEN] = codePoint;
      meta[LEFT] = LEFT_START;
      meta[RIGHT] = RIGHT_END;
      
      return false;
    }
    while(true);
    
  }
  
}