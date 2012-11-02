package plugins.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;

import org.jibble.pircbot.PircBot;

import plugins.Twitter;

import com.xtouchme.ircbot.BotOptions;

@PluginImplementation
@Author(name="xTouchMe")
public class TwitterImpl implements Twitter {

	private ArrayList<String> commandStart = new ArrayList<String>();
	private PircBot bot = null;
	
	@Override
	public void run(String params, String chan, String sender, BotOptions options) {
		ArrayList<String> tweets = new ArrayList<String>();
		ArrayList<String> dates = new ArrayList<String>();
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Calendar then = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		
		String isPM = params.substring(params.length()-2, params.length());
		params = params.substring(0, params.length()-2);
		String messages[] = params.split(" ");
		if(!options.isModerator(sender)) { restrictions(chan, sender, isPM.equals("PM")); return; }

		URL twitter;
		try {
			twitter = new URL("https://api.twitter.com/1/statuses/user_timeline.rss?screen_name="+messages[0]);
			BufferedReader in = new BufferedReader(new InputStreamReader(twitter.openStream()));
			Pattern patternTitle;
			Matcher matcher;

			String currLine;
			while((currLine = in.readLine()) != null) {
				patternTitle = Pattern.compile("<pubDate>.*?</pubDate>");
				matcher = patternTitle.matcher(currLine);
				if(matcher.find()) {
					String[] forCalendar = null;
					String date = matcher.group().replaceAll("</?pubDate>", "");
					date = date.substring(5, date.length());
					forCalendar = date.split(" ");
					
					for(int x = 0; x < months.length; x++) {
						if(forCalendar[1].equals(months[x])) {
							forCalendar[1] = String.valueOf(x);
							break;
						}
					}
					
					then.set(Integer.parseInt(forCalendar[2]), Integer.parseInt(forCalendar[1]), Integer.parseInt(forCalendar[0]), 
							 Integer.parseInt(forCalendar[3].substring(0, forCalendar[3].indexOf(':'))), 
							 Integer.parseInt(forCalendar[3].substring(forCalendar[3].indexOf(':', forCalendar[3].indexOf(':'))+1,
									 forCalendar[3].lastIndexOf(':'))));
					
					long delta = now.getTimeInMillis() - then.getTimeInMillis();
					long diffMinutes = delta / (60 * 1000);
					long diffHours = delta / (60 * 60 * 1000);
					long diffDays = delta / (24 * 60 * 60 * 1000);
				
					long excessHours = diffHours - (diffDays * 24);
					long excessMinutes = diffMinutes - (diffHours * 60);
					
					if(diffDays == 0) {
						if(excessHours == 0 && excessMinutes == 0) dates.add(" [Moments ago]");
						if(excessHours == 0 && excessMinutes == 1) dates.add(" [A minute ago]");
						if(excessHours == 0 && excessMinutes > 1) {
							dates.add(" ["+String.format("%2d minutes ago]", excessMinutes));
						}
						if(excessHours == 1 && excessMinutes == 0) dates.add(" [An hour ago]");
						if(excessHours == 1 && excessMinutes == 1) dates.add(" [An hour and a minute ago]");
						if(excessHours == 1 && excessMinutes > 1) {
							dates.add(" ["+String.format("An hour and %2d minutes ago]", excessMinutes));
						}
						if(excessHours > 1 && excessMinutes == 0) {
							dates.add(" ["+String.format("%d hours ago]", excessHours));
						}
						if(excessHours > 1 && excessMinutes == 1) {
							dates.add(" ["+String.format("%d hours and a minute ago]", excessHours));
						}
						if(excessHours > 1 && excessMinutes > 1) {
							dates.add(" ["+String.format("%d hours and %2d minutes ago]", excessHours, excessMinutes));
						}
					} else if(diffDays == 1) {
						if(excessHours == 0 && excessMinutes == 0) dates.add(" [A day ago]");
						if(excessHours == 0 && excessMinutes == 1) dates.add(" [A day and a minute ago]");
						if(excessHours == 0 && excessMinutes > 1) {
							dates.add(" [A day and "+String.format("%2d minutes ago]", excessMinutes));
						}
						if(excessHours == 1 && excessMinutes == 0) dates.add(" [A day and an hour ago]");
						if(excessHours == 1 && excessMinutes == 1) dates.add(" [A day, an hour and a minute ago]");
						if(excessHours == 1 && excessMinutes > 1) {
							dates.add(" [A day, an hour and "+String.format("%2d minutes ago]", excessMinutes));
						}
						if(excessHours > 1 && excessMinutes == 0) {
							dates.add(" [A day and "+String.format("%d hours ago]", excessHours));
						}
						if(excessHours > 1 && excessMinutes == 1) {
							dates.add(" [A day, "+String.format("%d hours and a minute ago]", excessHours));
						}
						if(excessHours > 1 && excessMinutes > 1) {
							dates.add(" [A day, "+String.format("%d hours and %2d minutes ago]", excessHours, excessMinutes));
						}
					} else {
						if(excessHours == 0 && excessMinutes == 0) {
							dates.add(" ["+String.format("%d days ago]", diffDays));
						}
						if(excessHours == 0 && excessMinutes == 1) {
							dates.add(" ["+String.format("%d days and a minute ago]", diffDays));
						}
						if(excessHours == 0 && excessMinutes > 1) {
							dates.add(" ["+String.format("%d days and %2d minutes ago]", diffDays, excessMinutes));
						}
						if(excessHours == 1 && excessMinutes == 0) {
							dates.add(" ["+String.format("%d days and an hour ago]", diffDays));
						}
						if(excessHours == 1 && excessMinutes == 1) {
							dates.add(" ["+String.format("%d days, an hour and a minute ago]", diffDays));
						}
						if(excessHours == 1 && excessMinutes > 1) {
							dates.add(" ["+String.format("%d days, an hour and %2d minutes ago]", diffDays, excessMinutes));
						}
						if(excessHours > 1 && excessMinutes == 0) {
							dates.add(" ["+String.format("%d days and %d hours ago]", diffDays, excessHours));
						}
						if(excessHours > 1 && excessMinutes == 1) {
							dates.add(" ["+String.format("%d days, %d hours and a minute ago]", diffDays, excessHours));
						}
						if(excessHours > 1 && excessMinutes > 1) {
							dates.add(" ["+String.format("%d days, %d hours and %2d minutes ago]", diffDays, excessHours, excessMinutes));
						}
					}
				}
				patternTitle = Pattern.compile("<title>.*?</title>");
				matcher = patternTitle.matcher(currLine);
				if(matcher.find()) {
					tweets.add("@"+matcher.group().replaceAll("</?title>", "").replaceAll("[.]", "&#46"));
				}
			}

			tweets.remove(0); //Removes the starting "Twitter / *"
			if(messages.length > 1) { 
				sendMessage(chan, sender, tweets.get(Integer.parseInt(messages[1])-1)+dates.get(Integer.parseInt(messages[1])-1)+isPM);
			}
			else { sendMessage(chan, sender, tweets.get(0)+dates.get(0)+isPM); }
		} catch (FileNotFoundException e) {
			sendMessage(chan, sender, "Sorry, this user/account does not exist!"+isPM);
		} catch(IndexOutOfBoundsException e) {
			sendMessage(chan, sender,
					(Integer.parseInt(messages[1])-1 < 0)?"Sorry, I cannot predict the future."+isPM:
					"Sorry, cannot retrieve more than 20 tweets"+isPM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void restrictions(String channel, String sender, boolean isPM) {
		sendMessage(channel, sender, "You don't have enough privileges to perform this task"+((isPM)?"PM":"NM"));
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
		setCommandStart(new String[] {"twitter", "tw"});
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
