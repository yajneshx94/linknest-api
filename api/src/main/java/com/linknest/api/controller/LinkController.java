package com.linknest.api.controller;

import com.linknest.api.model.Link;
import com.linknest.api.model.User;
import com.linknest.api.repository.LinkRepository;
import com.linknest.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/links")
public class LinkController {

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private UserRepository userRepository;

    // GET /api/links - Get all links for the currently logged-in user
    @GetMapping
    public ResponseEntity<List<Link>> getLinksForUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Link> links = linkRepository.findByUser(user);
        return ResponseEntity.ok(links);
    }

    // POST /api/links - Create a new link for the currently logged-in user
    @PostMapping
    public ResponseEntity<Link> createLink(@RequestBody Link newLinkRequest, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Link linkToSave = new Link();
        linkToSave.setTitle(newLinkRequest.getTitle());
        linkToSave.setUrl(newLinkRequest.getUrl());
        linkToSave.setUser(user);

        Link savedLink = linkRepository.save(linkToSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLink);
    }

    // PUT /api/links/{id} - Update an existing link
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLink(@PathVariable Long id, @RequestBody Link updatedLinkRequest, @AuthenticationPrincipal UserDetails userDetails) {
        Link existingLink = linkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Link not found with id: " + id));

        if (!existingLink.getUser().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: You don't have permission to update this link.");
        }

        existingLink.setTitle(updatedLinkRequest.getTitle());
        existingLink.setUrl(updatedLinkRequest.getUrl());

        linkRepository.save(existingLink);
        return ResponseEntity.ok(existingLink);
    }

    // DELETE /api/links/{id} - Delete a link
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLink(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Link linkToDelete = linkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Link not found with id: " + id));

        if (!linkToDelete.getUser().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: You don't have permission to delete this link.");
        }

        linkRepository.delete(linkToDelete);
        return ResponseEntity.ok("Link deleted successfully.");
    }

    // GET /api/links/public/{username} - Get all links for a specific user, publicly
    @GetMapping("/public/{username}")
    public ResponseEntity<List<Link>> getPublicLinksForUser(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        List<Link> links = linkRepository.findByUser(user);
        return ResponseEntity.ok(links);
    }
}