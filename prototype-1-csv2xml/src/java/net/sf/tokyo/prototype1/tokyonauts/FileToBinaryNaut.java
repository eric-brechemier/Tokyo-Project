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

import java.io.FileInputStream;

import net.sf.tokyo.ITokyoNaut;
import net.sf.tokyo.prototype1.tokyonauts.NCommonBase;


/**
 * TokyoNaut creating tokens of binary buffer from data read in a file.<br/>
 *
 */
public class FileToBinaryNaut extends NCommonBase implements ITokyoNaut
{
  protected FileInputStream _in;
  
  public FileToBinaryNaut(String filePath)
  {
    try 
    {
      _in = new FileInputStream(filePath);
    }
    catch(Exception e)
    {
      System.err.println("Error in FileToBinaryNaut(): "+e);
    }
  }
  
  public boolean areWeThereYet(int[] meta, byte[] data)
  {
    if ( super.areWeThereYet(meta,data) || _in==null )
      return true;
    
    int writeOffset;
    int writeLength;
    
    if (meta[TOKEN]==TOKEN_REMAIN)
    {
      writeOffset = meta[LENGTH];
      writeLength = data.length-writeOffset;
    }
    else
    {
      writeOffset = 0;
      writeLength = data.length;
    }
    
    try
    {
      if ( _in.available()==0 )
        return true;
      
      int bytesRead = _in.read(data,writeOffset,writeLength);
      
      meta[OFFSET] = 0;
      meta[LENGTH] = (bytesRead==-1? writeOffset : writeOffset+bytesRead);
      
      meta[LEFT] = (meta[TOKEN]==TOKEN_SPARK? LEFT_START: LEFT_CONTINUED);
      meta[RIGHT] = (_in.available()==0? RIGHT_END : RIGHT_CONTINUED);
      
      meta[LANGUAGE] = LANGUAGE_BINARY;
      meta[TOKEN] = TOKEN_BINARY;
    }
    catch(Exception e)
    {
      System.err.println("Error in FileToBinaryNaut#areWeThereYet(): "+e);
      meta[LANGUAGE]=LANGUAGE_ERROR;
      meta[TOKEN]=0x101;
      return true;
    }
    
    return false;
  }
  
  public ITokyoNaut unplug(ITokyoNaut foe)
  {
    try
    {
      if (_in!=null)
        _in.close();
      _in = null;
    }
    catch(Exception e)
    {
      System.err.println("Error closing file in FileToBinaryNaut#unplug() "+e);
    }
    
    return super.unplug(foe);
  }
}