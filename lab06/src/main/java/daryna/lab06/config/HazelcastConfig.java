package daryna.lab06.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import daryna.lab06.hazelcast.CounterMapStore;
import daryna.lab06.repository.CounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class HazelcastConfig {


    @Bean(name = "hazelcastServer")
    public HazelcastInstance hazelcastInstance(CounterRepository counterRepository) {

        CounterMapStore mapStore = new CounterMapStore(counterRepository);

        MapStoreConfig storeConfig = new MapStoreConfig()
                .setImplementation(mapStore)
                .setWriteDelaySeconds(5)
                .setEnabled(true);

        MapConfig countersConfig = new MapConfig("counters")
                .setMapStoreConfig(storeConfig);

        Config config = new Config();
        config.setClusterName("lab06");
        config.addMapConfig(countersConfig);

        return Hazelcast.newHazelcastInstance(config);
    }
}