package com.laoschool.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laoschool.R;
import com.laoschool.entities.ExamResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Hue on 3/11/2016.
 */
public class ExamResultsForStudentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ExamResultsForStudentAdapter.class.getSimpleName();
    private Fragment screen;
    private Context context;
    private List<ExamResult> examResults;
    private List<String> subjects = new ArrayList<>();

    private int TYPE_SUB_HEADER = 0;
    private int TYPE_TITLE = 1;
    private int TYPE_LINE = 2;
    Map<Integer, Map<Integer, ArrayList<ExamResult>>> listExamMap = new HashMap<>();

    private List<String> subjectNames = new ArrayList<>();
    private List<Integer> subjectIds = new ArrayList<>();
    private List<Integer> origin_subjectIds = new ArrayList<>();
    private List<String> origin_subjectNames = new ArrayList<>();

    Map<Integer, List<ExamResult>> hashExam = new HashMap<>();
    Map<Integer, String> mapSubject = new HashMap<>();

    public ExamResultsForStudentAdapter(Fragment screen, List<ExamResult> examResults) {
        this.screen = screen;
        this.context = screen.getActivity();
        if (examResults != null) {
            this.examResults = examResults;

            for (ExamResult examResult : examResults) {
                int subjectId = examResult.getSubject_id();
                String subjectName = examResult.getSubjectName();
                List<ExamResult> temp = null;
                if (hashExam.containsKey(subjectId)) {
                    temp = hashExam.get(subjectId);
                    temp.add(examResult);
                } else {
                    temp = new ArrayList<>();
                    temp.add(examResult);
                }
                Collections.sort(temp);
                hashExam.put(subjectId, temp);
                mapSubject.put(subjectId, subjectName);
            }

            for (Integer subId : mapSubject.keySet()) {
                origin_subjectIds.add(subId);
                origin_subjectNames.add(mapSubject.get(subId));
                subjectIds.add(subId);
                subjectNames.add(mapSubject.get(subId));
            }
            subjectIds.addAll(origin_subjectIds);
            subjectNames.addAll(origin_subjectNames);

        } else {
            this.examResults = new ArrayList<>();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_LINE)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_line, parent, false); //Inflating the layout
        else if (viewType == TYPE_TITLE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_exam_results_student, parent, false); //Inflating the layout
        } else if (viewType == TYPE_SUB_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_only_title, parent, false); //Inflating the layout
        }
        ExamResultsStudentSemesterAdapterViewHolder viewHolder = new ExamResultsStudentSemesterAdapterViewHolder(view, viewType);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof ExamResultsStudentSemesterAdapterViewHolder) {
                ExamResultsStudentSemesterAdapterViewHolder semesterHolder = (ExamResultsStudentSemesterAdapterViewHolder) holder;
                View view = semesterHolder.view;
                if (semesterHolder.viewType == TYPE_TITLE) {
                    final String title = subjectNames.get(position);
//                //Define and set data
                    TextView txtSubjectScreenResultsStudent = (TextView) view.findViewById(R.id.txtSubjectScreenResultsStudent);
                    RecyclerView mListScoreBySemester = (RecyclerView) view.findViewById(R.id.mListScoreBySemester);
                    //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

                    GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false);
                    mListScoreBySemester.setLayoutManager(gridLayoutManager);

                    txtSubjectScreenResultsStudent.setText(title);

                    int subjectIdbyPosition = subjectIds.get(position);

                    List<ExamResult> examResults = hashExam.get(subjectIdbyPosition);

                    ScoreStudentSemesterAdapter finalAdapter = new ScoreStudentSemesterAdapter(context, examResults);
                    mListScoreBySemester.setAdapter(finalAdapter);
                    mListScoreBySemester.setNestedScrollingEnabled(false);
                } else {

                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder() - exception messages:" + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return subjectIds.size();
    }

    @Override
    public int getItemViewType(int position) {
//        String item = strings.get(position);
//        if (item.equals(context.getString(R.string.row_sub_header)))
//            return TYPE_SUB_HEADER;
//        else if (!item.equals(context.getString(R.string.row_line)))
        return TYPE_TITLE;
//        else
//            return TYPE_LINE;

    }

    public class ExamResultsStudentSemesterAdapterViewHolder extends RecyclerView.ViewHolder {
        View view;
        int viewType;

        public ExamResultsStudentSemesterAdapterViewHolder(View itemView, int viewType) {
            super(itemView);
            this.view = itemView;
            this.viewType = viewType;
        }
    }
}
