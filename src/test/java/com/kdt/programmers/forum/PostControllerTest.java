package com.kdt.programmers.forum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdt.programmers.forum.transfer.PostDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostJpaRepository postJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void clean() {
        postJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글을 저장할 수 있다")
    void savePost() throws Exception {
        // Given
        PostDto postDto = new PostDto("test title", "");

        // When Then
        mockMvc
            .perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
            .andExpect(status().isCreated())
            .andDo(print());
    }


    @Test
    @DisplayName("게시글을 ID로 조회할 수 있다")
    void testGetPost() throws Exception {
        // Given
        PostDto dto = new PostDto("test title", "");
        PostDto post = postService.savePost(dto);

        // When Then
        mockMvc
            .perform(get("/api/v1/posts/{postId}", post.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print());
    }

    @Test
    @DisplayName("게시글을 페이지로 조회할 수 있다")
    void testGetPosts() throws Exception {
        // Given
        postService.savePost(new PostDto("test title", ""));
        postService.savePost(new PostDto("test title", ""));
        postService.savePost(new PostDto("test title", ""));
        postService.savePost(new PostDto("test title", ""));

        // When Then
        final String SIZE = "3";
        final String PAGE = "0";
        mockMvc
            .perform(get("/api/v1/posts")
                .param("size", SIZE)
                .param("page", PAGE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content", hasSize(3)))
            .andDo(print());

        // When Then
        final String NEXT_PAGE = "1";
        mockMvc
            .perform(get("/api/v1/posts")
                .param("size", SIZE)
                .param("page", NEXT_PAGE))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content", hasSize(1)))
            .andDo(print());
    }

    @Test
    @DisplayName("게시글을 수정할 수 있다")
    void testUpdatePost() throws Exception {
        // Given
        PostDto post = postService.savePost(new PostDto("test post", ""));
        PostDto updateDto = new PostDto("updated post", "");

        // When Then
        mockMvc
            .perform(patch("/api/v1/posts/{postId}", post.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andDo(print());
    }
}