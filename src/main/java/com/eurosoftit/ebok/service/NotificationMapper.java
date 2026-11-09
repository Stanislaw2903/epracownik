package com.eurosoftit.ebok.service;

import com.eurosoftit.ebok.domain.Notification;
import com.eurosoftit.ebok.domain.NotificationDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotificationMapper {

    public NotificationDTO toDTO(Notification notification){
        NotificationDTO notificationDTO = new NotificationDTO();

        notificationDTO.setId(notification.getId());
        notificationDTO.setCategory(notification.getCategory());
        notificationDTO.setCreateDate(notification.getCreateDate());
        notificationDTO.setCreatedBy(notification.getCreatedBy());
        notificationDTO.setDescription(notification.getDescription());
        notificationDTO.setReadDate(notification.getReadDate());
        notificationDTO.setRecepients(notification.getRecepient());
        notificationDTO.setStatus(notification.isStatus());
        notificationDTO.setSubject(notification.getSubject());
        notificationDTO.setCanal(notification.getCanal());

        return notificationDTO;
    }

    public Page<NotificationDTO> toDTO(Page<Notification> page) {
        return page.map(this::toDTO);
    }

    public Optional<NotificationDTO> toDTO(Optional<Notification> notification) {
        return notification.map(this::toDTO).map(Optional::of).orElse(Optional.empty());
    }
}
