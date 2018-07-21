package jotato.quantumflux.blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public abstract class TileSimpleInventory extends TileBase {

	protected ItemStackHandler itemHandler;

	public TileSimpleInventory(int inventorySize) {
		this.itemHandler = new ItemStackHandler(inventorySize) {
			@Override
			protected void onContentsChanged(int slot) {
				TileSimpleInventory.this.markDirty();
			}
		};
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTag) {
		nbtTag = super.writeToNBT(nbtTag);
		nbtTag.setTag("Items", itemHandler.serializeNBT());
		return nbtTag;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTag) {
		super.readFromNBT(nbtTag);
		if (nbtTag.hasKey("Items"))
			itemHandler.deserializeNBT(nbtTag.getCompoundTag("Items"));
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return true;
		return super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandler);
		return super.getCapability(capability, facing);
	}
}
