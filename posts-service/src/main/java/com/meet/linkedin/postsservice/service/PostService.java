package com.meet.linkedin.postsservice.service;

import com.meet.linkedin.postsservice.auth.UserContextHolder;
import com.meet.linkedin.postsservice.clients.ConnectionsClient;
import com.meet.linkedin.postsservice.dto.PostCreateRequestDto;
import com.meet.linkedin.postsservice.dto.PostDto;
import com.meet.linkedin.postsservice.entity.Post;
import com.meet.linkedin.postsservice.events.PostCreatedEvent;
import com.meet.linkedin.postsservice.exception.ResourceNotFoundException;
import com.meet.linkedin.postsservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final ConnectionsClient connectionsClient;

    private final KafkaTemplate<Long, PostCreatedEvent> kafkaTemplate;

    public PostDto createPost(PostCreateRequestDto postDto) {
        Long userId = UserContextHolder.getCurrentUserId();
        Post post = modelMapper.map(postDto, Post.class);
        post.setUserId(userId);

        Post savedPost = postRepository.save(post);

        PostCreatedEvent postCreatedEvent = PostCreatedEvent.builder()
                .postId(savedPost.getId())
                .creatorId(userId)
                .content(savedPost.getContent())
                .build();

        kafkaTemplate.send("post-created-topic", postCreatedEvent);

        return modelMapper.map(savedPost, PostDto.class);
    }

    public PostDto getPostById(Long postId) {
        log.debug("Getting post by id: {}", postId);

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post not found with id: " + postId)
        );
        return modelMapper.map(post, PostDto.class);
    }

    public List<PostDto> getAllPostOfUser(Long userId) {
        log.info("Getting all posts of user with id: {}", userId);
        List<Post> posts = postRepository.findAllPostsByUserId(userId);
        return posts.stream().map(post -> modelMapper.map(post, PostDto.class)).toList();
    }
}
