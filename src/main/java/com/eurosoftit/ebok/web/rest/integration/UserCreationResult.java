package com.eurosoftit.ebok.web.rest.integration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCreationResult {
    private List<UserDTO> success = new ArrayList<>();
    private List<UserWithErrorDTO> error;

    @JsonIgnore
    public List<UserWithErrorDTO> getErrorOrCreate() {
        if (this.error == null) {
            this.error = new ArrayList<>();
        }
        return this.error;
    }
}
