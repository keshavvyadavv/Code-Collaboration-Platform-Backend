package com.versionservice.entity;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "snapshots")
public class Snapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer snapshotId;
    @Column(nullable = false)
    Integer projectId;
    @Column(nullable = false)
    Integer field;
    @Column(nullable = false)
    Integer authorId;
    @Column(nullable = false)
    String message;
    @Column(nullable = false)
    String content;
    @Column(nullable = false)
    String hash;
    Integer parentSnapshotId;
    String branch;
    String tag;
    @Column(nullable = false, updatable = false)
    LocalTime createdAt;
}
