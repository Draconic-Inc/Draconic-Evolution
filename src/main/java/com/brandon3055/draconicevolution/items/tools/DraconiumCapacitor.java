package com.brandon3055.draconicevolution.items.tools;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.items.ItemEnergyBase;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.TechItemProps;
import com.brandon3055.brandonscore.utils.EnergyUtils;
import com.brandon3055.brandonscore.utils.InfoHelper;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.api.IInvCharge;
import com.brandon3055.draconicevolution.api.itemupgrade_dep.IUpgradableItem;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.integration.BaublesHelper;
import com.brandon3055.draconicevolution.integration.ModHelper;
import com.brandon3055.draconicevolution.items.ToolUpgrade;
import com.brandon3055.draconicevolution.items.tools.old.ToolBase;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brandon3055 on 31/05/2016.
 */
//@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class DraconiumCapacitor extends ItemEnergyBase implements IInvCharge, IUpgradableItem {

    public static final int wyvernTransfer = 8000000;
    public static final int draconicTransfer = 64000000;
    private TechLevel techLevel;

    public DraconiumCapacitor(TechItemProps properties) {
        super(properties);
        techLevel = properties.techLevel;
    }



//    public DraconiumCapacitor() {
//        this.setHasSubtypes(true);
//        this.addName(0, "wyvern").addName(1, "draconic").addName(2, "creative");
//        this.setMaxStackSize(1);
//    }

    //region Item

//    @Override
//    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
//        if (isInCreativeTab(tab)) {
//            subItems.add(new ItemStack(DEFeatures.draconiumCapacitor, 1, 0));
//            ItemStack wyvernCharged = new ItemStack(DEFeatures.draconiumCapacitor, 1, 0);
//            setEnergy(wyvernCharged, getCapacity(wyvernCharged));
//            subItems.add(wyvernCharged);
//
//            ItemStack uberWyvern = new ItemStack(DEFeatures.draconiumCapacitor, 1, 0);
//            for (String upgrade : getValidUpgrades(uberWyvern)) {
//                UpgradeHelper.setUpgradeLevel(uberWyvern, upgrade, getMaxUpgradeLevel(uberWyvern, upgrade));
//            }
//            setEnergy(uberWyvern, getCapacity(uberWyvern));
//            subItems.add(uberWyvern);
//
//            subItems.add(new ItemStack(DEFeatures.draconiumCapacitor, 1, 1));
//            ItemStack draconicCharged = new ItemStack(DEFeatures.draconiumCapacitor, 1, 1);
//            setEnergy(draconicCharged, getCapacity(draconicCharged));
//            subItems.add(draconicCharged);
//
//            ItemStack uberDraconic = new ItemStack(DEFeatures.draconiumCapacitor, 1, 1);
//            for (String upgrade : getValidUpgrades(uberDraconic)) {
//                UpgradeHelper.setUpgradeLevel(uberDraconic, upgrade, getMaxUpgradeLevel(uberDraconic, upgrade));
//            }
//            setEnergy(uberDraconic, getCapacity(uberDraconic));
//            subItems.add(uberDraconic);
//
//            subItems.add(new ItemStack(DEFeatures.draconiumCapacitor, 1, 2));
//        }
//    }

//    @Override
//    public boolean hasCustomEntity(ItemStack stack) {
//        return true;
//    }
//
//    @Override
//    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
//        return new EntityPersistentItem(world, location, itemstack);
//    }

    //endregion

    //region Energy

//    @Override
//    public long getCapacity(ItemStack stack) {
//        int tier = stack.getItemDamage();
//
//        int upgrade = UpgradeHelper.getUpgradeLevel(stack, ToolUpgrade.RF_CAPACITY);
//
//        switch (tier) {
//            case 0:
//                return DEConfig.wyvernFluxCapBaseCap + (upgrade * (DEConfig.wyvernFluxCapBaseCap / 2));
//            case 1:
//                return DEConfig.draconicFluxCapBaseCap + (upgrade * (DEConfig.draconicFluxCapBaseCap / 2));
//            case 2:
//                return Long.MAX_VALUE;
//        }
//
//        return 0;
//    }

    @Override
    public long getMaxReceive(ItemStack stack) {
        if (this == DEContent.capacitor_creative) {
            return Long.MAX_VALUE;
        }
//        int tier = stack.getItemDamage();
//
//        switch (tier) {
//            case 0:
//                return wyvernTransfer;
//            case 1:
//                return draconicTransfer;
//            case 2:
//                return Integer.MAX_VALUE;
//        }

        return 0;
    }

    @Override
    public long getMaxExtract(ItemStack stack) {
        if (this == DEContent.capacitor_creative) {
            return Long.MAX_VALUE;
        }

        switch (techLevel) {

            case DRACONIUM:
                break;
            case WYVERN:
                return wyvernTransfer;
            case DRACONIC:
                return draconicTransfer;
            case CHAOTIC:
                break;
        }

        return 0;
    }
//
    @Override
    public long getEnergyStored(ItemStack stack, boolean isOPAsking) {
        if (this == DEContent.capacitor_creative) {
            return isOPAsking ? Long.MAX_VALUE / 2 : Integer.MAX_VALUE / 2;
        }
        return super.getEnergyStored(stack, isOPAsking);
    }
//
//    @Override
//    public long receiveEnergy(ItemStack stack, long maxReceive, boolean simulate) {
//        if (stack.getItemDamage() == 2) {
//            return maxReceive;
//        }
//
//        return super.receiveEnergy(stack, maxReceive, simulate);
//    }
//
//    @Override
//    public long extractEnergy(ItemStack container, long maxExtract, boolean simulate) {
//        if (container.getItemDamage() == 2) {
//            return maxExtract;
//        }
//
//        return super.extractEnergy(container, maxExtract, simulate);
//    }

    //endregion

    //region Activation

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            int mode = ItemNBTHelper.getShort(stack, "Mode", (short) 0);
            int newMode = mode == 4 ? 0 : mode + 1;
            ItemNBTHelper.setShort(stack, "Mode", (short) newMode);
            if (world.isRemote) {
                ChatHelper.indexedMsg(player, InfoHelper.ITC() + I18n.format("info.de.capacitorMode.txt") + ": " + InfoHelper.HITC() + I18n.format("info.de.capacitorMode" + ItemNBTHelper.getShort(stack, "Mode", (short) 0) + ".txt"));
            }
        }
        return new ActionResult<>(ActionResultType.PASS, stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entity, int itemSlot, boolean isSelected) {
        if (!(entity instanceof PlayerEntity)) {
            return;
        }
        if (ModHelper.isBaublesInstalled) {
            updateEnergy(stack, (PlayerEntity) entity, getBaubles((PlayerEntity) entity));
        }
        else {
            updateEnergy(stack, (PlayerEntity) entity, new ArrayList<>());
        }
    }

    public void updateEnergy(ItemStack capacitor, PlayerEntity player, List<ItemStack> stacks) {
        int mode = ItemNBTHelper.getShort(capacitor, "Mode", (short) 0);

        if (mode == 0) {
            return;
        }

        if (mode == 4) { //Charge All
            stacks.addAll(player.inventory.armorInventory);
            stacks.addAll(player.inventory.mainInventory);
            stacks.addAll(player.inventory.offHandInventory);
        }
        else {
            if (mode == 1 || mode == 3) { //Charge Armor
                stacks.addAll(player.inventory.armorInventory);
            }
            else {
                stacks.clear(); // Dont charge baubles
            }
            if (mode == 2 || mode == 3) { //Charge Held Items
                stacks.add(player.getHeldItemOffhand());
                stacks.add(player.getHeldItemMainhand());
            }
        }

        for (ItemStack stack : stacks) {
            long max = Math.min(getEnergyStored(capacitor, true), getMaxExtract(capacitor));

            if (EnergyUtils.canReceiveEnergy(stack)) {
                Item item = stack.getItem();

                if (item instanceof IInvCharge && !((IInvCharge) item).canCharge(stack, player)) {
                    continue;
                }

                extractEnergy(capacitor, EnergyUtils.insertEnergy(stack, max, false), false);
            }
        }
    }

    @Override
    public boolean canCharge(ItemStack stack, PlayerEntity player) {
        return false;
    }

    //endregion

    //region Display

    @Override
    public boolean hasEffect(ItemStack stack) {
        return ItemNBTHelper.getShort(stack, "Mode", (short) 0) > 0;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (InfoHelper.holdShiftForDetails(tooltip)) {
            tooltip.add(new TranslationTextComponent("info.de.changwMode.txt"));
            tooltip.add(new TranslationTextComponent(InfoHelper.ITC() + I18n.format("info.de.capacitorMode.txt") + ": " + InfoHelper.HITC() + I18n.format("info.de.capacitorMode" + ItemNBTHelper.getShort(stack, "Mode", (short) 0) + ".txt")));
            //InfoHelper.addLore(stack, tooltip);
        }

        ToolBase.holdCTRLForUpgrades(tooltip, stack);
        InfoHelper.addEnergyInfo(stack, tooltip);
//        if (stack.getItemDamage() == 2) {
//            tooltip.add(InfoHelper.HITC() + I18n.format("info.creativeCapacitor.txt") + " " + Utils.formatNumber(Integer.MAX_VALUE / 2) + " RF/t");
//        }
    }

    //endregion

    //region IUpgradable

    @Override
    public List<String> getValidUpgrades(ItemStack stack) {
        return new ArrayList<String>() {{
            add(ToolUpgrade.RF_CAPACITY);
        }};
    }

    @Override
    public int getMaxUpgradeLevel(ItemStack stack, String upgrade) {
//        return stack.getItemDamage() == 0 ? 3 : stack.getItemDamage() == 1 ? 4 : 0;
        return 3;
    }
//
//    //endregion
//
//    @Override
//    @Optional.Method(modid = "baubles")
//    public BaubleType getBaubleType(ItemStack itemstack) {
//        return BaubleType.TRINKET;
//    }
//
//    @Override
//    @Optional.Method(modid = "baubles")
//    public void onWornTick(ItemStack itemstack, LivingEntity player) {
//        if (!(player instanceof PlayerEntity)) return;
//        updateEnergy(itemstack, (PlayerEntity) player, getBaubles((PlayerEntity) player));
//    }

    private static List<ItemStack> getBaubles(PlayerEntity entity) {
        return BaublesHelper.getBaubles(entity);
    }
}
