package org.tbk.nostr.demented;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.tbk.nostr.demented.impl.ExampleConnectionEstablishedHandler;
import org.tbk.nostr.demented.impl.NostrSupportService;
import org.tbk.nostr.identity.MoreIdentities;
import org.tbk.nostr.identity.Signer;
import org.tbk.nostr.identity.SimpleSigner;
import org.tbk.nostr.demented.domain.event.EventEntityService;
import org.tbk.nostr.relay.handler.ConnectionEstablishedHandler;

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
    ConnectionEstablishedHandler exampleConnectionEstablishedHandler() {
        return new ExampleConnectionEstablishedHandler(this.properties);
    }
}
