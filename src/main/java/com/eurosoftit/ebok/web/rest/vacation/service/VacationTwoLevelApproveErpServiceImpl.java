package com.eurosoftit.ebok.web.rest.vacation.service;

import com.eurosoftit.ebok.service.UserService;
import com.eurosoftit.ebok.service.dto.ApprovalType;
import com.eurosoftit.ebok.web.rest.UserUtils;
import com.eurosoftit.ebok.web.rest.persondata.PersonPrzRepository;
import com.eurosoftit.ebok.web.rest.vacation.Vacation;
import com.eurosoftit.ebok.web.rest.vacation.VacationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@ConditionalOnProperty(name = "vacation.approve", havingValue = "TWO_LEVEL")
public class VacationTwoLevelApproveErpServiceImpl extends VacationOneLevelApproveErpServiceImpl {

    @Autowired
    private UserService userService;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    public VacationTwoLevelApproveErpServiceImpl(PersonPrzRepository personPrzRepository) {
        super(personPrzRepository);
    }

    @Override
    protected boolean isAccepted(Vacation vacation) {
        return VacationStatus.ACCEPTED.equals(vacation.getStatus());
    }

    @Override
    public List<VacationStatus> getStatusType() {
        return Arrays.asList(VacationStatus.WAITING, VacationStatus.ACCEPTED, VacationStatus.ACCEPTED_INITIALLY, VacationStatus.REJECTED, VacationStatus.CANCELLED);
    }

    @Override
    public String getDefaultStatusType() {
        final ApprovalType userApprovalType = userService.getUserApprovalType(userUtils.getLoggedUserId());
        if (userApprovalType == null) {
            return "WAITING";
        }

        return userApprovalType == ApprovalType.INITIAL_VERIFICATION ? "WAITING" : "WAITING,ACCEPTED_INITIALLY";
    }
}
