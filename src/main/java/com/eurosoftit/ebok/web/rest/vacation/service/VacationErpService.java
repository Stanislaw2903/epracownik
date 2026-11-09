package com.eurosoftit.ebok.web.rest.vacation.service;

import com.eurosoftit.ebok.web.rest.vacation.Vacation;
import com.eurosoftit.ebok.web.rest.vacation.VacationStatus;

import java.util.List;

public interface VacationErpService {

    void writeToErp(Vacation vacation);

    void removeFromErp(Vacation vacation);

    List<VacationStatus> getStatusType();

    String getDefaultStatusType();
}
