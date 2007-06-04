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
 * Methods available() and read() are similar to same methods in InputStream, but the intention
 * is to get nodes one by one, with available() returning the number of bytes of current node,
 * and read() writing the bytes representing the node, starting with 0x00 to mark the node start
 * (Push on stack), one byte to identify the node type, all the bytes corresponding to the node 
 * content, including child nodes, and ending with 0xFF to mark the node end (Pop from stack).
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
   * Return the number of bytes available for writing.<br/>
   *
   * <p>
   * Consistent with InputStream.available(): 
   * "Returns the number of bytes that can be read (or skipped over) from this input stream 
   * without blocking by the next caller of a method for this input stream."
   * </p>
   *
   * @return the number of bytes that can be read without blocking, or null if none is available
   */
  public int available();
  
  /**
   * Return the number of bytes available for writing.<br/>
   *
   * <p>
   * Consistent with InputStream.read(byte[],int,int):
   * "Reads up to len bytes of data from the input stream into an array of bytes. An attempt 
   * is made to read as many as len bytes, but a smaller number may be read, possibly zero. 
   * The number of bytes actually read is returned as an integer. (...)"
   * </p>
   *
   * @param buffer the buffer into which the data is read.
   * @param offset the start offset in array b  at which the data is written.
   * @param length the maximum number of bytes to read.
   * @return the total number of bytes read into the buffer, or -1 if there is no more data because the end of the stream has been reached.
   */
  public int read(byte[] buffer, int offset, int length);
  
  /**
   * Plug a source from which data will be read.<br/>
   *
   * @param source TokyoNaut providing data to be read.
   * @return source parameter to allow a chaing of plug calls.
   */
  // 
  public ITokyoNaut plug(ITokyoNaut source);
  
  /**
   * Unplug source.<br/>
   *
   * <p>
   * This method unplug recursively all source TokyoNauts chained from this point.
   * If no source has been set by a previous call to plug(), nothing happens.
   * </p>
   *
   */
  public void unplug();
  
}