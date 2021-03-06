package me.paulf.wings.server.dreamcatcher;

import me.paulf.wings.WingsMod;
import me.paulf.wings.util.CapabilityHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = WingsMod.ID)
public final class InSomniableCapability {
	private InSomniableCapability() {}

	private static final CapabilityHolder<EntityPlayer, InSomniable, CapabilityHolder.State<EntityPlayer, InSomniable>> INSOMNIABLE = CapabilityHolder.create();

	private static final CapabilityHolder<TileEntityNote, Playable, CapabilityHolder.State<TileEntityNote, Playable>> PLAYABLE = CapabilityHolder.create();

	@Nullable
	public static InSomniable getInSomniable(final EntityPlayer player) {
		return INSOMNIABLE.state().get(player, null);
	}

	@Nullable
	public static Playable getPlayable(final TileEntityNote noteblock) {
		return PLAYABLE.state().get(noteblock, null);
	}

	@CapabilityInject(InSomniable.class)
	static void injectInSomniable(final Capability<InSomniable> capability) {
		INSOMNIABLE.inject(capability);
	}

	@CapabilityInject(Playable.class)
	static void injectPlayable(final Capability<Playable> capability) {
		PLAYABLE.inject(capability);
	}

	@SubscribeEvent
	public static void onAttachEntityCapabilities(final AttachCapabilitiesEvent<Entity> event) {
		final Entity entity = event.getObject();
		if (entity instanceof EntityPlayer) {
			event.addCapability(
				new ResourceLocation(WingsMod.ID, "insomniable"),
				INSOMNIABLE.state().providerBuilder(new InSomniable())
					.serializedBy(new InSomniable.Serializer())
					.build()
			);
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(final PlayerEvent.Clone event) {
		final InSomniable oldInstance = getInSomniable(event.getOriginal());
		final InSomniable newInstance;
		if (oldInstance != null && (newInstance = getInSomniable(event.getEntityPlayer())) != null) {
			newInstance.clone(oldInstance);
		}
	}

	@SubscribeEvent
	public static void onAttachBlockEntityCapabilities(final AttachCapabilitiesEvent<TileEntity> event) {
		final TileEntity entity = event.getObject();
		if (entity instanceof TileEntityNote) {
			event.addCapability(
				new ResourceLocation(WingsMod.ID, "playable"),
				PLAYABLE.state().providerBuilder(new Playable())
					.serializedBy(new Playable.Serializer())
					.build()
			);
		}
	}
}
