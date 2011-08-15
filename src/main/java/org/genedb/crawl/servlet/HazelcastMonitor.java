package org.genedb.crawl.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.InstanceEvent;
import com.hazelcast.core.InstanceListener;

public class HazelcastMonitor implements InstanceListener {

    private Logger logger = Logger.getLogger(HazelcastMonitor.class);
    private EntryMonitor<Object,Object> entryMonitor = new EntryMonitor<Object,Object>();
    private int time = 300000;
    private Map<String,Timer> timers = new HashMap<String,Timer>();
    
    private void instanceEvent(InstanceEvent event) {
        logger.info(String.format("%s %s %s", event.getEventType(), event.getInstanceType(), event.getInstance()));
    }
    
    private String getId(InstanceEvent event) {
        return event.getInstance().getId().toString().substring(2);
    }
    
    @Override
    public void instanceCreated(InstanceEvent event) {
        instanceEvent(event);
        
        final String id = getId(event);
        
        stats(id);
        
        Hazelcast.getMap(id).addEntryListener(entryMonitor, false);
        
        Timer timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override public void run() {
                stats(id);
            }
        }, time, time);
        timers.put(id, timer);
    }

    @Override
    public void instanceDestroyed(InstanceEvent event) {
        instanceEvent(event);
        clearTimer(getId(event));
    }
    
    public void clear() {
        logger.warn("Clearing monitors");
        for (Entry<String,Timer> entry : timers.entrySet()) {
            clearTimer(entry.getKey());
        }
        timers = new HashMap<String,Timer>();
    }
    
    private void clearTimer(String id) {
        logger.warn("Clearing monitors for " + id);
        Timer timer = timers.get(id);
        timer.cancel();
        timer.purge();
        logger.warn("timer purged");
        
        // attempting to remove the listener in this way stalls tomcat shutdown.
        // Hazelcast.getMap(id).removeEntryListener(entryMonitor);
        // logger.warn("listener removed");
    }
    
    private void stats(String mapName) {
        logger.info(String.format("%s (%s):\n%s\n%s\n%s",
                mapName,
                Hazelcast.getMap(mapName).size(),
                Hazelcast.getConfig().findMatchingMapConfig(mapName),
                Hazelcast.getMap(mapName),
                Hazelcast.getMap(mapName).getLocalMapStats()));
    }

    class EntryMonitor<K, V> implements EntryListener<K, V> {

        private void entryEvent(EntryEvent<K, V> event) {
            logger.info(String.format("%s %s", event.getEventType(), event.getName()));
        }

        @Override
        public void entryAdded(EntryEvent<K, V> event) {
            entryEvent(event);
        }

        @Override
        public void entryEvicted(EntryEvent<K, V> event) {
            entryEvent(event);

        }

        @Override
        public void entryRemoved(EntryEvent<K, V> event) {
            entryEvent(event);
        }

        @Override
        public void entryUpdated(EntryEvent<K, V> event) {
            entryEvent(event);
        }

    }
}
