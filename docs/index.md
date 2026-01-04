---
layout: default
---

<section class="hero hero-grid">
    <div class="hero-body">
        <div class="container has-text-centered">
            <h1 class="title">
                Deploy web apps anywhere.
            </h1>
            <h2 class="subtitle">
                From bare metal to cloud VMs.
            </h2>
            <p class="subtitle">Zero-downtime deploys for containerized web applications</p>
            <div class="hero-cta-buttons">
                <a class="button is-primary is-large" href="{{ '/installation' | relative_url }}">
                    <strong>Get Started</strong>
                </a>
                {% assign confIndexPage = site.configuration | where: 'default', true | first %}
                <a class="button is-outlined is-large" href="{{ confIndexPage.url | default: '/installation' | relative_url }}">
                    Read Documentation
                </a>
                {% assign guidesIndexPage = site.guides | where: 'default', true | first %}
                <a class="button is-outlined is-large" href="{{ guidesIndexPage.url | default: '/guides' | relative_url }}">
                    View Guides
                </a>
            </div>
        </div>
    </div>
</section>

<section class="features-section">
    <div class="container">
        <h2 class="title has-text-centered section-title-large">Why deploy4j?</h2>
        <p class="subtitle has-text-centered section-subtitle-spaced">Everything you need to deploy and manage web applications in production</p>
        
        <div class="features-grid">
            <div class="feature-card">
                <h3>🚀 Zero-Downtime Deploys</h3>
                <p>Rolling restarts ensure your application stays available during deployments, eliminating service interruptions.</p>
            </div>
            
            <div class="feature-card">
                <h3>🐳 Docker-Native</h3>
                <p>Built on Docker from the ground up. Containerize once, deploy anywhere with consistent environments.</p>
            </div>
            
            <div class="feature-card">
                <h3>💰 Cost-Effective</h3>
                <p>Deploy to self-hosted VMs starting at $6/month. No expensive PaaS fees or vendor lock-in.</p>
            </div>
            
            <div class="feature-card">
                <h3>⚙️ Simple Configuration</h3>
                <p>Single YAML file configuration. Define servers, images, and environment variables in one place.</p>
            </div>
            
            <div class="feature-card">
                <h3>🔒 Secure by Default</h3>
                <p>SSH-based deployments with key authentication. Environment secrets managed with encrypted files.</p>
            </div>
            
            <div class="feature-card">
                <h3>🌐 Traefik Integration</h3>
                <p>Automatic reverse proxy setup with Traefik for routing and load balancing across your services.</p>
            </div>
            
            <div class="feature-card">
                <h3>🧩 Accessory Services</h3>
                <p>Manage databases, caches, and other services alongside your application with ease.</p>
            </div>
            
            <div class="feature-card">
                <h3>🔄 Rolling Restarts</h3>
                <p>Update your application across multiple servers with automatic health checks and rollback support.</p>
            </div>
        </div>
    </div>
</section>

<section class="section quick-start-section">
    <div class="container">
        <h2 class="title has-text-centered section-title-large-spaced">Quick Start</h2>
        <div class="content quick-start-content">
            <p>Get started with deploy4j in just a few commands:</p>
            <pre><code># Install deploy4j with JBang
jbang app install --name deploy4j dev.deploy4j:deploy4j-cli:0.0.3

# Initialize your project
deploy4j init

# Deploy your application
deploy4j setup</code></pre>
        </div>
        <div class="has-text-centered cta-spacing">
            <a class="button is-primary is-medium" href="{{ '/installation' | relative_url }}">
                <strong>View Full Documentation</strong>
            </a>
        </div>
    </div>
</section>
