package plugins.impl;

import java.util.ArrayList;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;

import org.jibble.pircbot.PircBot;

import plugins.DC;

import com.xtouchme.ircbot.BotOptions;

@PluginImplementation
@Author(name="xTouchMe")
public class DCImpl implements DC {

	private ArrayList<String> commandStart = new ArrayList<String>();
	private PircBot bot = null;
	
	@Override
	public void load(PircBot bot) {
		setCommandStart(new String[] {"disconnect", "dc", "quit", "shutdown"});
		this.bot = bot;
	}
	
	@Override
	public void run(String params, String chan, String sender, BotOptions options) {
		if(!options.isAdmin(sender)) {
			bot.sendMessage(chan, "You don't have enough privileges to perform this task");
			return;
		}
		//whether pm or message, it will still say it on the channel
		if(params.substring(0, params.length()-2).length() > 0) {
			bot.sendMessage(chan, "Quitting("+params.substring(0, params.length()-2)+")");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			bot.quitServer(params.substring(0, params.length()-2));
		} else {
			bot.sendMessage(chan, "Quitting(See you guys later... Creature Caaaaats!!!~)");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			bot.quitServer("See you guys later... Creature Caaaaats!!!~");
		}
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
