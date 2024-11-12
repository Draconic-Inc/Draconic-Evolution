package com.brandon3055.draconicevolution.client.gui.modular.itemconfig;

import com.brandon3055.draconicevolution.DEConfig;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by brandon3055 on 17/5/20.
 */
public class ItemConfigDataHandler {

    public static CompoundTag retrieveData() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hasSingleplayerServer()) {
            ServerLevel world = mc.getSingleplayerServer().getLevel(Level.OVERWORLD);
            SinglePlayerWorldData data = world.getDataStorage().computeIfAbsent(new SavedData.Factory<>(SinglePlayerWorldData::new, SinglePlayerWorldData::load), SinglePlayerWorldData.FILE_NAME);
            return data.data;
        } else {
            Path file = Paths.get("./config/brandon3055/servers/" + DEConfig.serverID + ".dat");
            if (Files.exists(file)) {
                try (InputStream is = Files.newInputStream(file)) {
                    return NbtIo.readCompressed(is, NbtAccounter.create(0x6400000L));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new CompoundTag();
    }

    public static void saveData(CompoundTag nbt) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hasSingleplayerServer()) {
            ServerLevel world = mc.getSingleplayerServer().getLevel(Level.OVERWORLD);
            SinglePlayerWorldData data = world.getDataStorage().computeIfAbsent(new SavedData.Factory<>(SinglePlayerWorldData::new, SinglePlayerWorldData::load), SinglePlayerWorldData.FILE_NAME);
            data.data = nbt;
            data.setDirty();
        } else {
            Path file = Paths.get("./config/brandon3055/servers/" + DEConfig.serverID + ".dat");
            if (Files.notExists(file)) {
                SneakyUtils.sneaky(() -> Files.createDirectories(file.getParent()));
            } else {
                SneakyUtils.sneaky(() -> Files.delete(file));
            }

            try (OutputStream os = Files.newOutputStream(file, StandardOpenOption.CREATE)) {
                NbtIo.writeCompressed(nbt, os);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static class SinglePlayerWorldData extends SavedData {
        private static String FILE_NAME = "draconic_item_config";
        private CompoundTag data;

        public SinglePlayerWorldData() {
            data = new CompoundTag();
        }

        SinglePlayerWorldData(CompoundTag data) {
            this.data = data;
        }

        public static SinglePlayerWorldData load(CompoundTag nbt) {
            return new SinglePlayerWorldData(nbt);
        }

        @Override
        public CompoundTag save(CompoundTag compound) {
            return data;
        }
    }

}
