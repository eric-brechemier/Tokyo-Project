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
package net.sf.tokyo.prototype1;

import net.sf.tokyo.ITokyoNaut;


/**
 * Prototype One Language Definition.<br/>
 *
 * <p>
 * In the context of this language, rules[0] shall contain a ProtoOneLang instance;
 * following boxes of rules shall contain TokyoNauts only..
 * </p>
 *
 * <p>
 * state[0] will contain an int[1] array currentRulePosition, 
 * indicating the position in the rules array of the rule currently executed.
 * </p>
 * 
 * <p>
 * Each TokyoNaut is supposed to look at currentRulePosition in state[0];
 * it can then read and store its state in state[currentRulePosition[0]],
 * and shall transform data taking input in data[currentRulePosition[0]],
 * putting result into data[currentRulePosition[0]+1]
 * </p> 
 *
 * @author Eric Br&eacute;chemier
 * @version Harajuku
 */
public class ProtoOneLang
{
  public static String URI = "http://sf.tokyo.net/prototype-1/lang";
  
  public String toString()
  {
    return URI;
  }
  
  
  
  
  
  public static class InFileNaut implements ITokyoNaut
  {
    public void morph(Object[] state, Object[] rules, Object[] data)
    { 
      return;
    }
  }
  
  public static class InCsvNaut implements ITokyoNaut
  {
    public void morph(Object[] state, Object[] rules, Object[] data)
    { 
      return;
    }
  }
  
  public static class InSaxNaut implements ITokyoNaut
  {
    public void morph(Object[] state, Object[] rules, Object[] data)
    { 
      return;
    }
  }
  
  public static class XslTransformNaut implements ITokyoNaut
  {
    public void morph(Object[] state, Object[] rules, Object[] data)
    { 
      return;
    }
  }
  
  public static class OutSaxNaut implements ITokyoNaut
  {
    public void morph(Object[] state, Object[] rules, Object[] data)
    { 
      return;
    }
  }
  
  public static class OutCsvNaut implements ITokyoNaut
  {
    public void morph(Object[] state, Object[] rules, Object[] data)
    { 
      return;
    }
  }
  
  public static class OutFileNaut implements ITokyoNaut
  {
    public void morph(Object[] state, Object[] rules, Object[] data)
    { 
      return;
    }
  }
  
}