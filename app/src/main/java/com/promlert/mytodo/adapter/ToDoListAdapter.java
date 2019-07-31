package com.promlert.mytodo.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.promlert.mytodo.R;
import com.promlert.mytodo.db.ToDo;
import com.promlert.mytodo.fragment.ToDoListFragment;

import java.util.List;

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.MyViewHolder> {

    private Context mContext;
    private List<ToDo> mToDoList;
    private ToDoListFragment.ToDoListFragmentCallback mCallback;

    public ToDoListAdapter(Context context, List<ToDo> toDoList,
                           ToDoListFragment.ToDoListFragmentCallback callback) {
        this.mContext = context;
        this.mToDoList = toDoList;
        this.mCallback = callback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_todo, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ToDo todo = mToDoList.get(position);
        holder.titleTextView.setText(todo.getTitle());
        holder.detailsTextView.setText(todo.getDetails());

        holder.finishedCheckBox.setOnCheckedChangeListener(null);
        holder.finishedCheckBox.setChecked(todo.isFinished());
        holder.finishedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mCallback != null) {
                    todo.setFinished(isChecked);
                    mCallback.onFinishedChange(todo);
                }
            }
        });
        holder.toDo = todo;
    }

    @Override
    public int getItemCount() {
        return mToDoList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView, detailsTextView;
        private CheckBox finishedCheckBox;
        private ToDo toDo;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.title_text_view);
            detailsTextView = itemView.findViewById(R.id.details_text_view);
            finishedCheckBox = itemView.findViewById(R.id.finished_check_box);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback != null) {
                        mCallback.onClickItem(toDo);
                    }
                }
            });
        }
    }

    public static class SpacingDecoration extends RecyclerView.ItemDecoration {

        private final static int MARGIN_IN_DP = 88;
        private final int mMarginBottom;

        public SpacingDecoration(@NonNull Context context) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            mMarginBottom = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    MARGIN_IN_DP,
                    metrics
            );
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            final int itemPosition = parent.getChildAdapterPosition(view);
            if (itemPosition == RecyclerView.NO_POSITION) {
                return;
            }
            /*if (itemPosition == 0) {
                outRect.top = mMarginBottom;
            }*/
            final RecyclerView.Adapter adapter = parent.getAdapter();
            if ((adapter != null) && (itemPosition == adapter.getItemCount() - 1)) {
                outRect.bottom = mMarginBottom;
            }
        }
    }
}
