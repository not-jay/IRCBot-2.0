package com.xtouchme.ircbot;

import java.io.File;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.options.addpluginsfrom.OptionReportAfter;

import org.jibble.pircbot.PircBot;

import plugins.CommandPlugin;

public class TwitchBot extends PircBot {

	//name, server, port, chan, owner nick, options file defaults
	static Object[] botOptions = new Object[] {
		"Pircbot", "irc.epsilonirc.net", 6667, "#xTouchMe", "xTouchMe", "default.options"
	};
	QuoteDB qdb;
	BotOptions options;
	CommandPlugin commands;
	
	@Override
	protected void onMessage(String channel, String sender, String login,
			String hostname, String message) {
		if(options.shouldIgnore(sender) || (options.shouldShutUp() && !options.isAdmin(sender))) return;
		if(message.startsWith("$")) {
			if(!options.isModerator(sender)) {
				sendMessage(channel, "You don't have enough previlages to perform this task");
				return;
			}
			String identifier = message.split(" ")[0].substring(1);
			sendMessage(channel, "["+identifier+"]: "+qdb.retrieveQuote(identifier));
		} else if(message.startsWith("!") || message.startsWith(getNick()+" ") ||
				  message.startsWith(getNick()+", ")) {
			if(message.startsWith(getNick()+" ") || message.startsWith(getNick()+", ")) {
				message = '!' + (String)message.subSequence(message.indexOf(' ')+1, message.length());
			}
			commands.checkForCommands(message, channel, sender, false, options);
		}
	}

	@Override
	protected void onPrivateMessage(String sender, String login,
			String hostname, String message) {
		if(options.shouldIgnore(sender)) return;
		if(message.startsWith("$")) {
			if(!options.isModerator(sender)) {
				sendNotice(sender, "You don't have enough previlages to perform this task");
				return;
			}
			String identifier = message.split(" ")[0].substring(1);
			sendNotice(sender, "["+identifier+"]: "+qdb.retrieveQuote(identifier));
		} else {
			commands.checkForCommands("!"+message, null, sender, true, options);
		}
	}

	@Override
	protected void onServerResponse(int code, String response) {
		if(code==004) {	sendRawLine("MODE "+getNick()+" :+B"); }
	}

	@Override
	protected void onVersion(String sourceNick, String sourceLogin,
			String sourceHostname, String target) {
		sendNotice(sourceNick, "\u0001VERSION xTouchMeBot - PircBot 1.5.0 - http://git.io/xtouchmebot \u0001");
	}

	@Override
	protected void onInvite(String targetNick, String sourceNick,
			String sourceLogin, String sourceHostname, String channel) {
		joinChannel(channel);
		sendMessage(channel, "Thanks for the invite, "+sourceNick);
	}

	@Override
	protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname,
			String recipientNick, String reason) {
		if (recipientNick.equalsIgnoreCase(getNick()) && options.reconnect) {
			joinChannel(channel);
			sendMessage(channel, "Why would you do that, "+kickerNick+"? ಥ_ಥ");
		}
	}

	//Checks if the bot is disconnected, if it is, shut the program down gracefully
	@net.xeoh.plugins.base.annotations.Thread
	public void checkBotShutdown() {
		while(true) { if(!isConnected()) System.exit(0); }
	}
	
	public TwitchBot() {
		qdb = QuoteDB.getQuoteDB();
		options = BotOptions.getOptions((String)botOptions[4], (String)botOptions[5]);
		PluginManager pluginManager = PluginManagerFactory.createPluginManager();
		
		pluginManager.addPluginsFrom(new File("IRCBot/plugins/").toURI(), new OptionReportAfter());
		commands = pluginManager.getPlugin(CommandPlugin.class);
		commands.load(this);
	}
	
	public static void main(String[] args) {
		if(args.length > 5) { botOptions[5] = args[5]; }
		if(args.length > 4) { botOptions[4] = args[4]; }
		if(args.length > 3) { 
			if(args[3].startsWith("#")) { botOptions[3] = args[3]; }
			else { System.out.println("Invalid channel name, should start with #"); }
		}
		if(args.length > 2) { botOptions[2] = args[2]; }
		if(args.length > 1) { botOptions[1] = args[1]; }
		if(args.length > 0) { botOptions[0] = args[0]; }
		
		TwitchBot bot = new TwitchBot();
		bot.setName((String)botOptions[0]);
		bot.startIdentServer();
		bot.setVerbose(true);
		bot.setMessageDelay(250L);
		try {
			bot.setEncoding("UTF-8");
			bot.connect((String)botOptions[1], Integer.parseInt(String.valueOf(botOptions[2])), Password.getPass());
			//bot.identify(Password.getPass());
			Thread.sleep(1000);
		} catch(Exception e) {
			System.err.print("An error has occured - ");
			e.printStackTrace();
		}
		bot.joinChannel((String)botOptions[3]);
		//bot.checkBotShutdown();
	}
	
}
