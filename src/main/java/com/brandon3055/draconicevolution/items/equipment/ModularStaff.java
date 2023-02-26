package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.api.capability.DECapabilities;
import com.brandon3055.draconicevolution.api.capability.ModuleHost;
import com.brandon3055.draconicevolution.api.damage.IDraconicDamage;
import com.brandon3055.draconicevolution.api.modules.Module;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.DamageModData;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleEntity;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.init.DEModules;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.init.ModuleCfg;
import com.brandon3055.draconicevolution.init.TechProperties;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class ModularStaff extends DiggerItem implements IReaperItem, IModularMiningTool, IModularMelee, IDraconicDamage {
    private final TechLevel techLevel;
    private final DETier itemTier;

    public ModularStaff(DETier tier, TechProperties props) {
        super(0, 0, tier, BlockTags.MINEABLE_WITH_PICKAXE, props);
        this.techLevel = props.getTechLevel();
        this.itemTier = (DETier) getTier();
    }

    @Override
    public TechLevel getTechLevel() {
        return techLevel;
    }

    @Override
    public DETier getItemTier() {
        return itemTier;
    }

    @Override
    public TechLevel getTechLevel(@Nullable ItemStack stack) {
        return techLevel;
    }

    @Override
    public double getSwingSpeedMultiplier() {
        return EquipCfg.staffSwingSpeedMultiplier;
    }

    @Override
    public double getDamageMultiplier() {
        return EquipCfg.staffDamageMultiplier;
    }

    @Override
    public ModuleHostImpl createHost(ItemStack stack) {
        ModuleHostImpl host = new ModuleHostImpl(techLevel, ModuleCfg.staffWidth(techLevel), ModuleCfg.staffHeight(techLevel), "staff", ModuleCfg.removeInvalidModules);
//        host.addCategories(ModuleCategory.RANGED_WEAPON);
//        host.addAdditionalType(ModuleTypes.DAMAGE_MOD);
        host.addCategories(ModuleCategory.TOOL_AXE);
        host.addCategories(ModuleCategory.TOOL_HOE);
        host.addCategories(ModuleCategory.TOOL_SHOVEL);

        return host;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public ModularOPStorage createOPStorage(ItemStack stack, ModuleHostImpl host) {
        return new ModularOPStorage(host, EquipCfg.getBaseStaffEnergy(techLevel), EquipCfg.getBaseStaffTransfer(techLevel));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return IModularMiningTool.super.getDestroySpeed(stack, state);
    }

    @Override
    public float getBaseEfficiency() {
        return getTier().getSpeed() * EquipCfg.getStaffEffMult();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        addModularItemInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public int getReaperLevel(ItemStack stack) {
        return techLevel.index + 1;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.category == EnchantmentCategory.DIGGER ||
                enchantment.category == EnchantmentCategory.WEAPON ||
                enchantment.category.name().equals("PICKAXE_OR_SHOVEL") ||
                enchantment.category.name().equals("SWORD_OR_AXE") ||
                enchantment.category.name().equals("SWORD_OR_AXE_OR_CROSSBOW") ||
                super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return damageBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return damageBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return damageBarColour(stack);
    }

    @Override
    public boolean canBeHurtBy(DamageSource source) {
        return source == DamageSource.OUT_OF_WORLD;
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.getAge() >= 0) {
            entity.setExtendedLifetime();
        }
        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public boolean isEnchantable(ItemStack p_41456_) {
        return true;
    }

    //Projectile Attack Handling

    //    Ok so here is the plan:
//
//            There will be an interface on the tool that can be used to specify things like the type of projectile
//            and the damage mod if there is one. As well as ether apply modifiers to the projectile when fired
//            or the ability to specify things like no gravity and no drag.
//            There will also be a method for retriving the damage mod.
//
//    Most of the damage customization will be handled entierly by the arrow itself. Infact a possible plan B would be to
//    get rid of the projectile handler, I mean i'm really just offloading simple logic at this point.
//    I can just plug all of the requried data into the projectile entity and let it fly!
//    Possibly add anotehr subclass layer to the projectile that can more cleanely handle all the custom damage stuff?

    //Todo going to look into projectile stuff later. For now i have run into a bit of a creative block.
    // When i come back to this i think i might abandon the modular projectile idea and do something "simpler"
    // idk maybe some kinda simple "uber laser death ray" type thing xD
    // I also want to take a crack at rendering the beam directly from the tool renderer. To look good it would require the staff orientation to align perfectly with where the player is looking which may be tricky.
    // There is also the issue of render culling to deal with but will focus on one issue at a time...
//    @Override
//    public int getUseDuration(ItemStack stack) {
//        return 72000;
//    }

//    @Override
//    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
//        ItemStack stack = player.getItemInHand(hand);
//        boolean canFire = canFire(stack, player);
//        if (!canFire) {
//            return ActionResult.fail(stack);
//        }
//        //The staff does not exactly fire an "Arrow" but i am treating the projectile as an arrow in code for better compatibility.
//        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(stack, world, player, hand, true);
//        if (ret != null) {
//            return ret;
//        }
//        player.startUsingItem(hand);
//        return ActionResult.consume(stack);
//    }

//    @Override
//    public void releaseUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft) {
//        if (!(entity instanceof PlayerEntity)) {
//            return;
//        }
//
//        PlayerEntity player = (PlayerEntity) entity;
//        boolean canFire = canFire(stack, player);
//        if (!canFire) {
//            return;
//        }
//
//        int chargeTime = stack.getUseDuration() - timeLeft;
//
//        //The staff does not exactly fire an "Arrow" but i am treating the projectile as an arrow in code for better compatibility.
//        chargeTime = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, world, player, chargeTime, true);
//        if (chargeTime < 0 || world.isClientSide) {
//            return;
//        }
//
//        Module<DamageModData> damageMod = getDamageModule(stack);
//        if (damageMod == null && chargeTime < 10) {
////            return;
//        }
//
//        DraconicProjectileEntity projectile = new DraconicProjectileEntity(world, player);
//        projectile.setArrowProjectile(false);
//        projectile.setNoDrag(true);
//        projectile.setNoGravity(true);
//
//        if (damageMod != null) { //Apply Damage Mod
//            IDamageModifier modifier = damageMod.getData().getModifier();
//            //Will change this to a single secondary charge state but there will be a 10 or so tick delay before it starts charging then it will
//            //charge pretty quick
//            int chargeState = chargeTime >= 40 ? 2 : chargeTime >= 20 ? 1 : 0;
//            float speed = modifier.getType() == IDamageModifier.EffectType.LIGHTNING ? 100F : 6F;
//            projectile.shootFromRotation(player, player.xRot, player.yRot, 0.0F, speed, 0.25F); //May be modified by damage mod
//        } else {
//            projectile.shootFromRotation(player, player.xRot, player.yRot, 0.0F, 100F, 0.5F);
//            projectile.useDefaultStaffModifier();
//            projectile.setMaxFlightTime(5);
//        }
//
//        world.addFreshEntity(projectile);
//    }

    public static float getRenderChargeState(float chargeTime) {
        if (chargeTime >= 40) {
            return Math.min(1F, 0.5F + (chargeTime - 40F) / 5F);
        } else if (chargeTime >= 20) {
            return Math.min(0.5F, (chargeTime - 20F) / 5F);
        }
        return 0F;
    }

    public static float getDrawPower(float drawProgress) {
        drawProgress = (drawProgress * drawProgress + drawProgress * 2.0F) / 3.0F;
        return Math.min(drawProgress, 1F);
    }

    private boolean canFire(ItemStack stack, Player player) {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static Module<DamageModData> getDamageModule(ItemStack stack) {
        ModuleHost host = stack.getCapability(DECapabilities.MODULE_HOST_CAPABILITY).orElseThrow(IllegalStateException::new);
        ModuleEntity entity = host.getEntitiesByType(ModuleTypes.DAMAGE_MOD).findAny().orElse(null);
        return entity != null && entity.getModule().getData() instanceof DamageModData ? (Module<DamageModData>) entity.getModule() : null;
    }
}