package com.promlert.mytodo.db;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class ToDoRepository {

    private static final String TAG = ToDoRepository.class.getName();

    private Context mContext;

    public ToDoRepository(Context context) {
        this.mContext = context;
    }

    public void addToDo(String title, String details) {
        AddToDoTask addTask = new AddToDoTask(mContext);
        ToDo todo = new ToDo();
        todo.setTitle(title);
        todo.setDetails(details);
        todo.setFinished(false);
        addTask.execute(todo);
    }

    public void getAllToDo(Callback callback) {
        GetToDoTask getTask = new GetToDoTask(mContext, callback);
        getTask.execute();
    }

    public void updateToDo(int id, String title, String details, boolean finished) {
        UpdateToDoTask updateTask = new UpdateToDoTask(mContext);
        ToDo todo = new ToDo();
        todo.setId(id);
        todo.setTitle(title);
        todo.setDetails(details);
        todo.setFinished(finished);
        updateTask.execute(todo);
    }

    public void deleteToDo(int id) {
        DeleteToDoTask deleteTask = new DeleteToDoTask(mContext);
        ToDo todo = new ToDo();
        todo.setId(id);
        deleteTask.execute(todo);
    }

    private static class AddToDoTask extends AsyncTask<ToDo, Void, Void> {

        private Context mContext;

        AddToDoTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected Void doInBackground(ToDo... todos) {
            AppDatabase db = AppDatabase.getInstance(mContext);

            for (ToDo todo : todos) {
                db.toDoDao().insert(todo);
            }
            return null;
        } // ปิดเมธอด doInBackground
    } // ปิดคลาส AddToDoTask

    private static class UpdateToDoTask extends AsyncTask<ToDo, Void, Void> {

        private Context mContext;

        UpdateToDoTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected Void doInBackground(ToDo... todos) {
            AppDatabase db = AppDatabase.getInstance(mContext);

            for (ToDo todo : todos) {
                db.toDoDao().update(todo);
            }
            return null;
        } // ปิดเมธอด doInBackground
    } // ปิดคลาส UpdateToDoTask

    private static class DeleteToDoTask extends AsyncTask<ToDo, Void, Void> {

        private Context mContext;

        DeleteToDoTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected Void doInBackground(ToDo... todos) {
            AppDatabase db = AppDatabase.getInstance(mContext);

            for (ToDo todo : todos) {
                db.toDoDao().delete(todo);
            }
            return null;
        } // ปิดเมธอด doInBackground
    } // ปิดคลาส DeleteToDoTask

    private static class GetToDoTask extends AsyncTask<Void, Void, List<ToDo>> {

        private Context mContext;
        private Callback mCallback;

        GetToDoTask(Context context, Callback callback) {
            this.mContext = context;
            this.mCallback = callback;
        }

        @Override
        protected List<ToDo> doInBackground(Void... value) {
            AppDatabase db = AppDatabase.getInstance(mContext);
            return db.toDoDao().getAll();
        } // ปิดเมธอด doInBackground

        @Override
        protected void onPostExecute(List<ToDo> toDos) {
            super.onPostExecute(toDos);
            mCallback.onGetTodo(toDos);
        } // ปิดเมธอด onPostExecute

    } // ปิดคลาส GetToDoTask

    public interface Callback {
        void onGetTodo(List<ToDo> todoList);
    }
}
