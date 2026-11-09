package com.eurosoftit.ebok.web.rest.errors;

public class ExternalIdAlreadyUsedException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public ExternalIdAlreadyUsedException() {
        super(ErrorConstants.EXTERNAL_ID_ALREADY_USED_TYPE, "External id is already in use!", "userManagement", "externalidxists");
    }
}
