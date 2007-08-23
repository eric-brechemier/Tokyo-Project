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
 */
package net.sf.tokyo.prototype1.tokyonauts;

import net.sf.tokyo.ITokyoNaut;

/**
 * Base implementation of ITokyoNaut interface based on Version One.<br/>
 *
 * <p>
 * Provides simple forwarding of meta/data.
 * Used as a base class for TokyoNaut implementations in Prototype One.
 * </p>
 */
public class NCommonBase implements ITokyoNaut
{
  protected ITokyoNaut _destination;
  
  protected static final byte VERSION_ONE = 0x01;
  
  protected static final byte VERSION = 0;
  protected static final byte EVENT = 1;
  protected static final byte ITEM = 2;
  protected static final byte OFFSET = 3;
  protected static final byte LENGTH = 4;
  
  protected static final byte START = 1;
  protected static final byte CONTINUATION = 0;
  protected static final byte END = -1;
  protected static final byte ERROR = 0xE;
  
  // useful item types in the scope of this prototype
  protected static final int ITEM_DOCUMENT = 0x10;
  protected static final int ITEM_DOCUMENT_CHILDREN = 0x11;
  protected static final int ITEM_DOCUMENT_CHARACTER_ENCODING = 0x16;
  protected static final int ITEM_ELEMENT = 0x20;
  protected static final int ITEM_ELEMENT_NAMESPACE_NAME = 0x21;
  protected static final int ITEM_ELEMENT_LOCAL_NAME = 0x22;
  protected static final int ITEM_ELEMENT_PREFIX = 0x23;
  protected static final int ITEM_ELEMENT_CHILDREN = 0x24;
  protected static final int ITEM_ELEMENT_ATTRIBUTES = 0x25;
  protected static final int ITEM_ELEMENT_NAMESPACE_ATTRIBUTES = 0x26;
  protected static final int ITEM_ATTRIBUTE = 0x30;
  protected static final int ITEM_ATTRIBUTE_NAMESPACE_NAME = 0x31;
  protected static final int ITEM_ATTRIBUTE_LOCAL_NAME = 0x32;
  protected static final int ITEM_ATTRIBUTE_PREFIX = 0x33;
  protected static final int ITEM_ATTRIBUTE_NORMALIZED_VALUE = 0x34;
  protected static final int ITEM_CHARACTERS = 0x60;
  protected static final int ITEM_CHARACTERS_CODE = 0x61;
  protected static final int ITEM_NAMESPACE = 0xB0;
  protected static final int ITEM_NAMESPACE_PREFIX = 0xB1;
  protected static final int ITEM_NAMESPACE_NAME = 0xB2;
  
  public boolean areWeThereYet()
  {
    if (_destination != null)
      return _destination.areWeThereYet();
    
    return true;
  }
  
  public void filter(int[]meta, byte[] data)
  {
    if (_destination != null)
      _destination.filter(meta,data);
  }
  
  public ITokyoNaut plug(ITokyoNaut destination)
  {
    if (_destination!=null)
      _destination.unplug();
    
    _destination = destination;
    return destination;
  }
  
  public void unplug()
  {
    if (_destination==null)
      return;
      
    _destination.unplug();
    _destination = null;
  }
}