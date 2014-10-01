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
import com.github.theholywaffle.lolchatapi.listeners.FriendRequestListener;
import com.github.theholywaffle.lolchatapi.riotapi.RateLimit;
import com.github.theholywaffle.lolchatapi.riotapi.RiotApiKey;
import com.github.theholywaffle.lolchatapi.wrapper.Friend;
import com.github.theholywaffle.lolchatapi.wrapper.FriendGroup;

public class LoLChatClient {

	public LoLChatClient(Friend host, String apikey, String username, String password) {
		final LolChat api = new LolChat(ChatServer.NA,
				FriendRequestPolicy.MANUAL, new RiotApiKey(apikey,
						RateLimit.DEFAULT));
		api.setFriendRequestListener(new FriendRequestListener() {

			public boolean onFriendRequest(String userId, String name) {
				LoLChatLogger.logNotice(name + " has joined " + api.getName(true));
				for (final Friend f : api.getOnlineFriends()) {
					if(!f.getGroup().getName().equals("muted"))
						f.sendMessage(name + " has joined the channel.");
				}
				return true; // Accept user
			}
		});
		if (api.login(username, password)) {
			LoLChatLogger.logNotice(api.getName(true) + " launched succesfully.");
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
			if(host!=null){
				host.sendMessage("Instance succesfully launched");
				Friend admin = api.getFriendByName(host.getName());
				if(admin==null)
					api.addFriendByName(host.getName(), api.getFriendGroupByName("admin"));
				else
					api.getFriendGroupByName("admin").addFriend(admin);
			}
			for(FriendGroup group : api.getFriendGroups()){
				LoLChatLogger.logDebug("\t+"+group.getName());
				for(Friend friend : group.getFriends())
					LoLChatLogger.logDebug("\t\t-"+friend.getName());
			}
			api.addChatListener(new ChatListener(){
				@Override
				public void onMessage(Friend friend, String message) {
					switch(message.split(" ")[0]){
					case "-help":
						friend.sendMessage("-online: Get all online users");
						friend.sendMessage("-mute: Mute this chat");
						friend.sendMessage("-unmute: Unmute this chat");
						friend.sendMessage("-invite [user]: Invite [user] to chat");
						friend.sendMessage("-roll: Roll a number 1-100");
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
					case "-invite":
						api.addFriendByName(message.substring(message.indexOf(" ")), api.getFriendGroupByName("**Default"));
						LoLChatLogger.logNotice(friend.getName() + " invited " + message.substring(message.indexOf(" ")) + " to join " + api.getName(true));
						break;
					case "-roll":
						int num = (int) (Math.random() * 100);
						for (final Friend f : api.getOnlineFriends()) {
							if(!f.getGroup().getName().equals("muted"))
								f.sendMessage(friend.getName() + " rolls a " + num + " (1-100)");
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
			LoLChatLogger.logError(api.getName(true) + " launched succesfully.");
		}
	}
}
