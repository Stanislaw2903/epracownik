
package com.eurosoftit.ebok.web.rest.vacation.service;

import com.eurosoftit.ebok.domain.User;
import com.eurosoftit.ebok.security.AuthoritiesConstants;
import com.eurosoftit.ebok.service.dto.ApprovalType;
import com.eurosoftit.ebok.service.dto.ApprovalVerificationType;
import com.eurosoftit.ebok.web.rest.UserUtils;
import com.eurosoftit.ebok.web.rest.vacation.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationValidator {

    @Autowired
    private VacationRepository repository;
    @Autowired
    private UserUtils userUtils;

    @Value("${vacation.approve:ONE_LEVEL}")
    private ApproveLevelType approveLevel;

    @Transactional
    @SneakyThrows
    public boolean validate(Vacation vacation) {
        if (approveLevel == ApproveLevelType.ONE_LEVEL) {
            if (userUtils.hasRole(AuthoritiesConstants.HR)) {
                final Long createdById = vacation.getCreatedBy().getId();
                final List<Long> users = new ArrayList<>();
                users.addAll(this.userUtils.getLoggedUserRelatedUserIds());
                users.add(this.userUtils.getLoggedUserId());

                if(!users.contains(createdById)) {
                    throw new IllegalArgumentException("error.cannotProcessed");
                }
            }
            return false;
        }

        final Vacation beforeStatus = repository.findById(vacation.getId()).get();
        if (isStatusChanged(vacation, beforeStatus)) {
            final User createdBy = vacation.getCreatedBy();
            final User loggerUserReference = userUtils.getLoggerUserReference();

            if (createdBy.getApprovalVerification() == ApprovalVerificationType.TWO_LEVEL) {
                final VacationStatus statusType = vacation.getStatus();
                final VacationStatus beforeStatusType = beforeStatus.getStatus();

                if (beforeStatusType == VacationStatus.ACCEPTED_INITIALLY) {
                    if (statusType == VacationStatus.ACCEPTED) {
                        if (loggerUserReference.getApprovalType() != ApprovalType.FINAL_VERIFICATION) {
                            throw new LackOfFinalApproveException();
                        }
                    }
                } else if (beforeStatusType == VacationStatus.WAITING) {
                    if (statusType == VacationStatus.ACCEPTED
                        && (loggerUserReference.getApprovalType() != ApprovalType.FINAL_VERIFICATION)
                        && createdBy.getApprovalVerification() == ApprovalVerificationType.TWO_LEVEL) {
                        throw new LackOfFinalApproveException();
                    }
                } else if (beforeStatusType == VacationStatus.WAITING && statusType == VacationStatus.ACCEPTED_INITIALLY) {
                    if(loggerUserReference.getApprovalType() != ApprovalType.INITIAL_VERIFICATION) {
                        throw new LackOfInitialApproveException();
                    }
                }
            } else if(createdBy.getApprovalVerification() == ApprovalVerificationType.ONE_LEVEL) {
                final VacationStatus beforeStatusType = beforeStatus.getStatus();
                if (beforeStatusType == VacationStatus.WAITING) {
                    if(loggerUserReference.getApprovalType() != ApprovalType.FINAL_VERIFICATION) {
                        throw new LackOfFinalApproveException();
                    }
                }
            }
        }

        return false;
    }

    private boolean isStatusChanged(Vacation vacation, Vacation beforeStatus) {
        return beforeStatus.getStatus() != vacation.getStatus();
    }
}
