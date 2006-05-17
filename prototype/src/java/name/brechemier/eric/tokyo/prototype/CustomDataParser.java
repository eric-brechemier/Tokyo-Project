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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;


/**
 * (Tokyo Project prototype) <br />
 * Custom data parser to provide input to XSLT processor <br />
 * Implements the XML Pull Parser interface
 *
 * @author Eric Bréchemier
 *
 */
public class CustomDataParser implements XmlPullParser {
  
  public static final String NAMESPACE_COUNTRIES = "http://eric.brechemier.name/tokyo/prototype/countries";
  public static final String EMPTY = "";
  public static final String CDATA = "CDATA";
  
  public static final String COUNTRY = "country";
  private static final int MAX_ATTRIBUTES_COUNT = 3;
  public static final String NAME = "name";
  public static final String CONTINENT = "continent";
  public static final String CAPITAL_CITY = "capital";
  
  private Reader _reader;
  private String _inputEncoding;
  private int _currentEvent;
  private String _currentElementName;
  private int _currentAttributeCount;
  private String[] _currentAttributeName;
  private String[] _currentAttributeValue;
  
  private StringBuffer _buffer;
  private int _currentDepth;
  private int _lineNumber;
  private int _columnNumber;
  private boolean _endOfFileReached;
  private boolean _isAnotherCountryAvailable;
  
  public CustomDataParser() {
    reset();
  }
  
  /**
   * Resets the parser
   */
  private void reset() {
    _reader = null;
    _inputEncoding = null;
    _currentEvent = XmlPullParser.START_DOCUMENT;
    _currentElementName = null;
    _currentAttributeCount = -1;
    _currentAttributeName = new String[MAX_ATTRIBUTES_COUNT];
    _currentAttributeValue = new String[MAX_ATTRIBUTES_COUNT];
    
    _buffer = new StringBuffer(30);
    _currentDepth = 0;
    _lineNumber = -1;
    _columnNumber = -1;
    
    _endOfFileReached = false;
  }
  
  /**
   * (XML Pull Parser API) <br />
   * Use this call to change the general behaviour of the parser,
   * such as namespace processing or doctype declaration handling.
   * This method must be called before the first call to next or
   * nextToken. Otherwise, an exception is thrown.
   * <p>Example: call setFeature(FEATURE_PROCESS_NAMESPACES, true) in order
   * to switch on namespace processing. The initial settings correspond
   * to the properties requested from the XML Pull Parser factory.
   * If none were requested, all feautures are deactivated by default.
   *
   * @exception XmlPullParserException If the feature is not supported or can not be set
   * @exception IllegalArgumentException If string with the feature name is null
   */
  public void setFeature(String name, boolean state) throws XmlPullParserException {
    if(name==null)
      throw new IllegalArgumentException("Unknown feature: "+name);
    
    if( name.equals(XmlPullParser.FEATURE_PROCESS_NAMESPACES) )
      return; // Nota: always true for us, even if default value should be false
    
    throw new XmlPullParserException("Unsupported feature: "+name);
  }

  /**
   * (XML Pull Parser API) <br />
   * Returns the current value of the given feature.
   * <p><strong>Please note:</strong> unknown features are
   * <strong>always</strong> returned as false.
   *
   * @param name The name of feature to be retrieved.
   * @return The value of the feature.
   * @exception IllegalArgumentException if string the feature name is null
   */
  public boolean getFeature(String name) {
    if(name==null)
      throw new IllegalArgumentException("Unknown feature: "+name);
    
    if( name.equals(XmlPullParser.FEATURE_PROCESS_NAMESPACES) )
      return true;
    
    return false;
  }

  /**
   * (XML Pull Parser API) <br />
   * Set the value of a property.
   *
   * The property name is any fully-qualified URI.
   *
   * @exception XmlPullParserException If the property is not supported or can not be set
   * @exception IllegalArgumentException If string with the property name is null
   */
  public void setProperty(String name, Object value) throws XmlPullParserException {
    if(name==null)
      throw new IllegalArgumentException("Unknown property: "+name);
    
    throw new XmlPullParserException("Unsupported property: "+name);
  }

  /**
   * (XML Pull Parser API) <br />
   * Look up the value of a property.
   *
   * The property name is any fully-qualified URI.
   * <p><strong>NOTE:</strong> unknown properties are <strong>always</strong>
   * returned as null.
   *
   * @param name The name of property to be retrieved.
   * @return The value of named property.
   */
  public Object getProperty(String name) {
    return null;
  }


  /**
   * (XML Pull Parser API) <br />
   * Set the input source for parser to the given reader and
   * resets the parser. The event type is set to the initial value
   * START_DOCUMENT.
   * Setting the reader to null will just stop parsing and
   * reset parser state,
   * allowing the parser to free internal resources
   * such as parsing buffers.
   */
  public void setInput(Reader in) throws XmlPullParserException {
    reset();
    _reader = in;
  }

  /**
   * (XML Pull Parser API) <br />
   * Sets the input stream the parser is going to process.
   * This call resets the parser state and sets the event type
   * to the initial value START_DOCUMENT.
   *
   * <p><strong>NOTE:</strong> If an input encoding string is passed,
   *  it MUST be used. Otherwise,
   *  if inputEncoding is null, the parser SHOULD try to determine
   *  input encoding following XML 1.0 specification (see below).
   *  If encoding detection is supported then following feature
   *  <a href="http://xmlpull.org/v1/doc/features.html#detect-encoding">http://xmlpull.org/v1/doc/features.html#detect-encoding</a>
   *  MUST be true amd otherwise it must be false
   *
   * @param inputStream contains a raw byte input stream of possibly
   *     unknown encoding (when inputEncoding is null).
   *
   * @param inputEncoding if not null it MUST be used as encoding for inputStream
   */
  public void setInput(InputStream inputStream, String inputEncoding) 
      throws XmlPullParserException {
    
    reset();
    
    // NOTA: code to be changed when custom data is binary
    
    _inputEncoding = inputEncoding;
    if (_inputEncoding==null) {
      // TODO: should detect encoding
      _inputEncoding = "UTF8";
    }
    
    try {
      _reader = new InputStreamReader(inputStream, _inputEncoding);
    } catch(Exception e) {
      throw new XmlPullParserException( e.toString() );
    }
  }

  /**
   * (XML Pull Parser API) <br />
   * Returns the input encoding if known, null otherwise.
   * If setInput(InputStream, inputEncoding) was called with an inputEncoding
   * value other than null, this value must be returned
   * from this method. Otherwise, if inputEncoding is null and
   * the parser suppports the encoding detection feature
   * (http://xmlpull.org/v1/doc/features.html#detect-encoding),
   * it must return the detected encoding.
   * If setInput(Reader) was called, null is returned.
   * After first call to next if XML declaration was present this method
   * will return encoding declared.
   */
  public String getInputEncoding() {
    return _inputEncoding;
  }

  /**
   * (XML Pull Parser API) <br />
   * Set new value for entity replacement text as defined in
   * <a href="http://www.w3.org/TR/REC-xml#intern-replacement">XML 1.0 Section 4.5
   * Construction of Internal Entity Replacement Text</a>.
   * If FEATURE_PROCESS_DOCDECL or FEATURE_VALIDATION are set, calling this
   * function will result in an exception -- when processing of DOCDECL is
   * enabled, there is no need to the entity replacement text manually.
   *
   * <p>The motivation for this function is to allow very small
   * implementations of XMLPULL that will work in J2ME environments.
   * Though these implementations may not be able to process the document type
   * declaration, they still can work with known DTDs by using this function.
   *
   * <p><b>Please notes:</b> The given value is used literally as replacement text
   * and it corresponds to declaring entity in DTD that has all special characters
   * escaped: left angle bracket is replaced with &amp;lt;, ampersnad with &amp;amp;
   * and so on.
   *
   * <p><b>Note:</b> The given value is the literal replacement text and must not
   * contain any other entity reference (if it contains any entity reference
   * there will be no further replacement).
   *
   * <p><b>Note:</b> The list of pre-defined entity names will
   * always contain standard XML entities such as
   * amp (&amp;amp;), lt (&amp;lt;), gt (&amp;gt;), quot (&amp;quot;), and apos (&amp;apos;).
   * Those cannot be redefined by this method!
   *
   * @see #setInput
   * @see #FEATURE_PROCESS_DOCDECL
   * @see #FEATURE_VALIDATION
   */
  public void defineEntityReplacementText(String entityName, String replacementText) 
    throws XmlPullParserException {}

  /**
   * (XML Pull Parser API) <br />
   * Returns the numbers of elements in the namespace stack for the given
   * depth.
   * If namespaces are not enabled, 0 is returned.
   *
   * <p><b>NOTE:</b> when parser is on END_TAG then it is allowed to call
   *  this function with getDepth()+1 argument to retrieve position of namespace
   *  prefixes and URIs that were declared on corresponding START_TAG.
   * <p><b>NOTE:</b> to retrieve list of namespaces declared in current element:<pre>
   *       XmlPullParser pp = ...
   *       int nsStart = pp.getNamespaceCount(pp.getDepth()-1);
   *       int nsEnd = pp.getNamespaceCount(pp.getDepth());
   *       for (int i = nsStart; i < nsEnd; i++) {
   *          String prefix = pp.getNamespacePrefix(i);
   *          String ns = pp.getNamespaceUri(i);
   *           // ...
   *      }
   * </pre>
   *
   * @see #getNamespacePrefix
   * @see #getNamespaceUri
   * @see #getNamespace()
   * @see #getNamespace(String)
   */
  public int getNamespaceCount(int depth) throws XmlPullParserException {
    return 1; // only NAMESPACE_COUNTRIES
  }

  /**
   * (XML Pull Parser API) <br />
   * Returns the namespace prefixe for the given position
   * in the namespace stack.
   * Default namespace declaration (xmlns='...') will have null as prefix.
   * If the given index is out of range, an exception is thrown.
   * <p><b>Please note:</b> when the parser is on an END_TAG,
   * namespace prefixes that were declared
   * in the corresponding START_TAG are still accessible
   * although they are no longer in scope.
   */
  public String getNamespacePrefix(int pos) throws XmlPullParserException {
    if(pos > 0) // only NAMESPACE_COUNTRIES
      throw new XmlPullParserException("Namespace position out of range");
    
    return null; // default namespace declaration
  }

  /**
   * (XML Pull Parser API) <br />
   * Returns the namespace URI for the given position in the
   * namespace stack
   * If the position is out of range, an exception is thrown.
   * <p><b>NOTE:</b> when parser is on END_TAG then namespace prefixes that were declared
   *  in corresponding START_TAG are still accessible even though they are not in scope
   */
  public String getNamespaceUri(int pos) throws XmlPullParserException {
    if(pos > 0) // only NAMESPACE_COUNTRIES
      throw new XmlPullParserException("Namespace positions out of range");
    
    return NAMESPACE_COUNTRIES;
  }

  /**
   * (XML Pull Parser API) <br />
   * Returns the URI corresponding to the given prefix,
   * depending on current state of the parser.
   *
   * <p>If the prefix was not declared in the current scope,
   * null is returned. The default namespace is included
   * in the namespace table and is available via
   * getNamespace (null).
   *
   * <p>This method is a convenience method for
   *
   * <pre>
   *  for (int i = getNamespaceCount(getDepth ())-1; i >= 0; i--) {
   *   if (getNamespacePrefix(i).equals( prefix )) {
   *     return getNamespaceUri(i);
   *   }
   *  }
   *  return null;
   * </pre>
   *
   * <p><strong>Please note:</strong> parser implementations
   * may provide more efifcient lookup, e.g. using a Hashtable.
   * The 'xml' prefix is bound to "http://www.w3.org/XML/1998/namespace", as
   * defined in the
   * <a href="http://www.w3.org/TR/REC-xml-names/#ns-using">Namespaces in XML</a>
   * specification. Analogous, the 'xmlns' prefix is resolved to
   * <a href="http://www.w3.org/2000/xmlns/">http://www.w3.org/2000/xmlns/</a>
   *
   * @see #getNamespaceCount
   * @see #getNamespacePrefix
   * @see #getNamespaceUri
   */
  public String getNamespace (String prefix) {
    if(prefix==null)
      return NAMESPACE_COUNTRIES;
      
    return null;
  }


  // --------------------------------------------------------------------------
  // miscellaneous reporting methods

  /**
   * (XML Pull Parser API) <br />
   * Returns the current depth of the element.
   * Outside the root element, the depth is 0. The
   * depth is incremented by 1 when a start tag is reached.
   * The depth is decremented AFTER the end tag
   * event was observed.
   *
   * <pre>
   * &lt;!-- outside --&gt;     0
   * &lt;root>                  1
   *   sometext                 1
   *     &lt;foobar&gt;         2
   *     &lt;/foobar&gt;        2
   * &lt;/root&gt;              1
   * &lt;!-- outside --&gt;     0
   * </pre>
   */
  public int getDepth() {
    return _currentDepth;
  }

  /**
   * (XML Pull Parser API) <br />
   * Returns a short text describing the current parser state, including
   * the position, a
   * description of the current event and the data source if known.
   * This method is especially useful to provide meaningful
   * error messages and for debugging purposes.
   */
  public String getPositionDescription() {
    try {
      return "Parser state: "+XmlPullParser.TYPES[getEventType()]
        +" input: "+_reader
        +" at: line "+getLineNumber()+" column "+getColumnNumber();
    } catch(Exception e) {
      return "[Error] Unable to get parser state: "+e;
    }
  }


  /**
   * (XML Pull Parser API) <br />
   * Returns the current line number, starting from 1.
   * When the parser does not know the current line number
   * or can not determine it,  -1 is returned (e.g. for WBXML).
   *
   * @return current line number or -1 if unknown.
   */
  public int getLineNumber() {
    return _lineNumber;
  }

  /**
   * (XML Pull Parser API) <br />
   * Returns the current column number, starting from 0.
   * When the parser does not know the current column number
   * or can not determine it,  -1 is returned (e.g. for WBXML).
   *
   * @return current column number or -1 if unknown.
   */
  public int getColumnNumber() {
    return _columnNumber;
  }


  // --------------------------------------------------------------------------
  // TEXT related methods

  /**
   * (XML Pull Parser API) <br />
   * Checks whether the current TEXT event contains only whitespace
   * characters.
   * For IGNORABLE_WHITESPACE, this is always true.
   * For TEXT and CDSECT, false is returned when the current event text
   * contains at least one non-white space character. For any other
   * event type an exception is thrown.
   *
   * <p><b>Please note:</b> non-validating parsers are not
   * able to distinguish whitespace and ignorable whitespace,
   * except from whitespace outside the root element. Ignorable
   * whitespace is reported as separate event, which is exposed
   * via nextToken only.
   *
   */
  public boolean isWhitespace() throws XmlPullParserException {
    return false;
  }

  /**
   * (XML Pull Parser API) <br />
   * Returns the text content of the current event as String.
   * The value returned depends on current event type,
   * for example for TEXT event it is element content
   * (this is typical case when next() is used).
   *
   * See description of nextToken() for detailed description of
   * possible returned values for different types of events.
   *
   * <p><strong>NOTE:</strong> in case of ENTITY_REF, this method returns
   * the entity replacement text (or null if not available). This is
   * the only case where
   * getText() and getTextCharacters() return different values.
   *
   * @see #getEventType
   * @see #next
   * @see #nextToken
   */
  public String getText() {
    return null; // no text in this custom format, only attributes
  }


  /**
   * (XML Pull Parser API) <br />
   * Returns the buffer that contains the text of the current event,
   * as well as the start offset and length relevant for the current
   * event. See getText(), next() and nextToken() for description of possible returned values.
   *
   * <p><strong>Please note:</strong> this buffer must not
   * be modified and its content MAY change after a call to
   * next() or nextToken(). This method will always return the
   * same value as getText(), except for ENTITY_REF. In the case
   * of ENTITY ref, getText() returns the replacement text and
   * this method returns the actual input buffer containing the
   * entity name.
   * If getText() returns null, this method returns null as well and
   * the values returned in the holder array MUST be -1 (both start
   * and length).
   *
   * @see #getText
   * @see #next
   * @see #nextToken
   *
   * @param holderForStartAndLength Must hold an 2-element int array
   * into which the start offset and length values will be written.
   * @return char buffer that contains the text of the current event
   *  (null if the current event has no text associated).
   */
  public char[] getTextCharacters(int [] holderForStartAndLength) {
    holderForStartAndLength[0] = -1;
    holderForStartAndLength[1] = -1;
    return null;
  }

  // --------------------------------------------------------------------------
  // START_TAG / END_TAG shared methods

  /**
   * (XML Pull Parser API) <br />
   * Returns the namespace URI of the current element.
   * The default namespace is represented
   * as empty string.
   * If namespaces are not enabled, an empty String ("") is always returned.
   * The current event must be START_TAG or END_TAG; otherwise,
   * null is returned.
   */
  public String getNamespace() {
    if(_currentEvent==XmlPullParser.START_TAG || _currentEvent==XmlPullParser.END_TAG)
      return NAMESPACE_COUNTRIES;
      
    return null;
  }

  /**
   * (XML Pull Parser API) <br />
   * For START_TAG or END_TAG events, the (local) name of the current
   * element is returned when namespaces are enabled. When namespace
   * processing is disabled, the raw name is returned.
   * For ENTITY_REF events, the entity name is returned.
   * If the current event is not START_TAG, END_TAG, or ENTITY_REF,
   * null is returned.
   * <p><b>Please note:</b> To reconstruct the raw element name
   *  when namespaces are enabled and the prefix is not null,
   * you will need to  add the prefix and a colon to localName..
   *
   */
  public String getName() {
    if(_currentEvent==XmlPullParser.START_TAG || _currentEvent==XmlPullParser.END_TAG)
      return _currentElementName;
      
    return null;
  }

  /**
   * (XML Pull Parser API) <br />
   * Returns the prefix of the current element.
   * If the element is in the default namespace (has no prefix),
   * null is returned.
   * If namespaces are not enabled, or the current event
   * is not  START_TAG or END_TAG, null is returned.
   */
  public String getPrefix() {
    return null;
  }

  /**
   * (XML Pull Parser API) <br />
   * Returns true if the current event is START_TAG and the tag
   * is degenerated
   * (e.g. &lt;foobar/&gt;).
   * <p><b>NOTE:</b> if the parser is not on START_TAG, an exception
   * will be thrown.
   */
  public boolean isEmptyElementTag() throws XmlPullParserException {
    if(_currentEvent!=START_TAG)
      throw new XmlPullParserException("Forbidden call to isEmptyElementTag() outside START_TAG");
      
    if( COUNTRY.equals(_currentElementName) )  
      return true;
      
    return false;
  }

  // --------------------------------------------------------------------------
  // START_TAG Attributes retrieval methods

  /**
   * (XML Pull Parser API) <br />
   * Returns the number of attributes of the current start tag, or
   * -1 if the current event type is not START_TAG
   *
   * @see #getAttributeNamespace
   * @see #getAttributeName
   * @see #getAttributePrefix
   * @see #getAttributeValue
   */
  public int getAttributeCount() {
    return _currentAttributeCount;
  }
  
  private void checkAttributeIndex(int index) {
    if(_currentEvent != XmlPullParser.START_TAG || index<0 || index>=_currentAttributeCount)
      throw new IndexOutOfBoundsException();
  }
  
  /**
   * (XML Pull Parser API) <br />
   * Returns the namespace URI of the attribute
   * with the given index (starts from 0).
   * Returns an empty string ("") if namespaces are not enabled
   * or the attribute has no namespace.
   * Throws an IndexOutOfBoundsException if the index is out of range
   * or the current event type is not START_TAG.
   *
   * <p><strong>NOTE:</strong> if FEATURE_REPORT_NAMESPACE_ATTRIBUTES is set
   * then namespace attributes (xmlns:ns='...') must be reported
   * with namespace
   * <a href="http://www.w3.org/2000/xmlns/">http://www.w3.org/2000/xmlns/</a>
   * (visit this URL for description!).
   * The default namespace attribute (xmlns="...") will be reported with empty namespace.
   * <p><strong>NOTE:</strong>The xml prefix is bound as defined in
   * <a href="http://www.w3.org/TR/REC-xml-names/#ns-using">Namespaces in XML</a>
   * specification to "http://www.w3.org/XML/1998/namespace".
   *
   * @param zero based index of attribute
   * @return attribute namespace,
   *   empty string ("") is returned  if namespaces processing is not enabled or
   *   namespaces processing is enabled but attribute has no namespace (it has no prefix).
   */
  public String getAttributeNamespace (int index) {
    checkAttributeIndex(index);
      
    return XmlPullParser.NO_NAMESPACE;
  }

  /**
   * (XML Pull Parser API) <br />
   * Returns the local name of the specified attribute
   * if namespaces are enabled or just attribute name if namespaces are disabled.
   * Throws an IndexOutOfBoundsException if the index is out of range
   * or current event type is not START_TAG.
   *
   * @param zero based index of attribute
   * @return attribute name (null is never returned)
   */
  public String getAttributeName(int index) {
    checkAttributeIndex(index);
    
    return _currentAttributeName[index];
  }

  /**
   * (XML Pull Parser API) <br />
   * Returns the prefix of the specified attribute
   * Returns null if the element has no prefix.
   * If namespaces are disabled it will always return null.
   * Throws an IndexOutOfBoundsException if the index is out of range
   * or current event type is not START_TAG.
   *
   * @param zero based index of attribute
   * @return attribute prefix or null if namespaces processing is not enabled.
   */
  public String getAttributePrefix(int index) {
    checkAttributeIndex(index);
    
    return null;
  }

  /**
   * (XML Pull Parser API) <br />
   * Returns the type of the specified attribute
   * If parser is non-validating it MUST return CDATA.
   *
   * @param zero based index of attribute
   * @return attribute type (null is never returned)
   */
  public String getAttributeType(int index) {
    checkAttributeIndex(index);
    
    return CDATA;
  }

  /**
   * (XML Pull Parser API) <br />
   * Returns if the specified attribute was not in input was declared in XML.
   * If parser is non-validating it MUST always return false.
   * This information is part of XML infoset:
   *
   * @param zero based index of attribute
   * @return false if attribute was in input
   */
  public boolean isAttributeDefault(int index) {
    checkAttributeIndex(index);
    
    return false;
  }

  /**
   * (XML Pull Parser API) <br />
   * Returns the given attributes value.
   * Throws an IndexOutOfBoundsException if the index is out of range
   * or current event type is not START_TAG.
   *
   * <p><strong>NOTE:</strong> attribute value must be normalized
   * (including entity replacement text if PROCESS_DOCDECL is false) as described in
   * <a href="http://www.w3.org/TR/REC-xml#AVNormalize">XML 1.0 section
   * 3.3.3 Attribute-Value Normalization</a>
   *
   * @see #defineEntityReplacementText
   *
   * @param zero based index of attribute
   * @return value of attribute (null is never returned)
   */
  public String getAttributeValue(int index) {
    checkAttributeIndex(index);
    
    return _currentAttributeValue[index];
  }

  /**
   * (XML Pull Parser API) <br />
   * Returns the attributes value identified by namespace URI and namespace localName.
   * If namespaces are disabled namespace must be null.
   * If current event type is not START_TAG then IndexOutOfBoundsException will be thrown.
   *
   * <p><strong>NOTE:</strong> attribute value must be normalized
   * (including entity replacement text if PROCESS_DOCDECL is false) as described in
   * <a href="http://www.w3.org/TR/REC-xml#AVNormalize">XML 1.0 section
   * 3.3.3 Attribute-Value Normalization</a>
   *
   * @see #defineEntityReplacementText
   *
   * @param namespace Namespace of the attribute if namespaces are enabled otherwise must be null
   * @param name If namespaces enabled local name of attribute otherwise just attribute name
   * @return value of attribute or null if attribute with given name does not exist
   */
  public String getAttributeValue(String namespace, String name) {
    
    if(_currentAttributeValue==null)
      return null;
      
    for(int i=0; i<_currentAttributeCount; i++) {
      if ( namespace==null && name!=null && name.equals(_currentAttributeName[i]) )
        return _currentAttributeValue[i];
    }
    
    return null;
  }

  // --------------------------------------------------------------------------
  // actual parsing methods

  /**
   * (XML Pull Parser API) <br />
   * Returns the type of the current event (START_TAG, END_TAG, TEXT, etc.)
   *
   * @see #next()
   * @see #nextToken()
   */
  public int getEventType() throws XmlPullParserException {
    return _currentEvent;
  }

  /**
   * (XML Pull Parser API) <br />
   * Get next parsing event - element content wil be coalesced and only one
   * TEXT event must be returned for whole element content
   * (comments and processing instructions will be ignored and emtity references
   * must be expanded or exception mus be thrown if entity reerence can not be exapnded).
   * If element content is empty (content is "") then no TEXT event will be reported.
   *
   * <p><b>NOTE:</b> empty element (such as &lt;tag/>) will be reported
   *  with  two separate events: START_TAG, END_TAG - it must be so to preserve
   *   parsing equivalency of empty element to &lt;tag>&lt;/tag>.
   *  (see isEmptyElementTag ())
   *
   * @see #isEmptyElementTag
   * @see #START_TAG
   * @see #TEXT
   * @see #END_TAG
   * @see #END_DOCUMENT
   */
  public int next() throws XmlPullParserException, IOException {
    
    final int previousEvent = _currentEvent;
    
    switch(previousEvent) {
      case XmlPullParser.START_DOCUMENT:
        _currentElementName = "file";
        _currentAttributeCount = 1;
        _currentAttributeName[0] = "path";
        _currentAttributeValue[0] = "test"; // NOTA: mysteriously missing in output
        _currentDepth++;
        _currentEvent = XmlPullParser.START_TAG;
        break;
      
      case XmlPullParser.START_TAG:
        // Previous: either <file> or previous <country>
        
        // read following country
        _currentAttributeCount = 0;
        _columnNumber = 0;
        _lineNumber++;
        
        _isAnotherCountryAvailable = false;
        int token;
        do {
          token = _reader.read();
          _columnNumber++;
          
          switch(token) {
            case '\n':
            case -1:
              if(_currentAttributeCount==2) {
                _currentAttributeName[_currentAttributeCount] = CONTINENT;
                _currentAttributeValue[_currentAttributeCount] = _buffer.toString();
                _buffer.setLength(0);
                _currentAttributeCount++;
              }
              // else 
                // empty file or error
              break;
            case ',':
              switch(_currentAttributeCount) {
                case 0:
                  _currentAttributeName[_currentAttributeCount] = NAME;
                  break;
                case 1:
                  _currentAttributeName[_currentAttributeCount] = CAPITAL_CITY;
                  break;
                default:
                  // TODO: handle error
              }
              _currentAttributeValue[_currentAttributeCount] = _buffer.toString();
              _buffer.setLength(0);
              _currentAttributeCount++;
              break;
            default:
              _isAnotherCountryAvailable = true;
              _buffer.append((char)token);
          }
        } while(token!=-1 && token!='\n');
        
        // TODO: simplify
        if(token==-1) {
          // end of file
          _endOfFileReached = true;
          _currentEvent = XmlPullParser.END_TAG; // </file> or before last </country>
        }
        else {
          if(_currentDepth==1) { // root
            _currentDepth++;
            _currentElementName = COUNTRY;
            _currentEvent = XmlPullParser.START_TAG; // <country (...)>
          } else {
            _currentEvent = XmlPullParser.END_TAG; // </country> before new <country (...)>
          }
        }
        break;
        
      case XmlPullParser.END_TAG:
        if (_endOfFileReached) {
          // </file> or before last </country> or last </country>
          if (_currentDepth==2) { 
            if (_isAnotherCountryAvailable) {// before last country
              _currentEvent = XmlPullParser.START_TAG; // one to go
            } else {
              _currentDepth--;
              _currentEvent = XmlPullParser.END_TAG; // </file> 
            }
          } else {
            _currentDepth--;
            _currentEvent = XmlPullParser.END_DOCUMENT;
          }
        } else {
          // </country> before new <country (...)>
          _currentEvent = XmlPullParser.START_TAG;
        }
        
        break;
    }
    
    return _currentEvent;
  }


  /**
   * (XML Pull Parser API) <br />
   * This method works similarly to next() but will expose
   * additional event types (COMMENT, CDSECT, DOCDECL, ENTITY_REF, PROCESSING_INSTRUCTION, or
   * IGNORABLE_WHITESPACE) if they are available in input.
   *
   * <p>If special feature
   * <a href="http://xmlpull.org/v1/doc/features.html#xml-roundtrip">FEATURE_XML_ROUNDTRIP</a>
   * (identified by URI: http://xmlpull.org/v1/doc/features.html#xml-roundtrip)
   * is enabled it is possible to do XML document round trip ie. reproduce
   * exectly on output the XML input using getText():
   * returned content is always unnormalized (exactly as in input).
   * Otherwise returned content is end-of-line normalized as described
   * <a href="http://www.w3.org/TR/REC-xml#sec-line-ends">XML 1.0 End-of-Line Handling</a>
   * and. Also when this feature is enabled exact content of START_TAG, END_TAG,
   * DOCDECL and PROCESSING_INSTRUCTION is available.
   *
   * <p>Here is the list of tokens that can be  returned from nextToken()
   * and what getText() and getTextCharacters() returns:<dl>
   * <dt>START_DOCUMENT<dd>null
   * <dt>END_DOCUMENT<dd>null
   * <dt>START_TAG<dd>null unless FEATURE_XML_ROUNDTRIP
   *   enabled and then returns XML tag, ex: &lt;tag attr='val'>
   * <dt>END_TAG<dd>null unless FEATURE_XML_ROUNDTRIP
   *  id enabled and then returns XML tag, ex: &lt;/tag>
   * <dt>TEXT<dd>return element content.
   *  <br>Note: that element content may be delivered in multiple consecutive TEXT events.
   * <dt>IGNORABLE_WHITESPACE<dd>return characters that are determined to be ignorable white
   * space. If the FEATURE_XML_ROUNDTRIP is enabled all whitespace content outside root
   * element will always reported as IGNORABLE_WHITESPACE otherise rteporting is optional.
   *  <br>Note: that element content may be delevered in multiple consecutive IGNORABLE_WHITESPACE events.
   * <dt>CDSECT<dd>
   * return text <em>inside</em> CDATA
   *  (ex. 'fo&lt;o' from &lt;!CDATA[fo&lt;o]]>)
   * <dt>PROCESSING_INSTRUCTION<dd>
   *  if FEATURE_XML_ROUNDTRIP is true
   *  return exact PI content ex: 'pi foo' from &lt;?pi foo?>
   *  otherwise it may be exact PI content or concatenation of PI target,
   * space and data so for example for
   *   &lt;?target    data?> string &quot;target data&quot; may
   *       be returned if FEATURE_XML_ROUNDTRIP is false.
   * <dt>COMMENT<dd>return comment content ex. 'foo bar' from &lt;!--foo bar-->
   * <dt>ENTITY_REF<dd>getText() MUST return entity replacement text if PROCESS_DOCDECL is false
   * otherwise getText() MAY return null,
   * additionally getTextCharacters() MUST return entity name
   * (for example 'entity_name' for &amp;entity_name;).
   * <br><b>NOTE:</b> this is the only place where value returned from getText() and
   *   getTextCharacters() <b>are different</b>
   * <br><b>NOTE:</b> it is user responsibility to resolve entity reference
   *    if PROCESS_DOCDECL is false and there is no entity replacement text set in
   *    defineEntityReplacementText() method (getText() will be null)
   * <br><b>NOTE:</b> character entities (ex. &amp;#32;) and standard entities such as
   *  &amp;amp; &amp;lt; &amp;gt; &amp;quot; &amp;apos; are reported as well
   *  and are <b>not</b> reported as TEXT tokens but as ENTITY_REF tokens!
   *  This requirement is added to allow to do roundtrip of XML documents!
   * <dt>DOCDECL<dd>
   * if FEATURE_XML_ROUNDTRIP is true or PROCESS_DOCDECL is false
   * then return what is inside of DOCDECL for example it returns:<pre>
   * &quot; titlepage SYSTEM "http://www.foo.bar/dtds/typo.dtd"
   * [&lt;!ENTITY % active.links "INCLUDE">]&quot;</pre>
   * <p>for input document that contained:<pre>
   * &lt;!DOCTYPE titlepage SYSTEM "http://www.foo.bar/dtds/typo.dtd"
   * [&lt;!ENTITY % active.links "INCLUDE">]></pre>
   * otherwise if FEATURE_XML_ROUNDTRIP is false and PROCESS_DOCDECL is true
   *    then what is returned is undefined (it may be even null)
   * </dd>
   * </dl>
   *
   * <p><strong>NOTE:</strong> there is no gurantee that there will only one TEXT or
   * IGNORABLE_WHITESPACE event from nextToken() as parser may chose to deliver element content in
   * multiple tokens (dividing element content into chunks)
   *
   * <p><strong>NOTE:</strong> whether returned text of token is end-of-line normalized
   *  is depending on FEATURE_XML_ROUNDTRIP.
   *
   * <p><strong>NOTE:</strong> XMLDecl (&lt;?xml ...?&gt;) is not reported but its content
   * is available through optional properties (see class description above).
   *
   * @see #next
   * @see #START_TAG
   * @see #TEXT
   * @see #END_TAG
   * @see #END_DOCUMENT
   * @see #COMMENT
   * @see #DOCDECL
   * @see #PROCESSING_INSTRUCTION
   * @see #ENTITY_REF
   * @see #IGNORABLE_WHITESPACE
   */
  public int nextToken() throws XmlPullParserException, IOException {
    return next();
  }

  //-----------------------------------------------------------------------------
  // utility methods to make XML parsing easier ...

  /**
   * (XML Pull Parser API) <br />
   * Test if the current event is of the given type and if the
   * namespace and name do match. null will match any namespace
   * and any name. If the test is not passed, an exception is
   * thrown. The exception text indicates the parser position,
   * the expected event and the current event that is not meeting the
   * requirement.
   *
   * <p>Essentially it does this
   * <pre>
   *  if (type != getEventType()
   *  || (namespace != null &amp;&amp;  !namespace.equals( getNamespace () ) )
   *  || (name != null &amp;&amp;  !name.equals( getName() ) ) )
   *     throw new XmlPullParserException( "expected "+ TYPES[ type ]+getPositionDescription());
   * </pre>
   */
  public void require(int type, String namespace, String name) 
      throws XmlPullParserException, IOException {
    
    if (
          type != getEventType()
        || (namespace != null &&  !namespace.equals( getNamespace () ) )
        || (name != null &&  !name.equals( getName() ) ) 
       )
      throw new XmlPullParserException( "expected "+ XmlPullParser.TYPES[ type ]+getPositionDescription());
  }

  /**
   * (XML Pull Parser API) <br />
   * If current event is START_TAG then if next element is TEXT then element content is returned
   * or if next event is END_TAG then empty string is returned, otherwise exception is thrown.
   * After calling this function successfully parser will be positioned on END_TAG.
   *
   * <p>The motivation for this function is to allow to parse consistently both
   * empty elements and elements that has non empty content, for example for input: <ol>
   * <li>&lt;tag&gt;foo&lt;/tag&gt;
   * <li>&lt;tag&gt;&lt;/tag&gt; (which is equivalent to &lt;tag/&gt;
   * both input can be parsed with the same code:
   * <pre>
   *   p.nextTag()
   *   p.requireEvent(p.START_TAG, "", "tag");
   *   String content = p.nextText();
   *   p.requireEvent(p.END_TAG, "", "tag");
   * </pre>
   * This function together with nextTag make it very easy to parse XML that has
   * no mixed content.
   *
   *
   * <p>Essentially it does this
   * <pre>
   *  if(getEventType() != START_TAG) {
   *     throw new XmlPullParserException(
   *       "parser must be on START_TAG to read next text", this, null);
   *  }
   *  int eventType = next();
   *  if(eventType == TEXT) {
   *     String result = getText();
   *     eventType = next();
   *     if(eventType != END_TAG) {
   *       throw new XmlPullParserException(
   *          "event TEXT it must be immediately followed by END_TAG", this, null);
   *      }
   *      return result;
   *  } else if(eventType == END_TAG) {
   *     return "";
   *  } else {
   *     throw new XmlPullParserException(
   *       "parser must be on START_TAG or TEXT to read text", this, null);
   *  }
   * </pre>
   */
  public String nextText() throws XmlPullParserException, IOException {
    
    if(getEventType() != XmlPullParser.START_TAG) {
       throw new XmlPullParserException(
         "parser must be on START_TAG to read next text", this, null);
    }
    int eventType = next();
    if(eventType == XmlPullParser.TEXT) {
       String result = getText();
       eventType = next();
       if(eventType != XmlPullParser.END_TAG) {
         throw new XmlPullParserException(
            "event TEXT it must be immediately followed by END_TAG", this, null);
        }
        return result;
    } else if(eventType == XmlPullParser.END_TAG) {
       return "";
    } else {
       throw new XmlPullParserException(
         "parser must be on START_TAG or TEXT to read text", this, null);
    }
  }

  /**
   * (XML Pull Parser API) <br />
   * Call next() and return event if it is START_TAG or END_TAG
   * otherwise throw an exception.
   * It will skip whitespace TEXT before actual tag if any.
   *
   * <p>essentially it does this
   * <pre>
   *   int eventType = next();
   *   if(eventType == TEXT &amp;&amp;  isWhitespace()) {   // skip whitespace
   *      eventType = next();
   *   }
   *   if (eventType != START_TAG &amp;&amp;  eventType != END_TAG) {
   *      throw new XmlPullParserException("expected start or end tag", this, null);
   *   }
   *   return eventType;
   * </pre>
   */
  public int nextTag() throws XmlPullParserException, IOException {
    
    int eventType = next();
    if(eventType == XmlPullParser.TEXT &&  isWhitespace()) {   // skip whitespace
      eventType = next();
    }
    if (eventType != XmlPullParser.START_TAG &&  eventType != XmlPullParser.END_TAG) {
      throw new XmlPullParserException("expected start or end tag", this, null);
    }
    return eventType;
  }
  
}