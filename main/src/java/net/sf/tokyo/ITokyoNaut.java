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
 * Methods inTouch() and send() are called in turn to check whether destination TokyoNaut 
 * accepts the communication, and then have updated meta and data buffers transmitted.
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
   * Check whether TokyoNaut is available for communication.<br/>
   *
   * <p>
   * This method provide a simple means to control the reading process step by step.
   * The following loop will run until reading is complete:
<pre>
int[] meta = {1,0,0,0,0};
byte[] data = new byte[500];

while( tokyoNaut.inTouch() )
{
  tokyoNaut.send(meta,data);
} 
</pre>
   * </p>
   *
   * @return true when the TokyoNaut accepts to receive meta/data, false meaning the end
   */
  public boolean inTouch();
  
  /**
   * Send meta/data through this TokyoNaut.<br/>
   *
   * <p>
   * Allows to send data (a buffer of bytes) and associated meta information (an array of
   * integer attributes such as offset and length of the buffer, type and relative level of the
   * corresponding node in the document tree).
   * </p>
   * 
   * <p>
   * As a default behavior to allow chains of actions, TokyoNauts are expected to forward
   * received meta/data to their configured destination TokyoNaut (if any). In addition,
   * their processing must be independent of the actual Class of the destination TokyoNaut.
   * </p>
   *
   * <p>
   * Besides this default behavior, each TokyoNaut can provide a basic service by executing
   * an atomic action on received meta/data before forwarding it.
   * </p>
   *
   * <p>
   * Below is the definition of fields for version 0x01 set in leading byte.
   * Versions 0x00-0x9F are "reserved" for different versions of this specification, 
   * while versions 0xA0-0xFF are "user-defined".
   * Implementors wishing to define their own set of fields MUST signal it with
   * a leading version in the "user-defined" range.
   * </p>
   *
   * <p>
   * <table border="1">
   *   <tr>
   *     <th>0 - Version</th>
   *     <th>1 - Item Level</th>
   *     <th>2 - Item Type</th>
   *     <th>3 - Offset</th>
   *     <th>4 - Length</th>
   *   </tr>
   *   <tr>
   *     <td>0x01</td>
   *     <td>relative level increment for current data item
   *       <ul>
   *         <li>+1 for child</li>
   *         <li>-1 for parent</li>
   *         <li>0 for continuation of current node's data</li>
   *       </ul>
   *      more generally,
   *       <ul>
   *         <li>positive value +i for i-th descendant (limited to +1 in this version)</li>
   *         <li>negative value -i for i-th ancestor</li>
   *         <li>0 for current item</li>
   *       </ul>
   *     </td>
   *     <td>type of data item, e.g. XML node type.</td>
   *     <td>starting position of data item storage</td>
   *     <td>number of bytes expected, changed to number of bytes actually written</td>
   *   </tr>
   * </table>
   * </p>
   *
   * <p>
   * <table border="1">
   *   <tr>
   *     <th>
   *     XML Node Types
   *     </th>
   *   </tr>
   *   <tr>
   *     <td>
   *       <ul>
   *         <li><b>0x10 - Root Node</b></li>
   *         <li><b>0x20 - Element Node</b></li>
   *         <li>&nbsp;&nbsp;0x21 - Element Node - prefix</li>
   *         <li>&nbsp;&nbsp;0x22 - Element Node - local part of name</li>
   *         <li><b>0x30 - Attribute Node</b></li>
   *         <li>&nbsp;&nbsp;0x31 - Attribute Node - prefix</li>
   *         <li>&nbsp;&nbsp;0x32 - Attribute Node - local part of name</li>
   *         <li>&nbsp;&nbsp;0x33 - Attribute Node - string-value</li>
   *         <li><b>0x40 - Namespace Node (omitted for default namespace: without prefix)</b></li>
   *         <li>&nbsp;&nbsp;0x41 - Namespace Node - prefix (= local part of name)</li>
   *         <li>&nbsp;&nbsp;0x42 - Namespace Node - namespace URI (= string-value)</li>
   *         <li><b>0x50 - Processing Instruction Node</b></li>
   *         <li>&nbsp;&nbsp;0x51 - Processing Instruction Node - target (= local part of name)</li>
   *         <li>&nbsp;&nbsp;0x52 - Processing Instruction Node - string-value</li>
   *         <li><b>0x60 - Comment Node (omitted)</b></li>
   *         <li>&nbsp;&nbsp;0x61 - Comment Node - string-value</li>
   *         <li><b>0x70 - Text Node (omitted)</b></li>
   *         <li>&nbsp;&nbsp;0x71 - Text Node - string-value</li>
   *       </ul>
   *     </td>
   *   </tr>
   * </table>
   * </p>
   *
   * <p>
   * Behind the concept of relative level is the idea of a stack representing the path
   * of the current node. This path can be computed by appling the following steps based
   * on the relative level value:
   * 
   * <ul>
   *   <li>+1: push the new node on stack</li>
   *   <li>-1: pop 1 node from the stack</li>
   *   <li>-i: pop repeatedly i nodes from the stack</li>
   *   <li>0: keep path unchanged (corresponds to more data for the same node)</li>
   * </ul>
   * </p>
   * 
   * <p>
   * Based on <a href="http://www.w3.org/TR/xml">XML 1.0</a> and 
   * <a href="http://www.w3.org/TR/xml-names">Namespaces in XML 1.0</a>, 
   * the following EBNF grammar describes the set of send() sequences representing "well-formed" XML documents.
   * Compared with the original EBNF grammar for XML, this specification replaces the textual syntax
   * of nodes with the corresponding calls of the send() method. Original rules keep the same numbering,
   * prefixed with 'XML.' for XML and 'NS.' for XML Namespaces; modified or new rules are prefixed with 'TOKYO.'.
   * Comments start with '//'. The XML view provided by this specification is based on XML Logical Structures,
   * and silently omits prolog and document type declarations, CDATA markers, as well as character and entity
   * references, entity declarations, or syntactic details such as the use of single or double quote for attributes
   * or use of empty element tag instead of start and end tags.
   * For readability, long productions are written on several lines and end with an empty line.
<PRE>
////////////////////// Document Root Node //////////////////////
[TOKYO.XML.1] document ::= Misc* element Misc*

[XML.27] Misc ::= Comment | PI | S


////////////////////// Comment Node //////////////////////
[TOKYO.XML.15] Comment ::= 
  send({1,1,0x61,offset,length},{CommentValue});
  send({1,-1,0x61,0,0},{});

[TOKYO.15_a] CommentValue ::= ((Char - '-') | ('-' (Char - '-')))*
// the string "--" MUST NOT occur within text of comments


/////////////// Processing Instruction Node ///////////////
[TOKYO.XML.16] PI ::=
  send({1,1,0x50,0,0},{});
    PITarget
    PIValue
  send({1,-1,0x50,0,0},{});

[TOKYO.XML.17_a] PITarget ::=
  send({1,1,0x51,0,0},{});
    PITargetName
  send({1,-1,0x51,0,0},{});

[TOKYO.XML.17_b] PITargetName ::= PIName - (('X' | 'x') ('M' | 'm') ('L' | 'l'))
// in a namespace-well-formed document, no processing instruction targets contain any colons
[TOKYO.XML.5_a] PIName ::= (Letter | '_' ) (PINameChar)*
[TOKYO.XML.4_a] PINameChar ::= Letter | Digit | '.' | '-' | '_' | CombiningChar | Extender

[TOKYO.XML.16_a] PIValue ::=
  send({1,1,0x52,offset,length},{PIValueChars});
  send({1,-1,0x52,0,0},{});

[TOKYO.XML.16_b] PIValueChars ::= (Char* - (Char* '?>' Char*))    
    

////////////////////// Element Node //////////////////////

[TOKYO.XML.39] element ::= STag content ETag

[TOKYO.NS.12] STag ::=
  send({1,1,0x20,0,0},{});
    ElementQName
    Namespace*
    Attribute*

[TOKYO.XML.42] ETag ::= 
  send({1,-1,0x20,0,0},{});
    
[TOKYO.NS.7_a] ElementQName ::= ElementPrefix? ElementLocalPart
[TOKYO.NS.10_a] ElementPrefix ::=
  send({1,1,0x21,offset,length},{NCName});
  send({1,-1,0x21,0,0},{});

[TOKYO.NS.11_a] ElementLocalPart ::=
  send({1,1,0x22,offset,length},{NCName});
  send({1,-1,0x22,0,0},{});
  
[TOKYO.XML.43] content ::= ExpandedText? ((element | PI | Comment) ExpandedText?)*



////////////////////// Attribute Node //////////////////////

[TOKYO.NS.15] Attribute	::= 
  send({1,1,0x30,0,0},{});
    AttributeQName 
    AttValue
  send({1,-1,0x30,0,0},{});
  
[TOKYO.NS.7_b] AttributeQName ::= AttributePrefix? AttributeLocalPart
[TOKYO.NS.10_b] AttributePrefix ::=
  send({1,1,0x31,offset,length},{NCName});
  send({1,-1,0x31,0,0},{});

[TOKYO.NS.11_a] AttributeLocalPart ::=
  send({1,1,0x32,offset,length},{NCName});
  send({1,-1,0x32,0,0},{});

[TOKYO.XML.10] AttValue ::=
  send({1,1,0x33,offset,length},{NormalizedAttributeValue});
  send({1,-1,0x33,0,0},{});

[TOKYO.XML.10a] NormalizedAttributeValue ::= ( [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF] )*  
// (Char - (#x9 | #xA | #xD) )*

  
/////////////////////// Namespace Node ///////////////////////

[TOKYO.NS.1_a] Namespace ::= PrefixedNamespace | DefaultNamespace

[TOKYO.NS.2_a] PrefixedNamespace ::= 
  send({1,1,0x40,0,0},{});
    NamespacePrefix 
    NamespaceURI
  send({1,-1,0x40,0,0},{});

[TOKYO.NS.3_a] DefaultNamespace ::= NamespaceURI

[TOKYO.NS.2_a] NamespacePrefix ::= 
  send({1,1,0x41,0,0},{NCName});
  send({1,-1,0x41,0,0},{});

[TOKYO.NS.2_a] NamespaceURI ::= 
  send({1,1,0x42,0,0},{NCName});
  send({1,-1,0x42,0,0},{});


////////////////////////// Whitespace/Text node ///////////////////////

[TOKYO.XML.3] S	::=
  send({1,1,0x71,offset,length},{WhitespaceChars});
  send({1,-1,0x71,0,0},{});
  
[TOKYO.XML.3_a] WhitespaceChars ::= (#x20 | #x9 | #xD | #xA)+

[TOKYO.XML.14_a] ExpandedText ::=
  send({1,1,0x71,offset,length},{ExpandedCharData});
  send({1,-1,0x71,0,0},{});
  
[TOKYO.XML.14_b] ExpandedCharData ::= Char*
// '<', '&' and ']]>' can appear in text node after entity expansion


/////////////////////////// Characters ////////////////////////////

[NS.4] NCName ::= NCNameStartChar NCNameChar*	// An XML Name, minus the ":"
[NS.5] NCNameChar ::= NameChar - ':'
[NS.6] NCNameStartChar ::= Letter | '_'

[XML.84] Letter	::= BaseChar | Ideographic  

[XML.88] Digit ::= 
    [#x0030-#x0039] | [#x0660-#x0669] | [#x06F0-#x06F9] | [#x0966-#x096F] | [#x09E6-#x09EF] 
  | [#x0A66-#x0A6F] | [#x0AE6-#x0AEF] | [#x0B66-#x0B6F] | [#x0BE7-#x0BEF] | [#x0C66-#x0C6F] 
  | [#x0CE6-#x0CEF] | [#x0D66-#x0D6F] | [#x0E50-#x0E59] | [#x0ED0-#x0ED9] | [#x0F20-#x0F29]   

[XML.85] BaseChar ::= 
    [#x0041-#x005A] | [#x0061-#x007A] | [#x00C0-#x00D6] | [#x00D8-#x00F6] | [#x00F8-#x00FF] 
  | [#x0100-#x0131] | [#x0134-#x013E] | [#x0141-#x0148] | [#x014A-#x017E] | [#x0180-#x01C3] 
  | [#x01CD-#x01F0] | [#x01F4-#x01F5] | [#x01FA-#x0217] | [#x0250-#x02A8] | [#x02BB-#x02C1] 
  | #x0386 | [#x0388-#x038A] | #x038C | [#x038E-#x03A1] | [#x03A3-#x03CE] | [#x03D0-#x03D6] 
  | #x03DA | #x03DC | #x03DE | #x03E0 | [#x03E2-#x03F3] | [#x0401-#x040C] | [#x040E-#x044F] 
  | [#x0451-#x045C] | [#x045E-#x0481] | [#x0490-#x04C4] | [#x04C7-#x04C8] | [#x04CB-#x04CC] 
  | [#x04D0-#x04EB] | [#x04EE-#x04F5] | [#x04F8-#x04F9] | [#x0531-#x0556] | #x0559 
  | [#x0561-#x0586] | [#x05D0-#x05EA] | [#x05F0-#x05F2] | [#x0621-#x063A] | [#x0641-#x064A] 
  | [#x0671-#x06B7] | [#x06BA-#x06BE] | [#x06C0-#x06CE] | [#x06D0-#x06D3] | #x06D5 
  | [#x06E5-#x06E6] | [#x0905-#x0939] | #x093D | [#x0958-#x0961] | [#x0985-#x098C] 
  | [#x098F-#x0990] | [#x0993-#x09A8] | [#x09AA-#x09B0] | #x09B2 | [#x09B6-#x09B9] 
  | [#x09DC-#x09DD] | [#x09DF-#x09E1] | [#x09F0-#x09F1] | [#x0A05-#x0A0A] | [#x0A0F-#x0A10] 
  | [#x0A13-#x0A28] | [#x0A2A-#x0A30] | [#x0A32-#x0A33] | [#x0A35-#x0A36] | [#x0A38-#x0A39] 
  | [#x0A59-#x0A5C] | #x0A5E | [#x0A72-#x0A74] | [#x0A85-#x0A8B] | #x0A8D | [#x0A8F-#x0A91] 
  | [#x0A93-#x0AA8] | [#x0AAA-#x0AB0] | [#x0AB2-#x0AB3] | [#x0AB5-#x0AB9] | #x0ABD | #x0AE0 
  | [#x0B05-#x0B0C] | [#x0B0F-#x0B10] | [#x0B13-#x0B28] | [#x0B2A-#x0B30] | [#x0B32-#x0B33] 
  | [#x0B36-#x0B39] | #x0B3D | [#x0B5C-#x0B5D] | [#x0B5F-#x0B61] | [#x0B85-#x0B8A] 
  | [#x0B8E-#x0B90] | [#x0B92-#x0B95] | [#x0B99-#x0B9A] | #x0B9C | [#x0B9E-#x0B9F] 
  | [#x0BA3-#x0BA4] | [#x0BA8-#x0BAA] | [#x0BAE-#x0BB5] | [#x0BB7-#x0BB9] | [#x0C05-#x0C0C] 
  | [#x0C0E-#x0C10] | [#x0C12-#x0C28] | [#x0C2A-#x0C33] | [#x0C35-#x0C39] | [#x0C60-#x0C61] 
  | [#x0C85-#x0C8C] | [#x0C8E-#x0C90] | [#x0C92-#x0CA8] | [#x0CAA-#x0CB3] | [#x0CB5-#x0CB9] 
  | #x0CDE | [#x0CE0-#x0CE1] | [#x0D05-#x0D0C] | [#x0D0E-#x0D10] | [#x0D12-#x0D28] 
  | [#x0D2A-#x0D39] | [#x0D60-#x0D61] | [#x0E01-#x0E2E] | #x0E30 | [#x0E32-#x0E33] 
  | [#x0E40-#x0E45] | [#x0E81-#x0E82] | #x0E84 | [#x0E87-#x0E88] | #x0E8A | #x0E8D 
  | [#x0E94-#x0E97] | [#x0E99-#x0E9F] | [#x0EA1-#x0EA3] | #x0EA5 | #x0EA7 | [#x0EAA-#x0EAB] 
  | [#x0EAD-#x0EAE] | #x0EB0 | [#x0EB2-#x0EB3] | #x0EBD | [#x0EC0-#x0EC4] | [#x0F40-#x0F47] 
  | [#x0F49-#x0F69] | [#x10A0-#x10C5] | [#x10D0-#x10F6] | #x1100 | [#x1102-#x1103] 
  | [#x1105-#x1107] | #x1109 | [#x110B-#x110C] | [#x110E-#x1112] | #x113C | #x113E | #x1140 
  | #x114C | #x114E | #x1150 | [#x1154-#x1155] | #x1159 | [#x115F-#x1161] | #x1163 | #x1165 
  | #x1167 | #x1169 | [#x116D-#x116E] | [#x1172-#x1173] | #x1175 | #x119E | #x11A8 | #x11AB 
  | [#x11AE-#x11AF] | [#x11B7-#x11B8] | #x11BA | [#x11BC-#x11C2] | #x11EB | #x11F0 | #x11F9 
  | [#x1E00-#x1E9B] | [#x1EA0-#x1EF9] | [#x1F00-#x1F15] | [#x1F18-#x1F1D] | [#x1F20-#x1F45] 
  | [#x1F48-#x1F4D] | [#x1F50-#x1F57] | #x1F59 | #x1F5B | #x1F5D | [#x1F5F-#x1F7D] 
  | [#x1F80-#x1FB4] | [#x1FB6-#x1FBC] | #x1FBE | [#x1FC2-#x1FC4] | [#x1FC6-#x1FCC] 
  | [#x1FD0-#x1FD3] | [#x1FD6-#x1FDB] | [#x1FE0-#x1FEC] | [#x1FF2-#x1FF4] | [#x1FF6-#x1FFC] 
  | #x2126 | [#x212A-#x212B] | #x212E | [#x2180-#x2182] | [#x3041-#x3094] | [#x30A1-#x30FA] 
  | [#x3105-#x312C] | [#xAC00-#xD7A3]

[XML.86] Ideographic ::= [#x4E00-#x9FA5] | #x3007 | [#x3021-#x3029]

[XML.87] CombiningChar ::= 
    [#x0300-#x0345] | [#x0360-#x0361] | [#x0483-#x0486] | [#x0591-#x05A1] | [#x05A3-#x05B9] 
  | [#x05BB-#x05BD] | #x05BF | [#x05C1-#x05C2] | #x05C4 | [#x064B-#x0652] | #x0670 
  | [#x06D6-#x06DC] | [#x06DD-#x06DF] | [#x06E0-#x06E4] | [#x06E7-#x06E8] | [#x06EA-#x06ED] 
  | [#x0901-#x0903] | #x093C | [#x093E-#x094C] | #x094D | [#x0951-#x0954] | [#x0962-#x0963] 
  | [#x0981-#x0983] | #x09BC | #x09BE | #x09BF | [#x09C0-#x09C4] | [#x09C7-#x09C8] 
  | [#x09CB-#x09CD] | #x09D7 | [#x09E2-#x09E3] | #x0A02 | #x0A3C | #x0A3E | #x0A3F 
  | [#x0A40-#x0A42] | [#x0A47-#x0A48] | [#x0A4B-#x0A4D] | [#x0A70-#x0A71] | [#x0A81-#x0A83] 
  | #x0ABC | [#x0ABE-#x0AC5] | [#x0AC7-#x0AC9] | [#x0ACB-#x0ACD] | [#x0B01-#x0B03] | #x0B3C 
  | [#x0B3E-#x0B43] | [#x0B47-#x0B48] | [#x0B4B-#x0B4D] | [#x0B56-#x0B57] | [#x0B82-#x0B83] 
  | [#x0BBE-#x0BC2] | [#x0BC6-#x0BC8] | [#x0BCA-#x0BCD] | #x0BD7 | [#x0C01-#x0C03] 
  | [#x0C3E-#x0C44] | [#x0C46-#x0C48] | [#x0C4A-#x0C4D] | [#x0C55-#x0C56] | [#x0C82-#x0C83] 
  | [#x0CBE-#x0CC4] | [#x0CC6-#x0CC8] | [#x0CCA-#x0CCD] | [#x0CD5-#x0CD6] | [#x0D02-#x0D03] 
  | [#x0D3E-#x0D43] | [#x0D46-#x0D48] | [#x0D4A-#x0D4D] | #x0D57 | #x0E31 | [#x0E34-#x0E3A] 
  | [#x0E47-#x0E4E] | #x0EB1 | [#x0EB4-#x0EB9] | [#x0EBB-#x0EBC] | [#x0EC8-#x0ECD] 
  | [#x0F18-#x0F19] | #x0F35 | #x0F37 | #x0F39 | #x0F3E | #x0F3F | [#x0F71-#x0F84] 
  | [#x0F86-#x0F8B] | [#x0F90-#x0F95] | #x0F97 | [#x0F99-#x0FAD] | [#x0FB1-#x0FB7] | #x0FB9 
  | [#x20D0-#x20DC] | #x20E1 | [#x302A-#x302F] | #x3099 | #x309A 

[XML.89] Extender ::= 
    #x00B7 | #x02D0 | #x02D1 | #x0387 | #x0640 | #x0E46 | #x0EC6 | #x3005 | [#x3031-#x3035] 
  | [#x309D-#x309E] | [#x30FC-#x30FE]

</PRE>
   * </p>
   *
   *
   *
   * <p>
   * A single "push" event <br/>
   * <code>&nbsp;&nbsp;send({1,<b>1</b>,type1,0,n},data[0..n])</code> <br/>
   * can be replaced with one partial "push" event with relative level '1' containing the start of the data,
   * followed by a sequence of "continuation" events with relative level '0' and the following data: <br/>
   * <code>&nbsp;&nbsp;send({1,<b>1</b>,type1,0,n1},data[0..n1]) send({1,<b>0</b>,type1,n1+1,n2},data[n1+1..n2]) ... send({1,<b>0</b>,type1,ni+1,n},data[ni+1..n])</code> <br/>
   * </p>
   *
   * <p>
   * A sequence of <b>i</b> "pop" events, <br/>
   * <code>&nbsp;&nbsp;send({1,<b>-1</b>,type1,0,0},data) send({1,<b>-1</b>,type2,0,0},data) ... send({1,<b>-1</b>,typeI,0,0},data)</code> <br/>
   * can be replaced with a single event, <br/>
   * <code>&nbsp;&nbsp;send({1,<b>-i</b>,type1,0,0},data)</code>. <br />
   * Note that the Node Type in "pop" events is not considered relevant; however to facilitate 
   * debugging, it should usually be set to the last node previously "pushed" as a child.
   * </p>
   *
   * <p>
   * The sequence below represents a very short XML document, with a single empty element &lt;book/&gt;:<br/>
<PRE>
send({1,1,0x10,0,0},{});
send({1,1,0x20,0,0},{});
send({1,1,0x22,0,4},{'b','o','o','k'});
send({1,-1,0x22,0,0},{});
send({1,-1,0x20,0,0},{});
send({1,-1,0x10,0,0},{});
</PRE>
   * It is equivalent to the shorter sequence below where "pop" events have been grouped into a single one:
<PRE>
send({1,1,0x10,0,0},{});
send({1,1,0x20,0,0},{});
send({1,1,0x22,0,4},{'b','o','o','k'});
send({1,-3,0x22,0,0},{});
</PRE>
   * Note that when offset=length=0, the data array does not have to be empty, but its content will be ignored.
   * </p>
   *
   * <p>
   * The sample below illustrates the data continuation; it is equivalent to above examples:
<PRE>
send({1,1,0x10,0,0},{});
send({1,1,0x20,0,0},{});
send({1,1,0x22,0,1},{'b'});
send({1,0,0x22,0,1},{'o'});
send({1,0,0x22,0,2},{'o','k'});
send({1,-3,0x22,0,0},{});
</PRE>   
   * </p>
   *
   * <p>
   * Based on XML Namespaces, Namespace nodes are inherited by descendant elements, 
   * and their definition doesn't need to be repeated in the descendant elements.
   * Thus, a default namespace definition on the root element covers all elements in the document.
   * </p>
   *
   * @param meta information about data, with fields defined by leading version number.
   * @param data the buffer into which the data is written.
   */
  public void send(int[]meta, byte[] data);
  
  /**
   * Plug the TokyoNaut to a destination TokyoNaut.<br/>
   *
   * <p>
   * Following an exclusive mode of relationship, a single destination TokyoNaut can be plugged 
   * with a TokyoNaut at a given time. Subsequent calls to plug() will unplug and replace 
   * previously plugged destination TokyoNaut.
   * </p>
   *
   * @param destination TokyoNaut.
   * @return destination parameter to allow a chain of plug calls.
   */
  public ITokyoNaut plug(ITokyoNaut destination);
  
  /**
   * Unplug the TokyoNaut from previously plugged destination TokyoNaut.<br/>
   *
   * <p>
   * This method unplug recursively all source TokyoNauts chained from this point.
   * If no destination has been set by a previous call to plug(), nothing happens.
   * </p>
   *
   */
  public void unplug();
  
}