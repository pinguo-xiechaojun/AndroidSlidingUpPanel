package com.sothree.slidinguppanel.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.OverScroller;

/**
 * time: 15-12-29
 * description:
 *
 * @author crab
 */
public class MyResolverDrawerLayout extends ViewGroup implements NestedScrollingParent {
    private int mMaxWidth = -1;
    private int mMaxCollapsedHeight = 192*2;
    private int mMaxCollapsedHeightSmall = 56*2;
    private float mCollapseOffset;

    private int mCollapsibleHeight;
    private int mUncollapsibleHeight;
    private boolean mSmallCollapsed;
    private boolean mOpenOnLayout;
    private int mTopOffset;
    private final VelocityTracker mVelocityTracker;

    private float mInitialTouchX;
    private float mInitialTouchY;
    private float mLastTouchY;
    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;
    private boolean mOpenOnClick;
    private final int mTouchSlop;
    private final Rect mTempRect = new Rect();
    private boolean mIsDragging;
    private final OverScroller mScroller;
    private RunOnDismissedListener mRunOnDismissedListener;
    private OnDismissedListener mOnDismissedListener;
    private boolean mDismissOnScrollerFinished;
    private final NestedScrollingParentHelper mParentHelper;
    private final float mMinFlingVelocity;

    public interface OnDismissedListener {
        public void onDismissed();
    }

    private class RunOnDismissedListener implements Runnable {
        @Override
        public void run() {
            dispatchOnDismissed();
        }
    }
    void dispatchOnDismissed() {
        if (mOnDismissedListener != null) {
            mOnDismissedListener.onDismissed();
        }
        if (mRunOnDismissedListener != null) {
            removeCallbacks(mRunOnDismissedListener);
            mRunOnDismissedListener = null;
        }
    }
    public MyResolverDrawerLayout(Context context) {
        this(context, null);
    }

    public MyResolverDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyResolverDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mVelocityTracker = VelocityTracker.obtain();
        mTouchSlop= ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new OverScroller(context, AnimationUtils.loadInterpolator(context,
                android.R.interpolator.decelerate_quint));
        mMinFlingVelocity=ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        mParentHelper=new NestedScrollingParentHelper(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int sourceWidth = MeasureSpec.getSize(widthMeasureSpec);
        int widthSize = sourceWidth;
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // Single-use layout; just ignore the mode and use available space.
        // Clamp to maxWidth.
        if (mMaxWidth >= 0) {
            widthSize = Math.min(widthSize, mMaxWidth);
        }

        final int widthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        final int heightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        final int widthPadding = getPaddingLeft() + getPaddingRight();
        int heightUsed = getPaddingTop() + getPaddingBottom();

        // Measure always-show children first.
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.alwaysShow && child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthSpec, widthPadding, heightSpec, heightUsed);
                heightUsed += lp.topMargin + child.getMeasuredHeight() + lp.bottomMargin;
            }
        }
        final int alwaysShowHeight = heightUsed;

        // And now the rest.
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (!lp.alwaysShow && child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthSpec, widthPadding, heightSpec, heightUsed);
                heightUsed += lp.topMargin + child.getMeasuredHeight() + lp.bottomMargin;
            }
        }
        mCollapsibleHeight = Math.max(0,
                heightUsed - alwaysShowHeight - getMaxCollapsedHeight());
        mUncollapsibleHeight = heightUsed - mCollapsibleHeight;
            mCollapseOffset = mOpenOnLayout ? 0 : mCollapsibleHeight;

        mTopOffset = Math.max(0, heightSize - heightUsed) + (int) mCollapseOffset;

        setMeasuredDimension(sourceWidth, heightSize);
    }

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
         return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mParentHelper.onNestedScrollAccepted(child, target, ViewCompat.SCROLL_AXIS_VERTICAL);
    }
    @Override
    public void onStopNestedScroll(View child) {
        mParentHelper.onStopNestedScroll(child);
        if (mScroller.isFinished()) {
            smoothScrollTo(mCollapseOffset < mCollapsibleHeight / 2 ? 0 : mCollapsibleHeight, 0);
        }
    }
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed < 0) {
            performDrag(-dyUnconsumed);
        }
    }
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (dy > 0) {
            consumed[1] = (int) -performDrag(-dy);
        }
    }
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (velocityY > mMinFlingVelocity && mCollapseOffset != 0) {
            smoothScrollTo(0, velocityY);
            return true;
        }
        return false;
    }
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            final boolean keepGoing = !mScroller.isFinished();
            performDrag(mScroller.getCurrY() - mCollapseOffset);
            if (keepGoing) {
                postInvalidate();
            } else if (mDismissOnScrollerFinished && mOnDismissedListener != null) {
                mRunOnDismissedListener = new RunOnDismissedListener();
                post(mRunOnDismissedListener);
            }
        }
    }
        @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        if (!consumed && Math.abs(velocityY) > mMinFlingVelocity) {
            if (mOnDismissedListener != null
                    && velocityY < 0 && mCollapseOffset > mCollapsibleHeight) {
                smoothScrollTo(mCollapsibleHeight + mUncollapsibleHeight, velocityY);
                mDismissOnScrollerFinished = true;
            } else {
                smoothScrollTo(velocityY > 0 ? 0 : mCollapsibleHeight, velocityY);
            }
            return true;
        }
        return false;
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getWidth();

        int ypos = mTopOffset;
        int leftEdge = getPaddingLeft();
        int rightEdge = width - getPaddingRight();

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (child.getVisibility() == GONE) {
                continue;
            }

            int top = ypos + lp.topMargin;
            if (lp.ignoreOffset) {
                top -= mCollapseOffset;
            }
            final int bottom = top + child.getMeasuredHeight();

            final int childWidth = child.getMeasuredWidth();
            final int widthAvailable = rightEdge - leftEdge;
            final int left = leftEdge + (widthAvailable - childWidth) / 2;
            final int right = left + childWidth;

            child.layout(left, top, right, bottom);

            ypos = bottom + lp.bottomMargin;
        }
    }
    private void abortAnimation() {
        mScroller.abortAnimation();
        mRunOnDismissedListener = null;
        mDismissOnScrollerFinished = false;
    }
    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mInitialTouchX = ev.getX(newPointerIndex);
            mInitialTouchY = mLastTouchY = ev.getY(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    private void resetTouch() {
        mActivePointerId = MotionEvent.INVALID_POINTER_ID;
        mIsDragging = false;
        mOpenOnClick = false;
        mInitialTouchX = mInitialTouchY = mLastTouchY = 0;
        mVelocityTracker.clear();
    }

    /**
     * Note: this method doesn't take Z into account for overlapping views
     * since it is only used in contexts where this doesn't affect the outcome.
     */
    private View findChildUnder(float x, float y) {
        return findChildUnder(this, x, y);
    }

    private static View findChildUnder(ViewGroup parent, float x, float y) {
        final int childCount = parent.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = parent.getChildAt(i);
            if (isChildUnder(child, x, y)) {
                return child;
            }
        }
        return null;
    }

    private View findListChildUnder(float x, float y) {
        View v = findChildUnder(x, y);
        while (v != null) {
            x -= v.getX();
            y -= v.getY();
            if (v instanceof AbsListView) {
                // One more after this.
                return findChildUnder((ViewGroup) v, x, y);
            }
            v = v instanceof ViewGroup ? findChildUnder((ViewGroup) v, x, y) : null;
        }
        return v;
    }

    /**
     * This only checks clipping along the bottom edge.
     */
    private boolean isListChildUnderClipped(float x, float y) {
        final View listChild = findListChildUnder(x, y);
        return listChild != null && isDescendantClipped(listChild);
    }

    private boolean isDescendantClipped(View child) {
        mTempRect.set(0, 0, child.getWidth(), child.getHeight());
        offsetDescendantRectToMyCoords(child, mTempRect);
        View directChild;
        if (child.getParent() == this) {
            directChild = child;
        } else {
            View v = child;
            ViewParent p = child.getParent();
            while (p != this) {
                v = (View) p;
                p = v.getParent();
            }
            directChild = v;
        }

        // ResolverDrawerLayout lays out vertically in child order;
        // the next view and forward is what to check against.
        int clipEdge = getHeight() - getPaddingBottom();
        final int childCount = getChildCount();
        for (int i = indexOfChild(directChild) + 1; i < childCount; i++) {
            final View nextChild = getChildAt(i);
            if (nextChild.getVisibility() == GONE) {
                continue;
            }
            clipEdge = Math.min(clipEdge, nextChild.getTop());
        }
        return mTempRect.bottom > clipEdge;
    }

    private static boolean isChildUnder(View child, float x, float y) {
        final float left = child.getX();
        final float top = child.getY();
        final float right = left + child.getWidth();
        final float bottom = top + child.getHeight();
        return x >= left && y >= top && x < right && y < bottom;
    }
    private float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }
    private void smoothScrollTo(int yOffset, float velocity) {
        abortAnimation();
        final int sy = (int) mCollapseOffset;
        int dy = yOffset - sy;
        if (dy == 0) {
            return;
        }

        final int height = getHeight();
        final int halfHeight = height / 2;
        final float distanceRatio = Math.min(1f, 1.0f * Math.abs(dy) / height);
        final float distance = halfHeight + halfHeight *
                distanceInfluenceForSnapDuration(distanceRatio);

        int duration = 0;
        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
        } else {
            final float pageDelta = (float) Math.abs(dy) / height;
            duration = (int) ((pageDelta + 1) * 100);
        }
        duration = Math.min(duration, 300);

        mScroller.startScroll(0, sy, 0, dy, duration);
        postInvalidate();
    }

    private float performDrag(float dy) {
        final float newPos = Math.max(0, Math.min(mCollapseOffset + dy,
                mCollapsibleHeight + mUncollapsibleHeight));
        if (newPos != mCollapseOffset) {
            dy = newPos - mCollapseOffset;
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (!lp.ignoreOffset) {
                    child.offsetTopAndBottom((int) dy);
                }
            }
            final boolean isCollapsedOld = mCollapseOffset != 0;
            mCollapseOffset = newPos;
            mTopOffset += dy;
            final boolean isCollapsedNew = newPos != 0;
            if (isCollapsedOld != isCollapsedNew) {
                //notifyViewAccessibilityStateChangedIfNeeded(
                //       AccessibilityEvent.CONTENT_CHANGE_TYPE_UNDEFINED);
            }
            postInvalidate();
            return dy;
        }
        return 0;
    }
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    private int getMaxCollapsedHeight() {
        return isSmallCollapsed() ? mMaxCollapsedHeightSmall : mMaxCollapsedHeight;
    }

    public boolean isSmallCollapsed() {
        return mSmallCollapsed;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) p);
        } else if (p instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) p);
        }
        return new LayoutParams(p);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    public static class LayoutParams extends MarginLayoutParams {
        public boolean alwaysShow = false;
        public boolean ignoreOffset = false;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            final TypedArray a = c.obtainStyledAttributes(attrs,
                    R.styleable.MyResolverDrawerLayout_LayoutParams);
            alwaysShow = a.getBoolean(
                    R.styleable.MyResolverDrawerLayout_LayoutParams_layout_alwaysShow,
                    false);
            ignoreOffset = a.getBoolean(
                    R.styleable.MyResolverDrawerLayout_LayoutParams_layout_ignoreOffset,
                    false);

        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            alwaysShow = source.alwaysShow;
            ignoreOffset = source.ignoreOffset;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
