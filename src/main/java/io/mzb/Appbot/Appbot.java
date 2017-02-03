package io.mzb.Appbot;

import io.mzb.Appbot.commands.CommandManager;
import io.mzb.Appbot.events.EventListener;
import io.mzb.Appbot.events.EventManager;
import io.mzb.Appbot.log.AppbotLogger;
import io.mzb.Appbot.plugin.PluginManager;
import io.mzb.Appbot.threads.ShutdownThread;
import io.mzb.Appbot.threads.TaskManager;
import io.mzb.Appbot.twitch.Channel;
import io.mzb.Appbot.twitch.irc.IRCHandler;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Appbot extends EventListener {

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

    private final String VERSION = "beta-1";

    private static String BOT_NAME, BOT_OAUTH, BOT_CLIENTID;
    private static Channel CHANNEL;
    private static TaskManager taskManager;
    private static PluginManager pluginManager;
    private static IRCHandler ircHandler;
    private static EventManager eventManager;
    private static CommandManager commandManager;

    public Appbot() throws IOException, InterruptedException, ParseException {
        System.out.println("Starting Appbot version " + VERSION);
        System.out.println("Active location: " + getActiveLocation().toString());

        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        setupLogger();
        setupPluginsFolder();
        setupSettingsFile();

        taskManager = new TaskManager();

        loadSettings(() -> {
            if (CHANNEL.isValid()) {
                System.out.println("Loading managers");

                // Load only if a default channel is found and valid
                eventManager = new EventManager();
                commandManager = new CommandManager();

                System.out.println("Managers loaded");
                System.out.println("Loading plugins");
                // Plugins - only loaded if channel is valid!
                // Must be loaded after all other managers so plugins can use them
                pluginManager = new PluginManager(getPluginsFolder());

                System.out.println("Default channel loaded: " + CHANNEL.getName());
                System.out.println("Connecting to IRC");

                // Init the irc handler
                ircHandler = new IRCHandler();
                // Connect to the irc
                ircHandler.connect();
                // Send auth info
                ircHandler.sendAuth();

                // Join the channel on the irc
                CHANNEL.joinIrc();
                System.out.println("IRC connection complete");
            } else {
                // In the case the channel is not valid (Does not exist, can't connect to api, twitch returned error)
                // Application will terminate, nothing else can happen if there is no channel to connect to.w
                System.out.println("Default channel is invalid: " + CHANNEL.getInvalidReason());
                System.out.println("Please make sure that you have typed the channel name correctly!");
                System.exit(1);
            }
        });
    }

    /**
     * Setup a new log file and set the default system output to print to this file and the normal console
     *
     * @throws FileNotFoundException Log file can't be found
     */
    private void setupLogger() throws FileNotFoundException {
        // Get new log file
        File logFile = getNewLogFile();
        // File output stream for the log file
        FileOutputStream fileOutputStream = new FileOutputStream(logFile);
        PrintStream printStream = new PrintStream(fileOutputStream);
        AppbotLogger logger = new AppbotLogger(printStream, System.out);

        System.setOut(logger);
        System.setErr(logger);
    }

    /**
     * Only needs to be called on startup
     * Checks if plugin folder exists, if not then create it.
     */
    private void setupPluginsFolder() {
        if (!getPluginsFolder().exists()) {
            if (getPluginsFolder().mkdirs())
                System.out.println("Plugin folder made for the first time");
        }
    }

    /**
     * Only needs to be called on startup
     * Checks if a settings file exists, if not then create it
     * When the settings file is made for the first time it will be filled with the default
     * settings template and the application will sleep for 5 seconds then stop.
     * The user is warned that they should edit this file after is it made.
     *
     * @throws IOException Read/Write of file failed
     * @throws InterruptedException Read/Write was interrupted
     */
    private void setupSettingsFile() throws IOException, InterruptedException {
        // Setup settings file
        if (!getSettingsFile().exists()) {
            if(getSettingsFile().createNewFile()) {
                FileUtils.writeStringToFile(getSettingsFile(), defaultSettingsJson, "UTF-8");
                System.out.println("Settings file made for the first time");
                System.out.println("Please configure your settings file before starting this bot again!");
                Thread.sleep(5000);
            } else {
                System.out.println("Settings file failed to make!");
                System.exit(1);
            }
        }
    }

    /**
     * Loads the settings from the settings.json file
     * If any setting is not found the runnable will not be called.
     * After all settings have been loaded the default channel will be set.
     * In the case this is used for a settings file reload, the default channel should be killed first.
     *
     * @param runnable Will be passed to the default channel init.
     * @throws IOException Failed to read the file
     * @throws ParseException Failed to parse the file
     */
    private void loadSettings(Runnable runnable) throws IOException, ParseException {
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
        BOT_NAME = auth.get("username").toString();
        if (!auth.containsKey("oauth")) {
            // Auth does not contain oauth token
            System.err.println("Settings auth does not contain oauth!");
            return;
        }
        // Set oauth token from auth
        BOT_OAUTH = auth.get("oauth").toString();
        if (!auth.containsKey("clientid")) {
            // Client Id not in auth settings
            System.err.println("Settings auth does not contain clientid!");
            return;
        }
        // Set client id from auth
        BOT_CLIENTID = auth.get("clientid").toString();
        // Get connection part of settings
        JSONObject connection = (JSONObject) settings.get("connect");
        String channelName = connection.get("channel").toString().toLowerCase();
        CHANNEL = new Channel(channelName, runnable);
    }

    /**
     * @return The application task manager. Allows for easy multi-threading.
     */
    public static TaskManager getTaskManager() {
        return taskManager;
    }

    /**
     * @return The application event manager. Allows for simple event systems for plugins.
     */
    public static EventManager getEventManager() {
        return eventManager;
    }

    /**
     * @return Application irc handler. Handles any irc related stuff.
     */
    public static IRCHandler getIrcHandler() {
        return ircHandler;
    }

    /**
     * @return Command manager, manages any commands that plugins may register
     */
    public static CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * @return Plugin manager, manages all plugins
     */
    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * @return The default channel connection specified in the settings.json
     */
    public static Channel getDefaultChannel() {
        return CHANNEL;
    }

    /**
     * @return The client id used to talk with the twitch api
     */
    public static String getClientId() {
        return BOT_CLIENTID;
    }

    /**
     * @return The twitch username that the bot is using in the irc
     */
    public static String getName() {
        return BOT_NAME;
    }

    /**
     * @return The token that is used to authenticate with the irc server
     */
    public static String getOAuthToken() {
        return BOT_OAUTH;
    }

    /**
     * @return The directory that the application is being ran from
     */
    private File getActiveLocation() {
        try {
            File activeLocation = new File(Appbot.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            if(activeLocation.toString().endsWith(".jar")) {
                int lastSeparator = activeLocation.toString().lastIndexOf(activeLocation.separator);
                String execPath = activeLocation.toString().substring(0, lastSeparator);
                return new File(execPath);
            } else {
                return activeLocation;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return The file location all plugins should be located under
     */
    private File getPluginsFolder() {
        return new File(getActiveLocation(), "plugins");
    }

    /**
     * @return The file that contains all of the settings for the application
     */
    private File getSettingsFile() {
        return new File(getActiveLocation(), "settings.json");
    }

    /**
     * @return The parent log folder that contains all text logs from the application
     */
    private File getLogsFile() {
        return new File(getActiveLocation(), "logs");
    }

    /**
     * @return A new plain text file that will contain logs for the current application instance.
     */
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
                System.out.println("Failed to create new log file! Stopping!");
                e.printStackTrace();
                System.exit(1);
            }
        }
        return file;
    }

    // Program init
    public static void main(String[] args) {

        try {
            new Appbot();
        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
    }

}
