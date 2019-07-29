package com.promlert.mytodo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class UpdateToDoActivity extends AppCompatActivity {

    private EditText mTitleEditText, mDetailsEditText, mDueDateEditText;
    private CheckBox mFinishedCheckBox;
    private ProgressBar mProgressBar;

    private Calendar mCalendar = Calendar.getInstance();
    private ToDo mToDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_to_do);

        Intent intent = getIntent();
        mToDo = (ToDo) intent.getSerializableExtra("todo");

        mTitleEditText = findViewById(R.id.title_edit_text);
        mDetailsEditText = findViewById(R.id.details_edit_text);
        mFinishedCheckBox = findViewById(R.id.finished_check_box);
        mDueDateEditText = findViewById(R.id.due_date_edit_text);

        mTitleEditText.setText(mToDo.getTitle());
        mDetailsEditText.setText(mToDo.getDetails());
        mFinishedCheckBox.setChecked(mToDo.isFinished());
        mCalendar.setTime(mToDo.getDueDate());

        String formatDate = DateFormatConverter.formatForUi(mCalendar.getTime());
        mDueDateEditText.setText(formatDate);

        mProgressBar = findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        Utils.setupDatePicker(UpdateToDoActivity.this, mCalendar, mDueDateEditText);

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateToDo();
            }
        });

        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UpdateToDoActivity.this)
                        .setTitle(R.string.app_name)
                        .setMessage("ยืนยันลบ ToDo นี้หรือไม่?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /*ToDoRepository repo = new ToDoRepository(UpdateToDoActivity.this);
                                repo.deleteToDo(mToDo.getId());*/
                                deleteToDo();
                                finish();
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

            /*ToDoRepository repo = new ToDoRepository(UpdateToDoActivity.this);
            repo.updateToDo(mToDo.getId(), title, details, finished);*/

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
                    UpdateToDoActivity.this,
                    null,
                    mProgressBar,
                    new MyRetrofitCallback.MyRetrofitCallbackListener<UpdateToDoResponse>() {
                        @Override
                        public void onSuccess(UpdateToDoResponse responseBody) {
                            String successMessage = responseBody.error.getMessage();
                            Toast.makeText(UpdateToDoActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            new androidx.appcompat.app.AlertDialog.Builder(UpdateToDoActivity.this)
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
                UpdateToDoActivity.this,
                null,
                mProgressBar,
                new MyRetrofitCallback.MyRetrofitCallbackListener<DeleteToDoResponse>() {
                    @Override
                    public void onSuccess(DeleteToDoResponse responseBody) {
                        String successMessage = responseBody.error.getMessage();
                        Toast.makeText(UpdateToDoActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        new androidx.appcompat.app.AlertDialog.Builder(UpdateToDoActivity.this)
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
}
