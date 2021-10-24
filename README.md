# meta-sirius
![Ubuntu-ver](https://img.shields.io/badge/Ubuntu%20ver-20.04-lightpink.svgs?tyle=plastic&logo=linux)
![python3.x](https://img.shields.io/badge/python-3.x-orange.svg?style=plastic)
![languages](https://img.shields.io/badge/Supported%20lang-c,shell,bitbake-blue.svg?style=plastic)

![meta-sirius](layer-logo.png) \
A Yocto BSP meta-layer includes all the necessary recipes intended to use in yocto project \
for hands-on experience of creating own image adding supported recipes in the build.


## What's included?

* Custom minimal image
* SD card image or wic image
* Extra user add support
* Support for running userspace application at booting time
* C program binary(helloword) installed in OS

## Setting up

#### Clone the repository 

```
$ git clone https://github.com/prashantdivate/meta-sirius.git -b master
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
and to build SD card image (.wic)
```
$ bitbake sirius-sd-card-image
```

### Get involved

To get involved following things can be done

- create an issue
- fix an issue and create a pull request
- see the pinned issues in the [bugtracker](https://github.com/prashantdivate/meta-sirius/issues?q=is%3Aissue+is%3Aopen+sort%3Aupdated-desc)

# License
Use of this project is governed by the MIT License found at [LICENSE](./LICENSE).
