[![Build](https://github.com/theborakompanioni/demente/actions/workflows/build.yml/badge.svg)](https://github.com/theborakompanioni/demente/actions/workflows/build.yml)
[![GitHub Release](https://img.shields.io/github/release/theborakompanioni/demente.svg?maxAge=3600)](https://github.com/theborakompanioni/demente/releases/latest)
[![License](https://img.shields.io/github/license/theborakompanioni/demente.svg?maxAge=2592000)](https://github.com/theborakompanioni/demente/blob/master/LICENSE)

demente
===

## Development

### Requirements
- java >=21
- docker

### Build
```shell script
./gradlew build -x test
```

### Test
```shell script
./gradlew test integrationTest --rerun-tasks --no-parallel
```

Run full test suite (including load & end-to-end tests):
```shell script
CI=true ./gradlew test integrationTest --rerun-tasks --no-parallel
```

## Run
```shell script
./gradlew bootRun
```

```shell
curl -H "Accept: application/nostr+json" http://localhost:8080 | jq
```
```json
{
  "name": "demented-dev",
  "description": "A nostr relay implementation",
  "supported_nips": [
    1,
    9,
    11,
    40
  ],
  "software": "https://github.com/theborakompanioni/demente",
  "version": "0.0.1"
}
```

### Docker

```shell
just clean package docker-compose-up-devel
# or
./gradlew clean bootJar && docker compose -f docker-compose-devel.yml up --build
```

```shell
just docker-build
# or
docker build -t theborakompanioni/demented .
docker run -p 8080:8080 --name demented theborakompanioni/demented
```

```shell
just docker-run-shell
# or
docker run --rm --entrypoint="/bin/bash" -it theborakompanioni/demented
```

## Contributing
All contributions and ideas are always welcome. For any question, bug or feature request,
please create an [issue](https://github.com/theborakompanioni/demente/issues).
Before you start, please read the [contributing guidelines](contributing.md).

## Resources

- nostr (GitHub): https://github.com/nostr-protocol/nostr
- NIPs (GitHub): https://github.com/nostr-protocol/nips
- nostr-spring-boot-starter: https://github.com/theborakompanioni/nostr-spring-boot-starter

---

- nostr.com: https://nostr.com
- nostr Relay Registry: https://nostr-registry.netlify.app
- awesome-nostr (GitHub): https://github.com/aljazceru/awesome-nostr

## License

The project is licensed under the Apache License. See [LICENSE](LICENSE) for details.
