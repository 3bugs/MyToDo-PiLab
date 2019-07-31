package com.promlert.mytodo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.promlert.mytodo.MainActivity;
import com.promlert.mytodo.R;
import com.promlert.mytodo.adapter.ToDoListAdapter;
import com.promlert.mytodo.db.ToDo;
import com.promlert.mytodo.net.ApiClient;
import com.promlert.mytodo.net.GetToDoResponse;
import com.promlert.mytodo.net.MyRetrofitCallback;
import com.promlert.mytodo.net.WebServices;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

public class ToDoListFragment extends Fragment {

    private static final String TAG = ToDoListFragment.class.getName();

    private RecyclerView mToDoRecyclerView;
    private ProgressBar mProgressBar;

    private List<ToDo> mToDoList = null;
    private ToDoListFragmentCallback mCallback;

    public ToDoListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_to_do_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mToDoRecyclerView = view.findViewById(R.id.todo_recycler_view);
        mToDoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mToDoRecyclerView.addItemDecoration(new ToDoListAdapter.SpacingDecoration(getActivity()));

        mProgressBar = view.findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        if (mToDoList == null) {
            reloadData();
        } else {
            bindAdapter();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setFabVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setFabVisibility(View.GONE);
        }
    }

    public void reloadData() {
        mProgressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = ApiClient.getClient();
        WebServices services = retrofit.create(WebServices.class);
        Call<GetToDoResponse> call = services.getAllTodo();
        call.enqueue(new MyRetrofitCallback<>(
                getActivity(),
                null,
                mProgressBar,
                new MyRetrofitCallback.MyRetrofitCallbackListener<GetToDoResponse>() {
                    @Override
                    public void onSuccess(GetToDoResponse responseBody) {
                        mToDoList = responseBody.data;
                        bindAdapter();
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

    private void bindAdapter() {
        ToDoListAdapter adapter = new ToDoListAdapter(
                getActivity(),  // context
                mToDoList,      // data source
                mCallback       // callback กรณีคลิก item หรือคลิกที่ checkbox (handle ใน MainActivity)
        );
        mToDoRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ToDoListFragmentCallback) {
            mCallback = (ToDoListFragmentCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ToDoListFragmentCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    public interface ToDoListFragmentCallback {
        void onClickItem(ToDo toDo);
        void onFinishedChange(ToDo toDo);
    }
}
