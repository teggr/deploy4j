---
layout: page
title: Server Provisioning Guide
short_title: Server Provisioning
---

This guide demonstrates how to quickly provision a server on popular cloud providers for use with deploy4j. We'll cover Digital Ocean, Hetzner, and AWS EC2, focusing on creating a typical 4GB Ubuntu server with SSH key authentication.

## Prerequisites

Before provisioning a server, ensure you have:

- An SSH key pair generated on your local machine
- Access to your cloud provider account
- Command-line tools installed (optional, but recommended):
  - Digital Ocean: `doctl`
  - Hetzner: `hcloud`
  - AWS: `aws-cli`

### Generate SSH Keys (if needed)

If you don't already have SSH keys, generate them:

```bash
ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
```

This creates `~/.ssh/id_rsa` (private key) and `~/.ssh/id_rsa.pub` (public key).

## Digital Ocean

Digital Ocean offers "Droplets" - virtual machines that can be provisioned quickly and cost-effectively.

### Target Configuration

- **Region**: Choose closest to your users (e.g., lon1, nyc1, sfo3)
- **OS**: Ubuntu 24.04 LTS x64
- **Authentication**: SSH keys

### Using the CLI (doctl)

https://docs.digitalocean.com/reference/doctl/

First, install and authenticate `doctl`:

```bash
# Install doctl (macOS)
brew install doctl

# Install doctl (Linux)
cd ~
wget https://github.com/digitalocean/doctl/releases/download/v1.104.0/doctl-1.104.0-linux-amd64.tar.gz
tar xf doctl-1.104.0-linux-amd64.tar.gz
sudo mv doctl /usr/local/bin

# Authenticate
doctl auth init
```

Upload your SSH key (one-time setup):

```bash
# Add your SSH key to Digital Ocean
doctl compute ssh-key import my-key --public-key-file ~/.ssh/id_rsa.pub
```

Create the droplet:

```bash
# List available sizes and regions
doctl compute size list
doctl compute region list

# Get your SSH key ID
doctl compute ssh-key list

# Create the droplet
doctl compute droplet create my-app-prod-01 \
  --image ubuntu-24-04-x64 \
  --size s-1vcpu-1gb \
  --region lon1 \
  --ssh-keys <your-ssh-key-id> \
  --wait

# Get the droplet IP address
doctl compute droplet list
```

### Verify SSH Connection

```bash
ssh root@<droplet-ip-address>
```

If successful, you're connected to your server. Type `exit` to disconnect.

## Hetzner Cloud

Hetzner offers competitive pricing with excellent performance, especially for European deployments.

### Target Configuration

- **Location**: Choose closest to your users (e.g., fsn1, nbg1, hel1)
- **OS**: Ubuntu 24.04
- **Authentication**: SSH keys

### Using the CLI (hcloud)

https://github.com/hetznercloud/cli/blob/main/docs/tutorials/setup-hcloud-cli.md

First, install and authenticate `hcloud`:

```bash
# Install hcloud (macOS)
brew install hcloud

# Install hcloud (Linux)
cd ~
wget https://github.com/hetznercloud/cli/releases/download/v1.42.0/hcloud-linux-amd64.tar.gz
tar xf hcloud-linux-amd64.tar.gz
sudo mv hcloud /usr/local/bin

# Authenticate (create API token in Hetzner Cloud Console first)
hcloud context create my-project
```

Upload your SSH key (one-time setup):

```bash
# Add your SSH key to Hetzner
hcloud ssh-key create --name my-key --public-key-from-file ~/.ssh/id_rsa.pub
```

Create the server:

```bash
# List available server types and locations
hcloud server-type list
hcloud location list

# Get your SSH key name
hcloud ssh-key list

# Create the server
hcloud server create \
  --name my-app-prod-01 \
  --type cpx21 \
  --image ubuntu-24.04 \
  --location fsn1 \
  --ssh-key my-key

# Get the server IP address
hcloud server list
```

### Verify SSH Connection

```bash
ssh root@<server-ip-address>
```

If successful, you're connected to your server. Type `exit` to disconnect.

## AWS EC2

AWS EC2 provides a wide range of instance types and global availability zones.

### Target Configuration

- **Region**: Choose closest to your users (e.g., us-east-1, eu-west-1)
- **AMI**: Ubuntu 24.04 LTS
- **Authentication**: SSH key pair

### Using the CLI (aws-cli)

https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html

First, install and authenticate AWS CLI:

```bash
# Install AWS CLI (macOS)
brew install awscli

# Install AWS CLI (Linux)
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# Configure AWS CLI
aws configure
# Enter your AWS Access Key ID, Secret Access Key, Default region, and output format
```

Create or import SSH key pair (one-time setup):

```bash
# Import your existing SSH public key
aws ec2 import-key-pair \
  --key-name my-key-pair \
  --public-key-material fileb://~/.ssh/id_rsa.pub \
  --region us-east-1
```

Create security group for SSH and HTTP/HTTPS access:

```bash
# Create security group
aws ec2 create-security-group \
  --group-name deploy4j-sg \
  --description "Security group for deploy4j applications" \
  --region us-east-1

# Get the security group ID from the output, then add rules
SG_ID=sg-xxxxxxxxx  # Replace with your security group ID

# Allow SSH
aws ec2 authorize-security-group-ingress \
  --group-id $SG_ID \
  --protocol tcp \
  --port 22 \
  --cidr 0.0.0.0/0 \
  --region us-east-1

# Allow HTTP
aws ec2 authorize-security-group-ingress \
  --group-id $SG_ID \
  --protocol tcp \
  --port 80 \
  --cidr 0.0.0.0/0 \
  --region us-east-1

# Allow HTTPS
aws ec2 authorize-security-group-ingress \
  --group-id $SG_ID \
  --protocol tcp \
  --port 443 \
  --cidr 0.0.0.0/0 \
  --region us-east-1
```

Get the Ubuntu 24.04 AMI ID for your region:

```bash
# Find Ubuntu 24.04 LTS AMI
aws ec2 describe-images \
  --owners 099720109477 \
  --filters "Name=name,Values=*ubuntu-noble-24.04-amd64-server-*" \
  --query 'Images[*].[ImageId,CreationDate,Name]' \
  --output table \
  --region us-east-1 | head -5

# Use the most recent AMI ID
```

Create the EC2 instance:

```bash
# Launch instance (replace AMI_ID and SG_ID with your values)
aws ec2 run-instances \
  --image-id ami-xxxxxxxxx \
  --instance-type t3.medium \
  --key-name my-key-pair \
  --security-group-ids sg-xxxxxxxxx \
  --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=my-app-prod-01}]' \
  --region us-east-1

# Get the instance public IP
aws ec2 describe-instances \
  --filters "Name=tag:Name,Values=my-app-prod-01" \
  --query 'Reservations[*].Instances[*].[PublicIpAddress,State.Name]' \
  --output table \
  --region us-east-1
```

### Verify SSH Connection

**Important**: AWS Ubuntu instances use the `ubuntu` user by default, not `root`.

```bash
ssh ubuntu@<server-ip-address>
```

If successful, you're connected to your server. Type `exit` to disconnect.

## Deploy with deploy4j

Once your server is provisioned and you can SSH into it, you're ready to deploy with deploy4j:

```bash
# Initialize deploy4j in your application directory
deploy4j init

# Edit config/deploy.yml with your server IP
# service: my-app
# image: my-registry/my-app:latest
# servers:
#   - <server-ip-address>

# Setup and deploy
deploy4j setup
```

The `deploy4j setup` command will:
1. Connect to your server via SSH
2. Install Docker (if not present)
3. Configure Gateway as a reverse proxy
4. Pull your application image
5. Start your application

## Comparison Table

> **Note**: Prices are approximate and subject to change. Check provider websites for current pricing.

| Provider | Instance Type | vCPUs | RAM | Storage | Monthly Cost (approx.) | Best For |
|----------|--------------|-------|-----|---------|----------------------|----------|
| **Digital Ocean** | s-2vcpu-4gb | 2 | 4GB | 80GB SSD | $24 | Simple setup, good documentation |
| **Hetzner** | CPX21 | 3 | 4GB | 80GB SSD | ~$9 | Best price/performance, Europe |
| **AWS EC2** | t3.medium | 2 | 4GB | Variable | ~$30 | Enterprise features, global reach |

## Tips and Best Practices

1. **Use SSH Keys**: Always use SSH key authentication instead of passwords
2. **Regular Backups**: Enable automated backups on your cloud provider
3. **Monitoring**: Set up basic monitoring (CPU, memory, disk) through your provider's dashboard
4. **Security Updates**: Keep your Ubuntu system updated with `apt update && apt upgrade`
5. **Firewall Rules**: Only open necessary ports (22 for SSH, 80/443 for web traffic)
6. **DNS Setup**: Point your domain to the server IP address using A records
7. **Multiple Environments**: Consider separate servers for development, staging, and production
8. **Cost Optimization**: Start small and scale up as needed; monitor your usage

## Troubleshooting

### Cannot SSH to Server

```bash
# Check if SSH port is open
nc -zv <server-ip> 22

# Check SSH key permissions
chmod 600 ~/.ssh/id_rsa
chmod 644 ~/.ssh/id_rsa.pub

# Try verbose SSH for debugging
ssh -v root@<server-ip>
```

### Wrong SSH User

If you get "Permission denied" errors:
- Digital Ocean and Hetzner use `root` by default
- AWS uses `ubuntu` by default for Ubuntu images
- Adjust your deploy4j configuration accordingly

### Firewall Blocking Connections

If you can SSH but can't access your web application:
- Check security group rules (AWS)
- Check firewall settings on the server (`sudo ufw status`)
- Verify Gateway is running: `docker ps | grep gateway`

## Next Steps

- [Installation Guide]({{ '/installation' | relative_url }}) - Install and configure deploy4j
- [Spring Boot Guide]({{ '/guides/spring-boot' | relative_url }}) - Deploy a Spring Boot application
- [Let's Encrypt Guide]({{ '/guides/lets-encrypt-certificate' | relative_url }}) - Configure SSL certificates
- [Configuration Overview]({{ '/configuration/overview' | relative_url }}) - Learn about deploy4j configuration options
