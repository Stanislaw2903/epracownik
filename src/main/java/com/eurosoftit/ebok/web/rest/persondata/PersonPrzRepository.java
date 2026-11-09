package com.eurosoftit.ebok.web.rest.persondata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonPrzRepository extends JpaRepository<PersonPrz, Long>, JpaSpecificationExecutor<PersonPrz> {
    @Query("select new com.eurosoftit.ebok.web.rest.persondata.MedicalExaminationDTO(pp.dateFrom, pp.dateTo, pp.kind, pp.sanitaryTests, pp.comments) from PersonPrz pp where pp.type='L' and pp.externalId = :externalId")
    List<MedicalExaminationDTO> findMedicalExaminationById(@Param("externalId") Long externalId);

    @Query("select new com.eurosoftit.ebok.web.rest.persondata.MedicalExaminationDTO(pp.dateFrom, pp.dateTo, pp.kind, pp.sanitaryTests, pp.comments) from PersonPrz pp where pp.type='L' and pp.externalId = :externalId")
    Page<MedicalExaminationDTO> findMedicalExaminationById(@Param("externalId") Long externalId, Pageable pageable);

    @Query("select new com.eurosoftit.ebok.web.rest.persondata.SafetyTrainingDTO(pp.dateFrom, pp.dateTo, pp.kind, pp.comments) from PersonPrz pp where pp.type='H' and pp.externalId = :externalId")
    List<SafetyTrainingDTO> findSafetyTrainingById(@Param("externalId") Long externalId);

    @Query("select new com.eurosoftit.ebok.web.rest.persondata.SafetyTrainingDTO(pp.dateFrom, pp.dateTo, pp.kind, pp.comments) from PersonPrz pp where pp.type='H' and pp.externalId = :externalId")
    Page<SafetyTrainingDTO> findSafetyTrainingById(@Param("externalId") Long externalId, Pageable pageable);
}
