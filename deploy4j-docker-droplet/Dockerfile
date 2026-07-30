FROM ubuntu:24.04

# Avoid interactive prompts for packages
ENV DEBIAN_FRONTEND=noninteractive

# Update and install SSH server + curl (and CA certs for HTTPS)
RUN apt-get update && \
    apt-get install -y \
            openssh-server \
            sudo \
            coreutils \
            net-tools \
            iproute2 \
            vim \
            less \
            apt-utils \
            curl \
            ca-certificates \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Create SSH directory for root
RUN mkdir -p /root/.ssh && \
    chmod 700 /root/.ssh

# Configure SSH
RUN mkdir -p /var/run/sshd && \
    sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin prohibit-password/' /etc/ssh/sshd_config && \
    sed -i 's/#PubkeyAuthentication yes/PubkeyAuthentication yes/' /etc/ssh/sshd_config && \
    sed -i 's/#PasswordAuthentication yes/PasswordAuthentication no/' /etc/ssh/sshd_config

# Expose SSH port
EXPOSE 22

# Fix permissions for authorized_keys file on startup
# Create a startup script that copies the public key and starts sshd
RUN echo '#!/bin/bash\n\
if [ -f /tmp/authorized_keys ]; then\n\
    cp /tmp/authorized_keys /root/.ssh/authorized_keys\n\
    chown root:root /root/.ssh/authorized_keys\n\
    chmod 600 /root/.ssh/authorized_keys\n\
fi\n\
exec /usr/sbin/sshd -D' > /start.sh && \
    chmod +x /start.sh

# Start SSH service with permission fix
CMD ["/start.sh"]
