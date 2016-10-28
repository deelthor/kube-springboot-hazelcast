package de.deelthor.ksbhc.discovery;

import static ch.qos.logback.core.util.OptionHelper.getEnv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;

import io.fabric8.kubernetes.api.model.EndpointAddress;
import io.fabric8.kubernetes.api.model.EndpointSubset;
import io.fabric8.kubernetes.api.model.Endpoints;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

public class ServiceEndpointResolver
        extends HazelcastKubernetesDiscoveryStrategy.EndpointResolver {

    private static final Logger logger = LoggerFactory.getLogger(ServiceEndpointResolver.class);

    private final String serviceName;
    private final String namespace;

    private final KubernetesClient client;

    public ServiceEndpointResolver(ILogger logger, String namespace, String serviceName) {
        super(logger);

        this.serviceName = serviceName;
        this.namespace = namespace;

        String kubernetesMasterHost = getEnv("KUBERNETES_SERVICE_HOST");
        String kubernetesMasterPort = getEnv("KUBERNETES_SERVICE_PORT");
        String masterUrl = kubernetesMasterHost + ":" + kubernetesMasterPort;

        String accountToken = getAccountToken();
        Config config = new ConfigBuilder().withMasterUrl(masterUrl).withTrustCerts(true).withOauthToken(accountToken).build();
        this.client = new DefaultKubernetesClient(config);
    }

    public List<DiscoveryNode> resolve() {
        logger.info("start endpoint resolving for {}::{}", namespace, serviceName);
        Endpoints endpoints = client.endpoints().inNamespace(namespace).withName(serviceName).get();
        logger.info("endpoints: {}", endpoints);
        if (endpoints == null) {
            return Collections.emptyList();
        }

        List<DiscoveryNode> discoveredNodes = new ArrayList<DiscoveryNode>();
        for (EndpointSubset endpointSubset : endpoints.getSubsets()) {
            for (EndpointAddress endpointAddress : endpointSubset.getAddresses()) {
                Map<String, Object> properties = endpointAddress.getAdditionalProperties();

                String ip = endpointAddress.getIp();
                InetAddress inetAddress = mapAddress(ip);
                int port = getServicePort(properties);

                Address address = new Address(inetAddress, port);
                discoveredNodes.add(new SimpleDiscoveryNode(address, properties));
            }
        }

        logger.info("discoveredNodes: {}", discoveredNodes);
        return discoveredNodes;
    }

    @Override
    void destroy() {
        client.close();
    }

    private String getAccountToken() {
        try {
            String tokenFile = "/var/run/secrets/kubernetes.io/serviceaccount/token";
            File file = new File(tokenFile);
            byte[] data = new byte[(int) file.length()];
            InputStream is = new FileInputStream(file);
            is.read(data);
            return new String(data);

        } catch (IOException e) {
            throw new RuntimeException("Could not get token file", e);
        }
    }
}