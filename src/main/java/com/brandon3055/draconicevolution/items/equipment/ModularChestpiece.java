package com.brandon3055.draconicevolution.items.equipment;

import codechicken.lib.util.SneakyUtils;
import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.brandonscore.lib.TechItemProps;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.api.config.ConfigProperty;
import com.brandon3055.draconicevolution.api.config.DecimalProperty;
import com.brandon3055.draconicevolution.api.modules.ModuleCategory;
import com.brandon3055.draconicevolution.api.modules.ModuleTypes;
import com.brandon3055.draconicevolution.api.modules.data.SpeedData;
import com.brandon3055.draconicevolution.api.modules.lib.ModularOPStorage;
import com.brandon3055.draconicevolution.api.modules.lib.ModuleHostImpl;
import com.brandon3055.draconicevolution.client.model.ModularArmorModel;
import com.brandon3055.draconicevolution.init.EquipCfg;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

import java.util.List;

import static com.brandon3055.draconicevolution.api.config.ConfigProperty.DecimalFormatter.PERCENT_0;
import static com.brandon3055.draconicevolution.api.config.ConfigProperty.DecimalFormatter.PLUS_PERCENT_0;
import static com.brandon3055.draconicevolution.init.ModuleCfg.*;

/**
 * Created by brandon3055 on 21/5/20.
 */
public class ModularChestpiece extends ArmorItem implements IModularArmor {
    private final TechLevel techLevel;

    public ModularChestpiece(TechItemProps props) {
        super(ArmorMaterial.DIAMOND, EquipmentSlotType.CHEST, props);
        this.techLevel = props.techLevel;
    }

    @Override
    public TechLevel getTechLevel() {
        return techLevel;
    }

    @Override
    public ModuleHostImpl createHost(ItemStack stack) {
        ModuleHostImpl host = new ModuleHostImpl(techLevel, chestpieceWidth(techLevel), chestpieceHeight(techLevel), "chestpiece", removeInvalidModules);
        host.addCategories(ModuleCategory.CHESTPIECE);
        host.addPropertyBuilder(props -> {
            SpeedData speed = host.getModuleData(ModuleTypes.SPEED);
            if (speed != null) {
                double maxSpeed = speed.getSpeedMultiplier();
                if (DEConfig.armorSpeedLimit != -1) {
                    maxSpeed = Math.min(maxSpeed, DEConfig.armorSpeedLimit);
                }
                props.add(new DecimalProperty("walk_speed", 0).range(0, maxSpeed).setFormatter(PLUS_PERCENT_0));
                props.add(new DecimalProperty("run_speed", maxSpeed).range(0, maxSpeed).setFormatter(PLUS_PERCENT_0));
            }
        });
        return host;
    }

    @Nullable
    @Override
    public ModularOPStorage createOPStorage(ItemStack stack, ModuleHostImpl host) {
        return new ModularOPStorage(host, EquipCfg.getBaseChestpieceEnergy(techLevel), EquipCfg.getBaseChestpieceTransfer(techLevel));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        addModularItemInformation(stack, worldIn, tooltip, flagIn);
    }

    @OnlyIn(Dist.CLIENT)
    private BipedModel<?> model;

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public <A extends BipedModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, A _default) {
        if (model == null) {
            model = new ModularArmorModel(1F, techLevel);
//            model = new ModelTestArmor(1F); //Armor
//            model = new ModelBiped(0.5F); //Leggings
        }

//        model.leftArmPose = _default.leftArmPose;
//        model.bipedLeftArm.rotateAngleX = _default.bipedLeftArm.rotateAngleX;
//        model.bipedLeftArm.rotateAngleY = _default.bipedLeftArm.rotateAngleY;
//        model.bipedLeftArm.rotateAngleZ = _default.bipedLeftArm.rotateAngleZ;

        return SneakyUtils.unsafeCast(model);
    }
}
