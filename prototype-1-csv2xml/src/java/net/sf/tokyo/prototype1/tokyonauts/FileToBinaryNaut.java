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

import java.io.FileInputStream;

import net.sf.tokyo.ITokyoNaut;

/**
 * TokyoNaut reading binary tokens from a file.<br/>
 *
 * <p>
 *   <ul>
 *     <li><em>Role:</em> Source</li>
 *     <li><em>Input Consumed:</em> None</li>
 *     <li><em>Output Produced:</em>
 *       {@link net.sf.tokyo.ITokyoNaut#TOKEN_BINARY TOKEN_BINARY} tokens 
 *       in {@link net.sf.tokyo.ITokyoNaut#LANGUAGE_BINARY LANGUAGE_BINARY}
 *     </li>
 *     <li><em>Errors Produced: (* fatal errors)</em>
 *       <ul>
 *         <li><code>0x101*</code> - I/O error reading or checking bytes available in file</li>
 *       </ul>
 *     </li>
 *   </ul>
 * </p>
 */
public class FileToBinaryNaut implements ITokyoNaut
{
  protected FileInputStream _in;
  protected byte[] _data;
  protected boolean _isStart;
  
  public FileToBinaryNaut(String filePath)
  {
    try 
    {
      _in = new FileInputStream(filePath);
    }
    catch(Exception e)
    {
      System.err.println("Error opening file for input in FileToBinaryNaut(): "+e);
    }
    
    _isStart = true;
    _data = new byte[10];
  }
  
  public boolean areWeThereYet(ITokyoNaut[] chain, int position, int[] meta)
  {
    meta[VERSION] = VERSION_NANA;
    meta[LANGUAGE] = LANGUAGE_BINARY;
    meta[TOKEN] = TOKEN_BINARY;
    meta[LEFT] = (_isStart? LEFT_START: LEFT_CONTINUED);
    _isStart = false;
    meta[OFFSET] = 0;
    meta[LENGTH] = _data.length;
    
    try
    {
      if ( _in.available()==0 )
      {
        _terminate();
        return true;
      }
      
      int bytesRead = _in.read(_data,meta[OFFSET],meta[LENGTH]);
      meta[LENGTH] = (bytesRead==-1? 0 : bytesRead);
      meta[RIGHT] = (_in.available()==0? RIGHT_END : RIGHT_CONTINUED);
    }
    catch(Exception e)
    {
      System.err.println("Error in FileToBinaryNaut#areWeThereYet(): "+e);
      meta[LANGUAGE]=LANGUAGE_ERROR;
      meta[TOKEN]=0x101;
      _terminate();
      return true;
    }
    
    int destination = position+1;
    if (destination<chain.length)
      chain[destination].notYet(this,_data);
    return false;
  }
  
  protected void _terminate()
  {
    try
    {
      if (_in!=null)
          _in.close();
      _in = null;
    }
    catch(Exception e)
    {
      System.err.println("Error closing file in FileToBinaryNaut#_terminate(): "+e);
    }
  }
  
  public void notYet(ITokyoNaut source, byte[] data)
  {
  }
  
}