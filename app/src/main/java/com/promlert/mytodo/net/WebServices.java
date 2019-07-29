package com.promlert.mytodo.net;

import com.promlert.mytodo.db.ToDo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface WebServices {

    @GET("get_todo")
    Call<GetToDoResponse> getAllTodo();

    @POST("add_todo")
    Call<AddToDoResponse> addTodo(
            @Body ToDo toDo
            /*@Field("title") String title,
            @Field("details") String details*/
    );

    @POST("update_todo")
    Call<UpdateToDoResponse> updateTodo(
            @Body ToDo toDo
            /*@Field("id") int id,
            @Field("title") String title,
            @Field("details") String details,
            @Field("finished") boolean finished*/
    );

    @POST("delete_todo")
    Call<DeleteToDoResponse> deleteTodo(
            @Body ToDo toDo
    );
}
