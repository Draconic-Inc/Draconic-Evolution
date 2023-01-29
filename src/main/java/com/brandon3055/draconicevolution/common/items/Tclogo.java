package com.brandon3055.draconicevolution.common.items;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.brandon3055.draconicevolution.client.render.particle.Particles;
import com.brandon3055.draconicevolution.common.ModItems;
import com.brandon3055.draconicevolution.common.entity.ExtendedPlayer;
import com.brandon3055.draconicevolution.common.items.tools.baseclasses.ToolHandler;
import com.brandon3055.draconicevolution.common.lib.Strings;
import com.brandon3055.draconicevolution.common.utills.LogHelper;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Tclogo extends ItemDE {

    public Tclogo() {
        this.setUnlocalizedName(Strings.tclogoName);
        // this.setCreativeTab(draconicevolution.getCreativeTab());
        ModItems.register(this);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 100;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
        // if (entity instanceof EntityPlayerMP && !entity.onGround) {
        // //for (int i = 0; i < 10; i++)
        // EntityPlayer player = (EntityPlayer)entity;
        // if (player.posX < 500){
        //
        // ((EntityPlayerMP) entity).playerNetServerHandler.setPlayerLocation(player.posX + 10, entity.posY,
        // entity.posZ, entity.rotationYaw, entity.rotationPitch);
        // player.motionX += 10;
        // if (player.posX > 128) {
        //
        // ((EntityPlayerMP) entity).playerNetServerHandler.setPlayerLocation(501, entity.posY, entity.posZ,
        // entity.rotationYaw, entity.rotationPitch);
        //
        // }
        //
        // }
        // else player.motionX = 0;

        // ((EntityPlayerMP) entity).playerNetServerHandler.setPlayerLocation(entity.posX + 10, entity.posY,
        // entity.posZ, entity.rotationYaw, entity.rotationPitch);
        // ((EntityPlayerMP) entity).addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 10, 1000));
        // if (entity.posX > 500) ((EntityPlayerMP) entity).destroyCurrentEquippedItem();
        // }
        /*
         * //if (entity.isCollidedHorizontally)// && !world.isRemote) //entity.setLocationAndAngles(entity.posX+30D,
         * entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);; /* //System.out.println("Update Tick");
         * int X = (int)entity.posX; int Y = (int)entity.posY; int Z = (int)entity.posZ;
         * entity.worldObj.spawnParticle("flame", 613, 5, -822, 0, 0, 0); if (((EntityPlayer)entity).getHeldItem() !=
         * null && ((EntityPlayer)entity).getHeldItem().isItemEqual(new ItemStack(ModItems.tclogo))) { for(int x = X -
         * 5; x <= X + 5; x++) { for(int y = Y; y <= Y; y++) { for(int z = Z - 5; z <= Z + 5; z++) {
         * entity.worldObj.spawnParticle("flame", x - 0.5, y - 0.5, z - 0.5, 0, 0, 0);
         * entity.worldObj.scheduleBlockUpdate(x, y, z, entity.worldObj.getBlock(x, y, z), 1);
         * System.out.println("Update Tick " +X+ " " +Y+ " " +Z); } } } }
         */
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.block;
    }

    @Override
    public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        return par1ItemStack;
    }

    @SideOnly(Side.CLIENT)
    private void particle(World world, EntityPlayer player) {
        MovingObjectPosition mop = ToolHandler.raytraceFromEntity(world, player, 10000);
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            FMLClientHandler.instance().getClient().effectRenderer
                    .addEffect(new Particles.ReactorExplosionParticle(world, mop.blockX, mop.blockY, mop.blockZ, 100));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

        // int meta = Integer.parseInt());
        LogHelper.info("21=vserfegz".substring(0, "21=vserfegz".indexOf("=")));

        try {
            if (!world.isRemote) {
                // ExtendedPlayer.get(player).setSpawnCount(6);
                LogHelper.info(ExtendedPlayer.get(player).getSpawnCount());
                // player.getEntityData().setTag("Tag", new NBTTagCompound());
            } else LogHelper.info(ExtendedPlayer.get(player).getSpawnCount());
            // LogHelper.info(FMLCommonHandler.instance().getMinecraftServerInstance().func_152358_ax().func_152655_a(player.getCommandSenderName()).getId());
            // LogHelper.info(player.getUniqueID());
            // LogHelper.info(UUID.nameUUIDFromBytes(("OfflinePlayer:" +
            // player.getCommandSenderName()).getBytes(Charsets.UTF_8)));
            // LogHelper.info(UUID.nameUUIDFromBytes(("OfflinePlayer:" +
            // player.getCommandSenderName()).getBytes(Charsets.UTF_8)).equals(player.getUniqueID()));
            if (FMLCommonHandler.instance().getMinecraftServerInstance() instanceof IntegratedServer) {
                // LogHelper.info(FMLClientHandler.instance().getClient().getSession().getToken());
                // LogHelper.info(((YggdrasilMinecraftSessionService)FMLCommonHandler.instance().getMinecraftServerInstance().func_147130_as()).getAuthenticationService().);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // LogHelper.info(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode());
        //
        // ResourceLocation rs = new ResourceLocation(References.RESOURCESPREFIX + "manual-en_US.json");
        //
        // try {
        // LogHelper.info(Minecraft.getMinecraft().getResourceManager().getResource(rs));
        // }
        // catch (IOException e) {
        // e.printStackTrace();
        // }

        // if ((int)System.currentTimeMillis() < lts + 10) return stack;
        // else lts = (int)System.currentTimeMillis();
        // world.playSound(player.posX, player.posY, player.posZ, "DraconicEvolution:fusionExplosion", 1F, 1F, false);
        // LogHelper.info(GameRegistry.findBlock("ThermalDynamics", "ThermalDynamics_0"));
        // if (player.inventory.getStackInSlot(0) != null)
        // LogHelper.info(GameRegistry.findUniqueIdentifierFor(player.inventory.getStackInSlot(0).getItem()));
        //
        // if (world.isRemote){
        // particle(world, player);
        // //if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        // ProcessHandler.addProcess(new ReactorExplosion(world, mop.blockX, mop.blockY, mop.blockZ, 10F));
        // }
        // else {
        // MovingObjectPosition mop = ToolHandler.raytraceFromEntity(world, player, 10000);
        // //LogHelper.info(mop);
        // //FMLClientHandler.instance().getClient().effectRenderer.addEffect(new
        // Particles.ReactorExplosionParticle(world, mop.blockX, mop.blockY, mop.blockZ));
        // if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        // ProcessHandler.addProcess(new ReactorExplosion(world, mop.blockX, mop.blockY, mop.blockZ, 20F));
        // }

        // EntityDragonProjectile projectile = new EntityDragonProjectile(world, 6, null, 10, player);
        // projectile.setPosition(player.posX, player.posY, player.posZ);

        //
        // if (!world.isRemote)
        // {
        // int x = 45622;
        // LogHelper.info("45622".hashCode());
        //
        //
        // File file = new File("C:/Users/Brandon/Desktop/0to"+String.valueOf(Integer.MAX_VALUE)+".txt");
        //
        // try {
        // FileWriter writer = new FileWriter(file);
        // for (int i = 0; i < Integer.MAX_VALUE; i++) writer.write(i+",");
        // writer.close();
        // LogHelper.info("Done");
        // }
        // catch (Exception e) {
        // e.printStackTrace();
        // }
        //
        //
        // int xCoord = 0;
        // int yCoord = 0;
        // int zCoord = 0;
        //
        //
        //
        // int locationHash = (String.valueOf(xCoord)+String.valueOf(yCoord)+String.valueOf(zCoord)).hashCode();
        //
        //

        // LogHelper.info(ContributorHandler.contributors);
        // ContributorHandler.init();
        // LogHelper.info(ContributorHandler.contributors);

        // EntityFallingBlock fallingBlock = new EntityFallingBlock(world, (int)player.posX + 0.5, (int)player.posY +
        // 0.5, (int)player.posZ + 0.5, Blocks.obsidian, 0);
        // fallingBlock.field_145812_b = 2;

        float motion = 0.005F;
        // fallingBlock.motionX = (rand.nextFloat()-0.5F) * motion;
        // fallingBlock.motionY = (rand.nextFloat()-0.5F) * motion;
        // fallingBlock.motionZ = (rand.nextFloat()-0.5F) * motion;
        // world.spawnEntityInWorld(fallingBlock);

        // try
        // {
        // for (Object o : AchievementList.achievementList)
        // {
        // LogHelper.info(o);
        // if (o instanceof Achievement)LogHelper.info(((EntityPlayerMP)
        // player).func_147099_x().hasAchievementUnlocked((Achievement) o));
        // if (o instanceof Achievement)LogHelper.info(((EntityPlayerMP)
        // player).func_147099_x().canUnlockAchievement((Achievement) o));
        // LogHelper.info(o instanceof Achievement);
        // }
        //
        // //LogHelper.info(FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList);
        // }
        // catch (Exception e) {e.printStackTrace();}

        // LogHelper.info();

        // LogHelper.info("#########################");
        // Map<Integer, BlockCollection.BlockDat> blocks = new HashMap<Integer, BlockCollection.BlockDat>();
        // List<Integer> l = new ArrayList<Integer>();

        // int rand = itemRand.nextInt();
        //
        // long u = System.nanoTime();
        //
        // for (int i = 0; i < 100000; i++) blocks.containsKey(i+rand);
        //
        // LogHelper.info("Time1:"+(System.nanoTime()-u)/1000000D);
        // u = System.nanoTime();
        //
        // for (int i = 0; i < 100000; i++) l.get(i + rand);
        //
        // LogHelper.info("Time2:"+(System.nanoTime()-u)/1000000D);

        // DraconicWorldGenerator.chaosIslandGen = new WorldGenChaosIsland();
        // LogHelper.info("Reinitialized");
        // long u = System.nanoTime();
        // long m = System.currentTimeMillis();
        // BlockCollection blocks = DraconicWorldGenerator.chaosIslandGen.getBlocks(world.rand);
        // LogHelper.info(blocks.getIndex(30285000, 45, -30927500));
        ////
        // LogHelper.info("Generated in:"+(System.nanoTime()-u)+"ns, "+(System.currentTimeMillis()-m)+"ms");

        // for (float f = 0; f < 1; f+=0.01) LogHelper.info((0.5D-Math.abs(f-0.5D))*2D);

        // short id = (short)itemRand.nextInt(4000);
        // byte meta = (byte)itemRand.nextInt(16);
        // LogHelper.info("#########################");
        // LogHelper.info("ID:"+id+" Meta:"+meta);
        //
        // byte data1 = (byte)(id >> 4);
        // byte data2 = (byte)(((id & 0xF) << 4) | meta);
        //
        // int decodedID = (((data1 & 0xFF) << 4) | ((data2 & 0xF0) >> 4));
        // int decodedMeta = (data2 & 0x0F);
        // LogHelper.info("DecodedID:"+decodedID+" DecodedMeta:"+decodedMeta);

        // LogHelper.info(Integer.toBinaryString(id) + " "+Integer.toBinaryString(meta));
        // LogHelper.info(Integer.toBinaryString(id >> 4) +" " +Integer.toBinaryString(id & 0xF));
        // LogHelper.info(id+" "+meta);
        //// LogHelper.info(b1+" "+b2);
        // LogHelper.info(Integer.toBinaryString(b1 & 0xFF)+" "+Integer.toBinaryString(b2 & 0xFF));// +" "
        // +Integer.toBinaryString(0xF0));
        //// LogHelper.info((((b1 & 0xF0) << 8) + (b2 & 0xFF)) +" "+meta);
        // LogHelper.info("Id: "+Integer.toBinaryString(((b1 & 0xFF) << 4)) +" "+ Integer.toBinaryString(((b2 & 0xF0)
        // >>> 4)));
        // LogHelper.info("Id: "+Integer.toBinaryString(((b1 & 0xFF) << 4) | ((b2 & 0xF0) >> 4))+" "+(((b1 & 0xFF) <<
        // 4) | ((b2 & 0xF0) >> 4)));
        // LogHelper.info("Meta: "+Integer.toBinaryString(b2 & 0x0F)+" "+(b2 & 0x0F));
        // LogHelper.info("Meta: "+Integer.toBinaryString(4095));

        // for (int index = 0; index < blockId.length; index++) {
        // if ((index >> 1) >= addId.length) { // No corresponding AddBlocks index
        // blocks[index] = (short) (blockId[index] & 0xFF);
        // } else {
        // if ((index & 1) != 0) {
        // blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (short) (blockId[index] & 0xFF));
        // } else {
        // blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (short) (blockId[index] & 0xFF));
        // }
        // }
        // }

        // long s = (652800000L * 8L) + (652800000L * 8L);
        // LogHelper.info((s / 1000000000D) / 8D);

        // int posX = (int)player.posX;
        // int posZ = (int)player.posZ;
        //
        // BlockCollection blocks = DraconicWorldGenerator.chaosIslandGen.getBlocks(world.rand);
        // LogHelper.info("Generated");
        //
        // for (int x = -400; x < 400; x++){
        // for (int y = 0; y < 255; y++){
        // for (int z = -400; z < 400; z++){
        // world.setBlock(posX+x, y, posZ+z, blocks.getBlock(x, y, z));
        // }
        // }
        // System.out.print(x+", ");
        // }

        // int rand1 = itemRand.nextInt();
        // int rand2 = itemRand.nextInt();
        // int rand3 = itemRand.nextInt();
        // int rand4 = itemRand.nextInt();
        // int rand5 = itemRand.nextInt();
        // int rand6 = itemRand.nextInt();
        //
        // long l = System.currentTimeMillis();
        // for (int i = 0; i < 2000000000; i++){
        //
        // int x1 = rand1 + i;
        // int x2 = rand2 + i;
        // int y1 = rand3 + i;
        // int y2 = rand4 + i;
        // int z1 = rand5 + i;
        // int z2 = rand6 + i;
        //
        // double dx = x1-x2;
        // double dy = y1-y2;
        // double dz = z1-z2;
        // double d = dx * dx + dy * dy + dz * dz;
        // Math.sqrt(d+i);
        // }
        // LogHelper.info("Time 1:"+ (System.currentTimeMillis()-l));

        // long l = System.currentTimeMillis();
        // new WorldGenChaosIsland().initialize(world.rand);
        // LogHelper.info(System.currentTimeMillis() - l);
        //
        //

        // }

        // List<Entity> l = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(player.posX,
        // player.posY, player.posZ, player.posX, player.posY, player.posZ).expand(500, 500, 500));
        // for (Entity e : l) if (!(e instanceof EntityPlayer)) e.setDead();

        // if (1==1) return stack;
        /*
         * if (player instanceof EntityPlayerMP) { for (int i = 0; i < 10; i++) ((EntityPlayerMP)
         * player).playerNetServerHandler.setPlayerLocation(player.posX + 1, player.posY, player.posZ,
         * player.rotationYaw, player.rotationPitch); } if (1==1) return stack;
         */

        // Minecraft.getMinecraft().gameSettings.mouseSensitivity = -0.34F;

        // if (world.isRemote)
        // {
        // ResourceHandler.init(null);
        // String str = "A String";
        // IChatComponent localIChatComponent;
        //
        //
        // localIChatComponent = IChatComponent.Serializer.func_150699_a("[{\"text\":\"" + str +
        // "\",\"color\":\"aqua\"}," + "{\"text\":\" " + EnumChatFormatting.WHITE + "[" + EnumChatFormatting.GREEN +
        // "info.cofh.updater.download" + EnumChatFormatting.WHITE + "]\"," +
        // "\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":" + "{\"text\":\"" +
        //
        // "info.cofh.updater.tooltip" + ".\",\"color\":\"yellow\"}}," +
        // "\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + "www.google.com" + "\"}}]");
        //
        // //player.addChatMessage(localIChatComponent);
        // }
        // else
        // {
        // //for (Object o : EntityList.classToStringMapping.values()) LogHelper.info(o);
        // }

        if (!player.isSneaking()) {
            player.capabilities.allowFlying = true;
            // player.setPosition(player.posX, player.posY+1, player.posZ);
            player.onGround = false;
            player.capabilities.isFlying = true;
            player.noClip = !player.noClip;

        } else {
            int xi = (int) player.posX;
            int yi = (int) player.posY;
            int zi = (int) player.posZ;
            int rad = 100;

            for (int x = xi - rad; x < xi + rad; x++) {
                for (int y = yi - 10; y < yi + 30; y++) {
                    for (int z = zi - rad; z < zi + rad; z++) {
                        world.markBlockForUpdate(x, y, z);
                    }
                }
            }

            world.markBlockRangeForRenderUpdate(xi - rad, yi - 20, zi - rad, xi + rad, yi + 20, zi + rad);
        }
        //
        // LogHelper.info("Downloading Image");
        //
        // try {
        // URL url = new URL("http://i.imgur.com/oHRx1yQ.jpg");
        // String fileName = url.getFile();
        // String destName = ClientProxy.downloadLocation + fileName.substring(fileName.lastIndexOf("/"));
        // System.out.println(destName);
        //
        // InputStream is = url.openStream();
        // OutputStream os = new FileOutputStream(destName);
        //
        // byte[] b = new byte[2048];
        // int length;
        //
        // while ((length = is.read(b)) != -1) {
        // os.write(b, 0, length);
        // }
        //
        // is.close();
        // os.close();
        // }catch (IOException e){
        // LogHelper.info(e);
        // }

        // player.addPotionEffect(new PotionEffect(PotionHandler.potionFlight.id, 100, 0));
        // player.addPotionEffect(new PotionEffect(PotionHandler.potionFireResist.id, 100, 1));
        // player.addPotionEffect(new PotionEffect(PotionHandler.potionSpeed.id, 100, 1));
        // player.addPotionEffect(new PotionEffect(PotionHandler.potionUpHillStep.id, 100, 1));
        int xi = (int) player.posX;
        int yi = (int) player.posY;
        int zi = (int) player.posZ;
        int rad = 1000;

        // Block block;
        // for (int x = xi-rad; x < xi+rad; x++){
        // for (int y = yi-10; y < yi+30; y++){
        // for (int z = zi-rad; z < zi+rad; z++){
        // // block = world.getBlock(x, y, z);
        // //if (block.getMaterial().equals(Material.vine) || block.getMaterial().equals(Material.plants)){
        // //world.setBlockToAir(x, y, z);
        // //}
        //
        // //world.markBlockForUpdate(x, y, z);
        // }
        // }
        // }

        // world.markBlockRangeForRenderUpdate(xi-rad, yi-rad, zi-rad, xi+rad, yi+rad, zi+rad);
        // if (world.isRemote)player.displayGUIWorkbench((int)player.posX, (int)player.posY, (int)player.posZ);
        // world.setBlock(0, 0, 0, Blocks.crafting_table);
        // if (!world.isRemote)player.displayGUIWorkbench(0, 0, 0);

        return stack;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player) {
        LogHelper.info(GameRegistry.findUniqueIdentifierFor(player.worldObj.getBlock(X, Y, Z)));

        // player.worldObj.scheduleBlockUpdate(X, Y, Z, player.worldObj.getBlock(X, Y, Z), 10);

        return false;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        System.out.println("Use Tick");
        super.onUsingTick(stack, player, count);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack stack, final EntityPlayer player, final List list,
            final boolean extraInformation) {
        list.add(
                EnumChatFormatting.RED
                        + "Warning! this is an item used to test random bits of code. You should not play with it");
        list.add(EnumChatFormatting.RED + "because depending on what i used it for last it could do anything.");
        list.add(EnumChatFormatting.RED + "It may even break your world");
        list.add("At the time this warning was added it created a 200x200 block smoking creator");

        // list.add(EnumChatFormatting.AQUA + "AQUA");
        // list.add(EnumChatFormatting.BLACK + "BLACK");
        // list.add(EnumChatFormatting.BLUE + "BLUE");
        // list.add(EnumChatFormatting.DARK_AQUA + "DARK_AQUA");
        // list.add(EnumChatFormatting.DARK_BLUE + "DARK_BLUE");
        // list.add(EnumChatFormatting.DARK_GRAY + "DARK_GRAY");
        // list.add(EnumChatFormatting.DARK_GREEN + "DARK_GREEN");
        // list.add(EnumChatFormatting.DARK_PURPLE + "DARK_PURPLE");
        // list.add(EnumChatFormatting.DARK_RED + "DARK_RED");
        // list.add(EnumChatFormatting.GOLD + "GOLD");
        // list.add(EnumChatFormatting.GRAY + "GRAY");
        // list.add(EnumChatFormatting.GREEN + "GREEN");
        // list.add(EnumChatFormatting.LIGHT_PURPLE + "LIGHT_PURPLE");
        // list.add(EnumChatFormatting.RED + "RED");
        // list.add(EnumChatFormatting.WHITE + "WHITE");
        // list.add(EnumChatFormatting.YELLOW + "YELLOW");
        // list.add(EnumChatFormatting.BOLD + "BOLD");
        // list.add(EnumChatFormatting.ITALIC + "ITALIC");
        // list.add(EnumChatFormatting.OBFUSCATED + "OBFUSCATED");
        // list.add(EnumChatFormatting.UNDERLINE + "UNDERLINE");
        // list.add(EnumChatFormatting.STRIKETHROUGH + "STRIKETHROUGH");
    }
}
