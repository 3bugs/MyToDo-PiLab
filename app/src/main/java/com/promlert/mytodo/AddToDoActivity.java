package com.promlert.mytodo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class AddToDoActivity extends AppCompatActivity {

    private EditText mTitleEditText, mDetailsEditText, mDueDateEditText;
    private ProgressBar mProgressBar;

    private Calendar mCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_do);

        mTitleEditText = findViewById(R.id.title_edit_text);
        mDetailsEditText = findViewById(R.id.details_edit_text);
        mDueDateEditText = findViewById(R.id.due_date_edit_text);

        mProgressBar = findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        Utils.setupDatePicker(AddToDoActivity.this, mCalendar, mDueDateEditText);

        Button saveButton = findViewById(R.id.save_button);
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

            /*ToDoRepository repo = new ToDoRepository(AddToDoActivity.this);
            repo.addToDo(title, details);*/

            ToDo toDo = new ToDo();
            toDo.setTitle(title);
            toDo.setDetails(details);
            toDo.setDueDate(dueDate);

            mProgressBar.setVisibility(View.VISIBLE);

            Retrofit retrofit = ApiClient.getClient();
            WebServices services = retrofit.create(WebServices.class);
            Call<AddToDoResponse> call = services.addTodo(toDo);
            call.enqueue(new MyRetrofitCallback<>(
                    AddToDoActivity.this,
                    null,
                    mProgressBar,
                    new MyRetrofitCallback.MyRetrofitCallbackListener<AddToDoResponse>() {
                        @Override
                        public void onSuccess(AddToDoResponse responseBody) {
                            String successMessage = responseBody.error.getMessage();
                            Toast.makeText(AddToDoActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            new AlertDialog.Builder(AddToDoActivity.this)
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
}
