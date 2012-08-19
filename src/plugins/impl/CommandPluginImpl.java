package plugins.impl;

import java.io.File;
import java.util.ArrayList;

import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;
import net.xeoh.plugins.base.annotations.meta.Author;
import net.xeoh.plugins.base.impl.PluginManagerFactory;

import org.jibble.pircbot.PircBot;

import plugins.Choose;
import plugins.Coin;
import plugins.CommandPlugin;
import plugins.Countdown;
import plugins.DC;
import plugins.EightBall;
import plugins.Flirt;
import plugins.Get;
import plugins.Google;
import plugins.Help;
import plugins.Me;
import plugins.MemUsage;
import plugins.QDB;
import plugins.Say;
import plugins.Set;
import plugins.ShutUp;
import plugins.Slap;
import plugins.Speak;
import plugins.SystemInfo;
import plugins.Twitter;

import com.xtouchme.ircbot.BotOptions;

@PluginImplementation
@Author(name="xTouchMe")
public class CommandPluginImpl implements CommandPlugin {

	private ArrayList<PluginBase> plugins = new ArrayList<PluginBase>();
	private PircBot currentBot = null;
	
	@InjectPlugin
	public PluginManager pluginManager;
	
	@Override
	/** 
	 * @params -- Should still be the whole line of message the user would send
	 */
	public void checkForCommands(String params, String chan, String sender, boolean isPM, BotOptions options) {
		String[] line = params.split(" ");
		for(PluginBase p : plugins) {
			//Separates the unload/reload logic to do it a tad bit easier
			if(line[0].substring(1).equalsIgnoreCase("reload")) { reloadPlugins(); return; }
			if(line[0].substring(1).equalsIgnoreCase("unload")) { unloadPlugins(); return; }
			if(p.checkForCommand(line[0].substring(1))) {
				//The string passed here should be the string minus the "!<command>"
				p.run(params.substring(("!"+line[0]).length()-1).trim().concat((isPM)?"PM":"NM"), chan, sender, options);
				return;
			}
		}
		//Would only reach here if it truly doesn't recognize a command
		if(isPM) { currentBot.sendNotice(sender, "I don't recognize the command '"+line[0]+"'"); }
		else { currentBot.sendMessage(chan, "I don't recognize the command '"+line[0]+"'"); }
	}

	@Override
	public void load(PircBot bot) {
		currentBot = bot;
		
		//Should replace this with a loop sometime later
		Say say = pluginManager.getPlugin(Say.class);
		Me me = pluginManager.getPlugin(Me.class);
		DC dc = pluginManager.getPlugin(DC.class);
		Set set = pluginManager.getPlugin(Set.class);
		Countdown cd = pluginManager.getPlugin(Countdown.class);
		Slap slap = pluginManager.getPlugin(Slap.class);
		SystemInfo sys = pluginManager.getPlugin(SystemInfo.class);
		MemUsage mem = pluginManager.getPlugin(MemUsage.class);
		Choose ch = pluginManager.getPlugin(Choose.class);
		QDB qdb = pluginManager.getPlugin(QDB.class);
		ShutUp su = pluginManager.getPlugin(ShutUp.class);
		Help h = pluginManager.getPlugin(Help.class);
		Speak sp = pluginManager.getPlugin(Speak.class);
		EightBall ball = pluginManager.getPlugin(EightBall.class);
		Flirt flirt = pluginManager.getPlugin(Flirt.class);
		Google google = pluginManager.getPlugin(Google.class);
		Get get = pluginManager.getPlugin(Get.class);
		Coin coin = pluginManager.getPlugin(Coin.class);
		Twitter twitter = pluginManager.getPlugin(Twitter.class);
		
		//Add them in the ArrayList to iterate over later
		plugins.add(say);
		plugins.add(me);
		plugins.add(dc);
		plugins.add(set);
		plugins.add(cd);
		plugins.add(slap);
		plugins.add(sys);
		plugins.add(mem);
		plugins.add(ch);
		plugins.add(qdb);
		plugins.add(su);
		plugins.add(h);
		plugins.add(sp);
		plugins.add(ball);
		plugins.add(flirt);
		plugins.add(google);
		plugins.add(get);
		plugins.add(coin);
		plugins.add(twitter);
		
		for(PluginBase p : plugins) { p.load(bot); }
	}

	private void unloadPlugins() {
		System.out.println(">>Unloading plugins...");
		for(PluginBase p : plugins) { p.unload(); }
		plugins.clear();
		//Shutdown the plugin manager
		if(pluginManager != null) {
			pluginManager.shutdown();
			System.out.println(">>Unloaded successfully.");
		} else {
			System.out.println(">>Plugins are already unloaded.");
		}
	}
	
	private void reloadPlugins() {
		System.out.println(">>Reseting plugin manager...");
		if(pluginManager != null) pluginManager.shutdown();
		else System.out.println(">>Plugin manager already down, starting it up");
		System.out.println(">>Loading plugins...");
		pluginManager = PluginManagerFactory.createPluginManager();
		pluginManager.addPluginsFrom(new File("plugins/").toURI());
	}

}
