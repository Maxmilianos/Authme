package the.max.authme.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config extends YamlConfiguration {
	
	private File file;
	private String defaults;
	private JavaPlugin plugin;
	  
	public Config(JavaPlugin plugin, String fileName) {
		this(plugin, fileName, null);
	}
	  
	public Config(JavaPlugin plugin, String fileName, String defaultsName) {
		this.plugin = plugin;
		this.defaults = defaultsName;
		this.file = new File(plugin.getDataFolder(), fileName);
		reload();
	 }
	  
	public void reload() {
	    if (!this.file.exists()) {
	    	try {
	    		this.file.getParentFile().mkdirs();
	    		this.file.createNewFile();
	    	} catch (IOException exception) {
	    		exception.printStackTrace();
	    		this.plugin.getLogger().severe("Error while creating file " + this.file.getName());
	    	}
	    }
	    try {
	    	load(this.file);
	    	if (this.defaults != null) {
	    		InputStreamReader reader = new InputStreamReader(this.plugin.getResource(this.defaults));
	    		FileConfiguration defaultsConfig = YamlConfiguration.loadConfiguration(reader);
	    
	    		setDefaults(defaultsConfig);
				options().copyDefaults(true);
			
				reader.close();
				save();
	    	}
		} catch (IOException exception) {
		  exception.printStackTrace();
		  this.plugin.getLogger().severe("Error while loading file " + this.file.getName());
		} catch (InvalidConfigurationException exception) {
		  exception.printStackTrace();
		  this.plugin.getLogger().severe("Error while loading file " + this.file.getName());
		}
	}
  
	public void save() {
		try {
			options().indent(2);
			save(this.file);
		} catch (IOException exception) {
			exception.printStackTrace();
			this.plugin.getLogger().severe("Error while saving file " + this.file.getName());
		}
	}	
	
	/*public String getString(String path) {
		return ChatColor.translateAlternateColorCodes('&', file.get.getString(path));
	}*/
}
