/*
 * The Tokyo Project is hosted on Sourceforge:
 * http://sourceforge.net/projects/tokyo/
 * 
 * Copyright (c) 2005-2007 Eric Bréchemier
 * http://eric.brechemier.name
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 *
 */
package net.sf.tokyo.prototype1.tokyonauts;

import net.sf.tokyo.ITokyoNaut;

import java.io.UnsupportedEncodingException;

/**
 * Write Tokyo Project events to CSV.<br/>
 */
public class NWriteCsv extends NCommonBase implements ITokyoNaut
{
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
  
  public void filter(int[]meta, byte[] data)
  {
    if(areWeThereYet() || meta[VERSION]!=VERSION_ONE)
      return;
    
    switch(meta[ITEM])
    {
      case ITEM_DOCUMENT:
        _completed = (meta[EVENT]==END);
        super.filter(meta,data);
        break;
      
      case ITEM_CHARACTERS_CODE:
        meta[ITEM] = ITEM_DOCUMENT;
        meta[EVENT] = CONTINUATION;
        super.filter(meta,data);
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
              super.filter(meta,data);
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
              super.filter(meta,data);
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
}