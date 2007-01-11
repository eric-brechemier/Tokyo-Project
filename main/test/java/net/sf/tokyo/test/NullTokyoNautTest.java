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
 * Simple coverage test of Tokyo API based on NullTokyoNaut.
 */
public class NullTokyoNautTest
{
  public static void main(String[] args)
  {
    Object[] rules = {"http://tokyo.sf.net/null/rules","do nothing"};
    Object[] state = {};
    Object[] values = {};
    
    ITokyoNaut tokyoNaut = new NullTokyoNaut();
    while( tokyoNaut.remain(rules,state,values) >0 ) 
    {
      tokyoNaut.morph(rules,state,values);
    }
    System.out.println("Tokyo API coverage test complete.");
  }
}