# Boxshare

[![Build Status](https://travis-ci.com/Vonathar/boxshare.svg?token=fUKSDpqhxEVNtDPifgjH&branch=master)](https://travis-ci.com/Vonathar/boxshare)
[![codecov](https://codecov.io/gh/Vonathar/boxshare/branch/master/graph/badge.svg?token=5I037Q3NOJ)](https://codecov.io/gh/Vonathar/boxshare)

Boxshare is an open source torrent streamer for local networks.

## Usage

### VPN

To run Boxshare, please make sure you are using a VPN. Most ISPs are actively blocking access to the
services used for scraping, so outbound requests by the application may fail.

As an alternative, if a VPN should not be available, you can try to change the URLs used to contact
each engine inside of the [application.properties](src/main/resources/application.properties) file
with any unblocked proxy URL.

### Run

You can run Boxshare by either double clicking the jar file, or from the terminal (recommended):

`java -jar boxshare-0.1.0.jar`

The application will start on port 8080 by default, but this can be reassigned by changing '
server.port' in the [application.properties](src/main/resources/application.properties) file. You
can check if the application is working by calling the ping endpoint:

`http://localhost:8080/ping`

### Search

`/search/<query>`

The _search_ endpoint finds are returns all available search results for a given query. It currently
supports three parsing modes:

- Quick (only returns the first result) _(default)_
- Smart (returns a limited number of results, and only considers torrent with a minimum number of
  seeders)
- Complete (returns everything)

### Android APK

The Boxshare Client APK is available for Android devices, providing a simple UI to query the server.
The APK is completely optional, as it was only created to make it easier to communicate with the
server remotely from an Android device such as a TV box.

### Documentation

You can find the complete documentation [here](https://vonathar.github.io/boxshare/).

## Piracy notice

This project is for educational purposes only. I do not condone piracy in any way, and I am not
responsible for anything that happens as a result of piracy arising from the use of this software.

You must own the copyright or have explicit permission from the owner of the copyright for any
content you view using Boxshare.

## License

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Distributed under the MIT License. See [LICENSE.md](LICENSE.md) for more information.