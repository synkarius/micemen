package com.explosionduck.micemen.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private static final AtomicLong COUNTER = new AtomicLong();

    public static long createId() {
        return COUNTER.getAndIncrement();
    }
}
