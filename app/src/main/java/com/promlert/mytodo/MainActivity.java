package com.promlert.mytodo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.promlert.mytodo.db.ToDo;
import com.promlert.mytodo.fragment.AddToDoFragment;
import com.promlert.mytodo.fragment.ToDoListFragment;
import com.promlert.mytodo.fragment.UpdateToDoFragment;
import com.promlert.mytodo.net.ApiClient;
import com.promlert.mytodo.net.MyRetrofitCallback;
import com.promlert.mytodo.net.UpdateToDoResponse;
import com.promlert.mytodo.net.WebServices;

import retrofit2.Call;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements
        ToDoListFragment.ToDoListFragmentCallback,
        AddToDoFragment.AddToDoFragmentCallback,
        UpdateToDoFragment.UpdateToDoFragmentCallback {

    private static final String TAG = MainActivity.class.getName();
    private static final String TAG_FRAGMENT_TODO_LIST = "fragment_todo_list";
    private static final String TAG_FRAGMENT_ADD_TODO = "fragment_add_todo";
    private static final String TAG_FRAGMENT_UPDATE_TODO = "fragment_update_todo";

    private FloatingActionButton mFab;
    private ProgressBar mProgressBar;

    protected enum FragmentTransitionType {
        NONE,
        SLIDE,
        FADE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(
                        R.id.fragment_container,
                        new ToDoListFragment(),
                        TAG_FRAGMENT_TODO_LIST
                )
                .commit();
    }

    private void setupViews() {
        mProgressBar = findViewById(R.id.progress_bar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = findViewById(R.id.floating_action_button);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAdd();
            }
        });
    }

    protected void loadFragment(Fragment fragment, String tag, boolean addToBackStack,
                                FragmentTransitionType transitionType) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (transitionType == FragmentTransitionType.SLIDE) {
            transaction.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
            );
        } else if (transitionType == FragmentTransitionType.FADE) {
            transaction.setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
            );
        }
        transaction.replace(
                R.id.fragment_container,
                fragment,
                tag
        );
        if (addToBackStack) {
            transaction.addToBackStack(null).commit();
        } else {
            transaction.commit();
        }
    }

    protected void popBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }

    public void onClickAdd() {
        loadFragment(
                new AddToDoFragment(),
                TAG_FRAGMENT_ADD_TODO,
                true,
                FragmentTransitionType.FADE
        );
    }

    @Override
    public void onClickItem(ToDo toDo) {
        loadFragment(
                UpdateToDoFragment.newInstance(toDo),
                TAG_FRAGMENT_UPDATE_TODO,
                true,
                FragmentTransitionType.SLIDE
        );
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
                        //Toast.makeText(getActivity(), successMessage, Toast.LENGTH_SHORT).show();
                        Snackbar.make(
                                findViewById(R.id.content),
                                successMessage,
                                Snackbar.LENGTH_LONG
                        ).show();
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

    @Override
    public void onAddSuccess(String successMessage) {
        handleAddUpdateChangeSuccess(successMessage);
    }

    @Override
    public void onUpdateSuccess(String successMessage) {
        handleAddUpdateChangeSuccess(successMessage);
    }

    @Override
    public void onDeleteSuccess(String successMessage) {
        handleAddUpdateChangeSuccess(successMessage);
    }

    private void handleAddUpdateChangeSuccess(String successMessage) {
        popBackStack();
        Snackbar.make(
                findViewById(R.id.content),
                successMessage,
                Snackbar.LENGTH_LONG
        ).show();

        ToDoListFragment fragment = (ToDoListFragment)
                getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_TODO_LIST);
        if (fragment != null) {
            fragment.reloadData();
        } else {
            Log.w(TAG, "ToDoListFragment NOT FOUND!");
        }
    }

    public void setFabVisibility(int visibility) {
        mFab.setVisibility(visibility);
    }
}
