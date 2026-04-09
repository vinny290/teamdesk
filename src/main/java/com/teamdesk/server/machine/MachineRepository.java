package com.teamdesk.server.machine;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface MachineRepository extends JpaRepository<Machine, String> {

    List<Machine> findByStatusAndLastHeartbeatAtBefore(MachineStatus status, Instant instant);
}