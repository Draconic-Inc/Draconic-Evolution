package com.brandon3055.draconicevolution.client.gui.modwiki.moddata;

import com.brandon3055.brandonscore.client.ProcessHandlerClient;
import com.brandon3055.brandonscore.handlers.IProcess;
import com.brandon3055.draconicevolution.utils.LogHelper;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Created by brandon3055 on 20/09/2016.
 */
public class WikiDownloadManager {

    public static List<ManifestEntry> manifest = new ArrayList<>();
    public static List<DownloadThread> dlThreads = Collections.synchronizedList(new ArrayList<DownloadThread>());
    public static DLMonitor monitor = null;

    public static void downloadManifest() {
        LogHelper.info("Downloading Project Intelligence Mod Manifest...");
        DownloadThread thread = new DownloadThread("https://raw.githubusercontent.com/brandon3055/Project-Intelligence-Docs/master/ModDocs/manifest.json", WikiDocManager.wikiFolder.getAbsolutePath() + "\\manifest.json");
        thread.setDaemon(true);
        thread.start();
        ProcessHandlerClient.addProcess(new ManifestDLMonitor(thread));
    }

    public static void downloadDocs() {
        LogHelper.info("Checking Doc Versions");
        File modDocs = new File(WikiDocManager.wikiFolder, "ModDocs");

        if (!modDocs.exists() && !modDocs.mkdirs()) {
            LogHelper.error("Can not download mod documentation because could not create the ModDocs folder: " + modDocs);
            return;
        }

        for (ManifestEntry entry : manifest) {
            File mod = new File(modDocs, entry.modName);
            if (!mod.exists() && !mod.mkdirs()) {
                LogHelper.error("Can not download mod documentation because could not create the mod folder: " + mod);
                return;
            }

            LogHelper.dev("Checking Manifest: " +manifest);

            for (String url : entry.downloadToVersion.keySet()) {
                try {
                    String lang = url.substring(url.lastIndexOf("-") + 1).replace(".xml", "");

                    if (WikiDocManager.modDocMap.containsKey(entry.modid) && WikiDocManager.modDocMap.get(entry.modid).langToVersion.containsKey(lang)) {
                        LogHelper.dev("Version : " + WikiDocManager.modDocMap.get(entry.modid).langToVersion.get(lang));
                    }

                    if (WikiDocManager.modDocMap.containsKey(entry.modid) && WikiDocManager.modDocMap.get(entry.modid).langToVersion.containsKey(lang) && WikiDocManager.modDocMap.get(entry.modid).langToVersion.get(lang) >= entry.downloadToVersion.get(url)) {
                        LogHelper.dev(entry.modName+": Current Doc Version Up To Date.");
                        continue;
                    }
                    LogHelper.info("Found Doc update for %s downloading updated documentation.", entry.modName);

                    DownloadThread thread = new DownloadThread(url, new File(mod, entry.modid + "-" + lang + ".xml").getAbsolutePath());
                    thread.start();
                    dlThreads.add(thread);

                    if (monitor == null) {
                        monitor = new DLMonitor();
                        ProcessHandlerClient.addProcess(monitor);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class DLMonitor implements IProcess {
        public boolean isDead = false;

        @Override
        public void updateProcess() {
            boolean foundActive = false;

            for (DownloadThread thread : dlThreads) {
                if (thread.failed) {
                    LogHelper.dev("Download Failed: " + thread.url);
                    thread.failed = false;
                }
                else if (!thread.finished) {
                    foundActive = true;
                }
            }

            if (!foundActive) {
                isDead = true;
                monitor = null;
                LogHelper.dev("All " + dlThreads.size() + " Downloads Complete.");
                dlThreads.clear();
                WikiDocManager.reload(true, true, true);
            }
        }

        @Override
        public boolean isDead() {
            return isDead;
        }
    }

    public static class ManifestDLMonitor implements IProcess {
        private DownloadThread thread;
        private boolean isDead = false;

        public ManifestDLMonitor(DownloadThread thread) {
            this.thread = thread;
        }

        @Override
        public void updateProcess() {
            if (thread.failed) {
                LogHelper.error("Failed to download mod manifest.");
                isDead = true;
            }
            else if (thread.finished) {
                try {
                    JsonReader reader = new JsonReader(new FileReader(thread.file));
                    manifest.clear();
                    reader.beginArray();

                    while (reader.hasNext()) {
                        reader.beginObject();
                        String modName = "";
                        String modId = "";
                        Map<String, Integer> map = new HashMap<>();

                        while (reader.hasNext()) {
                            String name = reader.nextName();
                            switch (name) {
                                case "name":
                                    modName = reader.nextString();
                                    break;
                                case "modid":
                                    modId = reader.nextString();
                                    break;
                                case "downloads":
                                    reader.beginObject();

                                    while (reader.hasNext()) {
                                        String url = reader.nextName();
                                        int revision = reader.nextInt();
                                        map.put(url, revision);
                                    }

                                    reader.endObject();
                                    break;
                            }
                        }

                        manifest.add(new ManifestEntry(modName, modId, map));
                        reader.endObject();
                    }

                    reader.endArray();

                    downloadDocs();
                }
                catch (Exception e) {
                    LogHelper.error("Failed to load manifest!");
                    e.printStackTrace();
                }
                finally {
                    isDead = true;
                }
            }
        }

        @Override
        public boolean isDead() {
            return isDead;
        }
    }

    public static class DownloadThread extends Thread {
        public final String url;
        public final String file;
        public volatile boolean finished = false;
        public volatile boolean failed = false;
        public volatile double progress = 0;

        public DownloadThread(String url, String file) {
            super("Wiki Download Thread");
            this.url = url;
            this.file = file;
            LogHelper.dev(String.format("Download: %s -> %s", url, file));
            DLWatcher watcher = new DLWatcher(this);
            watcher.setDaemon(true);
            watcher.start();
        }

        @Override
        public void run() {
            try {
                URL url = new URL(this.url);
                File outputFile = new File(file);

                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }

                LogHelper.dev("Got URL: " + url);

                InputStream is = url.openStream();
                OutputStream os = new FileOutputStream(outputFile);

                LogHelper.dev("Starting Copy...");

                byte[] buffer = new byte[4096];
                long size = is.available();
                long count = 0;
                int n;
                while (-1 != (n = is.read(buffer))) {
                    os.write(buffer, 0, n);
                    count += n;
                    progress = (double) count / (double) size;
                }

                is.close();
                os.close();
                LogHelper.dev("Download Complete");
            }
            catch (IOException e) {
                e.printStackTrace();
                failed = true;
                LogHelper.dev("Download Failed");
            }

            finished = true;
        }
    }

    private static class DLWatcher extends Thread {
        private DownloadThread targetThread;
        private double lastCheckProgress = 0;

        public DLWatcher(DownloadThread targetThread) {
            this.targetThread = targetThread;
        }

        @Override
        public void run() {
            try {
                while (!targetThread.finished) {
                    Thread.sleep(30000);

                    if (targetThread.progress <= lastCheckProgress) {
                        targetThread.finished = true;
                        targetThread.failed = true;
                        targetThread.interrupt();
                        LogHelper.error("Download Timed Out: " + targetThread.url);
                        break;
                    }
                    else {
                        lastCheckProgress = targetThread.progress;
                    }
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class ManifestEntry {
        public final String modName;
        public final String modid;
        public final Map<String, Integer> downloadToVersion;

        public ManifestEntry(String modName, String modid, Map<String, Integer> downloadToVersion) {
            this.modName = modName;
            this.modid = modid;
            this.downloadToVersion = downloadToVersion;
        }

        @Override
        public String toString() {
            return String.format("Name: %s, ModId: %s, Downloads: %s", modName, modid, downloadToVersion);
        }
    }
}
