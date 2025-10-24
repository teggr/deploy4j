package dev.deploy4j.deploy.configuration;

import java.util.Collection;
import java.util.Map;

public class ConfigurationPrinter {

  public void print(Configuration configuration) {

    Map<String, Object> config = configuration.resolve();

    StringBuilder sb = new StringBuilder();
    try {
      printValue(sb, config, 0);
      System.out.println(sb.toString());
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println(config);
    }

  }


  private void printValue(StringBuilder sb, Object value, int indent) {
    if (value == null) {
      sb.append("null");
      return;
    }

    if (value instanceof Map<?, ?>) {
      Map<?, ?> m = (Map<?, ?>) value;
      sb.append("{\n");
      m.keySet().stream().map(Object::toString).sorted().forEach(k -> {
        Object v = m.get(k);
        indent(sb, indent + 2);
        sb.append(k).append(": ");
        printValue(sb, v, indent + 2);
        sb.append("\n");
      });
      indent(sb, indent);
      sb.append("}");
      return;
    }

    if (value instanceof Collection<?>) {
      Collection<?> c = (Collection<?>) value;
      sb.append("[\n");
      for (Object o : c) {
        indent(sb, indent + 2);
        printValue(sb, o, indent + 2);
        sb.append(",\n");
      }
      if (!c.isEmpty()) {
        sb.setLength(sb.length() - 2); // remove trailing comma+newline
        sb.append("\n");
      }
      indent(sb, indent);
      sb.append("]");
      return;
    }

    if (value.getClass().isArray()) {
      int len = java.lang.reflect.Array.getLength(value);
      sb.append("[\n");
      for (int i = 0; i < len; i++) {
        indent(sb, indent + 2);
        printValue(sb, java.lang.reflect.Array.get(value, i), indent + 2);
        sb.append(",\n");
      }
      if (len > 0) {
        sb.setLength(sb.length() - 2);
        sb.append("\n");
      }
      indent(sb, indent);
      sb.append("]");
      return;
    }

    if (value instanceof String) {
      sb.append('"').append(value).append('"');
      return;
    }

    sb.append(value.toString());
  }

  private void indent(StringBuilder sb, int n) {
    for (int i = 0; i < n; i++) {
      sb.append(' ');
    }
  }


}
