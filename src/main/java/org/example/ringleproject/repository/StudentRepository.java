package org.example.ringleproject.repository;

import org.example.ringleproject.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
