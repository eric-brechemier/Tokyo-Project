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
 * Each call of "morph" is intended to rewrite the input data by interpreting a set of instructions, 
 * one step at a time, going back and forth from data providers to data consumers. 
 * </p>
 * 
 * @author Eric Br&eacute;chemier
 * @version Harajuku
 */
public interface ITokyoNaut
{
  
  /**
   * Rewrite data based on atomic transformation actions (TokyoNauts).<br/>
   *
   * <p>
   * Each call of the "morph" method runs a data transformation by the selected action, 
   * reading bytes from an input queue, and writing bytes to an output queue
   * that will become the input queue of the following TokyoNaut.
   * </p>
   *
   * <p>
   * You can picture the data matrix as an abacus, and TokyoNauts as fingers moving bytes/beads
   * on the rows/rods to apply operations.
   * </p>
   * 
   * <p>
   * TokyoNaut instances are chained to realize a complex data transformation;
   * each tokyonaut is located in a separate row of the "action" array,
   * takes its input from the "data" queue in the previous row, and generates output
   * in the "data" queue in the row at the same level.
   * </p>
   *
   * <p>
   * More precisely, current TokyoNaut, the TokyoNaut instance currently called, takes data
   * as input in data[here[0]-1] and applies a transformation resulting in new data as output 
   * stored in data[here[0]]. Output data from one action is provided as input for the following 
   * transformation. <br />
   * First transformation will usually take input data from a file or a data input stream, while
   * last transformation will store output data in a file or a data output stream.
   * </p>
   *
   * <p>
   * "data" and "action" have the same number of elements. "here" is a single element array 
   * (int[1]) providing the index of current row as a read/write parameter: 
   * here[0] is the position of TokyoNaut instance on which the "morph" method is called.
   * </p>
   *
   * <p>
   * Each row of "data" is a queue where bytes are written by the TokyoNaut at the same index,
   * and read by the TokyoNaut at the upper index. <br />
   * The array starts with five bytes of header, followed by 250 bytes of payload.
   * In header, the first byte, data[here[0]][0] is set to '1' in this version, corresponding
   * to the conventions (with associated limitations such as the array size) described below.
   * Implementations wishing to use different conventions should set the first byte to a different
   * value; values in the range 0xF0 to 0xFF are reserved for private use and hence a good choice.
   * The second byte, data[here[0]][1] indicates the offset to the head of the queue (initially 
   * set to 5 by the data producer, and incremented by the data reader), and the third byte gives
   * the offset to the tail of the queue (one byte after the last byte of available data). <br />
   * Fourth and Fifth bytes can be used to store rollback values respectly for the head and tail of
   * the queue. These rollback values are practical to restore previous values when a short section 
   * of data does not fit at the end of the queue. <br />
   * </p>
   *
   * <p>
   * Using the above convention, queues cannot exceed 255 bytes of length, with 250 bytes 
   * of useful payload. No data is added to the queue before it has been emptied. <br />
   * When the queue is empty, head == tail. An empty queue is the last stage of a suite of morph
   * operations, and all TokyoNauts must write such an empty queue when their processing is complete
   * e.g. when input data has been processed completely and no more data is available in input. <br />
   * A processing associating several TokyoNauts will end with each TokyoNaut, from the first data
   * producer to the last data provider, will create an empty queue to be consumed by the following
   * TokyoNaut; and the last TokyoNaut of the chain shall return null upon receiving such a message.
   * </p>
   *
   * <p> 
   * "here" index is updated at the end of the morph method, either incremented when some output 
   * data remains to be consumed, or decremented to have new data written in the (now empty) input 
   * buffer. <br />
   * The returned TokyoNaut instance is the next to be called to go on the transformation process,
   * in both cases corresponding to action[here[0]]. <br />
   * It is null if the transformation is complete (in which case here[0] is set to -1).
   * </p>
   *
   * <p>
   * Given that "action" and "data" have been properly initialized, the following loop would run
   * a step by step transformation until completion:
<PRE>
int[] here = {0};
ITokyoNaut current = action[here[0]];
while (current != null)
{
  current = current.morph(action,data,here);
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
   * @param action chain of atomic transformations (TokyoNauts)
   * @param data queues of data (output of TokyoNaut on same row/input of TokyoNaut on next row)
   * @param here position of current TokyoNaut, identical in action and data
   * @return next TokyoNaut to run i.e. action[here[0]] or null when transformation is complete
   */
  public ITokyoNaut morph(ITokyoNaut[] action, byte[][] data, int[] here);
  
}