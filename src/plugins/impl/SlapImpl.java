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

		String isPM = params.substring(params.length()-2, params.length());
		params = params.substring(0, params.length()-2).trim();

		if(params.length() <= 0) { error(chan, sender, isPM.equals("PM")); return; }
		
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
		slapTemp = slapTemp.replace("[name]", params);
		String slap[] = slapTemp.split("\r\n");
		sendAction(chan, sender, slap[rand.nextInt(slap.length-1)]+isPM);
	}

	private void error(String channel, String sender, boolean isPM) {
		sendMessage(channel, sender, "Invalid usage. Usage: '!slap <name>'"+((isPM)?"PM":"NM"));
	}

	private void sendMessage(String channel, String sender, String sendWhat) {
		//If its a normal message
		if(sendWhat.substring(sendWhat.length()-2, sendWhat.length()).equals("NM")) {
			bot.sendMessage(channel, sendWhat.substring(0, sendWhat.length()-2));
		} else { //Otherwise, its a pm
			bot.sendNotice(sender, sendWhat.substring(0, sendWhat.length()-2));
		}
	}

	private void sendAction(String channel, String sender, String sendWhat) {
		//If its a normal message
		if(sendWhat.substring(sendWhat.length()-2, sendWhat.length()).equals("NM")) {
			bot.sendAction(channel, sendWhat.substring(0, sendWhat.length()-2));
		} else { //Otherwise, its a pm
			bot.sendNotice(sender, sendWhat.substring(0, sendWhat.length()-2));
		}
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
