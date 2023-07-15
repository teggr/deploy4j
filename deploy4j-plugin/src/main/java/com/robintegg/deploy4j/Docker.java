package com.robintegg.deploy4j;

public class Docker {

  public static String install() {
    return "curl -fsSL https://get.docker.com | sh";
  }

  public static String installed() {
    return "docker -v";
  }

  public static String running() {
    return "docker -v";
  }

  public static String superUser() {
    return "[ \"${EUID:-$id -u)}\" -eq 0 ]";
  }

}
