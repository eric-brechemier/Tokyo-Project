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

/**
 * Read file and write data into a byte array buffer.<br/>
 *
 */
public class NReadFile extends NCommonBase implements ITokyoNaut
{
  protected FileInputStream _in;
  
  public NReadFile(String inputFilePath)
  {
    try 
    {
      _in = new FileInputStream(inputFilePath);
    }
    catch(Exception e)
    {
      System.err.println("Error in NReadFile(): "+e);
    }
  }
  
  public boolean areWeThereYet(int[] meta, byte[] data)
  {
    if ( !_checkParams(meta,data) || _in==null )
    {
      meta[LANGUAGE]=LANGUAGE_ERROR;
      meta[TOKEN]=0x100;
      return true;
    }
    
    try
    {
      if ( _in.available()==0 )
        return true;
      
      _loadmy(meta);
      meta[LANGUAGE] = LANGUAGE_BINARY;
      meta[TOKEN] = TOKEN_BINARY;
      meta[LEFT] = (_mymeta==null? LEFT_START: LEFT_CONTINUED);
      meta[OFFSET] = 0;
      meta[LENGTH] = data.length;
      
      meta[LENGTH] = _in.read(data,meta[OFFSET],meta[LENGTH]);
      if (meta[LENGTH]==-1)
        meta[LENGTH] = 0;
        
      if (_in.available()==0)
        meta[RIGHT] = RIGHT_END;
      
      _savemy(meta);
    }
    catch(Exception e)
    {
      System.err.println("Error in NReadFile#areWeThereYet(): "+e);
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
      System.err.println("Error closing file in NReadFile#unplug() "+e);
    }
    
    return super.unplug(foe);
  }
}