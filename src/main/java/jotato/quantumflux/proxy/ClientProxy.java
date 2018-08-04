package jotato.quantumflux.proxy;

import jotato.quantumflux.KeyBindings;
import jotato.quantumflux.QuantumFluxMod;
import jotato.quantumflux.registers.BlockRegister;
import jotato.quantumflux.registers.ItemRegister;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);

		OBJLoader.INSTANCE.addDomain(QuantumFluxMod.MODID);
	}
	
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

		KeyBindings.init();
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		ItemRegister.registerRenders();
		BlockRegister.registerRenders();
	}
}
