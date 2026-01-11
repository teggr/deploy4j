package dev.deploy4j.init;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Processes templates using Thymeleaf template engine
 */
public class TemplateProcessor {

  private final TemplateEngine templateEngine;

  public TemplateProcessor() {
    this.templateEngine = createTemplateEngine();
  }

  /**
   * Create and configure the Thymeleaf template engine
   */
  private TemplateEngine createTemplateEngine() {
    TemplateEngine engine = new TemplateEngine();
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setTemplateMode(TemplateMode.TEXT);
    templateResolver.setPrefix("templates/");
    templateResolver.setSuffix("");
    templateResolver.setCharacterEncoding("UTF-8");
    engine.setTemplateResolver(templateResolver);
    return engine;
  }

  /**
   * Process a template with the given model
   * @param templateName the name of the template file (e.g., "deploy.yml")
   * @param model the model object to use in the template context
   * @return the processed template content
   */
  public String processTemplate(String templateName, Object model) {
    Context context = new Context();
    context.setVariable("model", model);
    return templateEngine.process(templateName, context);
  }
}
