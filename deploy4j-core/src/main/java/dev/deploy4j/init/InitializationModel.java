package dev.deploy4j.init;

/**
 * Model for initialization templates
 * @param serviceName the name of the service to deploy
 */
public record InitializationModel(String serviceName) {
  
  /**
   * Get the service name, defaulting to "deploy4j-demo" if not provided
   */
  public String serviceName() {
    return serviceName != null && !serviceName.trim().isEmpty() 
      ? serviceName.trim() 
      : "deploy4j-demo";
  }
  
  /**
   * Get the image name, using the service name
   */
  public String imageName() {
    return serviceName();
  }
}
