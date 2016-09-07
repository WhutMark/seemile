package com.seemile.launcher.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.view.ViewPropertyAnimator;
import com.seemile.launcher.R;
import com.seemile.launcher.util.Logger;

/**
 * Created by whuthm on 2016/1/7.
 */
public class CellLayout extends ViewGroup implements View.OnFocusChangeListener {

    private static final String TAG = "CellLayout";

    private static final float FOCUSED_SCALE = 1.15f;
    private static final float NORMAL_SCALE = 1.0f;
    private static final long ANIMTION_DURATION = 400;

    protected int mCellWidth;
    protected int mCellHeight;
    protected final int mOriginalCellWidth;
    protected final int mOriginalCellHeight;

    protected int mCountX;
    protected int mCountY;

    protected int mWidthGap;
    protected int mHeightGap;
    protected final int mOriginalWidthGap;
    protected final int mOriginalHeightGap;

    protected final boolean mFixedCountX;
    protected final boolean mFixedCountY;

    boolean[][] mOccupied;

    protected CellChildrenLayout mChildrenLayout;
    protected View mBorderView;
    protected Rect mBorderPadding = new Rect();

    public CellLayout(Context context) {
        this(context, null);
    }

    public CellLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CellLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setClipToPadding(false);
        setClipChildren(false);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CellLayout, defStyleAttr, 0);
        mCellWidth = mOriginalCellWidth = a.getDimensionPixelSize(R.styleable.CellLayout_cellWidth, 0);
        mCellHeight = mOriginalCellHeight = a.getDimensionPixelSize(R.styleable.CellLayout_cellHeight, 0);
        mWidthGap = mOriginalWidthGap = a.getDimensionPixelSize(R.styleable.CellLayout_widthGap, -1);
        mHeightGap = mOriginalHeightGap = a.getDimensionPixelSize(R.styleable.CellLayout_heightGap, -1);
        final int countX = a.getInteger(R.styleable.CellLayout_countX, 0);
        final int countY = a.getInteger(R.styleable.CellLayout_countY, 0);
        mFixedCountX = a.getBoolean(R.styleable.CellLayout_fixedCountX, true);
        mFixedCountY = a.getBoolean(R.styleable.CellLayout_fixedCountY, true);
        a.recycle();

        setGridSize(countX, countY);

        mChildrenLayout = new CellChildrenLayout(context);
        mChildrenLayout.setOnHierarchyChangeListener(mHierarchyChangeListener);
        addViewInternal(mChildrenLayout);

        mBorderView = new View(context);
        Drawable drawable = getResources().getDrawable(R.drawable.border);
        drawable.getPadding(mBorderPadding);
        Logger.i(TAG, mBorderPadding.toString());
        mBorderView.setBackgroundDrawable(drawable);
        mBorderView.setVisibility(GONE);
        addViewInternal(mBorderView);
    }

    public void setGridSize(int countX, int countY) {
        if (countX < 0 || countY < 0) {
            throw new RuntimeException("countX < 0 || countY < 0");
        }
        if (countX != mCountX || countY != mCountY) {
            mCountX = countX;
            mCountY = countY;
            mOccupied = new boolean[mCountX][mCountY];
            requestLayout();
        }
    }

    public int getCountX() {
        return mCountX;
    }

    public int getCountY() {
        return mCountY;
    }

    int getCellWidth() {
        return mCellWidth;
    }

    int getCellHeight() {
        return mCellHeight;
    }

    int getWidthGap() {
        return mWidthGap;
    }

    int getHeightGap() {
        return mHeightGap;
    }

    public View getChildAt(int x, int y) {
        return mChildrenLayout.getChildAt(x, y);
    }

    private OnHierarchyChangeListener mHierarchyChangeListener = new OnHierarchyChangeListener() {
        @Override
        public void onChildViewAdded(View parent, View child) {
            child.setOnFocusChangeListener(CellLayout.this);
        }

        @Override
        public void onChildViewRemoved(View parent, View child) {
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mChildrenLayout.setOnHierarchyChangeListener(null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.i(TAG, "onMeasure   widthSpecSize:" + widthSpecSize + " heightSpecSize: " + heightSpecSize);

        if (widthSpecMode == MeasureSpec.UNSPECIFIED && mOriginalCellWidth <= 0) {
            throw new RuntimeException("CellLayout cannot have Width UNSPECIFIED dimensions and CellWidth = 0");
        }

        if (heightSpecMode == MeasureSpec.UNSPECIFIED && mOriginalCellHeight <= 0) {
            throw new RuntimeException("CellLayout cannot have Height UNSPECIFIED dimensions and CellHeight = 0");
        }

        if (mCountX <= 0 || mCountY <= 0) {
            mCellWidth = 0;
            mCellHeight = 0;
            mWidthGap = 0;
            mHeightGap = 0;
            mChildrenLayout.setCellDimensions(mCellWidth, mCellHeight, mWidthGap, mHeightGap);
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                    getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
            return;
        }

        int newWidth = widthSpecSize;
        int newHeight = heightSpecSize;

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int numWidthGaps = mCountX - 1;
        int numHeightGaps = mCountY - 1;

        if (mOriginalCellWidth <= 0) {
            mWidthGap = Math.max(0, mOriginalWidthGap);
            int hSpace = widthSpecSize - paddingLeft - paddingRight - numWidthGaps * mWidthGap;
            mCellWidth = Math.max(0, hSpace / mCountX);
        } else {
            mCellWidth = mOriginalCellWidth;
            if (mOriginalWidthGap <= 0) {
                int hSpace = widthSpecSize - paddingLeft - paddingRight - mCountX * mCellWidth;
                mWidthGap = Math.max(0, numWidthGaps > 0 ? hSpace / numHeightGaps : 0);
            } else {
                mWidthGap = Math.max(0, mOriginalWidthGap);
            }
        }

        if (mOriginalCellHeight <= 0) {
            mHeightGap = Math.max(0, mOriginalHeightGap);
            int vSpace = heightSpecSize - paddingTop - paddingBottom - numHeightGaps * mHeightGap;
            mCellHeight = Math.max(0, vSpace / mCountY);
        } else {
            mCellHeight = mOriginalCellHeight;
            if (mOriginalHeightGap <= 0) {
                int vSpace = heightSpecSize - paddingTop - paddingBottom - mCountY * mCellWidth;
                mHeightGap = Math.max(0, numHeightGaps > 0 ? vSpace / numHeightGaps : 0);
            } else {
                mHeightGap = Math.max(0, mOriginalHeightGap);
            }
        }

        mChildrenLayout.setCellDimensions(mCellWidth, mCellHeight, mWidthGap, mHeightGap);

        if (widthSpecMode == MeasureSpec.AT_MOST || widthSpecMode == MeasureSpec.UNSPECIFIED) {
            newWidth = paddingLeft + paddingRight + (mCountX * mCellWidth) +
                    ((mCountX - 1) * mWidthGap);
        }

        if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            newHeight = paddingTop + paddingBottom + (mCountY * mCellHeight) +
                    ((mCountY - 1) * mHeightGap);
        }

        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(newWidth - paddingLeft -
                paddingRight, MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(newHeight - paddingTop -
                paddingBottom, MeasureSpec.EXACTLY);
        mChildrenLayout.measure(childWidthMeasureSpec, childHeightMeasureSpec);

        measureChild(mBorderView, widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(newWidth, newHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mChildrenLayout.layout(getPaddingLeft(), getPaddingTop(), r - l - getPaddingRight(), b - t - getPaddingBottom());

        if (mBorderView.getVisibility() == VISIBLE) {
            LayoutParams lp = (LayoutParams) mBorderView.getLayoutParams();
            mBorderView.layout(lp.x, lp.y, lp.x + lp.width, lp.y + lp.height);
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            child.cancelLongPress();
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        Logger.w(TAG, "addView: " + child.getClass().getSimpleName());
    }

    protected final void addViewInternal(View child) {
        super.addView(child, -1, generateDefaultLayoutParams());
    }

    @Override
    public void removeAllViews() {
        clearOccupiedCells();
        mChildrenLayout.removeAllViews();
    }

    @Override
    public void removeAllViewsInLayout() {
        if (mChildrenLayout.getChildCount() > 0) {
            clearOccupiedCells();
            mChildrenLayout.removeAllViewsInLayout();
        }
    }

    @Override
    public void removeView(View view) {
        markCellsAsUnoccupiedForView(view);
        mChildrenLayout.removeView(view);
    }

    @Override
    public void removeViewAt(int index) {
        markCellsAsUnoccupiedForView(mChildrenLayout.getChildAt(index));
        mChildrenLayout.removeViewAt(index);
    }

    @Override
    public void removeViewInLayout(View view) {
        markCellsAsUnoccupiedForView(view);
        mChildrenLayout.removeViewInLayout(view);
    }

    @Override
    public void removeViews(int start, int count) {
        for (int i = start; i < start + count; i++) {
            markCellsAsUnoccupiedForView(mChildrenLayout.getChildAt(i));
        }
        mChildrenLayout.removeViews(start, count);
    }

    @Override
    public void removeViewsInLayout(int start, int count) {
        for (int i = start; i < start + count; i++) {
            markCellsAsUnoccupiedForView(mChildrenLayout.getChildAt(i));
        }
        mChildrenLayout.removeViewsInLayout(start, count);
    }

    public void markCellsAsOccupiedForView(View view) {
        markCellsAsOccupiedForView(view, mOccupied);
    }

    public void markCellsAsOccupiedForView(View view, boolean[][] occupied) {
        if (view == null || view.getParent() != mChildrenLayout)
            return;
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        markCellsForView(lp.cellX, lp.cellY, lp.cellHSpan, lp.cellVSpan, occupied, true);
    }

    public void markCellsAsUnoccupiedForView(View view) {
        markCellsAsUnoccupiedForView(view, mOccupied);
    }

    public void markCellsAsUnoccupiedForView(View view, boolean occupied[][]) {
        if (view == null || view.getParent() != mChildrenLayout)
            return;
        LayoutParams lp = (LayoutParams) view.getLayoutParams();
        markCellsForView(lp.cellX, lp.cellY, lp.cellHSpan, lp.cellVSpan, occupied, false);
    }

    private void markCellsForView(int cellX, int cellY, int spanX, int spanY, boolean[][] occupied, boolean value) {
        if (cellX < 0 || cellY < 0)
            return;
        for (int x = cellX; x < cellX + spanX && x < mCountX; x++) {
            for (int y = cellY; y < cellY + spanY && y < mCountY; y++) {
                occupied[x][y] = value;
            }
        }
    }

    void clearOccupiedCells() {
        for (int x = 0; x < mCountX; x++) {
            for (int y = 0; y < mCountY; y++) {
                mOccupied[x][y] = false;
            }
        }
    }

    public boolean addViewToCellLayout(View child, int cellX, int cellY, int cellHSpan, int cellVSpan) {
        return addViewToCellLayout(child, new LayoutParams(cellX, cellY, cellHSpan, cellVSpan));
    }

    public boolean addViewToCellLayout(View child, LayoutParams lp) {
        if (child != null && child.getParent() == null && lp != null) {
            final int cellX = lp.cellX;
            final int cellY = lp.cellY;
            final int cellHSpan = lp.cellHSpan;
            final int cellVSpan = lp.cellVSpan;

            boolean overHorBounds = cellX + cellHSpan > mCountX;
            boolean overVerBounds = cellY + cellVSpan > mCountY;
            if (overHorBounds || overVerBounds) {
                Logger.w(TAG, "over bounds");
            }

            if (canAddToCellLayout(lp)) {
                boolean canAdd = true;
                int newCountX = mCountX;
                int newCountY = mCountY;
                if (overHorBounds) {
                    if (mFixedCountX) {
                        canAdd = false;
                    } else {
                        newCountX = cellX + cellHSpan;
                    }
                }
                if (overVerBounds) {
                    if (mFixedCountY) {
                        canAdd = false;
                    } else {
                        newCountY = cellY + cellVSpan;
                    }
                }
                if (canAdd) {
                    Logger.i(TAG, "addViewToCellLayout" + lp.toString());
                    setGridSize(newCountX, newCountY);
                    mChildrenLayout.addView(child, lp);
                    markCellsAsUnoccupiedForView(child);
                    return true;
                }
            } else {
                Logger.w(TAG, "Cell Occupied");
            }
        } else {
            Log.e(TAG, "child == null || child.getParent() != null || lp == null");
        }
        return false;
    }

    boolean canAddToCellLayout(LayoutParams lp) {
        final int cellX = lp.cellX;
        final int cellY = lp.cellY;
        final int spanX = lp.cellHSpan;
        final int spanY = lp.cellVSpan;
        if (cellX < 0 || cellY < 0) {
            return false;
        }
        for (int x = cellX; x < cellX + spanX && x < mCountX; x++) {
            for (int y = cellY; y < cellY + spanY && y < mCountY; y++) {
                if (mOccupied[x][y]) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean findCellForSpan(int[] cellXY, int spanX, int spanY) {
        return findCellForSpanThatIntersectsIgnoring(cellXY, spanX, spanY, -1, -1, null, mOccupied);
    }

    boolean findCellForSpanIgnoring(int[] cellXY, int spanX, int spanY, View ignoreView) {
        return findCellForSpanThatIntersectsIgnoring(cellXY, spanX, spanY, -1, -1, ignoreView,
                mOccupied);
    }

    boolean findCellForSpanThatIntersects(int[] cellXY, int spanX, int spanY, int intersectX,
                                          int intersectY) {
        return findCellForSpanThatIntersectsIgnoring(cellXY, spanX, spanY, intersectX, intersectY,
                null, mOccupied);
    }

    /**
     * The superset of the above two methods
     */
    boolean findCellForSpanThatIntersectsIgnoring(int[] cellXY, int spanX, int spanY,
                                                  int intersectX, int intersectY, View ignoreView, boolean occupied[][]) {
        // mark space take by ignoreView as available (method checks if
        // ignoreView is null)
        markCellsAsUnoccupiedForView(ignoreView, occupied);

        boolean foundCell = false;
        while (true) {
            int startX = 0;
            if (intersectX >= 0) {
                startX = Math.max(startX, intersectX - (spanX - 1));
            }
            int endX = mCountX - (spanX - 1);
            if (intersectX >= 0) {
                endX = Math.min(endX, intersectX + (spanX - 1) + (spanX == 1 ? 1 : 0));
            }
            int startY = 0;
            if (intersectY >= 0) {
                startY = Math.max(startY, intersectY - (spanY - 1));
            }
            int endY = mCountY - (spanY - 1);
            if (intersectY >= 0) {
                endY = Math.min(endY, intersectY + (spanY - 1) + (spanY == 1 ? 1 : 0));
            }

            for (int y = startY; y < endY && !foundCell; y++) {
                inner:
                for (int x = startX; x < endX; x++) {
                    for (int i = 0; i < spanX; i++) {
                        for (int j = 0; j < spanY; j++) {
                            if (occupied[x + i][y + j]) {
                                // small optimization: we can skip to after the
                                // column we just found
                                // an occupied cell
                                x += i;
                                continue inner;
                            }
                        }
                    }
                    if (cellXY != null) {
                        cellXY[0] = x;
                        cellXY[1] = y;
                    }
                    foundCell = true;
                    break;
                }
            }
            if (intersectX == -1 && intersectY == -1) {
                break;
            } else {
                // if we failed to find anything, try again but without any
                // requirements of
                // intersecting
                intersectX = -1;
                intersectY = -1;
                continue;
            }
        }

        // re-mark space taken by ignoreView as occupied
        markCellsAsOccupiedForView(ignoreView, occupied);
        return foundCell;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Logger.i(TAG, "onFocusChange : " + hasFocus);
        if (hasFocus && (v.getLayoutParams() instanceof LayoutParams)) {
            LayoutParams lp = (LayoutParams) v.getLayoutParams();
            LayoutParams bLp = (LayoutParams) mBorderView.getLayoutParams();
            bLp.x = lp.x + getPaddingLeft() - mBorderPadding.left;
            bLp.y = lp.y + getPaddingTop() - mBorderPadding.top;
            bLp.width = lp.width + mBorderPadding.left + mBorderPadding.right;
            bLp.height = lp.height + mBorderPadding.top + mBorderPadding.bottom;
            mBorderView.setVisibility(VISIBLE);
//            mBorderView.setScaleX(NORMAL_SCALE);
//            mBorderView.setScaleY(NORMAL_SCALE);
            ViewPropertyAnimator.animate(mBorderView).scaleX(NORMAL_SCALE).scaleY(NORMAL_SCALE).setDuration(0).start();

            ViewPropertyAnimator.animate(mBorderView).scaleX(FOCUSED_SCALE).scaleY(FOCUSED_SCALE).setDuration(ANIMTION_DURATION).start();
            mBorderView.invalidate();
            mBorderView.requestLayout();
        }
        if(hasFocus) {
            v.bringToFront();
        }

        ViewPropertyAnimator.animate(v).scaleX(hasFocus ? FOCUSED_SCALE : NORMAL_SCALE).scaleY(hasFocus ? FOCUSED_SCALE : NORMAL_SCALE).setDuration(ANIMTION_DURATION).start();
        v.invalidate();
        v.requestLayout();
    }

    public static class LayoutParams extends MarginLayoutParams {

        public int cellX;
        public int cellY;
        public int tmpCellX;
        public int tmpCellY;
        public int cellHSpan;
        public int cellVSpan;
        public boolean isLockedToGrid = true;
        public boolean useTmpCoords = false;

        int x;
        int y;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            cellHSpan = 1;
            cellVSpan = 1;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            cellHSpan = 1;
            cellVSpan = 1;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            cellHSpan = 1;
            cellVSpan = 1;
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.cellX = source.cellX;
            this.cellY = source.cellY;
            this.cellHSpan = source.cellHSpan;
            this.cellVSpan = source.cellVSpan;
        }

        public LayoutParams(int cellX, int cellY, int cellHSpan, int cellVSpan) {
            super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            this.cellX = cellX;
            this.cellY = cellY;
            this.cellHSpan = cellHSpan;
            this.cellVSpan = cellVSpan;
        }

        public void setup(int cellWidth, int cellHeight, int widthGap, int heightGap) {
            if (isLockedToGrid) {
                final int myCellHSpan = cellHSpan;
                final int myCellVSpan = cellVSpan;
                final int myCellX = useTmpCoords ? tmpCellX : cellX;
                final int myCellY = useTmpCoords ? tmpCellY : cellY;

                width = myCellHSpan * cellWidth + ((myCellHSpan - 1) * widthGap) - leftMargin
                        - rightMargin;
                height = myCellVSpan * cellHeight + ((myCellVSpan - 1) * heightGap) - topMargin
                        - bottomMargin;
                x = (int) (myCellX * (cellWidth + widthGap) + leftMargin);
                y = (int) (myCellY * (cellHeight + heightGap) + topMargin);
            }
        }

        public String toString() {
            return "(" + this.cellX + ", " + this.cellY + ")(" + this.cellHSpan + ", " + this.cellVSpan + ")";
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getWidth() {
            return width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getHeight() {
            return height;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getX() {
            return x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getY() {
            return y;
        }
    }

    static final class CellInfo {
        View cell;
        int cellX = -1;
        int cellY = -1;
        int spanX;
        int spanY;
        int screen;
        long container;

        @Override
        public String toString() {
            return "Cell[view=" + (cell == null ? "null" : cell.getClass()) + ", x=" + cellX
                    + ", y=" + cellY + "]";
        }
    }

}
