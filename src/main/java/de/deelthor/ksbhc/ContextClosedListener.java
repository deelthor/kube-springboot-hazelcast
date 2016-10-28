package de.deelthor.ksbhc;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import de.deelthor.ksbhc.controller.Lock;

@Component
public class ContextClosedListener implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Value("${POD_IP:127.0.0.1}")
    private String podIP;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        IMap<String, Lock> map = hazelcastInstance.getMap(Application.HC_MAP_NAME);
        Set<String> keys = map.keySet();
        for (String key : keys) {
            Lock lock = map.get(key);
            if (podIP.equals(lock.getIp())) {
                map.remove(key);
            }
        }

    }
}
