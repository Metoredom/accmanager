package com.accmanagement.accmanager.services;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TimeService implements ITimeService{
    @Override
    public Instant now() {
        return Instant.now();
    }
}
