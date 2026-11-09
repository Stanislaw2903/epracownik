package com.eurosoftit.ebok.web.rest.absence;

import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.domain.specification.SpecificationReflectionBuilder;
import com.eurosoftit.ebok.service.UserService;
import com.eurosoftit.ebok.web.rest.UserUtils;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Api
@RestController
@RequestMapping("/api/absences")
@RequiredArgsConstructor
class AbsencesResource {

    private final AbsenceRepository absenceRepository;
    private final UserUtils userUtils;
    private final UserService userService;
    private final AbsencesMapper absencesMapper;

    @GetMapping("/dictionary/relatedUsers")
    List<AbsenceUserDTO> relatedUsers() {
        final List<Long> externalIds = prepareUserExternalIdsFilter();
        return userService.findByExternalIds(externalIds).stream().map(absencesMapper::map).collect(Collectors.toList());
    }

    @GetMapping
    List<AbsenceDTO> absences(AbsenceParams params) {
        params.setExternalIds(calculateUserExternalIds(params.getExternalIds()));
        Specification<Absence> specification = SpecificationReflectionBuilder.process(params);
        return absenceRepository.findAll(specification).stream().map(this::map).collect(Collectors.toList());
    }

    private List<Long> prepareUserExternalIdsFilter() {
        List<Long> externalIds = new ArrayList<>();
        externalIds.add(userUtils.getExternalUserId());

        Optional.ofNullable(userUtils.getLoggedUserRelatedUserAndExtract(User::getExternalId))
            .filter(collection -> collection != null && !collection.isEmpty())
            .ifPresent(collection -> externalIds.addAll(collection));
        return externalIds;
    }

    @GetMapping(params = "page")
    Page<AbsenceDTO> absencesPageable(AbsenceParams params, Pageable pageable) {
        params.setExternalIds(calculateUserExternalIds(params.getExternalIds()));
        Specification<Absence> specification = SpecificationReflectionBuilder.process(params);
        specification = specification.and(SpecificationReflectionBuilder.distinct());
        return absenceRepository.findAll(specification, pageable).map(this::map);
    }

    private List<Long> calculateUserExternalIds(List<Long> externalIds) {
        List<Long> availableExternalIds = prepareUserExternalIdsFilter();
        if (externalIds != null && !externalIds.isEmpty()) {
            List<Long> filteredIds =  externalIds.stream().filter(id -> availableExternalIds.contains(id)).collect(Collectors.toList());

            if(filteredIds.isEmpty()) {
                return Arrays.asList(-1l);
            }

            return filteredIds;
        }

        return availableExternalIds;
    }

    private AbsenceDTO map(Absence absence) {
        AbsenceDTO absenceDto = new AbsenceDTO();
        absenceDto.setOspId(absence.getOspId());
        absenceDto.setCode(absence.getCode());
        absenceDto.setDateFrom(absence.getDateFrom());
        absenceDto.setDateTo(absence.getDateTo());
        absenceDto.setId(absence.getId());
        absenceDto.setFirstName(absence.getFirstName());
        absenceDto.setLastName(absence.getLastName());
        return absenceDto;
    }
}
