package com.eurosoftit.ebok.web.rest.vacation.service;

import com.eurosoftit.ebok.web.rest.persondata.PersonPrz;
import com.eurosoftit.ebok.web.rest.persondata.PersonPrzRepository;
import com.eurosoftit.ebok.web.rest.vacation.Vacation;
import com.eurosoftit.ebok.web.rest.vacation.VacationStatus;
import com.eurosoftit.ebok.web.rest.vacation.VacationType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "vacation.approve", havingValue = "ONE_LEVEL", matchIfMissing = true)
public class VacationOneLevelApproveErpServiceImpl implements VacationErpService {

    private final PersonPrzRepository personPrzRepository;

    @Override
    public void writeToErp(Vacation vacation) {
        if (isAccepted(vacation) &&
            (VacationType.ANNUAL_LEAVE.equals(vacation.getVacationType())
                || VacationType.LEAVE_ON_REQUEST.equals(vacation.getVacationType()))) {
            PersonPrz personPrz = new PersonPrz();
            personPrz.setDateFrom(vacation.getDateFrom().withZoneSameLocal(ZoneId.of("UTC")));
            personPrz.setDateTo(vacation.getDateTo().withZoneSameLocal(ZoneId.of("UTC")));

            if (StringUtils.isNotBlank(vacation.getComment())) {
                personPrz.setComments(vacation.getComment().substring(0, 64));
            }

            personPrz.setType("A");

            if (VacationType.LEAVE_ON_REQUEST.equals(vacation.getVacationType())) {
                personPrz.setSanitaryTests("T");
            }

            personPrz.setExternalId(vacation.getCreatedBy().getExternalId());
            personPrz.setToPay(BigDecimal.ZERO);
            personPrz.setVacationDays(vacation.getUsed());
            personPrz.setAbsSerial(5L);
            personPrz.setTytSerial(9L);

            PersonPrz saved = personPrzRepository.save(personPrz);
            vacation.setErpId(saved.getId());
        }
    }

    @Override
    public void removeFromErp(Vacation vacation) {
        if (vacation.getErpId() != null) {
            personPrzRepository.deleteById(vacation.getErpId());
        }
    }

    @Override
    public List<VacationStatus> getStatusType() {
        return Arrays.asList(VacationStatus.WAITING, VacationStatus.ACCEPTED, VacationStatus.REJECTED, VacationStatus.CANCELLED);
    }

    @Override
    public String getDefaultStatusType() {
        return "WAITING";
    }

    protected boolean isAccepted(Vacation vacation) {
        return VacationStatus.ACCEPTED.equals(vacation.getStatus());
    }
}
