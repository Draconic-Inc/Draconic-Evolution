package com.brandon3055.draconicevolution.handlers;

import com.brandon3055.brandonscore.BrandonsCore;
import com.brandon3055.brandonscore.client.ProcessHandlerClient;
import com.brandon3055.brandonscore.handlers.FileHandler;
import com.brandon3055.brandonscore.handlers.IProcess;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.base.Charsets;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by brandon3055 on 5/11/2015.
 */
public class ContributorHandler {

    public static Map<String, Contributor> contributors = new LinkedHashMap<>();
    public static boolean successfulLoad = false;
    private static DLThread thread;

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new ContributorHandler());
        thread = new DLThread();
        thread.start();

        IProcess process = new IProcess() {
            @Override
            public void updateProcess() {
                if (thread.isFinished()) {
                    thread = null;
                    readFile();
                    successfulLoad = true;
                    loadContributorConfig();
                    LogHelper.dev("Read Contributors File");
                }
                else if (thread.isFailed()) {
                    thread = null;
                    LogHelper.dev("Contributors File Download Failed");
                }
            }

            @Override
            public boolean isDead() {
                return thread == null;
            }
        };

        if (EffectiveSide.get().isClient()) {
            ProcessHandlerClient.addProcess(process);
        }
        else {
            ProcessHandler.addProcess(process);
        }
    }

    public static boolean isPlayerContributor(PlayerEntity player) {
        return contributors.containsKey(player.getName()) && contributors.get(player.getName()).isUserValid(player);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            for (String contribName : ContributorHandler.contributors.keySet()) {
                for (String name : BrandonsCore.proxy.getMCServer().getPlayerNames()) {
                    if (name.equals(contribName)) {
                        ContributorHandler.Contributor contributor = ContributorHandler.contributors.get(contribName);
                        //Packet Stuff
//                        DraconicEvolution.network.sendTo(new PacketContributor(contribName, contributor.contributorWingsEnabled, contributor.patreonBadgeEnabled, contributor.lolnetBadgeEnabled), (ServerPlayerEntity) event.player);
                    }
                }
            }
        }
    }

    //region Reading online contributors list
    private static void readFile() {
        File cFile = new File(FileHandler.brandon3055Folder, "contributors.json");

        if (!cFile.exists()) {
            LogHelper.error("Could not find contributors file");
            return;
        }

        try {
            JsonReader reader = new JsonReader(new FileReader(cFile));
            reader.setLenient(true);

            reader.beginArray();

            while (reader.hasNext()) {
                reader.beginObject();

                Contributor contributor = new Contributor();

                while (reader.hasNext()) {
                    String name = reader.nextName();

                    if (name.equals("name")) {
                        contributor.name = reader.nextString();
                    }
                    else if (name.equals("ign")) {
                        contributor.ign = reader.nextString();
                    }
                    else if (name.equals("contribution")) {
                        contributor.setContribution(reader.nextString());
                    }
                    else if (name.equals("details")) {
                        contributor.setDetails(reader.nextString());
                    }
                    else if (name.equals("website")) {
                        contributor.website = reader.nextString();
                    }
                    else if (name.equals("contributionLevel")) {
                        contributor.setContributionLevel(reader.nextInt());
                    }
                }

                contributors.put(contributor.ign, contributor);

                reader.endObject();
            }

            reader.endArray();

            reader.close();
            cFile.delete();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class DLThread extends Thread {

        private boolean finished = false;
        private boolean failed = false;

        public DLThread() {
            super("DE Contributors DL Thread");
        }

        @Override
        public void run() {
            super.run();

            try {
                File cFile = new File(FileHandler.brandon3055Folder, "contributors.json");
                FileHandler.downloadFile("http://www.brandon3055.com/json/DEContributors.json", cFile);
                finished = true;
            }
            catch (Exception e) {
                LogHelper.error("Failed to download contributors list");
                failed = true;
                e.printStackTrace();
            }

        }

        public boolean isFinished() {
            return finished;
        }

        public boolean isFailed() {
            return failed;
        }
    }
    //endregion

    public static void loadContributorConfig() {
        try {
            File file = new File(FileHandler.brandon3055Folder, "contributor_settings.json");
            if (!file.exists()) {
                return;
            }

            JsonReader reader = new JsonReader(new FileReader(file));
            reader.beginObject();

            while (reader.hasNext()) {
                String name = reader.nextName();
                reader.beginArray();
                boolean wings = reader.nextBoolean();
                boolean badge = reader.nextBoolean();
                boolean lbadge = reader.nextBoolean();

                if (contributors.containsKey(name)) {
                    contributors.get(name).contributorWingsEnabled = wings;
                    contributors.get(name).patreonBadgeEnabled = badge;
                    contributors.get(name).lolnetBadgeEnabled = lbadge;
                }

                reader.endArray();
            }

            reader.endObject();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveContributorConfig() {
        try {
            File file = new File(FileHandler.brandon3055Folder, "contributor_settings.json");
            if (!file.exists()) {
                file.createNewFile();
            }

            JsonWriter writer = new JsonWriter(new FileWriter(file));
            writer.beginObject();

            for (String name : contributors.keySet()) {
                writer.name(name);
                writer.beginArray();
                writer.value(contributors.get(name).contributorWingsEnabled);
                writer.value(contributors.get(name).patreonBadgeEnabled);
                writer.value(contributors.get(name).lolnetBadgeEnabled);
                writer.endArray();
            }

            writer.endObject();
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Contributor {
        public String name;
        public String ign;
        public String contribution = "";
        public String details = "";
        public String website;
        private int contributionLevel;
        public boolean contributorWingsEnabled = true;
        public boolean patreonBadgeEnabled = true;
        private boolean validated = false;
        private boolean isValid;

        public boolean hasWings = false;
        public boolean isPatreonSupporter = false;

        /**
         * As a big supporter of the lolnet.co.nz Minecraft Community i have offered to support
         * their upcoming donation drive but offering lolnet badges and temporary contributor wings to everyone who donates.
         */
        public boolean lolnetBadgeEnabled = true;
        public boolean isLolnetContributor = false;

        public Contributor() {
        }

        public boolean isUserValid(PlayerEntity player) {
            if (player == null) {
                return false;
            }
            if (!validated) {
                isValid = !UUID.nameUUIDFromBytes(("OfflinePlayer:" + player.getName()).getBytes(Charsets.UTF_8)).equals(player.getUUID());
                validated = true;
            }
            return isValid;
        }

        public void setContribution(String contribution) {
            this.contribution = contribution;

            if (contribution.toLowerCase(Locale.ENGLISH).contains("lolnet")) {
                isLolnetContributor = true;
            }
            if (contribution.toLowerCase(Locale.ENGLISH).contains("patreon")) {
                isPatreonSupporter = true;
            }
        }

        public void setDetails(String details) {
            this.details = details;
            if (details.toLowerCase(Locale.ENGLISH).contains("lolnet")) {
                isLolnetContributor = true;
            }
        }

        public void setContributionLevel(int contributionLevel) {
            this.contributionLevel = contributionLevel;
            if (contributionLevel >= 1) {
                hasWings = true;
            }
        }

        @Override
        public String toString() {
            return "[Contributor: " + name + ", Contribution: " + contribution + ", Details: " + details + ", Website: " + website + "]";
        }
    }
}
