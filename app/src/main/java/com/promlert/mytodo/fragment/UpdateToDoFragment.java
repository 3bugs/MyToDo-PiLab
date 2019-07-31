package com.promlert.mytodo.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.promlert.mytodo.MainActivity;
import com.promlert.mytodo.R;
import com.promlert.mytodo.db.ToDo;
import com.promlert.mytodo.etc.DateFormatConverter;
import com.promlert.mytodo.etc.Utils;
import com.promlert.mytodo.net.ApiClient;
import com.promlert.mytodo.net.DeleteToDoResponse;
import com.promlert.mytodo.net.MyRetrofitCallback;
import com.promlert.mytodo.net.UpdateToDoResponse;
import com.promlert.mytodo.net.WebServices;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Retrofit;

public class UpdateToDoFragment extends Fragment {

    private static final String TAG = UpdateToDoFragment.class.getName();
    private static final String ARG_JSON_TODO = "json_todo";

    private EditText mTitleEditText, mDetailsEditText, mDueDateEditText;
    private CheckBox mFinishedCheckBox;
    private ProgressBar mProgressBar;

    private Calendar mCalendar = Calendar.getInstance();
    private ToDo mToDo;

    private UpdateToDoFragmentCallback mCallback;

    public UpdateToDoFragment() {
        // Required empty public constructor
    }

    public static UpdateToDoFragment newInstance(ToDo toDo) {
        UpdateToDoFragment fragment = new UpdateToDoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_JSON_TODO, new Gson().toJson(toDo));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String jsonToDo = getArguments().getString(ARG_JSON_TODO);
            mToDo = new Gson().fromJson(jsonToDo, ToDo.class);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_to_do, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTitleEditText = view.findViewById(R.id.title_edit_text);
        mDetailsEditText = view.findViewById(R.id.details_edit_text);
        mFinishedCheckBox = view.findViewById(R.id.finished_check_box);
        mDueDateEditText = view.findViewById(R.id.due_date_edit_text);

        mTitleEditText.setText(mToDo.getTitle());
        mDetailsEditText.setText(mToDo.getDetails());
        mFinishedCheckBox.setChecked(mToDo.isFinished());
        mCalendar.setTime(mToDo.getDueDate());

        String formatDate = DateFormatConverter.formatForUi(mCalendar.getTime());
        mDueDateEditText.setText(formatDate);

        mProgressBar = view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        Utils.setupDatePicker(getActivity(), mCalendar, mDueDateEditText);

        Button saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateToDo();
            }
        });

        Button deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.app_name)
                        .setMessage("ยืนยันลบ ToDo นี้หรือไม่?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteToDo();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

    }

    private void updateToDo() {
        if (validateForm()) {
            String title = mTitleEditText.getText().toString().trim();
            String details = mDetailsEditText.getText().toString().trim();
            boolean finished = mFinishedCheckBox.isChecked();

            ToDo toDo = new ToDo();
            toDo.setId(mToDo.getId());
            toDo.setTitle(title);
            toDo.setDetails(details);
            toDo.setFinished(finished);
            toDo.setDueDate(mCalendar.getTime());

            mProgressBar.setVisibility(View.VISIBLE);

            Retrofit retrofit = ApiClient.getClient();
            WebServices services = retrofit.create(WebServices.class);
            Call<UpdateToDoResponse> call = services.updateTodo(toDo);
            call.enqueue(new MyRetrofitCallback<>(
                    getActivity(),
                    null,
                    mProgressBar,
                    new MyRetrofitCallback.MyRetrofitCallbackListener<UpdateToDoResponse>() {
                        @Override
                        public void onSuccess(UpdateToDoResponse responseBody) {
                            if (mCallback != null) {
                                String successMessage = responseBody.error.getMessage();
                                mCallback.onUpdateSuccess(successMessage);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                                    .setTitle("Error")
                                    .setMessage(errorMessage)
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    }
            ));
        }
    }

    private void deleteToDo() {
        ToDo toDo = new ToDo();
        toDo.setId(mToDo.getId());

        mProgressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);
        Call<DeleteToDoResponse> call = services.deleteTodo(toDo);
        call.enqueue(new MyRetrofitCallback<>(
                getActivity(),
                null,
                mProgressBar,
                new MyRetrofitCallback.MyRetrofitCallbackListener<DeleteToDoResponse>() {
                    @Override
                    public void onSuccess(DeleteToDoResponse responseBody) {
                        if (mCallback != null) {
                            String successMessage = responseBody.error.getMessage();
                            mCallback.onDeleteSuccess(successMessage);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                                .setTitle("Error")
                                .setMessage(errorMessage)
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }
        ));
    }

    private boolean validateForm() {
        boolean valid = true;

        String title = mTitleEditText.getText().toString().trim();
        String details = mDetailsEditText.getText().toString().trim();

        if (title.isEmpty()) {
            mTitleEditText.setError("กรุณากรอกหัวข้อ ToDo");
            valid = false;
        }
        if (details.isEmpty()) {
            mDetailsEditText.setError("กรุณากรอกรายละเอียด ToDo");
            valid = false;
        }

        return valid;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UpdateToDoFragmentCallback) {
            mCallback = (UpdateToDoFragmentCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement UpdateToDoFragmentCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            Toolbar toolbar = activity.getToolbar();
            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            });
        }
    }

    public interface UpdateToDoFragmentCallback {
        void onUpdateSuccess(String successMessage);
        void onDeleteSuccess(String successMessage);
    }
}
