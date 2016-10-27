package com.brandon3055.draconicevolution.client.gui.modwiki;

import com.brandon3055.brandonscore.utils.Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by brandon3055 on 3/09/2016.
 */
public class WikiConfig {

    public static File configJSON;

    //Windows
    public static volatile int NAV_WINDOW = 0xFF3c3f41;
    public static volatile int CONTENT_WINDOW = 0xFF3c3f41;
    public static volatile int MENU_BAR = 0xFF3c3f41;

    //Text
    public static volatile int NAV_TEXT = 0x00FFFF;
    public static volatile int TEXT_COLOUR = 0x8c8c8c;

    public static volatile String docLocation = "[CONFIG]";

    public static boolean drawEditInfo = true;
    public static volatile boolean editMode = false;

    public static void initialize(File wikiFolder) {
        configJSON = new File(wikiFolder, "options.json");

        if (!configJSON.exists()) {
            save();
        }
        load();
    }

    public static void load() {
        try {
            JsonReader reader = new JsonReader(new FileReader(configJSON));
            reader.beginObject();

            while (reader.hasNext()) {
                String name = reader.nextName();

                switch (name) {
                    case "colourNavWindow":
                        NAV_WINDOW = Utils.parseHex(reader.nextString());
                        break;
                    case "colourContentWindow":
                        CONTENT_WINDOW = Utils.parseHex(reader.nextString());
                        break;
                    case "colourMenuBar":
                        MENU_BAR = Utils.parseHex(reader.nextString());
                        break;
                    case "colourNavText":
                        NAV_TEXT = Utils.parseHex(reader.nextString());
                        break;
                    case "colourText2":
                        TEXT_COLOUR = Utils.parseHex(reader.nextString());
                        break;
                    case "editMode":
                        editMode = reader.nextBoolean();
                        break;
                    case "editTarget":
                        docLocation = reader.nextString();
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }

            reader.endObject();
            reader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            JsonWriter writer = new JsonWriter(new FileWriter(configJSON));
            writer.setIndent("	");
            writer.beginObject();

            writer.name("colourNavWindow").value(Integer.toHexString(NAV_WINDOW));
            writer.name("colourContentWindow").value(Integer.toHexString(CONTENT_WINDOW));
            writer.name("colourMenuBar").value(Integer.toHexString(MENU_BAR));
            writer.name("colourNavText").value(Integer.toHexString(NAV_TEXT));
            writer.name("colourText2").value(Integer.toHexString(TEXT_COLOUR));
            writer.name("editMode").value(editMode);
            writer.name("editTarget").value(docLocation);

            writer.endObject();
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
