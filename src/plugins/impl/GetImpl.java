package plugins.impl;

import java.util.ArrayList;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;

import org.jibble.pircbot.PircBot;

import com.xtouchme.ircbot.BotOptions;
import com.xtouchme.ircbot.QuoteDB;

import plugins.Get;

@PluginImplementation
@Author(name="xTouchMe")
public class GetImpl implements Get {

	private ArrayList<String> commandStart = new ArrayList<String>();
	private PircBot bot = null;

	@Override
	public void run(String params, String chan, String sender, BotOptions options) {
		String isPM = params.substring(params.length()-2, params.length());
		params = params.substring(0, params.length()-2);
		QuoteDB qdb = QuoteDB.getQuoteDB();
		
		if(params.length() <= 0) { error(chan, sender, isPM.equals("PM")); return; }
		
		if(params.equalsIgnoreCase("greet"))
			sendMessage(chan, sender, options.isGreet()+isPM);
		if(params.equalsIgnoreCase("reconnect"))
			sendMessage(chan, sender, options.isReconnect()+isPM);
		if(params.equalsIgnoreCase("greet.channel")) {
			sendMessage(chan, sender, "These are the channels where I'll greet people: " + options.getChans()+isPM);
		}
		if(params.equalsIgnoreCase("mod")) {
			sendMessage(chan, sender, "Bot Moderators: "+options.getModerators()+isPM);
		}
		if(params.equalsIgnoreCase("admin")) {
			sendMessage(chan, sender, "Bot Admins: "+options.getAdmins()+isPM);
		}
		if(params.equalsIgnoreCase("quote")) {
			sendMessage(chan, sender, "Quotes: "+qdb.listKeys()+isPM);
		}
	}

	private void error(String channel, String sender, boolean isPM) {
		sendMessage(channel, sender, "Invalid usage. Usage: '!get <option>'"+((isPM)?"PM":"NM"));
	}

	private void sendMessage(String channel, String sender, String sendWhat) {
		//If its a normal message
		if(sendWhat.substring(sendWhat.length()-2, sendWhat.length()).equals("NM")) {
			bot.sendMessage(channel, sendWhat.substring(0, sendWhat.length()-2));
		} else { //Otherwise, its a pm
			bot.sendNotice(sender, sendWhat.substring(0, sendWhat.length()-2));
		}
	}

	@Override
	public void load(PircBot bot) {
		setCommandStart(new String[] {"get", "ge"});
		this.bot = bot;
	}
	
	@Override
	public void setCommandStart(String[] commandStart) {
		for(String s : commandStart) {
			this.commandStart.add(s);
		}
	}
	
	@Override
	public boolean checkForCommand(String commandStart) {
		return this.commandStart.contains(commandStart);
	}

	@Override
	public void unload() {
		commandStart = null;
		bot = null;
	}
	
}
