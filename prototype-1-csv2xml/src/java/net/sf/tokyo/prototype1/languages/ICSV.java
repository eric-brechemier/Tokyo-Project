/* ===============================================================
 The Tokyo Project is hosted on GitHub:
 https://github.com/eric-brechemier/Tokyo-Project
 
 Copyright (c) 2005-2007 Eric Bréchemier
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
package net.sf.tokyo.prototype1.languages;

import net.sf.tokyo.ITokyoNaut;

// IANA mime-type: "text/csv" [RFC4180]
// Reference:
// [RFC4180] http://www.rfc-editor.org/rfc/rfc4180.txt
public interface ICSV extends ITokyoNaut
{
  public static final int LANGUAGE_CSV_RFC = 0xFC004180;
  
  // tokens based on the Augmented BNF grammar part of RFC4180
  public static final int TOKEN_CSV_FILE          = 0x10;
  public static final int TOKEN_CSV_HEADER        = 0x20;
  public static final int TOKEN_CSV_RECORD        = 0x30;
  public static final int TOKEN_CSV_NAME          = 0x40;
  public static final int TOKEN_CSV_FIELD         = 0x50;
  public static final int TOKEN_CSV_ESCAPED       = 0x60;
  public static final int TOKEN_CSV_NON_ESCAPED   = 0x70;
  public static final int TOKEN_CSV_COMMA         = 0x80;
  public static final int TOKEN_CSV_CR            = 0x90;
  public static final int TOKEN_CSV_DQUOTE        = 0xA0;
  public static final int TOKEN_CSV_LF            = 0xB0;
  public static final int TOKEN_CSV_CRLF          = 0xC0;
  public static final int TOKEN_CSV_TEXTDATA      = 0xD0;
  
  public static final char CHAR_COMMA   = 0x2C;
  public static final char CHAR_CR      = 0x0D;
  public static final char CHAR_DQUOTE  = 0x22;
  public static final char CHAR_LF      = 0x0A;
  
  public static final String  DEFAULT_CHARSET = "US-ASCII";
  public static final boolean DEFAULT_HEADER  = false;
  
}
