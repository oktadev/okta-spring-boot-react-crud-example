package com.okta.developer.jugtours.web;

import com.okta.developer.jugtours.model.Group;
import com.okta.developer.jugtours.model.GroupRepository;
import com.okta.developer.jugtours.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
class GroupController {

    private final Logger log = LoggerFactory.getLogger(GroupController.class);
    private GroupRepository repository;

	public GroupController(GroupRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/groups")
	Collection<Group> groups(Principal principal) {
		return repository.findAllByUserName(principal.getName());
	}

    @GetMapping("/group/{id}")
    ResponseEntity<?> getGroup(@PathVariable Long id) {
        Optional<Group> group = repository.findById(id);
        return group.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

	@PostMapping("/group")
    ResponseEntity<Group> createGroup(@Valid @RequestBody Group group, Principal principal) throws URISyntaxException {
        log.info("Request to create group: {}", group);
        OAuth2Authentication authentication = (OAuth2Authentication) principal;
        Map<String, Object> details = (Map<String, Object>) authentication.getUserAuthentication().getDetails();
        User user = new User(details.get("sub").toString(),
                details.get("name").toString(), details.get("email").toString());
        group.setUser(user);
        Group result = repository.save(group);
        return ResponseEntity.created(new URI("/api/group/" + result.getId()))
                .body(result);
    }

    @PutMapping("/group")
    ResponseEntity<Group> updateGroup(@Valid @RequestBody Group group) {
        log.info("Request to update group: {}", group);
        Group result = repository.save(group);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/group/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long id) {
        log.info("Request to delete group: {}", id);
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}