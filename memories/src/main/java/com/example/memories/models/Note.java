package com.example.memories.models;

import android.content.Context;

import com.example.memories.SQLitedatabase.NoteDataSource;

import java.util.List;

public class Note extends Memories {

    private String content;
    private String caption;

    public Note() {

    }

    public Note(String idOnServer, String jId, String memType, String caption, String content,
                String createdBy, long createdAt, long updatedAt, List<String> likedBy) {
        this.idOnServer = idOnServer;
        this.jId = jId;
        this.memType = memType;
        this.caption = caption;
        this.content = content;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.likedBy = likedBy;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void updateLikedBy(Context context, String memId, List<String> likedBy) {
        NoteDataSource.updateFavourites(context, memId, likedBy);
    }

}
