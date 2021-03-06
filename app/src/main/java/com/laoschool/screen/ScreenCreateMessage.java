package com.laoschool.screen;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.laoschool.LaoSchoolSingleton;
import com.laoschool.R;
import com.laoschool.entities.Message;
import com.laoschool.entities.MessageSample;
import com.laoschool.entities.User;
import com.laoschool.model.AsyncCallback;
import com.laoschool.model.DataAccessImpl;
import com.laoschool.model.DataAccessInterface;
import com.laoschool.model.sqlite.DataAccessMessage;
import com.laoschool.screen.view.Languages;
import com.laoschool.screen.view.TableStudents;
import com.laoschool.shared.LaoSchoolShared;
import com.laoschool.view.FragmentLifecycle;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScreenCreateMessage extends Fragment implements FragmentLifecycle {

    private static final String TAG = "ScreenCreateMessage";
    private int containerId;
    private String testMessage;
    private String currentRole;
    private Context context;
    private DataAccessInterface service;

    //Student
    private EditText txtMessageTitleStudent;
    private EditText txtMessageContentStudent;
    private CheckBox cbSendSmsStudent;
    //Teacher
    private CheckBox cbSendSms;
    private CheckBox cbImportant;
    private EditText txtMessageContent;
    private ImageView btnListContentSample;

    TextView txtMessageTo;

    //dbsql
    DataAccessMessage dataAccessMessage;

    AlertDialog dialog;
    TableStudents tableStudents;
    User teacher;
    List<User> listStudents = new ArrayList<>();
    List<User> selectedStudents = new ArrayList<>();

    List<MessageSample> list_att_reason = new ArrayList<>();
    List<MessageSample> list_std_reason = new ArrayList<>();

    public void setTestMessage(String testMessage) {
        this.testMessage = testMessage;
    }

    @Override
    public void onPauseFragment() {
        try {
            Log.d(getString(R.string.SCCreateMessage_CreateMessage), "onPauseFragment()");
            //Toast.makeText(getActivity(), "onPauseFragment():" + getString(R.string.title_screen_create_message), Toast.LENGTH_SHORT).show();
            _resetForm();
        } catch (Exception e) {
        }
    }

    @Override
    public void onResumeFragment() {
        if (testMessage != null) {
            if (!testMessage.equals("back")) {
                Log.d(getString(R.string.SCCreateMessage_CreateMessage), "onPauseFragment()");
                Toast.makeText(getActivity(), "message:" + testMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    void getMessageSample() {
        if (list_att_reason.isEmpty()) {
            LaoSchoolSingleton.getInstance().getDataAccessService().getAttMss(new AsyncCallback<List<MessageSample>>() {
                @Override
                public void onSuccess(List<MessageSample> result) {
                    list_att_reason.addAll(result);
                }

                @Override
                public void onFailure(String message) {

                }

                @Override
                public void onAuthFail(String message) {

                }
            });
        }
        if (list_std_reason.isEmpty()) {
            LaoSchoolSingleton.getInstance().getDataAccessService().getStdMss(new AsyncCallback<List<MessageSample>>() {
                @Override
                public void onSuccess(List<MessageSample> result) {
                    list_std_reason.addAll(result);
                }

                @Override
                public void onFailure(String message) {

                }

                @Override
                public void onAuthFail(String message) {

                }
            });
        }
    }

    public void presetData(List<User> students, List<User> selectedStudents, final String defaultText, boolean sampleContent) {
        if (!currentRole.equals(LaoSchoolShared.ROLE_STUDENT)) {
            if (students != null) {
                getMessageSample();
                listStudents.clear();
                this.selectedStudents.clear();
                listStudents.addAll(students);
                this.selectedStudents.addAll(selectedStudents);
//                cbSendSms.setChecked(true);
                String sendTo = "";
                for (User student : selectedStudents) {
                    sendTo = sendTo + student.getFullname() + ", ";
                }

                if (selectedStudents.size() == listStudents.size()) {
                    txtMessageTo.setText(context.getString(R.string.SCCommon_Class) + " " + LaoSchoolShared.selectedClass.getTitle());
                    txtMessageContent.setText(defaultText + context.getString(R.string.SCAttendance_DefaultMessage1));
                } else if (selectedStudents.size() == 1) {
                    txtMessageTo.setText(selectedStudents.get(0).getFullname());
                    txtMessageContent.setText(defaultText + context.getString(R.string.SCAttendance_DefaultMessage2));
                } else {
                    txtMessageTo.setText(context.getString(R.string.SCCreateMessage_TitleAllStudentsAttendance));
                    txtMessageContent.setText(defaultText + context.getString(R.string.SCAttendance_DefaultMessage1));
                }


                if(sampleContent)
                    btnListContentSample.setVisibility(View.VISIBLE);
                else
                    btnListContentSample.setVisibility(View.GONE);
                if(selectedStudents.size() != 1) {
                    btnListContentSample.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final PopupMenu popup = new PopupMenu(context, btnListContentSample);
                            if (list_att_reason.isEmpty()) {
                                LaoSchoolSingleton.getInstance().getDataAccessService().getAttMss(new AsyncCallback<List<MessageSample>>() {
                                    @Override
                                    public void onSuccess(List<MessageSample> result) {
                                        list_att_reason.addAll(result);
                                        for (MessageSample messageSample : list_att_reason) {
                                            SharedPreferences prefs = context.getSharedPreferences(
                                                    LaoSchoolShared.SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
                                            String language = prefs.getString(Languages.PREFERENCES_NAME, null);
                                            if (language != null && language.equals(Languages.LANGUAGE_LAOS))
                                                popup.getMenu().add(messageSample.getLval());
                                            else
                                                popup.getMenu().add(messageSample.getSval());
                                        }
                                    }

                                    @Override
                                    public void onFailure(String message) {

                                    }

                                    @Override
                                    public void onAuthFail(String message) {

                                    }
                                });
                            } else {
                                for (MessageSample messageSample : list_att_reason) {
                                    SharedPreferences prefs = context.getSharedPreferences(
                                            LaoSchoolShared.SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
                                    String language = prefs.getString(Languages.PREFERENCES_NAME, null);
                                    if (language != null && language.equals(Languages.LANGUAGE_LAOS))
                                        popup.getMenu().add(messageSample.getLval());
                                    else
                                        popup.getMenu().add(messageSample.getSval());
                                }
                            }

                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    txtMessageContent.setText(defaultText + menuItem.getTitle());
                                    return false;
                                }
                            });
                            popup.show();
                        }
                    });
                } else {
                    btnListContentSample.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final PopupMenu popup = new PopupMenu(context, btnListContentSample);
                            if (list_att_reason.isEmpty()) {
                                LaoSchoolSingleton.getInstance().getDataAccessService().getStdMss(new AsyncCallback<List<MessageSample>>() {
                                    @Override
                                    public void onSuccess(List<MessageSample> result) {
                                        list_std_reason.addAll(result);
                                        for (MessageSample messageSample : list_std_reason) {
                                            SharedPreferences prefs = context.getSharedPreferences(
                                                    LaoSchoolShared.SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
                                            String language = prefs.getString(Languages.PREFERENCES_NAME, null);
                                            if (language != null && language.equals(Languages.LANGUAGE_LAOS))
                                                popup.getMenu().add(messageSample.getLval());
                                            else
                                                popup.getMenu().add(messageSample.getSval());
                                        }
                                    }

                                    @Override
                                    public void onFailure(String message) {

                                    }

                                    @Override
                                    public void onAuthFail(String message) {

                                    }
                                });
                            } else {
                                for (MessageSample messageSample : list_std_reason) {
                                    SharedPreferences prefs = context.getSharedPreferences(
                                            LaoSchoolShared.SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE);
                                    String language = prefs.getString(Languages.PREFERENCES_NAME, null);
                                    if (language != null && language.equals(Languages.LANGUAGE_LAOS))
                                        popup.getMenu().add(messageSample.getLval());
                                    else
                                        popup.getMenu().add(messageSample.getSval());
                                }
                            }

                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    txtMessageContent.setText(defaultText + menuItem.getTitle());
                                    return false;
                                }
                            });
                            popup.show();
                        }
                    });
                }
            } else {
                btnListContentSample.setVisibility(View.GONE);
                if (listStudents.isEmpty())
                    getListStudents();
            }
        }
    }

    public void getTeacher() {
        LaoSchoolSingleton.getInstance().getDataAccessService().getUsers(LaoSchoolShared.myProfile.getEclass().getId(), User.USER_ROLE_TEACHER, "", -1, new AsyncCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                if(!result.isEmpty())
                    teacher = result.get(0);
                listStudents.add(0, teacher);
                selectedStudents.add(0 , teacher);
            }

            @Override
            public void onFailure(String message) {}

            @Override
            public void onAuthFail(String message) {
                LaoSchoolShared.goBackToLoginPage(context);
            }
        });
    }

    void getListStudents() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(this.getActivity(), "Please wait ...", "Loading ...", true);
        LaoSchoolSingleton.getInstance().getDataAccessService().getUsers(LaoSchoolShared.myProfile.getEclass().getId(), User.USER_ROLE_STUDENT, "", -1, new AsyncCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> result) {
                listStudents.addAll(result);
                ringProgressDialog.dismiss();
                selectedStudents.clear();
                selectedStudents.addAll(listStudents);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(context, context.getString(R.string.SCCreateMessage_CannotGetStudentsList), Toast.LENGTH_SHORT).show();
                ringProgressDialog.dismiss();
            }

            @Override
            public void onAuthFail(String message) {
                LaoSchoolShared.goBackToLoginPage(context);
            }
        });
    }

    interface IScreenCreateMessage {
        void gotoListStudent();

        void goBackToMessage();
    }

    private IScreenCreateMessage iScreenCreateMessage;

    public ScreenCreateMessage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (currentRole == null)
            return inflater.inflate(R.layout.screen_error_application, container, false);
        else {
            if (currentRole.equals(LaoSchoolShared.ROLE_STUDENT)) {
                return _defineCreateMessageStudent(inflater, container);
            } else {
                return _defineCreateMessageTeacher(inflater, container);
            }
        }
    }

    private View _defineCreateMessageTeacher(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.screen_create_message_tearcher, container, false);

        cbSendSms = (CheckBox) view.findViewById(R.id.cbSendSms);
        cbImportant = (CheckBox) view.findViewById(R.id.cbImportant);
        txtMessageContent = (EditText) view.findViewById(R.id.txtMessageContent);
        txtMessageTo = (TextView) view.findViewById(R.id.txtConversionMessageTo);
        btnListContentSample = (ImageView) view.findViewById(R.id.btnListContentSample);

        RelativeLayout btnStudentPicker = (RelativeLayout) view.findViewById(R.id.btnStudentPicker);
        RelativeLayout btnSmsCheck = (RelativeLayout) view.findViewById(R.id.btnSmsCheck);
        RelativeLayout btnImportantCheck = (RelativeLayout) view.findViewById(R.id.btnImportantCheck);
        TextView txvTo = (TextView) view.findViewById(R.id.txvTo);
        TextView txvSMS = (TextView) view.findViewById(R.id.txvSMS);
        TextView txvImportant = (TextView) view.findViewById(R.id.txvImportant);

        txvTo.setText(R.string.SCCreateMessage_To);
        txvSMS.setText(R.string.SCCreateMessage_SMS);
        txvImportant.setText(R.string.SCCreateMessage_Important);
        txtMessageContent.setHint(R.string.SCCreateMessage_Content);

        LaoSchoolShared.selectedClass = LaoSchoolShared.myProfile.getEclass();
        try {
            txtMessageTo.setText(context.getString(R.string.SCCommon_Class) + " " + LaoSchoolShared.selectedClass.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnStudentPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog == null)
                    dialog = new AlertDialog.Builder(context).create();
                if (tableStudents == null) {
                    tableStudents = new TableStudents(context, new TableStudents.TableStudentsListener() {
                        @Override
                        public void onBtnDoneClick(List<User> result) {
                            selectedStudents.clear();
                            selectedStudents.addAll(result);
                            dialog.dismiss();
                            String sendTo = "";
                            for (User student : selectedStudents) {
                                sendTo = sendTo + student.getFullname() + ", ";
                            }
                            if (selectedStudents.size() == listStudents.size())
                                try {
                                    txtMessageTo.setText(context.getString(R.string.SCCommon_Class) + " " + LaoSchoolShared.selectedClass.getTitle());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            else
                                txtMessageTo.setText(sendTo);
                        }

                        @Override
                        public void onBtnCancelClick() {
                            dialog.dismiss();
                        }

                        @Override
                        public void onSearch(List<User> searchList) {
                            if (searchList.size() > 4) {
                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                lp.copyFrom(dialog.getWindow().getAttributes());
                                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 480, getResources().getDisplayMetrics());
                                lp.height = height;
                                dialog.getWindow().setAttributes(lp);
                            } else {
                                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                lp.copyFrom(dialog.getWindow().getAttributes());
                                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                dialog.getWindow().setAttributes(lp);
                            }
                        }
                    });
                    dialog.setView(tableStudents.getView());
                }

//                List<User> students = new ArrayList<User>();
//                for(int i = 1; i<=6;i++) {
//                    User u = new User();
//                    u.setFullname("Student "+i);
//                    students.add(u);
//                }

//                Log.i("StudentList", listStudents.size()+","+selectedStudents.size());

                tableStudents.setData(listStudents, selectedStudents);
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);

                if (listStudents.size() > 4) {
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 480, getResources().getDisplayMetrics());
                    lp.height = height;
                    dialog.getWindow().setAttributes(lp);
                }
            }
        });

        btnSmsCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbSendSms.isChecked())
                    cbSendSms.setChecked(false);
                else
                    cbSendSms.setChecked(true);
            }
        });

        btnImportantCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbImportant.isChecked())
                    cbImportant.setChecked(false);
                else
                    cbImportant.setChecked(true);
            }
        });

        return view;
    }

    private View _defineCreateMessageStudent(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.screen_create_message_student, container, false);
        txtMessageTitleStudent = (EditText) view.findViewById(R.id.txtMessageTitleStudent);
        txtMessageContentStudent = (EditText) view.findViewById(R.id.txtMessageContentStudent);
        txtMessageTo = (TextView) view.findViewById(R.id.txtConversionMessageTo);
        cbSendSmsStudent = (CheckBox) view.findViewById(R.id.cbSendSmsStudent);
        TextView txvTo = (TextView) view.findViewById(R.id.txvTo);
        TextView txvSMS = (TextView) view.findViewById(R.id.txvSMS);

        txvTo.setText(R.string.SCCreateMessage_To);
        txvSMS.setText(R.string.SCCreateMessage_SMS);
        txtMessageContentStudent.setHint(R.string.SCCreateMessage_Content);

        try {
            txtMessageTo.setText(LaoSchoolShared.myProfile.getEclass().getHeadTeacherName());
        } catch (Exception e) {
            //e.printStackTrace();
            Toast.makeText(context, "Server null", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private boolean validateMessageTitle(EditText edit) {
        if (edit.getText().toString().trim().isEmpty()) {
            requestFocus(edit);
            Toast.makeText(context, R.string.SCCreateMessage_err_msg_input_message_title, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateMessageConten(EditText edit) {
        if (edit.getText().toString().trim().isEmpty()) {
            _showErorMessage(getString(R.string.SCCreateMessage_err_msg_input_message_conten), edit);
            return false;
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            //show soft keyboard
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.context = getActivity();
        service = DataAccessImpl.getInstance(context);

        if (getArguments() != null) {
            containerId = getArguments().getInt(LaoSchoolShared.CONTAINER_ID);
            currentRole = getArguments().getString(LaoSchoolShared.CURRENT_ROLE);
            Log.d(getString(R.string.SCCreateMessage_CreateMessage), "-Container Id:" + containerId);
        }
        if (testMessage != null) {
            Log.d(getString(R.string.SCCreateMessage_CreateMessage), "-Message:" + testMessage);
        } else {
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (currentRole != null) {
            //set display menu item
            inflater.inflate(R.menu.menu_screen_create_message, menu);
        }

    }

    public static Fragment instantiate(int containerId, String currentRole) {
        ScreenCreateMessage fragment = new ScreenCreateMessage();
        Bundle args = new Bundle();
        args.putInt(LaoSchoolShared.CONTAINER_ID, containerId);
        args.putString(LaoSchoolShared.CURRENT_ROLE, currentRole);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        iScreenCreateMessage = (IScreenCreateMessage) activity;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (currentRole != null) {
            int id = item.getItemId();
            switch (id) {
                case R.id.action_send_message:
                    _submitForm(currentRole);
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void _submitForm(String currentRole) {
        LaoSchoolShared.hideSoftKeyboard(getActivity());
        if (currentRole.equals(LaoSchoolShared.ROLE_TEARCHER) || currentRole.equals(LaoSchoolShared.ROLE_CLS_PRESIDENT)) {
            if (txtMessageTo.getText().equals("")) {
                Toast.makeText(context, context.getString(R.string.SCCreateMessage_SendNoOne), Toast.LENGTH_SHORT).show();
                return;
            }
            if (txtMessageContent.getText().equals("")) {
                Toast.makeText(context, context.getString(R.string.SCCreateMessage_EmptyMessage), Toast.LENGTH_SHORT).show();
                return;
            }
            //Building messagec
            final Message message = new Message();
            message.setContent(txtMessageContent.getText().toString());
            message.setChannel(cbSendSms.isChecked() ? 1 : 0);
            message.setImp_flg(cbImportant.isChecked() ? 1 : 0);
            message.setFrom_usr_id(LaoSchoolShared.myProfile.getId());
            message.setTo_usr_id(selectedStudents.get(0).getId());
            message.setFrom_user_name(LaoSchoolShared.myProfile.getFullname());
            message.setTo_user_name(selectedStudents.get(0).getFullname());
            message.setClass_id(LaoSchoolShared.myProfile.getEclass().getId());
            message.setSchool_id(LaoSchoolShared.myProfile.getSchool_id());
            //Building cc_list
            String cc_list = "";
            for (User student : selectedStudents)
                cc_list = cc_list + student.getId() + ",";
            message.setCc_list(cc_list);

            Log.d(TAG, "Message results: " + message.toJson());

            //Sending message
            final ProgressDialog ringProgressDialog = ProgressDialog.show(this.getActivity(),
                    context.getString(R.string.SCCommon_PleaseWait) + " ...",
                    context.getString(R.string.SCCommon_Sending) + " ...", true);
            service.createMessage(message, new AsyncCallback<Message>() {
                @Override
                public void onSuccess(Message result) {
                    //result.setIs_read(1);
                    //Save local
                    // dataAccessMessage.addMessage(result);
                    _resetForm();

                    if (iScreenCreateMessage != null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                iScreenCreateMessage.goBackToMessage();
                                ringProgressDialog.dismiss();
                                _showAlertMessage(context.getString(R.string.SCCreateMessage_SendSuccessful));
                            }
                        }, 500);
                    }

                }

                @Override
                public void onFailure(String message1) {
                    Log.d(TAG, R.string.SCCreateMessage_err_msg_create_message + ":" + message1);
                    ringProgressDialog.dismiss();
                    _showAlertMessage(getString(R.string.SCCreateMessage_SendFail));
                }

                @Override
                public void onAuthFail(String message) {
                    LaoSchoolShared.goBackToLoginPage(context);
                }
            });

            return;
        }
        if (!validateMessageConten(txtMessageContentStudent)) {
            return;
        }
        if (LaoSchoolShared.checkConn(context)) {
            if (LaoSchoolShared.myProfile != null) {
                if (LaoSchoolShared.myProfile.getEclass() != null) {
                    final Message message = new Message();

                    //message.setTitle(txtMessageTitleStudent.getText().toString());
                    message.setContent(txtMessageContentStudent.getText().toString());
                    //message.setChannel(cbSendSmsStudent.isChecked() ? 1 : 0);

                    message.setFrom_usr_id(LaoSchoolShared.myProfile.getId());
                    message.setTo_usr_id(LaoSchoolShared.myProfile.getEclass().getHead_teacher_id());
                    message.setFrom_user_name(LaoSchoolShared.myProfile.getFullname());
                    message.setTo_user_name(txtMessageTo.getText().toString());
                    message.setClass_id(LaoSchoolShared.myProfile.getEclass().getId());
                    message.setSchool_id(LaoSchoolShared.myProfile.getSchool_id());

                    final ProgressDialog ringProgressDialog = ProgressDialog.show(this.getActivity(),
                            context.getString(R.string.SCCommon_PleaseWait) + " ...",
                            context.getString(R.string.SCCommon_Sending) + " ...", true);
                    service.createMessage(message, new AsyncCallback<Message>() {
                        @Override
                        public void onSuccess(Message result) {
                            Log.d(TAG, "Message results:" + result.toJson());
                            // result.setIs_read(1);
                            // save local
                            // dataAccessMessage.addMessage(result);
                            _resetForm();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    iScreenCreateMessage.goBackToMessage();
                                    ringProgressDialog.dismiss();
                                    _showAlertMessage(context.getString(R.string.SCCreateMessage_SendSuccessful));
                                }
                            }, 500);

                        }

                        @Override
                        public void onFailure(String message1) {
                            Log.d(TAG, R.string.SCCreateMessage_err_msg_create_message + ":" + message1);
                            ringProgressDialog.dismiss();
                            _showAlertMessage(getString(R.string.SCCreateMessage_SendFail));

                        }

                        @Override
                        public void onAuthFail(String message) {
                            LaoSchoolShared.goBackToLoginPage(context);
                        }
                    });

                } else {
                    Toast.makeText(context, R.string.SCCreateMessage_SendFail, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, R.string.SCCreateMessage_err_msg_create_message + "_1");

                }
            } else {
                Toast.makeText(context, R.string.SCCreateMessage_SendFail, Toast.LENGTH_SHORT).show();
                Log.d(TAG, R.string.SCCreateMessage_err_msg_create_message + "_2");
            }
        } else {
            Toast.makeText(context, R.string.SCCommon_NoInternet, Toast.LENGTH_SHORT).show();
        }
        // }
    }

    private void _showAlertMessage(String alert) {
        Toast.makeText(context, alert, Toast.LENGTH_LONG).show();
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//
//        builder.setMessage(alert);
//        builder.setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
//        Dialog dialog = builder.create();
//        dialog.show();
    }

    private void _showErorMessage(String alert, final EditText edit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(alert);
        builder.setNegativeButton(R.string.SCCommon_Ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestFocus(edit);
                dialogInterface.dismiss();

            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void _resetForm() {
        if (currentRole.equals(LaoSchoolShared.ROLE_TEARCHER) || currentRole.equals(LaoSchoolShared.ROLE_CLS_PRESIDENT)) {
            cbSendSms.setChecked(false);
            cbImportant.setChecked(false);
            txtMessageContent.getText().clear();
            selectedStudents.clear();
            selectedStudents.addAll(listStudents);
            txtMessageTo.setText(context.getString(R.string.SCCommon_Class) + " " + LaoSchoolShared.selectedClass.getTitle());
            if (tableStudents != null)
                tableStudents.reset();
            btnListContentSample.setVisibility(View.GONE);
        } else {
            txtMessageTitleStudent.getText().clear();
            txtMessageContentStudent.getText().clear();
            txtMessageTitleStudent.clearFocus();
            txtMessageContentStudent.clearFocus();
            cbSendSmsStudent.setChecked(false);
        }
    }
}
