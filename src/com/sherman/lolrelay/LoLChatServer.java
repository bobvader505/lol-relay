package com.sherman.lolrelay;
import java.io.IOException;
import java.util.Properties;

import com.github.theholywaffle.lolchatapi.ChatServer;
import com.github.theholywaffle.lolchatapi.FriendRequestPolicy;
import com.github.theholywaffle.lolchatapi.LolChat;
import com.github.theholywaffle.lolchatapi.listeners.ChatListener;
import com.github.theholywaffle.lolchatapi.listeners.FriendListener;
import com.github.theholywaffle.lolchatapi.riotapi.RateLimit;
import com.github.theholywaffle.lolchatapi.riotapi.RiotApiKey;
import com.github.theholywaffle.lolchatapi.wrapper.Friend;

public class LoLChatServer {
	public static void main(String[] args){
		new LoLChatServer();
	}
	
	public LoLChatServer(){
		final LolChat api = new LolChat(ChatServer.NA,
				FriendRequestPolicy.ACCEPT_ALL, new RiotApiKey("f12fadad-50f3-4d04-b46d-2a620d811320",
						RateLimit.DEFAULT));
		if (api.login("shermanchathub", "thyroid1")) {
			new LoLChatClient(null, "chatwinningbot", "thyroid1");
			new LoLChatClient(null, "wpchat", "thyroid1");
			new LoLChatClient(null, "dreamchat", "thyroid1");
			new LoLChatClient(null, "napub", "thyroid1");
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
