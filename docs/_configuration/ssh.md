---
layout: page
title: SSH configuration
---
deploy4j uses SSH to connect and run commands on your hosts. By default it will attempt to connect to the `root` user on port 22.

If you are using a non-root user, you may need to bootstrap your servers manually. On Ubuntu, for example:

```shell
sudo apt update
sudo apt upgrade -y
sudo apt install -y docker.io curl git
sudo usermod -a -G docker app
```

## SSH options

The options are specified under the `ssh` key in the configuration file. Example:

```yaml
ssh:

  # The SSH user
  #
  # Defaults to `root`
  #
  user: app

  # The SSH port
  #
  # Defaults to 22
  port: "2222"

  # Proxy host
  #
  # Specified in the form <host> or <user>@<host>
  proxy: root@proxy-host

  # Proxy command
  #
  # A custom proxy command, required for older versions of SSH
  proxy_command: "ssh -W %h:%p user@proxy"

  # Log level
  #
  # Defaults to `fatal`. Set this to debug if you are having
  # SSH connection issues.
  log_level: debug

  # SSH Key
  #
  # The path to the private key file for authentication.
  key_path: /path/to/user/.ssh/id_rsa

  # SSH Key Passphrase
  # 
    # The passphrase for the private key, if applicable.
  keys_pathphrase: #####

  # Strict Host Key Checking
  #
  # Whether to enable strict host key checking.
  strict_host_key_checking: true

  # Known Hosts Path
  #
  # The path to the known hosts file.
  known_hosts_path: /path/to/user/.ssh/known_hosts

```
