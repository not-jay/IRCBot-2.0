package plugins.impl;

import java.util.ArrayList;
import java.util.Random;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;

import org.jibble.pircbot.PircBot;

import plugins.Choose;

import com.xtouchme.ircbot.BotOptions;

@PluginImplementation
@Author(name="xTouchMe")
public class ChooseImpl implements Choose {

	private ArrayList<String> commandStart = new ArrayList<String>();
	private PircBot bot = null;
	
	@Override
	public void run(String params, String chan, String sender, BotOptions options) {
		Random rand = new Random();
		
		String isPM = params.substring(params.length()-2, params.length());
		params = params.substring(0, params.length()-2);
		if(params.equals("")) {
			sendMessage(chan, sender, "("+sender+") Though I'm a bot, it's hard to choose between thin air and nothing"
						+isPM);
			return;
		}
		params = params.replace("or ", ", ");
		params = params.replace("?", "");
		String[] choice = params.split(", ");
		if(choice.length > 1) {
			sendMessage(chan, sender, "("+sender+") "+choice[rand.nextInt(choice.length)]+isPM);
		}
		else
			sendMessage(chan, sender, "("+sender+") It's difficult to choose even for a bot, but you'll somehow manage"
						+isPM);
	}

	private void sendMessage(String channel, String sender, String sendWhat) {
		//If its a normal message
		if(sendWhat.substring(sendWhat.length()-2, sendWhat.length()).equals("NM")) {
			bot.sendMessage(channel, sendWhat.substring(0, sendWhat.length()-2));
		} else { //Otherwise, its a pm
			bot.sendMessage(sender, sendWhat.substring(0, sendWhat.length()-2));
		}
	}

	@Override
	public void load(PircBot bot) {
		setCommandStart(new String[] {"choose", "ch"});
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
