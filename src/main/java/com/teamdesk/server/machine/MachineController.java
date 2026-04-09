package com.teamdesk.server.machine;

import com.teamdesk.server.dto.AgentHeartbeatRequest;
import com.teamdesk.server.dto.AgentRegisterRequest;
import com.teamdesk.server.dto.MachineDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/machines")
@RequiredArgsConstructor
public class MachineController {

    private final MachineService machineService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody AgentRegisterRequest request) {
        machineService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<Void> heartbeat(@Valid @RequestBody AgentHeartbeatRequest request) {
        machineService.heartbeat(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<MachineDto>> list() {
        return ResponseEntity.ok(machineService.findAll());
    }

    @PatchMapping("/{machineId}/offline")
    public ResponseEntity<Void> markOffline(@PathVariable String machineId) {
        machineService.deactivate(machineId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{machineId}")
    public ResponseEntity<Void> delete(@PathVariable String machineId) {
        machineService.delete(machineId);
        return ResponseEntity.noContent().build();
    }
}