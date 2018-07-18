package com.okta.developer.jugtours.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

@RepositoryEventHandler(Group.class)
public class GroupEventHandler {

    private final Logger log = LoggerFactory.getLogger(GroupEventHandler.class);

    @HandleBeforeSave
    @SuppressWarnings("unchecked")
    public void handleBeforeSave(Group group) {
        Map<String, Object> details = (Map<String, Object>) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        User user = new User(details.get("sub").toString(),
                details.get("name").toString(), details.get("email").toString());
        log.info("Creating group: {} with user: {}", group.getName());
        group.setUser(user);
    }
}