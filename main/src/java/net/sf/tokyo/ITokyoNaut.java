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
package net.sf.tokyo;

/**
 * ITokyoNaut is the single interface of TokyoNautilus API<br />
 *
 * The TokyoNautilus API enables both XML and non-XML data browsing through a concise API. Nautilus is a 
 * reference to Captain Nemo's submarine in "Twenty Thousand Leagues Under the Sea" by Jules Verne.
 * Captain Nemo's motto, "Mobilis in Mobili", fits well with this flexible data navigation API. 
 * 
 * @author Eric Bréchemier
 * @version Version One
 */
public interface ITokyoNaut
{
  /**
   * Delegate some parts of the data handling with a "collaborator", as specified by "role".<br/>
   *
   * <p>
   * This method allows to combine different INautilus in a flexible way, 
   * each responsible for a different part of the data extraction, e.g.:
   *   <ul>
   *     <li>Query: XPath parsing and interpretation</li>
   *     <li>Grammar defining the data structure</li>
   *     <li>Data parsing and aggregation into result</li>
   *   </ul>
   * </p>
   *
   * <p>Null Implementation:</p>
   * <pre>
   * return collaborator;
   * </pre>
   *
   * @param collaborator complementary TokyoNaut
   * @param role implementation-specific role, identifying the type of collaboration involved. The same instance can be plugged several times with different roles.
   * @return return collaborator to allow further combinations on the same line of code
   */
  public ITokyoNaut plug(ITokyoNaut collaborator, Object role);
  
  /**
   * Make "collaborator" redundant to free resources... ;(<br/>
   *
   * <p>
   * This method should be called after a successful call to plug() with the same arguments.
   * It allows to remove a previously created association, e.g. in order to free resources.
   * </p>
   *
   * <p>Null Implementation:</p>
   * <pre>
   * return this;
   * </pre>
   *
   * @param collaborator complementary TokyoNaut
   * @param role implementation-specific role, identifying the type of collaboration involved. If the same instance has be plugged several times with different roles, it needs to be unplugged for each role to be freed.
   * @return current TokyoNaut to allow new combinations on the same line of code
   */
  public ITokyoNaut unplug(ITokyoNaut collaborator, Object role);
  
  /**
   * Map given path, storing locations found in given buffer<br/>
   *
   * <p>
   * Scouts for data items on selection "path", and stores their locations in "buffer".
   * To allow repeated calls of this method, the first items are ignored 
   * until the "from" position is reached, counting from 1 like XPath position().
   * Including "from", at most "length" items are then stored in "buffer".
   * The "buffer" can be filled in several steps, by setting the "offset" to
   * the starting position for the storage of new items. It corresponds to 
   * the sum of values returned in previous calls, since this method 
   * returns the actual number of items stored at each step.
   * ITokyoNaut implementations should at least support "/","*","@*" for "path", 
   * similarly to their XPath meaning in XML Documents.
   * </p>
   *
   * <p>Null Implementation:</p>
   * <pre>
   * return 0;
   * </pre>
   *
   * @param path path to follow
   * @param from 1-based position of first item to be taken into account on path
   * @param buffer buffer to store selected items
   * @param offset 0-based offset to start storing items in buffer
   * @param length number of items to be selected on path
   * @return the actual number of items stored in buffer
   */
  public int map(Object path, int from, Object[] buffer, int offset, int length);
  
  /**
   * Jump to first item found on given path and return updated self<br/>
   *
   * <p>
   * Jump to a well-suited position for reading the first item on given "path",
   * starting from here() excluded.
   * Return this TokyoNaut to allow further moves on the same line of code.
   * Sets current location to "nowhere" (here() returns null)
   * when this move is not supported or no data item is found.
   * ITokyoNaut implementations should at least support "/","*","@*" for "path", 
   * similarly to their XPath meaning in XML Documents.
   * </p>
   *
   * <p>Null Implementation:</p>
   * <pre>
   * return this;
   * </pre>
   *
   * @param path path to follow
   * @return TokyoNaut positionned on first item reached by following "path" from previous location.
   */
  public ITokyoNaut go(Object path);
  
  /**
   * Get current location<br/>
   *
   * <p>
   * Can be used for bookmarking, by storing the returned location and later 
   * calling go(location); however, in mutable data structures, there is
   * no warranty that the corresponding data item will be exactly the same, 
   * or even still exist.
   * Return null if previous call to go() led to "nowhere", as well as initially 
   * before any call to go().
   * In ITokyoNaut implementations, when here() is not null, calling here().toString() 
   * should return an absolute path, either in XPath in the context of a single document,
   * or in XPointer to also include current document's URI.
   * </p>
   * 
   * <p>Null Implementation:</p>
   * <pre>
   * return null;
   * </pre>
   *
   * @return current location
   */
  public Object here();
  
  /**
   * Get the value of the first item found on given path<br/>
   *
   * <p>
   * Return the value of the first item found on given "path".
   * Return null when this move is not supported or no data item is found.
   * ITokyoNaut implementations should at least support "." for "path",
   * in the context of simple data items with text values,
   * similarly to its XPath meaning in XML Documents.
   * </p>
   *
   * <p>Null Implementation:</p>
   * <pre>
   * return null;
   * </pre>
   *
   * @param path path to follow
   * @return value of first item found or null if this move is not supported or no data item is found
   */
  public Object get(Object path);
  
  /**
   * Put given item to the first position on given path<br/>
   *
   * <p>
   * Put given "item" at the first position on given "path", 
   * replacing any previous data item found there.
   * Returns null if this "path" is not supported or the data structure is immutable.
   * Can be used to delete data items by setting "item" to null.
   * Insertion before, between, or after existing data items may be supported by 
   * refining on the "path", e.g. inserting "+" or "-" after positions as in
   * "/Root/Element[3+]" or "/Root/Element[1-]".
   * ITokyoNaut implementations should only support the put() method when found relevant.
   * </p>
   *
   * <p>Null Implementation:</p>
   * <pre>
   * return null;
   * </pre>
   *
   * @param item new item to set
   * @param path path to follow
   * @return newly set item or null if this path is not supported or the data structure is immutable
   */
  public Object put(Object path, Object item);
  
}