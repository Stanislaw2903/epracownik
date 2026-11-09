package com.eurosoftit.ebok.web.rest.persondata;

import com.eurosoftit.ebok.web.rest.UserUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api
@RestController
@RequestMapping("/api")
class PersonDataResource {

    @Autowired
    private PersonPrzRepository personPrzRepository;
    @Autowired
    private DataHiDaRepository dataHiDaRepository;
    @Autowired
    private UserUtils userUtils;

    @GetMapping("/medical-examination")
    List<MedicalExaminationDTO> medicalExamination() {
        return personPrzRepository.findMedicalExaminationById(userUtils.getExternalUserId());
    }

    @GetMapping(value = "/medical-examination", params = "page")
    Page<MedicalExaminationDTO> medicalExamination(Pageable pageable) {
        return personPrzRepository.findMedicalExaminationById(userUtils.getExternalUserId(), pageable);
    }

    @GetMapping("/safety-training")
    List<SafetyTrainingDTO> safetyTraining() {
        return personPrzRepository.findSafetyTrainingById(userUtils.getExternalUserId());
    }

    @GetMapping(value = "/safety-training", params = "page")
    Page<SafetyTrainingDTO> safetyTraining(Pageable pageable) {
        return personPrzRepository.findSafetyTrainingById(userUtils.getExternalUserId(), pageable);
    }

    @GetMapping("/courses")
    List<CoursesDTO> courses() {
        return dataHiDaRepository.findCoursesById(userUtils.getExternalUserId());
    }

    @GetMapping(value = "/courses", params = "page")
    Page<CoursesDTO> courses(Pageable pageable) {
        return dataHiDaRepository.findCoursesById(userUtils.getExternalUserId(), pageable);
    }

    @GetMapping("/privileges")
    List<PrivilegesDTO> privileges() {
        return dataHiDaRepository.findPrivilegesById(userUtils.getExternalUserId());
    }

    @GetMapping(value = "/privileges", params = "page")
    Page<PrivilegesDTO> privileges(Pageable pageable) {
        return dataHiDaRepository.findPrivilegesById(userUtils.getExternalUserId(), pageable);
    }
}
