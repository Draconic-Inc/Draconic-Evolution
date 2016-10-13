package com.brandon3055.draconicevolution.client.gui.modwiki.moddata;

import com.brandon3055.draconicevolution.utils.LogHelper;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brandon3055 on 30/08/2016.
 */
@Deprecated //TODO Delete. I am just leaving this here incase i find a use for it
public class ModContentList {

    public String lang;
    public int docRevision;
    public String modid;
    public String modName;
    public List<ContributorEntry> contributors = new LinkedList<ContributorEntry>();
    public List<ModContentPage> modDataEntries = new LinkedList<ModContentPage>();

    public ModContentList(String modid) {
        this.modid = modid;
    }


    //region Load / Save

    public void loadFromXML(File xmlFile) throws ParserConfigurationException, IOException, SAXException {
        contributors.clear();
        modDataEntries.clear();



        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = docFactory.newDocumentBuilder();
        Element mod = builder.parse(xmlFile).getDocumentElement();

        modid = mod.getAttribute("modid");
        modName = mod.getAttribute("modName");
        lang = mod.getAttribute("lang");

        try {
            docRevision = Integer.parseInt(mod.getAttribute("docRevision"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            docRevision = 0;
        }

        NodeList nodeList = mod.getElementsByTagName("contributor");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            NamedNodeMap map = node.getAttributes();
            Node name = map.getNamedItem("name");

            if (name == null) {
                continue;
            }

            Node ign = map.getNamedItem("ign");

            if (ign == null) {
                ign = name;
            }

            Node role = map.getNamedItem("role");
            String roleS = "";

            if (role != null) {
                roleS = role.getNodeValue();
            }

            ContributorEntry contributor = new ContributorEntry(name.getNodeValue(), ign.getNodeValue(), roleS, node.getTextContent());
            this.contributors.add(contributor);
        }

        NodeList entryList = mod.getElementsByTagName("entry");
        for (int entryIndex = 0; entryIndex < entryList.getLength(); entryIndex++) {
            Element entry = (Element) entryList.item(entryIndex);
            ModContentPage dataEntry = new ModContentPage();
            dataEntry.loadData(entry);
            LogHelper.dev(dataEntry);
            modDataEntries.add(dataEntry);
        }
    }

    public void writeToXML(File xml) {

    }

    //endregion
}
