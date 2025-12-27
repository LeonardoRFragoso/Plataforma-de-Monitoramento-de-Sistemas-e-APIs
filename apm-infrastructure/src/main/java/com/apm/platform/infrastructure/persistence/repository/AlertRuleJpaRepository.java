package com.apm.platform.infrastructure.persistence.repository;

import com.apm.platform.infrastructure.persistence.entity.AlertRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRuleJpaRepository extends JpaRepository<AlertRuleEntity, String> {

    List<AlertRuleEntity> findBySystemId(String systemId);

    @Query("SELECT r FROM AlertRuleEntity r WHERE r.systemId = :systemId AND r.enabled = true")
    List<AlertRuleEntity> findEnabledBySystemId(String systemId);

    List<AlertRuleEntity> findByEnabled(boolean enabled);
}
