package com.intellij.uiDesigner.core;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

public abstract class AbstractLayout implements LayoutManager2 {
    public static final int DEFAULT_HGAP = 10;
    public static final int DEFAULT_VGAP = 5;
    protected Component[] myComponents;
    protected GridConstraints[] myConstraints;
    protected Insets myMargin;
    private int myHGap;
    private int myVGap;
    private static final Component[] COMPONENT_EMPTY_ARRAY = new Component[0];

    public AbstractLayout() {
        this.myComponents = COMPONENT_EMPTY_ARRAY;
        this.myConstraints = GridConstraints.EMPTY_ARRAY;
        this.myMargin = new Insets(0, 0, 0, 0);
        this.myHGap = -1;
        this.myVGap = -1;
    }

    public final Insets getMargin() {
        return (Insets) this.myMargin.clone();
    }

    public final int getHGap() {
        return this.myHGap;
    }

    protected static int getHGapImpl(Container container) {
        if (container == null) {
            throw new IllegalArgumentException("container cannot be null");
        }
        while (container != null) {
            if ((container.getLayout() instanceof AbstractLayout)) {
                AbstractLayout layout = (AbstractLayout) container.getLayout();
                if (layout.getHGap() != -1) {
                    return layout.getHGap();
                }
            }
            container = container.getParent();
        }
        return 10;
    }

    public final void setHGap(int hGap) {
        if (hGap < -1) {
            throw new IllegalArgumentException("wrong hGap: " + hGap);
        }
        this.myHGap = hGap;
    }

    public final int getVGap() {
        return this.myVGap;
    }

    protected static int getVGapImpl(Container container) {
        if (container == null) {
            throw new IllegalArgumentException("container cannot be null");
        }
        while (container != null) {
            if ((container.getLayout() instanceof AbstractLayout)) {
                AbstractLayout layout = (AbstractLayout) container.getLayout();
                if (layout.getVGap() != -1) {
                    return layout.getVGap();
                }
            }
            container = container.getParent();
        }
        return 5;
    }

    public final void setVGap(int vGap) {
        if (vGap < -1) {
            throw new IllegalArgumentException("wrong vGap: " + vGap);
        }
        this.myVGap = vGap;
    }

    public final void setMargin(Insets margin) {
        if (margin == null) {
            throw new IllegalArgumentException("margin cannot be null");
        }
        this.myMargin = ((Insets) margin.clone());
    }

    final int getComponentCount() {
        return this.myComponents.length;
    }

    final Component getComponent(int index) {
        return this.myComponents[index];
    }

    final GridConstraints getConstraints(int index) {
        return this.myConstraints[index];
    }

    public void addLayoutComponent(Component comp, Object constraints) {
        if (!(constraints instanceof GridConstraints)) {
            throw new IllegalArgumentException("constraints: " + constraints);
        }
        Component[] newComponents = new Component[this.myComponents.length + 1];
        System.arraycopy(this.myComponents, 0, newComponents, 0, this.myComponents.length);
        newComponents[this.myComponents.length] = comp;
        this.myComponents = newComponents;

        GridConstraints[] newConstraints = new GridConstraints[this.myConstraints.length + 1];
        System.arraycopy(this.myConstraints, 0, newConstraints, 0, this.myConstraints.length);
        newConstraints[this.myConstraints.length] = ((GridConstraints) ((GridConstraints) constraints).clone());
        this.myConstraints = newConstraints;
    }

    public final void addLayoutComponent(String name, Component comp) {
        throw new UnsupportedOperationException();
    }

    public final void removeLayoutComponent(Component comp) {
        int i = getComponentIndex(comp);
        if (i == -1) {
            throw new IllegalArgumentException("component was not added: " + comp);
        }
        if (this.myComponents.length == 1) {
            this.myComponents = COMPONENT_EMPTY_ARRAY;
        }
        else {
            Component[] newComponents = new Component[this.myComponents.length - 1];
            System.arraycopy(this.myComponents, 0, newComponents, 0, i);
            System.arraycopy(this.myComponents, i + 1, newComponents, i, this.myComponents.length - i - 1);
            this.myComponents = newComponents;
        }
        if (this.myConstraints.length == 1) {
            this.myConstraints = GridConstraints.EMPTY_ARRAY;
        }
        else {
            GridConstraints[] newConstraints = new GridConstraints[this.myConstraints.length - 1];
            System.arraycopy(this.myConstraints, 0, newConstraints, 0, i);
            System.arraycopy(this.myConstraints, i + 1, newConstraints, i, this.myConstraints.length - i - 1);
            this.myConstraints = newConstraints;
        }
    }

    public GridConstraints getConstraintsForComponent(Component comp) {
        int i = getComponentIndex(comp);
        if (i == -1) {
            throw new IllegalArgumentException("component was not added: " + comp);
        }
        return this.myConstraints[i];
    }

    private int getComponentIndex(Component comp) {
        for (int i = 0; i < this.myComponents.length; i++) {
            Component component = this.myComponents[i];
            if (component == comp) {
                return i;
            }
        }
        return -1;
    }

    public final float getLayoutAlignmentX(Container container) {
        return 0.5F;
    }

    public final float getLayoutAlignmentY(Container container) {
        return 0.5F;
    }

    public abstract Dimension maximumLayoutSize(Container paramContainer);

    public abstract void invalidateLayout(Container paramContainer);

    public abstract Dimension preferredLayoutSize(Container paramContainer);

    public abstract Dimension minimumLayoutSize(Container paramContainer);

    public abstract void layoutContainer(Container paramContainer);
}