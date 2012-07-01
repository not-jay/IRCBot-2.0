package plugins.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;

import org.jibble.pircbot.PircBot;

import plugins.SystemInfo;

import com.xtouchme.ircbot.BotOptions;

@PluginImplementation
@Author(name="xTouchMe")
public class SystemInfoImpl implements SystemInfo {

	private ArrayList<String> commandStart = new ArrayList<String>();
	private PircBot bot = null;
	
	@Override
	public void run(String params, String chan, String sender, BotOptions options) {
		String pcHostName = "";
		String operatingSystem = "";
		String osVersion = "";
		String javaVer = "";
		String osArchitecture = "";
		try {
			pcHostName = InetAddress.getLocalHost().getHostName();
			operatingSystem = System.getProperty("os.name");
			osVersion = System.getProperty("os.version");
			javaVer = System.getProperty("java.version");
			boolean is64bit = false;
			if (System.getProperty("os.name").contains("Windows")) {
			    is64bit = (System.getenv("ProgramFiles(x86)") != null);
			} else {
			    is64bit = (System.getProperty("os.arch").indexOf("64") != -1);
			}
			osArchitecture = (is64bit)?"x64":"x86";
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		sendMessage(chan, sender, "("+sender+") Hostname: "+pcHostName+", OS: "+operatingSystem+
					", OS-Version: "+osVersion+", OS-Arch: "+osArchitecture+", Java-Version: "+javaVer+params);
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
		setCommandStart(new String[] {"systeminfo", "sysinfo", "sys"});
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
