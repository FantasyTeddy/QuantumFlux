package jotato.quantumflux.machines.entropyaccelerator;

import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.impl.EnergyStorage;
import jotato.quantumflux.ConfigMan;
import jotato.quantumflux.blocks.TileSimpleInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class TileEntropyAccelerator extends TileSimpleInventory implements IEnergyProvider, IEnergyStorage, ITickable {

	public TileEntropyAccelerator() {
		super(1);
		maxBurnTime = ConfigMan.incinerator_burnTime;
		energy = new EnergyStorage(ConfigMan.incinerator_buffer, Integer.MAX_VALUE, ConfigMan.incinerator_output);
	}

	private int currentBurnTime = 0;
	private EnergyStorage energy;
	public int maxBurnTime;
	public boolean isBurning = false;

	public boolean isActive() {
		return (hasFuel() || isBurning) && this.energy.getEnergyStored() < this.energy.getMaxEnergyStored();

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setShort("currentBurnTime", (short) this.currentBurnTime);

		NBTTagCompound energyTag = new NBTTagCompound();
		this.energy.writeToNBT(energyTag);
		tag.setTag("Energy", energyTag);
		tag.setBoolean("Burning", isBurning);
		
		return tag;

	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound energyTag = tag.getCompoundTag("Energy");

		this.currentBurnTime = tag.getShort("currentBurnTime");
		this.energy.readFromNBT(energyTag);
		this.isBurning = tag.getBoolean("Burning");
	}

	@Override
	public void update() {

		if (!world.isRemote) {
			if (isActive()) {

				if (this.currentBurnTime == 0) {
					isBurning = true;
					this.itemHandler.extractItem(0, 1, false);
				}

				this.energy.receiveEnergy(ConfigMan.incinerator_output, false);

				this.currentBurnTime++;
				if (this.currentBurnTime >= this.maxBurnTime) {
					this.currentBurnTime = 0;
					isBurning = false;
				}
				this.markDirty();
			}

			for (EnumFacing dir : EnumFacing.values()) {
				BlockPos targetBlock = getPos().add(dir.getDirectionVec());

				TileEntity tile = world.getTileEntity(targetBlock);
				if (tile == null)
					continue;
				if (tile instanceof IEnergyReceiver) {
					IEnergyReceiver receiver = (IEnergyReceiver) tile;

					if (receiver.canConnectEnergy(dir.getOpposite())) {
						int tosend = energy.extractEnergy(ConfigMan.incinerator_output, true);
						int used = receiver.receiveEnergy(dir.getOpposite(), tosend, false);
						if (used > 0) {
							this.markDirty();
						}
						energy.extractEnergy(used, false);
					}
				}
				else if (tile.hasCapability(CapabilityEnergy.ENERGY, dir.getOpposite())) {
					IEnergyStorage receiver = tile.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite());

					if (receiver.canReceive()) {
						int tosend = energy.extractEnergy(ConfigMan.incinerator_output, true);
						int used = receiver.receiveEnergy(tosend, false);
						if (used > 0) {
							this.markDirty();
						}
						energy.extractEnergy(used, false);
					}
				}

			}
		}
	}

	public void setProgress(int value) {
		currentBurnTime = value;
	}

	public int getProgress() {
		return currentBurnTime;
	}

	public boolean canInteractWith(EntityPlayer player) {
		return !isInvalid() && player.getDistanceSq(this.pos.add(0.5D, 0.5D, 0.5D)) <= 64.0D;
	}

	// energy stuff

	public void setEnergyStored(int value) {
		this.markDirty();
		this.energy.setEnergyStored(value);
	}

	private boolean hasFuel() {
		return !this.itemHandler.getStackInSlot(0).isEmpty();
	}

	@SideOnly(Side.CLIENT)
	public int getBufferScaled(int scale) {
		return getEnergyStored(null) * scale / getMaxEnergyStored(null);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {

		int toget = energy.extractEnergy(maxExtract, simulate);
		if (toget > 0 && !simulate) {
			this.markDirty();
		}
		return toget;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return energy.getMaxEnergyStored();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY)
			return true;

		return super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		if (capability == CapabilityEnergy.ENERGY)
			return (T) this;

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtract()	{
		return canConnectEnergy(null);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return extractEnergy(null, maxExtract, simulate);
	}

	@Override
	public boolean canReceive()	{
		return false;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored() {
		return getEnergyStored(null);
	}

	@Override
	public int getMaxEnergyStored() {
		return getMaxEnergyStored(null);
	}
}