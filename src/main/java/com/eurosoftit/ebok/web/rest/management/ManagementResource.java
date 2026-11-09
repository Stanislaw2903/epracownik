package com.eurosoftit.ebok.web.rest.management;

import com.eurosoftit.ebok.web.rest.UserUtils;
import com.eurosoftit.ebok.web.rest.vacation.Vacation;
import com.eurosoftit.ebok.web.rest.vacation.VacationRepository;
import com.eurosoftit.ebok.web.rest.vacation.VacationStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/management")
public class ManagementResource {

    @Autowired
    private ManagementRepository repository;

    @Autowired
    private VacationRepository vacationRepository;

    @Autowired
    private UserUtils userUtils;

    @GetMapping("/active-place-vacation-proposal")
    Boolean getActivePlaceVacationProposal(){
        return repository.findValueByType(ManagementType.ACTIVE_PLACE_VACATION_PROPOSAL).map(BooleanUtils::toBoolean).orElse(null);
    }

    @PutMapping("/active-place-vacation-proposal")
    ResponseEntity<?> putActivePlaceVacationProposal(@RequestParam(value = "value") String value){
        ManagementEntity managementEntity = repository.findByType(ManagementType.ACTIVE_PLACE_VACATION_PROPOSAL).orElseGet(() -> new ManagementEntity());
        managementEntity.setValue(value);
        repository.save(managementEntity);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/can-place-vacation-proposal")
    String getCanPlaceVacationProposal(){
        Boolean value = repository.findValueByType(ManagementType.ACTIVE_PLACE_VACATION_PROPOSAL).map(BooleanUtils::toBoolean).orElse(false);

        if(BooleanUtils.isFalse(value)) {
            return "DISABLED";
        }

        return "ACTIVE";
    }
}
