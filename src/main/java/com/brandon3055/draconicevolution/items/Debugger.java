package com.brandon3055.draconicevolution.items;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.brandon3055.brandonscore.client.particle.BCEffectHandler;
import com.brandon3055.brandonscore.client.particle.BCEffectRenderer;
import com.brandon3055.brandonscore.items.ItemBCore;
import com.brandon3055.brandonscore.lib.BlockPlacementBatcher;
import com.brandon3055.brandonscore.utils.ItemNBTHelper;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.blocks.tileentity.TileFusionCraftingCore;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
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
    }

    //region Item Junk

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    }

    //todo remove from tab and NEI

    @Override
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {

        List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class, player.getEntityBoundingBox().expand(10, 10, 10));

        for (EntityItem item : list) {
            NBTTagCompound compound = new NBTTagCompound();
            item.writeToNBT(compound);
            LogHelper.info(compound);
        }

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


//        if (!player.worldObj.isRemote) {
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


//        if (!player.worldObj.isRemote) {
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


        return super.onItemRightClick(itemStack, world, player, hand);
    }

    public ActionResult<ItemStack> handleRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        player.inventory.addItemStackToInventory(new ItemStack(Blocks.END_GATEWAY, 64));

        int mode = ItemNBTHelper.getInteger(stack, "mode", 0);
        if (player.isSneaking()) {
            mode++;
            if (mode == MODES.size()) {
                mode = 0;
            }
            if (!world.isRemote) {
                player.addChatComponentMessage(new TextComponentString(MODES.get(mode)));
            }
            ItemNBTHelper.setInteger(stack, "mode", mode);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
        }

        switch (mode) {
            case 0:
                break;
            case 3:
                if (world.isRemote) {
                    Map<ResourceLocation, ArrayDeque<Particle>[][]> texturedRenderQueue = ReflectionHelper.getPrivateValue(BCEffectRenderer.class, BCEffectHandler.effectRenderer, "texturedRenderQueue");

                    for (ArrayDeque<Particle>[][] array : texturedRenderQueue.values()) {
                        for (ArrayDeque<Particle>[] array2 : array) {
                            for (ArrayDeque<Particle> particle : array2) {
                                LogHelper.info(particle);
                            }
                        }
                    }

                }
                break;
            case 4:
                if (!world.isRemote) {
                    player.openGui(DraconicEvolution.instance, 2016, world, 0, 0, 0);
                }
                break;

            case 5:


                if (world instanceof WorldServer) {
                    FMLLog.info("Run");

                    BlockPlacementBatcher batcher = new BlockPlacementBatcher((WorldServer) world);

                    for (int x = -120; x < 120; x++) {
                        for (int y = 0; y < 100; y++) {
                            for (int z = -120; z < 120; z++) {
                                //world.getChunkFromBlockCoords(pos.add(x * 16, 0, z * 16)).generateSkylightMap();
                                BlockPos posAt = new BlockPos(player.posX + x, y, player.posZ + z);

                                if (posAt.getY() == 63 || world.getBlockState(posAt).getBlock() == Blocks.BEDROCK || world.getBlockState(posAt).getBlock() == Blocks.STONE || world.getBlockState(posAt).getBlock() == Blocks.DIRT || world.getBlockState(posAt).getBlock() == Blocks.GRASS || world.getBlockState(posAt).getBlock().getRegistryName().getResourceDomain().contains("jarrm") || world.getBlockState(posAt).getBlock().getRegistryName().getResourcePath().contains("dra")) {
                                    batcher.setBlockState(posAt, Blocks.AIR.getDefaultState());
                                }

                            }
                        }
                    }

                    batcher.finish();
                }

                BlockPos pos = new BlockPos(player);
                for (int x = -10; x < 10; x++) {
                    for (int z = -10; z < 10; z++) {
                        world.getChunkFromBlockCoords(pos.add(x * 16, 0, z * 16)).generateSkylightMap();
                    }
                }

                break;
        }

        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
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
////        if (position.getY() + 2 + height > 256) {
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
//        //TODO Check gen area.
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
