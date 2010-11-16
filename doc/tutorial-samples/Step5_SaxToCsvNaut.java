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
 *
 * Adapted from org.xml.sax.XMLReader, (XMLReader.java,v 1.1 2001/05/20 03:12:56 curcuru Exp) 
 * Written by David Megginson, sax@megginson.com
 * Note: NO WARRANTY!  This class is in the Public Domain.
 */
package net.sf.tokyo.prototype1;

import java.io.File;
import java.io.IOException;

import net.sf.tokyo.ITokyoNaut;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * SAX Content Handler for Custom Serializing
 */
public class Step5_SaxToCsvNaut
  implements ContentHandler,
             ITokyoNaut
{
  
  /**
   * Path of Output File, relative to the stylesheet path included in provided Locator.
   */
  public static final String RELATIVE_OUTPUT_FILE_PATH = "../../out/data/result.csvchain.csv";
  
  /**
   * Determine the origin of SAX document events.
   */
  protected String _outputFilePath;
  
  /**
   * Destination of Sax-like events.
   */
  protected ITokyoNaut _dataNaut;
  
  /**
   * Context Stack for Tokyo Nautilus
   */
  protected Object[] _context;
  
  /**
   * Context Stack Type for Tokyo Nautilus
   */
  protected int[] _contextType;
  
  // temporary storage for Context Args
  int[] _int1 = new int[1];
  int[] _int2 = new int[2];
  
  
  /**
   * Public constructor.
   */
  public Step5_SaxToCsvNaut()
  {
    _dataNaut = new Step5_CsvWriterNaut();
    _contextType = new int[Step5_Sax.SAX_MAX_STACK_LENGTH];
    _context = new Object[Step5_Sax.SAX_MAX_STACK_LENGTH];
    
    log("Custom Serializer Ready",1);
  }
  
  protected void initContextStack()
  {
    _contextType[Step5_Sax.LANGUAGE] = Step5_Sax.LANGUAGE;
    _context[Step5_Sax.LANGUAGE] = Step5_Sax.SAX_EVENTS_LANG;
    
    _contextType[Step5_Sax.DOCUMENT_OUTPUT_PATH] = Step5_Sax.DOCUMENT_OUTPUT_FULL_PATH;
    _context[Step5_Sax.DOCUMENT_OUTPUT_PATH] = _outputFilePath;
  } 
  
  protected void initOutputPath(Locator locator)
  {
    _outputFilePath = null;
    
    String systemId = locator.getSystemId();
    if (systemId == null)
      return;
      
    final String FILE_PREFIX = "file:";
    // Remove starting "file:"
    String path = systemId.substring(FILE_PREFIX.length());
      
    String parentPath = new File(path).getParent();
    if (parentPath == null)
      return;
    
    File outputFile = new File(parentPath,RELATIVE_OUTPUT_FILE_PATH);
    try
    {
      _outputFilePath = outputFile.getCanonicalPath();
    }
    catch(IOException e)
    {
      log("Could not find path. "+e,5);
    }
  }
  
  ////////////////////////////////////////////////////////////////////
  // Logging.
  ////////////////////////////////////////////////////////////////////
  
  protected static final int LOG_LEVEL_LIMIT = 0;
  
  protected void log(String message, int level)
  {
    if ( level > LOG_LEVEL_LIMIT )
      System.out.println("SaxToCsvNaut::"+message);
  }
  
  
  // ITokyoNaut Implementation
  /* Released under LGPL
   * Copyright (c) 2005-2006 Eric Bréchemier
   * http://eric.brechemier.name
   */
  
  /**
   * Every data processing starts with such a meeting between two TokyoNaut instances.<br/>
   *
   * <p>
   * The stranger instance is to propose a language stored in context[language], with
   * type[language] characterizing the language Object using a shared convention. 
   * The hearer TokyoNaut may either say something using the language, or keep silent 
   * and quit, using the same context, type and language as arguments.
   * </p>
   * <p>
   *  <b>Note:</b> TokyoNaut API makes extensive use of the three parameters <br/>
   *  Object[] context + int[] type + int someObjectPosition. <br/>
   *  The combination of context and type can be seen as a stack of named arguments,
   *  while the last integer parameter points to the top of the stack. 
   *  The rule is rather to keep the same arrays for the duration of the conversation,
   *  even if bigger arrays may be introduced as required if shared conventions associated 
   *  with the current language allow it. Besides, the two arrays should have the same size.  
   * </p>
   *
   * @param stranger TokyoNaut instance coming to start a conversation
   * @param context stack of questions and answers
   * @param type array of codes identifying types of objects at the same position in context array
   * @param language position of language object in context array
   */
  public void meet(ITokyoNaut stranger, Object[] context, int[] type, int language)
  {
    log("meet("+stranger+","+context+"=>"+context[language]+","+type+"=>"+type[language]+","+language+")",3);
  
    
  }
  
  /**
   * At the end of all discussions, TokyoNauts part company.<br/>
   *
   * <p>
   * This is the occasion
   * to free contextual ressources related to the task. Any TokyoNaut may end the discussion,
   * not necessarily the one having started it by calling the meet method.
   * context[language] and type[language] will usually be identical to the values
   * provided in the initial meet method.
   * </p>
   *
   * @param friend TokyoNaut instance wishing to quit the conversation
   * @param context stack of questions and answers
   * @param type array of codes identifying types of objects at the same position in context array
   * @param language position of language object in context array
   */
  public void quit(ITokyoNaut friend, Object[] context, int[] type, int language)
  {
    log("quit("+friend+","+context+"=>"+context[language]+","+type+"=>"+type[language]+","+language+")",3);
    
    try
    {
    
      if ( 
              (friend==_dataNaut) 
           && (type[language]==Step5_Sax.LANGUAGE) 
           && ( Step5_Sax.SAX_EVENTS_LANG.equals(context[language]) )
         )
      {
        _dataNaut = null;
        _context = null;
        _contextType = null;
        _outputFilePath = null;
      }
      
    }
    catch(Exception e)
    {
      System.err.println("Unexpected exception: "+e);
      e.printStackTrace();
    }
  }
  
  /**
   * TokyoNauts instances communicate by calling alternatively this "say" method.<br/>
   *
   * <p>
   * The speaker TokyoNaut says context[sentence], characterized by type[sentence], 
   * assuming it is relevant in the context. 
   * Each TokyoNaut is free to update the context stack to keep only the most relevant items. 
   * For hierarchical data processing, the context may for example contain the stack of
   * ancestors on the path from the root to the current node.
   * </p>
   *
   * @param speaker TokyoNaut instance uttering the sentence
   * @param context stack of questions and answers
   * @param type array of codes identifying types of objects at the same position in context array
   * @param sentence position of sentence object in context array
   */
  public void say(ITokyoNaut speaker, Object[] context, int[] type, int sentence)
  {
    log("say("+speaker+","+context+"=>"+context[sentence]+","+type+"=>"+type[sentence]+","+sentence+")",3);
    
    
  }
  
  /**
   * When tired of speaking, TokyoNauts can enjoy a little distraction...<br/>
   *
   * <p>
   * ... by playing
   * a quick game, a piece of music or even theater, proposed as context[play]. 
   * Serious TokyoNauts can freely skip the implementation of this method.
   * </p>
   *
   * @param player TokyoNaut instance communicating through play
   * @param context stack of questions and answers and various plays
   * @param type array of codes identifying types of objects at the same position in context array
   * @param play position of play object in context array
   */ 
  public void play(ITokyoNaut player, Object[] context, int[] type, int play)
  {
    log("play("+player+","+context+"=>"+context[play]+","+type+"=>"+type[play]+","+play+")",3);
  }
  
  /**
   * No need to panic, says Japan coach.<br/>
   *
   * <p>
   * Gestures provide alternative communication, especially useful for logs, 
   * assertions and errors.
   * </p>
   *
   * @param shower TokyoNaut instance communicating through gesture
   * @param towel communicated thing
   */ 
  public void show(ITokyoNaut shower, Object towel)
  {
    log("show("+shower+","+towel+")",3);
  }
  
  // End of ITokyoNaut Implementation
  
  
  
  // ContentHandler.java - handle main document content.
  // http://www.saxproject.org
  // Written by David Megginson
  // NO WARRANTY!  This class is in the public domain.
  // $Id: ContentHandler.java,v 1.1.24.1 2004/05/01 08:34:39 jsuttor Exp $

  /**
   * Receive an object for locating the origin of SAX document events.
   *
   * <p>SAX parsers are strongly encouraged (though not absolutely
   * required) to supply a locator: if it does so, it must supply
   * the locator to the application by invoking this method before
   * invoking any of the other methods in the ContentHandler
   * interface.</p>
   *
   * <p>The locator allows the application to determine the end
   * position of any document-related event, even if the parser is
   * not reporting an error.  Typically, the application will
   * use this information for reporting its own errors (such as
   * character content that does not match an application's
   * business rules).  The information returned by the locator
   * is probably not sufficient for use with a search engine.</p>
   *
   * <p>Note that the locator will return correct information only
   * during the invocation SAX event callbacks after
   * {@link #startDocument startDocument} returns and before
   * {@link #endDocument endDocument} is called.  The
   * application should not attempt to use it at any other time.</p>
   *
   * @param locator an object that can return the location of
   *                any SAX document event
   * @see org.xml.sax.Locator
   */
  public void setDocumentLocator(Locator locator)
  {
    log("setDocumentLocator("+locator+")",4);
    log("SystemId: "+locator.getSystemId(),5);
    
    initOutputPath(locator);
  }


  /**
   * Receive notification of the beginning of a document.
   *
   * <p>The SAX parser will invoke this method only once, before any
   * other event callbacks (except for {@link #setDocumentLocator 
   * setDocumentLocator}).</p>
   *
   * @throws org.xml.sax.SAXException any SAX exception, possibly
   *            wrapping another exception
   * @see #endDocument
   */
  public void startDocument()
	  throws SAXException
  {
    log("startDocument()",4);
    
    initContextStack();
    _dataNaut.meet(this,_context,_contextType,Step5_Sax.LANGUAGE);
    
    if (_outputFilePath == null)
    {
      _dataNaut.show(this,"No Locator available");
    }
    
    _contextType[Step5_Sax.EVENT] = Step5_Sax.CONTENT_START_DOCUMENT;
    _int1[0] = 0;
    _context[Step5_Sax.EVENT] = _int1;
    _dataNaut.say(this,_context,_contextType,Step5_Sax.EVENT);
  }


  /**
   * Receive notification of the end of a document.
   *
   * <p><strong>There is an apparent contradiction between the
   * documentation for this method and the documentation for {@link
   * org.xml.sax.ErrorHandler#fatalError}.  Until this ambiguity is
   * resolved in a future major release, clients should make no
   * assumptions about whether endDocument() will or will not be
   * invoked when the parser has reported a fatalError() or thrown
   * an exception.</strong></p>
   *
   * <p>The SAX parser will invoke this method only once, and it will
   * be the last method invoked during the parse.  The parser shall
   * not invoke this method until it has either abandoned parsing
   * (because of an unrecoverable error) or reached the end of
   * input.</p>
   *
   * @throws org.xml.sax.SAXException any SAX exception, possibly
   *            wrapping another exception
   * @see #startDocument
   */
  public void endDocument()
	  throws SAXException
  {
    log("endDocument()",4);
    
    initContextStack();
    
    _contextType[Step5_Sax.EVENT] = Step5_Sax.CONTENT_END_DOCUMENT;
    _int1[0] = 0;
    _context[Step5_Sax.EVENT] = _int1;
    _dataNaut.say(this,_context,_contextType,Step5_Sax.EVENT);
  }

  /**
   * Begin the scope of a prefix-URI Namespace mapping.
   *
   * <p>The information from this event is not necessary for
   * normal Namespace processing: the SAX XML reader will 
   * automatically replace prefixes for element and attribute
   * names when the <code>http://xml.org/sax/features/namespaces</code>
   * feature is <var>true</var> (the default).</p>
   *
   * <p>There are cases, however, when applications need to
   * use prefixes in character data or in attribute values,
   * where they cannot safely be expanded automatically; the
   * start/endPrefixMapping event supplies the information
   * to the application to expand prefixes in those contexts
   * itself, if necessary.</p>
   *
   * <p>Note that start/endPrefixMapping events are not
   * guaranteed to be properly nested relative to each other:
   * all startPrefixMapping events will occur immediately before the
   * corresponding {@link #startElement startElement} event, 
   * and all {@link #endPrefixMapping endPrefixMapping}
   * events will occur immediately after the corresponding
   * {@link #endElement endElement} event,
   * but their order is not otherwise 
   * guaranteed.</p>
   *
   * <p>There should never be start/endPrefixMapping events for the
   * "xml" prefix, since it is predeclared and immutable.</p>
   *
   * @param prefix the Namespace prefix being declared.
   *	An empty string is used for the default element namespace,
   *	which has no prefix.
   * @param uri the Namespace URI the prefix is mapped to
   * @throws org.xml.sax.SAXException the client may throw
   *            an exception during processing
   * @see #endPrefixMapping
   * @see #startElement
   */
  public void startPrefixMapping(String prefix, String uri)
	  throws SAXException
  {
    log("startPrefixMapping("+prefix+","+uri+")",4);
    
    initContextStack();
    
    _contextType[Step5_Sax.EVENT] = Step5_Sax.CONTENT_START_PREFIX_MAPPING;
    _int1[0] = 2;
    _context[Step5_Sax.EVENT] = _int1;
    _contextType[Step5_Sax.ARG1] = Step5_Sax.ARG_NAMESPACE_PREFIX;
    _context[Step5_Sax.ARG1] = prefix;
    _contextType[Step5_Sax.ARG2] = Step5_Sax.ARG_NAMESPACE_URI;
    _context[Step5_Sax.ARG2] = uri;
    _dataNaut.say(this,_context,_contextType,Step5_Sax.EVENT);
  }

  /**
   * End the scope of a prefix-URI mapping.
   *
   * <p>See {@link #startPrefixMapping startPrefixMapping} for 
   * details.  These events will always occur immediately after the
   * corresponding {@link #endElement endElement} event, but the order of 
   * {@link #endPrefixMapping endPrefixMapping} events is not otherwise
   * guaranteed.</p>
   *
   * @param prefix the prefix that was being mapped.
   *	This is the empty string when a default mapping scope ends.
   * @throws org.xml.sax.SAXException the client may throw
   *            an exception during processing
   * @see #startPrefixMapping
   * @see #endElement
   */
  public void endPrefixMapping(String prefix)
	  throws SAXException
  {
    log("endPrefixMapping("+prefix+")",4);
    
    initContextStack();
    
    _contextType[Step5_Sax.EVENT] = Step5_Sax.CONTENT_END_PREFIX_MAPPING;
    _int1[0] = 1;
    _context[Step5_Sax.EVENT] = _int1;
    _contextType[Step5_Sax.ARG1] = Step5_Sax.ARG_NAMESPACE_PREFIX;
    _context[Step5_Sax.ARG1] = prefix;
    _dataNaut.say(this,_context,_contextType,Step5_Sax.EVENT);
  }
  
  
  /**
   * Receive notification of the beginning of an element.
   *
   * <p>The Parser will invoke this method at the beginning of every
   * element in the XML document; there will be a corresponding
   * {@link #endElement endElement} event for every startElement event
   * (even when the element is empty). All of the element's content will be
   * reported, in order, before the corresponding endElement
   * event.</p>
   *
   * <p>This event allows up to three name components for each
   * element:</p>
   *
   * <ol>
   * <li>the Namespace URI;</li>
   * <li>the local name; and</li>
   * <li>the qualified (prefixed) name.</li>
   * </ol>
   *
   * <p>Any or all of these may be provided, depending on the
   * values of the <var>http://xml.org/sax/features/namespaces</var>
   * and the <var>http://xml.org/sax/features/namespace-prefixes</var>
   * properties:</p>
   *
   * <ul>
   * <li>the Namespace URI and local name are required when 
   * the namespaces property is <var>true</var> (the default), and are
   * optional when the namespaces property is <var>false</var> (if one is
   * specified, both must be);</li>
   * <li>the qualified name is required when the namespace-prefixes property
   * is <var>true</var>, and is optional when the namespace-prefixes property
   * is <var>false</var> (the default).</li>
   * </ul>
   *
   * <p>Note that the attribute list provided will contain only
   * attributes with explicit values (specified or defaulted):
   * #IMPLIED attributes will be omitted.  The attribute list
   * will contain attributes used for Namespace declarations
   * (xmlns* attributes) only if the
   * <code>http://xml.org/sax/features/namespace-prefixes</code>
   * property is true (it is false by default, and support for a 
   * true value is optional).</p>
   *
   * <p>Like {@link #characters characters()}, attribute values may have
   * characters that need more than one <code>char</code> value.  </p>
   *
   * @param uri the Namespace URI, or the empty string if the
   *        element has no Namespace URI or if Namespace
   *        processing is not being performed
   * @param localName the local name (without prefix), or the
   *        empty string if Namespace processing is not being
   *        performed
   * @param qName the qualified name (with prefix), or the
   *        empty string if qualified names are not available
   * @param atts the attributes attached to the element.  If
   *        there are no attributes, it shall be an empty
   *        Attributes object.  The value of this object after
   *        startElement returns is undefined
   * @throws org.xml.sax.SAXException any SAX exception, possibly
   *            wrapping another exception
   * @see #endElement
   * @see org.xml.sax.Attributes
   * @see org.xml.sax.helpers.AttributesImpl
   */
  public void startElement(String uri, String localName, String qName, Attributes atts)
	  throws SAXException
  {
    log("startElement("+uri+","+localName+","+qName+","+atts+")",4);
    
    initContextStack();
    
    _contextType[Step5_Sax.EVENT] = Step5_Sax.CONTENT_START_ELEMENT;
    _int1[0] = 4;
    _context[Step5_Sax.EVENT] = _int1;
    _contextType[Step5_Sax.ARG1] = Step5_Sax.ARG_NAMESPACE_URI;
    _context[Step5_Sax.ARG1] = uri;
    _contextType[Step5_Sax.ARG2] = Step5_Sax.ARG_LOCAL_NAME;
    _context[Step5_Sax.ARG2] = localName;
    _contextType[Step5_Sax.ARG3] = Step5_Sax.ARG_QNAME;
    _context[Step5_Sax.ARG3] = qName;
    _contextType[Step5_Sax.ARG4] = Step5_Sax.ARG_ATTRIBUTES;
    _context[Step5_Sax.ARG4] = atts;
    _dataNaut.say(this,_context,_contextType,Step5_Sax.EVENT);
  }


  /**
   * Receive notification of the end of an element.
   *
   * <p>The SAX parser will invoke this method at the end of every
   * element in the XML document; there will be a corresponding
   * {@link #startElement startElement} event for every endElement 
   * event (even when the element is empty).</p>
   *
   * <p>For information on the names, see startElement.</p>
   *
   * @param uri the Namespace URI, or the empty string if the
   *        element has no Namespace URI or if Namespace
   *        processing is not being performed
   * @param localName the local name (without prefix), or the
   *        empty string if Namespace processing is not being
   *        performed
   * @param qName the qualified XML name (with prefix), or the
   *        empty string if qualified names are not available
   * @throws org.xml.sax.SAXException any SAX exception, possibly
   *            wrapping another exception
   */
  public void endElement(String uri, String localName,String qName)
    throws SAXException
  {
    log("endElement("+uri+","+localName+","+qName+")",4);
    
    initContextStack();
    
    _contextType[Step5_Sax.EVENT] = Step5_Sax.CONTENT_END_ELEMENT;
    _int1[0] = 3;
    _context[Step5_Sax.EVENT] = _int1;
    _contextType[Step5_Sax.ARG1] = Step5_Sax.ARG_NAMESPACE_URI;
    _context[Step5_Sax.ARG1] = uri;
    _contextType[Step5_Sax.ARG2] = Step5_Sax.ARG_LOCAL_NAME;
    _context[Step5_Sax.ARG2] = localName;
    _contextType[Step5_Sax.ARG3] = Step5_Sax.ARG_QNAME;
    _context[Step5_Sax.ARG3] = qName;
    _dataNaut.say(this,_context,_contextType,Step5_Sax.EVENT);
  }

  /**
   * Receive notification of character data.
   *
   * <p>The Parser will call this method to report each chunk of
   * character data.  SAX parsers may return all contiguous character
   * data in a single chunk, or they may split it into several
   * chunks; however, all of the characters in any single event
   * must come from the same external entity so that the Locator
   * provides useful information.</p>
   *
   * <p>The application must not attempt to read from the array
   * outside of the specified range.</p>
   *
   * <p>Individual characters may consist of more than one Java
   * <code>char</code> value.  There are two important cases where this
   * happens, because characters can't be represented in just sixteen bits.
   * In one case, characters are represented in a <em>Surrogate Pair</em>,
   * using two special Unicode values. Such characters are in the so-called
   * "Astral Planes", with a code point above U+FFFF.  A second case involves
   * composite characters, such as a base character combining with one or
   * more accent characters. </p>
   *
   * <p> Your code should not assume that algorithms using
   * <code>char</code>-at-a-time idioms will be working in character
   * units; in some cases they will split characters.  This is relevant
   * wherever XML permits arbitrary characters, such as attribute values,
   * processing instruction data, and comments as well as in data reported
   * from this method.  It's also generally relevant whenever Java code
   * manipulates internationalized text; the issue isn't unique to XML.</p>
   *
   * <p>Note that some parsers will report whitespace in element
   * content using the {@link #ignorableWhitespace ignorableWhitespace}
   * method rather than this one (validating parsers <em>must</em> 
   * do so).</p>
   *
   * @param ch the characters from the XML document
   * @param start the start position in the array
   * @param length the number of characters to read from the array
   * @throws org.xml.sax.SAXException any SAX exception, possibly
   *            wrapping another exception
   * @see #ignorableWhitespace 
   * @see org.xml.sax.Locator
   */
  public void characters(char ch[], int start, int length)
    throws SAXException
  {
    log("characters("+ch+","+start+","+length+")",4);
    
    initContextStack();
    
    _contextType[Step5_Sax.EVENT] = Step5_Sax.CONTENT_CHARACTERS;
    _int1[0] = 2;
    _context[Step5_Sax.EVENT] = _int1;
    _contextType[Step5_Sax.ARG1] = Step5_Sax.ARG_CHARACTERS;
    _context[Step5_Sax.ARG1] = ch;
    _contextType[Step5_Sax.ARG2] = Step5_Sax.ARG_START_LENGTH;
    _int2[0] = start;
    _int2[1] = length;
    _context[Step5_Sax.ARG2] = _int2;
    _dataNaut.say(this,_context,_contextType,Step5_Sax.EVENT);
  }


  /**
   * Receive notification of ignorable whitespace in element content.
   *
   * <p>Validating Parsers must use this method to report each chunk
   * of whitespace in element content (see the W3C XML 1.0
   * recommendation, section 2.10): non-validating parsers may also
   * use this method if they are capable of parsing and using
   * content models.</p>
   *
   * <p>SAX parsers may return all contiguous whitespace in a single
   * chunk, or they may split it into several chunks; however, all of
   * the characters in any single event must come from the same
   * external entity, so that the Locator provides useful
   * information.</p>
   *
   * <p>The application must not attempt to read from the array
   * outside of the specified range.</p>
   *
   * @param ch the characters from the XML document
   * @param start the start position in the array
   * @param length the number of characters to read from the array
   * @throws org.xml.sax.SAXException any SAX exception, possibly
   *            wrapping another exception
   * @see #characters
   */
  public void ignorableWhitespace(char ch[], int start, int length)
	  throws SAXException
  {
    log("ignorableWhitespace("+ch+","+start+","+length+")",4);
    
    _contextType[Step5_Sax.EVENT] = Step5_Sax.CONTENT_IGNORABLE_WHITE_SPACE;
    _int1[0] = 2;
    _context[Step5_Sax.EVENT] = _int1;
    _contextType[Step5_Sax.ARG1] = Step5_Sax.ARG_CHARACTERS;
    _context[Step5_Sax.ARG1] = ch;
    _contextType[Step5_Sax.ARG2] = Step5_Sax.ARG_START_LENGTH;
    _int2[0] = start;
    _int2[1] = length;
    _context[Step5_Sax.ARG2] = _int2;
    _dataNaut.say(this,_context,_contextType,Step5_Sax.EVENT);
  }


  /**
   * Receive notification of a processing instruction.
   *
   * <p>The Parser will invoke this method once for each processing
   * instruction found: note that processing instructions may occur
   * before or after the main document element.</p>
   *
   * <p>A SAX parser must never report an XML declaration (XML 1.0,
   * section 2.8) or a text declaration (XML 1.0, section 4.3.1)
   * using this method.</p>
   *
   * <p>Like {@link #characters characters()}, processing instruction
   * data may have characters that need more than one <code>char</code>
   * value. </p>
   *
   * @param target the processing instruction target
   * @param data the processing instruction data, or null if
   *        none was supplied.  The data does not include any
   *        whitespace separating it from the target
   * @throws org.xml.sax.SAXException any SAX exception, possibly
   *            wrapping another exception
   */
  public void processingInstruction(String target, String data)
	  throws SAXException
  {
    log("processingInstruction("+target+","+data+")",4);
    
    initContextStack();
    
    _contextType[Step5_Sax.EVENT] = Step5_Sax.CONTENT_PROCESSING_INSTRUCTION;
    _int1[0] = 2;
    _context[Step5_Sax.EVENT] = _int1;
    _contextType[Step5_Sax.ARG1] = Step5_Sax.ARG_TARGET;
    _context[Step5_Sax.ARG1] = target;
    _contextType[Step5_Sax.ARG2] = Step5_Sax.ARG_DATA;
    _context[Step5_Sax.ARG2] = data;
    _dataNaut.say(this,_context,_contextType,Step5_Sax.EVENT);
  }


  /**
   * Receive notification of a skipped entity.
   * This is not called for entity references within markup constructs
   * such as element start tags or markup declarations.  (The XML
   * recommendation requires reporting skipped external entities.
   * SAX also reports internal entity expansion/non-expansion, except
   * within markup constructs.)
   *
   * <p>The Parser will invoke this method each time the entity is
   * skipped.  Non-validating processors may skip entities if they
   * have not seen the declarations (because, for example, the
   * entity was declared in an external DTD subset).  All processors
   * may skip external entities, depending on the values of the
   * <code>http://xml.org/sax/features/external-general-entities</code>
   * and the
   * <code>http://xml.org/sax/features/external-parameter-entities</code>
   * properties.</p>
   *
   * @param name the name of the skipped entity.  If it is a 
   *        parameter entity, the name will begin with '%', and if
   *        it is the external DTD subset, it will be the string
   *        "[dtd]"
   * @throws org.xml.sax.SAXException any SAX exception, possibly
   *            wrapping another exception
   */
  public void skippedEntity(String name)
    throws SAXException
  {
    log("skippedEntity("+name+")",4);
    
    initContextStack();
    
    _contextType[Step5_Sax.EVENT] = Step5_Sax.CONTENT_SKIPPED_ENTITY;
    _int1[0] = 1;
    _context[Step5_Sax.EVENT] = _int1;
    _contextType[Step5_Sax.ARG1] = Step5_Sax.ARG_ENTITY_NAME;
    _context[Step5_Sax.ARG1] = name;
    _dataNaut.say(this,_context,_contextType,Step5_Sax.EVENT);
  }
    
  // end of ContentHandler.java  
  
}
// end of XMLReader.java