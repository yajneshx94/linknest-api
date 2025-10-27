package com.linknest.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // Profile customization fields
    @Column(name = "display_name")
    private String displayName;

    @Column(length = 500)
    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(length = 20)
    private String theme = "light"; // light, dark, gradient

    // Privacy control
    @Column(name = "is_public")
    private Boolean isPublic = true;

    // Admin field
    @Column(name = "is_admin")
    private Boolean isAdmin = false;

    // Timestamp for analytics
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Link> links;
}