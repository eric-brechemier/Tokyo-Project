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
package net.sf.tokyo.test;

import net.sf.tokyo.ITokyoNaut;

/**
 * Null implementation of ITokyoNaut interface.<br/>
 *
 * <p>
 * Does nothing and if used without boundary will run in a main loop forever.
 * This source code can be used as a base for TokyoNaut implementations.
 * </p>
 */
public class NullTokyoNaut implements ITokyoNaut
{
  public boolean areWeThereYet(int[] meta, byte[] data)
  {
    return false;
  }
  
  public ITokyoNaut plug(ITokyoNaut friend)
  {
    return friend;
  }
  
  public ITokyoNaut unplug(ITokyoNaut foe)
  {
    return foe;
  }
}