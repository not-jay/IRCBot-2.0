package plugins.impl;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;

import org.jibble.pircbot.Colors;
import org.jibble.pircbot.PircBot;

import plugins.Google;

import com.google.gson.Gson;
import com.xtouchme.ircbot.BotOptions;

@PluginImplementation
@Author(name="xTouchMe")
public class GoogleImpl implements Google {

	private ArrayList<String> commandStart = new ArrayList<String>();
	private PircBot bot = null;
	
	@Override
	public void run(String params, String chan, String sender, BotOptions options) {
		String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&safe=moderate&q=";
		String isPM = params.substring(params.length()-2, params.length());
		params = params.substring(0, params.length()-2);
		
		if(params.length() <= 0) { error(chan, sender, isPM.equals("PM")); return; }
		
		params = params.replaceAll(" ", "+");
		try {
			params = URLEncoder.encode(params, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			URL googleSearch = new URL(google + params);
			Reader streamReader = new InputStreamReader(googleSearch.openStream(), "UTF-8");
			GoogleResults result = new Gson().fromJson(streamReader, GoogleResults.class);
			
			String resultStr = "("+sender+") ";
			resultStr += result.getResponseData().getResults().get(0).getUrl() + " - ";
			resultStr += Colors.BOLD + result.getResponseData().getResults().get(0).getTitle();
			resultStr += Colors.NORMAL + ": " + result.getResponseData().getResults().get(0).getContent();
			resultStr = resultStr.replaceAll("<b>", "\\x02");
			resultStr = resultStr.replaceAll("</b>", "\\x02");
			resultStr = resultStr.replaceAll("[.]", "&#46");
			
			sendMessage(chan, sender, resultStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void error(String channel, String sender, boolean isPM) {
		sendMessage(channel, sender, "Invalid usage. Usage: '!google <search terms>'"+((isPM)?"PM":"NM"));
	}

	private void sendMessage(String channel, String sender, String sendWhat) {
		//If its a normal message
		if(sendWhat.substring(sendWhat.length()-2, sendWhat.length()).equals("NM")) {
			bot.sendAction(channel, sendWhat.substring(0, sendWhat.length()-2));
		} else { //Otherwise, its a pm
			bot.sendNotice(sender, sendWhat.substring(0, sendWhat.length()-2));
		}
	}

	@Override
	public void load(PircBot bot) {
		setCommandStart(new String[] {"google", "goog", "g"});
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
	
	/**
	 * Private Inner Class -- GoogleResults
	 */
	private class GoogleResults {

	    private ResponseData responseData;
	    public ResponseData getResponseData() { return responseData; }
	    public void setResponseData(ResponseData responseData) { this.responseData = responseData; }
	    public String toString() { return "ResponseData[" + responseData + "]"; }

	    class ResponseData {
	        private List<Result> results;
	        public List<Result> getResults() { return results; }
	        public void setResults(List<Result> results) { this.results = results; }
	        public String toString() { return "Results[" + results + "]"; }
	    }

	    class Result {
	        private String url;
	        private String title;
	        private String content;
	        public String getUrl() { return url; }
	        public String getTitle() { return title; }
	        public String getContent() { return content; }
	        public void setUrl(String url) { this.url = url; }
	        public void setTitle(String title) { this.title = title; }
	        public void setContent(String content) { this.content = content; }
	        public String toString() { return "Result[url:" + url +",title:" + title + ",content:" + content + "]"; }
	    }

	}
}
