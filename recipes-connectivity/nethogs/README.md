# NetHogs - Per-Process Network Bandwidth Monitoring

NetHogs is a lightweight command-line network monitoring utility that groups
network traffic by **process/PID** instead of only showing traffic by interface,
protocol, or remote host.

It is useful on embedded Linux and Yocto-based systems when unexpected or high
network activity is observed and the responsible application needs to be
identified.

---

## Purpose

Typical interface-level tools can show that an Ethernet or Wi-Fi interface is
busy, but they do not always answer the most important question:

> Which process is consuming the network bandwidth?

NetHogs helps answer that question by displaying the transmit and receive
traffic associated with individual processes.

Example:

```text
PID     USER    PROGRAM                    DEV     SENT       RECEIVED
1234    root    /usr/bin/application-a     eth0    12.5 KB/s  2.1 KB/s
1450    root    /usr/bin/application-b     eth0     0.8 KB/s  8.6 KB/s
```

---

## Common Use Cases

NetHogs is useful for:

- Investigating unexpectedly high network usage.
- Identifying applications generating continuous background traffic.
- Finding applications uploading or downloading large amounts of data.
- Monitoring network activity during software updates.
- Checking telemetry, logging, cloud-agent, MQTT, HTTP, or HTTPS traffic.
- Investigating excessive network activity from long-running services.
- Comparing network usage between multiple applications.
- Debugging network usage from applications running for several hours.
- Correlating network spikes with a specific PID or process.
- Supporting field-device troubleshooting without requiring a full packet
  capture.

NetHogs is intended primarily as a **diagnostic/debugging utility**. It is not a
replacement for packet-capture tools such as `tcpdump` when protocol-level
analysis is required.

---

## Yocto Integration

A typical recipe requires the following build dependencies:

```bitbake
DEPENDS = "ncurses libpcap"
```

`libpcap` provides packet-capture functionality used by NetHogs, while
`ncurses` provides the interactive terminal interface.

To include NetHogs in an image:

```bitbake
IMAGE_INSTALL:append = " nethogs"
```

Yocto normally detects shared-library runtime dependencies automatically. For
example, if the NetHogs binary links against:

```text
libpcap.so.1
```

the package metadata should automatically contain a dependency on the runtime
package that provides that library.

Therefore, it is normally **not necessary to manually bundle `libpcap` inside
the NetHogs package**.

The expected package relationship is:

```text
nethogs
   |
   +-- runtime dependency --> libpcap
   +-- runtime dependency --> ncurses / tinfo
   +-- runtime dependency --> standard C/C++ runtime libraries
```

---

## Verify Runtime Dependencies

After installing NetHogs, verify that all shared libraries are available:

```sh
ldd "$(which nethogs)"
```

To display only missing libraries:

```sh
ldd "$(which nethogs)" | grep "not found"
```

A common missing dependency is:

```text
libpcap.so.1 => not found
```

In that case, install or include the `libpcap` runtime package generated from
the same Yocto build.

> `libcap` and `libpcap` are different libraries.
>
> - `libcap` provides Linux process capability support.
> - `libpcap` provides packet-capture support.
>
> NetHogs requires `libpcap`.

---

## Basic Usage

NetHogs normally requires root privileges because it captures network traffic.

Monitor all available interfaces:

```sh
nethogs
```

Monitor a specific interface:

```sh
nethogs eth0
```

For Wi-Fi:

```sh
nethogs wlan0
```

List available network interfaces:

```sh
ip -br link
```

---

## Understanding the Output

Typical output:

```text
PID     USER    PROGRAM                   DEV     SENT       RECEIVED
1234    root    /usr/bin/service-a        eth0    3.7 KB/s   0.9 KB/s
1560    root    /usr/bin/service-b        eth0    0.5 KB/s   2.6 KB/s
```

The important fields are:

| Field | Description |
|---|---|
| `PID` | Process ID |
| `USER` | User running the process |
| `PROGRAM` | Executable associated with the network traffic |
| `DEV` | Network interface |
| `SENT` | Transmit bandwidth or accumulated transmitted data |
| `RECEIVED` | Receive bandwidth or accumulated received data |

The displayed units depend on the selected NetHogs view mode.

---

## Instantaneous Bandwidth Monitoring

The default view is useful for finding which process is using bandwidth **right
now**:

```sh
nethogs eth0
```

Example:

```text
PROGRAM                 SENT        RECEIVED
/usr/bin/service-a      25 KB/s     4 KB/s
/usr/bin/service-b       2 KB/s    18 KB/s
```

This is useful for observing network spikes, but it does not by itself provide
a convenient historical report after the monitoring session has ended.

---

## Cumulative Data Monitoring

For investigations lasting several hours, cumulative data is usually more
useful than instantaneous KB/s.

NetHogs supports different display modes using `-v`.

A commonly useful cumulative view is:

```sh
nethogs -v 3 eth0
```

This displays accumulated traffic in MB for the current NetHogs session.

In interactive mode, the display mode can also be changed using:

```text
m
```

This allows switching between bandwidth-rate and accumulated-data views.

The accumulated counters are associated with the running NetHogs session.
Restarting NetHogs resets the session statistics.

---

## Long-Duration Monitoring

For a short investigation, NetHogs can simply remain open:

```sh
nethogs -v 3 eth0
```

For unattended monitoring, trace mode is more useful.

Example:

```sh
nethogs -t -v 3 -d 60 eth0
```

Where:

```text
-t       trace/non-interactive output
-v 3     cumulative MB view
-d 60    update every 60 seconds
```

This makes it possible to monitor a system over several hours without keeping
the interactive interface open.

---

## Logging to a File

To record NetHogs output:

```sh
nethogs -t -v 3 -d 60 eth0 > /var/log/nethogs.log 2>&1
```

Run it in the background:

```sh
nethogs -t -v 3 -d 60 eth0 > /var/log/nethogs.log 2>&1 &
```

Check whether it is running:

```sh
ps | grep nethogs
```

Follow the log:

```sh
tail -f /var/log/nethogs.log
```

Stop the monitor:

```sh
pkill nethogs
```

For temporary debugging where `/var/log` should not be used:

```sh
nethogs -t -v 3 -d 60 eth0 > /tmp/nethogs.log 2>&1 &
```

---

## Example Investigation Workflow

### 1. Check whether the interface is busy

```sh
ip -s link show eth0
```

or:

```sh
cat /sys/class/net/eth0/statistics/rx_bytes
cat /sys/class/net/eth0/statistics/tx_bytes
```

### 2. Identify the process using the bandwidth

```sh
nethogs eth0
```

### 3. Monitor accumulated usage

```sh
nethogs -v 3 eth0
```

### 4. Record activity for several hours

```sh
nethogs -t -v 3 -d 60 eth0 > /tmp/nethogs.log 2>&1 &
```

### 5. Inspect active sockets

TCP:

```sh
ss -tpn
```

UDP:

```sh
ss -upn
```

TCP and UDP:

```sh
ss -tupn
```

### 6. Perform packet-level investigation if required

```sh
tcpdump -i eth0
```

This provides a useful troubleshooting progression:

```text
Interface traffic
       |
       v
NetHogs process attribution
       |
       v
Socket/connection inspection
       |
       v
Packet capture if deeper analysis is required
```

---

## Measuring Total Interface Traffic

NetHogs is useful for process attribution, but kernel interface counters are
useful as the reference for total interface traffic.

Receive bytes:

```sh
cat /sys/class/net/eth0/statistics/rx_bytes
```

Transmit bytes:

```sh
cat /sys/class/net/eth0/statistics/tx_bytes
```

Example:

```sh
echo "Timestamp: $(date)"
echo "RX: $(cat /sys/class/net/eth0/statistics/rx_bytes)"
echo "TX: $(cat /sys/class/net/eth0/statistics/tx_bytes)"
```

Take the values before and after the test window to calculate the total traffic
seen by the interface.

This is useful when comparing NetHogs process attribution with actual interface
usage.

---

## NetHogs vs Other Network Tools

| Tool | Primary Purpose | Per-Process Traffic | Historical Logging |
|---|---|---:|---:|
| `nethogs` | Bandwidth by process/PID | Yes | Yes, using trace output |
| `iftop` | Bandwidth by connection/host | No | Limited |
| `ip -s link` | Interface counters | No | Manual |
| `ss` | Socket-to-process mapping | Yes | No bandwidth rate |
| `tcpdump` | Packet capture | Indirect | Yes |
| `bmon` | Interface bandwidth | No | Limited |

A useful combination is:

```text
nethogs   -> Who is using the network?
iftop     -> Which remote host is receiving/sending traffic?
ss        -> Which sockets belong to which process?
tcpdump   -> What exactly is being transmitted?
```

---

## Containerized Applications

When containers are used, host-side traffic attribution can sometimes be less
obvious because traffic may pass through virtual interfaces, namespaces,
bridges, or helper processes.

Useful commands include:

```sh
podman stats
```

or:

```sh
docker stats
```

To inspect processes inside a container:

```sh
podman top <container>
```

Active connections inside a container can also be inspected when the required
tools are available:

```sh
podman exec <container> ss -tupn
```

Use NetHogs together with the container runtime statistics when investigating
containerized workloads.

---

## Manual Package Installation

For development or testing, a generated `.deb` may be installed manually.

However, `dpkg` alone does not automatically download missing dependencies.

For example:

```sh
dpkg -i nethogs_<version>.deb
```

may fail if the runtime `libpcap` package is not already installed.

Using:

```sh
dpkg --force-depends -i nethogs_<version>.deb
```

forces installation but does **not** satisfy the missing dependencies.

If NetHogs later reports:

```text
error while loading shared libraries: libpcap.so.1:
cannot open shared object file
```

the correct solution is to install the matching Yocto `libpcap` runtime package
or include NetHogs directly in the image.

For production images, prefer:

```bitbake
IMAGE_INSTALL:append = " nethogs"
```

rather than manually installing individual packages on the target.

---

## Notes and Limitations

- NetHogs normally needs root privileges or suitable packet-capture
  capabilities.
- Statistics are maintained for the current NetHogs execution; restarting the
  utility resets the accumulated values.
- PID values can change when applications restart.
- Short-lived processes may be harder to observe than long-running services.
- Container networking can affect how traffic is attributed.
- NetHogs identifies traffic by process but does not replace protocol-level
  analysis.
- Kernel interface counters may include traffic that cannot be cleanly
  attributed to a userspace process.
- For security-sensitive debugging, be careful when saving packet captures or
  logs because they may contain addresses, hostnames, or other operational
  information.

---

## Quick Reference

```sh
# Monitor interface interactively
nethogs eth0

# Show cumulative usage
nethogs -v 3 eth0

# Trace mode, update every 60 seconds
nethogs -t -v 3 -d 60 eth0

# Save a long-running diagnostic log
nethogs -t -v 3 -d 60 eth0 > /tmp/nethogs.log 2>&1 &

# Find missing runtime libraries
ldd "$(which nethogs)" | grep "not found"

# Show TCP/UDP sockets and owning processes
ss -tupn

# Read total interface counters
cat /sys/class/net/eth0/statistics/rx_bytes
cat /sys/class/net/eth0/statistics/tx_bytes
```

---
