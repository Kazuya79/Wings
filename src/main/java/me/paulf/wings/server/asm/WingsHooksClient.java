package me.paulf.wings.server.asm;

import me.paulf.wings.util.Access;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.lang.invoke.MethodHandle;

public final class WingsHooksClient {
	private WingsHooksClient() {}

	private static int selectedItemSlot = 0;

	public static void onTurn(final Entity entity, final float deltaYaw) {
		if (entity instanceof EntityLivingBase) {
			final EntityLivingBase living = (EntityLivingBase) entity;
			final float theta = MathHelper.wrapDegrees(living.rotationYaw - living.renderYawOffset);
			final GetLivingHeadLimitEvent ev = GetLivingHeadLimitEvent.create(living);
			MinecraftForge.EVENT_BUS.post(ev);
			final float limit = ev.getHardLimit();
			if (theta < -limit || theta > limit) {
				living.renderYawOffset += deltaYaw;
				living.prevRenderYawOffset += deltaYaw;
			}
		}
	}

	public static boolean onCheckRenderEmptyHand(final boolean isMainHand, final ItemStack itemStackMainHand) {
		return isMainHand || !isMap(itemStackMainHand);
	}

	public static boolean onCheckDoReequipAnimation(final ItemStack from, final ItemStack to, final int slot) {
		final boolean fromEmpty = from.isEmpty();
		final boolean toEmpty = to.isEmpty();
		final boolean isOffHand = slot == -1;
		if (toEmpty && isOffHand) {
			final Minecraft mc = Minecraft.getMinecraft();
			final EntityPlayerSP player = mc.player;
			if (player == null) {
				return true;
			}
			final boolean fromMap = isMap(GetItemStackMainHand.invoke(mc.getItemRenderer()));
			final boolean toMap = isMap(player.getHeldItemMainhand());
			if (fromMap || toMap) {
				return fromMap != toMap;
			}
			if (fromEmpty) {
				final EmptyOffHandPresentEvent ev = new EmptyOffHandPresentEvent(player);
				MinecraftForge.EVENT_BUS.post(ev);
				return ev.getResult() != Event.Result.ALLOW;
			}
		}
		if (fromEmpty || toEmpty) {
			return fromEmpty != toEmpty;
		}
		final boolean hasSlotChange = !isOffHand && selectedItemSlot != (selectedItemSlot = slot);
		return from.getItem().shouldCauseReequipAnimation(from, to, hasSlotChange);
	}

	private static boolean isMap(final ItemStack stack) {
		return stack.getItem() instanceof ItemMap;
	}

	private static final class GetItemStackMainHand {
		private GetItemStackMainHand() {}

		private static final MethodHandle MH = Access.getter(ItemRenderer.class)
			.name("field_187467_d", "itemStackMainHand")
			.type(ItemStack.class);

		private static ItemStack invoke(final ItemRenderer instance) {
			try {
				return (ItemStack) MH.invokeExact(instance);
			} catch (final Throwable t) {
				throw Access.rethrow(t);
			}
		}
	}
}
