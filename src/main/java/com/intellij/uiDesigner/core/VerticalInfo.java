package com.intellij.uiDesigner.core;

final class VerticalInfo extends DimensionInfo {
    public VerticalInfo(LayoutState layoutState, int gap) {
        super(layoutState, gap);
    }

    protected int getOriginalCell(GridConstraints constraints) {
        return constraints.getRow();
    }

    protected int getOriginalSpan(GridConstraints constraints) {
        return constraints.getRowSpan();
    }

    int getSizePolicy(int componentIndex) {
        return this.myLayoutState.getConstraints(componentIndex).getVSizePolicy();
    }

    int getChildLayoutCellCount(GridLayoutManager childLayout) {
        return childLayout.getRowCount();
    }

    public int getMinimumWidth(int componentIndex) {
        return getMinimumSize(componentIndex).height;
    }

    public DimensionInfo getDimensionInfo(GridLayoutManager grid) {
        return grid.myVerticalInfo;
    }

    public int getCellCount() {
        return this.myLayoutState.getRowCount();
    }

    public int getPreferredWidth(int componentIndex) {
        return getPreferredSize(componentIndex).height;
    }
}