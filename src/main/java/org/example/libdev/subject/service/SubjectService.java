package org.example.libdev.subject.service;

import lombok.RequiredArgsConstructor;
import org.example.libdev.subject.dto.SubjectDto;
import org.example.libdev.subject.entity.Subject;
import org.example.libdev.subject.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class SubjectService {
    private final SubjectRepository subjectRepository;

    public List<SubjectDto> findAll() {
        List<Subject> subjects = new ArrayList<>();
        subjectRepository.findAll().forEach(subjects::add);
        List<SubjectDto> subjectDtos = subjects.stream()
                .map(Subject::toDto).toList();
        return subjectDtos;
    }


    public SubjectDto findById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Subject not found with id: " + id));
        return subject.toDto();
    }

    public SubjectDto save(String name) {
        // 이스케이프 처리
        String escapedName = StringEscapeUtils.escapeHtml4(name);

        validate(escapedName);

        Subject subject = new Subject(escapedName);
        return subjectRepository.save(subject).toDto();

    }

    private void validate(String escapedName) {
        if (escapedName == null || escapedName.trim().isEmpty()) {
            throw new IllegalArgumentException("주제는 공백일 수 없습니다.");
        }

        // 이미 이름이 있다면 중복예외 발생
        if(subjectRepository.existsByName(escapedName)){
            throw new IllegalArgumentException(escapedName + "은 이미 존재하는 주제입니다");
        }
    }


}
