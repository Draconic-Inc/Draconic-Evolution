package com.brandon3055.draconicevolution.items.equipment;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.lib.TechItemProps;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.IReaperItem;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.init.EquipCfg;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

import java.util.List;

import static com.brandon3055.draconicevolution.init.ModuleCfg.*;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class ModularBow extends BowItem implements IReaperItem, IModularItem {
    private final TechLevel techLevel;

    public ModularBow(TechItemProps props) {
        super(props);
        this.techLevel = props.techLevel;
    }

    @Override
    public TechLevel getTechLevel() {
        return techLevel;
    }

    @Override
    public ModuleHostImpl createHost(ItemStack stack) {
        return new ModuleHostImpl(techLevel, toolWidth(techLevel), toolHeight(techLevel), "bow", removeInvalidModules);
    }

    @Nullable
    @Override
    public ModularOPStorage createOPStorage(ItemStack stack, ModuleHostImpl host) {
        return new ModularOPStorage(host, EquipCfg.getBaseToolEnergy(techLevel), EquipCfg.getBaseToolTransfer(techLevel));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        addModularItemInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public int getReaperLevel(ItemStack stack) {
        return 0;
    }
}
