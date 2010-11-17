/* ===============================================================
 The Tokyo Project is hosted on GitHub:
 https://github.com/eric-brechemier/Tokyo-Project
 
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

import net.sf.tokyo.ITokyoNaut;

/**
 * Base implementation of ITokyoNaut interface based on Version NANA.<br/>
 *
 * <p>
 * Abstract class used as a base class for TokyoNaut implementations in Prototype One.
 * </p>
 */
public abstract class NCommonBase implements ITokyoNaut
{
  protected ITokyoNaut _src;
  
  protected NCommonBase()
  {
    _src = null;
  }
  
  public boolean areWeThereYet(int[] meta, byte[] data)
  {
    if (meta==null)
      return true;
      
    if 
      (    data==null 
        || meta[OFFSET]<0 || meta[OFFSET]>data.length 
        || meta[OFFSET]+meta[LENGTH]<0 || meta[OFFSET]+meta[LENGTH]>data.length 
      )
    {
      meta[LANGUAGE] = LANGUAGE_ERROR;
      meta[TOKEN] = 0xA00;
      return true;
    }
    
    return false;
  }
  
  public ITokyoNaut plug(ITokyoNaut friend)
  {
    if (friend==null)
      return friend;
    
    // Source or Destination?
    // for now, we don't know whether this is a source or a destination
    // which will be determined by the following ping/pong exchange.
    // This is because, for usability, the call is actually src.plug(dest)
    // while for ease of implementation, the inside wanted relationship is
    // dest._src = src
    
    // Step 1: [@source] if null or same as current source, do nothing
    // Step 4: [@destination] _src is null and the friend param is not, so we continue
    // Step 7: [@source] this time, _src is set, so we stop and return _src
    // 
    if (friend==null || friend==_src )
      return friend;
    
    // Step 2: [@source] set _src to stop the recursion at Step 7,
    //                   original source is saved in "previous", to be restored at step 11.
    // Step 5: [@destination] set _src, which is actually the final desired effect in destination
    ITokyoNaut previous = _src;
    _src = friend;
    
    // A different behavior in source and destination:
    // Due to the sequence,
    // User --> source --> destination
    //                 <--
    //                 ...> (result)
    //        (result) <...
    // destination gets the result before source does, which means
    // that the destination can detect its role because it gets ******** as result.
    
    // Step 3: [@source] Check the reverse associate
      // Step 6: [@destination] Check the reverse associate
      // Step 8: [@destination] result is received, per Step 7, result will be "this"
    // Step 10: [@source] Check the reverse associate, per Step 9, result will be destination "friend" (not "this")
    ITokyoNaut result = friend.plug(this);
    
    if ( result!=this )
    {
      // Step 11: [@source] we received destination, different from self,
      //                    _src is reset to previous to cancel the association process in this direction.
      //                    (the final association is unidirectional destination-->source)
      //                    The destination "friend" is returned to allow chained calls.
      
      _src = previous;
      //System.out.println("Added: association "+this+" <-- "+friend);
      return friend;
    }
    
    // Step 9:  [@destination] we received self, we will return self to confirm it is the actual destination
    return this;
  }
  
  public ITokyoNaut unplug(ITokyoNaut foe)
  {
    if (foe!=null)
      foe.unplug(null);
    
    if (foe==_src)
      _src = null;
    
    return foe;
  }
}
