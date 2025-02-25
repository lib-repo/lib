package org.example.libdev.subject.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.libdev.global.entity.BaseEntity;
import org.example.libdev.subject.dto.SubjectDto;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Subject extends BaseEntity {

    @Id
    @Column(name="subject_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public void update(String name) {
        this.name = name;
    }

    public Subject(String name) {
        this.name = name;
    }

    public SubjectDto toDto() {

        return SubjectDto.builder()
                .id(this.id)
                .name(this.name)
                .build();
    }
}
