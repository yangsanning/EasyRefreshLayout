package ysn.com.view.refresh_layout.listener;

/**
 * @Author yangsanning
 * @ClassName FootListener
 * @Description 脚布局监听
 * @Date 2020/2/12
 * @History 2020/2/12 author: description:
 */
public interface FootListener {

    void onLoadBefore(int scrollY);

    void onLoadAfter(int scrollY);

    void onLoadReady(int scrollY);

    void onLoading(int scrollY);

    void onLoadComplete(int scrollY, boolean isLoadSuccess);

    void onLoadCancel(int scrollY);
}
