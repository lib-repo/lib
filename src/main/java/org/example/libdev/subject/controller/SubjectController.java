package org.example.libdev.subject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.libdev.subject.dto.SubjectDto;
import org.example.libdev.subject.service.SubjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/subject")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    //사용자 :admin
    @GetMapping ("all")
    public ResponseEntity<List<SubjectDto>> findAll(){
        List<SubjectDto> SubjectDtos = subjectService.findAll();

        return new ResponseEntity<>(SubjectDtos, HttpStatus.OK);
    }

    @GetMapping ("{subjectId}")
    public ResponseEntity<SubjectDto> findById(@PathVariable Long subjectId){
        SubjectDto subjectDto = subjectService.findById(subjectId); //매개변수를 안 넣어서 오류 경험
        return new ResponseEntity<>(subjectDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SubjectDto> create(@RequestBody @Valid SubjectDto subjectDto){
        SubjectDto subjectDtoResponse = subjectService.save(subjectDto.getName());
        return new ResponseEntity<>(subjectDtoResponse, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<SubjectDto> update(@RequestBody @Valid SubjectDto subjectDto){
        SubjectDto subjectDtoResponse = subjectService.update(subjectDto.getName(), subjectDto.getId());
        return new ResponseEntity<>(subjectDtoResponse, HttpStatus.OK);
    }

    @DeleteMapping("{subjectId}")
    public void delete(@PathVariable Long subjectId){
        subjectService.delete(subjectId);
    }



}
