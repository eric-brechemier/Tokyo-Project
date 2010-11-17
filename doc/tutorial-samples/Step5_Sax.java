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
 */
package net.sf.tokyo.prototype1;

public abstract class Step5_Sax
{
  // [Start] Sax Language for TokyoNaut discussion
  //
  // This language is defined by URI String "prototype1.tokyo.sf.net/sax-events"
  // It allows a discussion of two tokyonauts, sourceNaut and destNaut: 
  //
  // destNaut makes use of meet() with message (1) to start the parsing processing
  // (1)
  //       Type:                                  Context: 
  // [ LANGUAGE              ]     [ SAX_EVENTS_LANG    ]
  // [ DOCUMENT_INPUT_SOURCE ]     [ InputSource source ]
  // [ ...                   ]     [ ...                ]
  // 
  // sourceNaut accepts and starts sending say() messages of following structure:
  // (2)
  //       Type:                                  Context: 
  // [ LANGUAGE              ]     [ SAX_EVENTS_LANG                 ]
  // [ DOCUMENT_INPUT_SOURCE ]     [ InputSource source              ]
  // [ DOCUMENT_LINE_COLUMN  ]     [ int[] {lineNumber,columnNumber} ]
  // [ CONTENT_*             ]     [ int[] {argCount}                ]
  // [ ARG_*                 ]     [ Object arg1                     ]
  // [ ARG_*                 ]     [ Object arg2                     ]
  // [ ...                   ]     [ ...                             ]
  //
  // The following combinations of CONTENT_* / ARG_* covers SAX ContentHandler Events:
  //
  // (start-document)
  // [ CONTENT_START_DOCUMENT ]        [ int[] {0}                   ]
  //
  // (start-PREFIX-mapping)
  // [ CONTENT_START_PREFIX_MAPPING ]  [ int[] {2}                   ]
  // [ ARG_NAMESPACE_PREFIX         ]  [ String PREFIX               ]
  // [ ARG_NAMESPACE_URI            ]  [ String uri                  ]
  //
  // (start-element)
  // [ CONTENT_START_ELEMENT ]         [ int[] {4}                   ]
  // [ ARG_NAMESPACE_URI     ]         [ String uri                  ]
  // [ ARG_LOCAL_NAME        ]         [ String localName            ]
  // [ ARG_QNAME             ]         [ String qName                ]
  // [ ARG_ATTRIBUTES        ]         [ Attributes attributes       ]
  //
  // (characters)
  // [ CONTENT_CHARACTERS    ]         [ int[] {2}                   ]
  // [ ARG_CHARACTERS        ]         [ char[] characters           ]
  // [ ARG_START_LENGTH      ]         [ int[] {start,length}        ]
  //
  // (end-element)
  // [ CONTENT_END_ELEMENT   ]         [ int[] {3}                   ]
  // [ ARG_NAMESPACE_URI     ]         [ String uri                  ]
  // [ ARG_LOCAL_NAME        ]         [ String localName            ]
  // [ ARG_QNAME             ]         [ String qName                ]
  //
  // (end-PREFIX-mapping)
  // [ CONTENT_END_PREFIX_MAPPING   ]  [ int[] {1}                   ]
  // [ ARG_NAMESPACE_PREFIX         ]  [ String PREFIX               ]
  //
  // (end-document)
  // [ CONTENT_END_DOCUMENT  ]         [ int[] {0}                   ]
  //
  // (ignorable-white-space)
  // [ CONTENT_IGNORABLE_WHITE_SPACE ] [ int[] {2}                   ]
  // [ ARG_CHARACTERS                ] [ char[] characters           ]
  // [ ARG_START_LENGTH              ] [ int[] {start,length}        ]
  //
  // (processing-instruction)
  // [ CONTENT_PROCESSING_INSTRUCTION ][ int[] {2}                   ]
  // [ ARG_TARGET                     ][ String target               ]
  // [ ARG_DATA                       ][ String data                 ]
  //
  // (skipped-entity)
  // [ CONTENT_SKIPPED_ENTITY       ]  [ int[] {1}                   ]
  // [ ARG_ENTITY_NAME              ]  [ String name                 ]
  //
  // destNaut completes the operation with message quit():
  // (quit)
  //       Type:                                  Context: 
  // [ LANGUAGE              ]     [ SAX_EVENTS_LANG    ]
  // [ ...                   ]     [ ...                ]
  //
  // show() method is used for log and error reporting.
  // In case of fatal error or unsupported language, sourceNaut may also
  // send quit() message to destNaut, prior to parsing completion.
  // destNaut is expected to notify the SAX ErrorHandler with fatalError 
  // and close the document if required.
  //
  // Above messages can be used for CSV serializing too, replacing
  // [ DOCUMENT_INPUT_SOURCE ]     [ InputSource source    ]
  // with
  // [ DOCUMENT_OUTPUT_FULL_PATH ] [ String outputFullPath ]
  // 
  public static final String SAX_EVENTS_LANG = "prototype1.tokyo.sf.net/sax-events";
  public static final int SAX_MAX_STACK_LENGTH = 8;
  
  public static final int LANGUAGE = 0;
  public static final int DOCUMENT_INPUT_SOURCE = 1;
  public static final int DOCUMENT_OUTPUT_PATH = 1;
  public static final int DOCUMENT_LINE_COLUMN = 2;
  public static final int EVENT = 3;
  public static final int ARG1 = 4;
  public static final int ARG2 = 5;
  public static final int ARG3 = 6;
  public static final int ARG4 = 7;
  
  public static final int DOCUMENT_OUTPUT_FULL_PATH = 10;
  
  public static final int ARG_CHARACTERS = 11;
  public static final int ARG_START_LENGTH = 12;
  public static final int ARG_NAMESPACE_URI = 13;
  public static final int ARG_NAMESPACE_PREFIX = 14;
  public static final int ARG_LOCAL_NAME = 15;
  public static final int ARG_QNAME = 16;
  public static final int ARG_ATTRIBUTES = 17;
  public static final int ARG_TARGET = 18;
  public static final int ARG_DATA = 19;
  public static final int ARG_ENTITY_NAME = 20;
  
  // (called internally) public static final int CONTENT_DOCUMENT_LOCATOR = 050;
  public static final int CONTENT_START_DOCUMENT = 100;
  public static final int CONTENT_START_PREFIX_MAPPING = 200;
  public static final int CONTENT_START_ELEMENT = 300;
  public static final int CONTENT_CHARACTERS = 400;
  public static final int CONTENT_END_ELEMENT = 500;
  public static final int CONTENT_END_PREFIX_MAPPING = 600;
  public static final int CONTENT_END_DOCUMENT = 700;
  public static final int CONTENT_IGNORABLE_WHITE_SPACE = 800;
  public static final int CONTENT_PROCESSING_INSTRUCTION = 900;
  public static final int CONTENT_SKIPPED_ENTITY = 1000;
  // [End] Sax Language for TokyoNaut discussion
  
}
