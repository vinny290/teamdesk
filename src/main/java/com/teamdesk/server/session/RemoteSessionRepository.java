package com.teamdesk.server.session;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RemoteSessionRepository extends JpaRepository<RemoteSession, String> {
}