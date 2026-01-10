---
layout: page
title: Kamal
aside: 
  group: kamal
  index: true
---

## Acknowledgment

Deploy4j is a Java port of the excellent [Kamal](https://kamal-deploy.org/) project, originally developed by the talented team at [Basecamp](https://basecamp.com/) (formerly known as MRSK). We are deeply grateful for their pioneering work in simplifying web application deployments to self-hosted infrastructure.

## Why a Java Port?

The original Kamal project is built with Ruby and is tailored for the Ruby on Rails ecosystem. We created deploy4j to bring the same deployment simplicity to the Java development community. By porting Kamal's battle-tested deployment patterns to Java, we aim to:

* **Leverage existing code**: Build upon Kamal's proven architecture and deployment strategies
* **Serve the Java ecosystem**: Provide Java developers with a native deployment tool that feels familiar
* **Add Java-focused features**: Extend the core functionality with features specifically useful for Java applications
* **Maintain compatibility**: Keep the configuration and workflow similar to Kamal where it makes sense

## Port Status

Deploy4j is currently based on [Kamal v1](https://kamal-deploy.org/v1/docs/installation/). We are actively evaluating v2 changes and will decide on porting those enhancements based on community needs and Java-specific requirements.

### Features Intentionally Not Ported

Some Kamal features were intentionally left out or delayed in the initial port:

* **Building**: We deliberately delegate building to Java's existing ecosystem tools (Maven, Gradle, etc.) rather than reimplementing build functionality. This allows developers to use their preferred build tools and configurations.

* **Asset Bridging**: This feature, which helps with zero-downtime asset deployments, is not currently supported but may be added in the future based on community feedback. See the [discussion on GitHub](https://github.com/teggr/deploy4j/discussions/57) for more details.

* **Maintenance Mode**: Not yet implemented in deploy4j. This is a potential future enhancement.

## Future Direction

As deploy4j matures, we plan to:

1. **Maintain Kamal v1 as the baseline**: Continue using v1's stable foundation
2. **Add Java-specific enhancements**: Introduce features that specifically benefit Java applications (Spring Boot integration, JVM optimization, etc.)
3. **Selective v2 adoption**: Evaluate and potentially adopt relevant features from Kamal v2
4. **Community-driven development**: Let user feedback guide our feature priorities

## Contributing and Discussion

We welcome your feedback, questions, and feature requests! Please join the conversation on our [GitHub Discussions](https://github.com/teggr/deploy4j/discussions) page.

If you're familiar with Kamal and have suggestions for the port, or if you'd like to contribute, we'd love to hear from you.

## Resources

* [Kamal Official Website](https://kamal-deploy.org/)
* [Kamal v1 Documentation](https://kamal-deploy.org/v1/docs/installation/)
* [Deploy4j GitHub Discussions](https://github.com/teggr/deploy4j/discussions)
* [Deploy4j GitHub Repository](https://github.com/teggr/deploy4j)
