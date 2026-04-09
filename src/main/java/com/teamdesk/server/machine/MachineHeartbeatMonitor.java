package com.teamdesk.server.machine;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MachineHeartbeatMonitor {

    private final MachineRepository machineRepository;

    @Scheduled(fixedDelay = 10000)
    public void markStaleMachinesOffline() {
        Instant threshold = Instant.now().minusSeconds(15);

        List<Machine> staleMachines =
                machineRepository.findByStatusAndLastHeartbeatAtBefore(MachineStatus.ONLINE, threshold);

        for (Machine machine : staleMachines) {
            machine.setStatus(MachineStatus.OFFLINE);
            machineRepository.save(machine);
        }
    }
}