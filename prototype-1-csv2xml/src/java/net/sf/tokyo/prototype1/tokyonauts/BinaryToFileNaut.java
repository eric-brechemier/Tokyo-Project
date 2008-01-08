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

import java.io.FileOutputStream;

import net.sf.tokyo.ITokyoNaut;
import net.sf.tokyo.prototype1.tokyonauts.NCommonBase;


/**
 * TokyoNaut writing a file from tokens carrying binary buffers.<br/>
 *
 */
public class BinaryToFileNaut extends NCommonBase implements ITokyoNaut
{
  protected FileOutputStream _out;
  
  public BinaryToFileNaut(String filePath)
  {
    try 
    {
      _out = new FileOutputStream(filePath);
    }
    catch(Exception e)
    {
      System.err.println("Error in BinaryToFileNaut(): "+e);
    }
  }
  
  public boolean areWeThereYet(int[] meta, byte[] data)
  {
    if ( super.areWeThereYet(meta,data) || _src==null || _out==null )
      return true;
    
    if ( _src.areWeThereYet(meta,data) )
      return true;
    
    // Skip unknown tokens
    if ( meta[LANGUAGE]!=LANGUAGE_BINARY || meta[TOKEN]!=TOKEN_BINARY )
      return false;
    
    try
    {
      _out.write(data,meta[OFFSET],meta[LENGTH]);
    }
    catch(Exception e)
    {
      System.err.println("Error in BinaryToFileNaut#areWeThereYet(): "+e);
      meta[LANGUAGE]=LANGUAGE_ERROR;
      meta[TOKEN]=0x201;
    }
    
    return false;
  }
  
  public ITokyoNaut unplug(ITokyoNaut foe)
  {
    try
    {
      if (_out!=null)
        _out.close();
      _out = null;
    }
    catch(Exception e)
    {
      System.err.println("Error closing file in BinaryToFileNaut#unplug() "+e);
    }
    
    return super.unplug(foe);
  }
}