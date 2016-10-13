package com.intellij.uiDesigner.core;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

public final class LayoutState {
    private final Component[] myComponents;
    private final GridConstraints[] myConstraints;
    private final int myColumnCount;
    private final int myRowCount;
    final Dimension[] myPreferredSizes;
    final Dimension[] myMinimumSizes;

    public LayoutState(GridLayoutManager layout, boolean ignoreInvisibleComponents) {
        ArrayList componentsList = new ArrayList(layout.getComponentCount());
        ArrayList constraintsList = new ArrayList(layout.getComponentCount());
        for (int i = 0; i < layout.getComponentCount(); i++) {
            Component component = layout.getComponent(i);
            if ((!ignoreInvisibleComponents) || (component.isVisible())) {
                componentsList.add(component);
                GridConstraints constraints = layout.getConstraints(i);
                constraintsList.add(constraints);
            }
        }
        this.myComponents = ((Component[]) componentsList.toArray(new Component[componentsList.size()]));
        this.myConstraints = ((GridConstraints[]) constraintsList.toArray(new GridConstraints[constraintsList.size()]));

        this.myMinimumSizes = new Dimension[this.myComponents.length];
        this.myPreferredSizes = new Dimension[this.myComponents.length];

        this.myColumnCount = layout.getColumnCount();
        this.myRowCount = layout.getRowCount();
    }

    public int getComponentCount() {
        return this.myComponents.length;
    }

    public Component getComponent(int index) {
        return this.myComponents[index];
    }

    public GridConstraints getConstraints(int index) {
        return this.myConstraints[index];
    }

    public int getColumnCount() {
        return this.myColumnCount;
    }

    public int getRowCount() {
        return this.myRowCount;
    }
}