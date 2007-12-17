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
package net.sf.tokyo.test;

import net.sf.tokyo.ITokyoNaut;

/**
 * Null implementation of ITokyoNaut interface.<br/>
 *
 * <p>
 * Provides simple forwarding of meta/data.
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