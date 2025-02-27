package com.gallery.photos.editpic.ImageEDITModule.edit.adapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.gallery.photos.editpic.R;

/* loaded from: classes.dex */
public class RecyclerTabLayout extends RecyclerView {
    protected static final float DEFAULT_POSITION_THRESHOLD = 0.6f;
    protected static final long DEFAULT_SCROLL_DURATION = 200;
    protected static final float POSITION_THRESHOLD_ALLOWABLE = 0.001f;
    protected Adapter<?> mAdapter;
    protected int mIndicatorGap;
    protected int mIndicatorHeight;
    protected Paint mIndicatorPaint;
    protected int mIndicatorPosition;
    protected int mIndicatorScroll;
    protected LinearLayoutManager mLinearLayoutManager;
    private int mOldPosition;
    protected float mOldPositionOffset;
    private int mOldScrollOffset;
    protected float mPositionThreshold;
    protected RecyclerOnScrollListener mRecyclerOnScrollListener;
    protected boolean mRequestScrollToTab;
    protected boolean mScrollEanbled;
    protected int mTabBackgroundResId;
    protected int mTabMaxWidth;
    protected int mTabMinWidth;
    protected int mTabOnScreenLimit;
    protected int mTabPaddingBottom;
    protected int mTabPaddingEnd;
    protected int mTabPaddingStart;
    protected int mTabPaddingTop;
    protected int mTabSelectedTextColor;
    protected boolean mTabSelectedTextColorSet;
    protected int mTabTextAppearance;
    protected ViewPager mViewPager;

    public RecyclerTabLayout(Context context) {
        this(context, null);
    }

    public RecyclerTabLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RecyclerTabLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setWillNotDraw(false);
        this.mIndicatorPaint = new Paint();
        getAttributes(context, attributeSet, i);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()) { // from class: com.gallery.photos.editphotovideo.adapters.RecyclerTabLayout.1
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean canScrollHorizontally() {
                return RecyclerTabLayout.this.mScrollEanbled;
            }
        };
        this.mLinearLayoutManager = linearLayoutManager;
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        setLayoutManager(this.mLinearLayoutManager);
        setItemAnimator(null);
        this.mPositionThreshold = DEFAULT_POSITION_THRESHOLD;
    }

    private void getAttributes(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, new int[]{R.attr.mScrollEnabled, R.attr.mTabBackground, R.attr.mTabIndicatorColor, R.attr.mTabIndicatorHeight, R.attr.mTabMaxWidth, R.attr.mTabMinWidth, R.attr.mTabOnScreenLimit, R.attr.mTabPadding, R.attr.mTabPaddingBottom, R.attr.mTabPaddingEnd, R.attr.mTabPaddingStart, R.attr.mTabPaddingTop, R.attr.mTabSelectedTextColor, R.attr.mTabTextAppearance}, i, R.style.mRecyclerTabLayout);
        setIndicatorColor(obtainStyledAttributes.getColor(2, 0));
        setIndicatorHeight(obtainStyledAttributes.getDimensionPixelSize(3, 0));
        this.mTabTextAppearance = obtainStyledAttributes.getResourceId(13, R.style.mRecyclerTabLayout_Tab);
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(7, 0);
        this.mTabPaddingBottom = dimensionPixelSize;
        this.mTabPaddingEnd = dimensionPixelSize;
        this.mTabPaddingTop = dimensionPixelSize;
        this.mTabPaddingStart = dimensionPixelSize;
        this.mTabPaddingStart = obtainStyledAttributes.getDimensionPixelSize(10, dimensionPixelSize);
        this.mTabPaddingTop = obtainStyledAttributes.getDimensionPixelSize(11, this.mTabPaddingTop);
        this.mTabPaddingEnd = obtainStyledAttributes.getDimensionPixelSize(9, this.mTabPaddingEnd);
        this.mTabPaddingBottom = obtainStyledAttributes.getDimensionPixelSize(8, this.mTabPaddingBottom);
        if (obtainStyledAttributes.hasValue(12)) {
            this.mTabSelectedTextColor = obtainStyledAttributes.getColor(12, 0);
            this.mTabSelectedTextColorSet = true;
        }
        int integer = obtainStyledAttributes.getInteger(6, 0);
        this.mTabOnScreenLimit = integer;
        if (integer == 0) {
            this.mTabMinWidth = obtainStyledAttributes.getDimensionPixelSize(5, 0);
            this.mTabMaxWidth = obtainStyledAttributes.getDimensionPixelSize(4, 0);
        }
        this.mTabBackgroundResId = obtainStyledAttributes.getResourceId(1, 0);
        this.mScrollEanbled = obtainStyledAttributes.getBoolean(0, true);
        obtainStyledAttributes.recycle();
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        RecyclerOnScrollListener recyclerOnScrollListener = this.mRecyclerOnScrollListener;
        if (recyclerOnScrollListener != null) {
            removeOnScrollListener(recyclerOnScrollListener);
            this.mRecyclerOnScrollListener = null;
        }
        super.onDetachedFromWindow();
    }

    public void setIndicatorColor(int i) {
        this.mIndicatorPaint.setColor(i);
    }

    public void setIndicatorHeight(int i) {
        this.mIndicatorHeight = i;
    }

    public void setPositionThreshold(float f) {
        this.mPositionThreshold = f;
    }

    public void setUpWithAdapter(Adapter<?> adapter) {
        this.mAdapter = adapter;
        ViewPager viewPager = adapter.getViewPager();
        this.mViewPager = viewPager;
        if (viewPager.getAdapter() != null) {
            this.mViewPager.addOnPageChangeListener(new ViewPagerOnPageChangeListener(this));
            setAdapter(adapter);
            scrollToTab(this.mViewPager.getCurrentItem());
            return;
        }
        throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
    }

    public void setCurrentItem(int i, boolean z) {
        ViewPager viewPager = this.mViewPager;
        if (viewPager != null) {
            viewPager.setCurrentItem(i, z);
            scrollToTab(this.mViewPager.getCurrentItem());
        } else if (!z || i == this.mIndicatorPosition) {
            scrollToTab(i);
        } else {
            startAnimation(i);
        }
    }

    public void startAnimation(final int i) {
        ValueAnimator ofFloat;
        View findViewByPosition = this.mLinearLayoutManager.findViewByPosition(i);
        float abs = findViewByPosition != null ? Math.abs((getMeasuredWidth() / 2.0f) - (findViewByPosition.getX() + (findViewByPosition.getMeasuredWidth() / 2.0f))) / findViewByPosition.getMeasuredWidth() : 1.0f;
        if (i < this.mIndicatorPosition) {
            ofFloat = ValueAnimator.ofFloat(abs, 0.0f);
        } else {
            ofFloat = ValueAnimator.ofFloat(-abs, 0.0f);
        }
        ofFloat.setDuration(DEFAULT_SCROLL_DURATION);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.gallery.photos.editphotovideo.adapters.RecyclerTabLayout.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                RecyclerTabLayout.this.scrollToTab(i, ((Float) valueAnimator.getAnimatedValue()).floatValue(), true);
            }
        });
        ofFloat.start();
    }

    public void scrollToTab(int i) {
        scrollToTab(i, 0.0f, false);
        this.mAdapter.setCurrentIndicatorPosition(i);
        this.mAdapter.notifyDataSetChanged();
    }

    public void scrollToTab(int i, float f, boolean z) {
        int i2;
        int i3;
        View findViewByPosition = this.mLinearLayoutManager.findViewByPosition(i);
        View findViewByPosition2 = this.mLinearLayoutManager.findViewByPosition(i + 1);
        int i4 = 0;
        if (findViewByPosition != null) {
            int measuredWidth = getMeasuredWidth();
            float measuredWidth2 = i == 0 ? 0.0f : (measuredWidth / 2.0f) - (findViewByPosition.getMeasuredWidth() / 2.0f);
            float measuredWidth3 = findViewByPosition.getMeasuredWidth() + measuredWidth2;
            if (findViewByPosition2 != null) {
                float measuredWidth4 = (measuredWidth3 - ((measuredWidth / 2.0f) - (findViewByPosition2.getMeasuredWidth() / 2.0f))) * f;
                i3 = (int) (measuredWidth2 - measuredWidth4);
                if (i == 0) {
                    float measuredWidth5 = (findViewByPosition2.getMeasuredWidth() - findViewByPosition.getMeasuredWidth()) / 2;
                    this.mIndicatorGap = (int) (measuredWidth5 * f);
                    this.mIndicatorScroll = (int) ((findViewByPosition.getMeasuredWidth() + measuredWidth5) * f);
                } else {
                    this.mIndicatorGap = (int) (((findViewByPosition2.getMeasuredWidth() - findViewByPosition.getMeasuredWidth()) / 2) * f);
                    this.mIndicatorScroll = (int) measuredWidth4;
                }
            } else {
                i3 = (int) measuredWidth2;
                this.mIndicatorScroll = 0;
                this.mIndicatorGap = 0;
            }
            if (z) {
                this.mIndicatorScroll = 0;
                this.mIndicatorGap = 0;
            }
            i4 = i3;
        } else {
            if (getMeasuredWidth() > 0 && (i2 = this.mTabMaxWidth) > 0 && this.mTabMinWidth == i2) {
                getMeasuredWidth();
            }
            this.mRequestScrollToTab = true;
        }
        updateCurrentIndicatorPosition(i, f - this.mOldPositionOffset, f);
        this.mIndicatorPosition = i;
        stopScroll();
        if (i != this.mOldPosition || i4 != this.mOldScrollOffset) {
            this.mLinearLayoutManager.scrollToPositionWithOffset(i, i4);
        }
        if (this.mIndicatorHeight > 0) {
            invalidate();
        }
        this.mOldPosition = i;
        this.mOldScrollOffset = i4;
        this.mOldPositionOffset = f;
    }

    public void updateCurrentIndicatorPosition(int i, float f, float f2) {
        Adapter<?> adapter = this.mAdapter;
        if (adapter != null) {
            if (f > 0.0f && f2 >= this.mPositionThreshold - POSITION_THRESHOLD_ALLOWABLE) {
                i++;
            } else if (f >= 0.0f || f2 > (1.0f - this.mPositionThreshold) + POSITION_THRESHOLD_ALLOWABLE) {
                i = -1;
            }
            if (i < 0 || i == adapter.getCurrentIndicatorPosition()) {
                return;
            }
            this.mAdapter.setCurrentIndicatorPosition(i);
            this.mAdapter.notifyDataSetChanged();
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onDraw(Canvas canvas) {
        int left;
        int right;
        int i;
        View findViewByPosition = this.mLinearLayoutManager.findViewByPosition(this.mIndicatorPosition);
        if (findViewByPosition != null) {
            this.mRequestScrollToTab = false;
            if (isLayoutRtl()) {
                left = (findViewByPosition.getLeft() - this.mIndicatorScroll) - this.mIndicatorGap;
                right = findViewByPosition.getRight() - this.mIndicatorScroll;
                i = this.mIndicatorGap;
            } else {
                left = (findViewByPosition.getLeft() + this.mIndicatorScroll) - this.mIndicatorGap;
                right = findViewByPosition.getRight() + this.mIndicatorScroll;
                i = this.mIndicatorGap;
            }
            canvas.drawRect(left, getHeight() - this.mIndicatorHeight, right + i, getHeight(), this.mIndicatorPaint);
            return;
        }
        if (this.mRequestScrollToTab) {
            this.mRequestScrollToTab = false;
            scrollToTab(this.mViewPager.getCurrentItem());
        }
    }

    public boolean isLayoutRtl() {
        return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    protected static class RecyclerOnScrollListener extends OnScrollListener {
        public int mDx;
        protected LinearLayoutManager mLinearLayoutManager;
        protected RecyclerTabLayout mRecyclerTabLayout;

        public RecyclerOnScrollListener(RecyclerTabLayout recyclerTabLayout, LinearLayoutManager linearLayoutManager) {
            this.mRecyclerTabLayout = recyclerTabLayout;
            this.mLinearLayoutManager = linearLayoutManager;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            this.mDx += i;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 0) {
                if (this.mDx > 0) {
                    selectCenterTabForRightScroll();
                } else {
                    selectCenterTabForLeftScroll();
                }
                this.mDx = 0;
            }
        }

        public void selectCenterTabForRightScroll() {
            int findLastVisibleItemPosition = this.mLinearLayoutManager.findLastVisibleItemPosition();
            int width = this.mRecyclerTabLayout.getWidth() / 2;
            for (int findFirstVisibleItemPosition = this.mLinearLayoutManager.findFirstVisibleItemPosition(); findFirstVisibleItemPosition <= findLastVisibleItemPosition; findFirstVisibleItemPosition++) {
                View findViewByPosition = this.mLinearLayoutManager.findViewByPosition(findFirstVisibleItemPosition);
                if (findViewByPosition.getLeft() + findViewByPosition.getWidth() >= width) {
                    this.mRecyclerTabLayout.setCurrentItem(findFirstVisibleItemPosition, false);
                    return;
                }
            }
        }

        public void selectCenterTabForLeftScroll() {
            int findFirstVisibleItemPosition = this.mLinearLayoutManager.findFirstVisibleItemPosition();
            int width = this.mRecyclerTabLayout.getWidth() / 2;
            for (int findLastVisibleItemPosition = this.mLinearLayoutManager.findLastVisibleItemPosition(); findLastVisibleItemPosition >= findFirstVisibleItemPosition; findLastVisibleItemPosition--) {
                if (this.mLinearLayoutManager.findViewByPosition(findLastVisibleItemPosition).getLeft() <= width) {
                    this.mRecyclerTabLayout.setCurrentItem(findLastVisibleItemPosition, false);
                    return;
                }
            }
        }
    }

    protected static class ViewPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private final RecyclerTabLayout mRecyclerTabLayout;
        private int mScrollState;

        public ViewPagerOnPageChangeListener(RecyclerTabLayout recyclerTabLayout) {
            this.mRecyclerTabLayout = recyclerTabLayout;
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
            this.mRecyclerTabLayout.scrollToTab(i, f, false);
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
            this.mScrollState = i;
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            if (this.mScrollState != 0 || this.mRecyclerTabLayout.mIndicatorPosition == i) {
                return;
            }
            this.mRecyclerTabLayout.scrollToTab(i);
        }
    }

    public static abstract class Adapter<T extends ViewHolder> extends RecyclerView.Adapter<T> {
        protected int mIndicatorPosition;
        protected ViewPager mViewPager;

        public Adapter(ViewPager viewPager) {
            this.mViewPager = viewPager;
        }

        public ViewPager getViewPager() {
            return this.mViewPager;
        }

        public void setCurrentIndicatorPosition(int i) {
            this.mIndicatorPosition = i;
        }

        public int getCurrentIndicatorPosition() {
            return this.mIndicatorPosition;
        }
    }

    public static class DefaultAdapter extends Adapter<DefaultAdapter.ViewHolder>{
        protected static final int MAX_TAB_TEXT_LINES = 2;
        private int mTabBackgroundResId;
        private int mTabMaxWidth;
        private int mTabMinWidth;
        private int mTabOnScreenLimit;
        protected int mTabPaddingBottom;
        protected int mTabPaddingEnd;
        protected int mTabPaddingStart;
        protected int mTabPaddingTop;
        protected int mTabSelectedTextColor;
        protected boolean mTabSelectedTextColorSet;
        protected int mTabTextAppearance;

        public DefaultAdapter(ViewPager viewPager) {
            super(viewPager);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TabTextView tabTextView = new TabTextView(viewGroup.getContext());
            if (this.mTabSelectedTextColorSet) {
                tabTextView.setTextColor(tabTextView.createColorStateList(tabTextView.getCurrentTextColor(), this.mTabSelectedTextColor));
            }
            ViewCompat.setPaddingRelative(tabTextView, this.mTabPaddingStart, this.mTabPaddingTop, this.mTabPaddingEnd, this.mTabPaddingBottom);
            tabTextView.setTextAppearance(viewGroup.getContext(), this.mTabTextAppearance);
            tabTextView.setGravity(17);
            tabTextView.setMaxLines(2);
            tabTextView.setEllipsize(TextUtils.TruncateAt.END);
            if (this.mTabOnScreenLimit > 0) {
                int measuredWidth = viewGroup.getMeasuredWidth() / this.mTabOnScreenLimit;
                tabTextView.setMaxWidth(measuredWidth);
                tabTextView.setMinWidth(measuredWidth);
            } else {
                int i2 = this.mTabMaxWidth;
                if (i2 > 0) {
                    tabTextView.setMaxWidth(i2);
                }
                tabTextView.setMinWidth(this.mTabMinWidth);
            }
            tabTextView.setTextAppearance(tabTextView.getContext(), this.mTabTextAppearance);
            if (this.mTabSelectedTextColorSet) {
                tabTextView.setTextColor(tabTextView.createColorStateList(tabTextView.getCurrentTextColor(), this.mTabSelectedTextColor));
            }
            if (this.mTabBackgroundResId != 0) {
                tabTextView.setBackgroundDrawable(AppCompatResources.getDrawable(tabTextView.getContext(), this.mTabBackgroundResId));
            }
            tabTextView.setLayoutParams(createLayoutParamsForTabs());
            return new ViewHolder(tabTextView);
        }


        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.title.setText(getViewPager().getAdapter().getPageTitle(i));
            viewHolder.title.setSelected(getCurrentIndicatorPosition() == i);
        }


        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return getViewPager().getAdapter().getCount();
        }

        public void setTabPadding(int i, int i2, int i3, int i4) {
            this.mTabPaddingStart = i;
            this.mTabPaddingTop = i2;
            this.mTabPaddingEnd = i3;
            this.mTabPaddingBottom = i4;
        }

        public void setTabTextAppearance(int i) {
            this.mTabTextAppearance = i;
        }

        public void setTabSelectedTextColor(boolean z, int i) {
            this.mTabSelectedTextColorSet = z;
            this.mTabSelectedTextColor = i;
        }

        public void setTabMaxWidth(int i) {
            this.mTabMaxWidth = i;
        }

        public void setTabMinWidth(int i) {
            this.mTabMinWidth = i;
        }

        public void setTabBackgroundResId(int i) {
            this.mTabBackgroundResId = i;
        }

        public void setTabOnScreenLimit(int i) {
            this.mTabOnScreenLimit = i;
        }

        public LayoutParams createLayoutParamsForTabs() {
            return new LayoutParams(-2, -1);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title;

            public ViewHolder(View view) {
                super(view);
                this.title = (TextView) view;
                view.setOnClickListener(new OnClickListener() { // from class: com.gallery.photos.editphotovideo.adapters.RecyclerTabLayout.DefaultAdapter.ViewHolder.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view2) {
                        int adapterPosition = ViewHolder.this.getAdapterPosition();
                        if (adapterPosition != -1) {
                            DefaultAdapter.this.getViewPager().setCurrentItem(adapterPosition, true);
                        }
                    }
                });
            }
        }



    }

    public static class TabTextView extends AppCompatTextView {
        public TabTextView(Context context) {
            super(context);
        }

        public ColorStateList createColorStateList(int i, int i2) {
            return new ColorStateList(new int[][]{SELECTED_STATE_SET, EMPTY_STATE_SET}, new int[]{i2, i});
        }
    }
}
