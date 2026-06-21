# Remotely Access Wayland Applications Using RDP

This layer provides support for remotely accessing **Wayland-based applications** using an **RDP client** in a Yocto image.

The implementation is based on **Weston screen sharing** with the **RDP backend**, allowing a host machine to connect to the target device and view Wayland applications remotely.

## Overview

Wayland applications normally render directly on the target device display through a compositor such as Weston. For development, debugging, demos, and remote access use cases, it is useful to access these applications from another machine.

meta-sirius enables this workflow by extending Weston with RDP-related support. Once configured, the target device can expose a remote RDP session that can be accessed from a host machine using clients such as:

* `wlfreerdp`
* `xfreerdp`
* Remmina

This is useful for:

* Remotely viewing Wayland applications
* Debugging graphical applications
* Accessing display output from headless or remote devices
* Demonstrating embedded GUI applications without a physical monitor
* Testing Weston and Wayland integration in a Yocto image

---

## Features

* Wayland application remote access
* Weston RDP backend support
* Weston screen sharing support
* FreeRDP client compatibility
* Support for Wayland and X11 host clients
* RDP certificate-based connection setup
* Suitable for Yocto-based embedded Linux images

---

## Layer support

The Weston configuration is extended in this layer under:

```text
recipes-graphics/
└── wayland/
```

The Weston append enables RDP-related support through Weston `PACKAGECONFIG`.

Typical enabled features include:

```text
rdp
fbdev
launch
```

Depending on the Yocto release and Weston version, the exact package configuration may vary.

---

## Runtime requirements

The target image should include the required Weston and FreeRDP components.

Typical target-side requirements:

* Weston
* Weston RDP backend
* Weston screen sharing module
* FreeRDP/winpr certificate utility
* systemd runtime support
* Valid RDP TLS certificate and key

The host machine should have an RDP client installed.

For Wayland hosts:

```bash
wlfreerdp /v:<target-ip>
```

For X11 hosts:

```bash
xfreerdp /v:<target-ip>
```

You can also use Remmina or another RDP client.

---

## Certificate setup

The Weston RDP backend requires a TLS certificate and key.

On the target device, create the certificate directory:

```bash
mkdir -p /etc/freerdp/keys
```

Generate the RDP certificate and key:

```bash
winpr-makecert -rdp -path /etc/freerdp/keys
```

Expected files:

```text
/etc/freerdp/keys/server.crt
/etc/freerdp/keys/server.key
```

Verify that the files exist:

```bash
ls -l /etc/freerdp/keys/
```

---

## Weston configuration

Add the following configuration to `weston.ini`.

Typical location:

```text
/etc/xdg/weston/weston.ini
```

Example configuration:

```ini
[core]
modules=systemd-notify.so

[screen-share]
command=/usr/bin/weston --backend=rdp-backend.so --shell=fullscreen-shell.so --rdp-tls-cert=/etc/freerdp/keys/server.crt --rdp-tls-key=/etc/freerdp/keys/server.key --no-clients-resize
start-on-startup=true
```

The `[screen-share]` section starts a secondary Weston instance using the RDP backend. This allows the compositor output to be shared with a remote RDP client.

---

## Launch Weston

Start Weston on the target device.

Example:

```bash
weston --debug --backend=fbdev-backend.so --modules=systemd-notify.so,screen-share.so
```

If your system uses a different backend or device configuration, adjust the command accordingly.

For example, systems with DRM/KMS support may use a DRM backend instead of fbdev.

---

## Connect from host

After Weston starts successfully on the target, connect from the host machine.

Replace `<target-ip>` with the IP address of your target device.

### From a Wayland host

```bash
wlfreerdp /v:<target-ip>
```

### From an X11 host

```bash
xfreerdp /v:<target-ip>
```

### Using Remmina

You can also connect using Remmina:

1. Open Remmina.
2. Create a new RDP connection.
3. Enter the target IP address.
4. Select RDP as the protocol.
5. Connect to the target.

---

## Verification

Check that Weston is running:

```bash
ps aux | grep weston
```

Check Weston logs:

```bash
journalctl -u weston
```

Or, if Weston was launched manually:

```bash
cat ${XDG_RUNTIME_DIR}/weston.log
```

Check whether the target is listening for RDP connections:

```bash
netstat -tulnp | grep 3389
```

Or:

```bash
ss -tulnp | grep 3389
```

---

## Troubleshooting

### RDP client cannot connect

Check the following:

* Target IP address is reachable
* Weston is running
* RDP backend is enabled in Weston
* RDP certificate and key exist
* Firewall rules are not blocking the connection
* Port `3389` is open on the target

Test target connectivity:

```bash
ping <target-ip>
```

Check listening ports:

```bash
ss -tulnp
```

### Certificate error

Regenerate the certificate and key:

```bash
rm -rf /etc/freerdp/keys
mkdir -p /etc/freerdp/keys
winpr-makecert -rdp -path /etc/freerdp/keys
```

Then restart Weston.

### Weston fails to start

Run Weston with debug output:

```bash
weston --debug --backend=fbdev-backend.so --modules=systemd-notify.so,screen-share.so
```

Check whether the required backend modules are available:

```bash
find /usr/lib* -name "*rdp*"
find /usr/lib* -name "*screen-share*"
```

### Existing display backend is different

The example uses `fbdev-backend.so`. Some platforms may require a different backend, such as:

```text
drm-backend.so
wayland-backend.so
headless-backend.so
```

Adjust the Weston command based on your device, GPU, display stack, and Yocto configuration.

---

## Security notes

RDP access exposes a graphical remote access interface to the network.

For production devices, consider:

* Restricting RDP access to trusted networks
* Using firewall rules
* Avoiding public exposure of port `3389`
* Rotating certificates where required
* Using VPN or secure tunneling for remote access
* Disabling RDP support in production images if not needed

---

## Example workflow

1. Build an image with Weston RDP support.
2. Boot the target device.
3. Generate RDP certificate and key:

```bash
winpr-makecert -rdp -path /etc/freerdp/keys
```

4. Configure `weston.ini`.
5. Start Weston with screen sharing enabled.
6. Connect from the host:

```bash
wlfreerdp /v:<target-ip>
```

7. Launch or view Wayland applications remotely.

---

## Reference

This implementation was inspired by the following article:

[Remotely Accessing Wayland Based Applications Using RDP Client in Yocto Image](https://medium.com/@prashant-divate/remotely-accessing-wayland-based-applications-using-rdp-client-in-yocto-image-55f38b67e0aa)

