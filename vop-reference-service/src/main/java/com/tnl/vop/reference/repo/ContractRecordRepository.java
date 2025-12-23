package com.tnl.vop.reference.repo;

import com.tnl.vop.reference.domain.ContractRecord;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRecordRepository extends JpaRepository<ContractRecord, UUID> {
  Optional<ContractRecord> findByExternalRef(String externalRef);
}
