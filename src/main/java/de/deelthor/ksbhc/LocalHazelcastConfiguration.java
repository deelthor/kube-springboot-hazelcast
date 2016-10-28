package de.deelthor.ksbhc;

import static java.util.Collections.singletonList;

import org.springframework.context.annotation.Bean;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;

import de.deelthor.ksbhc.controller.Lock;
import de.deelthor.ksbhc.controller.LockSerializer;

public class LocalHazelcastConfiguration {

    @Bean
    public Config config() {

        Config config = new Config();

        JoinConfig joinConfig = config.getNetworkConfig().getJoin();

        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().setEnabled(true).setMembers(singletonList("127.0.0.1"));

        SerializationConfig serializationConfig = config.getSerializationConfig();
        serializationConfig.addSerializerConfig(
                new SerializerConfig().setTypeClass(Lock.class).setImplementation(new LockSerializer()));
        config.setProperty("hazelcast.shutdownhook.enabled", "false");
        return config;
    }
}
