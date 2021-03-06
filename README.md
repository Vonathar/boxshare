# Boxshare

Boxshare is an open source torrent streamer for local networks.

## Mock data

The JSON data used for testing is intentionally not included in the repository for legal reasons.

To get a local copy, copy the output of the following command into _"src/test/resources/mock-search-results/test.json"_.

```shell
curl 'https://pirateproxy.ltda/newapi/q.php?q=test' \
  -H 'authority: pirateproxy.ltda' \
  -H 'accept: */*' \
  -H 'cookie: __cfduid=dcd49fb3477c3d2d92c353ef589f496c91615041571' \
  --compressed
```

Please make sure to read the [piracy notice](#piracy-notice) before accessing any copyrighted material.

## License

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Distributed under the MIT License. See [LICENSE.md](LICENSE.md) for more information.

## Piracy notice

I do not condone piracy in any way, and I am not responsible for anything that happens as a result of piracy arising
from the use of this software.

You must own the copyright or have explicit permission from the owner of the copyright for any content you view using
Boxshare.