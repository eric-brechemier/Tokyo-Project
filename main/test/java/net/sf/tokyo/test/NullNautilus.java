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
  public void meet(ITokyoNaut stranger, Object[] context, int[] type, int language) { return; }
  public void quit(ITokyoNaut friend, Object[] context, int[] type, int language)   { return; }
  
  public void say(ITokyoNaut speaker, Object[] context, int[] type, int sentence)   { return; }
  public void play(ITokyoNaut player, Object[] context, int[] type, int play)       { return; }
  public void show(ITokyoNaut shower, Object towel)                                 { return; }
}
