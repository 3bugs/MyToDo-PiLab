package com.promlert.mytodo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.promlert.mytodo.MainActivity;
import com.promlert.mytodo.R;
import com.promlert.mytodo.db.ToDo;
import com.promlert.mytodo.etc.Utils;
import com.promlert.mytodo.net.AddToDoResponse;
import com.promlert.mytodo.net.ApiClient;
import com.promlert.mytodo.net.MyRetrofitCallback;
import com.promlert.mytodo.net.WebServices;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Retrofit;

public class AddToDoFragment extends Fragment {

    private static final String TAG = AddToDoFragment.class.getName();

    private EditText mTitleEditText, mDetailsEditText, mDueDateEditText;
    private ProgressBar mProgressBar;

    private Calendar mCalendar = Calendar.getInstance();
    private AddToDoFragmentCallback mCallback;

    public AddToDoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_to_do, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTitleEditText = view.findViewById(R.id.title_edit_text);
        mDetailsEditText = view.findViewById(R.id.details_edit_text);
        mDueDateEditText = view.findViewById(R.id.due_date_edit_text);

        mProgressBar = view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        Utils.setupDatePicker(getActivity(), mCalendar, mDueDateEditText);

        Button saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToDo();
            }
        });
    }

    private void addToDo() {
        if (validateForm()) {
            String title = mTitleEditText.getText().toString().trim();
            String details = mDetailsEditText.getText().toString().trim();
            Date dueDate = mCalendar.getTime();

            ToDo toDo = new ToDo();
            toDo.setTitle(title);
            toDo.setDetails(details);
            toDo.setDueDate(dueDate);

            mProgressBar.setVisibility(View.VISIBLE);

            Retrofit retrofit = ApiClient.getClient();
            WebServices services = retrofit.create(WebServices.class);
            Call<AddToDoResponse> call = services.addTodo(toDo);
            call.enqueue(new MyRetrofitCallback<>(
                    getActivity(),
                    null,
                    mProgressBar,
                    new MyRetrofitCallback.MyRetrofitCallbackListener<AddToDoResponse>() {
                        @Override
                        public void onSuccess(AddToDoResponse responseBody) {
                            if (mCallback != null) {
                                String successMessage = responseBody.error.getMessage();
                                mCallback.onAddSuccess(successMessage);
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Error")
                                    .setMessage(errorMessage)
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    }
            ));
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String title = mTitleEditText.getText().toString().trim();
        String details = mDetailsEditText.getText().toString().trim();
        String dueDate = mDueDateEditText.getText().toString().trim();

        if (title.isEmpty()) {
            mTitleEditText.setError("กรุณากรอกหัวข้อ ToDo");
            valid = false;
        }
        if (details.isEmpty()) {
            mDetailsEditText.setError("กรุณากรอกรายละเอียด ToDo");
            valid = false;
        }
        if (dueDate.isEmpty()) {
            mDueDateEditText.setError("กรุณาระบุวัน");
            valid = false;
        }

        return valid;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddToDoFragmentCallback) {
            mCallback = (AddToDoFragmentCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AddToDoFragmentCallback");
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

    public interface AddToDoFragmentCallback {
        void onAddSuccess(String successMessage);
    }
}
