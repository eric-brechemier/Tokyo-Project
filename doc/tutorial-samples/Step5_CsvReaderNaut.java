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

import java.io.BufferedReader;
import java.io.FileReader;

import net.sf.tokyo.ITokyoNaut;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

public class Step5_CsvReaderNaut implements ITokyoNaut
{
  protected static final String PREFIX = "";
  protected static final String NAMESPACE = "http://tokyo.sf.net/prototype1/csv";
  protected static final Attributes NO_ATTRIBUTES = new AttributesImpl();
  protected static final String CSV_ELEMENT = "csv";
  protected static final String LINE_ELEMENT = "line";
  protected static final String FIELD_ELEMENT = "field";
  protected static final int MAX_FIELD_SIZE = 50;
  
  protected int[] lineCol = new int[2];
  protected int[] argCount = new int[1];
  protected int[] intArgCouple = new int[2];
  
  protected void resetLine()
  {
    lineCol[0] = 1;
  }
  
  protected void resetColumn()
  {
    lineCol[1] = 1;
  }
  
  protected int getLine()
  {
    return lineCol[0];
  }
  
  protected int getColumn()
  {
    return lineCol[1];
  }
  
  protected void incrementLine()
  {
    lineCol[0]++;
  }
  
  protected void incrementColumn()
  {
    lineCol[1]++;
  }
  
  protected void initContext(Object[] context)
  {
    context[Step5_Sax.DOCUMENT_LINE_COLUMN] = lineCol;
    context[Step5_Sax.EVENT] = argCount;
  }
  
  protected void startDocument(ITokyoNaut destNaut, Object[] context, int[] type)
  {
    initContext(context);
    
    type[Step5_Sax.EVENT] = Step5_Sax.CONTENT_START_DOCUMENT;
    argCount[0] = 0;
    destNaut.say(this,context,type,Step5_Sax.EVENT);
    
    type[Step5_Sax.EVENT] = Step5_Sax.CONTENT_START_PREFIX_MAPPING;
    argCount[0] = 2;
    type[Step5_Sax.ARG1] = Step5_Sax.ARG_NAMESPACE_PREFIX;
    context[Step5_Sax.ARG1] = PREFIX;
    type[Step5_Sax.ARG2] = Step5_Sax.ARG_NAMESPACE_URI;
    context[Step5_Sax.ARG2] = NAMESPACE;
    destNaut.say(this,context,type,Step5_Sax.EVENT);  
  }   
  
  protected void startElement(String elementName, ITokyoNaut destNaut, Object[] context, int[] type)
  {
    initContext(context);
    type[Step5_Sax.EVENT] = Step5_Sax.CONTENT_START_ELEMENT;
    argCount[0] = 4;
    type[Step5_Sax.ARG1] = Step5_Sax.ARG_NAMESPACE_URI;
    context[Step5_Sax.ARG1] = NAMESPACE;
    type[Step5_Sax.ARG2] = Step5_Sax.ARG_LOCAL_NAME;
    context[Step5_Sax.ARG2] = elementName;
    type[Step5_Sax.ARG3] = Step5_Sax.ARG_QNAME;
    context[Step5_Sax.ARG3] = elementName;
    type[Step5_Sax.ARG4] = Step5_Sax.ARG_ATTRIBUTES;
    context[Step5_Sax.ARG4] = NO_ATTRIBUTES;
    destNaut.say(this,context,type,Step5_Sax.EVENT);
  }
  
  protected void characters(char[] chars, int length, ITokyoNaut destNaut, Object[] context, int[] type)
  {
    initContext(context);
    type[Step5_Sax.EVENT] = Step5_Sax.CONTENT_CHARACTERS;
    argCount[0] = 2;
    type[Step5_Sax.ARG1] = Step5_Sax.ARG_CHARACTERS;
    context[Step5_Sax.ARG1] = chars;
    type[Step5_Sax.ARG2] = Step5_Sax.ARG_START_LENGTH;
    intArgCouple[0]=0;
    intArgCouple[1]=length;
    context[Step5_Sax.ARG2] = intArgCouple;
    destNaut.say(this,context,type,Step5_Sax.EVENT);
  }
  
  protected void endElement(String elementName, ITokyoNaut destNaut, Object[] context, int[] type)
  {
    initContext(context);
    type[Step5_Sax.EVENT] = Step5_Sax.CONTENT_END_ELEMENT;
    argCount[0] = 3;
    type[Step5_Sax.ARG1] = Step5_Sax.ARG_NAMESPACE_URI;
    context[Step5_Sax.ARG1] = NAMESPACE;
    type[Step5_Sax.ARG2] = Step5_Sax.ARG_LOCAL_NAME;
    context[Step5_Sax.ARG2] = elementName;
    type[Step5_Sax.ARG3] = Step5_Sax.ARG_QNAME;
    context[Step5_Sax.ARG3] = elementName;
    destNaut.say(this,context,type,Step5_Sax.EVENT);
  }
  
  protected void endDocument(ITokyoNaut destNaut, Object[] context, int[] type)
  {
    initContext(context);
    type[Step5_Sax.EVENT] = Step5_Sax.CONTENT_END_PREFIX_MAPPING;
    argCount[0] = 1;
    type[Step5_Sax.ARG1] = Step5_Sax.ARG_NAMESPACE_PREFIX;
    context[Step5_Sax.ARG1] = PREFIX;
    destNaut.say(this,context,type,Step5_Sax.EVENT);
    
    type[Step5_Sax.EVENT] = Step5_Sax.CONTENT_END_DOCUMENT;
    argCount[0] = 0;
    destNaut.say(this,context,type,Step5_Sax.EVENT);
  }
  
  public void parseAll(String fileName, ITokyoNaut destNaut, Object[] context, int[] type, int language)
  {
    try
    {
      BufferedReader br = new BufferedReader( new FileReader(fileName) ); // ASCII
      
      char[] fieldChars = new char[MAX_FIELD_SIZE];
      int fieldCharPos = 0;
      
      resetLine();
      resetColumn();
      startDocument(destNaut, context, type);
      
      startElement(CSV_ELEMENT, destNaut, context, type);
      
      int ch;
      boolean isStartLine = true;
      boolean isStartField = true;
      boolean isEndOfFile = false;
      do
      {
        if (isStartLine)
        {
          incrementLine();
          resetColumn();
          isStartField = true;
          
          startElement(LINE_ELEMENT, destNaut, context, type);
          isStartLine = false;
        }
        
        if (isStartField)
        {
          fieldCharPos = 0;
          startElement(FIELD_ELEMENT, destNaut, context, type);
          isStartField = false;
        }
        
        ch = br.read();
        System.out.println("car: "+(char)ch+" col: "+getColumn());
        incrementColumn();
        
        switch(ch)
        {
          case '\r':
            break;
            
          case '\n':
            characters(fieldChars, fieldCharPos, destNaut, context, type);
            endElement(FIELD_ELEMENT, destNaut, context, type);
            endElement(LINE_ELEMENT, destNaut, context, type);
            isStartLine = true;
            break;
          
          case ',':
            characters(fieldChars, fieldCharPos, destNaut, context, type);
            endElement(FIELD_ELEMENT, destNaut, context, type);
            isStartField = true;
            break;
          
          case -1:
            isEndOfFile = true;
            break;
          
          default:
            fieldChars[fieldCharPos]=(char)ch; // Nota: cast to char is supposing ASCII...
            fieldCharPos++;
        }
        
      } while (!isEndOfFile);
      
      // End of File
      
      characters(fieldChars, fieldCharPos, destNaut, context, type);
      endElement(FIELD_ELEMENT, destNaut, context, type);
      endElement(LINE_ELEMENT, destNaut, context, type);
      endElement(CSV_ELEMENT, destNaut, context, type);
      endDocument(destNaut, context, type);
    }
    catch(Exception e)
    {
      if (destNaut != null)
      {
        destNaut.show(this,e);
      }
      else
      {
        System.err.println("ERROR: "+ e);
        e.printStackTrace();
      }
    }
    finally
    {
      System.out.flush();
    }
  }
  
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
    try
    {
      if ( !Step5_Sax.SAX_EVENTS_LANG.equals(context[language]) )
      {
        stranger.quit(this,context,type,language);
        return;
      }
      
      InputSource input = (InputSource)context[Step5_Sax.DOCUMENT_INPUT_SOURCE];
      String systemId = input.getSystemId();
      final String FILE_PREFIX = "file:";
      // Remove starting "file:"
      String path = systemId.substring(FILE_PREFIX.length());
      
      parseAll( path, stranger,context,type,language);
      
    }
    catch(Exception e)
    {
      if (stranger!=null)
        stranger.show(this,"Unexpected exception: "+e);
      else
        System.err.println("Invalid stranger: "+stranger);
    }
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
    System.err.println("Error: "+towel+" reported by: "+shower);
  }
  
}
