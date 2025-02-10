package org.example.libdev.subject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDto {
    private Long id;

    @NotBlank
    @Size(max = 20, message = "이름은 1자 이상 20자 이하로 입력해주세요.")
    private String name;
}
