package org.example.libdev.subject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.libdev.subject.dto.SubjectDto;
import org.example.libdev.subject.service.SubjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/api/subject")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    //사용자 :admin
    @GetMapping("/all")
    public String findAll(Model model) {
        log.info("🔍 [START] GET /api/subject/all 요청 수신");

        // 과목 목록 조회
        List<SubjectDto> subjectDtos = subjectService.findAll();
        log.info("📌 조회된 과목 개수: {}", subjectDtos.size());

        model.addAttribute("subjects", subjectDtos);

        log.info("✅ [END] 데이터 모델에 추가 완료. 페이지 반환: subjectAdmin");
        return "subject/subjectAdmin";
    }

    @GetMapping ("{subjectName}")
    public ResponseEntity<SubjectDto> findByName(@PathVariable String subjectName){
        SubjectDto subjectDto = subjectService.findByName(subjectName); //매개변수를 안 넣어서 오류 경험
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
    public ResponseEntity<?> delete(@PathVariable Long subjectId) {
        try {
            subjectService.delete(subjectId);
            return ResponseEntity.ok().build(); // 정상 응답 반환
        } catch (Exception e) {
            e.printStackTrace(); // 콘솔에 에러 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("삭제 중 오류 발생: " + e.getMessage());
        }
    }



}
