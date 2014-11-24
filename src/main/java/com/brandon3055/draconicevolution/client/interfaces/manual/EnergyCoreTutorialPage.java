package com.brandon3055.draconicevolution.client.interfaces.manual;

import com.brandon3055.draconicevolution.common.lib.References;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Created by Brandon on 20/09/2014.
 */
public class EnergyCoreTutorialPage extends  TutorialPage {
	public static final ResourceLocation Energy_Core = new ResourceLocation(References.RESOURCESPREFIX + "textures/gui/images/T5_Energy_Core.png");
	public static final ResourceLocation Stabilizer_Placement = new ResourceLocation(References.RESOURCESPREFIX + "textures/gui/images/Stabilizer_Placement.png");
	public static final ResourceLocation Core_Tiers = new ResourceLocation(References.RESOURCESPREFIX + "textures/gui/images/Core_Tiers.png");
	public static final ResourceLocation Pylon = new ResourceLocation(References.RESOURCESPREFIX + "textures/gui/images/Energy_Pylon.png");
	public static final ResourceLocation Pylon_Particles = new ResourceLocation(References.RESOURCESPREFIX + "textures/gui/images/Energy_Pylon_Particles.png");
	public static final ResourceLocation Pylon_Particles_2 = new ResourceLocation(References.RESOURCESPREFIX + "textures/gui/images/Energy_Pylon_Particles_2.png");
	public static final ResourceLocation Pylon_y_Placement = new ResourceLocation(References.RESOURCESPREFIX + "textures/gui/images/Pylon_y_Placement.png");


	public EnergyCoreTutorialPage(String name, PageCollection collection) {
		super(name, collection, StatCollector.translateToLocal("manual.de.ecth.txt"));
		this.lastPage = 11;
		this.formattedDescription = new List[15];
	}

	@Override
	public void renderBackgroundLayer(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.renderBackgroundLayer(minecraft, offsetX, offsetY, mouseX, mouseY);

	}

	@Override
	public void drawScreen(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.drawScreen(minecraft, offsetX, offsetY, mouseX, mouseY);
		GL11.glColor4f(1f, 1f, 1f, 1f);
		switch (page){
			case 0:
				page0(minecraft, offsetX, offsetY, mouseX, mouseY);
				break;
			case 1:
				page1(minecraft, offsetX, offsetY, mouseX, mouseY);
				break;
			case 2:
				page2(minecraft, offsetX, offsetY, mouseX, mouseY);
				break;
			case 3:
				page3(minecraft, offsetX, offsetY, mouseX, mouseY);
				break;
			case 4:
				page4(minecraft, offsetX, offsetY, mouseX, mouseY);
				break;
			case 5:
				page5(minecraft, offsetX, offsetY, mouseX, mouseY);
				break;
			case 6:
				page6(minecraft, offsetX, offsetY, mouseX, mouseY);
				break;
			case 7:
				page7(minecraft, offsetX, offsetY, mouseX, mouseY);
				break;
			case 8:
				page8(minecraft, offsetX, offsetY, mouseX, mouseY);
				break;
			case 9:
				page9(minecraft, offsetX, offsetY, mouseX, mouseY);
				break;
			case 10:
				page10(minecraft, offsetX, offsetY, mouseX, mouseY);
				break;
			case 11:
				page11(minecraft, offsetX, offsetY, mouseX, mouseY);
				break;
		}
	}

	private void page0(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY){
		minecraft.renderEngine.bindTexture(Energy_Core);
		GL11.glPushMatrix();
		GL11.glScalef(0.5f, 0.5f, 1f);
		drawTexturedModalRect((offsetX+64)*2, (offsetY+35)*2, 0, 0, 256, 256);
		GL11.glPopMatrix();
		addDescription(minecraft, offsetX, offsetY-60, getFormattedText(fontRendererObj, ttl("manual.de.ectdpage0a.txt"), 0));
		addDescription(minecraft, offsetX, offsetY+55, getFormattedText(fontRendererObj, ttl("manual.de.ectdpage0b.txt"), 1));
	}

	private void page1(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY){
		minecraft.renderEngine.bindTexture(Stabilizer_Placement);
		GL11.glPushMatrix();
		double imageScale = 0.75 ;
		GL11.glScaled(imageScale, imageScale, 1D);
		double scaleMult = 1.125;
		drawTexturedModalRect((int)((offsetX+64)*scaleMult), (int)((offsetY+40)*scaleMult), 0, 0, 256, 256);
		GL11.glPopMatrix();
		addDescription(minecraft, offsetX, offsetY-60, getFormattedText(fontRendererObj, ttl("manual.de.ectdpage1a.txt"), 2));
		addDescription(minecraft, offsetX, offsetY+45, getFormattedText(fontRendererObj, ttl("manual.de.ectdpage1b.txt"), 3));
	}

	private void page2(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY){
		minecraft.renderEngine.bindTexture(Core_Tiers);
		GL11.glPushMatrix();
		double imageScale = 5 ;
		GL11.glScaled(imageScale, imageScale, 1D);
		double scaleMult = 0.21;

		int yOffset = (int)((minecraft.getSystemTime()/2000 % 2)*22);
		drawTexturedModalRect((int)((offsetX+49)*scaleMult), (int)((offsetY+15)*scaleMult), 180, 0+yOffset, 30, 20);
		GL11.glPopMatrix();
		drawCenteredString(fontRendererObj, "Tier 1", offsetX+128, offsetY+107, 0xff0000);
		addDescription(minecraft, offsetX, offsetY+45, getFormattedText(fontRendererObj, ttl("manual.de.ectdpage2.txt"), 4));
	}

	private void page3(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY){
		minecraft.renderEngine.bindTexture(Core_Tiers);
		GL11.glPushMatrix();
		double imageScale = 5 ;
		GL11.glScaled(imageScale, imageScale, 1D);
		double scaleMult = 0.21;

		int yOffset = (int)((minecraft.getSystemTime()/2000 % 2)*22);
		drawTexturedModalRect((int)((offsetX+54)*scaleMult), (int)((offsetY+15)*scaleMult), 151, 0+yOffset, 28, 20);
		GL11.glPopMatrix();
		drawCenteredString(fontRendererObj, "Tier 2", offsetX+128, offsetY+107, 0xff0000);
		addDescription(minecraft, offsetX, offsetY+45, getFormattedText(fontRendererObj, ttl("manual.de.ectdpage3.txt"), 5));
	}

	private void page4(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY){
		minecraft.renderEngine.bindTexture(Core_Tiers);
		GL11.glPushMatrix();
		double imageScale = 5 ;
		GL11.glScaled(imageScale, imageScale, 1D);
		double scaleMult = 0.21;

		int yOffset = (int)((minecraft.getSystemTime()/2000 % 3)*22);
		drawTexturedModalRect((int)((offsetX+54)*scaleMult), (int)((offsetY+15)*scaleMult), 123, 0+yOffset, 28, 20);
		GL11.glPopMatrix();
		drawCenteredString(fontRendererObj, "Tier 3", offsetX+128, offsetY+107, 0xff0000);
		addDescription(minecraft, offsetX, offsetY+45, getFormattedText(fontRendererObj, ttl("manual.de.ectdpage4.txt"), 6));
	}

	private void page5(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY){
		minecraft.renderEngine.bindTexture(Core_Tiers);
		GL11.glPushMatrix();
		double imageScale = 5 ;
		GL11.glScaled(imageScale, imageScale, 1D);
		double scaleMult = 0.21;

		int yOffset = (int)((minecraft.getSystemTime()/2000 % 4)*22);
		drawTexturedModalRect((int)((offsetX+60)*scaleMult), (int)((offsetY+15)*scaleMult), 98, 0+yOffset, 25, 20);
		GL11.glPopMatrix();
		drawCenteredString(fontRendererObj, "Tier 4", offsetX+128, offsetY+107, 0xff0000);
		addDescription(minecraft, offsetX, offsetY+45, getFormattedText(fontRendererObj, ttl("manual.de.ectdpage5.txt"), 7));
	}

	private void page6(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY){
		minecraft.renderEngine.bindTexture(Core_Tiers);
		GL11.glPushMatrix();
		double imageScale = 3 ;
		GL11.glScaled(imageScale, imageScale, 1D);
		double scaleMult = 0.338;

		int yOffset = (int)((minecraft.getSystemTime()/2000 % 5)*43 );
		drawTexturedModalRect((int)((offsetX+60)*scaleMult), (int)((offsetY+15)*scaleMult), 53, 0+yOffset, 44, 41);
		GL11.glPopMatrix();
		drawCenteredString(fontRendererObj, "Tier 5", offsetX+128, offsetY+130, 0xff0000);
		addDescription(minecraft, offsetX, offsetY+65, getFormattedText(fontRendererObj, ttl("manual.de.ectdpage6.txt"), 8));
	}

	private void page7(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY){
		minecraft.renderEngine.bindTexture(Core_Tiers);
		GL11.glPushMatrix();
		double imageScale = 3 ;
		GL11.glScaled(imageScale, imageScale, 1D);
		double scaleMult = 0.338;

		int yOffset = (int)((minecraft.getSystemTime()/2000 % 6)*43 );
		drawTexturedModalRect((int)((offsetX+49)*scaleMult), (int)((offsetY+15)*scaleMult), 0, 0+yOffset, 52, 41);
		GL11.glPopMatrix();
		drawCenteredString(fontRendererObj, "Tier 6", offsetX+128, offsetY+130, 0xff0000);
		addDescription(minecraft, offsetX, offsetY+65, getFormattedText(fontRendererObj, ttl("manual.de.ectdpage7.txt"), 9));
	}

	private void page8(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY){
		minecraft.renderEngine.bindTexture(Pylon);
		GL11.glPushMatrix();
		double imageScale = 0.75 ;
		GL11.glScaled(imageScale, imageScale, 1D);
		double scaleMult = 1.34;
		drawTexturedModalRect((int)((offsetX+32)*scaleMult), (int)((offsetY+15)*scaleMult), 0, 20, 256, 127);
		GL11.glPopMatrix();
		addDescription(minecraft, offsetX, offsetY+35, getFormattedText(fontRendererObj, ttl("manual.de.ectdpage8a.txt"), 10));
		addDescription(minecraft, offsetX, offsetY+75, getFormattedText(fontRendererObj, ttl("manual.de.ectdpage8b.txt"), 11));
	}

	private void page9(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY){
		minecraft.renderEngine.bindTexture(Pylon_Particles);
		GL11.glPushMatrix();
		double imageScale = 0.75 ;
		GL11.glScaled(imageScale, imageScale, 1D);
		double scaleMult = 1.34;
		drawTexturedModalRect((int)((offsetX+32)*scaleMult), (int)((offsetY+15)*scaleMult), 0, 30, 256, 177);
		GL11.glPopMatrix();
		addDescription(minecraft, offsetX, offsetY+45, getFormattedText(fontRendererObj, ttl("manual.de.ectdpage9a.txt"), 11));
		addDescription(minecraft, offsetX, offsetY+75, getFormattedText(fontRendererObj, ttl("manual.de.ectdpage9b.txt"), 12));
	}

	private void page10(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY){
		minecraft.renderEngine.bindTexture(Pylon_Particles_2);
		GL11.glPushMatrix();
		double imageScale = 0.75 ;
		GL11.glScaled(imageScale, imageScale, 1D);
		double scaleMult = 1.34;
		drawTexturedModalRect((int)((offsetX+32)*scaleMult), (int)((offsetY+15)*scaleMult), 0, 0, 256, 167);
		GL11.glPopMatrix();
		addDescription(minecraft, offsetX, offsetY+75, getFormattedText(fontRendererObj, ttl("manual.de.ectdpage10.txt"), 13));
	}

	private void page11(Minecraft minecraft, int offsetX, int offsetY, int mouseX, int mouseY){
		minecraft.renderEngine.bindTexture(Pylon_y_Placement);
		GL11.glPushMatrix();
		double imageScale = 0.75 ;
		GL11.glScaled(imageScale, imageScale, 1D);
		double scaleMult = 1.34;
		drawTexturedModalRect((int)((offsetX+32)*scaleMult), (int)((offsetY+15)*scaleMult), 0, 4, 256, 197);
		GL11.glPopMatrix();
		addDescription(minecraft, offsetX, offsetY+90, getFormattedText(fontRendererObj, ttl("manual.de.ectdpage11.txt"), 14));
	}
}
