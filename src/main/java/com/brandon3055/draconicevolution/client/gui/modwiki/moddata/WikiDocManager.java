package com.brandon3055.draconicevolution.client.gui.modwiki.moddata;

import com.brandon3055.brandonscore.handlers.FileHandler;
import com.brandon3055.brandonscore.utils.LinkedHashList;
import com.brandon3055.draconicevolution.DraconicEvolution;
import com.brandon3055.draconicevolution.client.gui.modwiki.GuiModWiki;
import com.brandon3055.draconicevolution.client.gui.modwiki.WikiConfig;
import com.brandon3055.draconicevolution.utils.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brandon3055 on 29/08/2016.
 */
public class WikiDocManager {
    public static final ResourceLocation piDoc = new ResourceLocation(DraconicEvolution.MOD_PREFIX + "projectintelligence.xml");

    public static final String ATTRIB_DOC_REV = "docRevision";
    public static final String ATTRIB_MODID = "modid";
    public static final String ATTRIB_MOD_NAME = "modName";
    public static final String ATTRIB_LANG = "lang";
    public static final String ATTRIB_BRANCH_NAME = "name";
    public static final String ATTRIB_BRANCH_ID = "branchId";
    public static final String ATTRIB_BRANCH_CATEGORY = "category";
    public static final String ATTRIB_ICON_TYPE = "iconType";
    public static final String ATTRIB_ICON = "icon";
    public static final String ATTRIB_TYPE = "type";

    public static final String ICON_TYPE_OFF = "off";
    public static final String ICON_TYPE_STACK = "stack";

    public static final String ELEMENT_MOD = "mod";
    public static final String ELEMENT_ENTRY = "entry";
    public static final String ELEMENT_CONTENT = "content";

    public static File wikiFolder;
    public static File modDocsFolder;

    public static Map<String, ModDocContainer> modDocMap = new LinkedHashMap<String, ModDocContainer>();
    public static List<String> loadedCategories = new LinkedHashList<String>();
    public static Map<Document, File> documentToFileMap = new HashMap<Document, File>();
    public static ModDocContainer projectIntelContainer;

    public static void clearCategories() {
        loadedCategories.clear();
        loadedCategories.add("wiki.cat.item");
        loadedCategories.add("wiki.cat.block");
        loadedCategories.add("wiki.cat.mob");
        loadedCategories.add("wiki.cat.worldGen");
        loadedCategories.add("wiki.cat.dimension");
    }

    /**
     * Sets the wiki folder and mod docs folder.
     * Should only ever be called once during pre init.
     */
    public static void initialize() {
        wikiFolder = new File(FileHandler.brandon3055Folder, "ProjectIntelligence");
        wikiFolder.mkdirs();
        initFiles();
        loadDocsFromDisk();
        WikiDownloadManager.downloadManifest();
    }

    public static void initFiles() {
        try {
            IResource piResource = Minecraft.getMinecraft().getResourceManager().getResource(piDoc);
            File piXML = new File(wikiFolder, "projectintelligence.xml");
            InputStream is = piResource.getInputStream();
            OutputStream os = new FileOutputStream(piXML);
            IOUtils.copy(is, os);
            is.close();
            os.close();

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document document = builder.parse(piXML);
            Element mod = document.getDocumentElement();
            String modid = mod.getAttribute(ATTRIB_MODID);
            String lang = mod.getAttribute(ATTRIB_LANG);

            projectIntelContainer = new ModDocContainer(modid, mod, lang);
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        WikiConfig.initialize(wikiFolder);

        if (!WikiConfig.editTarget.equals("[CONFIG]")) {
            modDocsFolder = new File("D:\\Mass Storage\\Minecraft Dev\\WorkSpaces\\1.9\\Project-Intelligence-Docs\\ModDocs");

            if (modDocsFolder.isDirectory() || modDocsFolder.mkdirs()) {
                return;
            }
            else {
                LogHelper.error("Specified docs folder [%s] dose not exist and could not be created. Using config folder instead.", modDocsFolder.isDirectory());
            }
        }

        modDocsFolder = new File(wikiFolder, "ModDocs");
        modDocsFolder.mkdirs();
    }

    /**
     * Finds and loads all docs from the mod docs folder
     */
    public static void loadDocsFromDisk() {
        modDocMap.clear();
        documentToFileMap.clear();
        clearCategories();

        File[] modFolders = modDocsFolder.listFiles();

        if (modFolders == null || modFolders.length == 0) {
            LogHelper.error("Did not find any mod docs in the selected docs folder.");
            return;
        }

        for (File modFolder : modFolders) {
            if (!modFolder.isDirectory()) {
                if (!modFolder.getName().equals("manifest.json")) {
                    LogHelper.warn("Found unknown file in Mod Doc folder. " + modFolder);
                }
                continue;
            }
            LogHelper.dev("Checking for mod documentation in " + modFolder);

            File[] files = modFolder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) { return name != null && name.endsWith(".xml"); }
            });

            if (files == null || files.length == 0) {
                LogHelper.warn("Found empty mod doc folder " + modFolder);
                continue;
            }

            for (File xml : files) {
                try {
                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = docFactory.newDocumentBuilder();
                    Document document = builder.parse(xml);
                    Element mod = document.getDocumentElement();
                    String modid = mod.getAttribute(ATTRIB_MODID);
                    String lang = mod.getAttribute(ATTRIB_LANG);
                    addModDoc(modid, lang, mod);
                    documentToFileMap.put(document, xml);
                }
                catch (Exception e) {
                    LogHelper.error("Encountered a problem while loading mod documentation file " + xml);
                    e.printStackTrace();
                }
            }
        }//TODO load from an online source
    }

    /**
     * Adds a new mod doc or mod doc language variant to the mod doc map
     */
    public static void addModDoc(String modid, String lang, Element modElement) {
        if (!modDocMap.containsKey(modid)) {
            modDocMap.put(modid, new ModDocContainer(modid, modElement, lang));
        }
        else {
            modDocMap.get(modid).addModDocForLang(lang, modElement);
        }
    }

    public static void createNewModEntry(String modid, String modName, String lang) throws Exception { //TODO Implement multi language support

        //region Create File

        File modFolder = new File(modDocsFolder, modName);
        modFolder.mkdir();
        String modFileName = modid + "-" + lang + ".xml";
        File modXML = new File(modFolder, modFileName);

        if (modXML.exists()) {
            throw new IOException("Mod file already exists. [" + modXML + "]\n If this file is invalid or broken please delete it manually before continuing ");
        }

        if (!modXML.createNewFile()) {
            throw new IOException("An unknown error prevented the mod XML from being created [" + modXML + "]");
        }

        //endregion

        //region GenerateXML

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = docFactory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element modElement = document.createElement(ELEMENT_MOD);
        document.appendChild(modElement);
        modElement.setAttribute(ATTRIB_MODID, modid);
        modElement.setAttribute(ATTRIB_MOD_NAME, modName);
        modElement.setAttribute(ATTRIB_LANG, lang);
        modElement.setAttribute(ATTRIB_DOC_REV, "0");
        writeXMLToFile(document, modXML);

        addModDoc(modid, lang, modElement);

        reload(false, true, true);
        //endregion
    }

    public static void writeXMLToFile(Document document, File modFile) throws TransformerException, FileNotFoundException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new FileOutputStream(modFile));
        transformer.transform(source, result);
        documentToFileMap.put(document, modFile);
    }

    public static void saveChanges(Document document) throws TransformerException, FileNotFoundException {
        File xml = documentToFileMap.get(document);
        if (xml == null) {
            throw new FileNotFoundException("The file for this doc was not found in the cache...");
        }
        writeXMLToFile(document, xml);
    }

    /**
     * Universal reload method.
     * @param fromDisk If true will reload from disk.
     * @param reloadTree If true will reload the doc tree (if the tree exists)
     * @param reloadGui If true will reload the GUI (if the gui exists)
     */
    public static void reload(boolean fromDisk, boolean reloadTree, boolean reloadGui) {
        if (fromDisk) {
            loadDocsFromDisk();
        }
        if (GuiModWiki.activeInstance == null) {
            return;
        }
        if (reloadTree && GuiModWiki.activeInstance.wikiDataTree != null) {
            GuiModWiki.activeInstance.wikiDataTree.reloadData();
        }
        if (reloadGui && GuiModWiki.activeInstance.wikiList != null) {
            GuiModWiki.activeInstance.wikiList.reloadList(); //TODO May need to reload other parts of the gui
        }
    }
}
