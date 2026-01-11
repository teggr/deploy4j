package dev.deploy4j.init;

/**
 * Model for initialization templates
 */
public class InitializationModel {
  
  private String serviceName = "deploy4j-demo";
  
  /**
   * Create a new InitializationModel with default values
   */
  public InitializationModel() {
  }
  
  /**
   * Set the service name
   * @param serviceName the name of the service to deploy
   * @return this model for chaining
   */
  public InitializationModel serviceName(String serviceName) {
    if (serviceName != null && !serviceName.trim().isEmpty()) {
      this.serviceName = serviceName.trim();
    }
    return this;
  }
  
  /**
   * Get the service name
   * @return the service name
   */
  public String serviceName() {
    return serviceName;
  }
  
  /**
   * Get the image name, using the service name
   * @return the image name
   */
  public String imageName() {
    return serviceName;
  }
}
