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
import com.brandon3055.draconicevolution.client.gui.DislocatorGui;
import com.brandon3055.draconicevolution.handlers.DESounds;
import com.brandon3055.draconicevolution.init.DEContent;
import com.brandon3055.draconicevolution.integration.equipment.EquipmentManager;
import com.brandon3055.draconicevolution.network.DraconicNetwork;
import com.google.common.collect.Sets;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by brandon3055 on 16/07/2016.
 */
public class DislocatorAdvanced extends Dislocator {
    public static final UUID MSG_ID1 = UUID.fromString("1a0f4ae8-7884-4402-b223-d93f221466e3");
    public static final UUID MSG_ID2 = UUID.fromString("bb5bf7be-5fbb-44b1-b7bc-35ac89b2890c");
    public static final UUID MSG_ID3 = UUID.fromString("d9349a0c-6013-43ea-9be2-04686eb8bd2d");

    public DislocatorAdvanced(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canBeHurtBy(DamageSource source) {
        return source.is(DamageTypes.FELL_OUT_OF_WORLD);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.getAge() >= 0) {
            entity.setExtendedLifetime();
        }
        return super.onEntityItemUpdate(stack, entity);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (player.level().isClientSide) {
            return true;
        }
        TargetPos location = getTargetPos(stack, player.level());
        int fuel = getFuel(stack);

        if (!player.getAbilities().instabuild && fuel <= 0) {
            messageUser(player, Component.translatable("dislocate.draconicevolution.no_fuel").withStyle(ChatFormatting.RED));
            return true;
        }

        if (entity instanceof Player) {
            if (entity.isShiftKeyDown()) {
                if (useFuel(stack, player)) {
                    dislocateEntity(stack, player, entity, location);
                }
            } else {
                messageUser(player, Component.translatable("dislocate.draconicevolution.player_allow"));
            }
            return true;
        }

        if (!entity.canChangeDimensions() || !(entity instanceof LivingEntity)) {
            return true;
        }

        if (useFuel(stack, player)) {
            dislocateEntity(stack, player, entity, location);
            messageUser(player, Component.literal(I18n.get("dislocate.draconicevolution.entity_sent_to") + " " + location.getReadableName(false)).withStyle(ChatFormatting.GREEN));
        }

        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        TargetPos location = getTargetPos(stack, player.level());

        boolean blink = getBlinkMode(stack);
        if (player.isShiftKeyDown() || (location == null && !blink)) {
            if (world.isClientSide) {
                openGui(stack, player);
            }
        } else if (!world.isClientSide) {
            if (blink) {
                handleBlink((ServerPlayer) player, stack, false);
            } else {
                handleTeleport(player, stack, location, false);
            }
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    @OnlyIn (Dist.CLIENT)
    private void openGui(ItemStack stack, Player player) {
        Minecraft.getInstance().setScreen(new DislocatorGui.Screen(stack.getHoverName(), player));
    }

    private void handleTeleport(Player player, ItemStack stack, TargetPos targetPos, boolean showFuel) {
        int fuel = getFuel(stack);
        if (!player.getAbilities().instabuild && fuel <= 0) {
            messageUser(player, Component.translatable("dislocate.draconicevolution.no_fuel").withStyle(ChatFormatting.RED));
        } else if (useFuel(stack, player)) {
            dislocateEntity(stack, player, player, targetPos);
            if (showFuel) {
                player.displayClientMessage(Component.translatable("dislocate.draconicevolution.teleport_fuel").append(" " + getFuel(stack)).withStyle(ChatFormatting.WHITE), true);
            }
        }
    }

    private void handleBlink(ServerPlayer player, ItemStack stack, boolean showFuel) {
        if (!player.getAbilities().instabuild) {
            if (player.getCooldowns().isOnCooldown(stack.getItem())) {
                return;
            }
            int blinkFuel = stack.getOrCreateTag().getByte("blink_fuel");
            if (blinkFuel <= 0) {
                if (!useFuel(stack, player)) {
                    messageUser(player, Component.translatable("dislocate.draconicevolution.no_fuel").withStyle(ChatFormatting.RED));
                    return;
                } else {
                    blinkFuel = DEConfig.dislocatorBlinksPerPearl;
                }
            }

            blinkFuel--;
            stack.getOrCreateTag().putByte("blink_fuel", (byte) blinkFuel);
            if (showFuel) {
                player.displayClientMessage(Component.translatable("dislocate.draconicevolution.teleport_fuel").append(" " + getFuel(stack)).withStyle(ChatFormatting.WHITE), true);
            }
            player.getCooldowns().addCooldown(stack.getItem(), 5);
        }

        Vec3 playerVec = player.getEyePosition(1);
        double range = DEConfig.dislocatorBlinkRange;
        Vec3 endVec = playerVec.add(player.getLookAngle().multiply(range, range, range));
        ClipContext context = new ClipContext(playerVec, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
        BlockHitResult result = player.level().clip(context);

        player.level().playSound(null, playerVec.x, playerVec.y, playerVec.z, DESounds.BLINK.get(), SoundSource.PLAYERS, 1F, 1F);

        DraconicNetwork.sendBlinkEffect(player, (float) (playerVec.distanceTo(endVec) / range));

        if (result.getType() == HitResult.Type.MISS) {
            if (player.isFallFlying()) { //Maintain momentum if elytra flying
                player.connection.teleport(endVec.x, endVec.y, endVec.z, player.getYRot(), player.getXRot(), Sets.newHashSet(RelativeMovement.X, RelativeMovement.Y, RelativeMovement.Z));
                player.setYHeadRot(player.getYRot());
            } else {
                TeleportUtils.teleportEntity(player, player.level().dimension(), endVec.x, endVec.y, endVec.z);
            }
        } else {
            BlockPos pos = result.getBlockPos().relative(result.getDirection());
            Vec3D vec = new Vec3D(pos).add(0.5, 0, 0.5);
            switch (result.getDirection()) {
                case DOWN:
                case UP:
                    break;
                default:
                    if (player.level().getBlockState(pos.below()).isPathfindable(player.level(), pos.below(), PathComputationType.AIR)) {
                        vec.y -= 1;
                    }
            }
            TeleportUtils.teleportEntity(player, player.level().dimension(), vec.x, vec.y, vec.z);
        }

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), DESounds.BLINK.get(), SoundSource.PLAYERS, 1F, 1F);

    }

    @Override
    public DislocatorTarget getTargetPos(ItemStack stack, @Nullable Level world) {
        return DataUtils.safeGet(getTargetList(stack), getSelectedIndex(stack));
    }

    @OnlyIn (Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flagIn) {
        DislocatorTarget selected = getTargetPos(stack, world);
        int fuel = getFuel(stack);
        if (selected != null) {
            tooltip.add(Component.literal(selected.getName()).withStyle(ChatFormatting.GOLD));
        }
        tooltip.add(Component.translatable("dislocate.draconicevolution.fuel").append(" " + fuel).withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("dislocate.draconicevolution.to_open_gui").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
//        tooltip.add(new TranslationTextComponent("dislocate.draconicevolution.scroll_change_select").withStyle(TextFormatting.DARK_PURPLE, TextFormatting.ITALIC));
    }

    @Override
    public void generateHudText(ItemStack stack, Player player, List<Component> displayList) {
        DislocatorTarget location = getTargetPos(stack, player.level());
        if (location != null) {
            displayList.add(Component.literal(location.getName()));
        }
        displayList.add(Component.translatable("dislocate.draconicevolution.fuel").append(" " + getFuel(stack)));
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

    public boolean useFuel(ItemStack stack, Player player) {
        if (player.getAbilities().instabuild) {
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
        ListTag targets = stack.getOrCreateTag().getList("locations", 10);
        ArrayList<DislocatorTarget> list = new ArrayList<>();
        targets.forEach(inbt -> list.add(new DislocatorTarget((CompoundTag) inbt)));
        return list;
    }

    public void setTargetList(ItemStack stack, List<DislocatorTarget> targets) {
        ListTag list = new ListTag();
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

    public void handleClientAction(ServerPlayer player, ItemStack stack, MCDataInput input) {
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
                    if (up != null) ChatHelper.sendIndexed(player, Component.literal(up.getName()).withStyle(ChatFormatting.GRAY), MSG_ID1);
                    ChatHelper.sendIndexed(player, Component.literal(ChatFormatting.GREEN + ">" + ChatFormatting.GOLD + selected.getName() + ChatFormatting.GREEN + "<"), MSG_ID2);
                    if (down != null) ChatHelper.sendIndexed(player, Component.literal(down.getName()).withStyle(ChatFormatting.GRAY), MSG_ID3);
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

    public void addFuel(ItemStack dislocator, Player player, boolean fullStack, boolean allStacks) {
        int max = DEConfig.dislocatorMaxFuel - getFuel(dislocator);
        int wanted = allStacks ? max : Math.min(max, fullStack ? 16 : 1);
        int added = 0;
        for (int i = 0; i < player.getInventory().getContainerSize() && wanted > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Tags.Items.ENDER_PEARLS)) {
                while (!stack.isEmpty() && wanted > 0) {
                    wanted--;
                    stack.shrink(1);
                    added++;
                }
            }
        }
        setFuel(dislocator, getFuel(dislocator) + added);
    }

    public static ItemStack findDislocator(Player player) {
        ItemStack stack = HandHelper.getItem(player, DEContent.DISLOCATOR_ADVANCED.get());
        if (!stack.isEmpty()) {
            return stack;
        }

        stack = EquipmentManager.findItem(DEContent.DISLOCATOR_ADVANCED.get(), player);
        if (!stack.isEmpty()) {
            return stack;
        }

        for (int i = 0; i < player.getInventory().getContainerSize() - player.getInventory().offhand.size(); i++) {
            stack = player.getInventory().getItem(i);
            if (stack.getItem() == DEContent.DISLOCATOR_ADVANCED.get()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static class DislocatorTarget extends TargetPos {
        private String name;
        private boolean locked;

        public DislocatorTarget() {
        }

        public DislocatorTarget(Entity entity) {
            super(entity);
        }

        public DislocatorTarget(CompoundTag nbt) {
            super(nbt);
        }

        public DislocatorTarget(double x, double y, double z, ResourceKey<Level> dimension) {
            super(x, y, z, dimension);
        }

        public DislocatorTarget(double x, double y, double z, ResourceKey<Level> dimension, float pitch, float yaw) {
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
        public CompoundTag writeToNBT(CompoundTag nbt) {
            nbt.putString("name", name);
            nbt.putBoolean("lock", locked);
            return super.writeToNBT(nbt);
        }

        @Override
        public void readFromNBT(CompoundTag nbt) {
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
