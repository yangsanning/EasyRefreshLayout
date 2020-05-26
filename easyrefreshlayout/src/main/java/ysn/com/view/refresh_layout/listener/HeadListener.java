package ysn.com.view.refresh_layout.listener;

/**
 * @Author yangsanning
 * @ClassName HeadListener
 * @Description 头布局监听
 * @Date 2020/2/12
 * @History 2020/2/12 author: description:
 */
public interface HeadListener {

    /**
     * 下拉刷新
     */
    void onRefreshBefore(int scrollY, int headerHeight);

    /**
     * 松开刷新
     */
    void onRefreshAfter(int scrollY, int headerHeight);

    /**
     * 准备刷新
     */
    void onRefreshReady(int scrollY, int headerHeight);

    /**
     * 正在刷新
     */
    void onRefreshing(int scrollY, int headerHeight);

    /**
     * 刷新成功
     */
    void onRefreshComplete(int scrollY, int headerHeight, boolean isRefreshSuccess);

    /**
     * 取消刷新
     */
    void onRefreshCancel(int scrollY, int headerHeight);

    int getRefreshHeight();
}
