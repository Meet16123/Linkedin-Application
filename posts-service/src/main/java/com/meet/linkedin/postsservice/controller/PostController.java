package com.meet.linkedin.postsservice.controller;

import com.meet.linkedin.postsservice.dto.PostCreateRequestDto;
import com.meet.linkedin.postsservice.dto.PostDto;
import com.meet.linkedin.postsservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/core")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto> cratePost(@RequestBody PostCreateRequestDto postCreateRequestDto) {
        PostDto createdPost = postService.createPost(postCreateRequestDto);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long postId) {
//        String userId = httpServletRequest.getHeader("X-User-Id");
        PostDto post = postService.getPostById(postId);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/allPosts")
    public ResponseEntity<List<PostDto>> getAllPostsByUserId(@PathVariable Long userId) {
        List<PostDto> posts = postService.getAllPostOfUser(userId);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
}
