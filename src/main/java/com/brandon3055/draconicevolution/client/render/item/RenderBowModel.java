package com.brandon3055.draconicevolution.client.render.item;

import com.brandon3055.brandonscore.common.utills.ItemNBTHelper;
import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.common.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

/**
 * Created by brandon3055 on 29/10/2015.
 */
public class RenderBowModel implements IItemRenderer
{
	private boolean draconic;
	private IModelCustom[] wyvernModels = new IModelCustom[4];
	private IModelCustom[] draconicModels = new IModelCustom[4];
	private IModelCustom arrow;

	public RenderBowModel(boolean draconic){
		this.draconic = draconic;

		for (int i = 0; i < 4; i++) wyvernModels[i] = AdvancedModelLoader.loadModel(ResourceHandler.getResource("models/tools/WyvernBow0"+i+".obj"));
		for (int i = 0; i < 4; i++) draconicModels[i] = AdvancedModelLoader.loadModel(ResourceHandler.getResource("models/tools/DraconicBow0"+i+".obj"));
		this.arrow = AdvancedModelLoader.loadModel(ResourceHandler.getResource("models/tools/ArrowCommon.obj"));
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return false;//type == ItemRenderType.ENTITY && helper == ItemRendererHelper.ENTITY_ROTATION;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		GL11.glPushMatrix();

		String currentMode = ItemNBTHelper.getString(item, "mode", "rapidfire");
		int j = 0;

		if (data.length >= 2 && (data[1] instanceof EntityPlayer)) {
			EntityPlayer player = (EntityPlayer) data[1];
			if (player.inventory.hasItem(ModItems.enderArrow)) currentMode = "sharpshooter";
			j = player.getItemInUseDuration();
		}

		IModelCustom activeModel = null;
		int selection = 0;

		if (draconic)
		{

			if (currentMode.equals("rapidfire"))
			{
				if (j >= 4) selection = 3;
				else if (j > 2) selection = 2;
				else if (j > 0) selection = 1;
			} else if (currentMode.equals("devistation"))
			{
				if (j >= 2) selection = 3;
				else if (j > 1) selection = 2;
				else if (j > 0) selection = 1;
			} else if (currentMode.equals("sharpshooter"))
			{
				if (j >= 20) selection = 3;
				else if (j > 10) selection = 2;
				else if (j > 0) selection = 1;
			} else
			{
				if (j >= 80) selection = 3;
				else if (j > 40) selection = 2;
				else if (j > 0) selection = 1;
			}

			activeModel = draconicModels[selection];
			ResourceHandler.bindResource("textures/models/tools/DraconicBow0" + selection + ".png");
		}else
		{

			if (currentMode.equals("rapidfire"))
			{
				if (j >= 13) selection = 2;
				else if (j > 7) selection = 1;
				else if (j > 0) selection = 0;
			} else if (currentMode.equals("sharpshooter"))
			{
				if (j >= 30) selection = 2;
				else if (j > 15) selection = 1;
				else if (j > 0) selection = 0;
			}

			activeModel = draconicModels[selection];
			ResourceHandler.bindResource("textures/models/tools/WyvernBow0"+selection+".png");
		}



		if (activeModel != null) doRender(activeModel, type, j > 0 ? selection : -1);

		GL11.glPopMatrix();
	}

	private void doRender(IModelCustom modelCustom, ItemRenderType type, int drawState){

		if (type == ItemRenderType.EQUIPPED)
		{
			GL11.glScaled(0.8, 0.8, 0.8);
			GL11.glTranslated(0.7, 0, 0.2);
			GL11.glRotatef(87, 0, 0, 1);
			GL11.glRotatef(190, 1, 0, 0);
		}
		else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON){
			GL11.glScaled(0.8, 0.8, 0.8);
			GL11.glTranslated(0.7, 0.7, 0.2);
			GL11.glRotatef(130, 0, 0, 1);
			GL11.glRotatef(-90, 1, 0, 0);
		}
		else if (type == IItemRenderer.ItemRenderType.INVENTORY){
			GL11.glScalef(6F, 6F, 6F);
			GL11.glRotatef(90, 1, 0, 0);
			GL11.glRotatef(135, 0, 1, 0);
			GL11.glTranslated(0, 0, 1.5);
		}
		else if (type == IItemRenderer.ItemRenderType.ENTITY){
			GL11.glRotatef(-34.5F, 0, 1, 0);
			GL11.glTranslated(-1.1, 0, -0.2);
		}


		modelCustom.renderAll();
		GL11.glTranslated(0.3, 0.151, -0.2 + (drawState == 1 ? 0 : drawState == 2 ? 0.55 : drawState == 3 ? 1 : -0.7));
		GL11.glRotatef(90, 0, 0, 1);
		if (drawState != -1)arrow.renderAll();

	}
}
