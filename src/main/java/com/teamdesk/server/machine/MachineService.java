package com.teamdesk.server.machine;

import com.teamdesk.server.dto.AgentHeartbeatRequest;
import com.teamdesk.server.dto.AgentRegisterRequest;
import com.teamdesk.server.dto.MachineDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MachineService {

    private final MachineRepository machineRepository;

    @Transactional
    public Machine register(AgentRegisterRequest request) {
        Machine machine = machineRepository.findById(request.getMachineId())
                .orElseGet(Machine::new);

        machine.setMachineId(request.getMachineId());
        machine.setHostname(request.getHostname());
        machine.setOsName(request.getOsName());
        machine.setAgentVersion(request.getAgentVersion());
        machine.setLastKnownIp(request.getIp());
        machine.setStatus(MachineStatus.ONLINE);

        Instant now = Instant.now();
        if (machine.getRegisteredAt() == null) {
            machine.setRegisteredAt(now);
        }
        machine.setLastHeartbeatAt(now);

        return machineRepository.save(machine);
    }

    @Transactional
    public void heartbeat(AgentHeartbeatRequest request) {
        Machine machine = machineRepository.findById(request.getMachineId())
                .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + request.getMachineId()));

        machine.setLastHeartbeatAt(Instant.now());
        machine.setStatus(MachineStatus.ONLINE);
        if (request.getIp() != null) {
            machine.setLastKnownIp(request.getIp());
        }

        machineRepository.save(machine);
    }

    @Transactional(readOnly = true)
    public List<MachineDto> findAll() {
        return machineRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void markOffline(String machineId) {
        machineRepository.findById(machineId).ifPresent(machine -> {
            machine.setStatus(MachineStatus.OFFLINE);
            machineRepository.save(machine);
        });
    }

    private MachineDto toDto(Machine machine) {
        return MachineDto.builder()
                .machineId(machine.getMachineId())
                .hostname(machine.getHostname())
                .osName(machine.getOsName())
                .agentVersion(machine.getAgentVersion())
                .lastKnownIp(machine.getLastKnownIp())
                .registeredAt(machine.getRegisteredAt())
                .lastHeartbeatAt(machine.getLastHeartbeatAt())
                .status(machine.getStatus())
                .build();
    }
}