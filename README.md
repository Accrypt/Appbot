# Appbot

## Settings.json
```json
{
	"auth": {
		"username": "your_bot_username",
		"oauth": "your_bot_oauth_key",
		"clientid": "your_client_id"
	},
	"connect": {
		"channel": "your_stream_channel"
	}
}
```

<b>Username</b> - The username of the bot in the chat (e.g. Appbot) <br>
<b>OAuth</b> - This is essentially your password [(Get that here)](https://twitchapps.com/tmi/) <br>
<b>Client ID</b> - This is required to connect to the twitch api, make a client id [here](https://www.twitch.tv/kraken/oauth2/clients/new)
<br>
<b>Channel</b> - This is the default channel the application will connect to, just enter your twitch name in lower case

## Making a plugin

To create your first plugin for Appbot you need to include the compiled Appbot jar as a dependency to your project.
This is different depending on your IDE so check how to do that.

In your main class you want to impliment `AppbotPlugin`
Make sure this imports, it should require you add 2 methods: `onLoad()` and `onUnload()`

In the `onLoad()` method you can add your events and commands:

To add a command, include this in the onload method
```java
public void onLoad() {
	getCommandManager().registerCommand(this, "your_command_name", new MyCommand());
}
```
Then, in your `MyCommand` class impliment `CommandHandler`
```java
public class MyCommand inpliments CommandHandler {

	public void onCommand(Channel channel, User user, String command, String[] args) {
		channel.send("Hello " + user.getName())
	}

}
```
Compile this and drop the jar into the plugins folder of the application, restart and then when you type `your_command_name`, it will return "Hello [name]"
