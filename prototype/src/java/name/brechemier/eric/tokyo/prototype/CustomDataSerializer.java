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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

  // reuse constants for potential optimizing (instance equality instead of string comparizon)
  // NOTA: with Saxon, instances do not match - does not seem to keep input instances
import name.brechemier.eric.tokyo.prototype.CustomDataParser; 

import org.xmlpull.v1.XmlSerializer;

/**
 * (Tokyo Project prototype) <br />
 * Custom data serializer to handle output of XSLT processor <br />
 * Implements the SAX2 ContentHandler interface
 *
 * @author Eric Bréchemier
 *
 */
public class CustomDataSerializer implements XmlSerializer {
  
  private Writer _writer;
  private boolean _isFirstCountry;
  
  private void reset() {
    _isFirstCountry = true;
    _currentDepth=0;
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   * Set feature identified by name (recommended to be URI for uniqueness).
   * Some well known optional features are defined in
   * <a href="http://www.xmlpull.org/v1/doc/features.html">
   * http://www.xmlpull.org/v1/doc/features.html</a>.
   *
   * If feature is not recocgnized or can not be set
   * then IllegalStateException MUST be thrown.
   *
   * @exception IllegalStateException If the feature is not supported or can not be set
   */
  public void setFeature(String name, boolean state)
    throws IllegalArgumentException, IllegalStateException {
    
    if(name==null)
      throw new IllegalArgumentException();
      
    throw new IllegalStateException();
  }
  
  
  /**
   * (XML Pull Serializer API) <br/>
   * Return the current value of the feature with given name.
   * <p><strong>NOTE:</strong> unknown properties are <strong>always</strong> returned as null
   *
   * @param name The name of feature to be retrieved.
   * @return The value of named feature.
   * @exception IllegalArgumentException if feature string is null
   */
  public boolean getFeature(String name) {
    return false;
  }
  
  
  /**
   * (XML Pull Serializer API) <br/>
   * Set the value of a property.
   * (the property name is recommened to be URI for uniqueness).
   * Some well known optional properties are defined in
   * <a href="http://www.xmlpull.org/v1/doc/properties.html">
   * http://www.xmlpull.org/v1/doc/properties.html</a>.
   *
   * If property is not recocgnized or can not be set
   * then IllegalStateException MUST be thrown.
   *
   * @exception IllegalStateException if the property is not supported or can not be set
   */
  public void setProperty(String name, Object value)
    throws IllegalArgumentException, IllegalStateException {
    
    if(name==null)
      throw new IllegalArgumentException();
      
    throw new IllegalStateException();
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   * Look up the value of a property.
   *
   * The property name is any fully-qualified URI. I
   * <p><strong>NOTE:</strong> unknown properties are <string>always</strong> returned as null
   *
   * @param name The name of property to be retrieved.
   * @return The value of named property.
   */
  public Object getProperty(String name) {
    return null;
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   * Set to use binary output stream with given encoding.
   */
  public void setOutput (OutputStream os, String encoding)
    throws IOException, IllegalArgumentException, IllegalStateException {
    
    reset();
    
    // TODO: change to use binary outpustream
    _writer = new OutputStreamWriter(os,encoding);
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   * Set the output to the given writer.
   * <p><b>WARNING</b> no information about encoding is available!
   */
  public void setOutput (Writer writer)
    throws IOException, IllegalArgumentException, IllegalStateException {
    
    reset();
    _writer = writer;
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   * Write &lt;&#63;xml declaration with encoding (if encoding not null)
   * and standalone flag (if standalone not null)
   * This method can only be called just after setOutput.
   */
  public void startDocument (String encoding, Boolean standalone)
    throws IOException, IllegalArgumentException, IllegalStateException {
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   * Finish writing. All unclosed start tags will be closed and output
   * will be flushed. After calling this method no more output can be
   * serialized until next call to setOutput()
   */
  public void endDocument ()
    throws IOException, IllegalArgumentException, IllegalStateException {
    
    _writer.close();
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   * Binds the given prefix to the given namespace.
   * This call is valid for the next element including child elements.
   * The prefix and namespace MUST be always declared even if prefix
   * is not used in element (startTag() or attribute()) - for XML 1.0
   * it must result in declaring <code>xmlns:prefix='namespace'</code>
   * (or <code>xmlns:prefix="namespace"</code> depending what character is used
   * to quote attribute value).
   *
   * <p><b>NOTE:</b> this method MUST be called directly before startTag()
   *   and if anything but startTag() or setPrefix() is called next there will be exception.
   * <p><b>NOTE:</b> prefixes "xml" and "xmlns" are already bound
   *   and can not be redefined see:
   * <a href="http://www.w3.org/XML/xml-names-19990114-errata#NE05">Namespaces in XML Errata</a>.
   * <p><b>NOTE:</b> to set default namespace use as prefix empty string.
   *
   * @param prefix must be not null (or IllegalArgumentException is thrown)
   * @param namespace must be not null
   */
  public void setPrefix (String prefix, String namespace)
    throws IOException, IllegalArgumentException, IllegalStateException {
    
    // ignored
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   * Return namespace that corresponds to given prefix
   * If there is no prefix bound to this namespace return null
   * but if generatePrefix is false then return generated prefix.
   *
   * <p><b>NOTE:</b> if the prefix is empty string "" and defualt namespace is bound
   * to this prefix then empty string ("") is returned.
   *
   * <p><b>NOTE:</b> prefixes "xml" and "xmlns" are already bound
   *   will have values as defined
   * <a href="http://www.w3.org/TR/REC-xml-names/">Namespaces in XML specification</a>
   */
  public String getPrefix (String namespace, boolean generatePrefix)
    throws IllegalArgumentException {
    
    // setPrefix ignored
    return null;
  }
  
  
  private int _currentDepth;
  /**
   * (XML Pull Serializer API) <br/>
   * Returns the current depth of the element.
   * Outside the root element, the depth is 0. The
   * depth is incremented by 1 when startTag() is called.
   * The depth is decremented after the call to endTag()
   * event was observed.
   *
   * <pre>
   * &lt;!-- outside --&gt;     0
   * &lt;root&gt;               1
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
  
  private String _currentNamespace = null;
  /**
   * (XML Pull Serializer API) <br/>
   * Returns the namespace URI of the current element as set by startTag().
   *
   * <p><b>NOTE:</b> that measn in particaulr that: <ul>
   * <li>if there was startTag("", ...) then getNamespace() returns ""
   * <li>if there was startTag(null, ...) then getNamespace() returns null
   * </ul>
   *
   * @return namespace set by startTag() that is currently in scope
   */
  public String getNamespace() {
    return _currentNamespace;
  }
  
  private String _currentElementName = null;
  /**
   * (XML Pull Serializer API) <br/>
   * Returns the name of the current element as set by startTag().
   * It can only be null before first call to startTag()
   * or when last endTag() is called to close first startTag().
   *
   * @return namespace set by startTag() that is currently in scope
   */
  public String getName() {
    if( getDepth()==0)
      return null;
    
    return _currentElementName;
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   * Writes a start tag with the given namespace and name.
   * If there is no prefix defined for the given namespace,
   * a prefix will be defined automatically.
   * The explicit prefixes for namespaces can be established by calling setPrefix()
   * immediately before this method.
   * If namespace is null no namespace prefix is printed but just name.
   * If namespace is empty string then serialzier will make sure that
   * default empty namespace is declared (in XML 1.0 xmlns='')
   * or throw IllegalStateException if default namespace is already bound
   * to non-empty string.
   */
  public XmlSerializer startTag(String namespace, String name)
    throws IOException, IllegalArgumentException, IllegalStateException {
      
    _currentElementName = name;
    _currentNamespace = namespace;
    _currentDepth++;
    
    if(_currentDepth==2) { // country
      if(_isFirstCountry) {
        _isFirstCountry = false;
      } else {
        _writer.write('\n');
      }
    }
    
    return this;
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   * Write an attribute. Calls to attribute() MUST follow a call to
   * startTag() immediately. If there is no prefix defined for the
   * given namespace, a prefix will be defined automatically.
   * If namespace is null or empty string
   * no namespace prefix is printed but just name.
   */
  public XmlSerializer attribute(String namespace, String name, String value)
    throws IOException, IllegalArgumentException, IllegalStateException {
    
    if ( name.equals(CustomDataParser.NAME) ) {
      _writer.write(value+',');
    }
    else if ( name.equals(CustomDataParser.CAPITAL_CITY) ) {
      _writer.write(value+',');
    }
    else if ( name.equals(CustomDataParser.CONTINENT) ) {
      _writer.write(value);
    }
    
    return this;
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   * Write end tag. Repetition of namespace and name is just for avoiding errors.
   * <p><b>Background:</b> in kXML endTag had no arguments, and non matching tags were
   *  very difficult to find...
   * If namespace is null no namespace prefix is printed but just name.
   * If namespace is empty string then serialzier will make sure that
   * default empty namespace is declared (in XML 1.0 xmlns='').
   */
  public XmlSerializer endTag(String namespace, String name)
    throws IOException, IllegalArgumentException, IllegalStateException {
    
    _currentElementName = name;
    _currentNamespace = namespace;
    _currentDepth--;
      
    return this;
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   * Writes text, where special XML chars are escaped automatically
   */
  public XmlSerializer text (String text)
    throws IOException, IllegalArgumentException, IllegalStateException {
      
    return this;
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   * Writes text, where special XML chars are escaped automatically
   */
  public XmlSerializer text(char [] buf, int start, int len)
    throws IOException, IllegalArgumentException, IllegalStateException {
      
    return this;
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   */
  public void cdsect (String text)
    throws IOException, IllegalArgumentException, IllegalStateException {
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   */
  public void entityRef (String text)  throws IOException,
    IllegalArgumentException, IllegalStateException {
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   */
  public void processingInstruction (String text)
    throws IOException, IllegalArgumentException, IllegalStateException {
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   */
  public void comment (String text)
    throws IOException, IllegalArgumentException, IllegalStateException {
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   */
  public void docdecl (String text)
    throws IOException, IllegalArgumentException, IllegalStateException {
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   */
  public void ignorableWhitespace (String text)
    throws IOException, IllegalArgumentException, IllegalStateException {
  }
  
  /**
   * (XML Pull Serializer API) <br/>
   * Write all pending output to the stream.
   * If method startTag() or attribute() was called then start tag is closed (final &gt;)
   * before flush() is called on underlying output stream.
   *
   * <p><b>NOTE:</b> if there is need to close start tag
   * (so no more attribute() calls are allowed) but without flushinging output
   * call method text() with empty string (text("")).
   *
   */
  public void flush () throws IOException {
    _writer.flush();
  }

}