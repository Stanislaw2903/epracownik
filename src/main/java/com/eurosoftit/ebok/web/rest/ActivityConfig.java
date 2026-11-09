package com.eurosoftit.ebok.web.rest;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = ActivityClient.class)
public class ActivityConfig {
}
