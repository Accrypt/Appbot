package io.mzb.Appbot.plugin;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

public class PluginManager {

    // List of all plugins running, stored by their name
    private static HashMap<String, AppbotPlugin> activePlugins = new HashMap<>();
    // The location of the plugin folder
    private File pluginDir;

    /**
     * Plugin manager init
     * @param pluginDir The location to load the plugins from
     */
    public PluginManager(File pluginDir) {
        this.pluginDir = pluginDir;
        if(this.pluginDir == null) {
            System.out.println("[Error] Plugin directory is null!");
            System.exit(1);
        }
        File[] files = this.pluginDir.listFiles();
        if(files == null) {
            System.out.println("[Error] Plugin directory list is null!");
            System.exit(1);
        }
        for (File file : files) {
            // Only load jar files
            if (file.getName().endsWith(".jar")) {
                try {
                    loadPlugin(file);
                } catch (IOException | ClassNotFoundException | IllegalAccessException | ParseException | InstantiationException e) {
                    System.err.println("Failed to load plugin " + file.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Loads a plugin from its file
     * @param file The file of the plugin
     * @throws IOException Failed to read the file
     * @throws ClassNotFoundException The main class is not found
     * @throws IllegalAccessException Can't gain access to the fail (Check user permissions)
     * @throws InstantiationException x
     * @throws ParseException x
     */
    private void loadPlugin(File file) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, ParseException {
        String name = file.getName();

        ClassLoader loader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});
        InputStream is = loader.getResourceAsStream("plugin.json");
        String pluginString = IOUtils.toString(is, "UTF-8");
        JSONParser jp = new JSONParser();
        JSONObject pluginJson = (JSONObject) jp.parse(pluginString);
        String mainDir = pluginJson.get("main").toString(); // TODO
        AppbotPlugin plugin = (AppbotPlugin) loader.loadClass(mainDir).newInstance();
        plugin.onLoad();
        activePlugins.put(pluginJson.get("name").toString(), plugin);
        System.out.println("Loaded plugin " + pluginJson.get("name").toString());
    }

    public String getPluginName(AppbotPlugin plugin) {
        for(String key : activePlugins.keySet()) {
            if(activePlugins.get(key).equals(plugin)) {
                return key;
            }
        }
        return null;
    }

}
