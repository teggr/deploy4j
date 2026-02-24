package dev.deploy4j.cli;

import dev.deploy4j.deploy.DeployApplicationContext;
import dev.deploy4j.deploy.DeployContext;
import dev.deploy4j.deploy.Hooks;
import dev.deploy4j.deploy.configuration.Configuration;
import dev.deploy4j.deploy.host.ssh.SshHosts;
import dev.deploy4j.deploy.local.LocalHost;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import picocli.CommandLine;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

@CommandLine.Command(
  name = "mcp",
  description = "Start deploy4j in MCP server mode (stdio transport)")
public class McpCliCommand implements Callable<Integer> {

  @CommandLine.Mixin
  private HelpOptions helpOptions = new HelpOptions();

  @CommandLine.Option(names = {"-c", "--config-file"}, paramLabel = "CONFIG_FILE", description = "Path to config file. Default: config/deploy.yml", defaultValue = "config/deploy.yml")
  private String configFile;

  @CommandLine.Option(names = {"-d", "--destination"}, paramLabel = "DESTINATION", description = "Specify destination to be used for config file (staging -> deploy.staging.yml)")
  private String destination;

  @Override
  public Integer call() throws Exception {

    CountDownLatch stdinClosedLatch = new CountDownLatch(1);

    InputStream stdinMonitor = new FilterInputStream(System.in) {
      @Override
      public int read() throws IOException {
        int b = super.read();
        if (b == -1) stdinClosedLatch.countDown();
        return b;
      }

      @Override
      public int read(byte[] b, int off, int len) throws IOException {
        int result = super.read(b, off, len);
        if (result == -1) stdinClosedLatch.countDown();
        return result;
      }
    };

    StdioServerTransportProvider transportProvider = new StdioServerTransportProvider(
      new ObjectMapper(), stdinMonitor, System.out);

    McpSyncServer server = McpServer.sync(transportProvider)
      .serverInfo("deploy4j", "0.0.7")
      .capabilities(McpSchema.ServerCapabilities.builder()
        .tools(false)
        .build())
      .tool(
        new McpSchema.Tool(
          "deploy",
          "Deploy app to servers",
          new McpSchema.JsonSchema(
            "object",
            Map.of(
              "skipPull", Map.of("type", "boolean", "description", "Skip pulling the app image before deploying")
            ),
            List.of(),
            false
          )
        ),
        (exchange, args) -> {
          try {
            boolean skipPull = args.containsKey("skipPull") && Boolean.TRUE.equals(args.get("skipPull"));

            Configuration configuration = Configuration.createFrom(configFile, destination, null);
            DeployContext deployContext = new DeployContext(configuration, null, null, null);
            LocalHost localhost = new LocalHost();
            Hooks hooks = new Hooks(localhost, deployContext.config(), false);

            try (SshHosts sshHosts = new SshHosts(deployContext.config())) {
              DeployApplicationContext ctx = new DeployApplicationContext(sshHosts, hooks, localhost, deployContext);
              ctx.deploy().deploy(ctx.deployContext(), skipPull, false);
            }

            return McpSchema.CallToolResult.builder()
              .addTextContent("Deployment completed successfully")
              .build();
          } catch (Exception e) {
            return McpSchema.CallToolResult.builder()
              .addTextContent("Deployment failed: " + e.getMessage())
              .isError(true)
              .build();
          }
        }
      )
      .build();

    try {
      stdinClosedLatch.await();
    } finally {
      server.close();
    }

    return 0;

  }

}
