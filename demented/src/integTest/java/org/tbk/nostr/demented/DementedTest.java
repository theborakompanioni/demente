package org.tbk.nostr.demented;

import fr.acinq.bitcoin.XonlyPublicKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.tbk.nostr.identity.MoreIdentities;
import org.tbk.nostr.identity.SimpleSigner;
import org.tbk.nostr.proto.Event;
import org.tbk.nostr.relay.config.NostrRelayProperties;
import org.tbk.nostr.template.NostrTemplate;

import java.time.Duration;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = NostrRelayTestConfig.class)
@ActiveProfiles("test")
class DementedTest {

    @Autowired(required = false)
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private DementedRelayProperties applicationProperties;

    @Autowired(required = false)
    private NostrRelayProperties relayProperties;

    @Autowired(required = false)
    private NostrTemplate nostrTemplate;

    @Test
    void contextLoads() {
        assertThat(applicationContext, is(notNullValue()));
        assertThat(applicationProperties, is(notNullValue()));
        assertThat(relayProperties, is(notNullValue()));
        assertThat(nostrTemplate, is(notNullValue()));
    }

    @Test
    void itShouldLoadProperties() {
        assertThat(applicationProperties.isStartupEventsEnabled(), is(true));
        assertThat(applicationProperties.getIdentity().isPresent(), is(true));
        assertThat(applicationProperties.getAsyncExecutor().getMaxPoolSize(), is(10));
        assertThat(applicationProperties.getInitialQueryLimit(), is(210));

        assertThat(relayProperties.getMaxLimitPerFilter(), is(2100));
        assertThat(relayProperties.getMaxFilterCount(), is(42));
    }

    @Test
    void itShouldReceiveStartupEvents() {
        XonlyPublicKey applicationPubkey = applicationProperties.getIdentity()
                .map(DementedRelayProperties.IdentityProperties::getSeed)
                .map(MoreIdentities::fromSeed)
                .map(SimpleSigner::fromIdentity)
                .map(SimpleSigner::getPublicKey)
                .orElseThrow();

        List<Event> events = nostrTemplate.fetchEventsByAuthor(applicationPubkey)
                .collectList()
                .blockOptional(Duration.ofSeconds(5))
                .orElseThrow();

        assertThat(events.isEmpty(), is(false));
    }
}
