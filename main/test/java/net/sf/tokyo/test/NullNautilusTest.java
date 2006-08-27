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

public class NullNautilusTest
{
  
  public static void main(String[] args) {
    
    ITokyoNaut nullNavigator = new NullNautilus();
    assertTrue(
      "For NullNautilus: plug(...) must return parameter",
      nullNavigator.plug(null, "null role")==null
    );
    assertTrue(
      "For NullNautilus: unplug(...) must return this",
      nullNavigator.unplug(null, "null role")==nullNavigator
    );
    
    assertTrue(
      "For NullNautilus: map(...) must return 0",
      nullNavigator.map("anywhere", 1, new Object[30], 0, 30)==0
    );
    assertTrue(
      "For NullNautilus: go(...) must return this",
      nullNavigator.go("anywhere")==nullNavigator
    );
    assertTrue(
      "For NullNautilus: here() must be null",
      nullNavigator.here()==null
    );
    
    assertTrue(
      "For NullNautilus: get(...) must return null",
      nullNavigator.get("anything")==null
    );
    assertTrue(
      "For NullNautilus: put(...) must return null",
      nullNavigator.put("anywhere","anything")==null
    );
  }
  
  /* 
  Following methods extracted from JUnit, by Kent Beck, Erich Gamma, and David Saff
  because latest version could not be compiled using JDK 1.4
  */
  
  public static void assertTrue(String message, boolean condition) {
    if(!condition)
      fail("Assertion failed: "+message);
  }
  
  public static void fail(String message) {
    throw new AssertionError(message);
  }
  
}