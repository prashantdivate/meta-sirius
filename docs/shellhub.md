# ShellHub Agent Integration for Yocto

This layer provides **ShellHub agent** integration for Yocto-based embedded Linux devices.

ShellHub enables secure reverse SSH access to remote devices without requiring public IP addresses, VPN setup, manual port forwarding, or direct inbound SSH exposure.

This is especially useful for IoT devices and embedded Linux systems deployed behind:

* NAT networks
* Firewalls
* Cellular routers
* Customer networks
* Remote field installations

## Overview

Managing and remotely accessing large-scale IoT deployments is one of the biggest operational challenges in embedded Linux systems.

Traditional SSH access usually requires one or more of the following:

* Static public IP address
* Port forwarding
* VPN configuration
* Manual network setup
* Customer-side firewall changes

These requirements are often impractical for field-deployed embedded devices.

ShellHub solves this problem using a reverse SSH model. The device runs a lightweight `shellhub-agent` service that establishes an outbound connection to the ShellHub server. After the device is approved in the ShellHub dashboard, engineers can securely access the device remotely.

In this layer, the ShellHub agent is integrated directly into the Yocto image so that devices can automatically register after first boot.

---

## Why integrate ShellHub into Yocto?

Integrating ShellHub directly into the Yocto build process provides a clean and scalable deployment workflow.

Instead of manually installing and configuring ShellHub on each device, the required agent, configuration, and systemd service are included in the generated root filesystem.

This enables:

* Zero-touch provisioning
* Automated device onboarding
* Secure reverse SSH access
* Remote diagnostics
* Field support
* Fleet-scale device management
* Reduced need for manual installation steps
* Better consistency across production images

Once a device is flashed, shipped, powered on, and connected to the internet, it can automatically register with the configured ShellHub namespace.

---

## Features

* Native ShellHub agent support for Yocto images
* systemd-managed `shellhub-agent` service
* Build-time tenant ID injection
* Optional custom ShellHub server address support
* Automatic device registration on boot
* Secure reverse SSH access
* Works behind NAT and firewalls
* Suitable for IoT and field-deployed embedded devices
* Supports cloud-hosted or self-hosted ShellHub deployments

---

## Layer support

The ShellHub integration is available under:

```text
recipes-core/
└── shellhub/
```

Typical contents may include:

```text
recipes-core/shellhub/
├── shellhub-agent/
├── shellhub-agent_%.bb
├── files/
│   ├── shellhub-agent.service
│   └── shellhub-agent.default
```

The exact file names may vary depending on the implementation in this layer.

---

## Architecture

The basic architecture is:

```text
+----------------------+        outbound connection        +----------------------+
| Yocto target device  |  ------------------------------>  | ShellHub server      |
|                      |                                   | Cloud or self-hosted |
| shellhub-agent       |                                   |                      |
| systemd service      |                                   | Device dashboard     |
+----------------------+                                   +----------------------+
          ^                                                           |
          |                                                           |
          |                  secure SSH session                       |
          +-----------------------------------------------------------+
                            from engineer workstation
```

The device does not need to expose an inbound SSH port to the public internet.

Instead:

1. The target boots.
2. `shellhub-agent` starts through systemd.
3. The agent reads its environment configuration.
4. The agent connects to the configured ShellHub server.
5. The device appears in the ShellHub dashboard.
6. The device is approved by an administrator.
7. Engineers can connect through ShellHub using SSH or the web console.

---

## Build-time configuration

To enable zero-touch provisioning, configure the ShellHub tenant information before building the image.

Add the following variables to your `conf/local.conf` or distro configuration.

### ShellHub Cloud

```conf
SHELLHUB_TENANT_ID = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
```

### Self-hosted ShellHub server

```conf
SHELLHUB_TENANT_ID = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
SHELLHUB_SERVER_ADDRESS = "http://your-shellhub-server-ip-or-domain"
```

Replace the values with your actual ShellHub tenant ID and server address.

> The tenant ID identifies the ShellHub namespace or organization where the device should register.

---

## Getting the ShellHub tenant ID

To get the tenant ID:

1. Log in to the ShellHub dashboard.
2. Create or select a namespace.
3. Open the device onboarding instructions.
4. Copy the tenant ID from the provided onboarding command or configuration.
5. Add the tenant ID to your Yocto build configuration.

Example:

```conf
SHELLHUB_TENANT_ID = "1234567890abcdef1234567890abcdef"
```

The tenant ID should be configured before running `bitbake`.

---

## Build the image

After configuring the ShellHub variables, build your image.

Example:

```bash
bitbake sirius-dev-image
```

Or build the SD-card image:

```bash
bitbake sirius-sd-card-image
```

After flashing and booting the target, the ShellHub agent should start automatically and attempt to register the device.

---

## Runtime configuration

The ShellHub agent configuration is deployed to the target filesystem.

A typical runtime environment file is:

```text
/etc/default/shellhub-agent
```

Verify it on the target:

```bash
cat /etc/default/shellhub-agent
```

Expected values may include:

```text
SHELLHUB_TENANT_ID=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
SHELLHUB_SERVER_ADDRESS=https://cloud.shellhub.io
```

For a self-hosted server, the server address should match the value configured during the Yocto build.

---

## systemd service

The ShellHub agent is managed by systemd.

Check service status:

```bash
systemctl status shellhub-agent
```

Start the service manually:

```bash
systemctl start shellhub-agent
```

Restart the service:

```bash
systemctl restart shellhub-agent
```

Enable the service at boot:

```bash
systemctl enable shellhub-agent
```

View logs:

```bash
journalctl -u shellhub-agent
```

Follow live logs:

```bash
journalctl -u shellhub-agent -f
```

---

## Device approval

For security reasons, newly registered devices may appear in a pending or quarantined state.

To approve a device:

1. Open the ShellHub dashboard.
2. Go to the device list or pending devices section.
3. Locate the newly registered target device.
4. Review the device identity.
5. Click **Accept** or **Approve**.

After approval, the device becomes available for remote access.

---

## Remote access

Once the device is approved, you can access it through ShellHub.

ShellHub typically supports:

* Native SSH from a local terminal
* Browser-based terminal from the dashboard

### Native SSH

Use the SSH command provided by the ShellHub dashboard.

Generic format:

```bash
ssh <device-user>@<device-sshid> -p <gateway-port>
```

Example format for a cloud deployment:

```bash
ssh root@<namespace>.<hostname>@cloud.shellhub.io
```

Example format for a self-hosted deployment:

```bash
ssh root@<device-sshid> -p 2222
```

The exact command depends on your ShellHub deployment mode and dashboard configuration.

### Browser terminal

You can also connect from the ShellHub web dashboard using the built-in browser terminal.

This is useful for:

* Quick diagnostics
* Browser-only access
* Emergency support
* Remote field debugging

Use the target device Linux credentials when prompted.

---

## Fleet deployment workflow

A typical production deployment flow looks like this:

1. Create a ShellHub namespace.
2. Retrieve the tenant ID.
3. Add the tenant ID to Yocto configuration.
4. Build the production image.
5. Flash the image to devices.
6. Ship devices to the field.
7. Devices boot and connect to the internet.
8. `shellhub-agent` starts automatically.
9. Devices register with the ShellHub server.
10. Devices are approved manually or through automation.
11. Engineers access approved devices remotely.

This provides a scalable zero-touch onboarding model for embedded Linux fleets.

---

## Automated approval

For large fleets, manually approving every device can become time-consuming.

ShellHub provides APIs that can be used to automate approval workflows. A management server can periodically check for pending devices and approve them based on your internal policy.

Example automation concept:

```bash
#!/bin/bash

SERVER="http://localhost"
API_TOKEN="your_namespace_api_token"

PENDING_DEVICES=$(curl -s -H "Authorization: Bearer ${API_TOKEN}" \
  "${SERVER}/api/devices?status=pending")

for uid in $(echo "${PENDING_DEVICES}" | jq -r '.[].uid'); do
    curl -s -X PUT \
      -H "Authorization: Bearer ${API_TOKEN}" \
      "${SERVER}/api/devices/${uid}/accept"

    echo "Automatically approved device UID: ${uid}"
done
```

> Production approval automation should include strong validation, audit logging, and access control. Do not blindly approve unknown devices in sensitive environments.

---

## Verification

Check that the service is installed:

```bash
systemctl list-unit-files | grep shellhub
```

Check service status:

```bash
systemctl status shellhub-agent
```

Check logs:

```bash
journalctl -u shellhub-agent
```

Check runtime configuration:

```bash
cat /etc/default/shellhub-agent
```

Check network connectivity:

```bash
ping cloud.shellhub.io
```

For self-hosted deployments:

```bash
ping <your-shellhub-server>
```

---

## Troubleshooting

### Device does not appear in ShellHub dashboard

Check the following:

* `SHELLHUB_TENANT_ID` is configured correctly
* `SHELLHUB_SERVER_ADDRESS` is correct
* Target has internet access
* DNS is working on the target
* `shellhub-agent` service is running
* System time is valid
* Firewall rules allow outbound connectivity

Useful commands:

```bash
systemctl status shellhub-agent
journalctl -u shellhub-agent -f
cat /etc/default/shellhub-agent
ip addr
ip route
cat /etc/resolv.conf
```

### Service is not starting

Check systemd logs:

```bash
journalctl -u shellhub-agent
```

Check whether the binary exists:

```bash
which shellhub-agent
```

Or:

```bash
find /usr -name "shellhub-agent"
```

Restart the service:

```bash
systemctl restart shellhub-agent
```

### Wrong tenant ID

If the wrong tenant ID was baked into the image, the device may register to the wrong namespace or fail to register.

Update the Yocto configuration:

```conf
SHELLHUB_TENANT_ID = "correct_tenant_id"
```

Then rebuild and redeploy the image.

### Self-hosted server not reachable

Verify the configured server address:

```bash
cat /etc/default/shellhub-agent
```

Test connectivity:

```bash
ping <your-shellhub-server>
curl -I <your-shellhub-server>
```

Check that the target network can reach the ShellHub server.

---

## Security notes

ShellHub is intended to provide secure remote access, but production deployments should still follow strong security practices.

Recommended practices:

* Use unique credentials per device
* Avoid default root passwords
* Disable unnecessary users
* Restrict dashboard access
* Use strong administrator authentication
* Use role-based access control where available
* Review and audit device access logs
* Prefer self-hosted deployment when required by data policy
* Protect API tokens used for automated approval
* Avoid blindly auto-approving unknown devices
* Keep the agent and server updated

For high-security environments, evaluate whether ShellHub Cloud or a self-hosted ShellHub deployment better fits your requirements.

---

## Recommended production settings

For production or field deployments:

* Configure `SHELLHUB_TENANT_ID` in a distro or production config layer
* Avoid hardcoding secrets in public repositories
* Use separate tenants/namespaces for development, staging, and production
* Add CI checks to prevent placeholder tenant IDs from shipping
* Track the ShellHub agent version used in the image
* Document the approval workflow for operations teams
* Include a recovery path if a device registers to the wrong namespace

Example environment split:

```text
development   -> ShellHub development namespace
staging       -> ShellHub staging namespace
production    -> ShellHub production namespace
```

---

## Example Yocto configuration

Development example:

```conf
SHELLHUB_TENANT_ID = "dev-tenant-id"
SHELLHUB_SERVER_ADDRESS = "https://cloud.shellhub.io"
```

Production self-hosted example:

```conf
SHELLHUB_TENANT_ID = "prod-tenant-id"
SHELLHUB_SERVER_ADDRESS = "https://shellhub.example.com"
```

Then build:

```bash
bitbake sirius-dev-image
```

---

## Notes

* The ShellHub agent runs as a native systemd-managed service.
* This avoids manually running container-based installation commands on each target.
* Devices can register automatically during first boot.
* Newly registered devices should be approved intentionally.
* Remote SSH access works even when devices are behind NAT or firewalls.
* The exact SSH command is provided by the ShellHub dashboard after device approval.

---

## Reference

This implementation was inspired by the following article:

[Scaling IoT fleets: Secure Reverse SSH into Yocto for Automated Field Deployment](https://medium.com/@prashant-divate/scaling-iot-fleets-secure-reverse-ssh-into-yocto-for-automated-field-deployment-df27d54be7f3)

