package jotato.quantumflux.proxy;

import jotato.quantumflux.ConfigMan;
import jotato.quantumflux.QuantumFluxMod;
import jotato.quantumflux.net.PacketHandler;
import jotato.quantumflux.registers.BlockRegister;
import jotato.quantumflux.registers.EventRegister;
import jotato.quantumflux.registers.ItemRegister;
import jotato.quantumflux.registers.WorldRegister;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		ConfigMan.init(new Configuration(event.getSuggestedConfigurationFile()));
		ItemRegister.init();
		BlockRegister.init();
		WorldRegister.init();
		PacketHandler.registerMessages(QuantumFluxMod.MODID);
	}

	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(QuantumFluxMod.instance, new GuiProxy());
	}

	public void postInit(FMLPostInitializationEvent event) {
		registerTickHandlers();
	}


	public void registerTickHandlers() {
		MinecraftForge.EVENT_BUS.register(new EventRegister());
	}
}
