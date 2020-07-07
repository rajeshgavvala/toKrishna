package com.wissenbaumllp.app.repository;

import com.wissenbaumllp.app.models.AppRole;
import com.wissenbaumllp.app.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByRole(AppRole role);
}
