docker run -d -p 2222:22 --name deploy4j-droplet -v "%USERPROFILE%\.ssh\id_rsa.pub":/tmp/authorized_keys:ro -v /var/run/docker.sock:/var/run/docker.sock teggr/deploy4j-docker-droplet:latest
