package com.sherman.lolrelay;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.theholywaffle.lolchatapi.ChatServer;
import com.github.theholywaffle.lolchatapi.FriendRequestPolicy;
import com.github.theholywaffle.lolchatapi.LolChat;
import com.github.theholywaffle.lolchatapi.listeners.ChatListener;
import com.github.theholywaffle.lolchatapi.listeners.FriendListener;
import com.github.theholywaffle.lolchatapi.riotapi.RateLimit;
import com.github.theholywaffle.lolchatapi.riotapi.RiotApiKey;
import com.github.theholywaffle.lolchatapi.wrapper.Friend;

public class LoLChatServer {
	public static void main(String[] args) {
		try {
			new LoLChatServer();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public LoLChatServer() throws ParserConfigurationException, SAXException, IOException{
		File xmlFile = new File("config.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);
		
		doc.getDocumentElement().normalize();
		Element config = (Element) doc.getElementsByTagName("config").item(0);
		
		final LolChat api = new LolChat(ChatServer.NA,
				FriendRequestPolicy.ACCEPT_ALL, new RiotApiKey(config.getElementsByTagName("apikey").item(0).getTextContent(),
						RateLimit.DEFAULT));
		if (api.login(config.getElementsByTagName("username").item(0).getTextContent(), config.getElementsByTagName("password").item(0).getTextContent())) {
			NodeList servers = doc.getElementsByTagName("server");
			for(int i=0; i<servers.getLength(); i++)
				new LoLChatClient(api.getFriendByName(((Element)servers.item(i)).getElementsByTagName("owner").item(0).getTextContent()), 
						((Element)servers.item(i)).getElementsByTagName("username").item(0).getTextContent(), 
						((Element)servers.item(i)).getElementsByTagName("password").item(0).getTextContent());
			
			api.addChatListener(new ChatListener(){
				@Override
				public void onMessage(Friend friend, String message) {
					String[] command = message.split(" ");
					if(command[0].equals("launch")){
						new LoLChatClient(friend, command[1], command[2]);
					}
				}
			});
		}
	}
}
