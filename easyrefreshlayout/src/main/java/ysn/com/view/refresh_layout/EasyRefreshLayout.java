package ysn.com.view.refresh_layout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import ysn.com.view.refresh_layout.constant.RefreshState;
import ysn.com.view.refresh_layout.constant.RefreshTag;
import ysn.com.view.refresh_layout.listener.FootListener;
import ysn.com.view.refresh_layout.listener.HeadListener;
import ysn.com.view.refresh_layout.listener.OnRefreshLoadMoreListener;
import ysn.com.view.refresh_layout.utils.AnimUtils;

/**
 * @Author yangsanning
 * @ClassName EasyRefreshLayout
 * @Description 刷新布局
 * @Date 2020/2/12
 * @History 2020/2/12 author: description:
 */
public class EasyRefreshLayout extends ViewGroup implements NestedScrollingParent, NestedScrollingChild {

    private static final float DRAG_RATE = 0.5f;
    public int animationDuration = 300;

    public View headView;
    public View footView;
    private View contentView;

    private int footHeight;
    private int headerHeight;
    public int bottomScroll;

    private RefreshState refreshStatus = RefreshState.NONE;
    private RefreshTag refreshTag = RefreshTag.NONE;
    public boolean isLoading = Boolean.FALSE;
    public boolean isRefreshing = Boolean.FALSE;
    private boolean isRefreshSuccess = Boolean.FALSE;
    private boolean isLoadSuccess = Boolean.FALSE;
    private Runnable refreshDelayRunnable, loadMoreDelayRunnable;

    private boolean enableLoadMore = Boolean.TRUE;
    private boolean enableAutoLoadMore = Boolean.FALSE;
    private boolean enableRefresh = Boolean.TRUE;
    private boolean enableAutoRefresh = Boolean.FALSE;

    private NestedScrollingParentHelper nestedScrollingParentHelper;
    private NestedScrollingChildHelper nestedScrollingChildHelper;
    private OnChildScrollUpCallback onChildScrollUpCallback;
    private boolean nestedScrollInProgress;

    private final int[] parentScrollConsumed = new int[2];
    private final int[] parentOffsetInWindow = new int[2];

    /**
     * 偏移值
     */
    private int refreshOffsetValue;
    private int loadMoreOffsetValue;

    /**
     * 触发移动事件的最短距离(如果小于这个距离就不触发移动控件)
     */
    private float touchShortestDistance;

    private float downY;
    private float lastY;
    private boolean isDrag;

    private HeadListener headListener;
    private FootListener footListener;
    private OnRefreshLoadMoreListener onRefreshLoadMoreListener;

    public EasyRefreshLayout(Context context) {
        this(context, null);
    }

    public EasyRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EasyRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        nestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
        // 获取触发移动事件的最短距离
        touchShortestDistance = ViewConfiguration.get(context).getScaledTouchSlop();
        ensureContentViewNotNull();
    }

    /**
     * 确保contentView不为空
     */
    private void ensureContentViewNotNull() {
        if (contentView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View childView = getChildAt(i);
                if (!childView.equals(headView) || !childView.equals(footView)) {
                    contentView = childView;
                    break;
                }
            }
        }
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
        nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        // 分配给嵌套的父级
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        refreshOffsetValue = 0;
        loadMoreOffsetValue = 0;
        nestedScrollInProgress = true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        setNestedScrollingEnabled(enabled);
        super.setEnabled(enabled);
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        nestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return nestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return nestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        nestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return nestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return nestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return nestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return nestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return nestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public int getNestedScrollAxes() {
        return nestedScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        return (nestedScrollAxes & 2) != 0;
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        if (headView != null && enableRefresh && !isRefreshing && refreshTag == RefreshTag.REFRESH
            && dy > 0 && refreshOffsetValue > 0) {
            refreshOffsetValue -= dy;
            if (refreshOffsetValue <= 0) {
                refreshOffsetValue = 0;
                dy = (int) (-getScrollY() / DRAG_RATE);
            }
            startRefresh(-dy);
            consumed[1] = dy;
        }

        if (footView != null && enableLoadMore && !isLoading && refreshTag == RefreshTag.LOAD_MORE
            && dy < 0 && getScrollY() >= bottomScroll && loadMoreOffsetValue > 0) {
            loadMoreOffsetValue += dy;
            startLoadMore(dy);
            consumed[1] = dy;
        }

        final int[] parentConsumed = parentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }

    private void startRefresh(int dy) {
        int scrollY = getScrollY();
        if (refreshTag == RefreshTag.REFRESH) {
            scrollBy(dy);
            int end = (headListener != null && headListener.getRefreshHeight() != 0) ?
                headListener.getRefreshHeight() : headerHeight;
            if (Math.abs(scrollY) > end) {
                refreshStatusChange(RefreshState.REFRESH_AFTER);
            } else {
                refreshStatusChange(RefreshState.REFRESH_BEFORE);
            }
        }
    }

    /**
     * 统一处理刷新状态
     */
    private void refreshStatusChange(RefreshState refreshStatus) {
        this.refreshStatus = refreshStatus;
        int scrollY = getScrollY();
        switch (refreshStatus) {
            case NONE:
                isRefreshSuccess = false;
                isLoadSuccess = false;
                break;
            case REFRESH_BEFORE:
                if (headListener != null) {
                    headListener.onRefreshBefore(scrollY, headerHeight);
                }
                break;
            case REFRESH_AFTER:
                if (headListener != null) {
                    headListener.onRefreshAfter(scrollY, headerHeight);
                }
                break;
            case REFRESH_READY:
                if (headListener != null) {
                    headListener.onRefreshReady(scrollY, headerHeight);
                }
                break;
            case REFRESH_DOING:
                if (headListener != null) {
                    headListener.onRefreshing(scrollY, headerHeight);
                }
                if (onRefreshLoadMoreListener != null) {
                    onRefreshLoadMoreListener.onRefresh();
                }
                break;
            case REFRESH_COMPLETE:
                if (headListener != null) {
                    headListener.onRefreshComplete(scrollY, headerHeight, isRefreshSuccess);
                }
                break;
            case REFRESH_CANCEL:
                if (headListener != null) {
                    headListener.onRefreshCancel(scrollY, headerHeight);
                }
                break;
            case LOAD_BEFORE:
                if (footListener != null) {
                    footListener.onLoadBefore(scrollY);
                }
                break;
            case LOAD_AFTER:
                if (footListener != null) {
                    footListener.onLoadAfter(scrollY);
                }
                break;
            case LOAD_MORE_READY:
                if (footListener != null) {
                    footListener.onLoadReady(scrollY);
                }
                break;
            case LOAD_MORE_DOING:
                if (footListener != null) {
                    footListener.onLoading(scrollY);
                }
                if (onRefreshLoadMoreListener != null) {
                    onRefreshLoadMoreListener.onLoadMore();
                }
                break;
            case LOAD_MORE_COMPLETE:
                if (footListener != null) {
                    footListener.onLoadComplete(scrollY, isLoadSuccess);
                }
                break;
            case LOAD_MORE_CANCEL:
                if (footListener != null) {
                    footListener.onLoadCancel(scrollY);
                }
                break;
            default:
                break;
        }
    }

    public void scrollBy(int dy) {
        scrollBy(0, (int) (-dy * DRAG_RATE));
    }

    private void startLoadMore(int dy) {
        if (footView == null && !enableAutoLoadMore) {
            return;
        }
        if (refreshTag == RefreshTag.LOAD_MORE) {
            scrollBy(-dy);
            if (getScrollY() >= bottomScroll + footHeight) {
                refreshStatusChange(RefreshState.LOAD_AFTER);
            } else {
                refreshStatusChange(RefreshState.LOAD_BEFORE);
            }
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        // 优先父级使用
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, parentOffsetInWindow);

        final int dy = dyUnconsumed + parentOffsetInWindow[1];
        if (enableRefresh && headView != null && dy < 0 && !isRefreshing && !canChildScrollUp()) {
            refreshOffsetValue += Math.abs(dy);
            if (refreshTag == RefreshTag.NONE || refreshOffsetValue != 0) {
                refreshTag = RefreshTag.REFRESH;
            }
            startRefresh(Math.abs(dy));
        }

        if (enableLoadMore && (enableAutoLoadMore || footView != null) && getScrollY() >= bottomScroll
            && dy > 0 && !isLoading && loadMoreOffsetValue <= 4 * footHeight) {
            loadMoreOffsetValue += dy;
            if (refreshTag == RefreshTag.NONE || loadMoreOffsetValue != 0) {
                refreshTag = RefreshTag.LOAD_MORE;
            }
            startLoadMore(dy);
        }

    }

    /**
     * 判断子视图是否可以向上滚动
     */
    public boolean canChildScrollUp() {
        if (onChildScrollUpCallback != null) {
            return onChildScrollUpCallback.canChildScrollUp(this, contentView);
        }
        return ViewCompat.canScrollVertically(contentView, -1);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        nestedScrollingParentHelper.onStopNestedScroll(target);
        nestedScrollInProgress = Boolean.FALSE;
        resetScroll();
        stopNestedScroll();
    }

    private void resetScroll() {
        // 判断本次触摸系列事件结束时,Layout的状态
        switch (refreshStatus) {
            //下拉刷新
            case REFRESH_BEFORE:
                scrollToDefaultStatus(RefreshState.REFRESH_CANCEL);
                break;
            case REFRESH_AFTER:
                scrollToRefreshStatus();
                break;
            //上拉加载更多
            case LOAD_BEFORE:
                scrollToDefaultStatus(RefreshState.LOAD_MORE_CANCEL);
                break;
            case LOAD_AFTER:
                scrollToLoadStatus();
                break;
            default:
                refreshTag = RefreshTag.NONE;
                break;
        }
        refreshOffsetValue = 0;
        loadMoreOffsetValue = 0;
    }

    private void scrollToDefaultStatus(final RefreshState startStatus) {
        int start = getScrollY();
        int end = 0;
        startAnim(start, end, new OnAnimListener() {
            @Override
            public void onStart() {
                refreshStatusChange(startStatus);
            }

            @Override
            public void onEnd() {
                refreshStatusChange(RefreshState.NONE);
            }
        });
    }

    private void startAnim(int start, int end, final OnAnimListener listener) {
        AnimUtils.ofInt(animationDuration, new AnimUtils.OnAnimActionListener() {
            @Override
            public void onAnimUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                scrollTo(0, value);
                postInvalidate();
                listener.onStart();
            }

            @Override
            public void onAnimEnd(Animator animation) {
                listener.onEnd();
            }
        }, start, end);
    }

    private void scrollToRefreshStatus() {
        isRefreshing = Boolean.TRUE;
        int start = getScrollY();
        int end = headListener != null && headListener.getRefreshHeight() != 0 ? -headListener.getRefreshHeight() : -headerHeight;
        startAnim(start, end, new OnAnimListener() {
            @Override
            public void onStart() {
                refreshStatusChange(RefreshState.REFRESH_READY);
            }

            @Override
            public void onEnd() {
                refreshStatusChange(RefreshState.REFRESH_DOING);
            }
        });
    }

    private void scrollToLoadStatus() {
        isLoading = Boolean.TRUE;
        int start = getScrollY();
        int end = footHeight + bottomScroll;
        startAnim(start, end, new OnAnimListener() {
            @Override
            public void onStart() {
                refreshStatusChange(RefreshState.LOAD_MORE_READY);
            }

            @Override
            public void onEnd() {
                refreshStatusChange(RefreshState.LOAD_MORE_DOING);
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ensureContentViewNotNull();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (contentView == null) {
            ensureContentViewNotNull();
        }
        if (contentView == null) {
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();
        int viewPaddingLeft = getPaddingLeft();
        int viewPaddingTop = getPaddingTop();
        int viewPaddingRight = getPaddingLeft();
        int viewPaddingBottom = getPaddingBottom();

        int childWidth = viewWidth - viewPaddingLeft - viewPaddingRight;
        int childHeight = viewHeight - viewPaddingTop - viewPaddingBottom;

        if (headView != null) {
            headerHeight = headView.getMeasuredHeight();
            headView.layout(viewPaddingLeft, (viewPaddingTop - headerHeight), (viewPaddingLeft + childWidth), viewPaddingTop);
        }

        if (contentView == null) {
            ensureContentViewNotNull();
        }
        if (contentView == null) {
            return;
        }
        contentView.layout(viewPaddingLeft, viewPaddingTop, (viewPaddingLeft + childWidth), (viewPaddingTop + childHeight));

        int contentViewHeight = contentView.getMeasuredHeight();
        if (footView != null) {
            footHeight = footView.getMeasuredHeight();
            footView.layout((0), contentViewHeight, footView.getMeasuredWidth(), (contentViewHeight + footHeight));
        }
        bottomScroll = contentViewHeight - getMeasuredHeight();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureContentViewNotNull();

        if (skip()) {
            return false;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrag = Boolean.FALSE;
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float y = ev.getY();
                startDragging(y);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDrag = Boolean.FALSE;
                break;
            default:
                break;
        }
        return isDrag;
    }

    private boolean skip() {
        return !isEnabled() || isRefreshing || isLoading || canChildScrollUp() || nestedScrollInProgress;
    }

    private void startDragging(float y) {
        final float yDiff = y - downY;
        if (yDiff > touchShortestDistance && !isDrag) {
            lastY = downY + touchShortestDistance;
            isDrag = true;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (skip()) {
            return false;
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrag =  Boolean.FALSE;
                break;
            case MotionEvent.ACTION_MOVE: {
                final float y = ev.getY();
                startDragging(y);
                if (isDrag) {
                    final float scrollValue = (y - lastY);
                    if (scrollValue < 0 && getScrollY() > 0) {
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        return false;
                    }
                    refreshTag = RefreshTag.REFRESH;
                    startRefresh((int) scrollValue);
                }
                lastY = y;
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (isDrag) {
                    isDrag =  Boolean.FALSE;
                    resetScroll();
                }
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                resetScroll();
                return false;
            default:
                break;
        }
        return true;
    }

    /**
     * 添加刷新头(如果头布局实现了{@link HeadListener} 则进行替换)
     */
    public EasyRefreshLayout addHeadView(@NonNull View headView) {
        this.headView = headView;
        if (headView instanceof HeadListener) {
            headListener = (HeadListener) headView;
        }
        addView(headView, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        return this;
    }

    /**
     * 添加加载布局(如果加载布局实现了{@link FootListener} 则进行替换)
     */
    public EasyRefreshLayout addFootView(@NonNull View footView) {
        this.footView = footView;
        if (footView instanceof FootListener) {
            footListener = (FootListener) footView;
        }
        addView(footView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        return this;
    }

    /**
     * 是否启用刷新
     */
    public void setEnableRefresh(boolean enableRefresh) {
        this.enableRefresh = enableRefresh;
    }

    /**
     * 是否启用自动刷新
     */
    public void setEnableAutoRefresh(boolean enableAutoRefresh) {
        this.enableAutoRefresh = enableAutoRefresh;
        autoRefresh();
    }

    /**
     * 自动刷新
     */
    private void autoRefresh() {
        if (!enableAutoRefresh) {
            return;
        }
        isRefreshing = true;
        measureView(headView);
        int refreshHeight = 0;
        if (headListener != null) {
            refreshHeight = headListener.getRefreshHeight();
        }
        int end = refreshHeight != 0 ? refreshHeight : headerHeight;
        startAnim(0, -end, new OnAnimListener() {
            @Override
            public void onStart() {
                refreshStatusChange(RefreshState.REFRESH_READY);
            }

            @Override
            public void onEnd() {
                refreshStatusChange(RefreshState.REFRESH_DOING);
            }
        });
    }

    private void measureView(View view) {
        if (view == null) {
            return;
        }
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
        if (view instanceof HeadListener && headerHeight == 0) {
            headerHeight = view.getMeasuredHeight();
        }
    }

    /**
     * 是否启用加载更多
     *
     * @param isCanLoad
     */
    public void setEnableLoadMore(boolean isCanLoad) {
        this.enableLoadMore = isCanLoad;
    }

    /**
     * 是否启用自动加载更多
     */
    public void setEnableAutoLoadMore(boolean enableAutoLoadMore) {
        this.enableAutoLoadMore = enableAutoLoadMore;
    }

    /**
     * 自动加载更多
     */
    public void autoLoadMore() {
        if (!enableAutoLoadMore) {
            return;
        }
        int end = footHeight + bottomScroll;
        startAnim(bottomScroll, end, new OnAnimListener() {
            @Override
            public void onStart() {
                refreshStatusChange(RefreshState.LOAD_MORE_READY);
            }

            @Override
            public void onEnd() {
                refreshStatusChange(RefreshState.LOAD_MORE_DOING);
            }
        });
    }

    /**
     * 结束刷新
     */
    public void finishRefresh(boolean isSuccess) {
        finishRefresh(isSuccess, 0);
    }

    /**
     * 结束刷新
     */
    public void finishRefresh(boolean isSuccess, long delay) {
        isRefreshSuccess = isSuccess;
        refreshStatusChange(RefreshState.REFRESH_COMPLETE);
        if (refreshDelayRunnable == null) {
            refreshDelayRunnable = new Runnable() {
                @Override
                public void run() {
                    scrollToDefaultStatus(RefreshState.REFRESH_COMPLETE);
                    isRefreshing = false;
                }
            };
        }
        postDelayed(refreshDelayRunnable, delay);
    }

    /**
     * 结束加载更多
     */
    public void finishLoadMore(boolean isSuccess) {
        finishLoadMore(isSuccess, 0);
    }

    /**
     * 结束加载更多
     */
    public void finishLoadMore(boolean isSuccess, long delay) {
        isLoadSuccess = isSuccess;
        refreshStatusChange(RefreshState.LOAD_MORE_COMPLETE);
        if (loadMoreDelayRunnable == null) {
            loadMoreDelayRunnable = new Runnable() {
                @Override
                public void run() {
                    scrollToDefaultStatus(RefreshState.LOAD_MORE_COMPLETE);
                    isLoading = Boolean.FALSE;
                }
            };
        }
        postDelayed(loadMoreDelayRunnable, delay);
    }

    /**
     * 设置头部监听(建议采用头布局实现{@link HeadListener}方式)
     */
    public void setOnHeaderListener(HeadListener mHeaderListener) {
        this.headListener = mHeaderListener;
    }

    /**
     * 设置加载监听(建议采用加载局实现{@link FootListener}方式)
     */
    public void setOnFooterListener(FootListener mFooterListener) {
        this.footListener = mFooterListener;
    }

    /**
     * 设置刷新加载组合监听
     */
    public EasyRefreshLayout setOnRefreshLoadMoreListener(OnRefreshLoadMoreListener onRefreshLoadMoreListener) {
        this.onRefreshLoadMoreListener = onRefreshLoadMoreListener;
        return this;
    }

    public EasyRefreshLayout setOnChildScrollUpCallback(@Nullable OnChildScrollUpCallback onChildScrollUpCallback) {
        this.onChildScrollUpCallback = onChildScrollUpCallback;
        return this;
    }

    public interface OnChildScrollUpCallback {
        /**
         * 是否允许子视图滚动
         */
        boolean canChildScrollUp(EasyRefreshLayout parent, @Nullable View child);
    }

    interface OnAnimListener {

        void onStart();

        void onEnd();
    }
}
