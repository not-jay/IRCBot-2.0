package plugins.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;

import org.jibble.pircbot.PircBot;

import plugins.Slap;

import com.xtouchme.ircbot.BotOptions;

@PluginImplementation
@Author(name="xTouchMe")
public class SlapImpl implements Slap {

	private ArrayList<String> commandStart = new ArrayList<String>();
	private PircBot bot = null;
	
	@Override
	public void run(String params, String chan, String sender, BotOptions options) {
		Random rand = new Random();
		Scanner sc;
		
		String init[] = {"slaps", "smacks", "whacks", "donkey punches", "throws", "flicks"};
		File slapFile = new File("slapFile.txt");
		String slapTemp = "";
		try {
			sc = new Scanner(slapFile);
			while(sc.hasNextLine()) {
				slapTemp += sc.nextLine()+"\r\n";
		}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		slapTemp = slapTemp.replace("[verb]", init[rand.nextInt(init.length-1)]);
		slapTemp = slapTemp.replace("[name]", params.substring(0, params.length()-2));
		String slap[] = slapTemp.split("\r\n");
		bot.sendAction(chan, slap[rand.nextInt(slap.length-1)]);
	}

	@Override
	public void load(PircBot bot) {
		setCommandStart(new String[] {"slap", "sl"});
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
