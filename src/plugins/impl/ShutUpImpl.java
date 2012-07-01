package plugins.impl;

import java.util.ArrayList;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;

import org.jibble.pircbot.PircBot;

import com.xtouchme.ircbot.BotOptions;

import plugins.ShutUp;

@PluginImplementation
@Author(name="xTouchMe")
public class ShutUpImpl implements ShutUp {

	private ArrayList<String> commandStart = new ArrayList<String>();
	private PircBot bot = null;
	
	@Override
	public void run(String params, String chan, String sender, BotOptions options) {
		if(!options.isSuperAdmin(sender)) { restrictions(chan, sender); return; }
		options.shouldShutUp(!options.shouldShutUp());
		sendMessage(chan, sender, ((options.shouldShutUp())?("Shutting up..."+params):("Yay! I can speak again"+params)));
	}

	private void restrictions(String channel, String sender) {
		sendMessage(channel, sender, "You don't have enough previliges to perform this task");
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
		setCommandStart(new String[] {"shutup"});
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
