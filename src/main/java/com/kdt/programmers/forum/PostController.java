package com.kdt.programmers.forum;

import com.kdt.programmers.forum.exception.NotFoundException;
import com.kdt.programmers.forum.exception.PostNotFoundException;
import com.kdt.programmers.forum.transfer.SimplePage;
import com.kdt.programmers.forum.transfer.request.PostRequest;
import com.kdt.programmers.forum.transfer.response.ApiResponse;
import com.kdt.programmers.forum.transfer.PostDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ApiResponse<SimplePage> getPosts(Pageable pageable) {
        Page<PostDto> posts = postService.findPostsByPage(pageable);
        SimplePage dto = new SimplePage(posts.getContent(), posts.getTotalPages(), posts.getTotalElements());
        return ApiResponse.response(dto);
    }

    @GetMapping("/{id}")
    public ApiResponse<PostDto> getPost(@PathVariable Long id) {
        try {
            PostDto post = postService.findPostById(id);
            return ApiResponse.response(post);
        } catch (PostNotFoundException e) {
            throw new NotFoundException("requested post was not found", e);
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ApiResponse<PostDto> savePost(@RequestBody final PostRequest request) {
        PostDto post = postService.savePost(request);
        return ApiResponse.response(post);
    }

    @PutMapping("/{id}")
    public ApiResponse<PostDto> updatePost(
        @PathVariable Long id,
        @RequestBody final PostRequest request
    ) {
        PostDto post = postService.updatePost(id, request);
        return ApiResponse.response(post);
    }
}
