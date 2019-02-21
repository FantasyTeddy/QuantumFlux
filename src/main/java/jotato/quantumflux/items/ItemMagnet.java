package jotato.quantumflux.items;

import java.util.Iterator;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import jotato.quantumflux.ConfigMan;
import jotato.quantumflux.Logger;
import jotato.quantumflux.helpers.EntityHelpers;
import jotato.quantumflux.registers.ItemRegister;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles", striprefs = true)
public class ItemMagnet extends ItemBase implements IBauble{
	protected double distanceFromPlayer;
	protected static String name = "magnet";

	public ItemMagnet() {
		super(name);
		setMaxStackSize(1);
		this.distanceFromPlayer = ConfigMan.magnet_range;
		canRepair = false;
		setMaxDamage(0);

	}

	@Override
	public void initModel() {
		Logger.devLog("    Registering model for %s", getSimpleName());
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	public boolean hasEffect(ItemStack item) {
		return isActivated(item);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemStackIn = playerIn.getHeldItem(handIn);
		if (!worldIn.isRemote && playerIn.isSneaking()) {
			itemStackIn.setItemDamage(itemStackIn.getItemDamage() == 0 ? 1 : 0);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onUpdate(ItemStack item, World world, Entity entity, int i, boolean f) {
		if(!world.isRemote && entity instanceof EntityPlayer) {
			doMagnet(item, (EntityPlayer) entity, entity.getEntityWorld());
		}
	}
	
	public static void doMagnet(ItemStack item, EntityPlayer player, World world) {
		if (world.isRemote)
			return;
		if (!isActivated(item))
			return;

		// items
		Iterator iterator = EntityHelpers.getEntitiesInRange(EntityItem.class, world, player.posX, player.posY,
				player.posZ, ConfigMan.magnet_range).iterator();
		while (iterator.hasNext()) {
			EntityItem itemToGet = (EntityItem) iterator.next();
			if (itemToGet.isDead || itemToGet.getEntityData().getBoolean("PreventRemoteMovement")) {
				continue;
			}
			if(itemToGet.ticksExisted<=1) itemToGet.setPickupDelay(1);
			itemToGet.onCollideWithPlayer(player);
		}

		// xp
		iterator = EntityHelpers.getEntitiesInRange(EntityXPOrb.class, world, player.posX, player.posY, player.posZ,
				ConfigMan.magnet_range).iterator();
		while (iterator.hasNext()) {
			EntityXPOrb xpToGet = (EntityXPOrb) iterator.next();
			player.xpCooldown=0;
			xpToGet.delayBeforeCanPickup=0;
			xpToGet.onCollideWithPlayer(player);
		}
	}

	public static boolean isActivated(ItemStack item) {
		if(item.getItem() == ItemRegister.magnet) {
			return item.getItemDamage() == 1;
		}
		
		return false;
	}
	
	@Optional.Method(modid = "baubles")
	@Override
	public void onWornTick(ItemStack stack, EntityLivingBase player) {
		if (player instanceof EntityPlayer && isActivated(stack)) {
			doMagnet(stack, (EntityPlayer) player, player.getEntityWorld());
		}
	}
	
	@Optional.Method(modid = "baubles")
	@Override
	public BaubleType getBaubleType(ItemStack itemstack) {
		return BaubleType.TRINKET;
	}
	
	public static void toggleMagnetWithMessage(ItemStack stack, EntityPlayer player) {
		if(stack.isEmpty()) return;
		
		if(isActivated(stack)) {
			stack.setItemDamage(0);
			player.sendMessage(new TextComponentString("Magnet disabled"));
		}
		else{
			stack.setItemDamage(1);
			player.sendMessage(new TextComponentString("Magnet enabled"));
		}	
	}
}
