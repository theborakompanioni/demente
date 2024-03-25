package org.tbk.nostr.demented;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.tbk.nostr.demented.domain.event.EventEntityService;
import org.tbk.nostr.demented.filter.IndexWriterFilter;
import org.tbk.nostr.demented.impl.NostrSupportService;
import org.tbk.nostr.identity.MoreIdentities;
import org.tbk.nostr.identity.Signer;
import org.tbk.nostr.identity.SimpleSigner;

import java.io.IOException;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DementedRelayProperties.class)
@RequiredArgsConstructor
class DementedConfig {

    @NonNull
    private final DementedRelayProperties properties;

    @Bean
    @ConditionalOnProperty("org.tbk.nostr.demented.relay.identity.mnemonics")
    Signer serverSigner() {
        return properties.getIdentity()
                .map(DementedRelayProperties.IdentityProperties::getSeed)
                .map(MoreIdentities::fromSeed)
                .map(SimpleSigner::fromPrivateKey)
                .orElseThrow();
    }

    @Bean
    NostrSupportService nipSupportService(EventEntityService eventEntityService, ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor) {
        return new NostrSupportService(eventEntityService, asyncThreadPoolTaskExecutor);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    IndexWriterFilter indexWriterFilter() throws IOException {
        return new IndexWriterFilter(new ClassPathResource("static/index.html"));
    }
}
