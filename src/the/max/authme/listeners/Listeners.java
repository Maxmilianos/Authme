package the.max.authme.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import the.max.authme.Authme;

public class Listeners implements Listener {
	
	private static Authme _authme;

	public Listeners(Authme authme) {
		_authme = authme;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		_authme._playerLogged.put(p, false);
		_authme._playerTime.put(p, System.currentTimeMillis());
		_authme._playerMessage.put(p, e.getJoinMessage());
		_authme.send(p, ChatColor.translateAlternateColorCodes('&', _authme._config.getString("authme.messages.command-login")));
		e.setJoinMessage(null);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (!_authme._playerLogged.get(p)) {
			e.setQuitMessage(null);
		}
		_authme._playerLogged.remove(p);
		_authme._playerTime.remove(p);
	}
	
	@EventHandler
	public void invClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getCurrentItem() != null && e.getClickedInventory() != null && !_authme._playerLogged.get(p)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void playerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!_authme._playerLogged.get(p)) {
			e.setCancelled(true);
			p.teleport(e.getFrom());
		}
	}
	
	@EventHandler
	public void playerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (!_authme._playerLogged.get(p)) {
			e.setCancelled(true);
			_authme.send(p,ChatColor.translateAlternateColorCodes('&',  _authme._config.getString("authme.messages.must-login-chat")));
		}
	}
	
	@EventHandler
	public void playerChat(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		String msg = e.getMessage().replace("/", "");
		if (!_authme._playerLogged.get(p) && !msg.startsWith("login") && !msg.startsWith("l") && !msg.startsWith("reg") && !msg.startsWith("register")) {
			e.setCancelled(true);
			_authme.send(p, ChatColor.translateAlternateColorCodes('&', _authme._config.getString("authme.messages.must-login-command")));
		}
	}
	
	@EventHandler
	public void itemDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if (!_authme._playerLogged.get(p)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void itemPick(PlayerPickupItemEvent e) {
		Player p = e.getPlayer();
		if (!_authme._playerLogged.get(p)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void blockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (!_authme._playerLogged.get(p)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void blockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (!_authme._playerLogged.get(p)) {
			e.setCancelled(true);
		}
	}
	
	
	
}
