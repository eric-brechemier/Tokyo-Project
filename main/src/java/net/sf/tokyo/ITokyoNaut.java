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
 * ITokyoNaut is the single interface making up Tokyo API, 
 * allowing both XML and non-XML data transformations.<br/>
 *
 * <p>
 * The name "TokyoNaut" is composed of "Tokyo", a mind-changing city where this project started,
 * while "Naut" is a shorthand reference to Nautilus, Captain Nemo's submarine in Jules Verne's
 * "Twenty Thousand Leagues Under the Sea".
 * More inspiration comes from Captain Nemo's motto: "Mobilis in Mobili".
 * </p>
 *
 * <p>
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
   * Rewrite data according to rules based on atomic transformations (TokyoNauts).<br/>
   *
   * <p>
   * Each call of the "morph" method runs one single rewrite step, which could correspond to 
   * one line of text, one binary structure or an XML fragment depending on the format at hand 
   * and the choice of implementation.
   * </p>
   *
   * <p>
   * A set of simple TokyoNaut instances may be associated to realize a complex data transformation,
   * each one located in a separate row of the "rules" array.
   * </p>
   *
   * <p>
   * "state" and "data" have the same number of elements as "rules". The association of these three 
   * arrays can be seen as a table where the row of index "i" is made of (state[i],rules[i],data[i]).
   * </p>
   *
   * <p>
   * "index" is a single element array (int[1]) providing current index as a read/write parameter.
   * The current index is the position of the current TokyoNaut, which will execute its atomic morph 
   * operation in the run of the morph call.
   * </p>
   *
   * <p>
   * Current TokyoNaut takes data as input in data[index[0]] and applies a transformation resulting to 
   * new data as output stored in data[index[1]], provided as input for the following transformation.
   * First transformation would typically take input data from a file or a data input stream, while
   * last transformation would typically store output data in a file or a data output stream.
   * </p>
   *
   * <p> 
   * The returned TokyoNaut instance is the next to be called to continue the transformation process,
   * or null if the transformation is complete (in which case index[0] shall be set to rules.length).
   * </p>
   * 
   * <p>
   * Given that state, rules and data have been properly initialized, the following loop would run
   * a step by step transformation until completion:
<PRE>
int[] index = {0};
ITokyoNaut current = rules[index[0]];
while (current != null)
{
  current = current.morph(state,rules,data,index);
}     
</PRE>
   * </p>
   * 
   * <p>
   * In the above example, it is not possible to replace the "while loop" with a "for loop",
   * because the flow of morph operations will typically loop back and forth from data providers
   * to data consumers, involding several passes trough the same TokyoNauts until completion.
   * </p>
   *
   * @param state initial configuration and current state of atomic transformations
   * @param rules chain of atomic transformations (TokyoNauts)
   * @param data buffers of data for atomic transformations
   * @param index position of current TokyoNaut, identical in the three arrays state,rules,data
   * @return next TokyoNaut to run i.e. rules[index[0]] or null when transformation is complete
   */
  public ITokyoNaut morph(Object[] state, ITokyoNaut[] rules, Object[] data, int[] index);
  
}