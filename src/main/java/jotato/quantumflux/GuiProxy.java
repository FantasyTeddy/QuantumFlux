package jotato.quantumflux;

import jotato.quantumflux.machines.entropyaccelerator.ContainerEntropyAccelerator;
import jotato.quantumflux.machines.entropyaccelerator.GuiEntropyAccelerator;
import jotato.quantumflux.machines.entropyaccelerator.TileEntropyAccelerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiProxy implements IGuiHandler {

	@Nullable
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileEntropyAccelerator)
			return new ContainerEntropyAccelerator(player.inventory, (TileEntropyAccelerator) tileEntity);

		return null;
	}

	@Nullable
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileEntropyAccelerator)
			return new GuiEntropyAccelerator((TileEntropyAccelerator) tileEntity, new ContainerEntropyAccelerator(player.inventory, (TileEntropyAccelerator) tileEntity));

		return null;
	}
}
