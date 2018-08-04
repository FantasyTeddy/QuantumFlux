package jotato.quantumflux;

import jotato.quantumflux.proxy.CommonProxy;
import jotato.quantumflux.registers.ItemRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = QuantumFluxMod.MODID, version = QuantumFluxMod.VERSION, name = QuantumFluxMod.MODNAME, dependencies = QuantumFluxMod.DEPENDENCIES)
public class QuantumFluxMod {
	public static final String MODID = "quantumflux";
	public static final String MODNAME = "QuantumFlux";
	public static final String VERSION = "2.0.15";
	public static final String DEPENDENCIES = "required-after:redstoneflux";
	public static final String TEXTURE_BASE = MODID + ":";

	@Mod.Instance
	public static QuantumFluxMod instance;

	@SidedProxy(clientSide = "jotato.quantumflux.proxy.ClientProxy", serverSide = "jotato.quantumflux.proxy.ServerProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	public static CreativeTabs tab = new CreativeTabs("tabQuantumFlux") {
		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getTabIconItem() {
			return ItemRegister.craftingPieces.getSubItem("quibitCrystal");
		}
	};
}
