/* ===============================================================
 The Tokyo Project is hosted on GitHub:
 https://github.com/eric-brechemier/Tokyo-Project
 
 Copyright (c) 2005-2008 Eric Bréchemier
 http://eric.brechemier.name
 Licensed under BSD License and/or MIT License.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                         BSD License
                             ~~~
             http://creativecommons.org/licenses/BSD/
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                          MIT License
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  Copyright (c) 2005-2007 Eric Bréchemier <tokyo@eric.brechemier.name>
  
  Permission is hereby granted, free of charge, to any person
  obtaining a copy of this software and associated documentation
  files (the "Software"), to deal in the Software without
  restriction, including without limitation the rights to use,
  copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the
  Software is furnished to do so, subject to the following
  conditions:

  The above copyright notice and this permission notice shall be
  included in all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
  OTHER DEALINGS IN THE SOFTWARE.
================================================================== */
package net.sf.tokyo;

/**
 * {@link net.sf.tokyo.ITokyoNaut} is the single interface making up Tokyo API, 
 * allowing both XML and non-XML data manipulation.<br/>
 *
 * <p>
 * The name "TokyoNaut" is composed of "Tokyo", a mind-changing city where this project started,
 * while "Naut" is a shorthand reference to Nautilus, Captain Nemo's submarine in Jules Verne's
 * "Twenty Thousand Leagues Under the Sea".
 * More inspiration comes from Captain Nemo's motto: "Mobilis in Mobili".
 * </p>
 *
 * <p>
 * {@link #areWeThereYet(ITokyoNaut[],int,int[]) areWeThereYet()} is to be called repeatedly until 
 * processing is complete (or time out), reporting metadata annotations (in ) in the result array 
 * <code>meta</code> provided as parameter, and corresponding raw binary data buffers by providing
 * the <code>data</code> buffer in the callback {@link #notYet(ITokyoNaut,byte[]) notYet()}.
 * </p>
 *
 * <p>
 * {@link net.sf.tokyo.ITokyoNaut TokyoNauts} are typically combined in chains of processing, described
 * in the <code>chain</code> parameter of the {@link #areWeThereYet(ITokyoNaut[],int,int[]) areWeThereYet()}
 * method, and meta/data is carried along this chain from source to destination until reaching the main loop.
 * This kind of associations is demonstrated in the prototypes and samples part of the Tokyo Project.
 * </p>
 *
 * @author Eric Br&eacute;chemier
 * @version NANA
 */
public interface ITokyoNaut
{
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // VERSION  
      /** meta[{@link #VERSION}] - the version of "Tokyo Project" in use (in order to check compatibility)
       * <p>
       *  <strong>Values:</strong>
       *  <ul>
       *    <li>[0x0000-0xFFFF]       reserved for current and future versions of the "Tokyo Project"</li>
       *    <li>[0x10000-0xFFFFFFFF]  available for use in your (let's say "user-defined") extension</li>
       *  </ul>
       * </p>
       * <p>
       *  <strong>Constant Values:</strong>
       *  <ul>
       *    <li>{@link #VERSION_NANA}</li>
       *  </ul>
       * </p>
       */
      public static final int VERSION = 0;
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // Constant Values:
    /** a value for meta[{@link #VERSION}] - current Version of the Tokyo Project */
      public static final int VERSION_NANA = 7;
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // LANGUAGE
      /** meta[{@link #LANGUAGE}] - the language or format specification providing the scope of the token id
       * <p>
       *  <strong>Values:</strong>
       *  <ul>
       *    <li>[0x0000-0xFFFF]       reserved     - for current and future versions of the "Tokyo Project"</li>
       *    <li>[0x10000-0xFA11EC]    user-defined - available for use in your extension</li>
       *    <li>[0xFA11ED]            reserved     - for current and future versions of the "Tokyo Project"</li>
       *    <li>[0xFA11EE-0xFFFFFFFF] user-defined - available for use in your extension</li>
       *  </ul>
       * </p>
       * <p>
       *  <strong>Constant Values:</strong>
       *  <ul>
       *    <li>{@link #LANGUAGE_BINARY}</li>
       *    <li>{@link #LANGUAGE_UNICODE_TEXT}</li>
       *    <li>{@link #LANGUAGE_ERROR}</li>
       *  </ul>
       * </p>
       */
      public static final int LANGUAGE = 1;
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // Constant Values:
      /** a value for meta[{@link #LANGUAGE}] - any Binary data without further semantic */
      public static final int LANGUAGE_BINARY       = 0;
      
      /** a value for meta[{@link #LANGUAGE}] - Unicode text with character code points as tokens */
      public static final int LANGUAGE_UNICODE_TEXT = 1;
      
      /** a value for meta[{@link #LANGUAGE}] - Error signalling with application error codes as tokens */
      public static final int LANGUAGE_ERROR        = 0xFA11ED;
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  
  
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // TOKEN
      /** meta[{@link #TOKEN}] - the token id, scoped by the language id 
       * <p>
       *  <strong>Values:</strong>
       *  <ul>
       *   <li>
       *    in meta[{@link #LANGUAGE}] = {@link #LANGUAGE_BINARY}
       *    <ul>
       *      <li>[0x0000-0xFFFF]       reserved     - for current and future versions of the "Tokyo Project"</li>
       *      <li>[0x10000-0xFFFFFFFF]  user-defined - available for use in your extension</li>
       *    </ul>
       *   </li>
       *   <li>
       *    in meta[{@link #LANGUAGE}] = {@link #LANGUAGE_UNICODE_TEXT}
       *    <ul>
       *      <li>[0x0000-0xFFFFFFFF]   reserved     - for current and future versions of Unicode code points</li>
       *    </ul>
       *   </li>
       *   <li>
       *    in meta[{@link #LANGUAGE}] = {@link #LANGUAGE_ERROR}
       *    <ul>
       *      <li>[0x0000-0xFFFFFFFF]   user-defined - application error code you can assign and use freely</li>
       *    </ul>
       *   </li>
       *   <li>
       *    in meta[{@link #LANGUAGE}] = [0x0002-0xFFFF]
       *    <ul>
       *      <li>[0x0000-0xFFFFFFFF]   reserved     - for current and future versions of the "Tokyo Project"</li>
       *    </ul>
       *   </li>
       *   <li>
       *    in meta[{@link #LANGUAGE}] = [0x10000-0xFA11EC] U [0xFA11EE-0xFFFFFFFF]
       *    <ul>
       *      <li>[0x0000-0xFFFFFFFF]   user-defined - available for use in your extension</li>
       *    </ul>
       *   </li>
       *  </ul>
       * </p>
       * <p>
       *  <strong>Constant Values:</strong>
       *  <ul>
       *    <li>{@link #TOKEN_BINARY}</li>
       *  </ul>
       * </p>
       */
      public static final int TOKEN = 2;
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // Constant Values:
      /** a value for meta[{@link #TOKEN}] - in {@link #LANGUAGE_BINARY}, a fragment of binary data carried in "data" buffer */
      public static final int TOKEN_BINARY          = 1;
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // LEFT
      /** meta[{@link #LEFT}] - the relation ({@linkplain #LEFT_START Start} or 
       *               {@linkplain #LEFT_CONTINUED Continued}) on the left (=start offset) of the fragment.
       *               {@linkplain #LEFT_CONTINUED Continued} is used for all but the last token in long 
       *               tokens reported as several data fragments.
       * <p>
       *  <strong>Values:</strong>
       *  <ul>
       *    <li>[0x0000-0xFFFFFFFF]   reserved - for current and future versions of the "Tokyo Project"</li>
       *  </ul>
       * </p>
       * <p>
       *  <strong>Constant Values:</strong>
       *  <ul>
       *    <li>{@link #LEFT_START}</li>
       *    <li>{@link #LEFT_CONTINUED}</li>
       *  </ul>
       * </p>
       */
      public static final int LEFT = 3;
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // Constant Values:
      /** a value for meta[{@link #LEFT}] - this data fragment is the first in the complete token */
      public static final int LEFT_START = 1;
      /** a value for meta[{@link #LEFT}] - this data fragment is a continuation of a started token */
      public static final int LEFT_CONTINUED = 0;
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  
  
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // RIGHT
      /** meta[{@link #RIGHT}] - the relation ({@linkplain #RIGHT_END End} or 
       *  {@linkplain #RIGHT_CONTINUED Continued}) on the right (=end offset, or start+length) of the 
       *  fragment. {@linkplain #RIGHT_CONTINUED Continued} is used for all but the first token in long 
       *  tokens reported as several data fragments.
       * <p>
       *  <strong>Values:</strong>
       *  <ul>
       *    <li>[0x0000-0xFFFFFFFF]   reserved - for current and future versions of the "Tokyo Project"</li>
       *  </ul>
       * </p>
       * <p>
       *  <strong>Constant Values:</strong>
       *  <ul>
       *    <li>{@link #RIGHT_END}</li>
       *    <li>{@link #RIGHT_CONTINUED}</li>
       *  </ul>
       * </p>
       */
      public static final int RIGHT = 6;
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // Constant Values:
      /** a value for meta[{@link #RIGHT}] - there are one or more continuation fragment to complete this token */
      public static final int RIGHT_CONTINUED = 0;
      /** a value for meta[{@link #RIGHT}] - this data fragment is the last in the complete token */
      public static final int RIGHT_END = -1;
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // OFFSET
      /** meta[{@link #OFFSET}] - the fragment start offset (in bytes) in accompanying "data" buffer
       * <p>
       *  <strong>Values:</strong>
       *  <ul>
       *    <li>[0x0000-0x7FFFFFFF]   range of allowed offset values (in bytes)</li>
       *  </ul>
       * </p>
       */
      public static final int OFFSET = 4;
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // Constant Values:
  //   None
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // LENGTH
      /** meta[{@link #LENGTH}] - the fragment length offset (in bytes) in accompanying "data" buffer
       * <p>
       *  <strong>Values:</strong>
       *  <ul>
       *    <li>[0x0000-0x7FFFFFFF]   range of allowed length values (in bytes)</li>
       *  </ul>
       * </p>
       */
      public static final int LENGTH = 5;
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  // Constant Values:
  //   None
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  
  
  /**
   * Get next event or "token" (that's a "pull" mode) with associated data "fragment".<br/>
   *
   * <p>
   * In case of long tokens running over several buffers of data, the token will be returned
   * in several parts, with the start and end signalled using <code>meta[{@link #LEFT}]={@link #LEFT_START}</code>
   * and <code>meta[{@link #RIGHT}]={@link #RIGHT_END}</code> respectively.
   * This method provides a simple means to control the reading process step by step.
   * The following loop will run until reading is complete or the step limit has been reached:
<pre>
int[] meta = new int[7];

int step = 0;
final int STEP_LIMIT = 99;

while(  (step++ < STEP_LIMIT)  &&  !destination.areWeThereYet(chain,chain.length-1,meta)  )
{
  if ( meta[ITokyoNaut.VERSION] == ITokyoNaut.VERSION_NANA)
  {
    System.out.println
      (  
         "Step: "+step+"\n\t"
        +"Language: 0x"+Integer.toHexString(meta[ITokyoNaut.LANGUAGE])+"\n\t"
        +"Token: 0x"+Integer.toHexString(meta[ITokyoNaut.TOKEN])+"\n\t"
        +"Left Relation: "+(meta[ITokyoNaut.LEFT]==1?"+":"")+meta[ITokyoNaut.LEFT]+"\n\t"
        +"Fragment Offset: "+meta[ITokyoNaut.OFFSET]+"\n\t"
        +"Fragment Length: "+meta[ITokyoNaut.LENGTH]+"\n\t"
        +"Right Relation: "+meta[ITokyoNaut.RIGHT]+"\n"
      );
  }
}
</pre>
   * </p>
   *
   * <p>
   * The code above, applied to a file containing 25 bytes of data with an appropriate destination
   * {@link net.sf.tokyo.ITokyoNaut TokyoNaut}, could for example produce the following output:
<pre>
Step: 1
   Language: 7
   Token: 1
   Left Relation: +1
   Fragment Offset: 0
   Fragment Length: 10
   Right Relation: 0
Step: 2
   Language: 7
   Token: 1
   Left Relation: 0
   Fragment Offset: 0
   Fragment Length: 10
   Right Relation: 0
Step: 3
   Language: 7
   Token: 1
   Left Relation: 0
   Fragment Offset: 0
   Fragment Length: 5
   Right Relation: -1
</pre>
   * Variants for the above output would include using less than the full data buffer, e.g. writing 
   * data only one byte at a time, or returning partial tokens with empty length, or even additional 
   * events in a foreign language not relevant to the processing.
   * </p>
   *
   * <p>
   * The above example illustrates that this specification provides the wire to get your data through, 
   * but you should definitely check the documentation of the {@link net.sf.tokyo.ITokyoNaut TokyoNauts} 
   * you are using to see what kind of events they expect and produce.
   * </p>
   *
   * <p>
   * {@link net.sf.tokyo.ITokyoNaut TokyoNauts} are associated in chains of processing, by putting them
   * into an array of ITokyoNauts, connecting them left-to-right in the order of source to destination,
   * provided to this method as param <code>chain</code>:
<pre>
ITokyoNaut[] chain = {source,filterOne,filterTwo,destination};
</pre>   
   * </p>
   *
   * <p>
   * Based on their position in the chain, {@link net.sf.tokyo.ITokyoNaut TokyoNauts} can actually 
   * play different roles:
   *   <ul>
   *     <li>
   *       <strong>Source</strong> - at the start of the chain, filling data buffer and 
   *       corresponding metadata from an external Input interface, e.g. reading a file.
   *     </li>
   *     <li>
   *       <strong>Filter</strong> - a regular {@link net.sf.tokyo.ITokyoNaut TokyoNaut} in the middle 
   *       of the chain, filtering received events and inserting new events (reported in <code>meta</code>)
   *       based on patterns identified, without any modification to the data buffer but cutting or merging
   *       of fragment parts for easier reporting (through the method {@link #notYet(ITokyoNaut,byte[]) notYet()}.
   *     </li>
   *     <li>
   *       <strong>Destination</strong> - the last {@link net.sf.tokyo.ITokyoNaut TokyoNaut} in the chain, 
   *       often writing received data to an external Output interface, e.g. a file or network,
   *       also the one in charge of reporting final events for the main loop.
   *     </li>
   *   </ul>
   * </p>
   *
   * <p>
   * Some {@link net.sf.tokyo.ITokyoNaut TokyoNauts} can actually combine several roles, for example:
   *   <ul>
   *     <li>
   *       <strong>Creative Filter</strong> - in the middle of the chain, but also creating 
   *       new data as a Source, based on previous events as a Filter.
   *     </li>
   *     <li>
   *       <strong>Side Effect Filter</strong> - in the middle of the chain, but producing
   *       side effects like a Destination while still reporting events as a Filter. Any Destination 
   *       can actually be seen as a Side Effect Filter, with the main loop playing the role of next 
   *       in chain. The main loop can actually be implemented as a TokyoNaut as well, inserted at the 
   *       end of the chain.
   *     </li>
   *     <li>
   *       <strong>Bridge</strong> - another composite connecting 
   *       a Destination {@link net.sf.tokyo.ITokyoNaut TokyoNaut}
   *       with a Source {@link net.sf.tokyo.ITokyoNaut TokyoNaut} 
   *       by implementing the expected I/O interfaces and forwarding 
   *       the data from the Output of the Destination {@link net.sf.tokyo.ITokyoNaut TokyoNaut} 
   *       to the Input of the Source {@link net.sf.tokyo.ITokyoNaut TokyoNaut}. 
   *       A common example of Bridge is an XSLT transformation, reading SAX from 
   *       a Destination Output and writing SAX for a Source Input thus bridging two chains of 
   *       processings into a single one.
   *     </li>
   *   </ul>
   * </p>
   *
   * @param chain a sequence of {@link net.sf.tokyo.ITokyoNaut TokyoNauts}, listed from source to
   *              destination based on position, combined to perform a chain of processings.
   *      <p>
   *        <strong>PRECONDITION:</strong> <code>chain</code> must not be null, its length must be 
   *        at least 1 since chain contains at least the current {@link net.sf.tokyo.ITokyoNaut TokyoNaut} 
   *        instance on which the {@link #areWeThereYet(ITokyoNaut[],int,int[]) areWeThereYet()} is called, and 
   *        each of its elements must not be null.
   *      </p>
   *
   * @param position the offset in the <code>chain</code> parameter, of the the current 
   *                 {@link net.sf.tokyo.ITokyoNaut TokyoNaut} instance on which the 
   *                 {@link #areWeThereYet(ITokyoNaut[],int,int[]) areWeThereYet()} method is called.
   *      <p>
   *        <strong>PRECONDITION:</strong> <code>position</code> must be inside the boundaries of the 
   *        <code>chain</code> array, and <code>this.equals( chain[position] )</code> must be true.
   *      </p>
   *
   * @param meta a result array carrying information about "data" sent separately by calling the callback
   *             method {@link #notYet(ITokyoNaut,byte[]) notYet()}.
   *       <p>
   *        The set of integer fields in meta is defined here, in the specification of the 
   *        {@link net.sf.tokyo.ITokyoNaut} interface corresponding to the leading version number 
   *        in the first field. In current version:
   *        <ul>
   *          <li><code>meta[{@link #VERSION}] = {@link #VERSION_NANA}</code></li>
   *          <li><code>meta[{@link #LANGUAGE}] = ( {@link #LANGUAGE_BINARY} | {@link #LANGUAGE_UNICODE_TEXT} | {@link #LANGUAGE_ERROR} | * )</code></li>
   *          <li><code>meta[{@link #TOKEN}] = ( {@link #TOKEN_BINARY} | *)</code></li>
   *          <li><code>meta[{@link #LEFT}] = ( {@link #LEFT_START} | {@link #LEFT_CONTINUED} )</code></li>
   *          <li><code>meta[{@link #OFFSET}] in [0;data.length[</code></li>
   *          <li><code>meta[{@link #LENGTH}] in [0;data.length]</code></li>
   *          <li><code>meta[{@link #RIGHT}] = ( {@link #RIGHT_END} | {@link #RIGHT_CONTINUED} )</code></li>
   *        </ul>
   *       </p>
   *       <p>
   *        <strong>PRECONDITION:</strong> <code>meta</code> must not be null, its length must be 7, 
   *        and its content conform to the above specification.
   *       </p>
   *
   * @return false until this {@link net.sf.tokyo.ITokyoNaut TokyoNaut} has reached the end of its 
   *         own processing (e.g. following the end of preceding {@link net.sf.tokyo.ITokyoNaut TokyoNauts} 
   *         providing input meta/data).
   *       <p>
   *         Before returning false, the {@link #notYet(ITokyoNaut,byte[]) notYet()} callback must be called
   *         to transmit the <code>data</code> buffer containing the fragment described in meta, unless
   *         a recoverable error is reported using <code>meta[{@link #LANGUAGE}] = {@link #LANGUAGE_ERROR}</code>;
   *         a reported error is considered recoverable when this method returns false, and fatal when it 
   *         returns true.
   *       </p>
   *       <p>
   *         The reason behind this mixed combination of result parameter for <code>meta</code> and 
   *         callback for <code>data</code> is based on their respective character and usage: while 
   *         <code>meta</code> is fixed-size and inspected by both {@link net.sf.tokyo.ITokyoNaut TokyoNauts}
   *         and non-TokyoNauts like the main loop, <code>data</code> varies in size and arrangement
   *         based on the position of tokens discovered, and the values it carries are for use by
   *         {@link net.sf.tokyo.ITokyoNaut TokyoNauts} only.
   *       </p>
   *       <p>
   *         <strong>Important:</strong> on the last turn, when the {@link net.sf.tokyo.ITokyoNaut TokyoNaut}
   *         completes a last token before the end of processing, {@link #areWeThereYet(ITokyoNaut[],int,int[]) areWeThereYet()}
   *         still returns false, and only returns true at the following run, which garantees that the result
   *         is always false when there is some relevant data. On the other hand, when the result is true,
   *         meta can be considered irrelevant and be discarded; for example, meta can optionally be checked 
   *         for error signalling based on <code>meta[{@link #LANGUAGE}] = {@link #LANGUAGE_ERROR}</code>
   *         usage.
   *       </p>
   *       <p>
   *         <strong>POSTCONDITION:</strong> If current {@link net.sf.tokyo.ITokyoNaut TokyoNaut} was
   *         not last in <code>chain</code> and no recoverable error was reported with 
   *         <code>meta[{@link #LANGUAGE}] = {@link #LANGUAGE_ERROR}</code>, it used <code>chain[position+1]</code> 
   *         as a destination to report the <code>data</code> buffer using {@link #notYet(ITokyoNaut,byte[]) notYet()} 
   *         callback; this callback must have been called once and only once before {@link #areWeThereYet(ITokyoNaut[],int,int[]) areWeThereYet()}
   *         returned false, and must not have been called before it returned true.
   *       </p>
   */
  public boolean areWeThereYet(ITokyoNaut[] chain, int position, int[] meta);
  
  /**
   * Callback method called once and only once by a preceding {@link net.sf.tokyo.ITokyoNaut TokyoNaut}
   * just before its {@link #areWeThereYet(ITokyoNaut[],int,int[]) areWeThereYet()} method returns false.<br/>
   *
   * <p>
   *   This method must never be called directly, but only in response to a call of 
   *   {@link #areWeThereYet(ITokyoNaut[],int,int[]) areWeThereYet()} ("Are We There Yet?"), 
   *   before returning false, which corresponds to the answer "Not Yet!".
   * </p>
   *
   * @param source preceding {@link net.sf.tokyo.ITokyoNaut TokyoNaut}, on which the 
   *               {@link #areWeThereYet(ITokyoNaut[],int,int[]) areWeThereYet()} method was called.
   *       <p>
   *        <strong>PRECONDITION:</strong> <code>source</code> must not be null.
   *       </p>
   *
   * @param data a binary buffer carrying data fragments corresponding to tokens (or parts of tokens) 
   *        described in the <code>meta</code> result parameter of {@link #areWeThereYet(ITokyoNaut[],int,int[]) areWeThereYet()}. 
   *       <p>
   *        Current fragment runs from <code>meta[{@link #OFFSET}]</code> to <code>meta[{@link #LENGTH}]</code>. 
   *        When the length is null, no data is available in current buffer.
   *       </p>
   *       <p>
   *        <strong>PRECONDITION:</strong> <code>data</code> may be empty but must not be null.
   *       </p>
   */
  public void notYet(ITokyoNaut source, byte[] data);
  
}
