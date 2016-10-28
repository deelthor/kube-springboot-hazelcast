package de.deelthor.ksbhc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator extends AbstractHealthIndicator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        builder.up();
    }
}
