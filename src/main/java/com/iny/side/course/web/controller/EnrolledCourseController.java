package com.iny.side.course.web.controller;

import com.iny.side.common.BasicResponse;
import com.iny.side.common.SliceResponse;
import com.iny.side.course.application.service.CourseService;
import com.iny.side.course.web.dto.EnrolledCoursesDto;
import com.iny.side.users.web.dto.AccountResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EnrolledCourseController {

    private final CourseService courseService;

    @GetMapping(value = "/student/courses")
    public ResponseEntity<BasicResponse<SliceResponse<EnrolledCoursesDto>>> get(
            @AuthenticationPrincipal AccountResponseDto accountResponseDto,
            @RequestParam(value = "semester") String semester,
            @RequestParam(value = "page", defaultValue = "0") int page) {
        return ResponseEntity.ok(BasicResponse.ok(courseService.getAllEnrolled(accountResponseDto.id(), semester, page)));
    }
}
