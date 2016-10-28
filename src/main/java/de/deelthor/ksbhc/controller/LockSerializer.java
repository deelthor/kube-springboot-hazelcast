package de.deelthor.ksbhc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.nio.serialization.ByteArraySerializer;

import java.io.IOException;

public class LockSerializer implements ByteArraySerializer<Lock> {

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public int getTypeId() {
        return 5;
    }

    @Override
    public void destroy() {
    }

    @Override
    public byte[] write(Lock object) throws IOException {
        return mapper.writeValueAsBytes(object);
    }

    @Override
    public Lock read(byte[] buffer) throws IOException {
        return mapper.readValue(buffer, Lock.class);
    }

}
