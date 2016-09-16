package com.brandon3055.draconicevolution.client.gui.modwiki.moddata;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.brandon3055.draconicevolution.client.gui.modwiki.moddata.WikiDocManager.ATTRIB_DOC_REV;

/**
 * Created by brandon3055 on 9/09/2016.
 * This is responsible for holding a mods documentation and mapping it to its various loaded languages.
 * There will only be 1 language loaded if the language is set to english or 2 if another language is selected and there
 * is actually documentation for that language.
 */
public class ModDocContainer {
    public static final int VERSION_STAT_OK = 0;
    public static final int VERSION_STAT_OLD = 1;
    public static final int VERSION_STAT_ERROR = 2;

    public final String modid;
    public final Map<String, Element> langToElement = new HashMap<String, Element>();
    public final Map<String, Integer> langToVersion = new HashMap<String, Integer>();
    public final Map<String, Integer> langToVerState = new HashMap<String, Integer>();
    public String langStatus = null;
    public String langOverride = null;

    public ModDocContainer(String modid, Element initialElement, String lang) {
        this.modid = modid;
        addModDocForLang(lang, initialElement);
    }

    /**
     * Returns the doc element for the given language or the default (en_US) if there is no doc for that language.
     */
    public Element getElement(String lang) {
        if (langOverride != null) {
            if (langToElement.containsKey(langOverride)) {
                return langToElement.get(langOverride);
            }
            else {
                langStatus = "guiwiki.langStatus.OverrideFailed";
            }
        }
        if (langToElement.containsKey(lang)) {
            return langToElement.get(lang);
        }
        else if (langToElement.containsKey("en_US")) {
            langStatus = "guiwiki.langStatus.NADefaultLoaded";
            return langToElement.get("en_US");
        }

        List<String> entries = new ArrayList<String>(langToElement.keySet());

        if (entries.size() > 0) {
            langStatus = "guiwiki.langStatus.ErrorFirstLoaded";
            return langToElement.get(entries.get(0));
        }

        langStatus = "guiwiki.langStatus.Error";
        return null;
    }

    public void addModDocForLang(String lang, Element modElement) {
        langToElement.put(lang, modElement);
        int version = getVersion(modElement);
        langToVersion.put(lang, version);

        if (version == -1) {
            langToVerState.put(lang, VERSION_STAT_ERROR);
        }
        else {
            langToVerState.put(lang, VERSION_STAT_OK);
        }
    }

    public int getVersion(Element modElement) {
        try {
            return Integer.parseInt(modElement.getAttribute(ATTRIB_DOC_REV));
        }
        catch (Exception e) {
            return -1;
        }
    }
}
