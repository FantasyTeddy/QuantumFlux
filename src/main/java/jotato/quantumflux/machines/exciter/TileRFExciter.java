package jotato.quantumflux.machines.exciter;

import java.util.UUID;

import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import jotato.quantumflux.ConfigMan;
import jotato.quantumflux.Logger;
import jotato.quantumflux.helpers.BlockHelpers;
import jotato.quantumflux.redflux.RedfluxField;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class TileRFExciter extends TileEntity implements IEnergyProvider, IEnergyStorage, ITickable
{
	public UUID owner;
	public int lastEnergyUsed;
	public EnumFacing targetDirection = null;
	public int maxOut;
	public int upgradeCount;
	public float wirelessEfficiency;

	public TileRFExciter()
	{
		maxOut=ConfigMan.rfExciter1_output;
		wirelessEfficiency =1.0f;// ConfigMan.rfExciter_Efficiency;
		
	}

	public String getOwner()
	{
		return owner == null ? null : owner.toString();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from)
	{
		return true;
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate)
	{
		return RedfluxField.requestEnergy(maxExtract, simulate, this.getOwner());
	}

	@Override
	public int getEnergyStored(EnumFacing from)
	{
		return 0; // todo: should this pull from the network?
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from)
	{
		return 0; // todo: should this pull from the network?
	}

	@Override
	public void onChunkUnload()
	{
		super.onChunkUnload();
	}

	@Override
	public void invalidate()
	{
		super.invalidate();
	}

	@Override
	public void validate()
	{
		super.validate();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		
		tag.setInteger("upgradeCount", upgradeCount);
		
		if(owner !=null)
		{
			tag.setString("owner", owner.toString());
		}
		
		return tag;
		
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		this.upgradeCount = tag.getInteger("upgradeCount");

		try
		{
			this.owner = UUID.fromString(tag.getString("owner"));
		}
		catch (IllegalArgumentException ex)
		{
			if (!world.isRemote)
				Logger.error("HEY YOU! An RF Exciter at %d, %d, %d has no owner, please replace it.", getPos().getX(), getPos().getY(), getPos().getZ());
		}
	}

	@Override
	public void update()
	{
		if (world.isRemote)
		{
			return;
		}
		
		if(owner==null ){
			return;
		}
		
		if(targetDirection == null){
			targetDirection = world.getBlockState(getPos()).getValue(BlockHelpers.FACING);
		}

		BlockPos targetBlock = getPos().add(targetDirection.getDirectionVec());

		TileEntity tile = world.getTileEntity(targetBlock);
		if (tile == null)
			return;
		if (tile instanceof IEnergyReceiver)
		{
		    int netPower = getNetPower();
		    
			int tosend = extractEnergy(null, netPower, true);
			int needed = ((IEnergyReceiver) tile).receiveEnergy(targetDirection.getOpposite(), tosend, true);
			int willSend = Math.round(needed * wirelessEfficiency);
			((IEnergyReceiver) tile).receiveEnergy(targetDirection.getOpposite(), willSend, false);
			
			if (needed > 0)
			{
				this.markDirty();
			}
			lastEnergyUsed = needed;
			extractEnergy(null, needed, false);
		}
		else if (tile.hasCapability(CapabilityEnergy.ENERGY, targetDirection.getOpposite())) {
			IEnergyStorage receiver = tile.getCapability(CapabilityEnergy.ENERGY, targetDirection.getOpposite());

			if (receiver.canReceive()) {
				int netPower = getNetPower();
				int tosend = extractEnergy(null, netPower, true);
				int needed = receiver.receiveEnergy(tosend, true);
				int willSend = Math.round(needed * wirelessEfficiency);
				receiver.receiveEnergy(willSend, false);

				if (needed > 0)
				{
					this.markDirty();
				}
				lastEnergyUsed = needed;
				extractEnergy(null, needed, false);
			}
		}
	}
	
	public int getNetPower(){
	    int power = maxOut + upgradeCount * 100;
	    return power;
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