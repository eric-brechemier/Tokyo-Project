/* ===============================================================
 The Tokyo Project is hosted on GitHub:
 https://github.com/eric-brechemier/Tokyo-Project
 
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

import java.io.FileOutputStream;

import net.sf.tokyo.ITokyoNaut;

/**
 * TokyoNaut writing tokens to a file.<br/>
 *
 * <p>
 *   <ul>
 *     <li><em>Role:</em> Destination</li>
 *     <li><em>Input Consumed:</em> Any non-error token</li>
 *     <li><em>Output Produced:</em> None</li>
 *     <li><em>Side effect:</em> Writes all incoming data fragments to a file</li>
 *     <li><em>Errors Produced: (* fatal errors)</em>
 *       <ul>
 *         <li><code>0x200*</code> - no preceding TokyoNaut available as Source</li>
 *         <li><code>0x201</code> - data expected from source TokyoNaut was not received</li>
 *         <li><code>0x202</code> - I/O error writing bytes in file</li>
 *       </ul>
 *     </li>
 *   </ul>
 * </p>
 */
public class BinaryToFileNaut implements ITokyoNaut
{
  protected byte[] _data;
  protected FileOutputStream _out;
  
  public BinaryToFileNaut(String filePath)
  {
    try
    {
      _out = new FileOutputStream(filePath);
    }
    catch(Exception e)
    {
      System.err.println("Error opening file for output in BinaryToFileNaut(): "+e);
    }
  }
  
  public boolean areWeThereYet(ITokyoNaut[] chain, int position, int[] meta)
  {
    int source = position-1;
    if (source<0)
    {
      meta[LANGUAGE]=LANGUAGE_ERROR;
      meta[TOKEN]=0x200;
      _terminate();
      return true;
    }
    
    if ( chain[source].areWeThereYet(chain,source,meta) )
    {
      _terminate();
      return true;
    }
    
    if (_data==null)
    {
      meta[LANGUAGE]=LANGUAGE_ERROR;
      meta[TOKEN]=0x201;
      return false;
    }
    
    try
    {
      _out.write(_data,meta[OFFSET],meta[LENGTH]);
    }
    catch(Exception e)
    {
      System.err.println("Error in BinaryToFileNaut#areWeThereYet(): "+e);
      meta[LANGUAGE]=LANGUAGE_ERROR;
      meta[TOKEN]=0x201;
      return false;
    }
    
    int destination = position+1;
    if (destination<chain.length)
      chain[destination].notYet(this,_data);
    return false;
  }
  
  public void notYet(ITokyoNaut source, byte[] data)
  {
    _data = data;
  }
  
  protected void _terminate()
  {
    try
    {
      if (_out!=null)
        _out.close();
      _out = null;
    }
    catch(Exception e)
    {
      System.err.println("Error closing file in BinaryToFileNaut#_terminate() "+e);
    }
  }
  
}
