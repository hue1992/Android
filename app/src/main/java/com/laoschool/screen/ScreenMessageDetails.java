package com.laoschool.screen;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.laoschool.R;
import com.laoschool.adapter.RecyclerViewConversionMessageAdapter;
import com.laoschool.entities.Message;
import com.laoschool.model.AsyncCallback;
import com.laoschool.model.DataAccessImpl;
import com.laoschool.model.DataAccessInterface;
import com.laoschool.model.sqlite.CRUDMessage;
import com.laoschool.shared.LaoSchoolShared;
import com.laoschool.view.FragmentLifecycle;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScreenMessageDetails extends Fragment implements FragmentLifecycle {


    private static final String TAG = "ScreenMessageDetails";
    private int containerId;
    private TextView txtTilteMessageDetails;
    private TextView txtContentMessageDetails;
    private TextView txtDateMessageDetails;
    private TextView txtFormUserNameMessageDetails;
    private TextView txtToUserNameMessageDetails;
    private ImageView imgPiorityMessageDetails;
    private ImageView imgUserSentMessageAvata;


    private RecyclerView mRecylerViewConversionMessage;
    private Context context;
    private DataAccessInterface service;
    private CRUDMessage crudMessage;


    EditText txtConversionMessage;
    TextView btnSendMesasage;
    CheckBox cbSwichUser;

    CheckBox cbSMS;

    public ScreenMessageDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            containerId = getArguments().getInt(LaoSchoolShared.CONTAINER_ID);
            Log.d(getString(R.string.title_screen_message_details), "-Container Id:" + containerId);
        }
        this.context = getActivity();
        this.service = DataAccessImpl.getInstance(context);
        this.crudMessage = new CRUDMessage(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.screen_message_details, container, false);
        txtTilteMessageDetails = (TextView) view.findViewById(R.id.txtTilteMessageDetails);
        txtContentMessageDetails = (TextView) view.findViewById(R.id.txtContentMessageDetails);
        txtDateMessageDetails = (TextView) view.findViewById(R.id.txtDateMessageDetails);
        txtFormUserNameMessageDetails = (TextView) view.findViewById(R.id.txtFormUserNameMessageDetails);
        txtToUserNameMessageDetails = (TextView) view.findViewById(R.id.txtToUserNameMessageDetails);
        imgPiorityMessageDetails = (ImageView) view.findViewById(R.id.imgPiorityMessageDetails);
        imgUserSentMessageAvata = (ImageView) view.findViewById(R.id.imgUserSentMessageAvata);

        //init Conversion list
        mRecylerViewConversionMessage = (RecyclerView) view.findViewById(R.id.mRecylerViewConversionMessage);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecylerViewConversionMessage.setLayoutManager(linearLayoutManager);


        txtConversionMessage = (EditText) view.findViewById(R.id.txtConversionMessage);
        btnSendMesasage = (TextView) view.findViewById(R.id.btnSendMesasage);

        cbSwichUser = (CheckBox) view.findViewById(R.id.cbSwichUser);

        cbSMS = (CheckBox) view.findViewById(R.id.cbSMS);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_screen_message_details, menu);
    }


    @Override
    public void onPauseFragment() {
    }

    @Override
    public void onResumeFragment() {
        try {
            String tag = LaoSchoolShared.makeFragmentTag(containerId, LaoSchoolShared.POSITION_SCREEN_MESSAGE_0);
            ScreenMessage screenMessage = (ScreenMessage) ((HomeActivity) getActivity()).getSupportFragmentManager().findFragmentByTag(tag);
            if (screenMessage != null) {
//            Message
                Message message = screenMessage.getMessage();
                if (message == null) {
                    message = Message.fromJson("{\n" +
                            "  \"id\": 1,\n" +
                            "  \"school_id\": 1,\n" +
                            "  \"class_id\": 1,\n" +
                            "  \"from_usr_id\": 1,\n" +
                            "  \"from_user_name\": \"NamNT1\",\n" +
                            "  \"to_usr_id\": 2,\n" +
                            "  \"to_user_name\": \"Hue1\",\n" +
                            "  \"content\": \"test message\",\n" +
                            "  \"msg_type_id\": 1,\n" +
                            "  \"channel\": 1,\n" +
                            "  \"is_sent\": 1,\n" +
                            "  \"sent_dt\": \"2016-03-24 00:00:00.0\",\n" +
                            "  \"is_read\": 1,\n" +
                            "  \"read_dt\": \"2016-03-24 00:00:00.0\",\n" +
                            "  \"imp_flg\": 1,\n" +
                            "  \"other\": \"ko co gi quan trong\",\n" +
                            "  \"title\": \"test title\",\n" +
                            "  \"cc_list\": \"\",\n" +
                            "  \"schoolName\": \"Truong Tieu Hoc Thanh Xuan Trung\",\n" +
                            "  \"messageType\": \"NX\"\n" +
                            "}");
                    Toast.makeText(getActivity(), "Message null", Toast.LENGTH_SHORT).show();
                }
                txtTilteMessageDetails.setText(message.getTitle());
                txtContentMessageDetails.setText(message.getContent());
                //
                DateFormat outputFormatter1 = new SimpleDateFormat("dd-MMM-yyyy");
                DateFormat inputFormatter1 = new SimpleDateFormat("yyyy-MM-dd");

                Date date1;
                if (message.getSent_dt() != null) {
                    date1 = inputFormatter1.parse(message.getSent_dt());
                } else {
                    date1 = new Date();
                }
                String output1 = outputFormatter1.format(date1);
                txtDateMessageDetails.setText(output1);


                imgPiorityMessageDetails.setColorFilter(message.getImp_flg() == 1 ?
                        screenMessage.getActivity().getResources().getColor(R.color.colorLaosRed)
                        : screenMessage.getActivity().getResources().getColor(R.color.color_messsage_read));
                imgUserSentMessageAvata.setColorFilter(screenMessage.getActivity().getResources().getColor(R.color.color_messsage_tilte_not_read));

                txtFormUserNameMessageDetails.setText(message.getFrom_user_name());
                txtToUserNameMessageDetails.setText("to " + message.getTo_user_name());

                //set Adaper
//            List<Message> messages=crudMessage.getAllMessages();
//            _setConversionMessages(messages);
                service.getMessages("", "", "", "", "", "", "", "", new AsyncCallback<List<Message>>() {
                    @Override
                    public void onSuccess(List<Message> result) {
                        _setConversionMessages(result);
                    }

                    @Override
                    public void onFailure(String message) {
                        Log.d(TAG, "set Adaper error:" + message);
                    }
                });


                //Handler onclic send
                btnSendMesasage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _sendMessage();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void _setConversionMessages(List<Message> messages) {
        RecyclerViewConversionMessageAdapter viewConversionMessage = new RecyclerViewConversionMessageAdapter(getActivity(), messages);
        mRecylerViewConversionMessage.setAdapter(viewConversionMessage);

        //Scroll to bottom
        mRecylerViewConversionMessage.scrollToPosition(messages.size() - 1);
    }


    public static Fragment instantiate(int containerId, String currentRole) {
        ScreenMessageDetails fragment = new ScreenMessageDetails();
        Bundle args = new Bundle();
        args.putInt(LaoSchoolShared.CONTAINER_ID, containerId);
        fragment.setArguments(args);
        return fragment;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            //show soft keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private boolean validateMessageTitle(EditText edit) {
        if (edit.getText().toString().trim().isEmpty()) {
            requestFocus(edit);
            Toast.makeText(context, R.string.err_msg_input_message_title, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void _sendMessage() {
        if (!validateMessageTitle(txtConversionMessage)) {
            return;
        }
        if (LaoSchoolShared.checkConn(context)) {
            if (LaoSchoolShared.myProfile != null) {
                String messageValue = txtConversionMessage.getText().toString();
                if (LaoSchoolShared.myProfile.getEclass() != null) {
                    final Message message = new Message();

                    message.setTitle("Title:" + messageValue);
                    message.setContent(messageValue);
                    message.setChannel(0);

                    //
                    if (cbSwichUser.isChecked()) {
                        message.setFrom_usr_id(LaoSchoolShared.myProfile.getEclass().getHead_teacher_id());
                        message.setTo_usr_id(LaoSchoolShared.myProfile.getId());
                    } else {
                        message.setFrom_usr_id(LaoSchoolShared.myProfile.getId());
                        message.setTo_usr_id(LaoSchoolShared.myProfile.getEclass().getHead_teacher_id());
                    }

                    message.setFrom_user_name(LaoSchoolShared.myProfile.getFullname());
                    message.setTo_user_name("");
                    message.setClass_id(LaoSchoolShared.myProfile.getEclass().getId());

                    message.setChannel((cbSMS.isChecked()) ? 0 : 1);

                    service.createMessage(message, new AsyncCallback<Message>() {
                        @Override
                        public void onSuccess(Message result) {
                            Log.d(TAG, "Message results:" + result.toJson());
                            // save local
                            crudMessage.addMessage(result);
                            _resetForm();
                            //Reload conversion
                            _refeshData();
                        }

                        @Override
                        public void onFailure(String message1) {
                            Log.d(TAG, R.string.err_msg_create_message + ":" + message1);
                            _resetForm();
                        }
                    });

                } else {
                    Toast.makeText(context, R.string.err_msg_create_message, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, R.string.err_msg_create_message + "_1");

                }
            } else {
                Toast.makeText(context, R.string.err_msg_create_message, Toast.LENGTH_SHORT).show();
                Log.d(TAG, R.string.err_msg_create_message + "_2");
            }
        } else {
            Toast.makeText(context, R.string.err_msg_network_disconnect, Toast.LENGTH_SHORT).show();
        }

    }

    private void _refeshData() {
        //set Adaper
//        List<Message> messages = crudMessage.getAllMessages();
//        _setConversionMessages(messages);
        service.getMessages("", "", "", "", "", "", "", "", new AsyncCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> result) {
                _setConversionMessages(result);
            }

            @Override
            public void onFailure(String message) {
                Log.d(TAG, "set Adaper error:" + message);
            }
        });
    }

    private void _resetForm() {
        txtConversionMessage.getText().clear();
        txtConversionMessage.clearFocus();
        //Hide key board
        LaoSchoolShared.hideSoftKeyboard(getActivity());
    }

}