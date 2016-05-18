package com.laoschool.screen;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.laoschool.LaoSchoolSingleton;
import com.laoschool.R;
import com.laoschool.adapter.SessionAdapter;
import com.laoschool.entities.TimeTable;
import com.laoschool.model.AsyncCallback;
import com.laoschool.shared.LaoSchoolShared;
import com.laoschool.view.FragmentLifecycle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScreenSchedule extends Fragment implements FragmentLifecycle {


    private static final String TAG = "ScreenSchedule";
    private static String CURRENT_ROLE = "current_role";
    private int containerId;
    private String currentRole;
    private Context context;

    private static String mime = "text/html";
    private static String encoding = "utf-8";
    private static String ASSETS = "file:///android_asset/";
    private SwipeRefreshLayout mSwipeRefeshScheduleStudent;

    //
    private ScrollView mScrollTimeTableStudent;
    private TextView lbSchoolNameStudent;
    private TextView lbGvcnStudent;
    private TextView lbTermStudent;
    private boolean alreadyExecuted = false;
    private FrameLayout mErrorStudent;
    private FrameLayout mNoDataStudent;
    private ProgressBar mProgressStudent;
    private ViewPager mPageTimeTableStudent;
    private PagerSlidingTabStrip tabStripStudent;

    String testDataStudentSchedule = "<table class=\"MsoTableGrid\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" width=\"400\" style=\"width: 50pt; border-collapse: collapse; border: none;\">\n" +
            "    <tbody>\n" +
            "        <tr>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border: 1pt solid windowtext; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\"><strong>THỨ\n" +
            "            HAI</strong></p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: solid solid solid none; border-top-color: windowtext; border-right-color: windowtext; border-bottom-color: windowtext; border-top-width: 1pt; border-right-width: 1pt; border-bottom-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\"><strong>THỨ\n" +
            "            BA</strong></p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: solid solid solid none; border-top-color: windowtext; border-right-color: windowtext; border-bottom-color: windowtext; border-top-width: 1pt; border-right-width: 1pt; border-bottom-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\"><strong>THỨ\n" +
            "            TƯ</strong></p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: solid solid solid none; border-top-color: windowtext; border-right-color: windowtext; border-bottom-color: windowtext; border-top-width: 1pt; border-right-width: 1pt; border-bottom-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\"><strong>THỨ\n" +
            "            NĂM</strong></p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 101.2pt; border-style: solid solid solid none; border-top-color: windowtext; border-right-color: windowtext; border-bottom-color: windowtext; border-top-width: 1pt; border-right-width: 1pt; border-bottom-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\"><strong>THỨ\n" +
            "            SÁU</strong></p>\n" +
            "            </td>\n" +
            "        </tr>\n" +
            "        <tr>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid; border-right-color: windowtext; border-bottom-color: windowtext; border-left-color: windowtext; border-right-width: 1pt; border-bottom-width: 1pt; border-left-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">SHDC</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">THỂ DỤC</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">TOÁN</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">THỂ DỤC</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 101.2pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">TOÁN </p>\n" +
            "            </td>\n" +
            "        </tr>\n" +
            "        <tr>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid; border-right-color: windowtext; border-bottom-color: windowtext; border-left-color: windowtext; border-right-width: 1pt; border-bottom-width: 1pt; border-left-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">TOÁN</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">TOÁN</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">KĨ THUẬT</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">TOÁN</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 101.2pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: justify; line-height: 150%;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; LỊCH SỬ</p>\n" +
            "            </td>\n" +
            "        </tr>\n" +
            "        <tr>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid; border-right-color: windowtext; border-bottom-color: windowtext; border-left-color: windowtext; border-right-width: 1pt; border-bottom-width: 1pt; border-left-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">ĐẠO ĐỨC</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">CHÍNH TẢ</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">TẬP ĐỌC</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">KHOA HỌC</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 101.2pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">TL VĂN</p>\n" +
            "            </td>\n" +
            "        </tr>\n" +
            "        <tr>\n" +
            "            <td colspan=\"5\" valign=\"top\" style=\"width: 455.4pt; border-style: none solid solid; border-right-color: windowtext; border-bottom-color: windowtext; border-left-color: windowtext; border-right-width: 1pt; border-bottom-width: 1pt; border-left-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\"><em>RA\n" +
            "            CHƠI</em></p>\n" +
            "            </td>\n" +
            "        </tr>\n" +
            "        <tr style=\"height: 24.25pt;\">\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid; border-right-color: windowtext; border-bottom-color: windowtext; border-left-color: windowtext; border-right-width: 1pt; border-bottom-width: 1pt; border-left-width: 1pt; padding: 0in 5.4pt; height: 24.25pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">TẬP ĐỌC</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt; height: 24.25pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"line-height: 150%;\">&nbsp;&nbsp;&nbsp;&nbsp; LT &amp; CÂU</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt; height: 24.25pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">ĐỊA LÝ</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt; height: 24.25pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">LT &amp; CÂU</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 101.2pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt; height: 24.25pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">MĨ THUẬT</p>\n" +
            "            </td>\n" +
            "        </tr>\n" +
            "        <tr>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid; border-right-color: windowtext; border-bottom-color: windowtext; border-left-color: windowtext; border-right-width: 1pt; border-bottom-width: 1pt; border-left-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">&nbsp;ÂM NHẠC</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">KHOA HỌC</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">TL VĂN</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 88.55pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">KỂ CHUYỆN</p>\n" +
            "            </td>\n" +
            "            <td valign=\"top\" style=\"width: 101.2pt; border-style: none solid solid none; border-bottom-color: windowtext; border-bottom-width: 1pt; border-right-color: windowtext; border-right-width: 1pt; padding: 0in 5.4pt;\">\n" +
            "            <p class=\"MsoNormal\" style=\"text-align: center; line-height: 150%;\">SH LỚP</p>\n" +
            "            </td>\n" +
            "        </tr>\n" +
            "    </tbody>\n" +
            "</table>";
    private FragmentManager fr;


    public ScreenSchedule() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return _defineScreenSchedulebyRole(inflater, container);
    }

    private View _defineSrceenScheduleTeacher(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.screen_schedule_tearcher, container, false);
        //
        TextView txtSchoolName = (TextView) view.findViewById(R.id.txtSchoolName);
        TextView txtTerm = (TextView) view.findViewById(R.id.txtClassScreenExamResults);
        WebView mWebViewDetailsScheduleview = (WebView) view.findViewById(R.id.mWebViewDetailsSchedule);
        mWebViewDetailsScheduleview.loadDataWithBaseURL(ASSETS, testDataStudentSchedule, mime, encoding, null);
        return view;
    }

    private View _defineSrceenScheduleStudent(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.screen_schedule_student, container, false);
        //
        lbSchoolNameStudent = (TextView) view.findViewById(R.id.txtSchoolName);
        lbGvcnStudent = (TextView) view.findViewById(R.id.txtGvcn);
        lbTermStudent = (TextView) view.findViewById(R.id.txtClassScreenExamResults);
        mSwipeRefeshScheduleStudent = (SwipeRefreshLayout) view.findViewById(R.id.mRefeshScheduleStudent);
        mScrollTimeTableStudent = (ScrollView) view.findViewById(R.id.mScrollTimeTableStudent);
        mProgressStudent = (ProgressBar) view.findViewById(R.id.mProgress);
        mErrorStudent = (FrameLayout) view.findViewById(R.id.mError);
        mNoDataStudent = (FrameLayout) view.findViewById(R.id.mNoData);
        mPageTimeTableStudent = (ViewPager) view.findViewById(R.id.mPageTimeTable);
        tabStripStudent = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);


        _handerSwipeRefresh();
        _handlerErrorRefresh();
        _handlerNodataRefresh();
        return view;
    }

    private void _handerSwipeRefresh() {
        mSwipeRefeshScheduleStudent.setEnabled(false);
        mSwipeRefeshScheduleStudent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefeshScheduleStudent.setRefreshing(false);
//                getInformationStudent();
//                getTimeTable();
            }
        });
        mScrollTimeTableStudent.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {

                int scrollX = mScrollTimeTableStudent.getScrollX(); //for horizontalScrollView
                int scrollY = mScrollTimeTableStudent.getScrollY(); //for verticalScrollView
                //Log.d(TAG, "_handerSwipeRefresh().onScrollChanged() scrollX:" + scrollX + ",scrollY:" + scrollY);
                //DO SOMETHING WITH THE SCROLL COORDINATES
//                if (scrollY == 0) {
//                    mSwipeRefeshScheduleStudent.setEnabled(true);
//                } else {
//                    mSwipeRefeshScheduleStudent.setEnabled(false);
//                }


            }
        });
    }

    private void _handlerErrorRefresh() {
        mErrorStudent.findViewById(R.id.mReloadData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInformationStudent();
                getTimeTable();
            }
        });
    }

    private void _handlerNodataRefresh() {
        mNoDataStudent.findViewById(R.id.mReloadData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInformationStudent();
                getTimeTable();
            }
        });
    }

    private View _defineScreenSchedulebyRole(LayoutInflater inflater, ViewGroup container) {
        if (currentRole.equals(LaoSchoolShared.ROLE_TEARCHER)) {
            return _defineSrceenScheduleTeacher(inflater, container);
        } else {
            return _defineSrceenScheduleStudent(inflater, container);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.context = getActivity();
        if (getArguments() != null) {
            containerId = getArguments().getInt(LaoSchoolShared.CONTAINER_ID);
            currentRole = getArguments().getString(CURRENT_ROLE);
            Log.d(TAG, "-Container Id:" + containerId);
            Log.d(TAG, "-Role:" + currentRole);
        }
        this.fr = getActivity().getSupportFragmentManager();
    }

    @Override
    public void onPauseFragment() {

    }

    @Override
    public void onResumeFragment() {
        if (!alreadyExecuted && getUserVisibleHint()) {
            if (currentRole == null) {
                Log.d(TAG, "onResumeFragment() - current role null");
                currentRole = LaoSchoolShared.ROLE_STUDENT;
            }
            if (currentRole.equals(LaoSchoolShared.ROLE_TEARCHER)) {
            } else {
                getInformationStudent();
                getTimeTable();
            }

        }
    }

    private void getInformationStudent() {
        try {
            if (LaoSchoolShared.myProfile != null) {
                //set infomation
                lbSchoolNameStudent.setText(LaoSchoolShared.myProfile.getSchoolName());
                lbGvcnStudent.setText(LaoSchoolShared.myProfile.getEclass().getHeadTeacherName());
                lbTermStudent.setText(LaoSchoolShared.myProfile.getEclass().getTerm() + "/" + LaoSchoolShared.myProfile.getEclass().getYears());
            }
        } catch (Exception e) {
            Log.e(TAG, "getInformationStudent() -exception:" + e.getMessage());
        }
    }

    private void getTimeTable() {
        int classId = LaoSchoolShared.myProfile.getEclass().getId();
        _showProgressLoadingStudent(true);
        LaoSchoolSingleton.getInstance().getDataAccessService().getTimeTables(classId, new AsyncCallback<List<TimeTable>>() {
            @Override
            public void onSuccess(List<TimeTable> result) {
                if (result.size() > 0) {
                    _defineTimeTableStudent(result);
                } else {
                    _showNoData();
                }
            }

            @Override
            public void onFailure(String message) {
                _showError();
            }

            @Override
            public void onAuthFail(String message) {
                LaoSchoolShared.goBackToLoginPage(getActivity());
            }
        });
    }


    private void _showError() {
        try {
            mScrollTimeTableStudent.setVisibility(View.GONE);
            mProgressStudent.setVisibility(View.GONE);
            mErrorStudent.setVisibility(View.VISIBLE);
            mNoDataStudent.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e(TAG, "_showError() -exception:" + e.getMessage());
        }
    }

    private void _showNoData() {
        try {
            mScrollTimeTableStudent.setVisibility(View.GONE);
            mProgressStudent.setVisibility(View.GONE);
            mErrorStudent.setVisibility(View.GONE);
            mNoDataStudent.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e(TAG, "_showNoData() -exception:" + e.getMessage());
        }
    }

    private void _showProgressLoadingStudent(boolean show) {
        try {
            mScrollTimeTableStudent.setVisibility(show ? View.GONE : View.VISIBLE);
            mProgressStudent.setVisibility(show ? View.VISIBLE : View.GONE);
            mErrorStudent.setVisibility(View.GONE);
            mNoDataStudent.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.e(TAG, "_showProgressLoadingStudent() -exception:" + e.getMessage());
        }

    }

    private void _defineTimeTableStudent(List<TimeTable> result) {
        try {
            //get current Term
            ArrayList<Integer> dayOfweek = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6));
            Map<Integer, ArrayList<TimeTable>> timeTablebyDayMap = new HashMap<>();

            for (Integer day : dayOfweek) {
                ArrayList<TimeTable> timeTables = new ArrayList<>();
                for (TimeTable timeTable : result) {
                    if (timeTable.getWeekday_id() == (day + 1)) {
                        timeTables.add(timeTable);
                    }
                }
                timeTablebyDayMap.put(day, timeTables);
            }
            DayOfWeekPageAdapter dayOfWeekPageAdapter = new DayOfWeekPageAdapter(timeTablebyDayMap);
            mPageTimeTableStudent.setAdapter(dayOfWeekPageAdapter);
            tabStripStudent.setViewPager(mPageTimeTableStudent);

            _scrollToTopStudent();
            _showProgressLoadingStudent(false);
        } catch (Exception e) {
            Log.e(TAG, "_defineTimeTableStudent() -exception:" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void _scrollToTopStudent() {
        mScrollTimeTableStudent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Ready, move up
                mScrollTimeTableStudent.fullScroll(View.FOCUS_UP);
            }
        });
    }

    public static Fragment instantiate(int containerId, String currentRole) {
        ScreenSchedule fragment = new ScreenSchedule();
        Bundle args = new Bundle();
        args.putInt(LaoSchoolShared.CONTAINER_ID, containerId);
        args.putString(CURRENT_ROLE, currentRole);
        fragment.setArguments(args);
        return fragment;
    }

    public class DayOfWeekPageAdapter extends FragmentPagerAdapter {
        private List<String> dayOfWeeks = new ArrayList<>(Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
        Map<Integer, ArrayList<TimeTable>> timeTablebyDayMap;

        public DayOfWeekPageAdapter(Map<Integer, ArrayList<TimeTable>> timeTablebyDayMap) {
            super(getActivity().getSupportFragmentManager());
            this.timeTablebyDayMap = timeTablebyDayMap;
        }

        @Override
        public Fragment getItem(int position) {
            return DayOfWeekPage.newInstance(position, timeTablebyDayMap.get(position));
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = dayOfWeeks.get(position);
            Log.d(TAG, DayOfWeekPageAdapter.class.getSimpleName() + "getPageTitle() -title:" + title);
            return title;
        }
    }

    public static class DayOfWeekPage extends Fragment {

        private static final String ARG_POSITION = "pos";
        private static final String ARG_LIST = "list";
        private Context context;
        private int page;
        private ArrayList<TimeTable> timeTables;

        public DayOfWeekPage() {
        }

        public static DayOfWeekPage newInstance(int page, ArrayList<TimeTable> timeTables) {
            Bundle args = new Bundle();
            args.putInt(ARG_POSITION, page);
            args.putParcelableArrayList(ARG_LIST, timeTables);
            DayOfWeekPage fragment = new DayOfWeekPage();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                page = getArguments().getInt(ARG_POSITION);
                timeTables = getArguments().getParcelableArrayList(ARG_LIST);
            }
            this.context = getActivity();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.view_day_of_week_time_table, container, false);
            Log.d(TAG, DayOfWeekPage.class.getSimpleName() + ".onCreateView()");
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.mTimeTableStudentbyDayofWeek);
            TextView lbNoSession = (TextView) view.findViewById(R.id.lbNoSession);
            recyclerView.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
            if (timeTables.size() > 0) {
                lbNoSession.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(new SessionAdapter(context, 1, timeTables));
                //recyclerView.setNestedScrollingEnabled(false);
            } else {
                lbNoSession.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }


            return view;
        }
    }
}
