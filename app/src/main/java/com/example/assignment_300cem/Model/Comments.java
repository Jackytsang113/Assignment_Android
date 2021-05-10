package com.example.assignment_300cem.Model;

public class Comments {
    private String commentId, comment, sender;

    public Comments() {}

    public Comments(String commentId, String comment, String sender) {
        this.commentId = commentId;
        this.comment = comment;
        this.sender = sender;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
