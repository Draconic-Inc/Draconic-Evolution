package com.brandon3055.draconicevolution.handlers;

import com.brandon3055.brandonscore.handlers.FileHandler;
import com.brandon3055.brandonscore.handlers.IProcess;
import com.brandon3055.brandonscore.handlers.ProcessHandler;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.network.PacketContributor;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.common.base.Charsets;
import com.google.gson.stream.JsonReader;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
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

        ProcessHandler.addProcess(new IProcess() {
            @Override
            public void updateProcess() {
                if (thread.isFinished()) {
                    thread = null;
                    readFile();
                    successfulLoad = true;
                }
                else if (thread.isFailed()) {
                    thread = null;
                }
            }

            @Override
            public boolean isDead() {
                return thread == null;
            }
        });
    }


    public static boolean isPlayerContributor(EntityPlayer player) {
        return contributors.containsKey(player.getName()) && contributors.get(player.getName()).isUserValid(player);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            for (String contribName : ContributorHandler.contributors.keySet()) {
                for (String name : FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames()) {
                    if (name.equals(contribName)) {
                        ContributorHandler.Contributor contributor = ContributorHandler.contributors.get(contribName);
                        DraconicEvolution.network.sendTo(new PacketContributor(contribName, contributor.contributorWingsEnabled, contributor.patreonBadgeEnabled), (EntityPlayerMP) event.player);
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

                    if (name.equals("name")) contributor.name = reader.nextString();
                    else if (name.equals("ign")) contributor.ign = reader.nextString();
                    else if (name.equals("contribution")) contributor.contribution = reader.nextString();
                    else if (name.equals("details")) contributor.details = reader.nextString();
                    else if (name.equals("website")) contributor.website = reader.nextString();
                    else if (name.equals("contributionLevel")) contributor.contributionLevel = reader.nextInt();
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

    public static void tick() {

        if (thread == null) {
            return;
        }

        if (thread.isFinished()) {
            thread = null;
            readFile();
            successfulLoad = true;
        } else if (thread.isFailed()) {
            thread = null;
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
                URL url = new URL("http://www.brandon3055.com/json/DEContributors.json");
                File cFile = new File(FileHandler.brandon3055Folder, "contributors.json");

                InputStream is = url.openStream();
                OutputStream os = new FileOutputStream(cFile);

                IOUtils.copy(is, os);

                is.close();
                os.close();
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

    public static class Contributor {
        public String name;
        public String ign;
        public String contribution;
        public String details;
        public String website;
        public int contributionLevel;
        /**
         * 0=Disabled, 1=Enabled when flying, 2=Always Enabled
         */
        public boolean contributorWingsEnabled = true;
        public boolean patreonBadgeEnabled = true;
        private boolean validated = false;
        private boolean isValid;

        public Contributor() {
        }

        public boolean isUserValid(EntityPlayer player) {
            if (player == null) {
                return false;
            }
            if (!validated) {
                isValid = !UUID.nameUUIDFromBytes(("OfflinePlayer:" + player.getName()).getBytes(Charsets.UTF_8)).equals(player.getUniqueID());
                validated = true;
            }
            return isValid;
        }

        @Override
        public String toString() {
            return "[Contributor: " + name + ", Contribution: " + contribution + ", Details: " + details + ", Website: " + website + "]";
        }
    }
}
