package com.brandon3055.draconicevolution.client.handler;

import com.brandon3055.draconicevolution.client.gui.componentguis.GUIManual;
import com.brandon3055.draconicevolution.client.utill.CustomResourceLocation;
import com.brandon3055.draconicevolution.common.lib.References;
import com.brandon3055.draconicevolution.common.utills.LogHelper;
import com.google.common.io.ByteStreams;
import com.google.gson.stream.JsonWriter;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FilenameUtils;

/**
 * Created by Brandon on 8/02/2015.
 */
public class ResourceHandler {
    public static ResourceHandler instance = new ResourceHandler();
    private static ResourceLocation defaultParticles;
    private static ResourceLocation particles =
            new ResourceLocation(References.RESOURCESPREFIX + "textures/particle/particles.png");
    private static Map<String, ResourceLocation> cachedResources = new HashMap<String, ResourceLocation>();
    public static Map<String, CustomResourceLocation> downloadedImages = new HashMap<String, CustomResourceLocation>();

    private static String savePath;
    private static File saveFolder;
    private static File imagesFolder;
    private static DownloadThread downloadThread;
    public static int downloadStatus = 0;

    // -------------------- File Handling -----------------------//

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (downloadThread != null && downloadThread.isFinished) {
            if (downloadThread.isReloadRequired()) LogHelper.info("Image Download Finished");
            downloadStatus = downloadThread.wasSuccessful ? 1 : 2;
            FMLCommonHandler.instance().bus().unregister(this);
            addRSPack(event != null);
            downloadThread = null;
        }
    }

    public static void init(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(instance);

        if (event != null)
            savePath = event.getModConfigurationDirectory().getParentFile().getAbsolutePath()
                    + "/config/draconicevolution";
        GUIManual.loadPages();

        downloadThread = new DownloadThread(GUIManual.imageURLs);
        downloadThread.start();
    }

    public static class DownloadThread extends Thread {
        private List<String> imageURLs;
        private boolean isFinished = false;
        private boolean wasSuccessful = true;
        private boolean reloadRequired = false;

        public DownloadThread(List<String> imageURLs) {
            this.imageURLs = new ArrayList<String>(imageURLs);
        }

        @Override
        public void run() {

            for (String s : imageURLs) {
                if (!checkExistence(s)) if (downloadImage(s)) reloadRequired = true;
                if (checkExistence(s)) {
                    try {
                        URL url = new URL(s);
                        String fileName = url.getFile();

                        BufferedImage bi = ImageIO.read(new File(getImagesFolder(), FilenameUtils.getName(fileName)));
                        downloadedImages.put(
                                FilenameUtils.getName(fileName),
                                new CustomResourceLocation(
                                        FilenameUtils.getName(fileName), bi.getWidth(), bi.getHeight()));
                    } catch (MalformedURLException e) {
                        LogHelper.error("Image Read Failed");
                        e.printStackTrace();
                    } catch (IOException e) {
                        LogHelper.error("Image Read Failed");
                        e.printStackTrace();
                    }
                }
            }

            isFinished = true;
        }

        private static boolean downloadImage(String urlString) {
            try {
                URL url = new URL(urlString);
                String fileName = url.getFile();

                LogHelper.info("Downloading Image " + FilenameUtils.getName(fileName));

                File dll = new File(getImagesFolder(), FilenameUtils.getName(fileName));

                InputStream is = url.openStream();
                OutputStream os = new FileOutputStream(dll);

                ByteStreams.copy(is, os);

                is.close();
                os.close();

            } catch (IOException e) {
                LogHelper.error("Download Failed");
                e.printStackTrace();
                return false;
            }
            return true;
        }

        private static boolean checkExistence(String urlS) {
            try {
                URL url = new URL(urlS);
                String fileName = url.getFile();
                return Arrays.asList(getImagesFolder().list()).contains(FilenameUtils.getName(fileName));
            } catch (MalformedURLException e) {
                LogHelper.error("Unable to check files existence. Invalid URL: " + urlS);
                e.printStackTrace();
                return false;
            }
        }

        public boolean isFinished() {
            return isFinished;
        }

        public boolean wasSuccessful() {
            return wasSuccessful;
        }

        public boolean isReloadRequired() {
            return reloadRequired;
        }
    }

    private static void addRSPack(boolean refreash) {
        File rspack = new File(getConfigFolder(), "/resources");
        if (!rspack.exists()) return;

        if (!Arrays.asList(rspack.list()).contains("pack.mcmeta")) {
            try {
                JsonWriter writer = new JsonWriter(new FileWriter(new File(rspack, "pack.mcmeta")));
                writer.beginObject();
                writer.name("pack");
                writer.beginObject();
                writer.name("pack_format").value(1);
                writer.name("description").value("Draconic Evolution GUI Images");
                writer.endObject();
                writer.endObject();
                writer.close();
            } catch (IOException e) {
                LogHelper.error("Error creating pack.mcmeta");
                e.printStackTrace();
            }
        }

        Field f = ReflectionHelper.findField(Minecraft.class, "defaultResourcePacks", "field_110449_ao");
        f.setAccessible(true);
        try {
            List defaultResourcePacks = (List) f.get(Minecraft.getMinecraft());
            defaultResourcePacks.add(new FolderResourcePack(rspack));

            f.set(Minecraft.getMinecraft(), defaultResourcePacks);
            LogHelper.info("RS Added");
            if (refreash) Minecraft.getMinecraft().refreshResources();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static File getConfigFolder() {
        if (saveFolder == null) {
            saveFolder = new File(savePath);
        }
        if (!saveFolder.exists()) saveFolder.mkdir();

        return saveFolder;
    }

    public static File getImagesFolder() {
        if (imagesFolder == null) {
            imagesFolder = new File(getConfigFolder(), "/resources/assets/draconicevolution/textures/gui/manualimages");
        }
        if (!imagesFolder.exists()) imagesFolder.mkdirs();

        return imagesFolder;
    }

    // ----------------------------------------------------------//

    public static void bindTexture(ResourceLocation texture) {
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
    }

    /**
     * Binds the vanilla particle sheet
     */
    public static void bindDefaultParticles() {
        if (defaultParticles == null) {
            try {
                defaultParticles = (ResourceLocation) ReflectionHelper.getPrivateValue(
                        EffectRenderer.class, null, "particleTextures", "field_110737_b");
            } catch (Exception e) {
            }
        }
        if (defaultParticles != null) bindTexture(defaultParticles);
    }

    public static void bindParticles() {
        bindTexture(particles);
    }

    public static ResourceLocation getResource(String rs) {
        if (!cachedResources.containsKey(rs))
            cachedResources.put(rs, new ResourceLocation(References.RESOURCESPREFIX + rs));
        return cachedResources.get(rs);
    }

    public static ResourceLocation getResourceWOP(String rs) {
        if (!cachedResources.containsKey(rs)) cachedResources.put(rs, new ResourceLocation(rs));
        return cachedResources.get(rs);
    }

    public static void bindResource(String rs) {
        bindTexture(ResourceHandler.getResource(rs));
    }
}
