package org.example.libdev.subject.repository;

import org.example.libdev.subject.entity.Subject;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubjectRepository extends CrudRepository<Subject,Long> {
    // 이름으로 Category 존재 여부 확인
    @Query("select count(*)>0 from Subject s where s.name =:name")
    boolean existsByName(@Param("name") String name);

    List<Subject> findByName(String name);
}
