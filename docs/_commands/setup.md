---
layout: page
title: deploy4j setup
---
Deploy4j setup will run everything required to deploy an application to a fresh host.

It will:

1. Install Docker on all servers, if it has permission and it is not already installed
2. Push secrets files to the hosts
3. Boot all accessories
4. Deploy the app (see [deploy4j deploy]({{ site.baseurl }}/commands/deploy))