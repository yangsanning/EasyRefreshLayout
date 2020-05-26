package ysn.com.view.refresh_layout.constant;

/**
 * @Author yangsanning
 * @ClassName RefreshState
 * @Description 刷新状态
 * @Date 2020/2/12
 * @History 2020/2/12 author: description:
 */
public enum RefreshState {

    /**
     * 正常情况
     */
    NONE,

    /**
     * 下拉刷新
     */
    REFRESH_BEFORE,

    /**
     * 释放刷新
     */
    REFRESH_AFTER,

    /**
     * 准备刷新
     */
    REFRESH_READY,

    /**
     * 正在刷新
     */
    REFRESH_DOING,

    /**
     * 刷新完成
     */
    REFRESH_COMPLETE,

    /**
     * 取消刷新
     */
    REFRESH_CANCEL,

    /**
     * 加载更多前
     */
    LOAD_BEFORE,

    /**
     * 准备加载更多
     */
    LOAD_MORE_READY,

    /**
     * 加载更多中
     */
    LOAD_MORE_DOING,

    /**
     * 加载更多后
     */
    LOAD_AFTER,

    /**
     * 加载更多完成
     */
    LOAD_MORE_COMPLETE,

    /**
     * 取消加载更多
     */
    LOAD_MORE_CANCEL,
    ;
}
