package com.teamdesk.server.session;

import com.teamdesk.server.dto.StartSessionRequest;
import com.teamdesk.server.dto.StartSessionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/remote-sessions")
@RequiredArgsConstructor
public class RemoteSessionController {

    private final RemoteSessionService remoteSessionService;

    @PostMapping("/start")
    public ResponseEntity<StartSessionResponse> start(@Valid @RequestBody StartSessionRequest request) {
        return ResponseEntity.ok(remoteSessionService.start(request));
    }
}