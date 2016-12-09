package io.mzb.Appbot;

import io.mzb.Appbot.commands.CommandManager;
import io.mzb.Appbot.commands.test.HelloWorldCommand;
import io.mzb.Appbot.events.EventListener;
import io.mzb.Appbot.events.EventManager;
import io.mzb.Appbot.log.AppbotLogger;
import io.mzb.Appbot.plugin.PluginManager;
import io.mzb.Appbot.threads.TaskManager;
import io.mzb.Appbot.twitch.Channel;
import io.mzb.Appbot.twitch.irc.IRCHandler;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Appbot extends EventListener {

    // Needed to read the twitch api, do not remove!
    private static String BOT_NAME, BOT_OAUTH, BOT_CLIENTID;
    private static Channel CHANNEL;

    private static final String defaultSettingsJson = "{\n" +
            "  \"auth\": {\n" +
            "    \"username\": \"your_bot_username\",\n" +
            "    \"oauth\": \"your_bot_oauth_key\",\n" +
            "    \"clientid\": \"your_client_id\"\n" +
            "  },\n" +
            "  \"connect\": {\n" +
            "    \"channel\": \"your_stream_channel\"\n" +
            "  }\n" +
            "}";

    private static TaskManager taskManager;
    private static PluginManager pluginManager;
    private static IRCHandler ircHandler;
    private static EventManager eventManager;
    private static CommandManager commandManager;

    public Appbot() throws IOException, InterruptedException, ParseException {
        // Setup logger
        AppbotLogger logger = new AppbotLogger(new PrintStream(new FileOutputStream(getNewLogFile())), System.out);
        System.setOut(logger);
        System.setErr(logger);
        // Task manager init

        System.out.println("Starting Appbot version beta-1");

        taskManager = new TaskManager();
        eventManager = new EventManager();
        commandManager = new CommandManager();

        // Setup plugin folder
        if (!getPluginsFolder().exists()) {
            getPluginsFolder().mkdirs();
            System.out.println("Plugin folder made for the first time");
        }

        // Setup settings file
        if (!getSettingsFile().exists()) {
            getSettingsFile().createNewFile();
            FileUtils.writeStringToFile(getSettingsFile(), defaultSettingsJson, "UTF-8");
            System.out.println("Settings file made for the first time");
            System.out.println("Please configure your settings file before starting this bot again!");
            Thread.sleep(5000);
            return;
        }

        // Load settings
        JSONObject settings = (JSONObject) new JSONParser().parse(FileUtils.readFileToString(getSettingsFile(), "UTF-8"));
        if (settings == null) {
            // Settings file does not exist!
            System.err.println("Settings file null!");
            return;
        }
        // Get auth section of the settings
        JSONObject auth = (JSONObject) settings.get("auth");
        if (auth == null) {
            // Settings file does not contain auth section!
            System.err.println("Settings file does not contain auth!");
            return;
        }
        if (!auth.containsKey("username")) {
            // Auth settings does not contain username
            System.err.println("Settings auth does not contain username!");
            return;
        }
        // Set bot name from the auth
        this.BOT_NAME = auth.get("username").toString();
        if (!auth.containsKey("oauth")) {
            // Auth does not contain oauth token
            System.err.println("Settings auth does not contain oauth!");
            return;
        }
        // Set oauth token from auth
        this.BOT_OAUTH = auth.get("oauth").toString();
        if (!auth.containsKey("clientid")) {
            // Client Id not in auth settings
            System.err.println("Settings auth does not contain clientid!");
            return;
        }
        // Set client id from auth
        this.BOT_CLIENTID = auth.get("clientid").toString();

        // Get connection part of settings
        JSONObject connection = (JSONObject) settings.get("connect");
        if (auth == null) {
            System.err.println("Settings file does not contain connection!");
            return;
        }

        this.CHANNEL = new Channel(connection.get("channel").toString().toLowerCase(), () -> {
            if (CHANNEL.isValid()) {
                // Plugin - only loaded if channel is valid!
                pluginManager = new PluginManager(getPluginsFolder());

                System.out.println("Default channel loaded: " + CHANNEL.getName());

                System.out.println("Connecting to IRC");
                ircHandler = new IRCHandler();
                ircHandler.connect();
                ircHandler.sendAuth();
                CHANNEL.joinIrc();

                commandManager.registerCommand("hello", new HelloWorldCommand());
            } else {
                System.out.println("Default channel is invalid: " + CHANNEL.getInvalidReason());
                System.out.println("Please make sure that you have typed the channel name correctly!");
                System.exit(1);
            }
        });

        Appbot.getTaskManager().runTask(() -> {
            CHANNEL.chat("Testing!");
        }, 1000 * 10);
    }

    public static TaskManager getTaskManager() {
        return taskManager;
    }

    public static EventManager getEventManager() {
        return eventManager;
    }

    public static String getClientId() {
        return BOT_CLIENTID;
    }

    public static String getName() {
        return BOT_NAME;
    }

    public static String getOAuthToken() {
        return BOT_OAUTH;
    }

    public static Channel getDefaultChannel() {
        return CHANNEL;
    }

    /*
    Program init point
     */
    public static void main(String[] args) {

        try {
            new Appbot();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private File getActiveLocation() {
        try {
            return new File(Appbot.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private File getPluginsFolder() {
        return new File(getActiveLocation(), "plugins");
    }

    private File getSettingsFile() {
        return new File(getActiveLocation(), "settings.json");
    }

    private File getLogsFile() {
        return new File(getActiveLocation(), "logs");
    }

    private File getNewLogFile() {
        if (!getLogsFile().exists()) {
            getLogsFile().mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        File file = new File(getLogsFile(), sdf.format(new Date()) + ".txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return file;
    }

    public static IRCHandler getIrcHandler() {
        return ircHandler;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

}
