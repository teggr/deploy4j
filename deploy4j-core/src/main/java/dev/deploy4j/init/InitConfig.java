package dev.deploy4j.init;

import java.util.List;

public class InitConfig {

  public static final String DEFAULT_HOSTNAME = "localhost";

  public enum AgentType {
    COPILOT, CLAUDE, NONE
  }

  private final boolean bundle;
  private final String hostname;
  private final List<String> extraSecretNames;
  private final AgentType agentType;

  public InitConfig(boolean bundle, String hostname, List<String> extraSecretNames, AgentType agentType) {
    this.bundle = bundle;
    this.hostname = (hostname != null && !hostname.isBlank()) ? hostname : DEFAULT_HOSTNAME;
    this.extraSecretNames = extraSecretNames != null ? List.copyOf(extraSecretNames) : List.of();
    this.agentType = agentType != null ? agentType : AgentType.NONE;
  }

  public static InitConfig defaults(boolean bundle) {
    return new InitConfig(bundle, DEFAULT_HOSTNAME, List.of(), AgentType.NONE);
  }

  public boolean isBundle() {
    return bundle;
  }

  public String getHostname() {
    return hostname;
  }

  public List<String> getExtraSecretNames() {
    return extraSecretNames;
  }

  public AgentType getAgentType() {
    return agentType;
  }

}
