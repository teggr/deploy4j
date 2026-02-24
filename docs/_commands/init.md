---
layout: page
title: deploy4j init
---
Interactively sets up the files needed to deploy your application with `deploy4j`.

The command walks you through a series of friendly questions to configure your deployment:

1. **Server hostname/IP** — the address of the server you'll be deploying to
2. **Secret variable names** — any environment variables your app needs at runtime
3. **AI agent skills** — optionally adds deploy4j deployment instructions to your AI coding assistant

For each file, the command checks whether it already exists and skips creation if so.

```shell
$ deploy4j init

👋 Welcome to deploy4j init!
   Let's get your project set up for deployment.

? Server IP address or hostname [localhost]: 192.168.1.100

? Secret environment variable names (comma-separated, press Enter to skip): DB_PASSWORD,API_KEY

? Detected GitHub Copilot — add deploy4j skills? [Y/n]: Y

📁 Setting up files...

[main] INFO dev.deploy4j.init.Initializer - Created configuration file in config/deploy.yml
[main] INFO dev.deploy4j.init.Initializer - Created secrets file in .deploy4j/secrets
[main] INFO dev.deploy4j.init.Initializer - Created hooks folder in .deploy4j/hooks
[main] INFO dev.deploy4j.init.Initializer - Added deploy4j skills to .github/copilot-instructions.md

✅ Done! Next steps:
   1. Review and edit config/deploy.yml
   2. Fill in your secrets in .deploy4j/secrets
   3. Run 'deploy4j setup <version>' for first-time deployment
```

## Files created

| File | Description |
|------|-------------|
| `config/deploy.yml` | Main deployment configuration (server, image, registry, SSH settings) |
| `.deploy4j/secrets` | Environment variable secrets for your application |
| `.deploy4j/hooks/` | Folder for deployment lifecycle hook scripts |
| `.github/copilot-instructions.md` | *(optional)* deploy4j skills for GitHub Copilot |
| `CLAUDE.md` | *(optional)* deploy4j skills for Claude |
