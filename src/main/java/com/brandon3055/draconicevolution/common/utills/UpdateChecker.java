package com.brandon3055.draconicevolution.common.utills;

import com.brandon3055.draconicevolution.common.lib.VersionHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;

/**
 * Created by Brandon on 24/02/2015.
 */
public class UpdateChecker {
    private final UpdateCheckThread thread;
    private int delay = 300;
    private boolean playerNotified = false;

    public UpdateChecker() {
        thread = new UpdateCheckThread();
        thread.start();
    }

    @SuppressWarnings({"unused"})
    @SubscribeEvent
    public void tickStart(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (delay > 0) {
            delay--;
            return;
        }

        if (!playerNotified && thread.isComplete()) {
            playerNotified = true;
            FMLCommonHandler.instance().bus().unregister(this);

            if (!thread.getVersion().equals(VersionHandler.VERSION)
                    || (VersionHandler.SNAPSHOT > 0 && thread.getSnapshot() == 0)) {
                event.player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE
                        + "[Draconic Evolution]" + EnumChatFormatting.RESET + " New version available:"));
                event.player.addChatComponentMessage(
                        new ChatComponentText(EnumChatFormatting.GREEN + "Draconic Evolution v" + thread.getVersion()));
                if (!StringUtils.isNullOrEmpty(thread.getNote())) {
                    event.player.addChatComponentMessage(new ChatComponentText(thread.getNote()));
                }
            } else if (thread.getSnapshot() > VersionHandler.SNAPSHOT) {
                event.player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE
                        + "[Draconic Evolution]" + EnumChatFormatting.RESET + " New snapshot version available:"));
                event.player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.BLUE
                        + "Draconic Evolution v" + thread.getVersion() + "-snapshot_" + thread.getSnapshot()));
                if (!StringUtils.isNullOrEmpty(thread.getNote())) {
                    event.player.addChatComponentMessage(new ChatComponentText(thread.getNote()));
                }
            }

        } else if (thread.isFailed()) {
            playerNotified = true;
            FMLCommonHandler.instance().bus().unregister(this);

            event.player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.DARK_PURPLE
                    + "[Draconic Evolution]" + EnumChatFormatting.RED + " Version check failed"));
            if (!StringUtils.isNullOrEmpty(thread.getNote()))
                event.player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + thread.getNote()));
        }
    }

    public class UpdateCheckThread extends Thread {
        private String version = null;
        private int snapshot = -1;
        private String note = null;
        private boolean complete = false;
        private boolean failed = false;

        @Override
        public void run() {
            LogHelper.info("[Update Checker] Thread Started");
            try {
                URL versionURL =
                        new URL("https://raw.githubusercontent.com/brandon3055/Draconic-Evolution/master/VERSION.txt");

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((versionURL).openStream()));

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains(":")) {
                        String value = line.substring(line.indexOf(":") + 1);
                        if (line.contains("Version")) version = value;
                        else if (line.contains("Snapshot")) snapshot = Integer.parseInt(value);
                        else if (line.contains("ReleaseNote")) note = value;
                    }
                }

                if (version != null && snapshot >= 0) complete = true;
                else {
                    note = "[Invalid Response]";
                    failed = true;
                }

                LogHelper.info("[Update Checker] Thread Finished");
                //				if (complete)
                //				{
                //					if (!getVersion().equals(VersionHandler.VERSION) || (VersionHandler.SNAPSHOT > 0 && getSnapshot()
                // == 0))
                //					{
                //						LogHelper.info("###############################");
                //						LogHelper.info("New version available:");
                //						LogHelper.info("Draconic Evolution v" + thread.getVersion());
                //						if (!StringUtils.isNullOrEmpty(thread.getNote())) LogHelper.info(thread.getNote());
                //						LogHelper.info("###############################");
                //					}
                //					else if (getSnapshot() > VersionHandler.SNAPSHOT)
                //					{
                //						LogHelper.info("###############################");
                //						LogHelper.info("New snapshot version available:");
                //						LogHelper.info("Draconic Evolution v" + thread.getVersion() + "-snapshot_" +
                // thread.getSnapshot());
                //						if (!StringUtils.isNullOrEmpty(thread.getNote())) LogHelper.info(thread.getNote());
                //						LogHelper.info("###############################");
                //					}
                //				}
            } catch (Exception e) {
                LogHelper.info("[Update Checker] Check Failed");
                failed = true;
                note = e.getClass().toString();
                e.printStackTrace();
            }
        }

        public String getVersion() {
            return version;
        }

        public int getSnapshot() {
            return snapshot;
        }

        public String getNote() {
            return note;
        }

        public boolean isComplete() {
            return complete;
        }

        public boolean isFailed() {
            return failed;
        }
    }
}
