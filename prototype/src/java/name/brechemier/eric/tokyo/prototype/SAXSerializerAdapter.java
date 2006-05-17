/*
 * The Tokyo Project is hosted on Sourceforge:
 * http://sourceforge.net/projects/tokyo/
 * 
 * Copyright (C) 2005 Eric Bréchemier
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
package name.brechemier.eric.tokyo.prototype;

import java.io.Writer;

import name.brechemier.eric.tokyo.prototype.CustomDataSerializer; 

import org.xmlpull.v1.XmlSerializer;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * (Tokyo Project prototype) <br />
 * Wrapper for XML Pull Serializer API implementing the SAX2 ContentHandler interface <br />
 *
 * @author Eric Bréchemier
 *
 */
public class SAXSerializerAdapter implements ContentHandler {
  
  private Writer _writer;
  private XmlSerializer _serializer;
  
  public SAXSerializerAdapter(Writer writer) throws SAXException {
    _writer = writer;
    _serializer = new CustomDataSerializer();
    
    try {
      _serializer.setOutput(writer);
    } catch(Exception e) {
      throw new SAXException(e);
    }
  }
  
  private Locator _locator;
  
  /**
   * (SAX2 interface) <br />
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
   * during the invocation of the events in this interface.  The
   * application should not attempt to use it at any other time.</p>
   *
   * @param locator An object that can return the location of
   *                any SAX document event.
   * @see org.xml.sax.Locator
   */
  public void setDocumentLocator (Locator locator) {
    _locator = locator;
  }

  /**
   * (SAX2 interface) <br />
   * Receive notification of the beginning of a document.
   *
   * <p>The SAX parser will invoke this method only once, before any
   * other methods in this interface or in {@link org.xml.sax.DTDHandler
   * DTDHandler} (except for {@link #setDocumentLocator 
   * setDocumentLocator}).</p>
   *
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #endDocument
   */
  public void startDocument () throws SAXException {
    try {
      _serializer.startDocument("UTF-8",new Boolean(true) );
    } catch(Exception e) {
      throw new SAXException(e);
    }
  }

  /**
   * (SAX2 interface) <br />
   * Receive notification of the end of a document.
   *
   * <p>The SAX parser will invoke this method only once, and it will
   * be the last method invoked during the parse.  The parser shall
   * not invoke this method until it has either abandoned parsing
   * (because of an unrecoverable error) or reached the end of
   * input.</p>
   *
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #startDocument
   */
  public void endDocument() throws SAXException {
    try {
      _serializer.endDocument();
    } catch(Exception e) {
      throw new SAXException(e);
    } 
  }
  
  /**
   * (SAX2 interface) <br />
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
   * guaranteed to be properly nested relative to each-other:
   * all startPrefixMapping events will occur before the
   * corresponding {@link #startElement startElement} event, 
   * and all {@link #endPrefixMapping endPrefixMapping}
   * events will occur after the corresponding {@link #endElement
   * endElement} event, but their order is not otherwise 
   * guaranteed.</p>
   *
   * <p>There should never be start/endPrefixMapping events for the
   * "xml" prefix, since it is predeclared and immutable.</p>
   *
   * @param prefix The Namespace prefix being declared.
   * @param uri The Namespace URI the prefix is mapped to.
   * @exception org.xml.sax.SAXException The client may throw
   *            an exception during processing.
   * @see #endPrefixMapping
   * @see #startElement
   */
  public void startPrefixMapping (String prefix, String uri) throws SAXException {
    try {
      _serializer.setPrefix(prefix,uri);
    } catch(Exception e) {
      throw new SAXException(e);
    }
  }


  /**
   * (SAX2 interface) <br />
   * End the scope of a prefix-URI mapping.
   *
   * <p>See {@link #startPrefixMapping startPrefixMapping} for 
   * details.  This event will always occur after the corresponding 
   * {@link #endElement endElement} event, but the order of 
   * {@link #endPrefixMapping endPrefixMapping} events is not otherwise
   * guaranteed.</p>
   *
   * @param prefix The prefix that was being mapping.
   * @exception org.xml.sax.SAXException The client may throw
   *            an exception during processing.
   * @see #startPrefixMapping
   * @see #endElement
   */
  public void endPrefixMapping (String prefix) throws SAXException {
    // prefix is always valid for the next element including child elements
    // TODO check SAX expections on prefix mapping
  }


  /**
   * (SAX2 interface) <br />
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
   * @param uri The Namespace URI, or the empty string if the
   *        element has no Namespace URI or if Namespace
   *        processing is not being performed.
   * @param localName The local name (without prefix), or the
   *        empty string if Namespace processing is not being
   *        performed.
   * @param qName The qualified name (with prefix), or the
   *        empty string if qualified names are not available.
   * @param atts The attributes attached to the element.  If
   *        there are no attributes, it shall be an empty
   *        Attributes object.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #endElement
   * @see org.xml.sax.Attributes
   */
  public void startElement
    (String namespaceURI, String localName, String qName, Attributes atts) 
      throws SAXException {
    
    try {
      _serializer.startTag(namespaceURI,localName);
      for(int i=0;i<atts.getLength();i++) {
        _serializer.attribute
          (
            atts.getURI(i),
            atts.getLocalName(i),
            atts.getValue(i)
          );
      }
    } catch(Exception e) {
      throw new SAXException(e);
    }
  }


  /**
   * (SAX2 interface) <br />
   * Receive notification of the end of an element.
   *
   * <p>The SAX parser will invoke this method at the end of every
   * element in the XML document; there will be a corresponding
   * {@link #startElement startElement} event for every endElement 
   * event (even when the element is empty).</p>
   *
   * <p>For information on the names, see startElement.</p>
   *
   * @param uri The Namespace URI, or the empty string if the
   *        element has no Namespace URI or if Namespace
   *        processing is not being performed.
   * @param localName The local name (without prefix), or the
   *        empty string if Namespace processing is not being
   *        performed.
   * @param qName The qualified XML 1.0 name (with prefix), or the
   *        empty string if qualified names are not available.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void endElement (String namespaceURI, String localName, String qName)
    throws SAXException {
    
    try {
      _serializer.endTag(namespaceURI,localName);
    } catch(Exception e) {
      throw new SAXException(e);
    }
  }


  /**
   * (SAX2 interface) <br />
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
   * <p>Note that some parsers will report whitespace in element
   * content using the {@link #ignorableWhitespace ignorableWhitespace}
   * method rather than this one (validating parsers <em>must</em> 
   * do so).</p>
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #ignorableWhitespace 
   * @see org.xml.sax.Locator
   */
  public void characters(char ch[], int start, int length) throws SAXException {
    try {
      _serializer.text(ch,start,length);
    } catch(Exception e) {
      throw new SAXException(e);
    }
  }


  /**
   * (SAX2 interface) <br />
   * Receive notification of ignorable whitespace in element content.
   *
   * <p>Validating Parsers must use this method to report each chunk
   * of whitespace in element content (see the W3C XML 1.0 recommendation,
   * section 2.10): non-validating parsers may also use this method
   * if they are capable of parsing and using content models.</p>
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
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #characters
   */
  public void ignorableWhitespace (char ch[], int start, int length) throws SAXException {
    try {
      _serializer.ignorableWhitespace(new String(ch,start,start+length));
    } catch(Exception e) {
      throw new SAXException(e);
    }
  }


  /**
   * (SAX2 interface) <br />
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
   * @param target The processing instruction target.
   * @param data The processing instruction data, or null if
   *        none was supplied.  The data does not include any
   *        whitespace separating it from the target.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void processingInstruction (String target, String data) throws SAXException {
    try {
      _serializer.processingInstruction(target+' '+data);
    } catch(Exception e) {
      throw new SAXException(e);
    }
  }


  /**
   * (SAX2 interface) <br />
   * Receive notification of a skipped entity.
   *
   * <p>The Parser will invoke this method once for each entity
   * skipped.  Non-validating processors may skip entities if they
   * have not seen the declarations (because, for example, the
   * entity was declared in an external DTD subset).  All processors
   * may skip external entities, depending on the values of the
   * <code>http://xml.org/sax/features/external-general-entities</code>
   * and the
   * <code>http://xml.org/sax/features/external-parameter-entities</code>
   * properties.</p>
   *
   * @param name The name of the skipped entity.  If it is a 
   *        parameter entity, the name will begin with '%', and if
   *        it is the external DTD subset, it will be the string
   *        "[dtd]".
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void skippedEntity (String name) throws SAXException {
  }
  
}