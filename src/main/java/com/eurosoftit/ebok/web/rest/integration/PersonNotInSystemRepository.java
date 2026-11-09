package com.eurosoftit.ebok.web.rest.integration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface PersonNotInSystemRepository extends JpaRepository<PersonNotInSystem, Long> {

    @Query("select new com.eurosoftit.ebok.web.rest.integration.UserDTO(p.id,p.firstName,p.lastName,p.email,p.type) " +
        "from PersonNotInSystem p where p.email is not null " +
        " and p.type in(:types) and p.osoDataZwol is null or p.osoDataZwol >= :osoDataZwol ")
    List<UserDTO> findAll(@Param("types") List<String> types, @Param("osoDataZwol") ZonedDateTime osoDataZwol);
}
