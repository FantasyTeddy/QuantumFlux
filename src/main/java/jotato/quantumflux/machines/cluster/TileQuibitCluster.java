package jotato.quantumflux.machines.cluster;

import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.impl.EnergyStorage;
import jotato.quantumflux.blocks.TileBase;
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

public class TileQuibitCluster extends TileBase implements IEnergyProvider, IEnergyReceiver, IEnergyStorage, ITickable {
	protected EnergyStorage localEnergyStorage;
	private int transferRate;
	private int capacity;
	public int level;
	public int lastUsed;

	public TileQuibitCluster(QuibitClusterSettings settings) {

		this.transferRate = settings.transferRate;
		this.capacity = settings.capacity;
		this.level = settings.level;

		localEnergyStorage = new EnergyStorage(this.capacity, this.transferRate);
	}

	public TileQuibitCluster() {
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);

		NBTTagCompound energyTag = new NBTTagCompound();
		this.localEnergyStorage.writeToNBT(energyTag);
		tag.setTag("Energy", energyTag);
		tag.setInteger("XferRate", this.transferRate);
		tag.setInteger("Capacity", this.capacity);
		tag.setInteger("Level", this.level);
		
		return tag;

	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		this.level = tag.getInteger("Level");
		this.capacity = tag.getInteger("Capacity");
		this.transferRate = tag.getInteger("XferRate");

		if (this.localEnergyStorage == null) {
			this.localEnergyStorage = new EnergyStorage(capacity, transferRate);
		}

		NBTTagCompound energyTag = tag.getCompoundTag("Energy");
		this.localEnergyStorage.readFromNBT(energyTag);
	}

	@SideOnly(Side.CLIENT)
	public int getBufferScaled(int scale) {
		double stored = getEnergyStored(null);
		double max = getMaxEnergyStored(null);
		double v = ((stored / max) * scale);
		return (int) v;
	}

	@Override
	public void update() {
		if (world.isRemote)
			return;

		for (EnumFacing dir : EnumFacing.values()) {
			BlockPos targetBlock = getPos().add(dir.getDirectionVec());

			TileEntity tile = world.getTileEntity(targetBlock);
			if (tile == null)
				continue;
			if (tile instanceof TileQuibitCluster)
				return;
			if (tile instanceof IEnergyReceiver) {
				IEnergyReceiver receiver = (IEnergyReceiver) tile;

				if (receiver.canConnectEnergy(dir.getOpposite())) {
					int tosend = localEnergyStorage.extractEnergy(transferRate, true);
					int used = receiver.receiveEnergy(dir.getOpposite(), tosend, false);
					if (used > 0) {
						this.markDirty();
					}
					this.lastUsed = used;
					localEnergyStorage.extractEnergy(used, false);
				}
			}
			else if (tile.hasCapability(CapabilityEnergy.ENERGY, dir.getOpposite())) {
				IEnergyStorage receiver = tile.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite());

				if (receiver.canReceive()) {
					int tosend = localEnergyStorage.extractEnergy(transferRate, true);
					int used = receiver.receiveEnergy(tosend, false);
					if (used > 0) {
						this.markDirty();
					}
					this.lastUsed = used;
					localEnergyStorage.extractEnergy(used, false);
				}
			}

		}
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		int used = localEnergyStorage.receiveEnergy(maxReceive, simulate);
		if (used > 0 && !simulate) {
			this.markDirty();
		}
		return used;
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		int given = localEnergyStorage.extractEnergy(maxExtract, simulate);

		if (given > 0 && !simulate) {
			this.markDirty();
		}
		return given;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return localEnergyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return localEnergyStorage.getMaxEnergyStored();
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
		return canConnectEnergy(null);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return receiveEnergy(null, maxReceive, simulate);
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