package com.GDG.worktree.team2.gardening_diary.controller;

import com.GDG.worktree.team2.gardening_diary.entity.User;
import com.GDG.worktree.team2.gardening_diary.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;
import java.util.List;

@Tag(name = "사용자 관리", description = "사용자 CRUD API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userservice;
    
    @Operation(
        summary = "사용자 생성",
        description = "새로운 사용자를 생성합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "사용자 생성 실패")
    })
    @PostMapping
    public ResponseEntity<String> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "사용자 정보",
                required = true,
                content = @Content(schema = @Schema(implementation = User.class))
            )
            @org.springframework.web.bind.annotation.RequestBody User user) throws ExecutionException, InterruptedException {
        userservice.createUser(user);
        return ResponseEntity.ok("User created successfully");
    }

    @Operation(
        summary = "사용자 조회 (ID로)",
        description = "사용자 ID로 사용자 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(
            @Parameter(description = "사용자 ID", required = true, example = "user123")
            @PathVariable String id) throws ExecutionException, InterruptedException {
        User user = userservice.getUser(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @Operation(
        summary = "전체 사용자 조회",
        description = "모든 사용자 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() throws ExecutionException, InterruptedException {
        List<User> users = userservice.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(
        summary = "사용자 수정",
        description = "사용자 정보를 수정합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "사용자 수정 실패")
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @Parameter(description = "사용자 ID", required = true, example = "user123")
            @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "수정할 사용자 정보",
                required = true,
                content = @Content(schema = @Schema(implementation = User.class))
            )
            @org.springframework.web.bind.annotation.RequestBody User user) throws ExecutionException, InterruptedException {
        user.setId(id);
        String time = userservice.updateUser(user);
        return ResponseEntity.ok("User updated successfully at " + time);
    }

    @Operation(
        summary = "사용자 삭제",
        description = "사용자를 삭제합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "사용자 삭제 실패")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "사용자 ID", required = true, example = "user123")
            @PathVariable String id) {
        String result = userservice.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

}
