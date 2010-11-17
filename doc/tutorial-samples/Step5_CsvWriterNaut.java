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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import net.sf.tokyo.ITokyoNaut;

import org.xml.sax.Attributes;

public class Step5_CsvWriterNaut implements ITokyoNaut
{
  protected static final String NAMESPACE = "http://tokyo.sf.net/prototype1/csv";
  protected static final String CSV_ELEMENT = "csv";
  protected static final String LINE_ELEMENT = "line";
  protected static final String FIELD_ELEMENT = "field";
  
  /**
   * Flag start of lines to skip separator before first field
   */
  protected boolean _isStartOfLine;
  
  /**
   * Output Writer for CSV Serializing
   */
  protected BufferedWriter _writer;
  
  protected void freeWriter()
  {
    if (_writer!=null)
      try 
      { 
        _writer.close(); 
      } 
      catch(Exception e) {}
    
    _writer = null;
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
      
      String outputFullPath = (String)context[Step5_Sax.DOCUMENT_OUTPUT_PATH];
      
      _writer = new BufferedWriter(new FileWriter(outputFullPath)); // ASCII
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
    freeWriter();
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
      switch(type[sentence])
      {
        case Step5_Sax.CONTENT_START_ELEMENT:
          {
            // pop arguments from Context Stack
            String uri = (String)context[sentence+1];
            String localName = (String)context[sentence+2];
            // (unused) String qName = (String)context[sentence+3];
            // (unused) Attributes atts = (Attributes)context[sentence+4];
            if ( NAMESPACE.equals(uri) ) 
            {
              if ( LINE_ELEMENT.equals(localName) )
              {
                _isStartOfLine = true;
              }
              else if ( FIELD_ELEMENT.equals(localName) )
              {
                if (_isStartOfLine)
                  _isStartOfLine = false;
                else
                  _writer.write(','); 
              }
            }
          }
          break;
        
        case Step5_Sax.CONTENT_CHARACTERS:
          {
            // pop arguments from Context Stack
            char[] characters = (char[])context[sentence+1];
            int[] startLength = (int[])context[sentence+2];
            int start = startLength[0];
            int length = startLength[1];
            _writer.write(characters,start,length);
          }
          break;
        
        case Step5_Sax.CONTENT_END_ELEMENT:
          {
            // pop arguments from Context Stack
            String uri = (String)context[sentence+1];
            String localName = (String)context[sentence+2];
            String qName = (String)context[sentence+3];
            if ( NAMESPACE.equals(uri) )
            {
              if ( LINE_ELEMENT.equals(localName) )
              {
                _writer.write('\r');
                _writer.write('\n');
              } 
            }
          }
          break;
        
        case Step5_Sax.CONTENT_END_DOCUMENT:
          _writer.flush();
          freeWriter();
          break;
        
        case Step5_Sax.CONTENT_START_DOCUMENT:
        case Step5_Sax.CONTENT_START_PREFIX_MAPPING:
        case Step5_Sax.CONTENT_END_PREFIX_MAPPING:
        case Step5_Sax.CONTENT_IGNORABLE_WHITE_SPACE:
        case Step5_Sax.CONTENT_PROCESSING_INSTRUCTION:
        case Step5_Sax.CONTENT_SKIPPED_ENTITY:
          break;
        
        default:
          speaker.show(this,"Unknown type: "+type[sentence]);
      }
      
    }
    catch(IOException e)
    {
      freeWriter();
      if (speaker!=null)
        speaker.show(this,"Unexpected exception: "+e);
      else
      {
        System.err.println("Error: "+e);
        e.printStackTrace();
      }
    }
    catch(Exception e)
    {
      if (speaker!=null)
        speaker.show(this,"Unexpected exception: "+e);
      else
      {
        System.err.println("Error: "+e);
        e.printStackTrace();
      }
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
