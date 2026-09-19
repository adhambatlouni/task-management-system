package com.adham.taskmanagement.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByTask_IdOrderByIdDesc(Long taskId);

    @Query("""
            SELECT c.task.id, COUNT(c.id)
            FROM Comment c
            WHERE c.task.id IN :taskIds
            GROUP BY c.task.id
            """)
    List<Object[]> countCommentsByTaskIds(
            @Param("taskIds") Collection<Long> taskIds
    );
}
