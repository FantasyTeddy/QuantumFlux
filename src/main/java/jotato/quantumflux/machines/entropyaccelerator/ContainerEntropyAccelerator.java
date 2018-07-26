package jotato.quantumflux.machines.entropyaccelerator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerEntropyAccelerator extends Container {

	private TileEntropyAccelerator tileEntity;

	public ContainerEntropyAccelerator(IInventory playerInventory, TileEntropyAccelerator tileEntity) {
		this.tileEntity = tileEntity;

		this.addSlots();
		this.addPlayerSlots(playerInventory);
	}

	private void addPlayerSlots(IInventory playerInventory) {
		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 9; ++col) {
				int x = 8 + col * 18;
				int y = 84 + row * 18;
				this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, x, y));
			}
		}

		for (int col = 0; col < 9; col++) {
			int x = 8 + col * 18;
			int y = 142;
			this.addSlotToContainer(new Slot(playerInventory, col, x, y));
		}
	}

	private void addSlots() {
		IItemHandler itemHandler = this.tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		this.addSlotToContainer(new SlotItemHandler(itemHandler, 0, 80, 16));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < 1) {
				if (!this.mergeItemStack(itemstack1, 1, this.inventorySlots.size(), true))
					return ItemStack.EMPTY;
			}
			else if (!this.mergeItemStack(itemstack1, 0, 1, false))
				return ItemStack.EMPTY;

			if (itemstack1.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.tileEntity.canInteractWith(playerIn);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < this.listeners.size(); ++i) {
			IContainerListener containerListener = this.listeners.get(i);

			containerListener.sendWindowProperty(this, 0, this.tileEntity.getProgress());
			containerListener.sendWindowProperty(this, 1, this.tileEntity.getEnergyStored());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		if (id == 0)
			this.tileEntity.setProgress(data);
		else
			this.tileEntity.setEnergyStored(data);
	}
}
