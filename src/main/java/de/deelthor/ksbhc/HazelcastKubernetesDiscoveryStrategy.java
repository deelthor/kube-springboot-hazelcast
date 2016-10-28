package de.deelthor.ksbhc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;

public class HazelcastKubernetesDiscoveryStrategy implements DiscoveryStrategy {

    private static final String HAZELCAST_SERVICE_PORT = "hazelcast-service-port";

    private final EndpointResolver endpointResolver;

    HazelcastKubernetesDiscoveryStrategy(ILogger logger, Map<String, Comparable> properties) {
        String namespace = getOrNull(properties, KubernetesProperties.NAMESPACE);
        String serviceName = getOrNull(properties, KubernetesProperties.SERVICE_NAME);
        
        logger.info("=== set namespace to " + namespace);
        logger.info("=== set serviceName to " + serviceName);
        
        if (serviceName == null || namespace == null) {
            throw new RuntimeException(
                    "For kubernetes discovery 'service-name' and 'namespace' must be set");
        }

        this.endpointResolver = new ServiceEndpointResolver(logger, namespace, serviceName);
    }

    public void start() {
        endpointResolver.start();
    }

    public Iterable<DiscoveryNode> discoverNodes() {
        return endpointResolver.resolve();
    }

    public void destroy() {
        endpointResolver.destroy();
    }

    private <T extends Comparable> T getOrNull(Map<String, Comparable> properties, PropertyDefinition property) {
        return getOrDefault(properties, property, null);
    }

    private <T extends Comparable> T getOrDefault(Map<String, Comparable> properties, PropertyDefinition property,
            T defaultValue) {

        if (properties == null || property == null) {
            return defaultValue;
        }

        Comparable value = properties.get(property.key());
        if (value == null) {
            return defaultValue;
        }

        return (T) value;
    }

    static abstract class EndpointResolver {
        private final ILogger logger;

        EndpointResolver(ILogger logger) {
            this.logger = logger;
        }

        abstract List<DiscoveryNode> resolve();

        void start() {
        }

        void destroy() {
        }

        protected InetAddress mapAddress(String address) {
            if (address == null) {
                return null;
            }
            try {
                return InetAddress.getByName(address);
            } catch (UnknownHostException e) {
                logger.warning("Address '" + address + "' could not be resolved");
            }
            return null;
        }

        protected int getServicePort(Map<String, Object> properties) {
            int port = NetworkConfig.DEFAULT_PORT;
            if (properties != null) {
                String servicePort = (String) properties.get(HAZELCAST_SERVICE_PORT);
                if (servicePort != null) {
                    port = Integer.parseInt(servicePort);
                }
            }
            return port;
        }
    }
}