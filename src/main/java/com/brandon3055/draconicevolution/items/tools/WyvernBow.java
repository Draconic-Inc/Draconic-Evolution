package com.brandon3055.draconicevolution.items.tools;

import codechicken.lib.model.SimpleOverrideBakedModel;
import codechicken.lib.render.ModelRegistryHelper;
import com.brandon3055.brandonscore.config.Feature;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.itemconfig.BooleanConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.DoubleConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.IntegerConfigField;
import com.brandon3055.draconicevolution.api.itemconfig.ItemConfigFieldRegistry;
import com.brandon3055.draconicevolution.api.itemupgrade.UpgradeHelper;
import com.brandon3055.draconicevolution.client.model.tool.BowModelOverrideList;
import com.brandon3055.draconicevolution.handlers.BowHandler;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static com.brandon3055.draconicevolution.api.itemconfig.IItemConfigField.EnumControlType.SLIDER;

/**
 * Created by brandon3055 on 2/06/2016.
 */
public class WyvernBow extends ToolBase {

    public WyvernBow(float attackDamage, float attackSpeed) {
        super(attackDamage, attackSpeed);
    }

    public WyvernBow() {
        super(1, 0);//TODO Attack Damage and speed
        setEnergyStats(ToolStats.WYVERN_BASE_CAPACITY, 512000, 0);
    }

    //region Bow Stuff

    private ItemStack findAmmo(EntityPlayer player) {
        if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND)))
        {
            return player.getHeldItem(EnumHand.OFF_HAND);
        }
        else if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND)))
        {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        }
        else
        {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack = player.inventory.getStackInSlot(i);

                if (this.isArrow(itemstack))
                {
                    return itemstack;
                }
            }

            return null;
        }
    }

    protected boolean isArrow(@Nullable ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemArrow;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
       if (entityLiving instanceof EntityPlayer) {
           BowHandler.onPlayerStoppedUsingBow(stack, world, (EntityPlayer) entityLiving, timeLeft);
       }
//        if (entityLiving instanceof EntityPlayer)
//        {
//            EntityPlayer entityplayer = (EntityPlayer)entityLiving;
//            boolean flag = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
//            ItemStack itemstack = this.findAmmo(entityplayer);
//
//            int i = this.getMaxItemUseDuration(stack) - timeLeft;
//            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, (EntityPlayer)entityLiving, i, itemstack != null || flag);
//            if (i < 0) return;
//
//            if (itemstack != null || flag)
//            {
//                if (itemstack == null)
//                {
//                    itemstack = new ItemStack(Items.ARROW);
//                }
//
//                float f = getArrowVelocity(i);
//
//                if ((double)f >= 0.1D)
//                {
//                    boolean flag1 = entityplayer.capabilities.isCreativeMode || (itemstack.getItem() instanceof ItemArrow ? ((ItemArrow)itemstack.getItem()).isInfinite(itemstack, stack, entityplayer) : false);
//
//                    if (!worldIn.isRemote)
//                    {
//                        ItemArrow itemarrow = (ItemArrow)((ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW));
//                        EntityArrow entityarrow = itemarrow.createArrow(worldIn, itemstack, entityplayer);
//                        entityarrow.setAim(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, f * 3.0F, 1.0F);
//
//                        if (f == 1.0F)
//                        {
//                            entityarrow.setIsCritical(true);
//                        }
//
//                        int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
//
//                        if (j > 0)
//                        {
//                            entityarrow.setDamage(entityarrow.getDamage() + (double)j * 0.5D + 0.5D);
//                        }
//
//                        int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
//
//                        if (k > 0)
//                        {
//                            entityarrow.setKnockbackStrength(k);
//                        }
//
//                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0)
//                        {
//                            entityarrow.setFire(100);
//                        }
//
//                        stack.damageItem(1, entityplayer);
//
//                        if (flag1)
//                        {
//                            entityarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
//                        }
//
//                        worldIn.spawnEntityInWorld(entityarrow);
//                    }
//
//                    worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
//
//                    if (!flag1)
//                    {
//                        --itemstack.stackSize;
//
//                        if (itemstack.stackSize == 0)
//                        {
//                            entityplayer.inventory.deleteStack(itemstack);
//                        }
//                    }
//
//                    entityplayer.addStat(StatList.getObjectUseStats(this));
//                }
//            }
//        }
    }

    public static float getArrowVelocity(int charge) {
        float f = (float)charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;

        if (f > 1.0F)
        {
            f = 1.0F;
        }

        return f;
    }

    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }

    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BOW;
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        return BowHandler.onBowRightClick(stack, world, player, hand);
//        boolean flag = this.findAmmo(playerIn) != null;
//
//        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemStackIn, worldIn, playerIn, hand, flag);
//        if (ret != null) return ret;
//
//        if (!playerIn.capabilities.isCreativeMode && !flag) {
//            return !flag ? new ActionResult(EnumActionResult.FAIL, itemStackIn) : new ActionResult(EnumActionResult.PASS, itemStackIn);
//        } else {
//            playerIn.setActiveHand(hand);
//            return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
//        }
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entityLivingBase, int count) {
        if (entityLivingBase instanceof EntityPlayer) {
            BowHandler.onBowUsingTick(stack, (EntityPlayer) entityLivingBase, count);
        }
    }

    //endregion

    //region Render

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRenderer(Feature feature) {
        if (!DEConfig.disable3DModels) {
            modelLocation = new ModelResourceLocation("draconicevolution:" + feature.name(), "inventory");
            ModelLoader.setCustomModelResourceLocation(this, 0, modelLocation);
            ModelRegistryHelper.register(new ModelResourceLocation("draconicevolution:" + feature.name(), "inventory"), new SimpleOverrideBakedModel(new BowModelOverrideList()));
        }
    }

    //endregion

    //region Upgrade & Config


    @Override
    public ItemConfigFieldRegistry getFields(ItemStack stack, ItemConfigFieldRegistry registry) {
        double maxDamage = 2 + (UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.ATTACK_DAMAGE) * 2);
        int maxSpeed = 100 + UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.ARROW_SPEED) * 100;

        registry.register(stack, new DoubleConfigField("bowArrowDamage", 2, 0, maxDamage, "config.field.bowArrowDamage.description", SLIDER));
        registry.register(stack, new IntegerConfigField("bowArrowSpeedModifier", 0, 0, maxSpeed, "config.field.bowArrowSpeedModifier.description", SLIDER).setPrefix("+").setExtension("%"));
        registry.register(stack, new BooleanConfigField("bowAutoFire", false, "config.field.bowAutoFire.description"));
        registry.register(stack, new DoubleConfigField("bowExplosionPower", 0, 0, 4, "config.field.bowExplosionPower.description", SLIDER));
        registry.register(stack, new IntegerConfigField("bowZoomModifier", 0, 0, 300, "config.field.bowZoomModifier.description", SLIDER));

        return super.getFields(stack, registry);
    }

    @Override
    public List<String> getValidUpgrades(ItemStack stack) {
        List<String> list = super.getValidUpgrades(stack);
        list.add(ToolUpgrade.ARROW_DAMAGE);
        list.add(ToolUpgrade.ARROW_SPEED);
        list.add(ToolUpgrade.DRAW_SPEED);
        return list;
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack, String upgrade) {
        return 2;
    }

    @Override
    public boolean checkEnchantTypeValid(EnumEnchantmentType type) {
        return type == EnumEnchantmentType.BOW || type == EnumEnchantmentType.ALL;
    }

    @Override
    public int getToolTier(ItemStack stack) {
        return 0;
    }

    //endregion


}
