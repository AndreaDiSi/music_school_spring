package com.scuoladimusica.controller;

import com.scuoladimusica.model.dto.request.TeacherRequest;
import com.scuoladimusica.model.dto.response.MessageResponse;
import com.scuoladimusica.model.dto.response.TeacherResponse;
import com.scuoladimusica.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private TeacherService teacherService;

    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeacherResponse> createTeacher(@Valid @RequestBody TeacherRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.createTeacher(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TeacherResponse>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    @GetMapping("/{matricola}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<TeacherResponse> getTeacher(@PathVariable String matricola) {
        return ResponseEntity.ok(teacherService.getTeacherByMatricola(matricola));
    }

    @PutMapping("/{matricola}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeacherResponse> updateTeacher(
            @PathVariable String matricola,
            @Valid @RequestBody TeacherRequest request) {
        return ResponseEntity.ok(teacherService.updateTeacher(matricola, request));
    }

    @DeleteMapping("/{matricola}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTeacher(@PathVariable String matricola) {
        teacherService.deleteTeacher(matricola);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{matricola}/courses/{codiceCorso}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> assignCourse(
            @PathVariable String matricola,
            @PathVariable String codiceCorso) {
        teacherService.assignCourse(matricola, codiceCorso);
        return ResponseEntity.ok(new MessageResponse("Corso assegnato con successo"));
    }
}
