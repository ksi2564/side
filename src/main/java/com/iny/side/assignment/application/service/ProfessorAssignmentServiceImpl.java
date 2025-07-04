package com.iny.side.assignment.application.service;

import com.iny.side.assignment.domain.entity.Assignment;
import com.iny.side.assignment.domain.repository.AssignmentRepository;
import com.iny.side.assignment.domain.vo.AssignmentInfo;
import com.iny.side.assignment.web.dto.AssignmentCreateDto;
import com.iny.side.assignment.web.dto.AssignmentSimpleResponseDto;
import com.iny.side.assignment.web.dto.ProfessorAssignmentDetailResponseDto;
import com.iny.side.common.exception.ForbiddenException;
import com.iny.side.common.exception.NotFoundException;
import com.iny.side.course.application.service.EnrollmentValidationService;
import com.iny.side.course.domain.entity.Course;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Builder
@RequiredArgsConstructor
public class ProfessorAssignmentServiceImpl implements ProfessorAssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final EnrollmentValidationService enrollmentValidationService;

    @Override
    public List<AssignmentSimpleResponseDto> getAll(Long courseId, Long professorId) {
        enrollmentValidationService.validateProfessorOwnsCourse(courseId, professorId);
        return assignmentRepository.findAllByCourseId(courseId).stream()
                .map(AssignmentSimpleResponseDto::from)
                .toList();
    }

    @Override
    @Transactional
    public AssignmentSimpleResponseDto create(Long courseId, Long professorId, AssignmentCreateDto dto) {
        Course course = enrollmentValidationService.validateProfessorOwnsCourse(courseId, professorId);
        Assignment assignment = Assignment.create(course, AssignmentInfo.from(dto));
        return AssignmentSimpleResponseDto.from(assignmentRepository.save(assignment));
    }

    @Override
    public ProfessorAssignmentDetailResponseDto get(Long courseId, Long professorId, Long assignmentId) {
        enrollmentValidationService.validateProfessorOwnsCourse(courseId, professorId);
        Assignment assignment = assignmentRepository.findByAssignmentId(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment"));
        validateAssignmentBelongsToCourse(courseId, assignment);
        return ProfessorAssignmentDetailResponseDto.from(assignment);
    }


    @Override
    @Transactional
    public void delete(Long courseId, Long professorId, Long assignmentId) {
        enrollmentValidationService.validateProfessorOwnsCourse(courseId, professorId);
        Assignment assignment = assignmentRepository.findByAssignmentId(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment"));
        validateAssignmentBelongsToCourse(courseId, assignment);
        assignmentRepository.delete(assignment);
    }

    private static void validateAssignmentBelongsToCourse(Long courseId, Assignment assignment) {
        if (!assignment.getCourse().getId().equals(courseId)) {
            throw new ForbiddenException("forbidden.assignment_not_in_course");
        }
    }


}
