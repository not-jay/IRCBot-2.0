package plugins.impl;

import java.util.ArrayList;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;

import org.jibble.pircbot.PircBot;

import com.xtouchme.ircbot.BotOptions;
import com.xtouchme.ircbot.QuoteDB;

import plugins.QDB;

@PluginImplementation
@Author(name="xTouchMe")
public class QDBImpl implements QDB {

	private ArrayList<String> commandStart = new ArrayList<String>();
	private PircBot bot = null;
	
	@Override
	public void run(String params, String chan, String sender, BotOptions options) {
		QuoteDB qdb = QuoteDB.getQuoteDB();
		String isPM = params.substring(params.length()-2, params.length());
		params = params.substring(0, params.length()-2);
		String[] line = params.split(" ");

		if(!options.isModerator(sender)) { restrictions(chan, sender, isPM.equals("PM")); return; }
		
		//!quote save
		if(line[0].equalsIgnoreCase("save")) {
			qdb.save();
			sendMessage(chan, sender, "Current quotes on memory are now saved to the DB."+isPM);
			return;
		}
		//!quote remove
		if(line[0].equalsIgnoreCase("remove")) {
			if(line.length < 1) { return; }
			qdb.removeFromDB(line[1]);
			sendMessage(chan, sender, "Removed "+line[1]+" from the DB"+isPM);
			qdb.save();
			return;
		}
		//!quote <identifier> <quote>
		String quote = line[1];
		for(int n=2; n<line.length; n++) {
			quote += " "+line[n];
		}
		qdb.addToDB(line[0], quote);
		sendMessage(chan, sender, "Added "+line[0]+" - "+quote+" to the DB"+isPM);
	}

	private void restrictions(String channel, String sender, boolean isPM) {
		sendMessage(channel, sender, "You don't have enough privileges to perform this task"+((isPM)?"PM":"NM"));
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
		setCommandStart(new String[] {"quote", "qdb"});
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
