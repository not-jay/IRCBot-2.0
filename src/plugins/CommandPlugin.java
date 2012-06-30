package plugins;

import net.xeoh.plugins.base.Plugin;

import org.jibble.pircbot.PircBot;

import com.xtouchme.ircbot.BotOptions;

public interface CommandPlugin extends Plugin {

	public void checkForCommands(String params, String chan, String sender, boolean isPM, BotOptions options);
	public void load(PircBot bot);
	
}
