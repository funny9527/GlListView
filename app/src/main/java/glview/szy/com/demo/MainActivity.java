package glview.szy.com.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

    private GlListView mListView;
    private ListView mLocalView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGlUi();
//        initLocalUi();
    }

    private void initGlUi() {
        mListView = (GlListView) findViewById(R.id.listview);
    }

    private void initLocalUi() {
//        mLocalView = (ListView) findViewById(R.id.locallist);
//        BaseAdapter adapter = new CustomAdapter(this);
//        mLocalView.setAdapter(adapter);
    }

    private class CustomAdapter extends BaseAdapter {

        private Context mContext;

        public CustomAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.item,
                        viewGroup, false);
            }
            return view;
        }
    }
}
