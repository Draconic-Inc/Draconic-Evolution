package com.brandon3055.draconicevolution.client.gui.modwiki.moddata;

/**
 * Created by brandon3055 on 3/09/2016.
 */
@Deprecated //TODO Delete. I am just leaving this here incase i find a use for it
public class ContributorEntry {

    public final String name;
    public final String ign;
    public final String role;
    public final String comment;

    public ContributorEntry(String name, String ign, String role, String comment) {
        this.name = name;
        this.ign = ign;
        this.role = role;
        this.comment = comment;
    }


    @Override
    public String toString() {
        return String.format("Contributor: [Name: %s, IGN: %s, Role: %s, Comment: %s]", name, ign, role, comment);
    }
}
