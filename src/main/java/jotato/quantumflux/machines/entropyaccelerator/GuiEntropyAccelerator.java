package jotato.quantumflux.machines.entropyaccelerator;

import jotato.quantumflux.QuantumFluxMod;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

public class GuiEntropyAccelerator extends GuiContainer {

	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(QuantumFluxMod.MODID, "textures/gui/entropyaccelerator.png");

	TileEntropyAccelerator tileEntity;

	public GuiEntropyAccelerator(TileEntropyAccelerator tileEntity, ContainerEntropyAccelerator container) {
		super(container);
		this.tileEntity = tileEntity;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.getTextureManager().bindTexture(GUI_TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		int progress = (int) (((float) this.tileEntity.getProgress() / (float) this.tileEntity.maxBurnTime) * 25);
		drawTexturedModalRect(guiLeft + 82, guiTop + 36, 0, 173, 12, progress);

		int energy = (int) (((float) this.tileEntity.getEnergyStored() / (float) this.tileEntity.getMaxEnergyStored()) * 124);
		drawTexturedModalRect(guiLeft + 26, guiTop + 66, 0, 166, energy, 7);

		fontRenderer.drawStringWithShadow(this.tileEntity.getEnergyStored() + "/" + this.tileEntity.getMaxEnergyStored() + " RF",
				guiLeft + 20, guiTop + 60, 0xFFFFFFFF);
	}
}
