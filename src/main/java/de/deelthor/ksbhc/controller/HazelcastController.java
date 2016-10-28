package de.deelthor.ksbhc.controller;

import java.util.Collection;

import de.deelthor.ksbhc.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.util.UuidUtil;

@RestController
@RequestMapping("/hazelcast")
public class HazelcastController {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Value("${POD_IP:127.0.0.1}")
    private String podIP;

    @RequestMapping(path = "/add", method = RequestMethod.GET)
    public String addItem() {
        IMap<String, Lock> map = hazelcastInstance.getMap(Application.HC_MAP_NAME);
        map.put(UuidUtil.newSecureUuidString(), new Lock(UuidUtil.newSecureUuidString().toString(), podIP));
        return "added Item";
    }

    @RequestMapping(path = "/get", method = RequestMethod.GET)
    public Collection<Lock> getItems() {
        IMap<String, Lock> map = hazelcastInstance.getMap(Application.HC_MAP_NAME);
        return map.values();
    }

}
