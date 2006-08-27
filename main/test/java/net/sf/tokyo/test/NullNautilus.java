/*
 * The Tokyo Project is hosted on Sourceforge:
 * http://sourceforge.net/projects/tokyo/
 * 
 * Copyright (c) 2005-2006 Eric Bréchemier
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
 * Null implementation of ITokyoNaut interface.
 */
public class NullNautilus implements ITokyoNaut
{
  
  /**
   * Null implementation of plug() method.
   * @param collaborator ignored
   * @param role ignored
   * @return collaborator
   */
  public ITokyoNaut plug(ITokyoNaut collaborator, Object role)
  {
    return collaborator;
  }
  
  /**
   * Null implementation of unplug() method.
   * @param collaborator ignored
   * @param role ignored
   * @return this
   */
  public ITokyoNaut unplug(ITokyoNaut collaborator, Object role)
  {
    return this;
  }
  
  /**
   * Null implementation of map() method.
   * @param path ignored
   * @param from ignored
   * @param buffer ignored
   * @param offset ignored
   * @param length ignored
   * @return 0
   */
  public int map(Object path, int from, Object[] buffer, int offset, int length)
  {
    return 0;
  }
  
  /**
   * Null implementation of go() method.
   * @param path ignored
   * @return this
   */
  public ITokyoNaut go(Object path)
  {
    return this;
  }
  
  /**
   * Null implementation of here() method.
   * @return null
   */
  public Object here()
  {
    return null;
  }
  
  /**
   * Null implementation of get() method.
   * @param path ignored
   * @return null
   */
  public Object get(Object path)
  {
    return null;
  }
  
  /**
   * Null implementation of put() method.
   * @param path ignored
   * @param item ignored
   * @return null
   */
  public Object put(Object path, Object item)
  {
    return null;
  }
  
}