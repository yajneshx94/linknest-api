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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/links")
public class LinkController {

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all links for the currently logged-in user
    @GetMapping
    public ResponseEntity<List<Link>> getLinksForUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Link> links = linkRepository.findByUser(user);
        return ResponseEntity.ok(links);
    }

    // Create a new link for the currently logged-in user
    @PostMapping
    public ResponseEntity<Link> createLink(@RequestBody Link newLinkRequest, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Link linkToSave = new Link();
        linkToSave.setTitle(newLinkRequest.getTitle());
        linkToSave.setUrl(newLinkRequest.getUrl());
        linkToSave.setCategory(newLinkRequest.getCategory() != null ? newLinkRequest.getCategory() : "Other");
        linkToSave.setUser(user);

        Link savedLink = linkRepository.save(linkToSave);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLink);
    }

    // Update an existing link
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLink(@PathVariable Long id, @RequestBody Link updatedLinkRequest, @AuthenticationPrincipal UserDetails userDetails) {
        Link existingLink = linkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Link not found with id: " + id));

        if (!existingLink.getUser().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: You don't have permission to update this link.");
        }

        existingLink.setTitle(updatedLinkRequest.getTitle());
        existingLink.setUrl(updatedLinkRequest.getUrl());
        if (updatedLinkRequest.getCategory() != null) {
            existingLink.setCategory(updatedLinkRequest.getCategory());
        }

        linkRepository.save(existingLink);
        return ResponseEntity.ok(existingLink);
    }

    // Delete a link
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

    // Track link click (for analytics)
    @PostMapping("/{id}/click")
    public ResponseEntity<?> trackClick(@PathVariable Long id) {
        Link link = linkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Link not found"));

        link.setClickCount(link.getClickCount() + 1);
        link.setLastClicked(LocalDateTime.now());
        linkRepository.save(link);

        return ResponseEntity.ok(Map.of("success", true, "clickCount", link.getClickCount()));
    }

    // Get analytics for user's links
    @GetMapping("/analytics")
    public ResponseEntity<?> getLinkAnalytics(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Link> links = linkRepository.findByUser(user);

        // Calculate total clicks
        long totalClicks = links.stream()
                .mapToLong(link -> link.getClickCount() != null ? link.getClickCount() : 0)
                .sum();

        // Get top 5 most clicked links
        List<Map<String, Object>> topLinks = links.stream()
                .sorted((a, b) -> Long.compare(
                        b.getClickCount() != null ? b.getClickCount() : 0,
                        a.getClickCount() != null ? a.getClickCount() : 0))
                .limit(5)
                .map(link -> {
                    Map<String, Object> linkData = new HashMap<>();
                    linkData.put("id", link.getId());
                    linkData.put("title", link.getTitle());
                    linkData.put("clickCount", link.getClickCount());
                    linkData.put("lastClicked", link.getLastClicked());
                    return linkData;
                })
                .collect(Collectors.toList());

        // Group by category
        Map<String, Long> clicksByCategory = links.stream()
                .collect(Collectors.groupingBy(
                        link -> link.getCategory() != null ? link.getCategory() : "Other",
                        Collectors.summingLong(link -> link.getClickCount() != null ? link.getClickCount() : 0)
                ));

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalLinks", links.size());
        analytics.put("totalClicks", totalClicks);
        analytics.put("topLinks", topLinks);
        analytics.put("clicksByCategory", clicksByCategory);

        return ResponseEntity.ok(analytics);
    }

    // Get all links for a specific user, publicly
    @GetMapping("/public/{username}")
    public ResponseEntity<?> getPublicLinksForUser(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // Check if profile is public
        if (!user.getIsPublic()) {
            return ResponseEntity.status(403).body("This profile is private");
        }

        List<Link> links = linkRepository.findByUser(user);
        return ResponseEntity.ok(links);
    }
}