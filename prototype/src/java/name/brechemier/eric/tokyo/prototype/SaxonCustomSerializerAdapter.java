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

import com.icl.saxon.output.ContentHandlerProxy;
import java.io.Writer;
import name.brechemier.eric.tokyo.prototype.SAXSerializerAdapter;

/**
 * (Tokyo Project prototype) <br />
 * Adapter for Saxon XSLT processor Serializer 
 * to plug a SAX2 ContentHandler and access the output document properties <br />
 * This adapter handles only text output, and manages a single hardcoded class of ContentHandler,
 * responding to the simple needs of this prototype. 
 * A more generic adapter should read these parameters from a property file.
 *
 * @author Eric Bréchemier
 *
 */
public class SaxonCustomSerializerAdapter extends ContentHandlerProxy {
  
  /**
   * Determine whether the Emitter wants a Writer for character output 
   * or an OutputStream for binary output.
   */
  public boolean usesWriter() {
    return true;
  }
  
  /**
   * Set the output destination as a character stream
   */
  public void setWriter(Writer writer) {
    super.setWriter(writer);
    
    try {
      setUnderlyingContentHandler(new SAXSerializerAdapter(writer) );
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
}