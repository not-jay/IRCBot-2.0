package plugins.impl;

import java.util.ArrayList;
import java.util.Random;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;

import org.jibble.pircbot.PircBot;

import plugins.Coin;

import com.xtouchme.ircbot.BotOptions;

@PluginImplementation
@Author(name="xTouchMe")
public class CoinImpl implements Coin {

	private ArrayList<String> commandStart = new ArrayList<String>();
	private PircBot bot = null;
	
	@Override
	public void run(String params, String chan, String sender, BotOptions options) {
		int heads = 0;
		int tails = 0;
		Random rand = new Random();
		
		String isPM = params.substring(params.length()-2, params.length());
		params = params.substring(0, params.length()-2);
		
		if(params.length() == 0) {
			sendAction(chan, sender, "("+sender+") You flip a coin and get "+(rand.nextBoolean()?"heads":"tails")+isPM);
		} else if(params.length() > 0){
			int coins = Integer.parseInt(params);
			if(coins > 0) {
				for(int n=0; n<coins; n++) {
					if(rand.nextBoolean()) heads++;
					else tails++;
				}
				sendAction(chan, sender, "("+sender+") You flip "+coins+((coins!=1)?" coins":" coin")+" and get "+
						   heads+" heads and "+tails+" tails"+isPM);
			}
			else {
				sendMessage(chan, sender, "("+sender+") Invalid number of coins."+isPM);
			}
		} else {
			sendMessage(chan, sender, "Invalid Usage. Usage: '!coin [number of coins to flip]'"+isPM);
		}
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
		setCommandStart(new String[] {"coin"});
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
