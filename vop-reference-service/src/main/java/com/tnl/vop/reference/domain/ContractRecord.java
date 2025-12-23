package com.tnl.vop.reference.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "contract_record")
public class ContractRecord {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id = UUID.randomUUID();

  @Column(name = "external_ref", nullable = false, unique = true, length = 64)
  private String externalRef;

  @Column(name = "title", nullable = false, length = 256)
  private String title;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  public ContractRecord() {}

  public ContractRecord(String externalRef, String title) {
    this.externalRef = externalRef;
    this.title = title;
    this.createdAt = Instant.now();
  }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }

  public String getExternalRef() { return externalRef; }
  public void setExternalRef(String externalRef) { this.externalRef = externalRef; }

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
