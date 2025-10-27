package com.linknest.api.controller;

import com.linknest.api.model.User;
import com.linknest.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    // Get current user's profile
    @GetMapping
    public ResponseEntity<?> getCurrentProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> profile = new HashMap<>();
        profile.put("username", user.getUsername());
        profile.put("displayName", user.getDisplayName());
        profile.put("bio", user.getBio());
        profile.put("avatarUrl", user.getAvatarUrl());
        profile.put("theme", user.getTheme());
        profile.put("isPublic", user.getIsPublic());
        profile.put("linkCount", user.getLinks() != null ? user.getLinks().size() : 0);
        profile.put("createdAt", user.getCreatedAt());

        return ResponseEntity.ok(profile);
    }

    // Update profile
    @PutMapping
    public ResponseEntity<?> updateProfile(
            @RequestBody Map<String, Object> updates,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update fields if provided
        if (updates.containsKey("displayName")) {
            user.setDisplayName((String) updates.get("displayName"));
        }
        if (updates.containsKey("bio")) {
            user.setBio((String) updates.get("bio"));
        }
        if (updates.containsKey("avatarUrl")) {
            user.setAvatarUrl((String) updates.get("avatarUrl"));
        }
        if (updates.containsKey("theme")) {
            String theme = (String) updates.get("theme");
            if (theme.equals("light") || theme.equals("dark") || theme.equals("gradient")) {
                user.setTheme(theme);
            }
        }
        if (updates.containsKey("isPublic")) {
            user.setIsPublic((Boolean) updates.get("isPublic"));
        }

        userRepository.save(user);

        return ResponseEntity.ok("Profile updated successfully");
    }

    // Get public profile (anyone can access)
    @GetMapping("/public/{username}")
    public ResponseEntity<?> getPublicProfile(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if profile is public
        if (!user.getIsPublic()) {
            return ResponseEntity.status(403).body("This profile is private");
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("username", user.getUsername());
        profile.put("displayName", user.getDisplayName());
        profile.put("bio", user.getBio());
        profile.put("avatarUrl", user.getAvatarUrl());
        profile.put("theme", user.getTheme());
        profile.put("linkCount", user.getLinks() != null ? user.getLinks().size() : 0);

        return ResponseEntity.ok(profile);
    }
}