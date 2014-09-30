package com.sherman.lolrelay;
import com.github.theholywaffle.lolchatapi.ChatServer;
import com.github.theholywaffle.lolchatapi.FriendRequestPolicy;
import com.github.theholywaffle.lolchatapi.LolChat;
import com.github.theholywaffle.lolchatapi.LolStatus;
import com.github.theholywaffle.lolchatapi.LolStatus.Division;
import com.github.theholywaffle.lolchatapi.LolStatus.Queue;
import com.github.theholywaffle.lolchatapi.LolStatus.Tier;
import com.github.theholywaffle.lolchatapi.listeners.ChatListener;
import com.github.theholywaffle.lolchatapi.listeners.FriendListener;
import com.github.theholywaffle.lolchatapi.riotapi.RateLimit;
import com.github.theholywaffle.lolchatapi.riotapi.RiotApiKey;
import com.github.theholywaffle.lolchatapi.wrapper.Friend;
import com.github.theholywaffle.lolchatapi.wrapper.FriendGroup;

public class LoLChatClient {

	public LoLChatClient(Friend host, String username, String password) {
		final LolChat api = new LolChat(ChatServer.NA,
				FriendRequestPolicy.ACCEPT_ALL, new RiotApiKey("f12fadad-50f3-4d04-b46d-2a620d811320",
						RateLimit.DEFAULT));
		if (api.login(username, password)) {
			if(host!=null)
				host.sendMessage("Instance succesfully launched");
			final LolStatus newStatus = new LolStatus();
			newStatus.setLevel(1337);
			newStatus.setRankedLeagueQueue(Queue.RANKED_SOLO_5x5);
			newStatus.setRankedLeagueTier(Tier.DIAMOND);
			newStatus.setRankedLeagueDivision(Division.I);
			newStatus.setRankedLeagueName("Fiora's Archknights");
			api.setStatus(newStatus);
			if(api.getFriendGroupByName("muted") == null)
				api.addFriendGroup("muted");
			if(api.getFriendGroupByName("admin") == null)
				api.addFriendGroup("admin");
			
			System.out.println(api.getName(true));
			for(FriendGroup group : api.getFriendGroups()){
				System.out.println("\t+"+group.getName());
				for(Friend friend : group.getFriends())
					System.out.println("\t\t-"+friend.getName());
			}

			api.addChatListener(new ChatListener(){
				@Override
				public void onMessage(Friend friend, String message) {
					switch(message.split(" ")[0]){
					case "-help":
						friend.sendMessage("-online: Get all online users");
						friend.sendMessage("-mute: Mute this chat");
						friend.sendMessage("-unmute: Unmute this chat");
						if(friend.getGroup().getName().equals("admin")){
							friend.sendMessage("-kick [user]: Kick [user] from chat");
							friend.sendMessage("-promote [user]: Promote [user] to admin");
						}
						break;
					case "-online":
						friend.sendMessage("Online Users:");
						for (final Friend f : api.getOnlineFriends())
							friend.sendMessage(f.getName());
						break;
					case "-mute":
						api.getFriendGroupByName("muted").addFriend(friend);
						friend.sendMessage("You have muted chat.  You will not recieve messages until you use -unmute.");
						break;
					case "-unmute":
						api.getFriendGroupByName("**Default").addFriend(friend);
						friend.sendMessage("You have unmuted chat.");
						break;
					case "-kick":
						String kicked = message.substring(message.indexOf(" "));
						if(friend.getGroup().getName().equals("admin"))
							if(!api.getFriendByName(kicked).getName().equals("admin") && api.getFriendByName(kicked).delete())
								friend.sendMessage("User removed");
							else
								friend.sendMessage("User can't be removed");
						break;
					case "-promote":
						if(friend.getGroup().getName().equals("admin")){
							api.getFriendGroupByName("admin").addFriend(api.getFriendByName(message.substring(message.indexOf(" "))));
							friend.sendMessage("User promoted to admin");
						}
						break;
					default:
						for (final Friend f : api.getOnlineFriends()) {
							if(!f.getName().equals(friend.getName()) && !f.getGroup().getName().equals("muted"))
								f.sendMessage(friend.getName() + ": " + message);
						}
						break;
					}
				}
			});
		}else{
			if(host!=null)
				host.sendMessage("Instance failed to launch");
		}
	}

}
