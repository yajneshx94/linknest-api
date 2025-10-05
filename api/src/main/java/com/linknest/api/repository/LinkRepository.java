package com.linknest.api.repository;

import com.linknest.api.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import com.linknest.api.model.User;
import java.util.List;

public interface LinkRepository extends JpaRepository<Link, Long> {
    // We'll add custom query methods here later
    List<Link> findByUser(User user);
}