package ysn.com.demo.jackrefreshlayout.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import ysn.com.demo.jackrefreshlayout.R;
import ysn.com.view.refresh_layout.listener.FootListener;

/**
 * @Author yangsanning
 * @ClassName FootView
 * @Description 脚布局
 * @Date 2020/2/12
 * @History 2020/2/12 author: description:
 */
public class FootView extends FrameLayout implements IView, FootListener {

    private TextView textView;

    public FootView(@NonNull Context context) {
        this(context, null);
    }

    public FootView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FootView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FootView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    public void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_foot, this);

        textView = findViewById(R.id.foot_view_text);
    }

    @Override
    public void onLoadBefore(int scrollY) {
        textView.setText("上拉加载");
    }

    @Override
    public void onLoadAfter(int scrollY) {
        textView.setText("释放加载");
    }

    @Override
    public void onLoadReady(int scrollY) {
        textView.setText("准备加载");
    }

    @Override
    public void onLoading(int scrollY) {
        textView.setText("加载中...");
    }

    @Override
    public void onLoadComplete(int scrollY, boolean isLoadSuccess) {
        textView.setText(isLoadSuccess ? "加载成功" : "加载失败");
    }

    @Override
    public void onLoadCancel(int scrollY) {
        textView.setText("取消加载更多");
    }
}
