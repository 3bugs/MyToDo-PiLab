package com.promlert.mytodo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.promlert.mytodo.adapter.ToDoListAdapter;
import com.promlert.mytodo.db.ToDo;
import com.promlert.mytodo.net.ApiClient;
import com.promlert.mytodo.net.GetToDoResponse;
import com.promlert.mytodo.net.MyRetrofitCallback;
import com.promlert.mytodo.net.UpdateToDoResponse;
import com.promlert.mytodo.net.WebServices;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements ToDoListAdapter.Callback {

    private static final String TAG = MainActivity.class.getName();
    static final int REQUEST_ADD = 0;
    static final int REQUEST_UPDATE = 1;

    private RecyclerView mToDoRecyclerView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToDoRecyclerView = findViewById(R.id.todo_recycler_view);
        mToDoRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        mProgressBar = findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        FloatingActionButton fab = findViewById(R.id.floating_action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddToDoActivity.class);
                startActivityForResult(intent, REQUEST_ADD);
            }
        });

        reloadData();

        /*ตัวอย่างการใช้ OkHttp (ก่อนเปลี่ยนมาใช้ Retrofit)*/
        /*OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.i(TAG, result);
            }
        });*/
    }

    private void reloadData() {
        /*ToDoRepository repo = new ToDoRepository(MainActivity.this);
        repo.getAllToDo(new ToDoRepository.Callback() {
            @Override
            public void onGetTodo(List<ToDo> todoList) {
                for (ToDo todo : todoList) {
                    Log.i(TAG, todo.getTitle());
                }
                ToDoListAdapter adapter = new ToDoListAdapter(MainActivity.this, todoList);
                mToDoRecyclerView.setAdapter(adapter);
            }
        });*/

        mProgressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);
        Call<GetToDoResponse> call = services.getAllTodo();
        call.enqueue(new MyRetrofitCallback<>(
                MainActivity.this,
                null,
                mProgressBar,
                new MyRetrofitCallback.MyRetrofitCallbackListener<GetToDoResponse>() {
                    @Override
                    public void onSuccess(GetToDoResponse responseBody) {
                        List<ToDo> toDoList = responseBody.data;
                        ToDoListAdapter adapter = new ToDoListAdapter(
                                MainActivity.this, // context
                                toDoList, // data source
                                MainActivity.this // callback สำหรับ adapter (เมธอด onItemClick ข้างล่าง)
                        );
                        mToDoRecyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Error")
                                .setMessage(errorMessage)
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }
        ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD || requestCode == REQUEST_UPDATE) {
            if (resultCode == RESULT_OK) { // มีการเพิ่มหรืออัพเดทฐานข้อมูล
                mToDoRecyclerView.setAdapter(null); // ล้าง item เดิมใน RecyclerView ไปก่อน
                reloadData(); // โหลดมาใหม่
            }
        }
    }

    @Override
    public void onItemClick(ToDo toDo) {
        Intent intent = new Intent(MainActivity.this, UpdateToDoActivity.class);
        intent.putExtra("todo", toDo);
        startActivityForResult(intent, REQUEST_UPDATE);
    }

    @Override
    public void onFinishedChange(ToDo toDo) {
        mProgressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);
        Call<UpdateToDoResponse> call = services.updateTodo(toDo);
        call.enqueue(new MyRetrofitCallback<>(
                MainActivity.this,
                null,
                mProgressBar,
                new MyRetrofitCallback.MyRetrofitCallbackListener<UpdateToDoResponse>() {
                    @Override
                    public void onSuccess(UpdateToDoResponse responseBody) {
                        String successMessage = responseBody.error.getMessage();
                        Toast.makeText(MainActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                                .setTitle("Error")
                                .setMessage(errorMessage)
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }
        ));
    }
}
