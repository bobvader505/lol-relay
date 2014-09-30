package com.sherman.lolrelay;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.github.theholywaffle.lolchatapi.ChatServer;
import com.github.theholywaffle.lolchatapi.FriendRequestPolicy;
import com.github.theholywaffle.lolchatapi.LolChat;
import com.github.theholywaffle.lolchatapi.listeners.ChatListener;
import com.github.theholywaffle.lolchatapi.listeners.FriendListener;
import com.github.theholywaffle.lolchatapi.riotapi.RateLimit;
import com.github.theholywaffle.lolchatapi.riotapi.RiotApiKey;
import com.github.theholywaffle.lolchatapi.wrapper.Friend;

public class LoLChatServer {
	public static void main(String[] args) throws ParserConfigurationException {
		try {
			new LoLChatServer();
		} catch (Exception e) {
			LoLChatLogger.logError(e.toString());
		}
	}
	
	public LoLChatServer() throws Exception{
		File xmlFile = new File("config.xml");
		if(xmlFile.exists()){
			LoLChatLogger.logNotice("Loading server data from config.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
	
			LoLChatLogger.flags[LoLChatLogger.ERROR_FLAG] = Boolean.parseBoolean(((Element)doc.getElementsByTagName("logging").item(0)).getElementsByTagName("error").item(0).getTextContent());
			LoLChatLogger.flags[LoLChatLogger.WARNING_FLAG] = Boolean.parseBoolean(((Element)doc.getElementsByTagName("logging").item(0)).getElementsByTagName("warning").item(0).getTextContent());
			LoLChatLogger.flags[LoLChatLogger.DEBUG_FLAG] = Boolean.parseBoolean(((Element)doc.getElementsByTagName("logging").item(0)).getElementsByTagName("debug").item(0).getTextContent());
			LoLChatLogger.flags[LoLChatLogger.NOTICE_FLAG] = Boolean.parseBoolean(((Element)doc.getElementsByTagName("logging").item(0)).getElementsByTagName("notice").item(0).getTextContent());
			
			doc.getDocumentElement().normalize();
			
			Element config = (Element) doc.getElementsByTagName("config").item(0);
			final String apiKey = config.getElementsByTagName("apikey").item(0).getTextContent();
			LoLChatLogger.logNotice("Launching server with api key: " + apiKey);
			final LolChat api = new LolChat(ChatServer.NA,
					FriendRequestPolicy.ACCEPT_ALL, new RiotApiKey(apiKey,
							RateLimit.DEFAULT));
			LoLChatLogger.logNotice("Logging into server with supplied credentials (" + config.getElementsByTagName("username").item(0).getTextContent() + ", " + config.getElementsByTagName("password").item(0).getTextContent() + ")");
			if (api.login(config.getElementsByTagName("username").item(0).getTextContent(), config.getElementsByTagName("password").item(0).getTextContent())) {
				LoLChatLogger.logNotice("Loading saved client configs");
				NodeList servers = doc.getElementsByTagName("client");
				for(int i=0; i<servers.getLength(); i++)
					new LoLChatClient(api.getFriendByName(((Element)servers.item(i)).getElementsByTagName("owner").item(0).getTextContent()), 
							apiKey,
							((Element)servers.item(i)).getElementsByTagName("username").item(0).getTextContent(), 
							((Element)servers.item(i)).getElementsByTagName("password").item(0).getTextContent());
				
				api.addChatListener(new ChatListener(){
					@Override
					public void onMessage(Friend friend, String message) {
						String[] command = message.split(" ");
						if(command[0].equals("launch")){
							new LoLChatClient(friend, apiKey, command[1], command[2]);
						}
					}
				});
			}
		}else{
			LoLChatLogger.logError("config.xml not found");
		}
	}
}
