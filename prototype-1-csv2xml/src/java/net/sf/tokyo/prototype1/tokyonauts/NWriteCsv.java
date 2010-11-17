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

import net.sf.tokyo.prototype1.tokyonauts.NCommonBase;

import net.sf.tokyo.prototype1.languages.ICSV;

import java.io.UnsupportedEncodingException;

/**
 * Write Tokyo Project events to CSV.<br/>
 */
public class NWriteCsv extends NCommonBase implements ITokyoNaut, ICSV
{
  /*
  protected boolean _completed;
  protected String _charEncoding;
  protected int _lineNumber;
  protected int _fieldNumber;
  
  public NWriteCsv()
  {
    _completed = false;
    _charEncoding = "UTF-8";
    _lineNumber = 1;
    _fieldNumber = 1;
  }
  
  public boolean areWeThereYet()
  {
    return _completed;
  }
  
  public void translate(int[]meta, byte[] data)
  {
    if(areWeThereYet() || meta[VERSION]!=VERSION_ONE)
      return;
    
    switch(meta[ITEM])
    {
      case ITEM_DOCUMENT:
        _completed = (meta[EVENT]==END);
        super.translate(meta,data);
        break;
      
      case ITEM_CHARACTERS_CODE:
        meta[ITEM] = ITEM_DOCUMENT;
        meta[EVENT] = CONTINUATION;
        super.translate(meta,data);
        break;
      
      case ITEM_DOCUMENT_CHARACTER_ENCODING:
        String charEncodingPart=null;
        try
        {
          charEncodingPart = new String(data,meta[OFFSET],meta[LENGTH],"UTF-8");
        }
        catch(UnsupportedEncodingException e)
        {
          // TODO: signal error with event
          System.out.println("ERROR: "+e);
        }
        if (meta[EVENT]==START)
          _charEncoding = charEncodingPart;
        else if (meta[EVENT]==CONTINUATION)
          _charEncoding = _charEncoding + charEncodingPart;
        break;
        
      // case NAMESPACE...
      // TODO: consider namespace URI as well
      
      case ITEM_ELEMENT_LOCAL_NAME:
        
        // TODO: consider case with CONTINUATION for localName
        if (meta[EVENT]==START)
        {
          String strLocalName=null;
          try
          {
            strLocalName = new String(data,meta[OFFSET],meta[LENGTH],_charEncoding);
          }
          catch(UnsupportedEncodingException e)
          {
            // TODO: signal error with event
            System.out.println("ERROR: "+e);
          }
          
          meta[ITEM] = ITEM_DOCUMENT;
          
          // if "file" nothing to do
          if ("line".equals(strLocalName))
          {
            if (_lineNumber>1)
            {
              meta[ITEM] = ITEM_DOCUMENT;
              meta[EVENT] = CONTINUATION;
              meta[OFFSET] = 0;
              meta[LENGTH] = 2;
              data[0] = '\r';
              data[1] = '\n';
              super.translate(meta,data);
            }
            _lineNumber ++;
            _fieldNumber = 1;
          }
          else if ("field".equals(strLocalName))
          {
            if (_fieldNumber>1)
            {
              meta[ITEM] = ITEM_DOCUMENT;
              meta[EVENT] = CONTINUATION;
              meta[OFFSET] = 0;
              meta[LENGTH] = 1;
              data[0] = ',';
              super.translate(meta,data);
            }
            _fieldNumber++;
          }
          else
            System.out.println("Unexpected element: "+strLocalName);
        }
        break;
      
      default:
        // no event
        
    }
    
  }
  
  public ITokyoNaut plug(ITokyoNaut destination)
  {
    return super.plug(destination);
  }
  
  public void unplug()
  {
    super.unplug();
  }
  */
}
