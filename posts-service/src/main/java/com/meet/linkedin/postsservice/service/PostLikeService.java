package com.meet.linkedin.postsservice.service;

import com.meet.linkedin.postsservice.auth.UserContextHolder;
import com.meet.linkedin.postsservice.entity.Post;
import com.meet.linkedin.postsservice.entity.PostLike;
import com.meet.linkedin.postsservice.events.PostLikedEvent;
import com.meet.linkedin.postsservice.exception.BadRequestException;
import com.meet.linkedin.postsservice.exception.ResourceNotFoundException;
import com.meet.linkedin.postsservice.repository.PostLikeRepository;
import com.meet.linkedin.postsservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final KafkaTemplate<Long, PostLikedEvent> kafkaTemplate;

    public void likePost(Long postId) {
        Long userId = UserContextHolder.getCurrentUserId();

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post with id: " + postId + " does not exist")
        );

        boolean alreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        if (alreadyLiked) {
            throw new BadRequestException("Post with id: " + postId + " already liked by user with id: " + userId);
        }

        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLikeRepository.save(postLike);
        log.info("Post with id: {} liked by user with id: {}", postId, userId);

        PostLikedEvent postLikedEvent = PostLikedEvent.builder()
                .postId(postId)
                .likedByUserId(userId)
                .creatorId(post.getUserId())
                .build();

        kafkaTemplate.send("post-liked-topic", postId, postLikedEvent);
    }

    public void unlikePost(Long postId) {
        Long userId = UserContextHolder.getCurrentUserId();
        boolean postExists = postRepository.existsById(postId);
        log.info("Attempting to Unlike post with id: {}", postId);
        if (!postExists) {
            throw new ResourceNotFoundException("Post with id: " + postId + " does not exist");
        }

        boolean alreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        if (!alreadyLiked) {
            throw new BadRequestException("Cannot unlike the post which is not liked");
        }
        postLikeRepository.deleteByUserIdAndPostId(userId, postId);
        log.info("Post with id: {} unliked by user with id: {}", postId, userId);

    }
}
