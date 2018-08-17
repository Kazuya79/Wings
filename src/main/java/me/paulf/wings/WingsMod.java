package me.paulf.wings;

import me.paulf.wings.server.dreamcatcher.InSomniableCapability;
import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.FlightCapability;
import me.paulf.wings.server.fix.WingsFixes;
import me.paulf.wings.util.ItemAccessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
	modid = WingsMod.ID,
	name = WingsMod.NAME,
	version = WingsMod.VERSION,
	dependencies = "required-after:llibrary@[1.7,1.8)",
	acceptedMinecraftVersions = "[1.12]"
)
public final class WingsMod {
	public static final String ID = "wings";

	public static final String NAME = "Wings";

	public static final String VERSION = "1.0.1";

	private static final class Holder {
		private static final WingsMod INSTANCE = new WingsMod();
	}

	@SidedProxy(
		clientSide = "me.paulf.wings.client.ClientProxy",
		serverSide = "me.paulf.wings.server.ServerProxy"
	)
	private static Proxy proxy;

	@Mod.EventHandler
	public void init(FMLPreInitializationEvent event) {
		FlightCapability.register();
		InSomniableCapability.register();
		WingsFixes.register();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	public Flight newFlight(EntityPlayer player) {
		return proxy.newFlight(player);
	}

	public ItemAccessor<EntityPlayer> getWingsAccessor() {
		return proxy.getWingsAccessor();
	}

	@Mod.InstanceFactory
	public static WingsMod instance() {
		return Holder.INSTANCE;
	}
}
