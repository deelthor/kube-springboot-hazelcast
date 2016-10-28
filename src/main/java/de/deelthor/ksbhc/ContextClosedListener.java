package de.deelthor.ksbhc;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.hazelcast.map.AbstractEntryProcessor;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.PredicateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Value("${POD_IP:127.0.0.1}")
    private String podIP;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        IMap<String, Lock> locks = hazelcastInstance.getMap(Application.HC_MAP_NAME);
        EntryObject entryObj = new PredicateBuilder().getEntryObject();
        PredicateBuilder predicate = entryObj.get( "ip" ).equal( podIP );

        try {
            Map<String, Object> deletedEntries = locks.executeOnEntries(new DeletePodEntriesProcessor(podIP), predicate);
            if(deletedEntries.size() > 0) {
                logger.info("removed locks for {}: {}", podIP, deletedEntries);
            }
        } catch (Exception e) {
            logger.error("Error occured while removing locks for {}: {}", podIP, e);
        }
    }


    private static class DeletePodEntriesProcessor extends AbstractEntryProcessor<UUID, Lock> {
        private static final long serialVersionUID = -1365634527454605455L;

        private final String podIP;

        public DeletePodEntriesProcessor(String podIP) {
            this.podIP = podIP;
        }

        @Override
        public Object process(Map.Entry<UUID, Lock> entry) {
            Lock lock = entry.getValue();
//            logger.info("check PartitionLock {} for {}", lock, podIP);
            if (podIP.equals(lock.getIp())) {
//                logger.info("remove PartitionLock {} for {}", lock, podIP);
                entry.setValue(null);
                return lock.toString();
            }
            return null;
        }
    }
}
