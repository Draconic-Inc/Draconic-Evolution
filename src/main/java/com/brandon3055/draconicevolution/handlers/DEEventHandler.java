package com.brandon3055.draconicevolution.handlers;

import codechicken.lib.raytracer.RayTracer;
import com.brandon3055.brandonscore.config.ModFeatureParser;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.ICrystalBinder;
import com.brandon3055.draconicevolution.entity.EntityDragonHeart;
import com.brandon3055.draconicevolution.network.CrystalUpdateBatcher;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.gen.feature.WorldGenEndPodium;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class DEEventHandler {

    public static int serverTicks = 0;
//    @SubscribeEvent
//    public void explodeEvent(ExplosionEvent event) {
////        event.setCanceled(true);
////        LogHelper.dev("Event Canceled");
//    }

    //region Ticking

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            CrystalUpdateBatcher.tickEnd();
            serverTicks++;
        }
    }

    //endregion

    //region C
/*
    @SubscribeEvent
    public void entityJoinWorld(EntityJoinWorldEvent event) {

    }





    Random random = new Random();
    private static Method becomeAngryAt;

    public static double maxSpeed = 10F;
    public static int ticksSinceRequest = 0;
    public static boolean speedNeedsUpdating = true;

    private Field persistenceRequired = null;

    public MinecraftForgeEventHandler() {
        try {
            persistenceRequired = ReflectionHelper.findField(EntityLiving.class, "field_82179_bU", "persistenceRequired");
        }
        catch (Exception e) {
            LogHelper.error("Unable to find field \"persistenceRequired\"");
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntity()Living;

        if (entity.getEntityData().hasKey("SpawnedByDESpawner")) {
            long spawnTime = entity.getEntityData().getLong("SpawnedByDESpawner");
            long livedFor = entity.worldObj.getTotalWorldTime() - spawnTime;

            if (livedFor > 600 && persistenceRequired != null) {
                try {
                    persistenceRequired.setBoolean(entity, false);
                    entity.getEntityData().removeTag("SpawnedByDESpawner");
                }
                catch (Exception e) {
                    LogHelper.warn("Error occured while resetting entity persistence: " + e);
                    entity.getEntityData().removeTag("SpawnedByDESpawner");
                }
            }
        }


        if (!event.getEntity()Living.worldObj.isRemote || !(event.getEntity()Living instanceof EntityPlayerSP)) return;
        EntityPlayerSP player = (EntityPlayerSP) entity;

        double motionX = player.motionX;
        double motionZ = player.motionZ;
        double motion = Math.sqrt((motionX * motionX + motionZ * motionZ));
        double reduction = motion - maxSpeed;

        if (motion > maxSpeed && (player.onGround || player.capabilities.isFlying)) {
            player.motionX -= motionX * reduction;
            player.motionZ -= motionZ * reduction;
        }

        if (speedNeedsUpdating) {
            if (ticksSinceRequest == 0) {
                DraconicEvolution.network.sendToServer(new SpeedRequestPacket());
                LogHelper.info("Requesting speed packet from server");
            }
            ticksSinceRequest++;
            if (ticksSinceRequest > 500) ticksSinceRequest = 0;
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity()Living instanceof EntityPlayer) {
            CustomArmorHandler.onPlayerHurt(event);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity()Living instanceof EntityPlayer) {
            CustomArmorHandler.onPlayerDeath(event);
        }
    }

    @SubscribeEvent
    public void onLivingJumpEvent(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntity()Living instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntity()Living;
        CustomArmorHandler.ArmorSummery summery = new CustomArmorHandler.ArmorSummery().getSummery(player);

        if (summery != null && summery.jumpModifier > 0) {
            player.motionY += (double) (summery.jumpModifier * 0.1F);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntity()Living instanceof EntityPlayer)) return;

        CustomArmorHandler.onPlayerAttacked(event);
    }*/
//endregion

    //region Mob Drops

    List<UUID> deadDragons = new LinkedList<>();
    @SubscribeEvent
    public void onDropEvent(LivingDropsEvent event) {
        if (deadDragons.contains(event.getEntity().getUniqueID())) {
            LogHelper.error("WTF Is Going On!?!?!? The dragon is already dead how can it die again!?!?!");
            LogHelper.error("Whoever is screwing with the dragon you need to fix your shit!");
            LogHelper.error("Offending Entity: " + event.getEntity()+" Class: " + event.getEntity().getClass());
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            LogHelper.error("****************************************");
            for (int i = 2; i < trace.length; i++) {
                LogHelper.error("*  at %s", trace[i].toString());
            }
            LogHelper.error("****************************************");
            event.setCanceled(true);
            return;
        }
        if (!event.getEntity().worldObj.isRemote && ((event.getEntity() instanceof EntityDragon) || (EntityList.getEntityString(event.getEntity()) != null && !EntityList.getEntityString(event.getEntity()).isEmpty() && EntityList.getEntityString(event.getEntity()).equals("HardcoreEnderExpansion.Dragon")))) {
            deadDragons.add(event.getEntity().getUniqueID());
            if (ModFeatureParser.isEnabled(DEFeatures.dragonHeart)) {
                EntityDragonHeart heart = new EntityDragonHeart(event.getEntity().worldObj, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ);
                event.getEntity().worldObj.spawnEntityInWorld(heart);
            }
            DragonFightManager manager = ((EntityDragon)event.getEntity()).getFightManager();
            if (DEConfig.dragonEggSpawnOverride && manager != null && manager.hasPreviouslyKilledDragon()) {
                event.getEntity().worldObj.setBlockState(event.getEntity().worldObj.getHeight(WorldGenEndPodium.END_PODIUM_LOCATION).add(0, 0, -4), Blocks.DRAGON_EGG.getDefaultState());
            }

            if (ModFeatureParser.isEnabled(DEFeatures.draconiumDust) && DEConfig.dragonDustLootModifier > 0) {
                double count = (DEConfig.dragonDustLootModifier * 0.9D) + (event.getEntity().worldObj.rand.nextDouble() * (DEConfig.dragonDustLootModifier * 0.2));
                for (int i = 0; i < (int) count; i++) {
                    float mm = 0.3F;
                    EntityItem item = new EntityItem(event.getEntity().worldObj, event.getEntity().posX - 2 + event.getEntity().worldObj.rand.nextInt(4), event.getEntity().posY - 2 + event.getEntity().worldObj.rand.nextInt(4), event.getEntity().posZ - 2 + event.getEntity().worldObj.rand.nextInt(4), new ItemStack(DEFeatures.draconiumDust));
                    item.motionX = mm * ((((float) event.getEntity().worldObj.rand.nextInt(100)) / 100F) - 0.5F);
                    item.motionY = mm * ((((float) event.getEntity().worldObj.rand.nextInt(100)) / 100F) - 0.5F);
                    item.motionZ = mm * ((((float) event.getEntity().worldObj.rand.nextInt(100)) / 100F) - 0.5F);
                    event.getEntity().worldObj.spawnEntityInWorld(item);
                }
            }
        }
//
//        if (event.getEntity().worldObj.isRemote || !(event.source.damageType.equals("player") || event.source.damageType.equals("arrow")) || !isValidEntity(event.getEntity()Living)) {
//            return;
//        }
//
//        EntityLivingBase entity = event.getEntity()Living;
//        Entity attacker = event.source.getEntity();
//
//        if (attacker == null || !(attacker instanceof EntityPlayer)) {
//            return;
//        }
//
//        int dropChanceModifier = getDropChanceFromItem(((EntityPlayer) attacker).getHeldItem());
//
//        if (dropChanceModifier == 0) return;
//
//        World world = entity.worldObj;
//        int rand = random.nextInt(Math.max(ConfigHandler.soulDropChance / dropChanceModifier, 1));
//        int rand2 = random.nextInt(Math.max(ConfigHandler.passiveSoulDropChance / dropChanceModifier, 1));
//        boolean isAnimal = entity instanceof EntityAnimal;
//
//        if ((rand == 0 && !isAnimal) || (rand2 == 0 && isAnimal)) {
//            ItemStack soul = new ItemStack(ModItems.mobSoul);
//            String registryName = EntityList.getEntityString(entity);
//            ItemNBTHelper.setString(soul, "Name", registryName);
//            if (entity instanceof EntitySkeleton) {
//                ItemNBTHelper.setInteger(soul, "SkeletonType", ((EntitySkeleton) entity).getSkeletonType());
//            }
//            world.spawnEntityInWorld(new EntityItem(world, entity.posX, entity.posY, entity.posZ, soul));
//            Achievements.triggerAchievement((EntityPlayer) attacker, "draconicevolution.soul");
//        }
    }

    //endregion

    //region Crystal Binder

    @SubscribeEvent
    public void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled()) {
            return;
        }

        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();

        //region Hacky check to compensate for the completely f***ing stupid interact event handling.
        //If you cancel the right click event for one hand the event will still fire for the other hand!
        //This check ensures that if the event was cancels by a binder in the other hand the event for this hand will also be canceled.
        //@Forge THIS IS HOW IT SHOULD WORK BY DEFAULT!!!!!!
        ItemStack other = player.getHeldItem(event.getHand() == EnumHand.OFF_HAND ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
        if (stack != null && stack.getItem() instanceof ICrystalBinder && other != null && other.getItem() instanceof ICrystalBinder) {
            if (event.getHand() == EnumHand.OFF_HAND) {
                event.setCanceled(true);
                return;
            }
        }
        else {
            if (event.getHand() == EnumHand.OFF_HAND && other != null && other.getItem() instanceof ICrystalBinder) {
                event.setCanceled(true);
                return;
            }

            if (event.getHand() == EnumHand.MAIN_HAND && other != null && other.getItem() instanceof ICrystalBinder) {
                event.setCanceled(true);
                return;
            }
        }
        //endregion

        if (stack == null || !(stack.getItem() instanceof ICrystalBinder)) {
            return;
        }

        if (BinderHandler.onBinderUse(event.getEntityPlayer(), event.getHand(), event.getWorld(), event.getPos(), stack, event.getFace())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void rightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getWorld().isRemote || event.isCanceled() || !event.getEntityPlayer().isSneaking() || event.getItemStack() == null || !(event.getItemStack().getItem() instanceof ICrystalBinder)) {
            return;
        }

        RayTraceResult traceResult = RayTracer.retrace(event.getEntityPlayer());

        if (traceResult != null && traceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
            return;
        }

        if (BinderHandler.clearBinder(event.getEntityPlayer(), event.getItemStack())) {
            event.setCanceled(true);
        }
    }

    //endregion

    //region C
/*
    private int getDropChanceFromItem(ItemStack stack) {
        int chance = 0;
        if (stack == null) return 0;
        if (stack.getItem().equals(ModItems.wyvernBow) || stack.getItem().equals(ModItems.wyvernSword)) chance++;
        if (stack.getItem().equals(ModItems.draconicSword) || stack.getItem().equals(ModItems.draconicBow)) chance += 2;
        if (stack.getItem().equals(ModItems.draconicDestructionStaff)) chance += 3;

        chance += EnchantmentHelper.getEnchantmentLevel(ConfigHandler.reaperEnchantID, stack);
        return chance;
    }

    private boolean isValidEntity(EntityLivingBase entity) {
        if (entity instanceof IBossDisplayData) {
            return false;
        }
        for (int i = 0; i < ConfigHandler.spawnerList.length; i++) {
            if (ConfigHandler.spawnerList[i].equals(entity.getCommandSenderName()) && ConfigHandler.spawnerListType) {
                return true;
            } else if (ConfigHandler.spawnerList[i].equals(entity.getCommandSenderName()) && !ConfigHandler.spawnerListType) {
                return false;
            }
        }
        if (ConfigHandler.spawnerListType) {
            return false;
        } else {
            return true;
        }
    }

    private boolean changeBlock(ExtendedBlockStorage storage, int x, int y, int z) {

        if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.particleGenerator"))
            return makeChange(storage, x, y, z, ModBlocks.particleGenerator);
        else if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.customSpawner"))
            return makeChange(storage, x, y, z, ModBlocks.customSpawner);
        else if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.generator"))
            return makeChange(storage, x, y, z, ModBlocks.generator);
        else if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.playerDetectorAdvanced"))
            return makeChange(storage, x, y, z, ModBlocks.playerDetectorAdvanced);
        else if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.energyInfuser"))
            return makeChange(storage, x, y, z, ModBlocks.energyInfuser);
        else if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.draconiumOre"))
            return makeChange(storage, x, y, z, ModBlocks.draconiumOre);
        else if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.weatherController"))
            return makeChange(storage, x, y, z, ModBlocks.weatherController);
        else if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.longRangeDislocator"))
            return makeChange(storage, x, y, z, ModBlocks.longRangeDislocator);
        else if (storage.getBlockByExtId(x, y, z).getUnlocalizedName().equals("tile.tile.grinder"))
            return makeChange(storage, x, y, z, ModBlocks.grinder);
        return false;
    }

    private boolean makeChange(ExtendedBlockStorage storage, int x, int y, int z, Block block) {
        if (block == null) return false;
        LogHelper.info("Changing block at [X:" + x + " Y:" + y + " Z:" + z + "] from " + storage.getBlockByExtId(x, y, z).getUnlocalizedName() + " to " + block.getUnlocalizedName());
        storage.func_150818_a(x, y, z, block);
        return true;
    }

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.getEntity() instanceof EntityPlayer && ExtendedPlayer.get((EntityPlayer) event.getEntity()) == null)
            ExtendedPlayer.register((EntityPlayer) event.getEntity());
    }*/
//endregion

    @SubscribeEvent
    public void itemTooltipEvent(ItemTooltipEvent event) {
        if (DEConfig.expensiveDragonRitual && event.getItemStack() != null && event.getItemStack().getItem() == Items.END_CRYSTAL) {
            event.getToolTip().add(TextFormatting.DARK_GRAY + "Recipe tweaked by Draconic Evolution.");
        }

//        ItemStack stack = event.getItemStack();
//        if (stack != null) {
//            int[] ids = OreDictionary.getOreIDs(stack);
//
//            event.getToolTip().add("Is Block: " + (stack.getItem() instanceof ItemBlock));
//            event.getToolTip().add(stack.getItem().getRegistryName() + "");
//            LogHelper.info(Item.REGISTRY.getObject(new ResourceLocation("dragonmounts:dragon_egg")));
//            LogHelper.info(Block.REGISTRY.getObject(new ResourceLocation("dragonmounts:dragon_egg")));
//            LogHelper.info(stack.getItem());
//            for (int id : ids) {
//                event.getToolTip().add(OreDictionary.getOreName(id));
//            }
//        }

//        if (DEConfig.showUnlocalizedNames) event.toolTip.add(event.itemStack.getUnlocalizedName());
//        if (DraconicEvolution.debug && event.itemStack.hasTagCompound()) {
//            String s = event.itemStack.getTagCompound().toString();
//            int escape = 0;
//            while (s.contains(",")) {
//                event.toolTip.add(s.substring(0, s.indexOf(",") + 1));
//                s = s.substring(s.indexOf(",") + 1, s.length());
//
//                if (escape++ >= 100) break;
//            }
//            event.toolTip.add(s);
//        }
    }

    //region C
/*
    @SubscribeEvent
    public void stopUsingEvent(PlayerUseItemEvent.Start event) {
        if (!ConfigHandler.pigmenBloodRage || event.item == null || event.item.getItem() == null) return;
        if (event.item.getItem() == Items.porkchop || event.item.getItem() == Items.cooked_porkchop) {
            World world = event.getEntity()Player.worldObj;
            if (world.isRemote) return;
            EntityPlayer player = event.getEntity()Player;
            List list = world.getEntitiesWithinAABB(EntityPigZombie.class, AxisAlignedBB.getBoundingBox(player.posX - 32, player.posY - 32, player.posZ - 32, player.posX + 32, player.posY + 32, player.posZ + 32));

            EntityZombie entityAtPlayer = new EntityPigZombie(world);
            entityAtPlayer.setPosition(player.posX, player.posY, player.posZ);

            boolean flag = false;

            for (Object o : list) {
                if (o instanceof EntityPigZombie) {
                    EntityPigZombie zombie = (EntityPigZombie) o;
                    if (becomeAngryAt == null) {
                        becomeAngryAt = ReflectionHelper.findMethod(EntityPigZombie.class, zombie, new String[]{"becomeAngryAt", "func_70835_c"}, Entity.class);
                        becomeAngryAt.setAccessible(true);
                    }

                    try {
                        becomeAngryAt.invoke(zombie, player);
                    }
                    catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                    if (Math.abs(zombie.posX - player.posX) < 14 && Math.abs(zombie.posY - player.posY) < 14 && Math.abs(zombie.posZ - player.posZ) < 14)
                        flag = true;
                    zombie.addPotionEffect(new PotionEffect(5, 10000, 3));
                    zombie.addPotionEffect(new PotionEffect(11, 10000, 2));
                }
            }

            if (flag) player.addPotionEffect(new PotionEffect(2, 500, 3));
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void joinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayerSP) {
            speedNeedsUpdating = true;
            DraconicEvolution.network.sendToServer(new MountUpdatePacket(0));
        }
    }
*/
//endregion

    @SubscribeEvent(priority = EventPriority.LOW)
    public void getBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.getEntityPlayer() != null) {
            float newDigSpeed = event.getOriginalSpeed();
            CustomArmorHandler.ArmorSummery summery = new CustomArmorHandler.ArmorSummery().getSummery(event.getEntityPlayer());
            if (summery == null){
                return;
            }

            if (event.getEntityPlayer().isInsideOfMaterial(Material.WATER)) {
                if (summery.armorStacks[3] != null && summery.armorStacks[3].getItem() == DEFeatures.draconicHelm) {
                    newDigSpeed *= 5f;
                }
            }

            if (!event.getEntityPlayer().onGround) {
                if (summery.armorStacks[2] != null && summery.armorStacks[2].getItem() == DEFeatures.draconicChest) {
                    newDigSpeed *= 5f;
                }
            }

            if (newDigSpeed != event.getOriginalSpeed()) {
                event.setNewSpeed(newDigSpeed);
            }
        }
    }

    //region C
    /*
    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload e) {
        TileGrinder.fakePlayer = null;
    }

    @SubscribeEvent
    public void playerInteract(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            ForgeDirection face = ForgeDirection.getOrientation(event.face);
            int x = event.x + face.offsetX;
            int y = event.y + face.offsetY;
            int z = event.z + face.offsetZ;
            if (event.world.getBlock(x, y, z) == ModBlocks.safetyFlame) {
                event.world.setBlockToAir(x, y, z);
                event.world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.fizz", 1F, event.world.rand.nextFloat() * 0.1F + 2F);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void entityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.world.isRemote && event.getEntity() instanceof EntityEnderCrystal && event.getEntity().dimension == 1) {
            DataUtills.XZPair<Integer, Integer> location = ChaosWorldGenHandler.getClosestChaosSpawn((int) event.getEntity().posX / 16, (int) event.getEntity().posZ / 16);
            if ((location.x != 0 || location.z != 0) && Utills.getDistanceAtoB(event.getEntity().posX, event.getEntity().posZ, location.x, location.z) < 500) {
                ProcessHandler.addProcess(new ChaosWorldGenHandler.CrystalRemover(event.getEntity()));
            }
        }
    }*/
    //endregion

    @SubscribeEvent
    public void login(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.player.onGround) {
            CustomArmorHandler.ArmorSummery summery = new CustomArmorHandler.ArmorSummery().getSummery(event.player);
            if (summery != null && summery.flight[0]) {
                event.player.capabilities.isFlying = true;
                event.player.sendPlayerAbilities();
            }
        }
    }
}
