package plugins.impl;

import java.util.ArrayList;

import net.xeoh.plugins.base.Plugin;

import org.jibble.pircbot.PircBot;

import com.xtouchme.ircbot.BotOptions;

public interface PluginBase extends Plugin {

	ArrayList<String> commandStart = new ArrayList<String>();
	PircBot currentBot = null;
	
	public abstract void run(String params, String chan, String sender, BotOptions options);
	public abstract void load(PircBot bot);
	public abstract void unload();
	public void setCommandStart(String[] commandStart);
	public boolean checkForCommand(String commandStart);
	
}
