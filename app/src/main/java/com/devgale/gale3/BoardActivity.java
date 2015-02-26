package com.devgale.gale3;
import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
import android.os.Build.*;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import org.xmlpull.v1.*;

public class BoardActivity extends ActionBarActivity {

    private String tagName;
    private String result;
    private ProgressDialog progDialog;

    private android.support.v7.widget.Toolbar toolbar;
    private DrawerLayout dlDrawer;
    private ActionBarDrawerToggle dtToggle;

    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;

    private SwipeRefreshLayout SRL;

    String uri = "http://storepocket.dothome.co.kr/list/list_map.xml";

    URL url;
    String tagname = "", title="", desc="", time="";
    Boolean flag = null;
    //private String str_url;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ///mListView.setAdapter(mAdapter);
        ///mListView.setOnItemClickListener(GoToWebPage);

        mAdapter = new ListViewAdapter(this);

        if (VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        ListView mListView = (ListView)findViewById(R.id.list);

        if (!isNetworkConnected(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("네트워크 연결")
                    .setMessage("\n네트워크 연결 상태 확인 후 다시 시도해 주십시요\n")
                    .setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    finish();
                                }
                            }).show();
        } else {
            SRL = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            SRL.setColorSchemeColors(Color.rgb(231, 76, 60),
                    Color.rgb(46, 204, 113), Color.rgb(41, 128, 185),
                    Color.rgb(241, 196, 15));
            SRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    networkTask();
                }
            });
            networkTask();
            mListView = (ListView)findViewById(R.id.list);
            mAdapter = new ListViewAdapter(BoardActivity.this);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                    ListData mData = mAdapter.mListData.get(position);
                    //mData.mTitle 이런식으로 ㅇ.ㅇ
                }
            });
        }

    }


    private void networkTask() {
        final Handler mHandler = new Handler();
        new Thread() {

            public void run() {

                mHandler.post(new Runnable() {

                    public void run() {
                        SRL.setRefreshing(true);
                        try
                        {
                            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                            factory.setNamespaceAware(true);
                            XmlPullParser xpp = factory.newPullParser();

                            url = new URL(uri);
                            InputStream in = url.openStream();

                            xpp.setInput(in, "utf-8");

                            int eventType = xpp.getEventType();

                            while(eventType != XmlPullParser.END_DOCUMENT )
                            {
                                if(eventType == XmlPullParser.START_TAG)
                                {

                                    tagname = xpp.getName();

                                }
                                if(eventType == XmlPullParser.TEXT)
                                {

                                    if(tagname.equals("title"))

                                        title += xpp.getText();

                                    if (tagname.equals("description"))

                                        desc += xpp.getText();

                                    if(tagname.equals("pubDate"))

                                        time +=  xpp.getText();

                                }
                                if (eventType == XmlPullParser.END_TAG)
                                {

                                    tagname = xpp.getName();

                                    if(tagname.equals("link"))
                                    {
                                        mAdapter.addItem(title.replace("\n", ""), desc.replace("\n", ""));

                                        title="";
                                        desc="";
                                        time="";
                                    }
                                }

                                eventType = xpp.next();
                            }

                            flag = true;

                        } catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });

                mHandler.post(new Runnable() {
                    public void run() {
                        mListView = (ListView)findViewById(R.id.list);
                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                                ListData mData = mAdapter.mListData.get(position);
                            }
                        });
                        handler.sendEmptyMessage(0);
                        SRL.setRefreshing(false);
                    }
                });

            }
        }.start();

    }
    private AdapterView.OnItemClickListener GoToWebPage = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View clickedView,
                                int pos, long id) {

            //ListData mData = mAdapter.mListData.get(pos);
            //Intent intent = new Intent(MapActivity.this, ContentActivity.class);
            //intent.putExtra("title", mData.mTitle);
            //intent.putExtra("url", mData.mUrl);

        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        //dtToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //dtToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if (dtToggle.onOptionsItemSelected(item)) {
        //return true;
        //

        return super.onOptionsItemSelected(item);
    }
    private class ViewHolder {
        public TextView mText;
        public TextView mUrl;
    }




    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<ListData> mListData = new ArrayList<ListData>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addItem(String mTitle, String mUrl) {
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.mTitle = mTitle;
            addInfo.mUrl = mUrl;

            mListData.add(addInfo);
        }

        public void remove(int position) {
            mListData.remove(position);
            dataChange();
        }

        public void sort() {
            Collections.sort(mListData, ListData.ALPHA_COMPARATOR);
            dataChange();
        }

        public void dataChange() {
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_list, null);

                holder.mText = (TextView) convertView.findViewById(R.id.title);
                holder.mUrl = (TextView) convertView.findViewById(R.id.url);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ListData mData = mListData.get(position);

            holder.mText.setText(mData.mTitle);
            holder.mUrl.setText(mData.mUrl);

            return convertView;
        }
    }

    public boolean isNetworkConnected(Context context) {
        boolean isConnected = false;

        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mobile.isConnected() || wifi.isConnected()) {
            isConnected = true;
        } else {
            isConnected = false;
        }
        return isConnected;
    }
}
