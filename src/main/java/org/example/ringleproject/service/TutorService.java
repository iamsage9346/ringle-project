package org.example.ringleproject.service;

import lombok.RequiredArgsConstructor;
import org.example.ringleproject.repository.TutorRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TutorService {

    private final TutorRepository tutorRepository;



}
