xcopy Dockerfile .\target\
xcopy %USERPROFILE%\.ssh\*.pub .\target\

docker build -t ubuntu-ssh .\target\