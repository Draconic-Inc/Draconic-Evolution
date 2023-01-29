package com.brandon3055.draconicevolution.common.handler;

import java.io.*;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.RenderPlayerEvent;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import com.brandon3055.brandonscore.common.handlers.FileHandler;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.handler.ResourceHandler;
import com.brandon3055.draconicevolution.client.model.special.ModelContributorWings;
import com.brandon3055.draconicevolution.common.network.ContributorPacket;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import com.google.common.base.Charsets;
import com.google.gson.stream.JsonReader;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.gameevent.PlayerEvent;

/**
 * Created by brandon3055 on 5/11/2015.
 */
public class ContributorHandler {

    public static Map<String, Contributor> contributors = new LinkedHashMap<String, Contributor>();
    public static boolean successfulLoad = false;
    private static DLThread thread;
    private static ModelContributorWings wings = new ModelContributorWings();

    public static void init() {
        thread = new DLThread();
        thread.start();
    }

    public static void render(RenderPlayerEvent.Specials event) {
        if (isPlayerContributor(event.entityPlayer)) {
            Contributor contributor = contributors.get(event.entityPlayer.getCommandSenderName());

            if (contributor.contributionLevel >= 1 && (contributor.contributorWingsEnabled)) renderWings(event);
            if (contributor.contribution != null && contributor.contribution.toLowerCase().contains("patreon")
                    && contributor.patreonBadgeEnabled)
                renderBadge(event);
        }
    }

    public static boolean isPlayerContributor(EntityPlayer player) {
        return contributors.containsKey(player.getCommandSenderName())
                && contributors.get(player.getCommandSenderName()).isUserValid(player);
    }

    private static void renderWings(RenderPlayerEvent event) {
        ResourceHandler.bindResource("textures/models/ContributorWings.png");
        GL11.glColor4f(1F, 1F, 1F, 1F);
        wings.render(event.entityPlayer, 0F, 0F, 0F, 0F, event.partialRenderTick, 1F / 16F);
    }

    private static void renderBadge(RenderPlayerEvent event) {
        ResourceHandler.bindResource("textures/special/PatreonBadge.png");
        Tessellator tess = Tessellator.instance;
        GL11.glPushMatrix();

        if (event.entityPlayer.isSneaking()) {
            GL11.glRotatef(29.0F, 1.0F, 0.0F, 0F);
        }

        double x = 0.01;
        double y = 0.04;
        double z = -0.13;
        double xSize = 0.22;
        double ySize = 0.22;

        tess.startDrawingQuads();
        tess.setColorRGBA_F(1F, 1F, 1F, 1F);
        tess.addVertexWithUV(x, y, z, 0, 0);
        tess.addVertexWithUV(x, y + ySize, z, 0, 1);
        tess.addVertexWithUV(x + xSize, y + ySize, z, 1, 1);
        tess.addVertexWithUV(x + xSize, y, z, 1, 0);
        tess.draw();

        GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        ResourceHandler.bindTexture(ResourceHandler.getResourceWOP("textures/misc/enchanted_item_glint.png"));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
        float f11 = 0.76F;
        GL11.glColor4f(0.9F * f11, 0.8F * f11, 0.1F * f11, 1.0F);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        GL11.glPushMatrix();

        float f12 = 0.125F;
        GL11.glScalef(f12, f12, f12);
        float f13 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
        GL11.glTranslatef(f13, 0.0F, 0.0F);
        GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);

        tess.startDrawingQuads();
        tess.addVertexWithUV(x, y, z, 0, 0);
        tess.addVertexWithUV(x, y + ySize, z, 0, 1);
        tess.addVertexWithUV(x + xSize, y + ySize, z, 1, 1);
        tess.addVertexWithUV(x + xSize, y, z, 1, 0);
        tess.draw();

        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glScalef(f12, f12, f12);
        f13 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
        GL11.glTranslatef(-f13, 0.0F, 0.0F);
        GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);

        tess.startDrawingQuads();
        tess.addVertexWithUV(x, y, z, 0, 0);
        tess.addVertexWithUV(x, y + ySize, z, 0, 1);
        tess.addVertexWithUV(x + xSize, y + ySize, z, 1, 1);
        tess.addVertexWithUV(x + xSize, y, z, 1, 0);
        tess.draw();

        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glPopMatrix();
    }

    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            for (String contribName : ContributorHandler.contributors.keySet()) {
                for (String name : FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames()) {
                    if (name.equals(contribName)) {
                        ContributorHandler.Contributor contributor = ContributorHandler.contributors.get(contribName);
                        DraconicEvolution.network.sendTo(
                                new ContributorPacket(
                                        contribName,
                                        contributor.contributorWingsEnabled,
                                        contributor.patreonBadgeEnabled),
                                (EntityPlayerMP) event.player);
                    }
                }
            }
        }
    }

    // region Reading online contributors list
    private static void readFile() {
        File cFile = new File(FileHandler.configFolder, "/draconicevolution/contributors.json");

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
        } catch (Exception e) {
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
                File cFile = new File(FileHandler.configFolder, "/draconicevolution/contributors.json");

                InputStream is = url.openStream();
                OutputStream os = new FileOutputStream(cFile);

                IOUtils.copy(is, os);

                is.close();
                os.close();
                finished = true;
            } catch (Exception e) {
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
    // endregion

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

        public Contributor() {}

        public boolean isUserValid(EntityPlayer player) {
            if (!validated) {
                isValid = !UUID
                        .nameUUIDFromBytes(("OfflinePlayer:" + player.getCommandSenderName()).getBytes(Charsets.UTF_8))
                        .equals(player.getUniqueID());
            }
            return isValid;
        }

        @Override
        public String toString() {
            return "[Contributor: " + name
                    + ", Contribution: "
                    + contribution
                    + ", Details: "
                    + details
                    + ", Website: "
                    + website
                    + "]";
        }
    }
}
