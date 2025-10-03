FROM ubuntu:22.04

# Install SSH server and some common server utilities
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
    && rm -rf /var/lib/apt/lists/*

# Configure SSH
RUN mkdir /var/run/sshd && \
    mkdir -p /root/.ssh

# Copy your public key into root's authorized_keys
COPY id_rsa.pub /root/.ssh/authorized_keys

RUN chmod 600 /root/.ssh/authorized_keys && \
    chown root:root /root/.ssh/authorized_keys

# Expose SSH port
EXPOSE 22

# Start SSHD in foreground
CMD ["/usr/sbin/sshd", "-D"]
