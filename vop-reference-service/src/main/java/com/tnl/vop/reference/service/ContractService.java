package com.tnl.vop.reference.service;

import com.tnl.vop.reference.domain.ContractRecord;
import com.tnl.vop.reference.repo.ContractRecordRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContractService {

  private final ContractRecordRepository repo;

  public ContractService(ContractRecordRepository repo) {
    this.repo = repo;
  }

  @Transactional
  public ContractRecord create(String externalRef, String title) {
    var existing = repo.findByExternalRef(externalRef);
    if (existing.isPresent()) {
      throw new IllegalArgumentException("externalRef already exists: " + externalRef);
    }
    return repo.save(new ContractRecord(externalRef, title));
  }

  @Transactional(readOnly = true)
  public ContractRecord get(UUID id) {
    return repo.findById(id).orElse(null);
  }
}
