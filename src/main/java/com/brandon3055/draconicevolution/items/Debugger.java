package com.brandon3055.draconicevolution.items;

import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.lib.Vec3I;
import com.brandon3055.brandonscore.utils.DataUtils;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.entity.EntityChaosGuardian;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 4/4/2016.
 */
public class Debugger extends ItemBCore {

    private static final Map<Integer, String> MODES = new HashMap<Integer, String>();

    static {
        MODES.put(0, "Complete Crafting");
        MODES.put(1, "Insert RF");
        MODES.put(2, "Extract RF");
        MODES.put(3, "List Particles");
        MODES.put(4, "Recipe");
        MODES.put(5, "Clear");
        MODES.put(6, "Mod Wiki");
        MODES.put(7, "Explode");
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        World world = player.world;
        EntityChaosGuardian guardian = DataUtils.firstMatch(world.getEntitiesWithinAABB(EntityChaosGuardian.class, player.getEntityBoundingBox().grow(300)), entityChaosGuardian -> true);
        if (guardian != null) {
            guardian.removePassengers();
            if (entity instanceof EntityCreeper) {
                ((EntityCreeper) entity).enablePersistence();
            }
            entity.startRiding(guardian, true);
            LogHelper.dev("Success");
        }


        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {

        if (!worldIn.isRemote && ClientEventHandler.elapsedTicks % 500 == 0) {
//            EntityPlayerMP player = (EntityPlayerMP) entityIn;
//
//            for (StatBase stat : StatList.ALL_STATS) {
//                int value = player.getStatFile().readStat(stat);
//                if (value != 0) {
//                    LogHelper.dev(stat.statId + ": " + value);
//                }
//            }


////            DEShaders.initReactorShader();
//            DEShaders.initReactorShieldShader();
//            DEShaders.initEnergyCrystalShader();
//            DEShaders.initReactorBeams();
//            DEShaders.initExplosionOverlay();
//            DEShaders.initExplosionWave();


//            int n = 3;
//            int s = 2;
//            int e = 1;
//            int w = 1;
//
//            LogHelper.dev(n+" "+s+" "+e+" "+w);
//
//            byte b = (byte) ((n & 3) << 6 | (s & 3) << 4 | (e & 3) << 2 | (w & 3));
//
//            b = (byte) ((b & 0xCF) | 2 << 4);
//            //0x3F << 6
//            //0xCF << 4
//            //0xF3 << 3
//            //0xFC
//
//            n = (b >> 6) & 3;
//            s = (b >> 4) & 3;
//            e = (b >> 2) & 3;
//            w = b & 3;
//
//            LogHelper.dev(n+" "+s+" "+e+" "+w);
//            LogHelper.dev(Integer.toBinaryString(Byte.toUnsignedInt(b)));
        }

    }

    //region Item Junk

    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack) {
        return super.getNBTShareTag(stack);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

//    private

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        //if (!world.isRemote) {
        double posX = player.posX - (player.posX % 16) + 8;
        double posZ = player.posZ - (player.posZ % 16) + 8;

//            player.setPosition(posX, player.posY, posZ);

//            for (int d = 1; d <= 30; d++) {
//
//                int totalDays = 30;
//                int day = d;
//                int target = 20;
//                int r = target - day;
////            Result=13
////
//                LogHelper.dev(r + (r <= 0 ? totalDays : 0));
//            }

//            30 - 7 = 23


//            for (int i = 0; i < 1; i++) {
//                //This is the corrected input so x and z are offset by + 2048 and range from 0 to 4096
//
//                ShortPos pos = new ShortPos(new BlockPos(itemRand.nextInt(10000) - 5000, itemRand.nextInt(128), itemRand.nextInt(10000) - 5000));
//                int xIn = pos.getRelativeTo().getX() + (itemRand.nextInt(4096) - 2048);
//                int yIn = pos.getRelativeTo().getY() + itemRand.nextInt(128);
//                int zIn = pos.getRelativeTo().getZ() + (itemRand.nextInt(4096) - 2048);
//
//                // Y - 8 bit    X - 12 bit     Z - 12 bit
//                //[11111111] [11111111 1111] [1111 11111111]
//                int posInt = (yIn << 24) | (xIn << 12) | (zIn);
//
//                int yOut = posInt >> 24 & 0xFF;
//                int xOut = posInt >> 12 & 0xFFF;
//                int zOut = posInt & 0xFFF;
//
//                boolean match = xIn == xOut && yIn == yOut && zIn == zOut;
//
//                if (!match) {
//                   // LogHelper.error("Match Failed! " + xIn + " " + yIn + " " + zIn + " -> " + xOut + " " + yOut + " " + zOut);
//                }
//
//
//
//                BlockPos posIn = new BlockPos(xIn, yIn, zIn);
//                int iPos = pos.getIntPos(posIn);
//                if (!posIn.equals(pos.getActualPos(iPos))) {
//                    LogHelper.dev("MissMatch " + posIn + " " + pos.getActualPos(iPos));
//                }
//
//            }
//            LogHelper.dev("Done");


        //}

//        if (!world.isRemote) {
//            PacketCustom packet = new PacketCustom("DE", 1);
//            LogHelper.dev("Writing Test Data...");
//            for (int i = 0; i < 20; i++) {
//                packet.writeString("Test String "+i+"0000000000 " + i * 432 + " 1111111111");
//            }
//            LogHelper.dev("Raw Packet Size: " + packet.readableBytes() +" Array Size: " + packet.array().length);
//            packet.compress();
//            LogHelper.dev("Sent Size: " + packet.toPacket().payload().readableBytes());
//            packet.sendToPlayer(player);
//
////            DraconicEvolution.network.sendTo(new PacketCompressionTest(), (EntityPlayerMP) player);
//        }


//        if (!world.isRemote) {
//            List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().expand(10, 10, 10));
//            for (Entity entity : list) {
//                if (entity.isRiding()) {
//                    LogHelper.dev(entity+" "+entity.isRiding()+" "+entity.getRidingEntity());
//                    continue;
//                }
//
//                Entity e;
//                for (e = player; e.getPassengers().size() > 0; e = e.getPassengers().get(0)) {
//                    LogHelper.dev("Passengers: " + e.getPassengers());
//                }
//                entity.startRiding(e, true);
//                ((EntityPlayerMP) player).connection.sendPacket(new SPacketSetPassengers(e));
//                break;
//            }
//
//        }
//
//
//        if (world.isRemote) {
//            for (int i = 0; i < 50; i++) {
//                double rf = i * 100;
//                double d = rf / (10000 + rf);
//                LogHelper.dev(Utils.round(d, 100) + " " + rf);
//            }
//        }
//
//        if (true) {
//            return new ActionResult(EnumActionResult.FAIL, itemStack);
//        }
//
//        if (world.isRemote) {
//
//            Map<String, List<String>> variantValueMap = new LinkedHashMap<>();
//            variantValueMap.put("type", new ArrayList<String>(){{add("alloyfurnace");}});
//            variantValueMap.put("active", new ArrayList<String>(){{add("true"); add("false");}});
//            variantValueMap.put("facinghoz", new ArrayList<String>(){{add("north"); add("south"); add("east"); add("west");}});
//
//
//            LinkedList<String> possibleCombos = new LinkedList<>();
//            List<String> keys = Lists.newArrayList(variantValueMap.keySet());
//
//            int comboCount = 1;
//            for (String key : variantValueMap.keySet()) {
//                comboCount *= variantValueMap.get(key).size();
//            }
//
//            int[] indexes = new int[variantValueMap.size()];
//            for (int l = 0; l < comboCount; l++) {
//                for (int in = 0; in < indexes.length; in++) {
//                    indexes[in]++;
//                    if (indexes[in] >= variantValueMap.get(keys.get(in)).size()) {
//                        indexes[in] = 0;
//                    }
//                    else {
//                        break;
//                    }
//                }
//
//                String combo = "";
//                for (int i = 0; i < indexes.length; i++) {
//                    combo += keys.get(i) + "=" + variantValueMap.get(keys.get(i)).get(indexes[i]) + ",";
//                }
//                possibleCombos.add(combo.substring(0, combo.length() - 1));
//            }
//
//            for (String s : possibleCombos) LogHelper.info(s);
//        }


//      LogHelper.info(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "draconicevolution:creativeSource"));

//        if (!world.isRemote ) {
//            MassBlockModificationThread plThread = new MassBlockModificationThread(world);
//
//            int range = 10;
//
//            Iterable<BlockPos> blocks = BlockPos.getAllInBox(new BlockPos(player.posX - range, 0, player.posZ - range), new BlockPos(player.posX + range, 128, player.posZ + range));
//
//            for (BlockPos pos : blocks) {
//                plThread.setBlock(pos, Blocks.AIR.getDefaultState());
//            }
//
//            plThread.start();
//        }
//
//
//        if (true) {
//            return new ActionResult(EnumActionResult.FAIL, itemStack);
//        }

//        if (!world.isRemote && world instanceof WorldServer) {
//            BlockPlacementBatcher batcher = new BlockPlacementBatcher((WorldServer)world);
//
//            int range = 100;
//
//            Iterable<BlockPos> blocks = BlockPos.getAllInBox(new BlockPos(player.posX - range, 0, player.posZ - range), new BlockPos(player.posX + range, 128, player.posZ + range));
//
//            for (BlockPos pos : blocks) {
//
//                if (world.rand.nextBoolean()) {
//                    batcher.setBlockState(pos, Blocks.AIR.getDefaultState());
//                }
//                else {
//                    batcher.setBlockState(pos, Blocks.STONE.getDefaultState());
//                }
//
//            }
//
////            batcher.finish();
////            SPacketChunkData data = new SPacketChunkData(chunk, 65535);
////            if (player instanceof EntityPlayerMP) {
////                  ((EntityPlayerMP) player).connection.sendPacket(data);
////            }
//        }


//        if (!player.world.isRemote) {
//            int range = 4;
//
//            for (int chunkX = - range; chunkX < range; chunkX++) {
//                for (int chunkZ = - range; chunkZ < range; chunkZ++) {
//
//                    Chunk chunk = world.getChunkFromChunkCoords((int) (player.posX / 16) + chunkX, (int) (player.posZ / 16) + chunkZ);
//
//                    ExtendedBlockStorage[] blockStorage = chunk.getBlockStorageArray();
//
//                    //for (ExtendedBlockStorage storage : blockStorage) {
//                        for (int x = 0; x < 16; x++) {
//                            for (int y = 0; y < 255; y++) {
//                                for (int z = 0; z < 16; z++) {
//                                    chunk.setBlockState(new BlockPos(x, y, z), Blocks.AIR.getDefaultState());
//                                }
//                            }
//                        }
//                    //}
//
//
////                    SPacketChunkData data = new SPacketChunkData(chunk, 65535);
////                    if (player instanceof EntityPlayerMP) {
////                        ((EntityPlayerMP) player).connection.sendPacket(data);
////                    }
//                }
//            }
//        }


//        if (!player.world.isRemote) {
//            int range = 5;
//
//            for (int chunkX = - range; chunkX < range; chunkX++) {
//                for (int chunkZ = - range; chunkZ < range; chunkZ++) {
//
//                    Chunk chunk = world.getChunkFromChunkCoords((int) (player.posX / 16) + chunkX, (int) (player.posZ / 16) + chunkZ);
//
//                    ExtendedBlockStorage[] blockStorage = chunk.getBlockStorageArray();
//
//                    for (int i = 0; i < blockStorage.length - 8; i++) {
//                        ExtendedBlockStorage storage = blockStorage[i];
//                        for (int x = 0; x < 16; x++) {
//                            for (int y = 0; y < 16; y++) {
//                                for (int z = 0; z < 16; z++) {
//                                    if (storage == null) {
//                                        storage = new ExtendedBlockStorage(i, true);
//                                        blockStorage[i] = storage;
//                                    }
//
//                                    if (world.rand.nextBoolean()) {
//                                        storage.set(x, y, z, Blocks.AIR.getDefaultState());
//                                    }
//                                    else {
//                                        storage.set(x, y, z, Blocks.STONE.getDefaultState());
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//
//                    SPacketChunkData data = new SPacketChunkData(chunk, 65535);
//                    if (player instanceof EntityPlayerMP) {
//                        ((EntityPlayerMP) player).connection.sendPacket(data);
//                    }
//                }
//            }
//        }


//        LogHelper.info("Side");
//        if (true) return new ActionResult(EnumActionResult.FAIL, itemStack);

        MODES.clear();
        MODES.put(0, "Complete Crafting");
        MODES.put(1, "Insert RF");
        MODES.put(2, "Extract RF");
        MODES.put(3, "List Particles");
        MODES.put(4, "Recipe");
        MODES.put(5, "Clear");
        MODES.put(6, "Mod Wiki");
        MODES.put(7, "Explode");

        handleRightClick(itemStack, world, player, hand);

//        if (!world.isRemote){
//            RayTraceResult traceResult = RayTracer.retrace(player, 1000);
//            if (traceResult != null){
//                Vec3D pos = new Vec3D(traceResult.getBlockPos());
//                BCEffectHandler.spawnFX(DEParticles.CHAOS_IMPLOSION, world, pos, pos, 1024D, 5);
//            }
//        }
        //LogHelper.info(world.getNearestPlayerNotCreative(player, 100));

//        FusionRecipeRegistry.recipeRegistry.clear();
//        FusionRecipes.registerRecipes();
//
//        if (world.isRemote && BCEffectHandler.effectRenderer != null) {
//            BCEffectHandler.effectRenderer.addEffect(new ResourceLocation("textures/particle/particles2.png"), new ParticleFusionCrafting(world, new Vec3D(player.posX, player.posY, player.posZ + 3), new Vec3D(), new TileFusionCraftingCore()));
//
//            return super.onItemRightClick(itemStack, world, player, hand);
//        }
//
//        world.addWeatherEffect(new EntityLightningBolt(world, player.posX, player.posY + 0, player.posZ+ 20, true));
//
//        try {
//            List<String> list = new ArrayList<String>();
//            list.add("a");
//            list.add("b");
//            list.add("c");
//            list.add("d");
//            list.add("e");
//            list.add("f");
//
//            for (int i = 0; i < 5; i++) {
//                for (String s : list) {
//                    if (s.equals("d")) {
//                        list.remove(s);
//                        LogHelper.info("Removed S");
//                        break;
//                    }
//                }
//                LogHelper.info("Loop");
//            }
//
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }


//        int range = 1;
//        for (BlockPos pos : BlockPos.getAllInBox(new BlockPos(player.posX-range, player.posY-range + (range * 2) + 5, player.posZ-range), new BlockPos(player.posX+range, player.posY+range + (range * 2) + 5, player.posZ+range))){
//            world.setBlockState(pos, Blocks.TNT.getDefaultState());
//           // world.setBlockToAir(pos);
//        }

//
        //BODMAS

        //LogHelper.info(7 + 36 / 9 + 9 * 1 + 4 + 3 + 7 / 2 * 6 - 14 + Math.pow(2.6, 5));
        //LogHelper.info(Math.pow((((((((((((7 + 36) / 9) + 9) * 1) + 4) + 3) + 7) / 2) * 6) - 14) + 2.6), 5));

//        Object[] array = new Object[] {1, 2, 3, 4, 5, 6};
//        LogHelper.info(Arrays.asList(array));
//
//        array = ArrayUtils.arrayShift(array, -4527035);
//
//        LogHelper.info(Arrays.asList(array));
//        LogHelper.info("");

        //LogHelper.info(-6 % 5);


//        byte value = (byte)itemRand.nextInt(16);
//        LogHelper.info(value);
//
//        boolean[] booleans = new boolean[8];
//
//        booleans[0] = (value & 0x01) != 0;
//        booleans[1] = (value & 0x02) != 0;
//        booleans[2] = (value & 0x04) != 0;
//        booleans[3] = (value & 0x08) != 0;
//        booleans[4] = (value & 0x10) != 0;
//        booleans[5] = (value & 0x32) != 0;
//        booleans[6] = (value & 0x64) != 0;
//        booleans[7] = (value & 0x128) != 0;
//
//
//        String s = "";
//        for (boolean b : booleans) s+=b+",";
//        //LogHelper.info(Integer.toBinaryString(value));
//        //LogHelper.info(s);
//       // LogHelper.info(Integer.parseInt("011010", 2));


        //  LogHelper.info();

//        BigInteger bigInteger = BigInteger.valueOf(Long.MAX_VALUE);
//        BigInteger bigInteger2 = BigInteger.valueOf(Long.MAX_VALUE);
//        BigInteger value = bigInteger.multiply(bigInteger2);
//        LogHelper.info(value+" "+bigInteger);
        // generate(world, world.rand, new BlockPos(player.posX, player.posY, player.posZ));


//        for (int i = 0; i < 6; i++){
//            LogHelper.info(i+" " + (i / 3) + " " + (i % 3));
//        }

        //BCEffectHandler.testRenderer.spawnEffectParticle(EnumParticleTypes.BARRIER.getParticleID(), player.posX, player.posY, player.posZ, 0, 0, 0);
        //world.spawnParticle(EnumParticleTypes.BARRIER, player.posX, player.posY, player.posZ + 1, 0, 0, 0);
        // for (int i = 0; i < 20000; i++) BCEffectHandler.s(new ResourceLocation("textures/particle/particles.png"), new EntitySpellParticleFX.AmbientMobFactory().getEntityFX(EnumParticleTypes.SPELL_MOB.getParticleID(), world, player.posX + itemRand.nextDouble(), player.posY+itemRand.nextDouble(), player.posZ + itemRand.nextDouble(), 0, 0, 0));

//        for (int i = 0; i < 200; i++) BCEffectHandler.spawnFX(DEParticles.ENERGY_PARTICLE, world, player.posX+ itemRand.nextFloat(), player.posY+ itemRand.nextFloat(), player.posZ+ itemRand.nextFloat(), player.posX, player.posY, player.posZ + 10);

//        LogHelper.info("Clic");
//        int posX = (int)player.posX;
//        int posY = (int)player.posY;
//        int posZ = (int)player.posZ;
//        int size = 500;
//
//
//        for (int x = posX; x < posX + size; x++){
//            double perc = (double)(x - posX) / (double)(size);
//            LogHelper.info(perc * 100D+"-Percent");
//            for (int z = posZ; z < posZ + size; z++){
//                for (int y = posY; y < 255; y++){
//                    BlockPos pos = new BlockPos(x, y, z);
//
//                    double noise = 0;
//
//                    for (int octave = 1; octave < 5; octave++) {
//                        double d = octave * 0.007D;
//                        noise +-= SimplexNoise.noise(x * d, y * d, z * d);
//                    }
//
//
//
//                    //noise = Math.abs(noise);
//
//                    //LogHelper.info(noise);
//                    double d = 0.05D;
//                    if (noise > SimplexNoise.noise(y * d, x * d, z * d)) {
//                        world.setBlockState(pos, Blocks.stone.getDefaultState());
//                    }
//                    else {
//                       // world.setBlockToAir(pos);
//                    }
//                }
//            }
//        }


//        for (int x = posX; x < posX + size; x++){
//            for (int y = posY; y < posY + size; y++){
//                for (int z = posZ; z < posZ + size; z++){
//                    BlockPos pos = new BlockPos(x, y, z);
//
//                    double dist = Utills.getDistanceAtoB(x - posX, y - posY, z - posZ, size / 2, size / 2, size / 2);
//
//                    dist /= size;
//
//                   // dist = 1 - dist;
//
//                   //LogHelper.info(dist);
//
//                    double noise = 0;
//
//                    for (int octave = 1; octave < 2; octave++) {
//                        double d = octave * 0.5D;
//                        noise += SimplexNoise.noise(x * d, y * d, z * d) - (dist * 2);
//                    }
//
//
//
//                    //noise = Math.abs(noise);
//
//                    //LogHelper.info(noise);
//
//                    if (noise > 0) {
//                       world.setBlockState(pos, Blocks.stone.getDefaultState());
//                    }
//                    else {
//                       world.setBlockToAir(pos);
//                    }
//                }
//            }
//        }
//
//        LogHelper.info("Done!");
//
//
//
//
//
//        for (int x = posX; x < posX + size; x++){
//            for (int y = posY; y < posY + size; y++){
//                for (int z = posZ; z < posZ + size; z++){
//                    BlockPos pos = new BlockPos(x, y, z);
//
//                    double noise = 0;
//
//                    for (int octave = 1; octave < 5; octave++) {
//                        double d = octave * 0.005D;
//                        noise += SimplexNoise.noise(x * d, y * d, z * d);
//                    }
//
//
//
//                    //noise = Math.abs(noise);
//
//                    //LogHelper.info(noise);
//
//                    if (noise > 0) {
//                        world.setBlockState(pos, Blocks.stone.getDefaultState());
//                    }
//                    else {
//                        world.setBlockToAir(pos);
//                    }
//                }
//            }
//        }


        return super.onItemRightClick(world, player, hand);
    }

    public ActionResult<ItemStack> handleRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {


//
//
        if (false) {

            int posX = (int) player.posX;
            double posY = (int) player.posY;
            int posZ = (int) player.posZ;

            int rayCount = 500;

            NoiseGeneratorSimplex ng = new NoiseGeneratorSimplex();

            for (int ray = rayCount; ray > 0; ray--) {

                double pc = ray / (double) rayCount;
                double direction = Math.PI * 2 * pc;
                int rayDist = 20 + itemRand.nextInt(10);

                double x = posX + itemRand.nextGaussian() * 5;
                double y = posY + itemRand.nextGaussian() * 5;
                double z = posZ + itemRand.nextGaussian() * 5;

                double r = 1 + itemRand.nextDouble();

                for (int rayD = 0; rayD < rayDist; rayD++) {
                    double dpc = 1D - (rayD / (double) rayDist);

                    double d2 = direction + (ng.getValue(x / 1.8, z / 1.8) * r);
                    x += Math.cos(d2);
                    z += Math.sin(d2);

                    int prevY = (int) y;
                    y += itemRand.nextGaussian() + (dpc * 1.3);
                    if ((int) y - prevY > 1) y--;
                    if ((int) y - prevY < -1) y++;

                    BlockPos pos = new BlockPos((int) x, (int) y, (int) z);

                    world.setBlockState(pos, Blocks.LOG.getDefaultState());

                    for (EnumFacing facing : EnumFacing.VALUES) {
                        BlockPos pos2 = pos.offset(facing);
                        if (world.isAirBlock(pos2)) {
                            world.setBlockState(pos2, Blocks.LEAVES.getDefaultState());
                        }
                    }
                }
            }

            rayCount = 200;


            for (; rayCount > 0; rayCount--) {

                double pc = rayCount / 100D;
                double direction = Math.PI * 2 * pc;
                int rayDist = 15 + itemRand.nextInt(5);

                Vec3I vec = new Vec3I(posX, (int) posY, posZ);
                double x = posX + itemRand.nextDouble() * 10;
                double y = posY + itemRand.nextDouble() * 10;
                double z = posZ + itemRand.nextDouble() * 10;

                double r = 1 + itemRand.nextDouble();

                for (int rayD = 0; rayD < rayDist; rayD++) {
                    double d2 = direction + (ng.getValue(x / 1.8, z / 1.8) * r);
                    x += Math.cos(d2);
                    z += Math.sin(d2);

                    int prevY = (int) y;
                    y += itemRand.nextGaussian();
                    if ((int) y - prevY > 1) y--;
                    if ((int) y - prevY < -1) y++;

                    vec.set((int) x, (int) y, (int) z);

                    world.setBlockState(vec.getPos(), Blocks.LOG.getDefaultState());

                    for (EnumFacing facing : EnumFacing.VALUES) {
                        BlockPos pos = vec.getPos().add(facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
                        if (world.isAirBlock(pos)) {
                            world.setBlockState(pos, Blocks.LEAVES.getDefaultState());
                        }
                    }
                }
            }

            posX = (int) player.posX;
            posY = (int) player.posY;
            posZ = (int) player.posZ;

            rayCount = 100;

            for (; rayCount > 0; rayCount--) {

                double pc = rayCount / 100D;
                double direction = Math.PI * 2 * pc;
                int rayDist = 35 + itemRand.nextInt(10);

                Vec3I vec = new Vec3I(posX, (int) posY, posZ);
                double x = posX + itemRand.nextDouble() * 10;
                double y = posY + itemRand.nextDouble() * 10;
                double z = posZ + itemRand.nextDouble() * 10;

                for (int rayD = 0; rayD < rayDist; rayD++) {
                    double d2 = direction + itemRand.nextDouble();
                    x += Math.cos(d2);
                    z += Math.sin(d2);
                    y += itemRand.nextGaussian();

                    vec.set((int) x, (int) y, (int) z);

                    world.setBlockState(vec.getPos(), Blocks.LOG.getDefaultState());

                    for (EnumFacing facing : EnumFacing.VALUES) {
                        BlockPos pos = vec.getPos().add(facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
                        if (world.isAirBlock(pos)) {
                            world.setBlockState(pos, Blocks.LEAVES.getDefaultState());
                        }
                    }
                }
            }


        }


        int mode = ItemNBTHelper.getInteger(stack, "mode", 0);
        if (player.isSneaking()) {
            mode++;
            if (mode == MODES.size()) {
                mode = 0;
            }
            if (!world.isRemote) {
                player.sendMessage(new TextComponentString(MODES.get(mode)));
            }
            ItemNBTHelper.setInteger(stack, "mode", mode);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        switch (mode) {
            case 0:
                break;
            case 3:
                if (world.isRemote) {
//                    Map<ResourceLocation, ArrayDeque<Particle>[][]> texturedRenderQueue = ReflectionHelper.getPrivateValue(BCEffectRenderer.class, BCEffectHandler.effectRenderer, "texturedRenderQueue");
//
//                    for (ArrayDeque<Particle>[][] array : texturedRenderQueue.values()) {
//                        for (ArrayDeque<Particle>[] array2 : array) {
//                            for (ArrayDeque<Particle> particle : array2) {
//                                LogHelper.info(particle);
//                            }
//                        }
//                    }

                }
                break;
            case 4:
                if (!world.isRemote) {
                    player.openGui(DraconicEvolution.instance, 2016, world, 0, 0, 0);
                }
                break;

            case 6:
                if (world.isRemote) {
                    openWiki();
                }
                break;
            case 7:
                if (!world.isRemote) {
                    destroyUniverse(player);
                }
                break;
        }

        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }


    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        //        if (world.isRemote) return EnumActionResult.PASS;
//
//        WorldGenMinableCluster cluster = new WorldGenMinableCluster(BlockOre.oreCopper, 100);
//
//        cluster.generate(world, world.rand, pos);
////        BlockOre.oreMithril.getItemDamage()
//
////        world.setBlockState(pos, Block.getBlockFromItem(BlockOre.oreMithril.getItem()).getStateFromMeta(BlockOre.oreMithril.getItemDamage()));
////        world.setBlockState(pos, Block.getBlockFromItem(BlockOre.oreMithril.getItem()).getStateFromMeta(BlockOre.oreMithril.getItemDamage()));
//
//        LogHelper.dev("GenOre");
//
//        if (true) return EnumActionResult.FAIL;

//        try {
//            //          minecraft:stone,21,2,{SomeTag:\"test\",AnotherTag:\"What The Hell...\"}
//            String s = "minecraft:stone,21,2,{SomeTag:\"test\",AnotherTag:\"What The Hell...\"}";
//            LogHelper.dev(StackReference.fromString(s));
//            LogHelper.dev(StackReference.fromString(s).createStack());
//            LogHelper.dev(StackReference.fromString(s).createStack().getTagCompound().getString("SomeTag"));
////            NBTTagCompound c = new NBTTagCompound();
////            c.setString("TestS", "What?");
////            c.setString("Test2", "Why?");
////            c.setString("Test3", "I Dont Get It...");
//            LogHelper.dev("");
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }


//        if (!world.isRemote) {
//
//            LogHelper.info("GO");
//
//            InventoryDynamic inventory = new InventoryDynamic();
//
//            for (BlockPos getPos : BlockPos.getAllInBox(pos.add(-20, -20, -20), pos.add(20, 20, 20))) {
//                List<ItemStack> stacks = BlockToStackHelper.breakAndCollect(world, getPos);
//                for (ItemStack s : stacks) {
//                    InventoryUtils.insertItem(inventory, s, false);
//                }
//            }
//
////            for (int i = 0; i < inventory.getSizeInventory(); i++) {
////                ItemStack s = inventory.removeStackFromSlot(i);
////                if (s != null) {
////                    EntityItem item = new EntityItem(world, player.posX, player.posY - 10, player.posZ, s);
////                    world.spawnEntityInWorld(item);
////                }
////            }
//
//            return EnumActionResult.PASS;
//        }


        int mode = ItemNBTHelper.getInteger(stack, "mode", 0);

        switch (mode) {
            case 0:
                return finishCraft(world, pos);

            case 1:
            case 2:
                TileEntity tile = world.getTileEntity(pos);
                if (mode == 1 && tile instanceof IEnergyReceiver) {
                    if (!world.isRemote) {
                        LogHelper.info(((IEnergyReceiver) tile).receiveEnergy(side, Integer.MAX_VALUE, false));
                    }
                    return EnumActionResult.PASS;
                }
                else if (mode == 2 && tile instanceof IEnergyProvider) {
                    if (!world.isRemote) {
                        ((IEnergyProvider) tile).extractEnergy(side, Integer.MAX_VALUE, false);
                    }
                    return EnumActionResult.PASS;
                }
                break;

        }

        return miscFunctions(stack, player, world, pos, side, hitX, hitY, hitZ, hand, mode);
    }

    //endregion

    public EnumActionResult miscFunctions(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand, int mode) {
        IBlockState state = world.getBlockState(pos);

        return EnumActionResult.PASS;
    }

    //region Functions

    private void openWiki() {
//        Minecraft.getMinecraft().displayGuiScreen(new GuiModWiki());
    }

    public EnumActionResult finishCraft(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileFusionCraftingCore && !world.isRemote) {
            if (((TileFusionCraftingCore) tile).craftingInProgress()) {
                ((TileFusionCraftingCore) tile).craftingStage.value = 2000;
            }
            return EnumActionResult.FAIL;
        }

        return EnumActionResult.PASS;
    }

    private void destroyUniverse(EntityPlayer player) {
        /*
         * == Logic Design ideas ==
         * Zones:
         * -Z1 <= 10% rad.
         *   Nothing survives unless its indestructible.
         *
         * //-Z2 10 -> 50~% rad. (Will vary depending on the resistance of the blocks destroyed though)
         * //-Z1 <= 10% rad.
         * Dont think zones are going to work out except for zone 1.
         *
         * Resistance:
         * -The resistance of each block will be divided by the radius of the trace.
         */


//        Vec3D a = new Vec3D(0, 0, 0);
//        Vec3D b = new Vec3D(1, 0, -1);
//
//        double theta = Math.atan2(b.x - a.x, a.z - b.z);
//        if (theta < 0.0){
//            theta += Math.PI * 2;
//        }
//
//        int arraySize = 2;
//        double angularValue = (theta / 6.28319) * arraySize;
//
//        int min = MathHelper.floor(angularValue);
//        int max = MathHelper.ceil(angularValue);
//        double delta = angularValue - min;
//        double minShare = 1 - delta;
//        double maxShare = delta;
//
//        LogHelper.dev(angularValue);
//        LogHelper.dev("Min: " + min);
//        LogHelper.dev("Min Share: " + minShare);
//        LogHelper.dev("Max: " + max);
//        LogHelper.dev("Max Share: " + maxShare + "\n");

//        RayTraceResult result = RayTracer.retrace(player, 5000);
//        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
//
//        }

//        ProcessExplosion explosion = new ProcessExplosion(new Vec3D(player).getPos(), 350, (WorldServer) player.world, 10);
//        ProcessHandler.addProcess(explosion);

        IBlockState lava = Blocks.FLOWING_LAVA.getDefaultState();
        LogHelper.dev(FluidRegistry.isFluidRegistered("pyrotheum"));
        if (FluidRegistry.isFluidRegistered("pyrotheum")) {
            Fluid pyro = FluidRegistry.getFluid("pyrotheum");
            if (pyro.canBePlacedInWorld()) {
                lava = pyro.getBlock().getDefaultState();
            }
        }

        World world = player.world;
        world.createExplosion(null, player.posX, player.posY, player.posZ, 8, true);
        int c = 25 + world.rand.nextInt(25);
        for (int i = 0; i < c; i++) {
            EntityFallingBlock entity = new EntityFallingBlock(world, ((int) player.posX) + 0.5, (int) player.posY, ((int) player.posZ) + 0.5, lava);
            entity.fallTime = 1;
            entity.shouldDropItem = false;
            double vMod = 0.5 + (2 * world.rand.nextDouble());
            entity.addVelocity((world.rand.nextDouble() - 0.5) * vMod, (world.rand.nextDouble() / 1.5) * vMod, (world.rand.nextDouble() - 0.5) * vMod);
            world.spawnEntity(entity);
        }


//        for (BlockPos pos : BlockPos.getAllInBox(new Vec3D(player).getPos().add(-20, -20, -20), new Vec3D(player).getPos().add(20, 20, 20))) {
//            IBlockState state = player.world.getBlockState(pos);
//            if (state.getBlock() instanceof BlockFalling) {
//                state.getBlock().updateTick(player.world, pos, state, player.world.rand);
//            }
//            state.neighborChanged(player.world, pos, Blocks.AIR);
//        }


//        while (!explosion.isDead()) {
//            explosion.updateProcess();
//        }
//
//        WorldServer world = (WorldServer) player.world;
//        LogHelper.dev("Finding Chunks to relight");
//        List<ChunkPos> chunks = new LinkedHashList<>();
//        for (BlockPos pos : explosion.destroyedCache) {
//            ChunkPos cp = new ChunkPos(pos);
//            if (!chunks.contains(cp)) {
//                chunks.add(cp);
//            }
//        }
//        LogHelper.dev("Relighting chunks");
//        for (ChunkPos pos : chunks) {
//            Chunk chunk = world.getChunkFromChunkCoords(pos.chunkXPos, pos.chunkZPos);
//            chunk.generateSkylightMap();
//            SPacketChunkData packet = new SPacketChunkData(chunk, 65535);
//            world.getMinecraftServer().getPlayerList().sendPacketToAllPlayers(packet);
//
//        }
//        for (int x = -10; x < 2; x++) {
//            for (int z = -10; z < 2; z++) {
//                world.getChunkFromBlockCoords(new Vec3D(player).getPos().add(x * 16, 0, z * 16)).generateSkylightMap();
//            }
//        }

//        LogHelper.dev("Done!");

//        double[] radialPower = new double[128];


//        for (int i = 0; i < radialPower.length; i++) {
//            double r = (i / (double) radialPower.length) * Math.PI * 2;
//            LogHelper.dev(SimplexNoise.noise(Math.sin(r), Math.cos(r)));
//
////            radialPower[i] = p + (SimplexNoise.noise(Math.sin(r) * 1000, Math.cos(r) * 1000) * (power / 5D));
//        }


    }

    //endregion
}

//region Junk

//int drain = Integer.MAX_VALUE;
//long ticks = Long.MAX_VALUE / drain;
//long seconds = ticks / 20;
//long minutes = seconds / 60;
//long hours = minutes / 60;
//long days = hours / 24;
//double years = days / 365D;
//int centuries = (int)years / 100;
//LogHelper.info(centuries+" "+years+" "+Integer.MAX_VALUE);


////    private void generate(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
////
////        int posX = (int)player.posX;
////        double posY = (int)player.posY;
////        int posZ = (int)player.posZ;
////
////        int rayCount = 500;
////
////        NoiseGeneratorSimplex ng = new NoiseGeneratorSimplex();
////
////        for (int ray = rayCount; ray > 0; ray--){
////
////            double pc = ray / (double)rayCount;
////            double direction = Math.PI * 2 * pc;
////            int rayDist = 20 + itemRand.nextInt(10);
////
////            double x = posX + itemRand.nextGaussian() * 5;
////            double y = posY + itemRand.nextGaussian() * 5;
////            double z = posZ + itemRand.nextGaussian() * 5;
////
////            double r = 1 + itemRand.nextDouble();
////
////            for (int rayD = 0; rayD < rayDist; rayD++){
////                double dpc = 1D - (rayD / (double)rayDist);
////
////                double d2 = direction + (ng.getValue(x/1.8, z/1.8) * r);
////                x += Math.cos(d2);
////                z += Math.sin(d2);
////
////                int prevY = (int)y;
////                y += itemRand.nextGaussian() + (dpc*1.3);
////                if ((int)y - prevY > 1) y--;
////                if ((int)y - prevY < -1) y++;
////
////                BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
////
////                world.setBlockState(pos, Blocks.LOG.getDefaultState());
////
////                for (EnumFacing facing : EnumFacing.VALUES){
////                    BlockPos pos2 = pos.offset(facing);
////                    if (world.isAirBlock(pos2)) {
////                        world.setBlockState(pos2, Blocks.LEAVES.getDefaultState());
////                    }
////                }
////            }
////        }
//
////        rayCount = 200;
////
////
////        for (; rayCount > 0; rayCount--) {
////
////            double pc = rayCount / 100D;
////            double direction = Math.PI * 2 * pc;
////            int rayDist = 15 + itemRand.nextInt(5);
////
////            Vec3I vec = new Vec3I(posX, (int) posY, posZ);
////            double x = posX + itemRand.nextDouble() * 10;
////            double y = posY + itemRand.nextDouble() * 10;
////            double z = posZ + itemRand.nextDouble() * 10;
////
////            double r = 1 + itemRand.nextDouble();
////
////            for (int rayD = 0; rayD < rayDist; rayD++) {
////                double d2 = direction + (ng.getValue(x / 1.8, z / 1.8) * r);
////                x += Math.cos(d2);
////                z += Math.sin(d2);
////
////                int prevY = (int) y;
////                y += itemRand.nextGaussian();
////                if ((int) y - prevY > 1) y--;
////                if ((int) y - prevY < -1) y++;
////
////                vec.set((int) x, (int) y, (int) z);
////
////                world.setBlockState(vec.getPos(), Blocks.LOG.getDefaultState());
////
////                for (EnumFacing facing : EnumFacing.VALUES) {
////                    BlockPos pos = vec.getPos().add(facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
////                    if (world.isAirBlock(pos)) {
////                        world.setBlockState(pos, Blocks.LEAVES.getDefaultState());
////                    }
////                }
////            }
////        }
//
////        int posX = (int)player.posX;
////        double posY = (int)player.posY;
////        int posZ = (int)player.posZ;
////
////        int rayCount = 100;
////
////        for (; rayCount > 0; rayCount--){
////
////            double pc = rayCount / 100D;
////            double direction = Math.PI * 2 * pc;
////            int rayDist = 35 + itemRand.nextInt(10);
////
////            Vec3I vec = new Vec3I(posX, (int)posY, posZ);
////            double x = posX + itemRand.nextDouble() * 10;
////            double y = posY + itemRand.nextDouble() * 10;
////            double z = posZ + itemRand.nextDouble() * 10;
////
////            for (int rayD = 0; rayD < rayDist; rayD++){
////                double d2 = direction + itemRand.nextDouble();
////                x += Math.cos(d2);
////                z += Math.sin(d2);
////                y += itemRand.nextGaussian();
////
////                vec.set((int)x, (int)y, (int)z);
////
////                world.setBlockState(vec.getPos(), Blocks.LOG.getDefaultState());
////
////                for (EnumFacing facing : EnumFacing.VALUES){
////                    BlockPos pos = vec.getPos().add(facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
////                    if (world.isAirBlock(pos)) {
////                        world.setBlockState(pos, Blocks.LEAVES.getDefaultState());
////                    }
////                }
////            }
////        }
////    }
//
//    public boolean generate(World worldIn, Random rand, BlockPos position) {
////        if (position.getY() + 2 + ySize > 256) {
//////            return false;
//////
////  }
//        int trunkHeight = 20;
//
//
////        for (BlockPos pos : BlockPos.getAllInBox(position.add(-1, -1, -1), position.add(1, -1, 1))) {
////            IBlockState state = worldIn.getBlockState(pos);
////            if (!(state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.GRASS)) {
////                return false;
////            }
////        }
//        //for (BlockPos pos : BlockPos.getAllInBox(position.add(-1, 0, -1), position.add(1, 1, 1))) {
//        //    IBlockState state = worldIn.getBlockState(pos);
//        //    Block block = state.getBlock();
//        //    if (!block.isLeaves(state, worldIn, pos) && !block.isWood(worldIn, pos) && block != Blocks.tallgrass && block != Blocks.grass && block != Blocks.vine) {
//        //        return false;
//        //    }
//        //}
//
//        for (BlockPos pos : BlockPos.getAllInBox(position.add(-1, -1, -1), position.add(1, -1, 1))) {
//            worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());
//        }
//        for (BlockPos pos : BlockPos.getAllInBox(position.add(-1, 0, -1), position.add(1, trunkHeight, 1))) {
//            worldIn.setBlockState(pos, Blocks.LOG.getDefaultState());
//        }
//        for (EnumFacing dir : EnumFacing.HORIZONTALS) {
//            BlockPos edgePos = position.offset(dir, 2);
//            switch (dir) {
//                case NORTH:
//                    generateVines(worldIn, rand, dir, edgePos.add(-1, 0, 0), edgePos.add(1, trunkHeight, 0));
//                    continue;
//                case SOUTH:
//                    generateVines(worldIn, rand, dir, edgePos.add(-1, 0, 0), edgePos.add(1, trunkHeight, 0));
//                    continue;
//                case WEST:
//                    generateVines(worldIn, rand, dir, edgePos.add(0, 0, -1), edgePos.add(0, trunkHeight, 1));
//                    continue;
//                case EAST:
//                    generateVines(worldIn, rand, dir, edgePos.add(0, 0, -1), edgePos.add(0, trunkHeight, 1));
//                    continue;
//                default:
//                    LogHelper.info("Invalid side in EnumFacing.HORIZONTALS [%s]", dir.getName());
//            }
//        }
//        BlockPos rayStartPos = position.add(0, trunkHeight - 3, 0);
//        int posX = rayStartPos.getA();
//        double posY = rayStartPos.getY();
//        int posZ = rayStartPos.getZ();
//
//        /*int rayCount = 1000;
//
//        NoiseGeneratorSimplex ng = new NoiseGeneratorSimplex();
//
//        for (int ray = rayCount; ray > 0; ray--){
//
//            double pc = ray / (double)rayCount;
//            double direction = Math.PI * 2 * pc;
//            int rayDist = 20 + rand.nextInt(10);
//
//            double x = posX;
//            double y = posY;
//            double z = posZ;
//
//            double r = 1 + rand.nextDouble();
//
//            for (int rayD = 0; rayD < rayDist; rayD++) {
//                double d2 = direction + (ng.func_151605_a(x/1.8, z/1.8) * r);
//                x += Math.cos(d2);
//                z += Math.sin(d2);
//
//                int prevY = (int) y;
//                y += rand.nextGaussian();
//                if ((int) y - prevY > 1) {
//                    y--;
//                }
//                if ((int) y - prevY < -1) {
//                    y++;
//                }
//                placeRayPart(worldIn, new BlockPos(x, y, z));
//            }
//        }*/
//        int rayCount = 100;
//
//        NoiseGeneratorSimplex ng = new NoiseGeneratorSimplex();
//
//        //region Long Rays
//
////        for (int ray = rayCount; ray > 0; ray--) {
////
////            double pc = ray / (double) rayCount;
////            double direction = Math.PI * 2 * pc;
////            int rayDist = 35 + rand.nextInt(10);
////
////            double x = posX + itemRand.nextGaussian() * 3;
////            double y = posY + itemRand.nextGaussian() * 2;
////            double z = posZ + itemRand.nextGaussian() * 3;
////
////            double r = 1 + rand.nextDouble();
////
////            for (int rayD = 0; rayD < rayDist; rayD++) {
////                double dpc = 1D - (rayD / (double) rayDist);
////
////                double d2 = direction + (ng.func_151605_a(x / 1.8, z / 1.8) * r);
////                x += Math.cos(d2);
////                z += Math.sin(d2);
////
////                int prevY = (int) y;
////                y += rand.nextGaussian() + (dpc);
////                if ((int) y - prevY > 1) {
////                    y--;
////                }
////                if ((int) y - prevY < -1) {
////                    y++;
////                }
////
////                BlockPos vec = new BlockPos(x, y, z);
////
////                placeRayPart(worldIn, vec);
////            }
////        }
//
//        //region Short Rays
//
//        rayCount = 100;
//
////        for (int ray = rayCount; ray > 0; ray--) {
////
////            double pc = ray / (double) rayCount;
////            double direction = Math.PI * 2 * pc;
////            int rayDist = 15 + rand.nextInt(5);
////
////            double x = posX + itemRand.nextGaussian() * 3;
////            double y = posY + itemRand.nextGaussian() * 2;
////            double z = posZ + itemRand.nextGaussian() * 3;
////
////            double r = 1 + rand.nextDouble();
////
////            for (int rayD = 0; rayD < rayDist; rayD++) {
////                double dpc = 1D - (rayD / (double) rayDist);
////
////                double d2 = direction + (ng.func_151605_a(x / 1.8, z / 1.8) * r);
////                x += Math.cos(d2);
////                z += Math.sin(d2);
////
////                int prevY = (int) y;
////                y += rand.nextGaussian() + (dpc);
////                if ((int) y - prevY > 1) {
////                    y--;
////                }
////                if ((int) y - prevY < -1) {
////                    y++;
////                }
////
////                BlockPos vec = new BlockPos(x, y, z);
////
////                placeRayPart(worldIn, vec);
////            }
////        }
//
//
//        return true;
//    }
//
//    private void generateVines(World world, Random rand, EnumFacing dir, BlockPos from, BlockPos to) {
//        for (BlockPos pos : BlockPos.getAllInBox(from, to)) {
////            if (rand.nextInt(5) == 1 && world.getBlockState(pos).getBlock() == Blocks.air) {
////                world.setBlockState(pos, Blocks.vine.getDefaultState().withProperty(BlockVine.getPropertyFor(dir.getOpposite()), true));
////  /          }
//        }
//    }
//
//    private void placeRayPart(World world, BlockPos pos) {
//        //Iterable<BlockPos> l = BlockPos.getAllInBox(sPos, sPos.add(itemRand.nextInt(2), itemRand.nextInt(2), itemRand.nextInt(2)));
//
////        //for (BlockPos pos : l) {
////            world.setBlockState(pos, Blocks.log.getDefaultState());
////            for (EnumFacing side : EnumFacing.VALUES) {
////                BlockPos leavesPos = pos.offset(side);
////                if (world.isAirBlock(leavesPos)) {
////                    world.setBlockState(leavesPos, Blocks.leaves.getDefaultState());
////                }
////            }
////        //}
//    }
//        //endregion
