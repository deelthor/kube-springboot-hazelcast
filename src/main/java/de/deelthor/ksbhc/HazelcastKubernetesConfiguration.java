package de.deelthor.ksbhc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.*;

import de.deelthor.ksbhc.controller.Lock;
import de.deelthor.ksbhc.controller.LockSerializer;
import de.deelthor.ksbhc.discovery.HazelcastKubernetesDiscoveryStrategyFactory;
import de.deelthor.ksbhc.discovery.KubernetesProperties;

@Configuration
public class HazelcastKubernetesConfiguration {

    @Value("${datagrid.group.name}")
    private String datagridGroupName;

    @Value("${datagrid.group.pwd}")
    private String datagridGroupPwd;

    @Value("${datagrid.discovery.namespace}")
    private String datagridDiscoveryNamespace;

    @Value("${datagrid.discovery.service}")
    private String datagridDiscoveryServiceName;

    @Bean
    public Config config() {

        Config cfg = new Config();

        SerializationConfig serializationConfig = cfg.getSerializationConfig();
        serializationConfig.addSerializerConfig(
                new SerializerConfig().setTypeClass(Lock.class).setImplementation(new LockSerializer()));

        // KUBERNETES discovery: at the moment the discovery needs to be
        // activated explicitly
        cfg.setProperty("hazelcast.rest.enabled", "true");
        cfg.setProperty("hazelcast.discovery.enabled", "true");
        // needed for a graceful shutdown
        cfg.setProperty("hazelcast.shutdownhook.enabled", "false");
        cfg.setProperty("hazelcast.logging.type", "slf4j");
        // Timeout in seconds to connect all other cluster members when a member
        // is joining a cluster.
        cfg.setProperty("hazelcast.connect.all.wait.seconds", "120");

        cfg.getGroupConfig().setName(datagridGroupName).setPassword(datagridGroupPwd);

        NetworkConfig network = cfg.getNetworkConfig();
        JoinConfig join = network.getJoin();
        join.getAwsConfig().setEnabled(false);
        join.getMulticastConfig().setEnabled(false);
        join.getTcpIpConfig().setEnabled(false);

        // KUBERNETES discovery: configure discovery service API lookup
        DiscoveryStrategyConfig discoveryStrategyConfig = new DiscoveryStrategyConfig(new HazelcastKubernetesDiscoveryStrategyFactory());
        discoveryStrategyConfig.addProperty(KubernetesProperties.NAMESPACE.key(), datagridDiscoveryNamespace);
        discoveryStrategyConfig.addProperty(KubernetesProperties.SERVICE_NAME.key(), datagridDiscoveryServiceName);
        join.getDiscoveryConfig().addDiscoveryStrategyConfig(discoveryStrategyConfig);

        return cfg;
    }
}
