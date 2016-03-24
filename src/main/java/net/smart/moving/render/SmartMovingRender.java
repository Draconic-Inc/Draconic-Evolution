// ==================================================================
// This file is part of Smart Moving.
//
// Smart Moving is free software: you can redistribute it and/or
// modify it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of the
// License, or (at your option) any later version.
//
// Smart Moving is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Smart Moving. If not, see <http://www.gnu.org/licenses/>.
// ==================================================================

package net.smart.moving.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;

import net.smart.moving.*;
import net.smart.render.statistics.*;

public class SmartMovingRender extends SmartRenderContext
{
	public static SmartMovingModel CurrentMainModel;

	public IRenderPlayer irp;

	public SmartMovingRender(IRenderPlayer irp)
	{
		this.irp = irp;

		modelBipedMain = irp.getPlayerModelBipedMain().getMovingModel();
		SmartMovingModel modelArmorChestplate = irp.getPlayerModelArmorChestplate().getMovingModel();
		SmartMovingModel modelArmor = irp.getPlayerModelArmor().getMovingModel();

		modelBipedMain.scaleArmType = Scale;
		modelBipedMain.scaleLegType = Scale;
		modelArmorChestplate.scaleArmType = NoScaleStart;
		modelArmorChestplate.scaleLegType = NoScaleEnd;
		modelArmor.scaleArmType = NoScaleStart;
		modelArmor.scaleLegType = Scale;
	}

	public void renderPlayer(AbstractClientPlayer entityplayer, double d, double d1, double d2, float f, float renderPartialTicks)
	{
		IModelPlayer[] modelPlayers = null;
		SmartMoving moving = SmartMovingFactory.getInstance(entityplayer);
		if(moving != null)
		{
			boolean isInventory = d == 0.0F && d1 == 0.0F && d2 == 0.0F && f == 0.0F && renderPartialTicks == 1.0F;

			boolean isClimb = moving.isClimbing && !moving.isCrawling && !moving.isCrawlClimbing && !moving.isClimbJumping;
			boolean isClimbJump = moving.isClimbJumping;
			int handsClimbType = moving.actualHandsClimbType;
			int feetClimbType = moving.actualFeetClimbType;
			boolean isHandsVineClimbing = moving.isHandsVineClimbing;
			boolean isFeetVineClimbing = moving.isFeetVineClimbing;
			boolean isCeilingClimb = moving.isCeilingClimbing;
			boolean isSwim = moving.isSwimming && !moving.isDipping;
			boolean isDive = moving.isDiving;
			boolean isLevitate = moving.isLevitating;
			boolean isCrawl = moving.isCrawling && !moving.isClimbing;
			boolean isCrawlClimb = moving.isCrawlClimbing || (moving.isClimbing && moving.isCrawling);
			boolean isJump = moving.isJumping();
			boolean isHeadJump = moving.isHeadJumping;
			boolean isFlying = moving.doFlyingAnimation();
			boolean isSlide = moving.isSliding;
			boolean isFalling = moving.doFallingAnimation();
			boolean isGenericSneaking = moving.isSlow;
			boolean isAngleJumping = moving.isAngleJumping();
			int angleJumpType = moving.angleJumpType;
			boolean isRopeSliding = moving.isRopeSliding;

			SmartStatistics statistics = SmartStatisticsFactory.getInstance(entityplayer);
			float currentHorizontalSpeedFlattened = statistics != null ? statistics.getCurrentHorizontalSpeedFlattened(renderPartialTicks, -1) : Float.NaN;
			float smallOverGroundHeight = isCrawlClimb || isHeadJump ? (float)moving.getOverGroundHeight(5D) : 0F;
			Block overGroundBlock = isHeadJump && smallOverGroundHeight < 5F ? moving.getOverGroundBlockId(smallOverGroundHeight) : null;

			modelPlayers = irp.getPlayerModels();

			for(int i = 0; i < modelPlayers.length; i++)
			{
				SmartMovingModel modelPlayer = modelPlayers[i].getMovingModel();
				modelPlayer.isClimb = isClimb;
				modelPlayer.isClimbJump = isClimbJump;
				modelPlayer.handsClimbType = handsClimbType;
				modelPlayer.feetClimbType = feetClimbType;
				modelPlayer.isHandsVineClimbing = isHandsVineClimbing;
				modelPlayer.isFeetVineClimbing = isFeetVineClimbing;
				modelPlayer.isCeilingClimb = isCeilingClimb;
				modelPlayer.isSwim = isSwim;
				modelPlayer.isDive = isDive;
				modelPlayer.isCrawl = isCrawl;
				modelPlayer.isCrawlClimb = isCrawlClimb;
				modelPlayer.isJump = isJump;
				modelPlayer.isHeadJump = isHeadJump;
				modelPlayer.isSlide = isSlide;
				modelPlayer.isFlying = isFlying;
				modelPlayer.isLevitate = isLevitate;
				modelPlayer.isFalling = isFalling;
				modelPlayer.isGenericSneaking = isGenericSneaking;
				modelPlayer.isAngleJumping = isAngleJumping;
				modelPlayer.angleJumpType = angleJumpType;
				modelPlayer.isRopeSliding = isRopeSliding;

				modelPlayer.currentHorizontalSpeedFlattened = currentHorizontalSpeedFlattened;
				modelPlayer.smallOverGroundHeight = smallOverGroundHeight;
				modelPlayer.overGroundBlock = overGroundBlock;
			}

			if (!isInventory && entityplayer.isSneaking() && !(entityplayer instanceof EntityPlayerSP) && isCrawl)
				d1 += 0.125D;
		}

		CurrentMainModel = modelBipedMain;
		irp.superRenderRenderPlayer(entityplayer, d, d1, d2, f, renderPartialTicks);
		CurrentMainModel = null;

		if (moving != null && moving.isLevitating && modelPlayers != null)
			for(int i = 0; i < modelPlayers.length; i++)
				modelPlayers[i].getMovingModel().md.currentHorizontalAngle = modelPlayers[i].getMovingModel().md.currentCameraAngle;
	}

	public void rotatePlayer(AbstractClientPlayer entityplayer, float totalTime, float actualRotation, float f2)
	{
		SmartMoving moving = SmartMovingFactory.getInstance(entityplayer);
		if(moving != null)
		{
			boolean isInventory = f2 == 1.0F && moving.isp != null && moving.isp.getMcField().currentScreen instanceof GuiInventory;
			if(!isInventory)
			{
				float forwardRotation = entityplayer.prevRotationYaw + (entityplayer.rotationYaw - entityplayer.prevRotationYaw) * f2;

				if(moving.isClimbing || moving.isClimbCrawling || moving.isCrawlClimbing || moving.isFlying || moving.isSwimming || moving.isDiving || moving.isCeilingClimbing || moving.isHeadJumping || moving.isSliding || moving.isAngleJumping())
					entityplayer.renderYawOffset = forwardRotation;
			}
		}
		irp.superRenderRotatePlayer(entityplayer, totalTime, actualRotation, f2);
	}

	public void renderPlayerAt(AbstractClientPlayer entityplayer, double d, double d1, double d2)
	{
		if(entityplayer instanceof EntityOtherPlayerMP)
		{
			SmartMoving moving = SmartMovingFactory.getOtherSmartMoving(entityplayer.getEntityId());
			if(moving != null && moving.heightOffset != 0)
				d1 += moving.heightOffset;
		}
		irp.superRenderRenderPlayerAt(entityplayer, d, d1, d2);
	}

	public void renderName(EntityLivingBase entityPlayer, double d, double d1, double d2)
	{
		boolean changedIsSneaking = false, originalIsSneaking = false;
		if(Minecraft.isGuiEnabled() && entityPlayer != irp.getRenderManager().livingPlayer)
		{
			SmartMoving moving = entityPlayer instanceof EntityPlayer ? SmartMovingFactory.getInstance((EntityPlayer)entityPlayer) : null;
			if(moving != null)
			{
				originalIsSneaking = entityPlayer.isSneaking();
				boolean temporaryIsSneaking = originalIsSneaking;
				if(moving.isCrawling && !moving.isClimbing)
					temporaryIsSneaking = !Config._crawlNameTag.value;
				else if(originalIsSneaking)
					temporaryIsSneaking = !Config._sneakNameTag.value;

				changedIsSneaking = temporaryIsSneaking != originalIsSneaking;
				if(changedIsSneaking)
					entityPlayer.setSneaking(temporaryIsSneaking);

				if(moving.heightOffset == -1)
					d1 -= 0.2F;
				else if(originalIsSneaking && !temporaryIsSneaking)
					d1 -= 0.05F;
			}
		}

		irp.superRenderRenderName(entityPlayer, d, d1, d2);

		if(changedIsSneaking)
			entityPlayer.setSneaking(originalIsSneaking);
	}

	public static void renderGuiIngame(Minecraft minecraft)
	{
		if (!Client.getNativeUserInterfaceDrawing())
			return;

		if(!GL11.glGetBoolean(GL11.GL_ALPHA_TEST))
			return;

		SmartMovingSelf moving = (SmartMovingSelf)SmartMovingFactory.getInstance(minecraft.thePlayer);
		if(moving != null && Config.enabled && (Options._displayExhaustionBar.value || Options._displayJumpChargeBar.value))
		{
			ScaledResolution scaledresolution = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
			int width = scaledresolution.getScaledWidth();
			int height = scaledresolution.getScaledHeight();

			if(minecraft.playerController.shouldDrawHUD())
			{
				float maxExhaustion = Client.getMaximumExhaustion();
				float exhaustion = Math.min(moving.exhaustion, maxExhaustion);
				boolean drawExhaustion = exhaustion > 0 && exhaustion <= maxExhaustion;

				float maxStillJumpCharge = Config._jumpChargeMaximum.value;
				float stillJumpCharge = Math.min(moving.jumpCharge, maxStillJumpCharge);

				float maxRunJumpCharge = Config._headJumpChargeMaximum.value;
				float runJumpCharge = Math.min(moving.headJumpCharge, maxRunJumpCharge);

				boolean drawJumpCharge = stillJumpCharge > 0 || runJumpCharge > 0;
				float maxJumpCharge = stillJumpCharge > runJumpCharge ? maxStillJumpCharge : maxRunJumpCharge;
				float jumpCharge = Math.max(stillJumpCharge, runJumpCharge);

				if(drawExhaustion || drawJumpCharge)
				{
					GL11.glPushAttrib(GL11.GL_TEXTURE_BIT);
					minecraft.getTextureManager().bindTexture(new ResourceLocation("smartmoving", "gui/icons.png"));
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					_minecraft = minecraft;
				}

				if(drawExhaustion)
				{
					float maxExhaustionForAction = Math.min(moving.maxExhaustionForAction, maxExhaustion);
					float maxExhaustionToStartAction = Math.min(moving.maxExhaustionToStartAction, maxExhaustion);

					float fitness = maxExhaustion - exhaustion;
					float minFitnessForAction = Float.isNaN(maxExhaustionForAction) ? 0 : maxExhaustion - maxExhaustionForAction;
					float minFitnessToStartAction = Float.isNaN(maxExhaustionToStartAction) ? 0 : maxExhaustion - maxExhaustionToStartAction;

					float maxFitnessDrawn = Math.max(Math.max(minFitnessToStartAction, fitness), minFitnessForAction);

					int halfs = (int)Math.floor(maxFitnessDrawn / maxExhaustion * 21F);
					int fulls = halfs / 2;
					int half = halfs % 2;

					int fitnessHalfs = (int)Math.floor(fitness / maxExhaustion * 21F);
					int fitnessFulls = fitnessHalfs / 2;
					int fitnessHalf = fitnessHalfs % 2;

					int minFitnessForActionHalfs = (int)Math.floor(minFitnessForAction / maxExhaustion * 21F);
					int minFitnessForActionFulls = minFitnessForActionHalfs / 2;
					int minFitnessForActionHalf = minFitnessForActionHalfs % 2;

					int minFitnessToStartActionHalfs = (int)Math.floor(minFitnessToStartAction / maxExhaustion * 21F);
					int minFitnessToStartActionFulls = minFitnessToStartActionHalfs / 2;

					_jOffset = height - 39 - 10 - (minecraft.thePlayer.isInsideOfMaterial(Material.water) ? 10 : 0);
					for(int i = 0; i < Math.min(fulls + half, 10); i++)
					{
						_iOffset = (width / 2 + 90) - (i + 1) * 8;
						if(i < fitnessFulls)
						{
							if(i < minFitnessForActionFulls)
								drawIcon(2, 2);
							else if(i == minFitnessForActionFulls && minFitnessForActionHalf > 0)
								drawIcon(3, 2);
							else
								drawIcon(0, 0);
						}
						else if(i == fitnessFulls && fitnessHalf > 0)
						{
							if(i < minFitnessForActionFulls)
								drawIcon(1, 2);
							else if(i == minFitnessForActionFulls && minFitnessForActionHalf > 0)
								if(i < minFitnessToStartActionFulls)
									drawIcon(3, 1);
								else
									drawIcon(4, 2);
							else
								if(i < minFitnessToStartActionFulls)
									drawIcon(1, 1);
								else
									drawIcon(1, 0);
						}
						else
						{
							if(i < minFitnessForActionFulls)
								drawIcon(0, 2);
							else if(i == minFitnessForActionFulls && minFitnessForActionHalf > 0)
								if(i < minFitnessToStartActionFulls)
									drawIcon(2, 1);
								else
									drawIcon(5, 2);
							else
								if(i < minFitnessToStartActionFulls)
									drawIcon(0, 1);
								else
									drawIcon(4, 1);
						}
					}
				}

				if(drawJumpCharge)
				{
					boolean max = jumpCharge == maxJumpCharge;
					int fulls = max ? 10 : (int)Math.ceil(((jumpCharge - 2) * 10D) / maxJumpCharge);
					int half = max ? 0 : (int)Math.ceil((jumpCharge * 10D) / maxJumpCharge) - fulls;

					_jOffset = height - 39 - 10 - (minecraft.thePlayer.getTotalArmorValue() > 0 ? 10 : 0);
					for(int i = 0; i < fulls + half; i++)
					{
						_iOffset = (width / 2 - 91) + i * 8;
						drawIcon(i < fulls ? 2 : 3, 0);
					}
				}

				if(drawExhaustion || drawJumpCharge)
					GL11.glPopAttrib();
			}
		}
	}

	private static void drawIcon(int x, int y)
	{
		_minecraft.ingameGUI.drawTexturedModalRect(_iOffset, _jOffset, x * 9, y * 9, 9, 9);
	}

	public final SmartMovingModel modelBipedMain;

	private static int _iOffset, _jOffset;
	private static Minecraft _minecraft;
}