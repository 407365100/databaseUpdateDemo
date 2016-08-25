package com.luomo.databaseupdate;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.luomo.databaseupdate.database.utils.SQLiteOpenHelperWrap;
import com.luomo.databaseupdate.database.domain.UserDomain;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private ListView mLvUser;
    private TextView mTvVersion;
    /**
     * 获取的数据库数据
     */
    private List<UserDomain> mUserList;
    private UserAdapter mUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.layout_main);
        initViews();
    }

    private void initViews() {
        mLvUser = (ListView) findViewById(R.id.lv_content);
        mTvVersion = (TextView) findViewById(R.id.tv_version);
        initData();
    }

    private void initData() {
//        mUserList = SQLiteOpenHelperWrap.getLowUserList(mContext);
        mUserList = SQLiteOpenHelperWrap.getHighUserList(mContext);
        mLvUser.setAdapter(mUserAdapter = new UserAdapter(mContext, mUserList));
        mTvVersion.setText("版本号："+ SQLiteOpenHelperWrap.getVersion(mContext));
    }

    class UserAdapter extends BaseAdapter {
        private Context mContext;
        private List<UserDomain> userList;

        public UserAdapter(Context context, List<UserDomain> list) {
            this.mContext = context;
            this.userList = list;
        }

        @Override
        public int getCount() {
            return userList.size();
        }

        @Override
        public Object getItem(int position) {
            return userList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.layout_user_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.tvId = (TextView) convertView.findViewById(R.id.tv_id);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                //高版本添加的字段
                viewHolder.tvPhone = (TextView) convertView.findViewById(R.id.tv_phone);
                viewHolder.llSplit = (LinearLayout) convertView.findViewById(R.id.ll_split);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvId.setText(userList.get(position).id);
            viewHolder.tvName.setText(userList.get(position).name);
            //高版本加载的字段
            viewHolder.tvPhone.setText(userList.get(position).phone);

            //最后一个则隐藏掉分割线
            if (userList.size() == userList.indexOf(userList.get(position)) + 1) {
                viewHolder.llSplit.setVisibility(View.GONE);
            } else {
                viewHolder.llSplit.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    class ViewHolder {
        private TextView tvId;
        private TextView tvName;

        /**
         * 高版本添加的字段
         */
        private TextView tvPhone;
        /**
         * 分割线
         */
        private LinearLayout llSplit;
    }
}
