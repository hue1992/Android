package com.laoschool.screen;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.astuetz.PagerSlidingTabStrip;
import com.laoschool.R;
import com.laoschool.LaoSchoolSingleton;
import com.laoschool.adapter.ListMessageAdapter;
import com.laoschool.adapter.ListNotificationAdapter;
import com.laoschool.entities.Message;
import com.laoschool.listener.OnLoadMoreListener;
import com.laoschool.model.AsyncCallback;
import com.laoschool.model.DataAccessInterface;
import com.laoschool.model.sqlite.DataAccessNotification;
import com.laoschool.shared.LaoSchoolShared;
import com.laoschool.view.FragmentLifecycle;
import com.laoschool.view.ViewpagerDisableSwipeLeft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScreenAnnouncements extends Fragment implements FragmentLifecycle {
    private static final String TAG = "ScreenAnnouncements";
    private static ScreenAnnouncements thiz;
    private static FragmentManager fr;
    private int containerId;
    private String currentRole = LaoSchoolShared.ROLE_STUDENT;
    private static Context context;

    private static DataAccessInterface service;
    private static DataAccessNotification accessNotification;
    private Message notification;
    boolean alreadyExecuted = false;

    //Item for student
    private RecyclerView mRecylerViewNotificationStudent;

    //Item for teacher
    static ViewpagerDisableSwipeLeft viewPage;
    static PagerSlidingTabStrip tabs;

    static NotificationPagerAdapter notificationPagerAdapter;

    static NotificationList currentPage;

    private static LinearLayout mAnnouncement;
    private static ProgressBar mProgressBar;

    public Message getNotification() {
        return notification;
    }

    public void setNotification(Message message) {
        this.notification = message;
    }

    public interface IScreenAnnouncements {

        void gotoScreenAnnouncementDetails(Message notificaiton);

        void _gotoCreateAnnouncement();

        void logoutApplication();

    }

    public IScreenAnnouncements iScreenAnnouncements;

    public ScreenAnnouncements() {
    }

    public static ScreenAnnouncements instantiate(int containerId, String currentRole) {
        Log.d(TAG, "instantiate()");
        ScreenAnnouncements fragment = new ScreenAnnouncements();
        Bundle args = new Bundle();
        args.putInt(LaoSchoolShared.CONTAINER_ID, containerId);
        args.putString(LaoSchoolShared.CURRENT_ROLE, currentRole);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            containerId = getArguments().getInt(LaoSchoolShared.CONTAINER_ID);
            currentRole = getArguments().getString(LaoSchoolShared.CURRENT_ROLE);
            Log.d(TAG, "-Container Id:" + containerId);
        }
        this.thiz = this;
        this.context = getActivity();
        this.service = LaoSchoolSingleton.getInstance().getDataAccessService();
        this.fr = getFragmentManager();


    }

    private void _defineData() {
        _showProgressLoading(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int countLocal = accessNotification.getNotificationCount();
                Log.d(TAG, "_defineData():count notification in Local=" + countLocal);
                if (countLocal > 0) {
                    getDataFormLocal();
                } else {
                    getDataFormServer();
                }
                _handlerPageChange();
                alreadyExecuted = true;
            }
        }, LaoSchoolShared.LOADING_TIME);

    }

    public static void getDataFormServer() {
        service.getNotification(new AsyncCallback<List<Message>>() {
            @Override
            public void onSuccess(final List<Message> result) {
                try {
                    for (Message message : result) {
                        accessNotification.addOrUpdateNotification(message);
                    }
                    getDataFormLocal(-1);
                } catch (Exception e) {
                    Log.e(TAG, "getDataFormServer()/getNotification() Exception=" + e.getMessage());
                }
            }

            @Override
            public void onFailure(String message) {
                Log.e(TAG, "getDataFormServer()/getNotification() onFailure=" + message);
            }

            @Override
            public void onAuthFail(String message) {
                LaoSchoolShared.goBackToLoginPage(context);
            }
        });
    }

    private static void _showProgressLoading(boolean b) {
        if (b) {
            mProgressBar.setVisibility(View.VISIBLE);
            mAnnouncement.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mAnnouncement.setVisibility(View.VISIBLE);
        }
    }


    public static void getDataFormServer(final int form_id, final int position) {
        service.getNotification(form_id, new AsyncCallback<List<Message>>() {
            @Override
            public void onSuccess(final List<Message> result) {
                try {
                    for (Message message : result) {
                        accessNotification.addOrUpdateNotification(message);
                    }
                    getDataFormLocal(position);
                } catch (Exception e) {
                    Log.e(TAG, "getDataFormServer()/getNotification(" + form_id + ") Exception=" + e.getMessage());
                }
            }

            @Override
            public void onFailure(String message) {
                Log.e(TAG, "getDataFormServer()/getNotification(" + form_id + ") onFailure=" + message);
            }

            @Override
            public void onAuthFail(String message) {
                LaoSchoolShared.goBackToLoginPage(context);
            }
        });
    }

    private static void getDataFormLocal() {
        getDataFormLocal(-1);
    }

    private static void getDataFormLocal(int position) {
        try {
            _initPageData();
            if (position > -1) {
                viewPage.setCurrentItem(position);
            }
            _showProgressLoading(false);
        } catch (Exception e) {
            Log.e(TAG, "getDataFormLocal() Exception = " + e.getMessage());
        }
    }

    private static void _initPageData() {
        List<Message> notificationForUserInbox = accessNotification.getListNotificationForUser(Message.MessageColumns.COLUMN_NAME_TO_USR_ID, LaoSchoolShared.myProfile.getId(), 30, 0, 1);
        List<Message> notificationForUserUnread = accessNotification.getListNotificationForUser(Message.MessageColumns.COLUMN_NAME_TO_USR_ID, LaoSchoolShared.myProfile.getId(), 30, 0, 0);
        notificationPagerAdapter = new NotificationPagerAdapter(fr, notificationForUserInbox, notificationForUserUnread);
        viewPage.setAdapter(notificationPagerAdapter);
        tabs.setViewPager(viewPage);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        if (currentRole == null)
            return inflater.inflate(R.layout.screen_error_application, container, false);
        else {
            // Inflate the layout for this fragment
            //  View view = inflater.inflate(R.layout.screen_announcements_student, container, false);
//            if (currentRole.equals(LaoSchoolShared.ROLE_STUDENT))
//                return _defineStudentView(view);
//            else {

            return _defineTeacherView(inflater, container);
            //}
        }

    }

    private View _defineTeacherView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.screen_announcements_teacher, container, false);
        mAnnouncement = (LinearLayout) view.findViewById(R.id.mAnnouncement);
        mProgressBar = (ProgressBar) view.findViewById(R.id.mProgress);
        // Bind the tabs to the ViewPager
        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        viewPage = (ViewpagerDisableSwipeLeft) view.findViewById(R.id.notificationViewPage);
        viewPage.setAllowedSwipeDirection(HomeActivity.SwipeDirection.none);
        return view;
    }

    private void _handlerPageChange() {
        ViewPager.OnPageChangeListener onPageNotificationChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "_handlerPageChange() onPageSelected() = " + position);
                List<Message> notificationForUser = accessNotification.getListNotificationForUser(Message.MessageColumns.COLUMN_NAME_TO_USR_ID, LaoSchoolShared.myProfile.getId(), 30, 0, (position == 0) ? 1 : 0);
                NotificationList notifragment = ((NotificationPagerAdapter) (viewPage.getAdapter())).getFragment(position);
                notifragment._setListNotification(notificationForUser, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        viewPage.addOnPageChangeListener(onPageNotificationChangeListener);
    }

    private View _defineStudentView(View view) {
        mRecylerViewNotificationStudent = (RecyclerView) view.findViewById(R.id.mRecylerViewNotificationStudent);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mRecylerViewNotificationStudent.setLayoutManager(linearLayoutManager);
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        //
        int count = accessNotification.getNotificationCount();
        Log.d(TAG, "notification count in local:" + count);
        if (count > 0) {
            getNotificationFormClientForStudent();
        } else {
            getNotificationFormServerForStudent();
        }


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotificationFormServerForStudent();
                // Refresh items
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


        return view;
    }

    private void getNotificationFormClientForStudent() {
        List<Message> messages = accessNotification.getListNotificationForUser(Message.MessageColumns.COLUMN_NAME_TO_USER_NAME, LaoSchoolShared.myProfile.getId(), 30, 0, 1);
        _setListNotificationForStudent(messages);

    }

    private void getNotificationFormServerForStudent() {
        service.getNotification(new AsyncCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> result) {
                try {
                    for (Message message : result) {
                        accessNotification.addOrUpdateNotification(message);
                    }
                    List<Message> messages = accessNotification.getListNotificationForUser(Message.MessageColumns.COLUMN_NAME_TO_USER_NAME, LaoSchoolShared.myProfile.getId(), 30, 0, 1);
                    _setListNotificationForStudent(messages);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String message) {

            }

            @Override
            public void onAuthFail(String message) {
                LaoSchoolShared.goBackToLoginPage(context);
            }
        });
    }

    private void _setListNotificationForStudent(List<Message> messages) {
        final ListNotificationAdapter listMessageAdapter = new ListNotificationAdapter(this, mRecylerViewNotificationStudent, 0, messages);
        mRecylerViewNotificationStudent.setAdapter(listMessageAdapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (currentRole != null)
            if (currentRole.equals(LaoSchoolShared.ROLE_TEARCHER))
                inflater.inflate(R.menu.menu_screen_announcement, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (currentRole != null) {
            if (currentRole.equals(LaoSchoolShared.ROLE_TEARCHER)) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.action_create_announcement:
                        iScreenAnnouncements._gotoCreateAnnouncement();
                        return true;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void onPauseFragment() {

    }

    @Override
    public void onResumeFragment() {
        Log.d(TAG, "onResumeFragment()");

    }

    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach()");
        super.onAttach(context);
        iScreenAnnouncements = (IScreenAnnouncements) context;
    }

    public static class NotificationPagerAdapter extends FragmentPagerAdapter {
        private FragmentManager mFragmentManager;
        private Map<Integer, String> mFragmentTags;
        private List<Message> notificationForUserInbox;
        private List<Message> notificationForUserUnread;


        public NotificationPagerAdapter(FragmentManager fm, List<Message> notificationForUserInbox, List<Message> notificationForUserUnread) {
            super(fm);
            mFragmentManager = fm;
            mFragmentTags = new HashMap<Integer, String>();
            this.notificationForUserInbox = notificationForUserInbox;
            this.notificationForUserUnread = notificationForUserUnread;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return "Inbox";
            else
                return "Unread";

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                currentPage = new NotificationList(0, notificationForUserInbox);
                return currentPage;
            } else {
                currentPage = new NotificationList(1, notificationForUserUnread);
                return currentPage;
            }

        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object obj = super.instantiateItem(container, position);
            if (obj instanceof Fragment) {
                // record the fragment tag here.
                Fragment f = (Fragment) obj;
                String tag = f.getTag();
                mFragmentTags.put(position, tag);
            }
            return obj;
        }

        public NotificationList getFragment(int position) {
            String tag = mFragmentTags.get(position);
            if (tag == null)
                return null;
            return (NotificationList) mFragmentManager.findFragmentByTag(tag);
        }
    }

    @SuppressLint("ValidFragment")
    public static class NotificationList extends Fragment {
        private int position;
        private RecyclerView mRecyclerListMessage;
        private Context context;
        SwipeRefreshLayout mSwipeRefreshLayout;
        private List<Message> notificationForUser;

        public NotificationList() {

        }

        public NotificationList(int position, List<Message> notificationForUser) {
            this.position = position;
            this.notificationForUser = notificationForUser;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.context = getActivity();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.view_message_list, container, false);
            mRecyclerListMessage = (RecyclerView) view.findViewById(R.id.mRecyclerListMessage);
            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            //set adapter
            mRecyclerListMessage.setLayoutManager(linearLayoutManager);
            //
            _setListNotification(notificationForUser, position);

            _handlerSwipeReload();
            return view;
        }

        private void _handlerSwipeReload() {
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //get max from id
                    if (mRecyclerListMessage.getAdapter() != null) {
                        try {
                            ListNotificationAdapter listMessageAdapter = (ListNotificationAdapter) mRecyclerListMessage.getAdapter();
                            int form_id = listMessageAdapter.getNotificationList().get(0).getId();
                            getDataFormServer(form_id, position);
                        } catch (Exception e) {
                            getDataFormServer();
                        }
                    } else {
                        getDataFormServer();
                    }
                    // Refresh items
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }

        private void _setListNotification(final List<Message> messages, final int position) {
            try {
                final ListNotificationAdapter listMessageAdapter = new ListNotificationAdapter(thiz, mRecyclerListMessage, position, messages);
                listMessageAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        int countMessageFormLocal = accessNotification.getNotificationCount((position == 0) ? 1 : 0);
                        if (messages.size() < countMessageFormLocal) {
                            Log.d(TAG, "Load more notification !!!");
                            messages.add(null);
                            listMessageAdapter.notifyItemInserted(messages.size() - 1);
                            _loadMoreData(messages, listMessageAdapter, position);
                        } else {
                            Log.d(TAG, "No notification load !!!");
                        }

                    }
                });
                mRecyclerListMessage.setAdapter(listMessageAdapter);

            } catch (Exception e) {
                Log.e(TAG, "NotificationList:_setListNotification():" + e.getMessage());
            }

        }

        private void _loadMoreData(final List<Message> messages, final ListNotificationAdapter notificationAdapter, final int position) {
            //Load more data for reyclerview
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "/_loadMoreData():Load More 2");

                    //Remove loading item
                    messages.remove(messages.size() - 1);
                    notificationAdapter.notifyItemRemoved(messages.size());

                    //Load data

                    List<Message> messagesForUser = new ArrayList<>();
                    if (LaoSchoolShared.myProfile != null) {
                        messagesForUser = accessNotification.getListNotificationForUser(Message.MessageColumns.COLUMN_NAME_TO_USR_ID, LaoSchoolShared.myProfile.getId(), messages.size() + 30, messages.size(), (position == 0 ? 1 : 0));
                        Log.d(TAG, "NotificationList:getListNotificationForUser size=" + messages.size());
                    }
                    messages.addAll(messagesForUser);


                    notificationAdapter.notifyDataSetChanged();
                    notificationAdapter.setLoaded();
                }
            }, 2000);
        }

        @Override
        public void onResume() {
            super.onResume();
            if (LaoSchoolShared.myProfile == null) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.logoutApplication();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d(TAG, "setUserVisibleHint(" + isVisibleToUser + ")");
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (!alreadyExecuted)
                _defineData();
        }
    }
}
