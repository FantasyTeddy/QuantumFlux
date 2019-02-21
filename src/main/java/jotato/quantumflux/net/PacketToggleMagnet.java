package jotato.quantumflux.net;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import io.netty.buffer.ByteBuf;
import jotato.quantumflux.items.ItemMagnet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketToggleMagnet implements IMessage {

	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

	public PacketToggleMagnet() {

	}

	public static class Handler implements IMessageHandler<PacketToggleMagnet, IMessage> {
		@Override
		public IMessage onMessage(PacketToggleMagnet message, MessageContext ctx) {

			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
			return null;
		}

		private void handle(PacketToggleMagnet message, MessageContext ctx) {
			EntityPlayer p = ctx.getServerHandler().player;
			NonNullList<ItemStack> inv = p.inventory.mainInventory;
			// Baubles toggle
			try {
				ItemMagnet.toggleMagnetWithMessage(findMagnetBaubles(p), p);
			}catch (NoSuchMethodError e) {}
			
			// Normal inventory toggle
			for (int i = 0; i < inv.size(); i++) {
				if (inv.get(i).getItem() instanceof ItemMagnet) {
					ItemMagnet.toggleMagnetWithMessage(inv.get(i), p);
				}
			}

		}
	}
	
	@Optional.Method(modid = "baubles")
	public static ItemStack findMagnetBaubles(EntityPlayer player) {
		IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
		for (int i = 0; i < baubles.getSlots(); i++) {
			ItemStack b = baubles.getStackInSlot(i);
			if (b.getItem() instanceof ItemMagnet) {
				return b;
			}
		}
		return ItemStack.EMPTY;
	}
}