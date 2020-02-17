package ysn.com.demo.jackrefreshlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import ysn.com.demo.jackrefreshlayout.view.FootView;
import ysn.com.demo.jackrefreshlayout.view.HeadView;
import ysn.com.view.jackrefreshlayout.JackRefreshLayout;
import ysn.com.view.jackrefreshlayout.listener.OnRefreshLoadMoreListener;

public class MainActivity extends AppCompatActivity {

    private DataAdapter adapter;
    private List<String> dataList = new ArrayList<>();

    private JackRefreshLayout jackRefreshLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jackRefreshLayout = findViewById(R.id.main_activity_jack_refresh_layout);
        jackRefreshLayout.addHeadView(new HeadView(this))
            .addFootView(new FootView(this))
            .setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
                @Override
                public void onRefresh() {
                    jackRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            jackRefreshLayout.finishRefresh(Boolean.TRUE);
                            adapter.setNewData(getNewData());
                        }
                    }, 2000);
                }

                @Override
                public void onLoadMore() {
                    jackRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            jackRefreshLayout.finishLoadMore(Boolean.TRUE);
                            addData();
                            adapter.notifyDataSetChanged();
                        }
                    }, 2000);
                }
            });

        recyclerView = findViewById(R.id.main_activity_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DataAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setNewData(getNewData());
    }

    private List<String> getNewData() {
        dataList.clear();
        for (int i = 0; i < 20; i++) {
            dataList.add("Item" + i);
        }
        return dataList;
    }

    private List<String> addData() {
        int size = dataList.size();
        for (int i = size; i < size + 20; i++) {
            dataList.add("Item" + i);
        }
        return dataList;
    }

    private class DataAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public DataAdapter() {
            super(R.layout.item_data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.date_item_text, item);
        }
    }
}
