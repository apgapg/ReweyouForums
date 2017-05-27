package in.reweyou.reweyouforums;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import in.reweyou.reweyouforums.adapter.CommentsAdapter;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.fragment.CommentFragment;
import in.reweyou.reweyouforums.fragment.ThreadFragment;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = CommentActivity.class.getName();

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public EditText editText;
    private ImageView send;
    private TextView replyheader;
    private UserSessionManager userSessionManager;
    private String threadid;
    private String tempcommentid;
    private TextView nocommenttxt;
    private CommentsAdapter adapterComment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ViewPager viewpager;
    private TabLayout tablayout;
    private PagerAdapter pagerAdapter;
    private PagerAdapterSingle pagerAdapterSingle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        try {
            threadid = getIntent().getStringExtra("threadid");
            if (threadid == null)
                throw new NullPointerException("threadid cannot be null");
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewpager = (ViewPager) findViewById(R.id.viewPager);
        tablayout = (TabLayout) findViewById(R.id.tabLayout);


        if (getIntent().getStringExtra("from").equals("n")) {
            pagerAdapter = new PagerAdapter(getSupportFragmentManager());
            viewpager.setAdapter(pagerAdapter);
            tablayout.setupWithViewPager(viewpager);
        } else {
            tablayout.setVisibility(View.GONE);
            pagerAdapterSingle = new PagerAdapterSingle(getSupportFragmentManager());
            viewpager.setAdapter(pagerAdapterSingle);

        }



    }


    @Override
    public void onBackPressed() {
        Intent i;
        if (getIntent().getStringExtra("from").equals("g"))
            finish();
        else {
            i = new Intent(CommentActivity.this, ForumMainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
            finish();
        }
    }


    public void refreshlist() {
        // getData();
    }

    public void passClicktoEditText(String username, String commentid) {

        if (getIntent().getStringExtra("from").equals("n")) {
            ((CommentFragment) pagerAdapter.getRegisteredFragment(1)).passClicktoEditText(username, commentid);

        } else {
            ((CommentFragment) pagerAdapterSingle.getRegisteredFragment(0)).passClicktoEditText(username, commentid);
        }
    }

    public void showCommentPage() {
        viewpager.setCurrentItem(1);
    }


    private class PagerAdapterSingle extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        private String[] tabs = getResources().getStringArray(R.array.tabssinglecomment);

        private PagerAdapterSingle(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {

            Log.d(TAG, "getItem: fljewnfjknjwf");
            CommentFragment commentFragment = new CommentFragment();
            Bundle bundle = new Bundle();
            bundle.putString("threadid", threadid);
            commentFragment.setArguments(bundle);

            return commentFragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }


        @Override
        public int getCount() {
            return tabs.length;
        }


    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        private String[] tabs = getResources().getStringArray(R.array.tabsdoublecomment);

        private PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {


            if (position == 0) {
                ThreadFragment threadFragment = new ThreadFragment();
                Bundle bundle = new Bundle();
                bundle.putString("threadid", threadid);
                threadFragment.setArguments(bundle);
                return threadFragment;


            } else {
                CommentFragment commentFragment = new CommentFragment();
                Bundle bundle = new Bundle();
                bundle.putString("threadid", threadid);
                commentFragment.setArguments(bundle);
                return commentFragment;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }


        @Override
        public int getCount() {
            return tabs.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

    }

}
