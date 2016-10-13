package com.intellij.uiDesigner.core;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public final class GridLayoutManager extends AbstractLayout {
    private int myMinCellSize = 20;
    private final int[] myRowStretches;
    private final int[] myColumnStretches;
    private final int[] myYs;
    private final int[] myHeights;
    private final int[] myXs;
    private final int[] myWidths;
    private LayoutState myLayoutState;
    DimensionInfo myHorizontalInfo;
    DimensionInfo myVerticalInfo;
    private boolean mySameSizeHorizontally;
    private boolean mySameSizeVertically;
    public static Object DESIGN_TIME_INSETS = new Object();
    private static final int SKIP_ROW = 1;
    private static final int SKIP_COL = 2;

    public GridLayoutManager(int rowCount, int columnCount) {
        if (columnCount < 1) {
            throw new IllegalArgumentException("wrong columnCount: " + columnCount);
        }
        if (rowCount < 1) {
            throw new IllegalArgumentException("wrong rowCount: " + rowCount);
        }
        this.myRowStretches = new int[rowCount];
        for (int i = 0; i < rowCount; i++) {
            this.myRowStretches[i] = 1;
        }
        this.myColumnStretches = new int[columnCount];
        for (int i = 0; i < columnCount; i++) {
            this.myColumnStretches[i] = 1;
        }
        this.myXs = new int[columnCount];
        this.myWidths = new int[columnCount];

        this.myYs = new int[rowCount];
        this.myHeights = new int[rowCount];
    }

    public GridLayoutManager(int rowCount, int columnCount, Insets margin, int hGap, int vGap) {
        this(rowCount, columnCount);
        setMargin(margin);
        setHGap(hGap);
        setVGap(vGap);
        this.myMinCellSize = 0;
    }

    public GridLayoutManager(int rowCount, int columnCount, Insets margin, int hGap, int vGap, boolean sameSizeHorizontally, boolean sameSizeVertically) {
        this(rowCount, columnCount, margin, hGap, vGap);
        this.mySameSizeHorizontally = sameSizeHorizontally;
        this.mySameSizeVertically = sameSizeVertically;
    }

    public void addLayoutComponent(Component comp, Object constraints) {
        GridConstraints c = (GridConstraints) constraints;
        int row = c.getRow();
        int rowSpan = c.getRowSpan();
        int rowCount = getRowCount();
        if ((row < 0) || (row >= rowCount)) {
            throw new IllegalArgumentException("wrong row: " + row);
        }
        if (row + rowSpan - 1 >= rowCount) {
            throw new IllegalArgumentException("wrong row span: " + rowSpan + "; row=" + row + " rowCount=" + rowCount);
        }
        int column = c.getColumn();
        int colSpan = c.getColSpan();
        int columnCount = getColumnCount();
        if ((column < 0) || (column >= columnCount)) {
            throw new IllegalArgumentException("wrong column: " + column);
        }
        if (column + colSpan - 1 >= columnCount) {
            throw new IllegalArgumentException("wrong col span: " + colSpan + "; column=" + column + " columnCount=" + columnCount);
        }
        super.addLayoutComponent(comp, constraints);
    }

    public int getRowCount() {
        return this.myRowStretches.length;
    }

    public int getColumnCount() {
        return this.myColumnStretches.length;
    }

    public int getRowStretch(int rowIndex) {
        return this.myRowStretches[rowIndex];
    }

    public void setRowStretch(int rowIndex, int stretch) {
        if (stretch < 1) {
            throw new IllegalArgumentException("wrong stretch: " + stretch);
        }
        this.myRowStretches[rowIndex] = stretch;
    }

    public int getColumnStretch(int columnIndex) {
        return this.myColumnStretches[columnIndex];
    }

    public void setColumnStretch(int columnIndex, int stretch) {
        if (stretch < 1) {
            throw new IllegalArgumentException("wrong stretch: " + stretch);
        }
        this.myColumnStretches[columnIndex] = stretch;
    }

    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(2147483647, 2147483647);
    }

    public Dimension minimumLayoutSize(Container container) {
        validateInfos(container);


        DimensionInfo horizontalInfo = this.myHorizontalInfo;
        DimensionInfo verticalInfo = this.myVerticalInfo;

        Dimension result = getTotalGap(container, horizontalInfo, verticalInfo);

        int[] widths = getMinSizes(horizontalInfo);
        if (this.mySameSizeHorizontally) {
            makeSameSizes(widths);
        }
        result.width += sum(widths);

        int[] heights = getMinSizes(verticalInfo);
        if (this.mySameSizeVertically) {
            makeSameSizes(heights);
        }
        result.height += sum(heights);

        return result;
    }

    private static void makeSameSizes(int[] widths) {
        int max = widths[0];
        for (int i = 0; i < widths.length; i++) {
            int width = widths[i];
            max = Math.max(width, max);
        }
        for (int i = 0; i < widths.length; i++) {
            widths[i] = max;
        }
    }

    private static int[] getSameSizes(DimensionInfo info, int totalWidth) {
        int[] widths = new int[info.getCellCount()];

        int average = totalWidth / widths.length;
        int rest = totalWidth % widths.length;
        for (int i = 0; i < widths.length; i++) {
            widths[i] = average;
            if (rest > 0) {
                widths[i] += 1;
                rest--;
            }
        }
        return widths;
    }

    public Dimension preferredLayoutSize(Container container) {
        validateInfos(container);


        DimensionInfo horizontalInfo = this.myHorizontalInfo;
        DimensionInfo verticalInfo = this.myVerticalInfo;

        Dimension result = getTotalGap(container, horizontalInfo, verticalInfo);

        int[] widths = getPrefSizes(horizontalInfo);
        if (this.mySameSizeHorizontally) {
            makeSameSizes(widths);
        }
        result.width += sum(widths);

        int[] heights = getPrefSizes(verticalInfo);
        if (this.mySameSizeVertically) {
            makeSameSizes(heights);
        }
        result.height += sum(heights);

        return result;
    }

    private static int sum(int[] ints) {
        int result = 0;
        for (int i = ints.length - 1; i >= 0; i--) {
            result += ints[i];
        }
        return result;
    }

    private Dimension getTotalGap(Container container, DimensionInfo hInfo, DimensionInfo vInfo) {
        Insets insets = getInsets(container);
        return new Dimension(insets.left + insets.right + countGap(hInfo, 0, hInfo.getCellCount()) + this.myMargin.left + this.myMargin.right, insets.top + insets.bottom + countGap(vInfo, 0, vInfo.getCellCount()) + this.myMargin.top + this.myMargin.bottom);
    }

    private static int getDesignTimeInsets(Container container) {
        while (container != null) {
            if ((container instanceof JComponent)) {
                Integer designTimeInsets = (Integer) ((JComponent) container).getClientProperty(DESIGN_TIME_INSETS);
                if (designTimeInsets != null) {
                    return designTimeInsets.intValue();
                }
            }
            container = container.getParent();
        }
        return 0;
    }

    private static Insets getInsets(Container container) {
        Insets insets = container.getInsets();
        int insetsValue = getDesignTimeInsets(container);
        if (insetsValue != 0) {
            return new Insets(insets.top + insetsValue, insets.left + insetsValue, insets.bottom + insetsValue, insets.right + insetsValue);
        }
        return insets;
    }

    private static int countGap(DimensionInfo info, int startCell, int cellCount) {
        int counter = 0;
        for (int cellIndex = startCell + cellCount - 2; cellIndex >= startCell; cellIndex--) {
            if (shouldAddGapAfterCell(info, cellIndex)) {
                counter++;
            }
        }
        return counter * info.getGap();
    }

    private static boolean shouldAddGapAfterCell(DimensionInfo info, int cellIndex) {
        if ((cellIndex < 0) || (cellIndex >= info.getCellCount())) {
            throw new IllegalArgumentException("wrong cellIndex: " + cellIndex + "; cellCount=" + info.getCellCount());
        }
        boolean endsInThis = false;
        boolean startsInNext = false;

        int indexOfNextNotEmpty = -1;
        for (int i = cellIndex + 1; i < info.getCellCount(); i++) {
            if (!isCellEmpty(info, i)) {
                indexOfNextNotEmpty = i;
                break;
            }
        }
        for (int i = 0; i < info.getComponentCount(); i++) {
            Component component = info.getComponent(i);
            if (!(component instanceof Spacer)) {
                if ((info.componentBelongsCell(i, cellIndex)) && (DimensionInfo.findAlignedChild(component, info.getConstraints(i)) != null)) {
                    return true;
                }
                if (info.getCell(i) == indexOfNextNotEmpty) {
                    startsInNext = true;
                }
                if (info.getCell(i) + info.getSpan(i) - 1 == cellIndex) {
                    endsInThis = true;
                }
            }
        }
        return (startsInNext) && (endsInThis);
    }

    private static boolean isCellEmpty(DimensionInfo info, int cellIndex) {
        if ((cellIndex < 0) || (cellIndex >= info.getCellCount())) {
            throw new IllegalArgumentException("wrong cellIndex: " + cellIndex + "; cellCount=" + info.getCellCount());
        }
        for (int i = 0; i < info.getComponentCount(); i++) {
            Component component = info.getComponent(i);
            if ((info.getCell(i) == cellIndex) && (!(component instanceof Spacer))) {
                return false;
            }
        }
        return true;
    }

    public void layoutContainer(Container container) {
        validateInfos(container);


        LayoutState layoutState = this.myLayoutState;
        DimensionInfo horizontalInfo = this.myHorizontalInfo;
        DimensionInfo verticalInfo = this.myVerticalInfo;

        Insets insets = getInsets(container);

        int skipLayout = checkSetSizesFromParent(container, insets);

        Dimension gap = getTotalGap(container, horizontalInfo, verticalInfo);

        Dimension size = container.getSize();
        size.width -= gap.width;
        size.height -= gap.height;

        Dimension prefSize = preferredLayoutSize(container);
        prefSize.width -= gap.width;
        prefSize.height -= gap.height;

        Dimension minSize = minimumLayoutSize(container);
        minSize.width -= gap.width;
        minSize.height -= gap.height;
        if ((skipLayout & 0x1) == 0) {
            int[] heights;
            if (this.mySameSizeVertically) {
                heights = getSameSizes(verticalInfo, Math.max(size.height, minSize.height));
            }
            else if (size.height < prefSize.height) {
                heights = getMinSizes(verticalInfo);
                new_doIt(heights, 0, verticalInfo.getCellCount(), size.height, verticalInfo, true);
            }
            else {
                heights = getPrefSizes(verticalInfo);
                new_doIt(heights, 0, verticalInfo.getCellCount(), size.height, verticalInfo, false);
            }
            int y = insets.top + this.myMargin.top;
            for (int i = 0; i < heights.length; i++) {
                this.myYs[i] = y;
                this.myHeights[i] = heights[i];
                y += heights[i];
                if (shouldAddGapAfterCell(verticalInfo, i)) {
                    y += verticalInfo.getGap();
                }
            }
        }
        if ((skipLayout & 0x2) == 0) {
            int[] widths;
            if (this.mySameSizeHorizontally) {
                widths = getSameSizes(horizontalInfo, Math.max(size.width, minSize.width));
            }
            else if (size.width < prefSize.width) {
                widths = getMinSizes(horizontalInfo);
                new_doIt(widths, 0, horizontalInfo.getCellCount(), size.width, horizontalInfo, true);
            }
            else {
                widths = getPrefSizes(horizontalInfo);
                new_doIt(widths, 0, horizontalInfo.getCellCount(), size.width, horizontalInfo, false);
            }
            int x = insets.left + this.myMargin.left;
            for (int i = 0; i < widths.length; i++) {
                this.myXs[i] = x;
                this.myWidths[i] = widths[i];
                x += widths[i];
                if (shouldAddGapAfterCell(horizontalInfo, i)) {
                    x += horizontalInfo.getGap();
                }
            }
        }
        for (int i = 0; i < layoutState.getComponentCount(); i++) {
            GridConstraints c = layoutState.getConstraints(i);
            Component component = layoutState.getComponent(i);

            int column = horizontalInfo.getCell(i);
            int colSpan = horizontalInfo.getSpan(i);
            int row = verticalInfo.getCell(i);
            int rowSpan = verticalInfo.getSpan(i);

            int cellWidth = this.myXs[(column + colSpan - 1)] + this.myWidths[(column + colSpan - 1)] - this.myXs[column];
            int cellHeight = this.myYs[(row + rowSpan - 1)] + this.myHeights[(row + rowSpan - 1)] - this.myYs[row];

            Dimension componentSize = new Dimension(cellWidth, cellHeight);
            if ((c.getFill() & 0x1) == 0) {
                componentSize.width = Math.min(componentSize.width, horizontalInfo.getPreferredWidth(i));
            }
            if ((c.getFill() & 0x2) == 0) {
                componentSize.height = Math.min(componentSize.height, verticalInfo.getPreferredWidth(i));
            }
            Util.adjustSize(component, c, componentSize);

            int dx = 0;
            int dy = 0;
            if ((c.getAnchor() & 0x4) != 0) {
                dx = cellWidth - componentSize.width;
            }
            else if ((c.getAnchor() & 0x8) == 0) {
                dx = (cellWidth - componentSize.width) / 2;
            }
            if ((c.getAnchor() & 0x2) != 0) {
                dy = cellHeight - componentSize.height;
            }
            else if ((c.getAnchor() & 0x1) == 0) {
                dy = (cellHeight - componentSize.height) / 2;
            }
            int indent = 10 * c.getIndent();
            componentSize.width -= indent;
            dx += indent;

            component.setBounds(this.myXs[column] + dx, this.myYs[row] + dy, componentSize.width, componentSize.height);
        }
    }

    private int checkSetSizesFromParent(Container container, Insets insets) {
        int skipLayout = 0;

        GridLayoutManager parentGridLayout = null;
        GridConstraints parentGridConstraints = null;


        Container parent = container.getParent();
        if (parent != null) {
            if ((parent.getLayout() instanceof GridLayoutManager)) {
                parentGridLayout = (GridLayoutManager) parent.getLayout();
                parentGridConstraints = parentGridLayout.getConstraintsForComponent(container);
            }
            else {
                Container parent2 = parent.getParent();
                if ((parent2 != null) && ((parent2.getLayout() instanceof GridLayoutManager))) {
                    parentGridLayout = (GridLayoutManager) parent2.getLayout();
                    parentGridConstraints = parentGridLayout.getConstraintsForComponent(parent);
                }
            }
        }
        if ((parentGridLayout != null) && (parentGridConstraints.isUseParentLayout())) {
            if (this.myRowStretches.length == parentGridConstraints.getRowSpan()) {
                int row = parentGridConstraints.getRow();
                this.myYs[0] = (insets.top + this.myMargin.top);
                this.myHeights[0] = (parentGridLayout.myHeights[row] - this.myYs[0]);
                for (int i = 1; i < this.myRowStretches.length; i++) {
                    this.myYs[i] = (parentGridLayout.myYs[(i + row)] - parentGridLayout.myYs[row]);
                    this.myHeights[i] = parentGridLayout.myHeights[(i + row)];
                }
                this.myHeights[(this.myRowStretches.length - 1)] -= insets.bottom + this.myMargin.bottom;
                skipLayout |= 0x1;
            }
            if (this.myColumnStretches.length == parentGridConstraints.getColSpan()) {
                int col = parentGridConstraints.getColumn();
                this.myXs[0] = (insets.left + this.myMargin.left);
                this.myWidths[0] = (parentGridLayout.myWidths[col] - this.myXs[0]);
                for (int i = 1; i < this.myColumnStretches.length; i++) {
                    this.myXs[i] = (parentGridLayout.myXs[(i + col)] - parentGridLayout.myXs[col]);
                    this.myWidths[i] = parentGridLayout.myWidths[(i + col)];
                }
                this.myWidths[(this.myColumnStretches.length - 1)] -= insets.right + this.myMargin.right;
                skipLayout |= 0x2;
            }
        }
        return skipLayout;
    }

    public void invalidateLayout(Container container) {
        this.myLayoutState = null;
        this.myHorizontalInfo = null;
        this.myVerticalInfo = null;
    }

    void validateInfos(Container container) {
        if (this.myLayoutState == null) {
            this.myLayoutState = new LayoutState(this, getDesignTimeInsets(container) == 0);
            this.myHorizontalInfo = new HorizontalInfo(this.myLayoutState, getHGapImpl(container));
            this.myVerticalInfo = new VerticalInfo(this.myLayoutState, getVGapImpl(container));
        }
    }

    public int[] getXs() {
        return this.myXs;
    }

    public int[] getWidths() {
        return this.myWidths;
    }

    public int[] getYs() {
        return this.myYs;
    }

    public int[] getHeights() {
        return this.myHeights;
    }

    public int[] getCoords(boolean isRow) {
        return isRow ? this.myYs : this.myXs;
    }

    public int[] getSizes(boolean isRow) {
        return isRow ? this.myHeights : this.myWidths;
    }

    private int[] getMinSizes(DimensionInfo info) {
        return getMinOrPrefSizes(info, true);
    }

    private int[] getPrefSizes(DimensionInfo info) {
        return getMinOrPrefSizes(info, false);
    }

    private int[] getMinOrPrefSizes(DimensionInfo info, boolean min) {
        int[] widths = new int[info.getCellCount()];
        for (int i = 0; i < widths.length; i++) {
            widths[i] = this.myMinCellSize;
        }
        for (int i = info.getComponentCount() - 1; i >= 0; i--) {
            if (info.getSpan(i) == 1) {
                int size = min ? getMin2(info, i) : Math.max(info.getMinimumWidth(i), info.getPreferredWidth(i));
                int cell = info.getCell(i);
                int gap = countGap(info, cell, info.getSpan(i));
                size = Math.max(size - gap, 0);

                widths[cell] = Math.max(widths[cell], size);
            }
        }
        updateSizesFromChildren(info, min, widths);


        boolean[] toProcess = new boolean[info.getCellCount()];
        for (int i = info.getComponentCount() - 1; i >= 0; i--) {
            int size = min ? getMin2(info, i) : Math.max(info.getMinimumWidth(i), info.getPreferredWidth(i));

            int span = info.getSpan(i);
            int cell = info.getCell(i);

            int gap = countGap(info, cell, span);
            size = Math.max(size - gap, 0);

            Arrays.fill(toProcess, false);

            int curSize = 0;
            for (int j = 0; j < span; j++) {
                curSize += widths[(j + cell)];
                toProcess[(j + cell)] = true;
            }
            if (curSize < size) {
                boolean[] higherPriorityCells = new boolean[toProcess.length];
                getCellsWithHigherPriorities(info, toProcess, higherPriorityCells, false, widths);

                distribute(higherPriorityCells, info, size - curSize, widths);
            }
        }
        return widths;
    }

    private static void updateSizesFromChildren(DimensionInfo info, boolean min, int[] widths) {
        for (int i = info.getComponentCount() - 1; i >= 0; i--) {
            Component child = info.getComponent(i);
            GridConstraints c = info.getConstraints(i);
            if ((c.isUseParentLayout()) && ((child instanceof Container))) {
                Container container = (Container) child;
                if ((container.getLayout() instanceof GridLayoutManager)) {
                    updateSizesFromChild(info, min, widths, container, i);
                }
                else if ((container.getComponentCount() == 1) && ((container.getComponent(0) instanceof Container))) {
                    Container childContainer = (Container) container.getComponent(0);
                    if ((childContainer.getLayout() instanceof GridLayoutManager)) {
                        updateSizesFromChild(info, min, widths, childContainer, i);
                    }
                }
            }
        }
    }

    private static void updateSizesFromChild(DimensionInfo info, boolean min, int[] widths, Container container, int childIndex) {
        GridLayoutManager childLayout = (GridLayoutManager) container.getLayout();
        if (info.getSpan(childIndex) == info.getChildLayoutCellCount(childLayout)) {
            childLayout.validateInfos(container);
            DimensionInfo childInfo = (info instanceof HorizontalInfo) ? childLayout.myHorizontalInfo : childLayout.myVerticalInfo;


            int[] sizes = childLayout.getMinOrPrefSizes(childInfo, min);
            int cell = info.getCell(childIndex);
            for (int j = 0; j < sizes.length; j++) {
                widths[(cell + j)] = Math.max(widths[(cell + j)], sizes[j]);
            }
        }
    }

    private static int getMin2(DimensionInfo info, int componentIndex) {
        int s;
        if ((info.getSizePolicy(componentIndex) & 0x1) != 0) {
            s = info.getMinimumWidth(componentIndex);
        }
        else {
            s = Math.max(info.getMinimumWidth(componentIndex), info.getPreferredWidth(componentIndex));
        }
        return s;
    }

    private void new_doIt(int[] widths, int cell, int span, int minWidth, DimensionInfo info, boolean checkPrefs) {
        int toDistribute = minWidth;
        for (int i = cell; i < cell + span; i++) {
            toDistribute -= widths[i];
        }
        if (toDistribute <= 0) {
            return;
        }
        boolean[] allowedCells = new boolean[info.getCellCount()];
        for (int i = cell; i < cell + span; i++) {
            allowedCells[i] = true;
        }
        boolean[] higherPriorityCells = new boolean[info.getCellCount()];
        getCellsWithHigherPriorities(info, allowedCells, higherPriorityCells, checkPrefs, widths);

        distribute(higherPriorityCells, info, toDistribute, widths);
    }

    private static void distribute(boolean[] higherPriorityCells, DimensionInfo info, int toDistribute, int[] widths) {
        int stretches = 0;
        for (int i = 0; i < info.getCellCount(); i++) {
            if (higherPriorityCells[i]) {
                stretches += info.getStretch(i);
            }
        }
        int toDistributeFrozen = toDistribute;
        for (int i = 0; i < info.getCellCount(); i++) {
            if (higherPriorityCells[i]) {
                int addon = toDistributeFrozen * info.getStretch(i) / stretches;
                widths[i] += addon;

                toDistribute -= addon;
            }
        }
        if (toDistribute != 0) {
            for (int i = 0; i < info.getCellCount(); i++) {
                if (higherPriorityCells[i]) {
                    widths[i] += 1;
                    toDistribute--;
                    if (toDistribute == 0) {
                        break;
                    }
                }
            }
        }
        if (toDistribute != 0) {
            throw new IllegalStateException("toDistribute = " + toDistribute);
        }
    }

    private void getCellsWithHigherPriorities(DimensionInfo info, boolean[] allowedCells, boolean[] higherPriorityCells, boolean checkPrefs, int[] widths) {
        Arrays.fill(higherPriorityCells, false);

        int foundCells = 0;
        if (checkPrefs) {
            int[] prefs = getMinOrPrefSizes(info, false);
            for (int cell = 0; cell < allowedCells.length; cell++) {
                if (allowedCells[cell]) {
                    if ((!isCellEmpty(info, cell)) && (prefs[cell] > widths[cell])) {
                        higherPriorityCells[cell] = true;
                        foundCells++;
                    }
                }
            }
            if (foundCells > 0) {
                return;
            }
        }
        for (int cell = 0; cell < allowedCells.length; cell++) {
            if (allowedCells[cell]) {
                if ((info.getCellSizePolicy(cell) & 0x4) != 0) {
                    higherPriorityCells[cell] = true;
                    foundCells++;
                }
            }
        }
        if (foundCells > 0) {
            return;
        }
        for (int cell = 0; cell < allowedCells.length; cell++) {
            if (allowedCells[cell]) {
                if ((info.getCellSizePolicy(cell) & 0x2) != 0) {
                    higherPriorityCells[cell] = true;
                    foundCells++;
                }
            }
        }
        if (foundCells > 0) {
            return;
        }
        for (int cell = 0; cell < allowedCells.length; cell++) {
            if (allowedCells[cell]) {
                if (!isCellEmpty(info, cell)) {
                    higherPriorityCells[cell] = true;
                    foundCells++;
                }
            }
        }
        if (foundCells > 0) {
            return;
        }
        for (int cell = 0; cell < allowedCells.length; cell++) {
            if (allowedCells[cell]) {
                higherPriorityCells[cell] = true;
            }
        }
    }

    public boolean isSameSizeHorizontally() {
        return this.mySameSizeHorizontally;
    }

    public boolean isSameSizeVertically() {
        return this.mySameSizeVertically;
    }

    public void setSameSizeHorizontally(boolean sameSizeHorizontally) {
        this.mySameSizeHorizontally = sameSizeHorizontally;
    }

    public void setSameSizeVertically(boolean sameSizeVertically) {
        this.mySameSizeVertically = sameSizeVertically;
    }

    public int[] getHorizontalGridLines() {
        int[] result = new int[this.myYs.length + 1];
        result[0] = this.myYs[0];
        for (int i = 0; i < this.myYs.length - 1; i++) {
            result[(i + 1)] = ((this.myYs[i] + this.myHeights[i] + this.myYs[(i + 1)]) / 2);
        }
        result[this.myYs.length] = (this.myYs[(this.myYs.length - 1)] + this.myHeights[(this.myYs.length - 1)]);
        return result;
    }

    public int[] getVerticalGridLines() {
        int[] result = new int[this.myXs.length + 1];
        result[0] = this.myXs[0];
        for (int i = 0; i < this.myXs.length - 1; i++) {
            result[(i + 1)] = ((this.myXs[i] + this.myWidths[i] + this.myXs[(i + 1)]) / 2);
        }
        result[this.myXs.length] = (this.myXs[(this.myXs.length - 1)] + this.myWidths[(this.myXs.length - 1)]);
        return result;
    }

    public int getCellCount(boolean isRow) {
        return isRow ? getRowCount() : getColumnCount();
    }

    public int getCellSizePolicy(boolean isRow, int cellIndex) {
        DimensionInfo info = isRow ? this.myVerticalInfo : this.myHorizontalInfo;
        if (info == null) {
            return 0;
        }
        return info.getCellSizePolicy(cellIndex);
    }
}