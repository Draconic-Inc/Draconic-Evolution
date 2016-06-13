package com.brandon3055.draconicevolution.common.utills;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;


/**
 * Created by Brandon on 1/09/2014.
 */
public class DamageSourceChaos extends EntityDamageSource {
    public DamageSourceChaos(Entity entity) {
        super("Chaos", entity);
        this.setDamageBypassesArmor();
    }

    @Override
    public IChatComponent func_151519_b(EntityLivingBase par1EntityLivingBase) {
        return super.func_151519_b(par1EntityLivingBase);
        //return new ChatComponentTranslation("death.attack.Chaos", par1EntityLivingBase.func_145748_c_());
    }

    @Override
    public boolean isUnblockable() {
        return true;
    }
}
