package com.adham.taskmanagement.comment;

public interface CommentCountProjection {

    Long getTaskId();

    Long getTotalComments();
}
