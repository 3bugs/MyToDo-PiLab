package com.promlert.mytodo.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.promlert.mytodo.etc.DateFormatConverter;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "todo")
public class ToDo implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    private int id;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    private String title;

    @ColumnInfo(name = "details")
    @SerializedName("details")
    private String details;

    @ColumnInfo(name = "finished")
    @SerializedName("finished")
    private boolean finished;

    @ColumnInfo(name = "due_date")
    @TypeConverters({DateFormatConverter.class})
    @SerializedName("due_date")
    private Date dueDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
