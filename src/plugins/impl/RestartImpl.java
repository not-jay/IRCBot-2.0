package plugins.impl;

import java.util.ArrayList;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;

import org.jibble.pircbot.Colors;
import org.jibble.pircbot.PircBot;

import com.xtouchme.ircbot.BotOptions;

import plugins.Restart;

@PluginImplementation
@Author(name="xTouchMe")
public class RestartImpl implements Restart {

	private ArrayList<String> commandStart = new ArrayList<String>();
	private PircBot bot = null;

	@Override
	public void run(String params, String chan, String sender, BotOptions options) {
		if(!options.isAdmin(sender)) {
			bot.sendMessage(chan, "("+sender+") You don't have enough previlages to perform this task");
			return;
		}
		//whether pm or message, it will still say it on the channel
		if(params.substring(0, params.length()-2).length() > 0) {
			bot.sendMessage(chan, "Restart initiated by " + sender + "(" + params.substring(0, params.length()-2) + ")");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			bot.quitServer("Restart initiated by " + sender + "(" + params.substring(0, params.length()-2) + ")");
		} else {
			bot.sendMessage(chan, Colors.MAGENTA + "Bot restart triggered by " + sender + Colors.NORMAL);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			bot.quitServer(Colors.MAGENTA + "Bot restart triggered by " + sender + Colors.NORMAL);
		}
	}

	@Override
	public void load(PircBot bot) {
		setCommandStart(new String[] {"restart", "reboot"});
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
