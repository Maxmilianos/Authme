package the.max.authme;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import the.max.authme.commands.Commands;
import the.max.authme.config.Config;
import the.max.authme.database.Database;
import the.max.authme.hash.Hash;
import the.max.authme.listeners.Listeners;

public class Authme extends JavaPlugin {

	private static Authme _authme;
	
	public Config _config, _players;
	
	public Boolean sql = false;
	
	public Database _db;
	
	public HashMap<Player, Long> _playerTime = new HashMap<Player, Long>();
	
	public HashMap<Player, Boolean> _playerLogged = new HashMap<Player, Boolean>();
	
	public HashMap<Player, String> _playerMessage = new HashMap<Player, String>();
	
	public String _prefix = "$8[$aAuthme$8] $7";
	
	@Override
	public void onEnable() {
		CommandSender s = Bukkit.getConsoleSender();
		send(s, "           $aAuthme $7" + getDescription().getVersion() + "           ");
		send(s, "");
		send(s, " - Creating istance...");
		_authme = this;
		send(s, " - Succesfully created istance");
		send(s, "");
		send(s, " - Registering listeners and commands...");
		Bukkit.getPluginManager().registerEvents(new Listeners(this), this);
		getCommand("register").setExecutor(new Commands(this));
		getCommand("login").setExecutor(new Commands(this));
		getCommand("changepassword").setExecutor(new Commands(this));
		send(s, " - Succesfully registered listeners and commands");
		send(s, "");
		send(s, " - Creating config...");
		_config = new Config(this, "config.yml");
		_config.addDefault("message.prefix", "&8[&aAuthme&8] &7");
		_config.addDefault("database.type", "yml");
		_config.addDefault("database.mysql.hostname", "localhost");
		_config.addDefault("database.mysql.user", "username");
		_config.addDefault("database.mysql.password", "password");
		_config.addDefault("database.mysql.database", "database");
		_config.addDefault("authme.settings.time-to-kick", 30);
		_config.addDefault("authme.messages.kick", "&cYou must logged in 30 seconds.");
		_config.addDefault("authme.messages.must-login-chat", "&7You must login to chatting.");
		_config.addDefault("authme.messages.must-login-command", "&7You must login to send command. (Only /l, /login, /reg, /register)");
		_config.addDefault("authme.messages.not-registered", "&7You must register to login!");
		_config.addDefault("authme.messages.command-login", "&7/login <password>");
		_config.addDefault("authme.messages.command-register", "&7/register <password> <password>");
		_config.addDefault("authme.messages.command-changepassword", "&7/changepassword <oldPassword> <newPassword>");
		_config.addDefault("authme.messages.bad-password", "&7Bad password. Try again.");
		_config.addDefault("authme.messages.changed-password", "&7Password succesfully changed!");
		_config.addDefault("authme.messages.already-registered", "&7This username is already registered.");
		_config.addDefault("authme.messages.already-logged", "&7You already logged!");
		_config.addDefault("authme.messages.logged", "&7You succesfully logged!");
		_config.addDefault("authme.messages.registered", "&7You succesfully registered!");
		_config.addDefault("authme.messages.correct-password", "&7Incorrect passwords!");
		_config.addDefault("authme.messages.login.title", "&a&lPlease login!");
		_config.addDefault("authme.messages.login.subtitle", "&a&l/login <password>");
		_config.addDefault("authme.messages.register.title", "&a&lPlease register!");
		_config.addDefault("authme.messages.register.subtitle", "&a&l/register <password> <password>");
		_config.options().copyDefaults(true);
		_config.save();
		send(s, " - Succesfully created config");
		send(s, "");
		send(s, " - Getting prefix from config...");
		_prefix = ChatColor.translateAlternateColorCodes('&', _config.getString("message.prefix"));
		send(s, " - Succesfully get prefix from config");
		send(s, "");
		send(s, " - Getting type of database from config...");
		if (_config.getString("database.type").equalsIgnoreCase("sql")) {
			sql = true;
			_db = new Database();
			_db.open();
			_db.query("CREATE TABLE IF NOT EXISTS `authme_passwords` (`username` varchar(50) NOT NULL, `password` varchar(100) not null, UNIQUE KEY `username` (`username`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1");
			send(s, " - Succesfully get type SQL");
		} else {
			_players = new Config(this, "players.yml");
			send(s, " - Succesfully get type Yaml");
		}
		send(s, "");
		send(s, " - Creating schedulers of logging...");
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for (Player p : _playerTime.keySet()) {
					if (!_playerLogged.get(p)) {
						if (isRegistered(p.getName())) {
							sendTitle(p, 0, 40, 0, ChatColor.translateAlternateColorCodes('&', _config.getString("authme.messages.login.title")), ChatColor.translateAlternateColorCodes('&', _config.getString("authme.messages.login.subtitle")));
						} else {
							sendTitle(p, 0, 40, 0, ChatColor.translateAlternateColorCodes('&', _config.getString("authme.messages.register.title")), ChatColor.translateAlternateColorCodes('&', _config.getString("authme.messages.register.subtitle")));
						}
						int time = (int) ((System.currentTimeMillis() - _playerTime.get(p)) / 1000D); 
						if (time >= _config.getInt("authme.settings.time-to-kick")) {
							p.kickPlayer(ChatColor.translateAlternateColorCodes('&', _config.getString("authme.messages.kick")));
							_playerLogged.remove(p);
							_playerTime.remove(p);
						}
					}
				}
			}
		}, 0L, 20L);
		send(s, " - Succesfully created schedulers");
		send(s, "");
		send(s, " $a- Succesfully enabled Authme");
		send(s, "");
	}
	
	public static Authme getAuthme() {
		if (_authme == null) {
			_authme = new Authme();
		}
		return _authme;
	}
	
	public void send(CommandSender s, String msg) {
		s.sendMessage(_prefix + msg);
	}
	
	public Boolean isRegistered(String name) {
		if (sql) {
			if (!_db.get(name).equalsIgnoreCase("")) {
				return true;
			} else {
				return false;
			}
		} else {
			if (_players.getString(name + ".password") != null) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public String getPassword(String name) {
		String pass = "";
		if (isRegistered(name)) {
			if (sql) {
				pass = _db.get(name);
			} else {
				pass = _players.getString(name + ".password");
			}
		} else {
			pass = "Not registered";
		}
		return pass;
	}
	
	public void register(String name, String password) {
		password = Hash.getMD5(password);
		if (sql) {
			_db.create(name, password);
		} else {
			_players.set(name+ ".password", password);
			_players.save();
		}
	}

	public void change(String name, String password) {
		password = Hash.getMD5(password);
		if (sql) {
			_db.set(name, password);
		} else {
			_players.set(name+ ".password", password);
			_players.save();
		}
	}
	

    public void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut);
        connection.sendPacket(packetPlayOutTimes);

        if (subtitle != null) {
            subtitle = subtitle.replaceAll("%player%", player.getName());
            subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
            IChatBaseComponent titleSub = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
            PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, titleSub);
            connection.sendPacket(packetPlayOutSubTitle);
        }

        if (title != null) {
            title = title.replaceAll("%player%", player.getName());
            title = ChatColor.translateAlternateColorCodes('&', title);
            IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
            PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleMain);
            connection.sendPacket(packetPlayOutTitle);
        }
    }
	
}
