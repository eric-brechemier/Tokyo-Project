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
 */
package net.sf.tokyo.test;

import net.sf.tokyo.ITokyoNaut;

public class Joshua implements ITokyoNaut
{
  public static final int TYPE_PROPOSE_GAME_CHESS = 100;
  public static final int TYPE_REPLY_PROPOSE_GAME = 110;
  public static final int TYPE_REAL_DOUBT = 900;
  
  public static final void main(String[] args)
  {
    ITokyoNaut josh = new Joshua();
    ITokyoNaut nullNaut = new NullNautilus();
    
    Object[] context = new Object[10];
    int[] type = new int[10];
    
    final int language = 0;
    context[language] = "Language: None";
    type[language] = -1;
    
    final int sentence = 2;
    context[sentence] = "hi";
    type[sentence] = -2;
    
    final int play = 4;
    context[play] = "noughts and crosses";
    type[play] = -3;
    
    Object thing = null;
    
    System.out.println("Starting to call all Joshua methods for API coverage...");
    
    System.out.println("To Joshua: meet "+nullNaut+" speaking "+context[language]+" (type "+type[language]+")");
    josh.meet(nullNaut,context,type,language);
    
    System.out.println("To Joshua: "+nullNaut+" says "+context[sentence]+" (type "+type[sentence]+")");
    josh.say(nullNaut,context,type,sentence);
    System.out.println("From Joshua: "+context[sentence+1]);
    
    System.out.println("To Joshua: "+nullNaut+" wants to play "+context[play]+" (type "+type[play]+")");
    josh.play(nullNaut,context,type,play);
    System.out.println("From Joshua: "+context[play+1]);
    
    System.out.println("To Joshua: "+nullNaut+" shows "+thing);
    josh.show(nullNaut,thing);
    
    System.out.println("To Joshua: quit "+nullNaut+" speaking "+context[language]+" (type "+type[language]+")");
    josh.quit(nullNaut,context,type,language);
    System.out.println("From Joshua: "+context[language+1]);
    
    System.out.println("API coverage complete.");
  }
  
  public void meet(ITokyoNaut stranger, Object[] context, int[] type, int language)
  {
    stranger.quit(this,context,type,language);
    return;
  }
  
  public void quit(ITokyoNaut friend, Object[] context, int[] type, int language)
  {
    if ( isSafe(friend,context,type,language) ) {
      int play = language+1;
      context[play] = "How about a nice game of chess?";
      type[play] = TYPE_PROPOSE_GAME_CHESS;
      friend.play(this,context,type,play);
    }
    return;
  }
  
  public void say(ITokyoNaut speaker, Object[] context, int[] type, int sentence)
  {
    if ( isSafe(speaker,context,type,sentence) ) {
      int play = sentence+1;
      context[play] = "Is it a game, or is it real?";
      type[play] = TYPE_REAL_DOUBT;
      speaker.play(this,context,type,play);
    }
    return;
  }
  
  public void play(ITokyoNaut player, Object[] context, int[] type, int play)
  {
    if ( isSafe(player,context,type,play) ) {
      int reply = play+1;
      context[reply] = "Shall we play a game?";
      type[reply] = TYPE_REPLY_PROPOSE_GAME;
      player.say(this,context,type,reply);
    }
    return;
  }
    
  public void show(ITokyoNaut shower, Object towel)
  {
    System.err.println("Don't Panic! "+shower+" knows where his "+towel+" is.");
    return;
  }
  
  private boolean isSafe(ITokyoNaut friend, Object[] context, int[] type, int position)
  {
    if
      (
        (friend != null)
        &&
        (context != null)
        &&
        (context.length >0)
        &&
        (0 <= position && position < context.length-1)
        &&
        (type != null)
        &&
        (type.length == context.length)
      )
    {
      return true;
    }
    else
    {
      friend.show(this,"Error: Invalid parameters or context buffer too small");
      return false;
    }
  }
  
}