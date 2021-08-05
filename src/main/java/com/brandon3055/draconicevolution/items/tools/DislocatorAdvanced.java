package com.brandon3055.draconicevolution.items.tools;

import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import com.brandon3055.brandonscore.handlers.HandHelper;
import com.brandon3055.brandonscore.lib.ChatHelper;
import com.brandon3055.brandonscore.lib.TeleportUtils;
import com.brandon3055.brandonscore.lib.Vec3D;
import com.brandon3055.brandonscore.utils.DataUtils;
import com.brandon3055.brandonscore.utils.TargetPos;
import com.brandon3055.draconicevolution.DEConfig;
import com.brandon3055.draconicevolution.api.IHudDisplay;
import com.brandon3055.draconicevolution.client.gui.GuiDislocator;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.server.SPlayerPositionLookPacket.Flags;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class DislocatorAdvanced extends Dislocator implements IHudDisplay {
    public DislocatorAdvanced(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
        if (player.level.isClientSide) {
            return true;
        }
        TargetPos location = getTargetPos(stack, player.level);
        int fuel = getFuel(stack);

        if (!player.abilities.instabuild && fuel <= 0) {
            messageUser(player, new TranslationTextComponent("dislocate.draconicevolution.no_fuel").withStyle(TextFormatting.RED));
            return true;
        }

        if (entity instanceof PlayerEntity) {
            if (entity.isShiftKeyDown()) {
                if (useFuel(stack, player)) {
                    dislocateEntity(stack, player, entity, location);
                }
            } else {
                messageUser(player, new TranslationTextComponent("dislocate.draconicevolution.player_allow"));
            }
            return true;
        }

        if (!entity.canChangeDimensions() || !(entity instanceof LivingEntity)) {
            return true;
        }

        if (useFuel(stack, player)) {
            dislocateEntity(stack, player, entity, location);
            messageUser(player, new StringTextComponent(I18n.get("dislocate.draconicevolution.entity_sent_to") + " " + location.getReadableName(false)).withStyle(TextFormatting.GREEN));
        }

        return true;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        TargetPos location = getTargetPos(stack, player.level);

        boolean blink = getBlinkMode(stack);
        if (player.isShiftKeyDown() || (location == null && !blink)) {
            if (world.isClientSide) {
                openGui(stack, player);
            }
        } else if (!world.isClientSide) {
            if (blink) {
                handleBlink((ServerPlayerEntity) player, stack, false);
            }else {
                handleTeleport(player, stack, location, false);
            }
        }
        return new ActionResult<>(ActionResultType.PASS, stack);
    }

    @OnlyIn(Dist.CLIENT)
    private void openGui(ItemStack stack, PlayerEntity player) {
        Minecraft.getInstance().setScreen(new GuiDislocator(stack.getHoverName(), player));
    }

    private void handleTeleport(PlayerEntity player, ItemStack stack, TargetPos targetPos, boolean showFuel) {
        int fuel = getFuel(stack);
        if (!player.abilities.instabuild && fuel <= 0) {
            messageUser(player, new TranslationTextComponent("dislocate.draconicevolution.no_fuel").withStyle(TextFormatting.RED));
        } else if (useFuel(stack, player)) {
            dislocateEntity(stack, player, player, targetPos);
            if (showFuel){
                player.displayClientMessage(new TranslationTextComponent("dislocate.draconicevolution.teleport_fuel").append(" " + getFuel(stack)).withStyle(TextFormatting.WHITE), true);
            }
        }
    }

    private void handleBlink(ServerPlayerEntity player, ItemStack stack, boolean showFuel) {
        if (!player.abilities.instabuild) {
            int blinkFuel = stack.getOrCreateTag().getByte("blink_fuel");
            if (blinkFuel <= 0) {
                if (!useFuel(stack, player)) {
                    messageUser(player, new TranslationTextComponent("dislocate.draconicevolution.no_fuel").withStyle(TextFormatting.RED));
                    return;
                }else {
                    blinkFuel = DEConfig.dislocatorBlinksPerPearl;
                }
            }
            blinkFuel--;
            stack.getOrCreateTag().putByte("blink_fuel", (byte) blinkFuel);
            if (showFuel){
                player.displayClientMessage(new TranslationTextComponent("dislocate.draconicevolution.teleport_fuel").append(" " + getFuel(stack)).withStyle(TextFormatting.WHITE), true);
            }
        }

        Vector3d playerVec = player.getEyePosition(1);
        double range = DEConfig.dislocatorBlinkRange;
        Vector3d endVec = playerVec.add(player.getLookAngle().multiply(range, range, range));
        RayTraceContext context = new RayTraceContext(playerVec, endVec, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, player);
        BlockRayTraceResult result = player.level.clip(context);

        player.level.playSound(null, playerVec.x, playerVec.y, playerVec.z, DESounds.blink, SoundCategory.PLAYERS, 1F, 1F);

        DraconicNetwork.sendBlinkEffect(player, (float) (playerVec.distanceTo(endVec) / range));

        if (result.getType() == RayTraceResult.Type.MISS) {
            if (player.isFallFlying()) { //Maintain momentum if elytra flying
                player.connection.teleport(endVec.x, endVec.y, endVec.z, player.yRot, player.xRot, Sets.newHashSet(Flags.X, Flags.Y, Flags.Z));
                player.setYHeadRot(player.yRot);
            } else {
                TeleportUtils.teleportEntity(player, player.level.dimension(), endVec.x, endVec.y, endVec.z);
            }
        } else {
            BlockPos pos = result.getBlockPos().relative(result.getDirection());
            Vec3D vec = new Vec3D(pos).add(0.5, 0, 0.5);
            switch (result.getDirection()) {
                case DOWN:
                case UP:
                    break;
                default:
                    if (player.level.getBlockState(pos.below()).isPathfindable(player.level, pos.below(), PathType.AIR)) {
                        vec.y -= 1;
                    }
            }
            TeleportUtils.teleportEntity(player, player.level.dimension(), vec.x, vec.y, vec.z);
        }

        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), DESounds.blink, SoundCategory.PLAYERS, 1F, 1F);
    }

    @Override
    public DislocatorTarget getTargetPos(ItemStack stack, @Nullable World world) {
        return DataUtils.safeGet(getTargetList(stack), getSelectedIndex(stack));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        DislocatorTarget selected = getTargetPos(stack, world);
        int fuel = getFuel(stack);
        if (selected != null) {
            tooltip.add(new StringTextComponent(selected.getName()).withStyle(TextFormatting.GOLD));
        }
        tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.fuel").append(" " + fuel).withStyle(TextFormatting.WHITE));
        tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.to_open_gui").withStyle(TextFormatting.DARK_PURPLE, TextFormatting.ITALIC));
        tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.scroll_change_select").withStyle(TextFormatting.DARK_PURPLE, TextFormatting.ITALIC));
    }

    @Override
    public void addDisplayData(ItemStack stack, World world, @Nullable BlockPos pos, List<String> displayData) {
        DislocatorTarget location = getTargetPos(stack, world);
        if (location != null) {
            displayData.add(location.getName());
        }
        displayData.add(I18n.get("dislocate.draconicevolution.fuel") + " " + getFuel(stack));
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    public int getFuel(ItemStack stack) {
        return stack.getOrCreateTag().getInt("fuel");
    }

    public void setFuel(ItemStack stack, int value) {
        stack.getOrCreateTag().putInt("fuel", value);
    }

    public boolean useFuel(ItemStack stack, PlayerEntity player) {
        if (player.abilities.instabuild) {
            return true;
        }
        int fuel = getFuel(stack);
        if (fuel > 0) {
            setFuel(stack, fuel - 1);
            return true;
        }
        return false;
    }

    public List<DislocatorTarget> getTargetList(ItemStack stack) {
        ListNBT targets = stack.getOrCreateTag().getList("locations", 10);
        ArrayList<DislocatorTarget> list = new ArrayList<>();
        targets.forEach(inbt -> list.add(new DislocatorTarget((CompoundNBT) inbt)));
        return list;
    }

    public void setTargetList(ItemStack stack, List<DislocatorTarget> targets) {
        ListNBT list = new ListNBT();
        targets.forEach(e -> list.add(e.writeToNBT()));
        stack.getOrCreateTag().put("locations", list);
    }

    public int getSelectedIndex(ItemStack stack) {
        return stack.getOrCreateTag().getInt("selected");
    }

    public void setSelectedIndex(ItemStack stack, int index) {
        stack.getOrCreateTag().putInt("selected", index);
    }

    public boolean getBlinkMode(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("blink");
    }

    public void setBlinkMode(ItemStack stack, boolean blink) {
        stack.getOrCreateTag().putBoolean("blink", blink);
    }


    //Interaction Handling

    public void handleClientAction(ServerPlayerEntity player, ItemStack stack, MCDataInput input) {
        int action = input.readByte();
        int selectIndex = getSelectedIndex(stack);
        LinkedList<DislocatorTarget> list = new LinkedList<>(getTargetList(stack));
        //if (list.size() <= 1 && action != 6 && action != 0) return;
        DislocatorTarget selected = selectIndex >= 0 && selectIndex < list.size() ? list.get(selectIndex) : null;

        switch (action) {
            case 0: //Add Current Pos
                int mode = input.readByte();
                int index = input.readVarInt();
                DislocatorTarget newPoint = new DislocatorTarget(player).setName(input.readString());
                if (mode == 1 || index < 0) { //Add Top
                    list.addFirst(newPoint);
                } else if (mode == 2 || index >= list.size()) { //Add Bottom
                    list.addLast(newPoint);
                } else { //Add bellow selected
                    list.add(index, newPoint);
                }
                selected = newPoint;
                break;
            case 1: //Remove
                DataUtils.safeRemove(list, input.readVarInt());
                break;
            case 2: //Update Name
                DataUtils.ifPresent(list, input.readVarInt(), e -> e.setName(input.readString()));
                break;
            case 3: //Update Lock
                DataUtils.ifPresent(list, input.readVarInt(), e -> e.setLocked(input.readBoolean()));
                break;
            case 4: //Set Selected
                selected = DataUtils.safeGet(list, input.readVarInt());
                break;
            case 5: //Set Blink Mode
                setBlinkMode(stack, input.readBoolean());
                break;
            case 6: //Add Fuel
                addFuel(stack, player, input.readBoolean(), input.readBoolean());
                return;
            case 7: //Update Pos
                DataUtils.ifPresent(list, input.readVarInt(), e -> e.update(player));
                break;
            case 8: //Teleport
                DataUtils.ifPresent(list, input.readVarInt(), e -> handleTeleport(player, stack, e, true));
                break;
            case 9: //Scroll
                break;
            case 10: //Move
                if (list.size() == 0) return;
                if (selected == null) return;
//                boolean up = input.readBoolean();
                int newIndex = input.readVarInt();
                if (newIndex > selectIndex) newIndex--;
                if (newIndex < 0 || newIndex >= list.size()) return;
                list.remove(selected);
                list.add(newIndex, selected);

//                list.set(newIndex, selected);
//                list.set(selectIndex, swapWith);
                break;
            case 11: //Teleport to selected
                if (list.size() == 0) return;
                handleTeleport(player, stack, selected, true);
                break;
            case 12: //Blink
                handleBlink(player, stack, true);
                break;
            case 13: //Select Next / Previous
                if (list.size() == 0) return;
                selected = DataUtils.safeGet(list, Math.floorMod(selectIndex += input.readBoolean() ? 1 : -1, list.size()));
                if (selected != null) {
                    DislocatorTarget up = DataUtils.safeGet(list, Math.floorMod(selectIndex - 1, list.size()));
                    DislocatorTarget down = DataUtils.safeGet(list, Math.floorMod(selectIndex + 1, list.size()));
                    if (up != null) ChatHelper.sendIndexed(player, new StringTextComponent(up.getName()).withStyle(TextFormatting.GRAY), 391);
                    ChatHelper.sendIndexed(player, new StringTextComponent(TextFormatting.GREEN + ">" + TextFormatting.GOLD + selected.getName() + TextFormatting.GREEN + "<"), 392);
                    if (down != null) ChatHelper.sendIndexed(player, new StringTextComponent(down.getName()).withStyle(TextFormatting.GRAY), 393);
                }
                break;
            default:
                return;
        }

        setTargetList(stack, list);
        if (selected != null) {
            if (list.contains(selected)) {
                setSelectedIndex(stack, list.indexOf(selected));
            } else if (selectIndex > 0 && selectIndex - 1 < list.size()) {
                setSelectedIndex(stack, selectIndex - 1);
            }
        } else {
            setSelectedIndex(stack, 0);
        }
    }

    public void addFuel(ItemStack dislocator, PlayerEntity player, boolean fullStack, boolean allStacks) {
        int max = DEConfig.dislocatorMaxFuel - getFuel(dislocator);
        int wanted = allStacks ? max : Math.min(max, fullStack ? 16 : 1);
        int added = 0;
        for (int i = 0; i < player.inventory.getContainerSize() && wanted > 0; i++) {
            ItemStack stack = player.inventory.getItem(i);
            if (Tags.Items.ENDER_PEARLS.contains(stack.getItem())) {
                while (!stack.isEmpty() && wanted > 0) {
                    wanted--;
                    stack.shrink(1);
                    added++;
                }
            }
        }
        setFuel(dislocator, getFuel(dislocator) + added);
    }

    public static ItemStack findDislocator(PlayerEntity player) {
        ItemStack stack = HandHelper.getItem(player, DEContent.dislocator_advanced);
        if (!stack.isEmpty()) {
            return stack;
        }

        stack = EquipmentManager.findItem(DEContent.dislocator_advanced, player);
        if (!stack.isEmpty()) {
            return stack;
        }

        for (int i = 0; i < player.inventory.getContainerSize() - player.inventory.offhand.size(); i++) {
            stack = player.inventory.getItem(i);
            if (stack.getItem() == DEContent.dislocator_advanced) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static class DislocatorTarget extends TargetPos {
        private String name;
        private boolean locked;

        public DislocatorTarget() {}

        public DislocatorTarget(Entity entity) {
            super(entity);
        }

        public DislocatorTarget(CompoundNBT nbt) {
            super(nbt);
        }

        public DislocatorTarget(double x, double y, double z, RegistryKey<World> dimension) {
            super(x, y, z, dimension);
        }

        public DislocatorTarget(double x, double y, double z, RegistryKey<World> dimension, float pitch, float yaw) {
            super(x, y, z, dimension, pitch, yaw);
        }

        public DislocatorTarget setName(String name) {
            this.name = name;
            return this;
        }

        public String getName() {
            return name == null ? "" : name;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }

        public boolean isLocked() {
            return locked;
        }

        @Override
        public CompoundNBT writeToNBT(CompoundNBT nbt) {
            nbt.putString("name", name);
            nbt.putBoolean("lock", locked);
            return super.writeToNBT(nbt);
        }

        @Override
        public void readFromNBT(CompoundNBT nbt) {
            super.readFromNBT(nbt);
            name = nbt.getString("name");
            locked = nbt.getBoolean("lock");
        }

        @Override
        public void write(MCDataOutput output) {
            super.write(output);
            output.writeString(name);
            output.writeBoolean(locked);
        }

        @Override
        public void read(MCDataInput input) {
            super.read(input);
            name = input.readString();
            locked = input.readBoolean();
        }
    }
}
