/*
 * The Tokyo Project is hosted on GitHub:
 * https://github.com/eric-brechemier/Tokyo-Project
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

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

/**
 * SAX Filter - Very similar to org.xml.sax.helpers.XMLFilterImpl,
 * with addtional logging of all events and LexicalHandler filtering.
 *
 * <p>XMLReader is the interface that an XML parser's SAX2 driver must
 * implement.  This interface allows an application to set and
 * query features and properties in the parser, to register
 * event handlers for document processing, and to initiate
 * a document parse.</p>
 *
 * <p>All SAX interfaces are assumed to be synchronous: the
 * {@link #parse parse} methods must not return until parsing
 * is complete, and readers must wait for an event-handler callback
 * to return before reporting the next event.</p>
 *
 * <p>This interface replaces the (now deprecated) SAX 1.0 {@link
 * org.xml.sax.Parser Parser} interface.  The XMLReader interface
 * contains two important enhancements over the old Parser
 * interface:</p>
 *
 * <ol>
 * <li>it adds a standard way to query and set features and 
 *  properties; and</li>
 * <li>it adds Namespace support, which is required for many
 *  higher-level XML standards.</li>
 * </ol>
 *
 * <p>There are adapters available to convert a SAX1 Parser to
 * a SAX2 XMLReader and vice-versa.</p>
 *
 * @see org.xml.sax.XMLFilter
 * @see org.xml.sax.helpers.ParserAdapter
 * @see org.xml.sax.helpers.XMLReaderAdapter 
 */
public class Step3_SaxFilter
  implements XMLReader, ErrorHandler,ContentHandler,Locator,DTDHandler,LexicalHandler
{
  
  // Internal State for Filtering of Callback events
  
  /** Internal Error Handler. */
  protected ErrorHandler _errorHandler;
  /** Internal Content Locator. */
  protected Locator _locator;
  /** Internal Content Handler. */
  protected ContentHandler _contentHandler;
  /** Internal DTD Handler. */
  protected DTDHandler _dtdHandler;
  /** Internal Lexical Handler. */
  protected LexicalHandler _lexicalHandler;
  /** Standard URI of LexicalHandler property. */
  protected final String LEXICAL_HANDLER_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
  
  /**
   * Internal SAX Parser.
   *
   * <p>Default SAX Parser, used to delegate all calls to SAX API 
   * (SAX Filter).</p>
   *
   */
  protected XMLReader _parser;
  
  /**
   * Public constructor.
   *
   * <p>
   * Initializes the default SAX Parser of the platform, in order to
   * delegate all calls to it, playing a role of SAX Filter.
   * </p>
   */
  public Step3_SaxFilter()
  {
    try
    {
      _parser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
      log("XMLReader Parser loaded: "+_parser,1);
    } catch (ParserConfigurationException err) {
      throw new RuntimeException(err);
    } catch (SAXException err) {
      throw new RuntimeException(err);
    }
  }
  
  
  
  
  ////////////////////////////////////////////////////////////////////
  // Logging.
  ////////////////////////////////////////////////////////////////////
  
  protected static final int LOG_LEVEL_LIMIT = 0;
  
  protected void log(String message, int level)
  {
    if ( level > LOG_LEVEL_LIMIT )
      System.out.println("SaxFilter::"+message);
  }
  
  
  
  // XMLReader.java - read an XML document.
  // http://www.saxproject.org
  // Written by David Megginson
  // NO WARRANTY!  This class is in the Public Domain.
  // $Id: XMLReader.java,v 1.2.22.1 2004/05/01 08:34:40 jsuttor Exp $
  
  ////////////////////////////////////////////////////////////////////
  // Configuration.
  ////////////////////////////////////////////////////////////////////
  
  /**
   * Look up the value of a feature.
   *
   * <p>The feature name is any fully-qualified URI.  It is
   * possible for an XMLReader to recognize a feature name but
   * to be unable to return its value; this is especially true
   * in the case of an adapter for a SAX1 Parser, which has
   * no way of knowing whether the underlying parser is
   * performing validation or expanding external entities.</p>
   *
   * <p>All XMLReaders are required to recognize the
   * http://xml.org/sax/features/namespaces and the
   * http://xml.org/sax/features/namespace-prefixes feature names.</p>
   *
   * <p>Some feature values may be available only in specific
   * contexts, such as before, during, or after a parse.</p>
   *
   * <p>Typical usage is something like this:</p>
   *
   * <pre>
   * XMLReader r = new MySAXDriver();
   *
   *                         // try to activate validation
   * try {
   *   r.setFeature("http://xml.org/sax/features/validation", true);
   * } catch (SAXException e) {
   *   System.err.println("Cannot activate validation."); 
   * }
   *
   *                         // register event handlers
   * r.setContentHandler(new MyContentHandler());
   * r.setErrorHandler(new MyErrorHandler());
   *
   *                         // parse the first document
   * try {
   *   r.parse("http://www.foo.com/mydoc.xml");
   * } catch (IOException e) {
   *   System.err.println("I/O exception reading XML document");
   * } catch (SAXException e) {
   *   System.err.println("XML exception reading document.");
   * }
   * </pre>
   *
   * <p>Implementors are free (and encouraged) to invent their own features,
   * using names built on their own URIs.</p>
   *
   * @param name The feature name, which is a fully-qualified URI.
   * @return The current state of the feature (true or false).
   * @exception org.xml.sax.SAXNotRecognizedException When the
   *            XMLReader does not recognize the feature name.
   * @exception org.xml.sax.SAXNotSupportedException When the
   *            XMLReader recognizes the feature name but 
   *            cannot determine its value at this time.
   * @see #setFeature
   */
  public boolean getFeature(String name)
      throws SAXNotRecognizedException, SAXNotSupportedException
  {
    boolean result = _parser.getFeature(name);
    log("getFeature("+name+") : "+result,2);
    return result;
  }


  /**
   * Set the state of a feature.
   *
   * <p>The feature name is any fully-qualified URI.  It is
   * possible for an XMLReader to recognize a feature name but
   * to be unable to set its value; this is especially true
   * in the case of an adapter for a SAX1 {@link org.xml.sax.Parser Parser},
   * which has no way of affecting whether the underlying parser is
   * validating, for example.</p>
   *
   * <p>All XMLReaders are required to support setting
   * http://xml.org/sax/features/namespaces to true and
   * http://xml.org/sax/features/namespace-prefixes to false.</p>
   *
   * <p>Some feature values may be immutable or mutable only 
   * in specific contexts, such as before, during, or after 
   * a parse.</p>
   *
   * @param name The feature name, which is a fully-qualified URI.
   * @param state The requested state of the feature (true or false).
   * @exception org.xml.sax.SAXNotRecognizedException When the
   *            XMLReader does not recognize the feature name.
   * @exception org.xml.sax.SAXNotSupportedException When the
   *            XMLReader recognizes the feature name but 
   *            cannot set the requested value.
   * @see #getFeature
   */
  public void setFeature(String name, boolean value)
      throws SAXNotRecognizedException, SAXNotSupportedException
  {
    log("setFeature("+name+","+value+")",2);
    _parser.setFeature(name, value);
  }


  /**
   * Look up the value of a property.
   *
   * <p>The property name is any fully-qualified URI.  It is
   * possible for an XMLReader to recognize a property name but
   * to be unable to return its state; this is especially true
   * in the case of an adapter for a SAX1 {@link org.xml.sax.Parser
   * Parser}.</p>
   *
   * <p>XMLReaders are not required to recognize any specific
   * property names, though an initial core set is documented for
   * SAX2.</p>
   *
   * <p>Some property values may be available only in specific
   * contexts, such as before, during, or after a parse.</p>
   *
   * <p>Implementors are free (and encouraged) to invent their own properties,
   * using names built on their own URIs.</p>
   *
   * @param name The property name, which is a fully-qualified URI.
   * @return The current value of the property.
   * @exception org.xml.sax.SAXNotRecognizedException When the
   *            XMLReader does not recognize the property name.
   * @exception org.xml.sax.SAXNotSupportedException When the
   *            XMLReader recognizes the property name but 
   *            cannot determine its value at this time.
   * @see #setProperty
   */
  public Object getProperty(String name)
      throws SAXNotRecognizedException, SAXNotSupportedException
  {
    Object result;
    if ( LEXICAL_HANDLER_PROPERTY.equals(name) )
      result = _lexicalHandler;
    else
      result = _parser.getProperty(name);
    
    log("getProperty("+name+") : "+result,2);
    return result;
  }

  /**
   * Set the value of a property.
   *
   * <p>The property name is any fully-qualified URI.  It is
   * possible for an XMLReader to recognize a property name but
   * to be unable to set its value; this is especially true
   * in the case of an adapter for a SAX1 {@link org.xml.sax.Parser
   * Parser}.</p>
   *
   * <p>XMLReaders are not required to recognize setting
   * any specific property names, though a core set is provided with 
   * SAX2.</p>
   *
   * <p>Some property values may be immutable or mutable only 
   * in specific contexts, such as before, during, or after 
   * a parse.</p>
   *
   * <p>This method is also the standard mechanism for setting
   * extended handlers.</p>
   *
   * @param name The property name, which is a fully-qualified URI.
   * @param state The requested value for the property.
   * @exception org.xml.sax.SAXNotRecognizedException When the
   *            XMLReader does not recognize the property name.
   * @exception org.xml.sax.SAXNotSupportedException When the
   *            XMLReader recognizes the property name but 
   *            cannot set the requested value.
   */
  public void setProperty(String name, Object value)
      throws SAXNotRecognizedException, SAXNotSupportedException
  {
    log("setProperty("+name+","+value+")",2);
    if ( LEXICAL_HANDLER_PROPERTY.equals(name) )
      _lexicalHandler = (LexicalHandler)value;
    else
      _parser.setProperty(name, value);
  }
  
  
  ////////////////////////////////////////////////////////////////////
  // Event handlers.
  ////////////////////////////////////////////////////////////////////


  /**
   * Allow an application to register an entity resolver.
   *
   * <p>If the application does not register an entity resolver,
   * the XMLReader will perform its own default resolution.</p>
   *
   * <p>Applications may register a new or different resolver in the
   * middle of a parse, and the SAX parser must begin using the new
   * resolver immediately.</p>
   *
   * @param resolver The entity resolver.
   * @exception java.lang.NullPointerException If the resolver 
   *            argument is null.
   * @see #getEntityResolver
   */
  public void setEntityResolver(EntityResolver resolver)
  {
    log("setEntityResolver("+resolver+")",2);
    _parser.setEntityResolver(resolver);
  }


  /**
   * Return the current entity resolver.
   *
   * @return The current entity resolver, or null if none
   *         has been registered.
   * @see #setEntityResolver
   */
  public EntityResolver getEntityResolver()
  {
    EntityResolver result = _parser.getEntityResolver();
    log("getEntityResolver() : "+result,2);
    return result;
  }


  /**
   * Allow an application to register a DTD event handler.
   *
   * <p>If the application does not register a DTD handler, all DTD
   * events reported by the SAX parser will be silently ignored.</p>
   *
   * <p>Applications may register a new or different handler in the
   * middle of a parse, and the SAX parser must begin using the new
   * handler immediately.</p>
   *
   * @param handler The DTD handler.
   * @exception java.lang.NullPointerException If the handler 
   *            argument is null.
   * @see #getDTDHandler
   */
  public void setDTDHandler(DTDHandler handler)
  {
    log("setDTDHandler("+handler+")",2);
    _dtdHandler = handler;
    _parser.setDTDHandler(this);
  }


  /**
   * Return the current DTD handler.
   *
   * @return The current DTD handler, or null if none
   *         has been registered.
   * @see #setDTDHandler
   */
  public DTDHandler getDTDHandler()
  {
    log("getDTDHandler() : "+_dtdHandler,3);
    return _dtdHandler;
  }


  /**
   * Allow an application to register a content event handler.
   *
   * <p>If the application does not register a content handler, all
   * content events reported by the SAX parser will be silently
   * ignored.</p>
   *
   * <p>Applications may register a new or different handler in the
   * middle of a parse, and the SAX parser must begin using the new
   * handler immediately.</p>
   *
   * @param handler The content handler.
   * @exception java.lang.NullPointerException If the handler 
   *            argument is null.
   * @see #getContentHandler
   */
  public void setContentHandler(ContentHandler handler)
  {
    log("setContentHandler("+handler+")",2);
    _contentHandler = handler;
    _parser.setContentHandler(this);
  }


  /**
   * Return the current content handler.
   *
   * @return The current content handler, or null if none
   *         has been registered.
   * @see #setContentHandler
   */
  public ContentHandler getContentHandler()
  {
    log("getContentHandler() : "+_contentHandler,3);
    return _contentHandler;
  }


  /**
   * Allow an application to register an error event handler.
   *
   * <p>If the application does not register an error handler, all
   * error events reported by the SAX parser will be silently
   * ignored; however, normal processing may not continue.  It is
   * highly recommended that all SAX applications implement an
   * error handler to avoid unexpected bugs.</p>
   *
   * <p>Applications may register a new or different handler in the
   * middle of a parse, and the SAX parser must begin using the new
   * handler immediately.</p>
   *
   * @param handler The error handler.
   * @exception java.lang.NullPointerException If the handler 
   *            argument is null.
   * @see #getErrorHandler
   */
  public void setErrorHandler(ErrorHandler handler)
  {
    log("setErrorHandler("+handler+")",2);
    _errorHandler = handler;
    _parser.setErrorHandler(this);
  }


  /**
   * Return the current error handler.
   *
   * @return The current error handler, or null if none
   *         has been registered.
   * @see #setErrorHandler
   */
  public ErrorHandler getErrorHandler()
  {
    log("getErrorHandler() : "+_errorHandler,3);
    return _errorHandler;
  }


  ////////////////////////////////////////////////////////////////////
  // Parsing.
  ////////////////////////////////////////////////////////////////////

  /**
   * Parse an XML document.
   *
   * <p>The application can use this method to instruct the XML
   * reader to begin parsing an XML document from any valid input
   * source (a character stream, a byte stream, or a URI).</p>
   *
   * <p>Applications may not invoke this method while a parse is in
   * progress (they should create a new XMLReader instead for each
   * nested XML document).  Once a parse is complete, an
   * application may reuse the same XMLReader object, possibly with a
   * different input source.</p>
   *
   * <p>During the parse, the XMLReader will provide information
   * about the XML document through the registered event
   * handlers.</p>
   *
   * <p>This method is synchronous: it will not return until parsing
   * has ended.  If a client application wants to terminate 
   * parsing early, it should throw an exception.</p>
   *
   * @param source The input source for the top-level of the
   *        XML document.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @exception java.io.IOException An IO exception from the parser,
   *            possibly from a byte stream or character stream
   *            supplied by the application.
   * @see org.xml.sax.InputSource
   * @see #parse(java.lang.String)
   * @see #setEntityResolver
   * @see #setDTDHandler
   * @see #setContentHandler
   * @see #setErrorHandler 
   */
  public void parse(InputSource input)
      throws IOException, SAXException
  {
    log("parse("+input+")",2);
    _parser.parse(input);
    
  }


  /**
   * Parse an XML document from a system identifier (URI).
   *
   * <p>This method is a shortcut for the common case of reading a
   * document from a system identifier.  It is the exact
   * equivalent of the following:</p>
   *
   * <pre>
   * parse(new InputSource(systemId));
   * </pre>
   *
   * <p>If the system identifier is a URL, it must be fully resolved
   * by the application before it is passed to the parser.</p>
   *
   * @param systemId The system identifier (URI).
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @exception java.io.IOException An IO exception from the parser,
   *            possibly from a byte stream or character stream
   *            supplied by the application.
   * @see #parse(org.xml.sax.InputSource)
   */
  public void parse(String systemId)
      throws IOException, SAXException
  {
    log("parse("+systemId+")",2);
    _parser.parse(systemId);
  }
  
  // end of XMLReader.java
  
  
  
  // SAX error handler.
  // http://www.saxproject.org
  // No warranty; no copyright -- use this as you will.
  // $Id: ErrorHandler.java,v 1.1.24.1 2004/05/01 08:34:39 jsuttor Exp $
  
  /**
   * Receive notification of a warning.
   *
   * <p>SAX parsers will use this method to report conditions that
   * are not errors or fatal errors as defined by the XML
   * recommendation.  The default behaviour is to take no
   * action.</p>
   *
   * <p>The SAX parser must continue to provide normal parsing events
   * after invoking this method: it should still be possible for the
   * application to process the document through to the end.</p>
   *
   * <p>Filters may use this method to report other, non-XML warnings
   * as well.</p>
   *
   * @param exception The warning information encapsulated in a
   *                  SAX parse exception.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.SAXParseException 
   */
  public void warning(SAXParseException exception)
    throws SAXException
  {
    log("warning("+exception+")",4);
    _errorHandler.warning(exception);
  }
  
  
  /**
   * Receive notification of a recoverable error.
   *
   * <p>This corresponds to the definition of "error" in section 1.2
   * of the W3C XML 1.0 Recommendation.  For example, a validating
   * parser would use this callback to report the violation of a
   * validity constraint.  The default behaviour is to take no
   * action.</p>
   *
   * <p>The SAX parser must continue to provide normal parsing
   * events after invoking this method: it should still be possible
   * for the application to process the document through to the end.
   * If the application cannot do so, then the parser should report
   * a fatal error even if the XML recommendation does not require
   * it to do so.</p>
   *
   * <p>Filters may use this method to report other, non-XML errors
   * as well.</p>
   *
   * @param exception The error information encapsulated in a
   *                  SAX parse exception.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.SAXParseException 
   */
  public void error(SAXParseException exception)
    throws SAXException
  {
    log("error("+exception+")",4);
    _errorHandler.error(exception);
  }
  
  
  /**
   * Receive notification of a non-recoverable error.
   *
   * <p><strong>There is an apparent contradiction between the
   * documentation for this method and the documentation for {@link
   * org.xml.sax.ContentHandler#endDocument}.  Until this ambiguity
   * is resolved in a future major release, clients should make no
   * assumptions about whether endDocument() will or will not be
   * invoked when the parser has reported a fatalError() or thrown
   * an exception.</strong></p>
   *
   * <p>This corresponds to the definition of "fatal error" in
   * section 1.2 of the W3C XML 1.0 Recommendation.  For example, a
   * parser would use this callback to report the violation of a
   * well-formedness constraint.</p>
   *
   * <p>The application must assume that the document is unusable
   * after the parser has invoked this method, and should continue
   * (if at all) only for the sake of collecting additional error
   * messages: in fact, SAX parsers are free to stop reporting any
   * other events once this method has been invoked.</p>
   *
   * @param exception The error information encapsulated in a
   *                  SAX parse exception.  
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.SAXParseException
   */
  public void fatalError(SAXParseException exception)
    throws SAXException
  {
    log("fatalError("+exception+")",4);
    _errorHandler.fatalError(exception);
  }
  
  // end of ErrorHandler.java
  
  
  
  // SAX locator interface for document events.
  // http://www.saxproject.org
  // No warranty; no copyright -- use this as you will.
  // $Id: Locator.java,v 1.1.24.1 2004/05/01 08:34:40 jsuttor Exp $
    
  /**
   * Return the public identifier for the current document event.
   *
   * <p>The return value is the public identifier of the document
   * entity or of the external parsed entity in which the markup
   * triggering the event appears.</p>
   *
   * @return A string containing the public identifier, or
   *         null if none is available.
   * @see #getSystemId
   */
  public String getPublicId()
  {
    String result = _locator.getPublicId();
    log("getPublicId() : "+result,3);
    return result;
  }
  
  /**
   * Return the system identifier for the current document event.
   *
   * <p>The return value is the system identifier of the document
   * entity or of the external parsed entity in which the markup
   * triggering the event appears.</p>
   *
   * <p>If the system identifier is a URL, the parser must resolve it
   * fully before passing it to the application.  For example, a file
   * name must always be provided as a <em>file:...</em> URL, and other
   * kinds of relative URI are also resolved against their bases.</p>
   *
   * @return A string containing the system identifier, or null
   *         if none is available.
   * @see #getPublicId
   */
  public String getSystemId()
  {
    String result = _locator.getSystemId();
    log("getSystemId() : "+result,3);
    return result;
  }
  
  /**
   * Return the line number where the current document event ends.
   * Lines are delimited by line ends, which are defined in
   * the XML specification.
   *
   * <p><strong>Warning:</strong> The return value from the method
   * is intended only as an approximation for the sake of diagnostics;
   * it is not intended to provide sufficient information
   * to edit the character content of the original XML document.
   * In some cases, these "line" numbers match what would be displayed
   * as columns, and in others they may not match the source text
   * due to internal entity expansion.  </p>
   *
   * <p>The return value is an approximation of the line number
   * in the document entity or external parsed entity where the
   * markup triggering the event appears.</p>
   *
   * <p>If possible, the SAX driver should provide the line position 
   * of the first character after the text associated with the document 
   * event.  The first line is line 1.</p>
   *
   * @return The line number, or -1 if none is available.
   * @see #getColumnNumber
   */
  public int getLineNumber()
  {
    int result = _locator.getLineNumber();
    log("getLineNumber() : "+result,3);
    return result;
  }
  
  /**
   * Return the column number where the current document event ends.
   * This is one-based number of Java <code>char</code> values since
   * the last line end.
   *
   * <p><strong>Warning:</strong> The return value from the method
   * is intended only as an approximation for the sake of diagnostics;
   * it is not intended to provide sufficient information
   * to edit the character content of the original XML document.
   * For example, when lines contain combining character sequences, wide
   * characters, surrogate pairs, or bi-directional text, the value may
   * not correspond to the column in a text editor's display. </p>
   *
   * <p>The return value is an approximation of the column number
   * in the document entity or external parsed entity where the
   * markup triggering the event appears.</p>
   *
   * <p>If possible, the SAX driver should provide the line position 
   * of the first character after the text associated with the document 
   * event.  The first column in each line is column 1.</p>
   *
   * @return The column number, or -1 if none is available.
   * @see #getLineNumber
   */
  public int getColumnNumber()
  {
    int result = _locator.getColumnNumber();
    log("getColumnNumber() : "+result,3);
    return result;
  }
  
  // end of Locator.java
  
  
  
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
    _locator = locator;
    _contentHandler.setDocumentLocator(this);
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
    _contentHandler.startDocument();
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
    _contentHandler.endDocument();
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
    _contentHandler.startPrefixMapping(prefix,uri);
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
    _contentHandler.endPrefixMapping(prefix);
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
    _contentHandler.startElement(uri, localName, qName, atts);
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
    _contentHandler.endElement(uri, localName, qName);
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
    _contentHandler.characters(ch, start, length);
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
    _contentHandler.ignorableWhitespace(ch, start, length);
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
    _contentHandler.processingInstruction(target, data);
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
    _contentHandler.skippedEntity(name);
  }
    
  // end of ContentHandler.java

    


  // SAX DTD handler.
  // http://www.saxproject.org
  // No warranty; no copyright -- use this as you will.
  // $Id: DTDHandler.java,v 1.1.24.1 2004/05/01 08:34:39 jsuttor Exp $

  /**
   * Receive notification of a notation declaration event.
   *
   * <p>It is up to the application to record the notation for later
   * reference, if necessary;
   * notations may appear as attribute values and in unparsed entity
   * declarations, and are sometime used with processing instruction
   * target names.</p>
   *
   * <p>At least one of publicId and systemId must be non-null.
   * If a system identifier is present, and it is a URL, the SAX
   * parser must resolve it fully before passing it to the
   * application through this event.</p>
   *
   * <p>There is no guarantee that the notation declaration will be
   * reported before any unparsed entities that use it.</p>
   *
   * @param name The notation name.
   * @param publicId The notation's public identifier, or null if
   *        none was given.
   * @param systemId The notation's system identifier, or null if
   *        none was given.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #unparsedEntityDecl
   * @see org.xml.sax.Attributes
   */
  public void notationDecl(String name, String publicId, String systemId)
    throws SAXException
  {
    log("notationDecl("+name+","+publicId+","+systemId+")",4);
    _dtdHandler.notationDecl(name,publicId,systemId);
  }
  
  
  /**
   * Receive notification of an unparsed entity declaration event.
   *
   * <p>Note that the notation name corresponds to a notation
   * reported by the {@link #notationDecl notationDecl} event.  
   * It is up to the application to record the entity for later 
   * reference, if necessary;
   * unparsed entities may appear as attribute values. 
   * </p>
   *
   * <p>If the system identifier is a URL, the parser must resolve it
   * fully before passing it to the application.</p>
   *
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @param name The unparsed entity's name.
   * @param publicId The entity's public identifier, or null if none
   *        was given.
   * @param systemId The entity's system identifier.
   * @param notationName The name of the associated notation.
   * @see #notationDecl
   * @see org.xml.sax.Attributes
   */
  public void unparsedEntityDecl(String name,String publicId,String systemId,String notationName)
    throws SAXException
  {
    log("unparsedEntityDecl("+name+","+publicId+","+systemId+","+notationName+")",4);
    _dtdHandler.unparsedEntityDecl(name,publicId,systemId,notationName);
  }
  
  // end of DTDHandler.java
  

  

  // LexicalHandler.java - optional handler for lexical parse events.
  // http://www.saxproject.org
  // Public Domain: no warranty.
  // $Id: LexicalHandler.java,v 1.1.24.1 2004/05/01 08:34:43 jsuttor Exp $
  
  /**
   * Report the start of DTD declarations, if any.
   *
   * <p>This method is intended to report the beginning of the
   * DOCTYPE declaration; if the document has no DOCTYPE declaration,
   * this method will not be invoked.</p>
   *
   * <p>All declarations reported through 
   * {@link org.xml.sax.DTDHandler DTDHandler} or
   * {@link org.xml.sax.ext.DeclHandler DeclHandler} events must appear
   * between the startDTD and {@link #endDTD endDTD} events.
   * Declarations are assumed to belong to the internal DTD subset
   * unless they appear between {@link #startEntity startEntity}
   * and {@link #endEntity endEntity} events.  Comments and
   * processing instructions from the DTD should also be reported
   * between the startDTD and endDTD events, in their original 
   * order of (logical) occurrence; they are not required to
   * appear in their correct locations relative to DTDHandler
   * or DeclHandler events, however.</p>
   *
   * <p>Note that the start/endDTD events will appear within
   * the start/endDocument events from ContentHandler and
   * before the first 
   * {@link org.xml.sax.ContentHandler#startElement startElement}
   * event.</p>
   *
   * @param name The document type name.
   * @param publicId The declared public identifier for the
   *        external DTD subset, or null if none was declared.
   * @param systemId The declared system identifier for the
   *        external DTD subset, or null if none was declared.
   *        (Note that this is not resolved against the document
   *        base URI.)
   * @exception SAXException The application may raise an
   *            exception.
   * @see #endDTD
   * @see #startEntity
   */
  public void startDTD(String name, String publicId,String systemId)
    throws SAXException
  {
    log("startDTD("+name+","+publicId+","+systemId+")",4);
    _lexicalHandler.startDTD(name,publicId,systemId);
  }


  /**
   * Report the end of DTD declarations.
   *
   * <p>This method is intended to report the end of the
   * DOCTYPE declaration; if the document has no DOCTYPE declaration,
   * this method will not be invoked.</p>
   *
   * @exception SAXException The application may raise an exception.
   * @see #startDTD
   */
  public void endDTD()
    throws SAXException
  {
    log("endDTD()",4);
    _lexicalHandler.endDTD();
  }


  /**
   * Report the beginning of some internal and external XML entities.
   *
   * <p>The reporting of parameter entities (including
   * the external DTD subset) is optional, and SAX2 drivers that
   * report LexicalHandler events may not implement it; you can use the
   * <code
   * >http://xml.org/sax/features/lexical-handler/parameter-entities</code>
   * feature to query or control the reporting of parameter entities.</p>
   *
   * <p>General entities are reported with their regular names,
   * parameter entities have '%' prepended to their names, and 
   * the external DTD subset has the pseudo-entity name "[dtd]".</p>
   *
   * <p>When a SAX2 driver is providing these events, all other 
   * events must be properly nested within start/end entity 
   * events.  There is no additional requirement that events from 
   * {@link org.xml.sax.ext.DeclHandler DeclHandler} or
   * {@link org.xml.sax.DTDHandler DTDHandler} be properly ordered.</p>
   *
   * <p>Note that skipped entities will be reported through the
   * {@link org.xml.sax.ContentHandler#skippedEntity skippedEntity}
   * event, which is part of the ContentHandler interface.</p>
   *
   * <p>Because of the streaming event model that SAX uses, some
   * entity boundaries cannot be reported under any 
   * circumstances:</p>
   *
   * <ul>
   * <li>general entities within attribute values</li>
   * <li>parameter entities within declarations</li>
   * </ul>
   *
   * <p>These will be silently expanded, with no indication of where
   * the original entity boundaries were.</p>
   *
   * <p>Note also that the boundaries of character references (which
   * are not really entities anyway) are not reported.</p>
   *
   * <p>All start/endEntity events must be properly nested.
   *
   * @param name The name of the entity.  If it is a parameter
   *        entity, the name will begin with '%', and if it is the
   *        external DTD subset, it will be "[dtd]".
   * @exception SAXException The application may raise an exception.
   * @see #endEntity
   * @see org.xml.sax.ext.DeclHandler#internalEntityDecl
   * @see org.xml.sax.ext.DeclHandler#externalEntityDecl 
   */
  public void startEntity(String name)
    throws SAXException
  {
    log("startEntity("+name+")",4);
    _lexicalHandler.startEntity(name);
  }

  /**
   * Report the end of an entity.
   *
   * @param name The name of the entity that is ending.
   * @exception SAXException The application may raise an exception.
   * @see #startEntity
   */
  public void endEntity(String name)
    throws SAXException
  {
    log("endEntity("+name+")",4);
    _lexicalHandler.endEntity(name);
  }

  /**
   * Report the start of a CDATA section.
   *
   * <p>The contents of the CDATA section will be reported through
   * the regular {@link org.xml.sax.ContentHandler#characters
   * characters} event; this event is intended only to report
   * the boundary.</p>
   *
   * @exception SAXException The application may raise an exception.
   * @see #endCDATA
   */
  public void startCDATA()
    throws SAXException
  {
    log("startCDATA()",4);
    _lexicalHandler.startCDATA();
  }

  /**
   * Report the end of a CDATA section.
   *
   * @exception SAXException The application may raise an exception.
   * @see #startCDATA
   */
  public void endCDATA()
    throws SAXException
  {
    log("endCDATA()",4);
    _lexicalHandler.endCDATA();
  }

  /**
   * Report an XML comment anywhere in the document.
   *
   * <p>This callback will be used for comments inside or outside the
   * document element, including comments in the external DTD
   * subset (if read).  Comments in the DTD must be properly
   * nested inside start/endDTD and start/endEntity events (if
   * used).</p>
   *
   * @param ch An array holding the characters in the comment.
   * @param start The starting position in the array.
   * @param length The number of characters to use from the array.
   * @exception SAXException The application may raise an exception.
   */
  public void comment(char ch[], int start, int length)
    throws SAXException
  {
    log("comment("+ch+","+start+","+length+")",4);
    _lexicalHandler.comment(ch, start, length);
  }

  // end of LexicalHandler.java
  
  
}
// end of XMLReader.java
