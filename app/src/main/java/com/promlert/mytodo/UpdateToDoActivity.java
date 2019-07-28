package com.promlert.mytodo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.promlert.mytodo.db.ToDo;
import com.promlert.mytodo.db.ToDoRepository;

public class UpdateToDoActivity extends AppCompatActivity {

    private EditText mTitleEditText, mDetailsEditText;
    private CheckBox mFinishedCheckBox;
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

        mTitleEditText.setText(mToDo.getTitle());
        mDetailsEditText.setText(mToDo.getDetails());
        mFinishedCheckBox.setChecked(mToDo.isFinished());

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
                                ToDoRepository repo = new ToDoRepository(UpdateToDoActivity.this);
                                repo.deleteToDo(mToDo.getId());
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

            ToDoRepository repo = new ToDoRepository(UpdateToDoActivity.this);
            repo.updateToDo(mToDo.getId(), title, details, finished);
            finish();
        }
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
