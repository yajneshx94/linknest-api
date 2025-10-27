package com.linknest.api.controller;

import com.linknest.api.model.User;
import com.linknest.api.repository.LinkRepository;
import com.linknest.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LinkRepository linkRepository;

    // Check if user is admin before allowing access
    private boolean isAdmin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getIsAdmin() != null && user.getIsAdmin();
    }

    // Get overall statistics
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@AuthenticationPrincipal UserDetails userDetails) {
        if (!isAdmin(userDetails.getUsername())) {
            return ResponseEntity.status(403).body("Access denied: Admin only");
        }

        long totalUsers = userRepository.count();
        long totalLinks = linkRepository.count();

        // Count active users (users who have at least one link)
        List<User> allUsers = userRepository.findAll();
        long activeUsers = allUsers.stream()
                .filter(user -> user.getLinks() != null && !user.getLinks().isEmpty())
                .count();

        // Count recent registrations (last 7 days)
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        long recentRegistrations = allUsers.stream()
                .filter(user -> user.getCreatedAt() != null && user.getCreatedAt().isAfter(weekAgo))
                .count();

        // Calculate average links per user
        double avgLinksPerUser = totalUsers > 0 ? (double) totalLinks / totalUsers : 0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalLinks", totalLinks);
        stats.put("activeUsers", activeUsers);
        stats.put("recentRegistrations", recentRegistrations);
        stats.put("averageLinksPerUser", Math.round(avgLinksPerUser * 10.0) / 10.0);

        return ResponseEntity.ok(stats);
    }

    // Get recent users (last 10 registered)
    @GetMapping("/users/recent")
    public ResponseEntity<?> getRecentUsers(@AuthenticationPrincipal UserDetails userDetails) {
        if (!isAdmin(userDetails.getUsername())) {
            return ResponseEntity.status(403).body("Access denied: Admin only");
        }

        List<User> users = userRepository.findAll().stream()
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .limit(10)
                .map(user -> {
                    User userCopy = new User();
                    userCopy.setId(user.getId());
                    userCopy.setUsername(user.getUsername());
                    userCopy.setDisplayName(user.getDisplayName());
                    userCopy.setCreatedAt(user.getCreatedAt());
                    userCopy.setIsPublic(user.getIsPublic());
                    userCopy.setIsAdmin(user.getIsAdmin());
                    return userCopy;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    // Get all users with link counts
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@AuthenticationPrincipal UserDetails userDetails) {
        if (!isAdmin(userDetails.getUsername())) {
            return ResponseEntity.status(403).body("Access denied: Admin only");
        }

        List<User> users = userRepository.findAll();

        List<Map<String, Object>> userList = users.stream()
                .map(user -> {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("id", user.getId());
                    userData.put("username", user.getUsername());
                    userData.put("displayName", user.getDisplayName());
                    userData.put("linkCount", user.getLinks() != null ? user.getLinks().size() : 0);
                    userData.put("isAdmin", user.getIsAdmin());
                    userData.put("isPublic", user.getIsPublic());
                    userData.put("createdAt", user.getCreatedAt());
                    return userData;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(userList);
    }

    // Get user growth data (for charts)
    @GetMapping("/growth")
    public ResponseEntity<?> getUserGrowth(@AuthenticationPrincipal UserDetails userDetails) {
        if (!isAdmin(userDetails.getUsername())) {
            return ResponseEntity.status(403).body("Access denied: Admin only");
        }

        List<User> users = userRepository.findAll();

        // Group users by registration date (last 30 days)
        Map<LocalDate, Long> growthData = users.stream()
                .filter(user -> user.getCreatedAt() != null)
                .filter(user -> user.getCreatedAt().isAfter(LocalDateTime.now().minusDays(30)))
                .collect(Collectors.groupingBy(
                        user -> user.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));

        // Sort by date
        List<Map<String, Object>> sortedGrowth = growthData.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("date", entry.getKey().toString());
                    data.put("count", entry.getValue());
                    return data;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(sortedGrowth);
    }

    // Toggle admin status for a user
    @PostMapping("/users/{userId}/toggle-admin")
    public ResponseEntity<?> toggleAdmin(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (!isAdmin(userDetails.getUsername())) {
            return ResponseEntity.status(403).body("Access denied: Admin only");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsAdmin(!user.getIsAdmin());
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "username", user.getUsername(),
                "isAdmin", user.getIsAdmin()
        ));
    }
}