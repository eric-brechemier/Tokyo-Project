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
package net.sf.tokyo;

/**
 * ITokyoNaut is the single interface making up Tokyo API.<br/>
 *
 * <p>
 * The name "TokyoNaut" is composed of "Tokyo" which inspired this project, while "Naut" is a shorthand 
 * for Nautilus, Captain Nemo's submarine in "Twenty Thousand Leagues Under the Sea" by Jules Verne. 
 * Captain Nemo's motto, "Mobilis in Mobili", fits well with this flexible data navigation API.
 * </p>
 *
 * <p>
 * TokyoNaut instances (classes that implement the ITokyoNaut interface) are expected to be stateless. 
 * Each call of "morph" is intended to rewrite the input data by interpreting a set of instructions 
 * or "rules", one step at a time, and updating the associated state for use in following runs. 
 * </p>
 * 
 * @author Eric Br&eacute;chemier
 * @version Harajuku
 */
public interface ITokyoNaut
{
  
  /**
   * Rewrite data according to rules and update state.<br/>
   *
   * <p>
   * Rules are interpreted by the TokyoNaut to rewrite the values, one step at a time.
   * While the internal state required for processing is stored in boxes of the state array,
   * ITokyoNaut instances remain stateless.
   * </p>
   *
   * @param state current state resulting from previous morph operation
   * @param rules list of rewrite operations
   * @param data current values
   */
  public void morph(Object[] state, Object[] rules, Object[] data);
  
}