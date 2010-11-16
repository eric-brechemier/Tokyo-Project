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

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import net.sf.tokyo.ITokyoNaut;

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
public class Step5_CsvToSaxNaut
  implements XMLReader,Locator,
             ITokyoNaut
{
  /**
   * The http://xml.org/sax/features/namespaces feature controls general Namespace processing. 
   * When this feature is true (the default), any applicable Namespace URIs and localNames 
   * (for elements in namespaces) must be available through the startElement and endElement callbacks 
   * in the ContentHandler interface, and through the various methods in the Attributes interface, 
   * and start/endPrefixMapping events must be reported. For elements and attributes outside of 
   * namespaces, the associated namespace URIs will be empty strings and the qName parameter is 
   * guaranteed to be provided as a non-empty string.
   * (quoted from http://www.saxproject.org/namespaces.html)
   */
  public static final String FEATURE_NAMESPACES = "http://xml.org/sax/features/namespaces";
  
  /**
   * Default value of the http://xml.org/sax/features/namespaces feature.
   *
   */
  public static final boolean DEFAULT_VALUE_FEATURE_NAMESPACES = true;
  
  /**
   * The http://xml.org/sax/features/namespace-prefixes feature controls the reporting of 
   * qNames and Namespace declarations (xmlns* attributes). 
   * Unless the value of this feature flag is changed to true (from its default of false), 
   * qNames may optionally (!) be reported as empty strings for elements and attributes that 
   * have an associated namespace URI, and xmlns* attributes will not be reported. 
   * When set to true, that information will always be available.
   * (quoted from http://www.saxproject.org/namespaces.html)
   */
  public static final String FEATURE_NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
  
  /**
   * Default value of the http://xml.org/sax/features/namespace-prefixes feature.
   *
   */
  public static final boolean DEFAULT_VALUE_FEATURE_NAMESPACE_PREFIXES = false;
  
  
  /** Internal Error Handler. */
  protected ErrorHandler _errorHandler;
  /** Internal Content Handler. */
  protected ContentHandler _contentHandler;
  /** Internal DTD Handler. */
  protected DTDHandler _dtdHandler;
  /** Internal Entity Resolver. */
  protected EntityResolver _entityResolver;
  
  protected InputSource _inputSource;
  
  /**
   * Source of Sax-like events.
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
   * InputSource current line number
   */
  protected int _lineNumber;
  
  /**
   * InputSource current column number
   */
  protected int _columnNumber;
  
  /**
   * Public constructor.
   *
   */
  public Step5_CsvToSaxNaut()
  {
    _dataNaut = new Step5_CsvReaderNaut();
  }
  
  
  
  
  ////////////////////////////////////////////////////////////////////
  // Logging.
  ////////////////////////////////////////////////////////////////////
  
  protected static final int LOG_LEVEL_LIMIT = 0;
  
  protected void log(String message, int level)
  {
    if ( level > LOG_LEVEL_LIMIT )
      System.out.println("CsvToSaxNaut::"+message);
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
        if (_errorHandler != null)
          _errorHandler.fatalError(new SAXParseException("Error reported by custom parser: "+context[language+1],this));
        
        if (_contentHandler != null)
          _contentHandler.endDocument();
          
        _inputSource = null;
        _dataNaut = null;
        _context = null;
        _contextType = null;
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
    try
    {
      log("say("+speaker+","+context+"=>"+context[sentence]+","+type+"=>"+type[sentence]+","+sentence+")",3);
      
      int[] lineCol = (int[])context[Step5_Sax.DOCUMENT_LINE_COLUMN];
      _lineNumber = lineCol[0];
      _columnNumber = lineCol[1]; 
      
      switch(type[sentence])
      {
        case Step5_Sax.CONTENT_START_DOCUMENT:
          {
          _contentHandler.startDocument();
          }
          break;
        
        case Step5_Sax.CONTENT_START_PREFIX_MAPPING:
          {
          // pop arguments from Context Stack
          String prefix = (String)context[sentence+1];
          String uri = (String)context[sentence+2];
          _contentHandler.startPrefixMapping(prefix,uri);
          }
          break;
        
        case Step5_Sax.CONTENT_START_ELEMENT:
          {
          // pop arguments from Context Stack
          String uri = (String)context[sentence+1];
          String localName = (String)context[sentence+2];
          String qName = (String)context[sentence+3];
          Attributes atts = (Attributes)context[sentence+4];
          _contentHandler.startElement(uri,localName,qName,atts);
          }
          break;
        
        case Step5_Sax.CONTENT_CHARACTERS:
          {
          // pop arguments from Context Stack
          char[] characters = (char[])context[sentence+1];
          int[] startLength = (int[])context[sentence+2];
          int start = startLength[0];
          int length = startLength[1];
          _contentHandler.characters(characters,start,length);
          }
          break;
        
        case Step5_Sax.CONTENT_END_ELEMENT:
          {
          // pop arguments from Context Stack
          String uri = (String)context[sentence+1];
          String localName = (String)context[sentence+2];
          String qName = (String)context[sentence+3];
          _contentHandler.endElement(uri,localName,qName);
          }
          break;
          
        case Step5_Sax.CONTENT_END_PREFIX_MAPPING:
          {
          // pop arguments from Context Stack
          String prefix = (String)context[sentence+1];
          _contentHandler.endPrefixMapping(prefix);
          }
          break;
        
        case Step5_Sax.CONTENT_END_DOCUMENT:
          {
          _contentHandler.endDocument();
          speaker.quit(this,context,type,Step5_Sax.LANGUAGE);
          }
          break;
        
        case Step5_Sax.CONTENT_IGNORABLE_WHITE_SPACE:
          {
          // pop arguments from Context Stack
          char[] characters = (char[])context[sentence+1];
          int[] startLength = (int[])context[sentence+2];
          int start = startLength[0];
          int length = startLength[1];
          _contentHandler.ignorableWhitespace(characters,start,length);
          }
          break;
        
        case Step5_Sax.CONTENT_PROCESSING_INSTRUCTION:
          {
          // pop arguments from Context Stack
          String target = (String)context[sentence+1];
          String data = (String)context[sentence+2];
          _contentHandler.processingInstruction(target,data);
          }
          break;
        
        case Step5_Sax.CONTENT_SKIPPED_ENTITY:
          {
          // pop arguments from Context Stack
          String entity_name = (String)context[sentence+1];
          _contentHandler.skippedEntity(entity_name);
          }
          break;
        
        default:
          speaker.show(this,"Unknown type: "+type[sentence]);
      }
      
    }
    catch(SAXException e)
    {
      if (speaker!=null)
        speaker.show(this,"SAX Error: "+e);
      else
        log("Invalid speaker="+speaker,5);
    }
    catch(Exception e)
    {
      if (speaker!=null)
        speaker.show(this,"Unexpected exception: "+e);
      else
        log("Invalid speaker="+speaker,5);
    }
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
    boolean result;
    
    if ( FEATURE_NAMESPACES.equals(name) )
      result = DEFAULT_VALUE_FEATURE_NAMESPACES;
    else if ( FEATURE_NAMESPACE_PREFIXES.equals(name) )
      result = DEFAULT_VALUE_FEATURE_NAMESPACE_PREFIXES;
    else
      throw new SAXNotRecognizedException("Feature: "+name);
    
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
    
    if ( FEATURE_NAMESPACES.equals(name) )
    {
      if ( value != DEFAULT_VALUE_FEATURE_NAMESPACES )
        throw new SAXNotSupportedException("Feature: "+name+" Value: "+value);
      else
        return;
    }
    else if ( FEATURE_NAMESPACE_PREFIXES.equals(name) )
    {
      if ( value != DEFAULT_VALUE_FEATURE_NAMESPACE_PREFIXES )
        throw new SAXNotSupportedException("Feature: "+name+" Value: "+value);
      else
        return;
    }
    else
    {
      throw new SAXNotRecognizedException("Feature: "+name);
    }
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
    log("getProperty("+name+") : ...",2);
    throw new SAXNotRecognizedException("Property: "+name);
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
    throw new SAXNotRecognizedException("Property: "+name);
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
    _entityResolver = resolver;
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
    log("getEntityResolver() : "+_entityResolver,2);
    return _entityResolver;
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
    
    // Added Internal Call to setDocumentLocator here
    // in order to invoke it before any of the other methods in the ContentHandler interface
    _contentHandler.setDocumentLocator(this);
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
    
    _inputSource = input;
    _lineNumber = 1;
    _columnNumber = 1;
    _contextType = new int[Step5_Sax.SAX_MAX_STACK_LENGTH];
    _context = new Object[Step5_Sax.SAX_MAX_STACK_LENGTH];
    
    _contextType[Step5_Sax.LANGUAGE] = Step5_Sax.LANGUAGE;
    _context[Step5_Sax.LANGUAGE] = Step5_Sax.SAX_EVENTS_LANG;
    
    _contextType[Step5_Sax.DOCUMENT_INPUT_SOURCE] = Step5_Sax.DOCUMENT_INPUT_SOURCE;
    _context[Step5_Sax.DOCUMENT_INPUT_SOURCE] = _inputSource;
    
    _dataNaut.meet(this,_context,_contextType,Step5_Sax.LANGUAGE);
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
    parse(new InputSource(systemId));
  }
  
  // end of XMLReader.java
  
  
  
  
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
    String result = _inputSource.getPublicId();
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
    String result = _inputSource.getSystemId();
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
    int result = _lineNumber;
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
    int result = _columnNumber;
    log("getColumnNumber() : "+result,3);
    return result;
  }
  
  // end of Locator.java
    
  
}
// end of XMLReader.java