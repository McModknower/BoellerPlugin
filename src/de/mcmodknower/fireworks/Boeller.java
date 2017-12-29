package de.mcmodknower.fireworks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow.PickupStatus;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Boeller extends JavaPlugin implements Listener {

	private static Boeller pl;
	
	@Override
	public void onLoad() {
		pl = this;
		super.onLoad();
	}

	@Override
	public void onEnable() {
		super.onEnable();
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler(ignoreCancelled = false)
	public void onClick(PlayerInteractEvent evt) {
		if (!evt.getAction().equals(Action.RIGHT_CLICK_AIR))
			return;
		if (evt.getItem().getType().equals(Material.FIREWORK)) {
			Player p = evt.getPlayer();
			if(!evt.isCancelled()&&!p.isSneaking())
				return;
			evt.setCancelled(true);
			SpectralArrow proj = p.launchProjectile(SpectralArrow.class, p.getEyeLocation().getDirection());
			proj.setGlowingTicks(0);
			proj.setBounce(false);
			proj.setCritical(false);
			proj.setPickupStatus(PickupStatus.DISALLOWED);
			new Explosion(proj, (FireworkMeta) evt.getItem().getItemMeta()).runTaskLater(this, 3 * 20);
			return;
		}
	}

	private class Explosion extends BukkitRunnable {

		private Entity e;
		private FireworkMeta meta;

		public Explosion(Entity e, FireworkMeta meta) {
			this.e = e;
			this.meta = meta;
		}

		@Override
		public void run() {
			Location loc = e.getLocation();
			Firework fw = loc.getWorld().spawn(loc, Firework.class);
			meta.setPower(0);
			fw.setFireworkMeta(meta);
			e.remove();
			fw.setVelocity(new Vector());
//			((CraftFirework) fw).getHandle().expectedLifespan = 1;
			new BukkitRunnable() {
				
				@Override
				public void run() {
					fw.detonate();
				}
			}.runTaskLater(Boeller.pl, 1);
		}

	}

}
