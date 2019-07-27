package com.promlert.mytodo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.promlert.mytodo.adapter.ToDoListAdapter;
import com.promlert.mytodo.db.ToDo;
import com.promlert.mytodo.db.ToDoRepository;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private RecyclerView mToDoRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToDoRecyclerView = findViewById(R.id.todo_recycler_view);
        mToDoRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        FloatingActionButton fab = findViewById(R.id.floating_action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddToDoActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        ToDoRepository repo = new ToDoRepository(MainActivity.this);
        repo.getAllToDo(new ToDoRepository.Callback() {
            @Override
            public void onGetTodo(List<ToDo> todoList) {
                for (ToDo todo : todoList) {
                    Log.i(TAG, todo.getTitle());
                }
                ToDoListAdapter adapter = new ToDoListAdapter(MainActivity.this, todoList);
                mToDoRecyclerView.setAdapter(adapter);
            }
        });
    }
}
