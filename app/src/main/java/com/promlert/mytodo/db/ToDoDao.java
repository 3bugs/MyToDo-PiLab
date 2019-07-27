package com.promlert.mytodo.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ToDoDao {

    @Query("SELECT * FROM todo")
    List<ToDo> getAll();

    @Query("SELECT * FROM todo WHERE id = :id")
    List<ToDo> getById(int id);

    @Insert
    void insert(ToDo todo);

    @Update
    void update(ToDo todo);

    @Delete
    void delete(ToDo todo);

}
