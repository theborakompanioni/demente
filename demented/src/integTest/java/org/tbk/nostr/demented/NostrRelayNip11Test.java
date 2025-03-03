package org.tbk.nostr.demented;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.tbk.nostr.nip11.RelayInfoDocument;
import org.tbk.nostr.template.NostrTemplate;
import org.tbk.nostr.util.MorePublicKeys;

import java.net.URI;
import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = NostrRelayTestConfig.class)
@ActiveProfiles({"test", "nip-test"})
class NostrRelayNip11Test {

    @Autowired
    private NostrTemplate nostrTemplate;

    @Test
    void itShouldFetchRelayInfoSuccessfully0() {
        RelayInfoDocument relayInfo = nostrTemplate.fetchRelayInfoDocument()
                .blockOptional(Duration.ofSeconds(5))
                .orElseThrow();

        assertThat(relayInfo, is(notNullValue()));
        assertThat(relayInfo.getName(), is("demented-nip-test"));
        assertThat(relayInfo.getDescription(), is("The Times 03/Jan/2009 Chancellor on brink of second bailout for banks"));
        assertThat(relayInfo.getPubkey(), is(MorePublicKeys.fromHex("0000000000000000000000000000000000000000000000000000000000000001")));
        assertThat(relayInfo.getSoftware(), is(URI.create("https://github.com/theborakompanioni/nostr-spring-boot-starter")));
        assertThat(relayInfo.getVersion(), is("0.2.1-beta"));
        assertThat(relayInfo.getSupportedNips(), hasItems(1, 42, 21000000));
        assertThat(relayInfo.getContact(), is(nullValue()));
        assertThat(relayInfo.getIcon(), is("https://example.org/favicon.ico"));
    }
}
