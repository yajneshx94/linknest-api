package com.linknest.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.net.URI;
import java.time.LocalDateTime;

@Entity
@Table(name = "links")
@Data
@NoArgsConstructor
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String url;

    // Category for organization
    @Column(length = 50)
    private String category; // "Social", "Work", "Portfolio", "Other"

    // Analytics fields
    @Column(name = "click_count")
    private Long clickCount = 0L;

    @Column(name = "last_clicked")
    private LocalDateTime lastClicked;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    // Extract domain name for display (e.g., "github.com" from "https://github.com/user")
    @Transient
    @JsonProperty("displayDomain")
    public String getDisplayDomain() {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            if (domain != null) {
                return domain.startsWith("www.") ? domain.substring(4) : domain;
            }
        } catch (Exception e) {
            // If URL parsing fails, return domain from URL string
            String cleaned = url.replaceAll("https?://(www\\.)?", "");
            int slashIndex = cleaned.indexOf('/');
            return slashIndex > 0 ? cleaned.substring(0, slashIndex) : cleaned;
        }
        return "Unknown";
    }

    // Get favicon URL for the link
    @Transient
    @JsonProperty("faviconUrl")
    public String getFaviconUrl() {
        try {
            URI uri = new URI(url);
            String domain = uri.getScheme() + "://" + uri.getHost();
            return domain + "/favicon.ico";
        } catch (Exception e) {
            return "https://www.google.com/s2/favicons?domain=" + getDisplayDomain();
        }
    }
}
