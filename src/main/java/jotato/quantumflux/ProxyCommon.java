package jotato.quantumflux;

import jotato.quantumflux.net.PacketHandler;
import jotato.quantumflux.registers.BlockRegister;
import jotato.quantumflux.registers.EventRegister;
import jotato.quantumflux.registers.ItemRegister;
import jotato.quantumflux.registers.RecipeRegister;
import jotato.quantumflux.registers.WorldRegister;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class ProxyCommon {

	public void preInit() {
		ItemRegister.init();
		BlockRegister.init();
		WorldRegister.init();
		PacketHandler.registerMessages(QuantumFluxMod.MODID);
	}

	public void init() {
		NetworkRegistry.INSTANCE.registerGuiHandler(QuantumFluxMod.instance, new GuiProxy());
	}

	public void postInit() {
		registerTickHandlers();
		new RecipeRegister().init();
	}



	public void registerTickHandlers() {
		MinecraftForge.EVENT_BUS.register(new EventRegister());
	}

}
