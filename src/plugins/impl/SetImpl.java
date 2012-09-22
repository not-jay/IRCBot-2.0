package plugins.impl;

import java.util.ArrayList;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;

import org.jibble.pircbot.PircBot;

import com.xtouchme.ircbot.BotOptions;

import plugins.Set;

@PluginImplementation
@Author(name="xTouchMe")
public class SetImpl implements Set {

	private ArrayList<String> commandStart = new ArrayList<String>();
	private PircBot bot = null;
	
	@Override
	public void run(String params, String chan, String sender, BotOptions options) {
		String[] line = params.split(" ");
		if(!options.isModerator(sender)) { restrictions(chan, sender); return; }
		if(line[0].equalsIgnoreCase("event") && line.length >= 1) {
			String eventName = line[1];
			for(int n=2; n<line.length; n++) {
				eventName += " "+line[n];
			}
			String isPM = eventName.substring(eventName.length()-2, eventName.length());
			eventName = eventName.substring(0, eventName.length()-2);
			
			sendMessage(chan, sender, options.updateEvent(eventName)+isPM);
		}
		if(line[0].equalsIgnoreCase("countdown") && line.length >= 2) {
			String toRegex = line[1].substring(0, line[1].length()-2);
			String isPM = line[1].substring(line[1].length()-2, line[1].length());
			
			int year = Integer.parseInt(toRegex.substring(0, toRegex.indexOf('-')));
			int month = Integer.parseInt(toRegex.substring(toRegex.indexOf('-')+1,toRegex.lastIndexOf('-'))) - 1;
			int day = Integer.parseInt(toRegex.substring(toRegex.lastIndexOf('-')+1, toRegex.indexOf(',')));
			int hour = Integer.parseInt(toRegex.substring(toRegex.indexOf(',')+1, toRegex.indexOf(':')));
			int minute= Integer.parseInt(toRegex.substring(toRegex.indexOf(':')+1, toRegex.length()));
			
			sendMessage(chan, sender, options.setTarget(month, day, year, hour, minute)+isPM);
		}
		if(line[0].equalsIgnoreCase("greet") && line.length >= 1) {
			String isPM = line[1].substring(line[1].length()-2, line[1].length());
			String message = line[1].substring(0, line[1].length()-2);
			sendMessage(chan, sender, options.setGreet(Boolean.parseBoolean(message))+isPM);
		}
		if(line[0].equalsIgnoreCase("reconnect") && line.length >= 1) {
			String isPM = line[1].substring(line[1].length()-2, line[1].length());
			String message = line[1].substring(0, line[1].length()-2);
			sendMessage(chan, sender, options.setReconnect(Boolean.parseBoolean(message))+isPM);
		}
		if(line[0].equalsIgnoreCase("greet.channel") && line.length >= 2) {
			if(line[1].equalsIgnoreCase("add")) {
				String isPM = line[2].substring(line[2].length()-2, line[2].length());
				String message = line[2].substring(0, line[2].length()-2);
				sendMessage(chan, sender, options.addChan(message.toLowerCase())+isPM);
			}
			if(line[1].equalsIgnoreCase("remove")) {
				String isPM = line[2].substring(line[2].length()-2, line[2].length());
				String message = line[2].substring(0, line[2].length()-2);
				sendMessage(chan, sender, options.removeChan(message.toLowerCase())+isPM);
			}
		}
		if(line[0].equalsIgnoreCase("admin") && line.length >= 2) {
			if(line[1].equalsIgnoreCase("remove") && options.isSuperAdmin(sender)) {
				if(line.length >= 3) {
					String temp = line[2];
					for(int n=3; n<line.length; n++) {
						temp += " "+line[n];
					}
					String isPM = temp.substring(temp.length()-2, temp.length());
					String[] toRemove = temp.substring(0, temp.length()-2).split(" ");
					sendMessage(chan, sender, options.removeAdmin(toRemove)+isPM);
				}
				else {
					String isPM = line[2].substring(line[2].length()-2, line[2].length());
					String message = line[2].substring(0, line[2].length()-2);
					sendMessage(chan, sender, options.removeAdmin(message)+isPM);
				}
			}
			if(line[1].equalsIgnoreCase("add")) {
				if(line.length >= 3) {
					String temp = line[2];
					for(int n=3; n<line.length; n++) {
						temp += " "+line[n];
					}
					String isPM = temp.substring(temp.length()-2, temp.length());
					String[] toAdd = temp.substring(0, temp.length()-2).split(" ");
					sendMessage(chan, sender, options.addAdmin(toAdd)+isPM);
				}
				else sendMessage(chan, sender, options.addAdmin(line[2]));
			}
		}
		if(line[0].equalsIgnoreCase("ignore") && line.length >= 2) {
			if(!options.isAdmin(sender)) { restrictions(chan, sender);  }
			if(line[1].equalsIgnoreCase("add")) {
				if(line.length >= 3) {
					String temp = line[2];
					for(int n=3; n<line.length; n++) {
						temp += " "+line[n];
					}
					String isPM = temp.substring(temp.length()-2, temp.length());
					String[] toIgnore = temp.substring(0, temp.length()-2).split(" ");
					sendMessage(chan, sender, options.addToIgnore(toIgnore)+isPM);
				}
				else {
					String isPM = line[2].substring(line[2].length()-2, line[2].length());
					String message = line[2].substring(0, line[2].length()-2);
					sendMessage(chan, sender, options.addToIgnore(message)+isPM);
				}
			}
			if(line[1].equalsIgnoreCase("remove")) {
				if(line.length >= 3) {
					String temp = line[2];
					for(int n=3; n<line.length; n++) {
						temp += " "+line[n];
					}
					String isPM = temp.substring(temp.length()-2, temp.length());
					String[] toUnignore = temp.substring(0, temp.length()-2).split(" ");
					sendMessage(chan, sender, options.removeFromIgnore(toUnignore)+isPM);
				}
				else {
					String isPM = line[2].substring(line[2].length()-2, line[2].length());
					String message = line[2].substring(0, line[2].length()-2);
					sendMessage(chan, sender, options.removeFromIgnore(message)+isPM);
				}
			}
		}
		if(line[0].equalsIgnoreCase("mod") && line.length >= 2) {
			if(!options.isAdmin(sender)) {
				sendMessage(chan, sender, "You don't have enough previliges to perform this task");
			}
			if(line[1].equalsIgnoreCase("add")) {
				if(line.length >= 3) {
					String temp = line[2];
					for(int n=3; n<line.length; n++) {
						temp += " "+line[n];
					}
					String isPM = temp.substring(temp.length()-2, temp.length());
					String[] toAdd = temp.substring(0, temp.length()-2).split(" ");
					sendMessage(chan, sender, options.addModerator(toAdd)+isPM);
				}
				else {
					String isPM = line[2].substring(line[2].length()-2, line[2].length());
					String message = line[2].substring(0, line[2].length()-2);
					sendMessage(chan, sender, options.addModerator(message)+isPM);
				}
			}
			if(line[1].equalsIgnoreCase("remove")) {
				if(line.length >= 3) {
					String temp = line[2];
					for(int n=3; n<line.length; n++) {
						temp += " "+line[n];
					}
					String isPM = temp.substring(temp.length()-2, temp.length());
					String[] toRemove = temp.substring(0, temp.length()-2).split(" ");
					sendMessage(chan, sender, options.removeModerator(toRemove)+isPM);
				}
				else {
					String isPM = line[2].substring(line[2].length()-2, line[2].length());
					String message = line[2].substring(0, line[2].length()-2);
					sendMessage(chan, sender, options.removeModerator(message)+isPM);
				}
			}
		}
		if(line[0].substring(0, line[0].length()-2).equalsIgnoreCase("save")) {
			options.save();
			sendMessage(chan, sender, "Saving current options..."+line[0].substring(line[0].length()-2, line[0].length()));
		}
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
		setCommandStart(new String[] {"settings", "set", "se"});
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
