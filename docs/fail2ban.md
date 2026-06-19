# SSH Brute Force Protection (Fail2Ban + nftables)

This layer provides SSH brute-force protection using **Fail2Ban** and **nftables** for systems running **OpenSSH**.

## Overview

Fail2Ban monitors SSH authentication failures and dynamically updates nftables rules to block IP addresses that exceed a configured number of failed login attempts.

Blocked IP addresses are added to a dedicated nftables set (`f2b_sshd`) and are automatically removed after the configured ban period expires.

## Features

* OpenSSH integration
* Automatic detection of failed SSH login attempts
* Dynamic IP banning using nftables
* Automatic unban after configurable timeout
* Lightweight implementation suitable for embedded Linux devices
* Systemd service integration

## Configuration

Default configuration:

```ini
[DEFAULT]
ignoreip = 127.0.0.1/8 ::1
bantime = 900
findtime = 600
maxretry = 2
backend = polling
banaction = nftables-sshd

[sshd]
enabled = true
port = 22
filter = sshd-custom
logpath = /var/log/messages
```

| Parameter  | Description                                                     |
| ---------- | --------------------------------------------------------------- |
| `maxretry` | Number of failed login attempts allowed before a ban is applied |
| `findtime` | Time window (seconds) in which failed attempts are counted      |
| `bantime`  | Duration (seconds) an IP address remains blocked                |
| `ignoreip` | IP addresses or networks excluded from banning                  |

## Ban Behaviour

Example:

1. User enters an incorrect password twice.
2. Fail2Ban detects the failed authentication attempts.
3. The source IP address is added to the `f2b_sshd` nftables set.
4. Any new SSH connections from that IP address are blocked.
5. After the configured `bantime` expires, the IP address is automatically removed from the nftables set.

No manual intervention is required for unbanning.

## Verification

Check Fail2Ban status:

```bash
fail2ban-client status
fail2ban-client status sshd
```

Display currently banned IP addresses:

```bash
nft list set inet filter f2b_sshd
```

View active nftables rules:

```bash
nft list ruleset
```

## Manual Unban

To manually remove a banned IP address:

```bash
fail2ban-client set sshd unbanip <IP_ADDRESS>
```

Example:

```bash
fail2ban-client set sshd unbanip 192.168.1.100
```

## Recommended Production Settings

For field-deployed devices, the following settings provide a balance between security and serviceability:

```ini
maxretry = 5
findtime = 600
bantime = 300
```

This configuration allows five failed login attempts within ten minutes and applies a temporary five-minute ban before automatically restoring access.

## Notes

* OpenSSH is used instead of Dropbear.
* SSH access is provided through the systemd `sshd.socket` unit.
* Existing SSH sessions are not terminated when an IP address becomes banned; the ban applies to new incoming connections.
* Fail2Ban uses log monitoring (`/var/log/messages`) to detect authentication failures.

