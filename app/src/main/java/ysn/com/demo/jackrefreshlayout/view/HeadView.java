package ysn.com.demo.jackrefreshlayout.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ysn.com.demo.jackrefreshlayout.R;
import ysn.com.view.jackrefreshlayout.listener.HeadListener;

/**
 * @Author yangsanning
 * @ClassName HeaderView
 * @Description 头布局
 * @Date 2020/2/12
 * @History 2020/2/12 author: description:
 */
public class HeadView extends FrameLayout implements IView, HeadListener {

    private ImageView imageView;
    private TextView textView;

    public HeadView(@NonNull Context context) {
        this(context, null);
    }

    public HeadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HeadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    public void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_head, this);

        imageView = findViewById(R.id.head_view_arrow);
        textView = findViewById(R.id.head_view_text);
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onRefreshBefore(int scrollY, int headerHeight) {
        imageView.setImageResource(R.drawable.ic_arrow_down);
        textView.setText("下拉刷新");
    }

    /**
     * 释放刷新
     */
    @Override
    public void onRefreshAfter(int scrollY, int headerHeight) {
        imageView.setImageResource(R.drawable.ic_arrow_up);
        textView.setText("释放刷新");
    }

    /**
     * 准备刷新
     */
    @Override
    public void onRefreshReady(int scrollY, int headerHeight) {
        textView.setText("准备刷新");
    }

    /**
     * 正在刷新
     */
    @Override
    public void onRefreshing(int scrollY, int headerHeight) {
        textView.setText("正在刷新");
    }

    /**
     * 刷新完成
     */
    @Override
    public void onRefreshComplete(int scrollY, int headerHeight, boolean isRefreshSuccess) {
        imageView.setImageResource(R.drawable.ic_arrow_up);
        textView.setText(isRefreshSuccess ? "刷新成功" : "刷新失败");
    }

    /**
     * 取消刷新
     */
    @Override
    public void onRefreshCancel(int scrollY, int headerHeight) {
        imageView.setImageResource(R.drawable.ic_arrow_up);
        textView.setText("取消刷新");
    }

    @Override
    public int getRefreshHeight() {
        return 0;
    }
}
