package plugins.impl;

import java.util.ArrayList;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;

import org.jibble.pircbot.Colors;
import org.jibble.pircbot.PircBot;

import plugins.MemUsage;

import com.xtouchme.ircbot.BotOptions;

@PluginImplementation
@Author(name="xTouchMe")
public class MemUsageImpl implements MemUsage {

	private ArrayList<String> commandStart = new ArrayList<String>();
	private PircBot bot = null;
	
	@Override
	public void run(String params, String chan, String sender, BotOptions options) {
		long total = Runtime.getRuntime().totalMemory();
		long free = Runtime.getRuntime().freeMemory();
		sendMessage(chan, sender, "("+sender+") Memory Usage: approx. "+
					Colors.BOLD+formatBytes(total-free)+Colors.NORMAL+params);
	}

	private void sendMessage(String channel, String sender, String sendWhat) {
		//If its a normal message
		if(sendWhat.substring(sendWhat.length()-2, sendWhat.length()).equals("NM")) {
			bot.sendMessage(channel, sendWhat.substring(0, sendWhat.length()-2));
		} else { //Otherwise, its a pm
			bot.sendNotice(sender, sendWhat.substring(0, sendWhat.length()-2));
		}
	}

	private String formatBytes(long delta) {
		if((delta/1073741824) > 0) return (delta/1073741824) + " GB";
		if((delta/1048576) > 0) return (delta/1048576) +" MB";
		if((delta/1024) > 0) return (delta/1024) + " KB";
		return delta + " B";
	}
	
	@Override
	public void load(PircBot bot) {
		setCommandStart(new String[] {"memoryusage", "memusage", "memory", "mem"});
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
