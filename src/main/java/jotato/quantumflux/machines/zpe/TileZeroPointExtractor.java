package jotato.quantumflux.machines.zpe;

import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.impl.EnergyStorage;
import jotato.quantumflux.ConfigMan;
import jotato.quantumflux.blocks.TileBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class TileZeroPointExtractor extends TileBase implements IEnergyProvider, IEnergyStorage, ITickable {

	private EnergyStorage energy;

	public TileZeroPointExtractor() {
		energy = new EnergyStorage(ConfigMan.zpe_maxPowerGen, Integer.MAX_VALUE);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);

		NBTTagCompound energyTag = new NBTTagCompound();
		this.energy.writeToNBT(energyTag);
		tag.setTag("Energy", energyTag);
		
		return tag;

	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagCompound energyTag = tag.getCompoundTag("Energy");
		this.energy.readFromNBT(energyTag);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update() {

		if (world.isRemote) {
			return;
		}

		this.energy.receiveEnergy(Math.max(ConfigMan.zpe_maxPowerGen - getPos().getY(), 1), false);

		for (EnumFacing dir : EnumFacing.values()) {
			BlockPos targetBlock = getPos().add(dir.getDirectionVec());

			TileEntity tile = world.getTileEntity(targetBlock);
			if (tile == null)
				continue;
			if (tile instanceof IEnergyReceiver) {
				IEnergyReceiver receiver = (IEnergyReceiver) tile;

				if (receiver.canConnectEnergy(dir.getOpposite())) {
					int tosend = energy.extractEnergy(ConfigMan.zpe_maxPowerGen, true);
					int used = receiver.receiveEnergy(dir.getOpposite(), tosend, false);
					// TODO: need this? It doesn't really *need* state saved
					if (used > 0) {
						this.markDirty();
					}
					energy.extractEnergy(used, false);
				}
			}
			else if (tile.hasCapability(CapabilityEnergy.ENERGY, dir.getOpposite())) {
				IEnergyStorage receiver = tile.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite());

				if (receiver.canReceive()) {
					int tosend = energy.extractEnergy(ConfigMan.zpe_maxPowerGen, true);
					int used = receiver.receiveEnergy(tosend, false);
					// TODO: need this? It doesn't really *need* state saved
					if (used > 0) {
						this.markDirty();
					}
					energy.extractEnergy(used, false);
				}
			}
		}
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return energy.extractEnergy(maxExtract, simulate);
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