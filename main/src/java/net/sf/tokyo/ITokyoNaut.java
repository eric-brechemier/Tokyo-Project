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
 * Methods inTouch() and read() are called in turn to check whether data is available for reading,
 * and then have meta and data buffers filled by reading TokyoNaut's writing.
 * </p>
 *
 * <p>
 * Plug and unplug allow to initialize a chain of collaborating TokyoNauts, and unchain them
 * to let them free any allocated ressources at the end of the processing.
 * </p>
 * 
 * @author Eric Br&eacute;chemier
 * @version Harajuku
 */
public interface ITokyoNaut
{
  
  /**
   * Check whether TokyoNaut is available for writing.<br/>
   *
   * <p>
   * </p>
   *
   * @return true when the TokyoNaut is likely to write again, false meaning the end
   */
  public boolean inTouch();
  
  /**
   * Read meta/data written by the TokyoNaut.<br/>
   *
   * <p>
   * The TokyoNaut will take information from the meta array to determine the offset position
   * and the expected or maximal length to be written in the data buffer. It will then update
   * the meta array to reflect the changes following her writing.
   * </p>
   *
   * <p>
   * Below is the definition of fields for leading version 0x01.
   * Versions 0x00-0x9F are "reserved", while 0xA0-0xFF are "user-defined".
   * Implemetors wishing to define their own set of fields MUST signal it with
   * a leading version in the "user-defined" range.
   * </p>
   *
   * <p>
   * <table border="1">
   *   <tr>
   *     <th>version</th>
   *     <th>offset</th>
   *     <th>length</th>
   *     <th>node type</th>
   *     <th>relative level of node</th>
   *   </tr>
   *   <tr>
   *     <td>0x01</td>
   *     <td>
   *     position of next byte to be written, updated to next available byte, or 
   *     -1 if no more data is available, or data.length if buffer is full
   *     </td>
   *     <td>number of bytes expected, changed to number of bytes actually written</td>
   *     <td>
   *     type of data item, the table below defines data items for XML, range 0x00-0x9F is reserved,
   *     0xA0-0xFF user-defined.
   *     </td>
   *     <td>
   *     relative level increments when pushing nodes on current path (+1), 
   *     negative when popping them to attach them to ancestors (-i for ith ancestor), 
   *     null (0) means a continuation of data corresponding to current node. <br/>
   *     Note 1: Node items (prefix, local name, string) are considered one level above the node itself. <br/>
   *     Note 2: Siblings are attached to the parent node (-1).
   *     </td>
   *   </tr>
   * </table>
   * </p>
   *
   * <p>
   * <table border="1">
   *   <tr>
   *     <th>
   *     XML Node Types
   *     </th>
   *   </tr>
   *   <tr>
   *     <td>
   *       <ul>
   *         <li><b>0x10 - Root Node</b></li>
   *         <li><b>0x20 - Element Node</b></li>
   *         <li>&nbsp;&nbsp;0x21 - Element Node - prefix</li>
   *         <li>&nbsp;&nbsp;0x22 - Element Node - local part of name</li>
   *         <li><b>0x30 - Attribute Node</b></li>
   *         <li>&nbsp;&nbsp;0x31 - Attribute Node - prefix</li>
   *         <li>&nbsp;&nbsp;0x32 - Attribute Node - local part of name</li>
   *         <li>&nbsp;&nbsp;0x33 - Attribute Node - string-value</li>
   *         <li><b>0x40 - Namespace Node</b></li>
   *         <li>&nbsp;&nbsp;0x41 - Namespace Node - prefix (= local part of name)</li>
   *         <li>&nbsp;&nbsp;0x42 - Namespace Node - namespace URI (= string-value)</li>
   *         <li><b>0x50 - Processing Instruction Node</b></li>
   *         <li>&nbsp;&nbsp;0x51 - Processing Instruction Node - target (= local part of name)</li>
   *         <li>&nbsp;&nbsp;0x52 - Processing Instruction Node - string-value</li>
   *         <li><b>0x60 - Comment Node</b></li>
   *         <li>&nbsp;&nbsp;0x61 - Comment Node - string-value</li>
   *         <li><b>0x70 - Text Node</b></li>
   *         <li>&nbsp;&nbsp;0x71 - Text Node - string-value</li>
   *       </ul>
   *     </td>
   *   </tr>
   * </table>
   * </p>
   *
   * @param meta information about data, with fields defined by leading version number.
   * @param data the buffer into which the data is read.
   */
  public void read(int[]meta, byte[] data);
  
  /**
   * Plug the TokyoNaut to a source TokyoNaut from which meta/data will be read.<br/>
   *
   * <p>
   * Following an exclusive mode of relationship, a single source TokyoNaut can be plugged 
   * with each TokyoNaut at a given time. Subsequent calls to plug() will unplug and replace 
   * previously plugged TokyoNaut.
   * </p>
   *
   * @param source TokyoNaut providing meta/data to be read.
   * @return source parameter to allow a chaing of plug calls.
   */
  public ITokyoNaut plug(ITokyoNaut source);
  
  /**
   * Unplug the TokyoNaut from previously plugged source TokyoNaut.<br/>
   *
   * <p>
   * This method unplug recursively all source TokyoNauts chained from this point.
   * If no source has been set by a previous call to plug(), nothing happens.
   * </p>
   *
   */
  public void unplug();
  
}