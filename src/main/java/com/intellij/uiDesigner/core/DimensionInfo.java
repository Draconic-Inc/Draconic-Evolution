package com.intellij.uiDesigner.core;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;

public abstract class DimensionInfo {
    private final int[] myCell;
    private final int[] mySpan;
    protected final LayoutState myLayoutState;
    private final int[] myStretches;
    private final int[] mySpansAfterElimination;
    private final int[] myCellSizePolicies;
    private final int myGap;

    public DimensionInfo(LayoutState layoutState, int gap) {
        if (layoutState == null) {
            throw new IllegalArgumentException("layoutState cannot be null");
        }
        if (gap < 0) {
            throw new IllegalArgumentException("invalid gap: " + gap);
        }
        this.myLayoutState = layoutState;
        this.myGap = gap;

        this.myCell = new int[layoutState.getComponentCount()];
        this.mySpan = new int[layoutState.getComponentCount()];
        for (int i = 0; i < layoutState.getComponentCount(); i++) {
            GridConstraints c = layoutState.getConstraints(i);
            this.myCell[i] = getOriginalCell(c);
            this.mySpan[i] = getOriginalSpan(c);
        }
        this.myStretches = new int[getCellCount()];
        for (int i = 0; i < this.myStretches.length; i++) {
            this.myStretches[i] = 1;
        }
        ArrayList elimitated = new ArrayList();
        this.mySpansAfterElimination = ((int[]) this.mySpan.clone());
        Util.eliminate((int[]) this.myCell.clone(), this.mySpansAfterElimination, elimitated);

        this.myCellSizePolicies = new int[getCellCount()];
        for (int i = 0; i < this.myCellSizePolicies.length; i++) {
            this.myCellSizePolicies[i] = getCellSizePolicyImpl(i, elimitated);
        }
    }

    public final int getComponentCount() {
        return this.myLayoutState.getComponentCount();
    }

    public final Component getComponent(int componentIndex) {
        return this.myLayoutState.getComponent(componentIndex);
    }

    public final GridConstraints getConstraints(int componentIndex) {
        return this.myLayoutState.getConstraints(componentIndex);
    }

    public abstract int getCellCount();

    public abstract int getPreferredWidth(int paramInt);

    public abstract int getMinimumWidth(int paramInt);

    public abstract DimensionInfo getDimensionInfo(GridLayoutManager paramGridLayoutManager);

    public final int getCell(int componentIndex) {
        return this.myCell[componentIndex];
    }

    public final int getSpan(int componentIndex) {
        return this.mySpan[componentIndex];
    }

    public final int getStretch(int cellIndex) {
        return this.myStretches[cellIndex];
    }

    protected abstract int getOriginalCell(GridConstraints paramGridConstraints);

    protected abstract int getOriginalSpan(GridConstraints paramGridConstraints);

    abstract int getSizePolicy(int paramInt);

    abstract int getChildLayoutCellCount(GridLayoutManager paramGridLayoutManager);

    public final int getGap() {
        return this.myGap;
    }

    public boolean componentBelongsCell(int componentIndex, int cellIndex) {
        int componentStartCell = getCell(componentIndex);
        int span = getSpan(componentIndex);
        return (componentStartCell <= cellIndex) && (cellIndex < componentStartCell + span);
    }

    public final int getCellSizePolicy(int cellIndex) {
        return this.myCellSizePolicies[cellIndex];
    }

    private int getCellSizePolicyImpl(int cellIndex, ArrayList eliminatedCells) {
        int policyFromChild = getCellSizePolicyFromInheriting(cellIndex);
        if (policyFromChild != -1) {
            return policyFromChild;
        }
        for (int i = eliminatedCells.size() - 1; i >= 0; i--) {
            if (cellIndex == ((Integer) eliminatedCells.get(i)).intValue()) {
                return 1;
            }
        }
        return calcCellSizePolicy(cellIndex);
    }

    private int calcCellSizePolicy(int cellIndex) {
        boolean canShrink = true;
        boolean canGrow = false;
        boolean wantGrow = false;

        boolean weakCanGrow = true;
        boolean weakWantGrow = true;

        int countOfBelongingComponents = 0;
        for (int i = 0; i < getComponentCount(); i++) {
            if (componentBelongsCell(i, cellIndex)) {
                countOfBelongingComponents++;

                int p = getSizePolicy(i);

                boolean thisCanShrink = (p & 0x1) != 0;
                boolean thisCanGrow = (p & 0x2) != 0;
                boolean thisWantGrow = (p & 0x4) != 0;
                if ((getCell(i) == cellIndex) && (this.mySpansAfterElimination[i] == 1)) {
                    canShrink &= thisCanShrink;
                    canGrow |= thisCanGrow;
                    wantGrow |= thisWantGrow;
                }
                if (!thisCanGrow) {
                    weakCanGrow = false;
                }
                if (!thisWantGrow) {
                    weakWantGrow = false;
                }
            }
        }
        return (canShrink ? 1 : 0) | ((canGrow) || ((countOfBelongingComponents > 0) && (weakCanGrow)) ? 2 : 0) | ((wantGrow) || ((countOfBelongingComponents > 0) && (weakWantGrow)) ? 4 : 0);
    }

    private int getCellSizePolicyFromInheriting(int cellIndex) {
        int nonInheritingComponentsInCell = 0;
        int policyFromInheriting = -1;
        for (int i = getComponentCount() - 1; i >= 0; i--) {
            if (componentBelongsCell(i, cellIndex)) {
                Component child = getComponent(i);
                GridConstraints c = getConstraints(i);
                Container container = findAlignedChild(child, c);
                if (container != null) {
                    GridLayoutManager grid = (GridLayoutManager) container.getLayout();
                    grid.validateInfos(container);
                    DimensionInfo info = getDimensionInfo(grid);
                    int policy = info.calcCellSizePolicy(cellIndex - getOriginalCell(c));
                    if (policyFromInheriting == -1) {
                        policyFromInheriting = policy;
                    }
                    else {
                        policyFromInheriting |= policy;
                    }
                }
                else if ((getOriginalCell(c) == cellIndex) && (getOriginalSpan(c) == 1) && (!(child instanceof Spacer))) {
                    nonInheritingComponentsInCell++;
                }
            }
        }
        if (nonInheritingComponentsInCell > 0) {
            return -1;
        }
        return policyFromInheriting;
    }

    public static Container findAlignedChild(Component child, GridConstraints c) {
        if ((c.isUseParentLayout()) && ((child instanceof Container))) {
            Container container = (Container) child;
            if ((container.getLayout() instanceof GridLayoutManager)) {
                return container;
            }
            if ((container.getComponentCount() == 1) && ((container.getComponent(0) instanceof Container))) {
                Container childContainer = (Container) container.getComponent(0);
                if ((childContainer.getLayout() instanceof GridLayoutManager)) {
                    return childContainer;
                }
            }
        }
        return null;
    }

    protected final Dimension getPreferredSize(int componentIndex) {
        Dimension size = this.myLayoutState.myPreferredSizes[componentIndex];
        if (size == null) {
            size = Util.getPreferredSize(this.myLayoutState.getComponent(componentIndex), this.myLayoutState.getConstraints(componentIndex), true);
            this.myLayoutState.myPreferredSizes[componentIndex] = size;
        }
        return size;
    }

    protected final Dimension getMinimumSize(int componentIndex) {
        Dimension size = this.myLayoutState.myMinimumSizes[componentIndex];
        if (size == null) {
            size = Util.getMinimumSize(this.myLayoutState.getComponent(componentIndex), this.myLayoutState.getConstraints(componentIndex), true);
            this.myLayoutState.myMinimumSizes[componentIndex] = size;
        }
        return size;
    }
}