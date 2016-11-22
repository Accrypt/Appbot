package io.mzb.AppBot.plugin;

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

    static HashMap<String, AppbotPlugin> activePlugins = new HashMap<String, AppbotPlugin>();
    File pluginDir;

    public PluginManager(File pluginDir) {
        this.pluginDir = pluginDir;

        for (File file : pluginDir.listFiles()) {
            if (file.getName().endsWith(".jar")) {
                try {
                    loadPlugin(file);
                } catch (IOException e) {
                    System.err.println("Failed to load plugin " + file.getName());
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    System.err.println("Failed to load plugin " + file.getName());
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    System.err.println("Failed to load plugin " + file.getName());
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    System.err.println("Failed to load plugin " + file.getName());
                    e.printStackTrace();
                } catch (ParseException e) {
                    System.err.println("Failed to load plugin " + file.getName());
                    e.printStackTrace();
                }
            }
        }
    }

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
}
