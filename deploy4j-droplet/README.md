# Running instructions

```shell
cd deploy4j-droplet
build-ssh.bat
run-ssh.bat

# connect via ssh
ssh -o StrictHostKeyChecking=no -p 2222 root@localhost 

# connect to shell
docker exec -it my-ubuntu-ssh /bin/bash
```