# meta-sirius
![Ubuntu-ver](https://img.shields.io/badge/Ubuntu%20ver-16.04-lightpink.svg?style=plastic&logo=linux)
![python3.x](https://img.shields.io/badge/python-3.x-orange.svg?style=plastic)
![languages](https://img.shields.io/badge/Supported%20lang-c,shell,bitbake-blue.svg?style=plastic)
![star](https://img.shields.io/static/v1?label=%F0%9F%8C%9F&message=If%20Useful&style=style=flat&color=BC4E99)

![meta-sirius](layer-logo.png) \
A Yocto BSP meta-layer includes all the necessary recipes intended to use in yocto project \
for hands-on experience of creating own image adding supported recipes in the build.


## What's included?

* systemd support
* Own custom image
* SD card image or wic image
* Extra user add support
* Support for running userspace application at booting time
* Own C-program (helloword) installed in OS
* wolfssl security library support
* OSversion info in the RootFS
* custom image specific defconfig
* oelint-adv integration and commit-msg formatting
* Add support for Remote Desktop Protocol (RDP) connection for wayland applications

## Setting up

#### Clone the repository 

```
$ git clone https://github.com/prashantdivate/meta-sirius.git -b honister
$ cd meta-sirius
```
#### 1. Yocto Environment set-up from scratch

Run the below command and you will get all the env inplace at ${HOME}/Yocto-Sirius 
```
$ source Yocto-env-setup.sh
```
###                    OR 

#### 2. Adding the meta-sirius layer to your existing build

In order to use this layer, you need to make the build system aware of it.

Assuming the sirius layer exists at the top-level of your
yocto build tree, you can add it to the build system by adding the
location of the custom layer to bblayers.conf, along with any
other layers needed. e.g.:
```
  BBLAYERS ?= " \
    /path/to/yocto/meta \
    /path/to/yocto/meta-yocto \
    /path/to/yocto/meta-yocto-bsp \
    /path/to/yocto/meta-sirius \
    "
```
#### Building sirius-core-image

In order to build image, initiate yocto build setup by running 
```
$ source setup-environment <build-dir>
```
Run the below command to build dev image
```
$ bitbake sirius-dev-image
```
and to build SD card image (wic.gz)
```
$ bitbake sirius-sd-card-image
```

#### Enabling oelint-adv and commit message formating in the layer

```
$ cp -pPR scripts/oelint-adv.sh .git/hooks/pre-commit
```

```
$ cp -pPR scripts/commit-msg .git/hooks/commit-msg
```

NOTE: You can copy these hooks to other layers as well for formatting using oelint

#### Use Remote Desktop Protocol to access Wayland applications
weston should build with rdp-compositor and screen sharing enabled and is taken care in weston\_%.bbappend file
The rdp compositor have dependency on **freerdp**

**On target**
1. Install the freerdp package on target. Make sure you have the TLS certificate/key (server.crt and server.key) installed generally at /etc/freerdp/keys dir
To generate certs use below command
```
 $ winpr-makecert -rdp -path $PWD
```
2. Add the following configs to weston.ini file
```
[core]
modules=systemd-notify.so

[screen-share]
command=/usr/bin64/weston --backend=rdp-backend.so --shell=fullscreen-shell.so --rdp-tls-cert=/etc/freerdp/keys/server.crt --rdp-tls-key=/etc/freerdp/keys/server.key --no-clients-resize
start-on-startup=true
```
3. launch weston with following command
```
weston --debug --ttty=1 --backend=fbdev-backend.so --modules=systemd-notify.so,screen-share.so --use-gl=1 --log=${XDG_RUNTIME_DIR}/weston.log $OPTARGS
```

**On Host**
launch the rdp client:
* For Wayland
```
wlfreerdp /v:<target_ip> /log-level:TRACE
```
* For X11
```
xfreerdp /v:<target_ip>
```
Additionally you can Use remmina as well to connect with target

### Get involved

To get involved following things can be done

- create an issue
- fix an issue and create a pull request
- see the pinned issues in the [bugtracker](https://github.com/prashantdivate/meta-sirius/issues?q=is%3Aissue+is%3Aopen+sort%3Aupdated-desc)

# License
Use of this project is governed by the MIT License found at [LICENSE](./LICENSE).
