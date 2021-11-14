package the.max.authme.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import the.max.authme.Authme;
import the.max.authme.hash.Hash;

public class Commands implements CommandExecutor {
	
	private static Authme _authme;

	public Commands(Authme authme) {
		_authme = authme;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("login")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (_authme.isRegistered(p.getName())) {
					if (!_authme._playerLogged.get(p)) {
						if (args.length == 1) {
							String pass = args[0];
							if (_authme.getPassword(p.getName()).equalsIgnoreCase(Hash.getMD5(pass))) {
								_authme.send(p, ChatColor.translateAlternateColorCodes('&', _authme._config.getString("authme.messages.logged")));
								_authme._playerLogged.put(p, true);
								_authme._playerTime.remove(p);
								Bukkit.broadcastMessage(_authme._playerMessage.get(p));
							} else {
								_authme.send(p, ChatColor.translateAlternateColorCodes('&', _authme._config.getString("authme.messages.bad-password")));
							}
						} else {
							_authme.send(p, ChatColor.translateAlternateColorCodes('&', _authme._config.getString("authme.messages.command-login")));
						}
					} else {
						_authme.send(p, ChatColor.translateAlternateColorCodes('&', _authme._config.getString("authme.messages.already-logged")));
					}
				} else {
					_authme.send(p, ChatColor.translateAlternateColorCodes('&', _authme._config.getString("authme.messages.not-registered")));
				}
			} else {
				_authme.send(sender, "This command is only for player!");
			}
		} else if (cmd.getName().equalsIgnoreCase("register")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (!_authme.isRegistered(p.getName())) {
					if (args.length == 2) {
						String pass = args[0];
						String pass1 = args[1];
						if (pass.equalsIgnoreCase(pass1)) {
							_authme.send(p, ChatColor.translateAlternateColorCodes('&', _authme._config.getString("authme.messages.registered")));
							_authme.register(p.getName(), pass);
							_authme._playerLogged.put(p, true);
							_authme._playerTime.remove(p);
							Bukkit.broadcastMessage(_authme._playerMessage.get(p));
						} else {
							_authme.send(p, ChatColor.translateAlternateColorCodes('&', _authme._config.getString("authme.messages.correct-password")));
						}
					} else {
						_authme.send(p, ChatColor.translateAlternateColorCodes('&', _authme._config.getString("authme.messages.command-register")));
					}
				} else {
					_authme.send(p, ChatColor.translateAlternateColorCodes('&', _authme._config.getString("authme.messages.already-registered")));
				}
			} else {
				_authme.send(sender, "This command is only for player!");
			}
		} else if (cmd.getName().equalsIgnoreCase("changepassword")) {
			if (sender instanceof Player) {
				Player p = (Player) sender;
				if (_authme.isRegistered(p.getName())) {
					if (_authme._playerLogged.get(p)) {
						if (args.length == 2) {
							String pass = args[0];
							String pass1 = args[1];
							if (_authme.getPassword(p.getName()).equalsIgnoreCase(Hash.getMD5(pass))) {
								_authme.send(p, ChatColor.translateAlternateColorCodes('&', _authme._config.getString("authme.messages.changed-password")));
								_authme.change(p.getName(), pass1);
							} else {
								_authme.send(p, ChatColor.translateAlternateColorCodes('&', _authme._config.getString("authme.messages.correct-password")));
							}
						} else {
							_authme.send(p, ChatColor.translateAlternateColorCodes('&', _authme._config.getString("authme.messages.command-changepassword")));
						}
					} else {
						_authme.send(p, ChatColor.translateAlternateColorCodes('&', _authme._config.getString("authme.messages.must-login-command")));
					}
				} else {
					_authme.send(p, ChatColor.translateAlternateColorCodes('&', _authme._config.getString("authme.messages.not-registered")));
				}
			} else {
				_authme.send(sender, "This command is only for player!");
			}
		}
		return false;
	}
}
