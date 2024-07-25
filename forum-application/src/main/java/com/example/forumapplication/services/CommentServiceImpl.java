package com.example.forumapplication.services;

import com.example.forumapplication.exceptions.AuthorizationException;
import com.example.forumapplication.exceptions.EntityNotFoundException;
import com.example.forumapplication.models.Comment;
import com.example.forumapplication.models.Post;
import com.example.forumapplication.models.User;
import com.example.forumapplication.repositories.CommentRepository;
import com.example.forumapplication.repositories.PostRepository;
import com.example.forumapplication.repositories.UserRepository;
import com.example.forumapplication.services.contracts.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CommentServiceImpl implements CommentService {

    private static final String DELETE_COMMENT_ERROR_MESSAGE = "Only admin or comment creator can delete a comment.";

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Override
    public Comment getCommentById(int id) {
        return commentRepository.findCommentById(id);
    }

    @Override
    public List<Comment> getComments() {
        return commentRepository.findAll();
    }

    @Override
    public Post addComment(int postId, Comment comment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName());
        Post post = postRepository.getById(postId);
        comment.setCreatedBy(user);
        post.getComments().add(comment);
        return postRepository.save(post);
    }

    @Override
    public Comment updateComment(int commentId, Comment updatedComment) {
        Comment existingComment = getCommentById(commentId);
        existingComment.setContent(updatedComment.getContent());
        return commentRepository.save(existingComment);
    }

    @Override
    public void deleteComment(int commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment", commentId));
        checkDeleteCommentPermissions(comment, user);
        commentRepository.delete(comment);
    }

    private void checkDeleteCommentPermissions(Comment comment, User user) {
        if (!user.getRole_id().getName().equals("Admin") && !comment.getCreatedBy().equals(user)) {
            throw new AuthorizationException(DELETE_COMMENT_ERROR_MESSAGE);
        }
    }
}
