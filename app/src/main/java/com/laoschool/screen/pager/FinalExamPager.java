package com.laoschool.screen.pager;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laoschool.R;
import com.laoschool.adapter.MyFinalExamResultsAdapter;
import com.laoschool.entities.ExamResult;
import com.laoschool.entities.FinalResult;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FinalExamPager extends Fragment {

    private static final String TAG = FinalExamPager.class.getSimpleName();
    private static final String ARG_POSITION = "position";
    private static final String ARG_FINAL = "final";
    private int position;
    FinalResult finalResult;
    RecyclerView mListExam;
    private View mNoData;

    public static FinalExamPager create(int position, FinalResult finalResult) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_FINAL, finalResult);
        args.putInt(ARG_POSITION, position);
        FinalExamPager finalExamPager = new FinalExamPager();
        finalExamPager.setArguments(args);
        return finalExamPager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            finalResult = getArguments().getParcelable(ARG_FINAL);
            position = getArguments().getInt(ARG_POSITION);
        }
    }

    public FinalExamPager() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.final_exam_pager, container, false);
        mNoData = view.findViewById(R.id.mNoData);
        mListExam = (RecyclerView) view.findViewById(R.id.mListExam);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mListExam.setLayoutManager(linearLayoutManager);
        if (finalResult != null) {
            List<ExamResult> results = finalResult.getExam_results();
            if (results != null) {
                if (finalResult.getExam_results().size() > 0) {
                    Log.d(TAG, "-size:" + finalResult.getExam_results().size());
                    MyFinalExamResultsAdapter myExamResultsAdapter = new MyFinalExamResultsAdapter(this, position, results);
                    mListExam.setAdapter(myExamResultsAdapter);
                    //mListExam.setNestedScrollingEnabled(true);
                    mNoData.setVisibility(View.GONE);
                    mListExam.setVisibility(View.VISIBLE);
                } else {
                    showNodata();
                }
            } else {
                showNodata();
            }
        } else {
            showNodata();
            Log.d(TAG, "No data");
        }

        return view;
    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//    }

    private void showNodata() {
        mNoData.setVisibility(View.VISIBLE);
        mListExam.setVisibility(View.GONE);
        mListExam.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });

    }

}
