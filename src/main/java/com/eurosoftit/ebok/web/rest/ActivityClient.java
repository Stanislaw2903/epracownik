package com.eurosoftit.ebok.web.rest;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "activity", url = "${activity.url}")
public interface ActivityClient {
    @GetMapping("/pp_api.php")
    IdVendo getId(@RequestParam("idsp") Long externalId, @RequestParam("userName") String userName);

    @Data
    class IdVendo{
       int status;
       Integer idvendo;
    }
}
