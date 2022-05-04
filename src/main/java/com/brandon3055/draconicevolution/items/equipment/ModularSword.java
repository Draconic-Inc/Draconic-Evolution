package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.lib.TechPropBuilder;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.api.damage.IDraconicDamage;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.init.EquipCfg;
import com.brandon3055.draconicevolution.init.ModuleCfg;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class ModularSword extends SwordItem implements IReaperItem, IModularMelee, IDraconicDamage {
    private final TechLevel techLevel;
    private final DEItemTier itemTier;

    public ModularSword(TechPropBuilder props) {
        super(new DEItemTier(props, EquipCfg::getSwordDmgMult, EquipCfg::getSwordSpeedMult), 0, 0, props.build().fireResistant());
        this.techLevel = props.techLevel;
        this.itemTier = (DEItemTier) getTier();
    }

    @Override
    public TechLevel getTechLevel() {
        return techLevel;
    }

    @Override
    public DEItemTier getItemTier() {
        return itemTier;
    }

    @Override
    public TechLevel getTechLevel(@Nullable ItemStack stack) {
        return techLevel;
    }

    @Override
    public ModuleHostImpl createHost(ItemStack stack) {
        ModuleHostImpl host = new ModuleHostImpl(techLevel, ModuleCfg.toolWidth(techLevel), ModuleCfg.toolHeight(techLevel), "sword", ModuleCfg.removeInvalidModules);
        return host;
    }

    @Nullable
    @Override
    public ModularOPStorage createOPStorage(ItemStack stack, ModuleHostImpl host) {
        return new ModularOPStorage(host, EquipCfg.getBaseToolEnergy(techLevel), EquipCfg.getBaseToolTransfer(techLevel));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        addModularItemInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public int getReaperLevel(ItemStack stack) {
        return techLevel.index;
    }

//    @Override
//    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand handIn) {
//        if (!worldIn.isRemote && player.isSneaking()) {
//            for (double x = 0; x < 15; x += 0.5) {
//                for (double z = 0; z < 15; z += 0.5) {
//                    CreeperEntity creeper = EntityType.CREEPER.create(worldIn);
//                    creeper.setNoAI(true);
//                    creeper.setInvulnerable(true);
//                    creeper.setPosition(player.getPosX() + x, player.getPosY(), player.getPosZ() + z);
//                    creeper.getAttribute(Attributes.MAX_HEALTH).applyNonPersistentModifier(new AttributeModifier("Health Boost", 10000, AttributeModifier.Operation.MULTIPLY_TOTAL));
//                    creeper.setHealth(Float.MAX_VALUE);
//                    worldIn.addEntity(creeper);
//                }
//            }
//        }
//
//        return super.onItemRightClick(worldIn, player, handIn);
//    }
}
