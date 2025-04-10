package org.tbk.nostr.demented;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.tbk.nostr.identity.Identity;
import org.tbk.nostr.identity.Signer;
import org.tbk.nostr.identity.SimpleSigner;
import org.tbk.nostr.persona.Persona;
import org.tbk.nostr.proto.Event;
import org.tbk.nostr.proto.OkResponse;
import org.tbk.nostr.relay.plugin.allowlist.Allowlist;
import org.tbk.nostr.relay.plugin.allowlist.config.AllowlistPluginProperties;
import org.tbk.nostr.relay.plugin.allowlist.db.domain.AllowlistEntryService;
import org.tbk.nostr.template.NostrTemplate;
import org.tbk.nostr.util.MoreEvents;

import java.time.Duration;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = NostrRelayTestConfig.class)
@ActiveProfiles("test")
class NostrRelayPluginAllowlistTest {
    private static final List<Identity.Account> ALLOWED_ACCOUNTS = List.of(
            Persona.alice().deriveAccount(0)
    );

    @Autowired
    private AllowlistPluginProperties allowlistPluginProperties;

    @Autowired
    private Allowlist allowlist;

    @Autowired
    private AllowlistEntryService allowlistEntryService;

    @Autowired
    private NostrTemplate nostrTemplate;

    @BeforeEach
    public void beforeEach() {
        ALLOWED_ACCOUNTS
                .forEach(it -> allowlistEntryService.create(it.getPublicKey()));
    }

    @AfterEach
    public void afterEach() {
        ALLOWED_ACCOUNTS
                .forEach(it -> allowlistEntryService.remove(it.getPublicKey()));
    }

    @Test
    void contextLoads() {
        assertThat(allowlistPluginProperties, is(notNullValue()));
        assertThat(allowlist, is(notNullValue()));
    }

    @Test
    void itShouldDeclineEventFromPubkeyNotInAllowlist() {
        Signer signer = SimpleSigner.random();

        assertThat("sanity check", allowlist.isAllowed(signer.getPublicKey()), is(false));

        Event event = MoreEvents.createFinalizedTextNote(signer, "GM");

        OkResponse ok = nostrTemplate.send(event)
                .blockOptional(Duration.ofSeconds(5))
                .orElseThrow();

        assertThat(ok.getEventId(), is(event.getId()));
        assertThat(ok.getMessage(), is("blocked: pubkey is not allowed."));
        assertThat(ok.getSuccess(), is(false));
    }

    @Test
    void itShouldAllowEventFromPubkeyInAllowlist() {
        SimpleSigner signer = SimpleSigner.fromAccount(ALLOWED_ACCOUNTS.getFirst());

        assertThat("sanity check", allowlist.isAllowed(signer.getPublicKey()), is(true));

        Event event = MoreEvents.createFinalizedTextNote(signer, "GM");

        OkResponse ok = nostrTemplate.send(event)
                .blockOptional(Duration.ofSeconds(5))
                .orElseThrow();

        assertThat(ok.getEventId(), is(event.getId()));
        assertThat(ok.getMessage(), is(""));
        assertThat(ok.getSuccess(), is(true));
    }
}
