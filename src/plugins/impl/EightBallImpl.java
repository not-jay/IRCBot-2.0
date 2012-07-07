package plugins.impl;

import java.util.ArrayList;
import java.util.Random;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;

import org.jibble.pircbot.PircBot;

import plugins.EightBall;

import com.xtouchme.ircbot.BotOptions;

@PluginImplementation
@Author(name="xTouchMe")
public class EightBallImpl implements EightBall {

	private ArrayList<String> commandStart = new ArrayList<String>();
	private PircBot bot = null;


	@Override
	public void run(String params, String chan, String sender, BotOptions options) {
		String[] response = {
				"It is certain",
			    "It is decidedly so",
			    "Without a doubt",
			    "Yes – definitely",
			    "You may rely on it",
			    "As I see it, yes",
			    "Most likely",
			    "Outlook good",
			    "Signs point to yes",
			    "Yes",
			    "Reply hazy, try again",
			    "Ask again later",
			    "Better not tell you now",
			    "Cannot predict now",
			    "Concentrate and ask again",
				"Don't count on it",
			    "My reply is no",
			    "My sources say no",
			    "Outlook not so good",
			    "Very doubtful" };
		Random rand = new Random();
		
		String isPM = params.substring(params.length()-2, params.length());
		String[] line = params.substring(0, params.length()-2).split(" ");
		
		if(params.length() <= 0) { error(chan, sender, isPM.equals("PM")); return; }
		
		String question = line[0];
		for(int n=1; n<line.length; n++) {
			question += " "+line[n];
		}
		if(!line[line.length-1].contains("?")) question += "?";
		
		sendAction(chan, sender, "shakes magic 8 ball... \""+question+"\" "+response[rand.nextInt(response.length)]+isPM);
	}

	private void error(String channel, String sender, boolean isPM) {
		sendMessage(channel, sender, "Invalid usage. Usage: '!8ball <question>'"+((isPM)?"PM":"NM"));
	}

	private void sendMessage(String channel, String sender, String sendWhat) {
		//If its a normal message
		if(sendWhat.substring(sendWhat.length()-2, sendWhat.length()).equals("NM")) {
			bot.sendAction(channel, sendWhat.substring(0, sendWhat.length()-2));
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
		setCommandStart(new String[] {"speak"});
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
