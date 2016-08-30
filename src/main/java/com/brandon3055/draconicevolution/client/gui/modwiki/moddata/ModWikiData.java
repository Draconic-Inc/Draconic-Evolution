package com.brandon3055.draconicevolution.client.gui.modwiki.moddata;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon3055 on 29/08/2016.
 */
public class ModWikiData {

    public static Map<String, ModDataContainer> modDataContainerMap = new HashMap<String, ModDataContainer>();

    public static void loadData() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = docFactory.newDocumentBuilder();
            Document document = builder.newDocument();














        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }//TODO load from an online source


}
