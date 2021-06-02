# meta-sirius

Sirius is the brightest star in the night sky. Its name is derived from the Greek word. \
This project/meta-layer includes all the necessary recipes intended to use in yocto project \
for hands-on experience of creating own image adding supported recipes in the build. \


This README file contains information on the contents of the \
custom layer as introduced in the "Adding a new software layer" on the yocto build \


## Table of Contents
=================

  I.  Adding the custom layer to your build
  II. Building sirius-core-image


## I. Adding the custom layer to your build
=================================================

In order to use this layer, you need to make the build system aware ofit.

Assuming the sirius layer exists at the top-level of your
yocto build tree, you can add it to the build system by adding the
location of the custom layer to bblayers.conf, along with any
other layers needed. e.g.:

  BBLAYERS ?= " \
    /path/to/yocto/meta \
    /path/to/yocto/meta-yocto \
    /path/to/yocto/meta-yocto-bsp \
    /path/to/yocto/meta-sirius \
    "
## II. Building sirius-core-image
=================================================

In order to build image, initiate yocto build setup by running \
```
source setup-environment <build-dir>
```
now run the below command to build image
```
bitbake sirius-core-image
```

# Contribute
Contributions are always welcome!  
Please read the [contribution guidelines](contributing.md) first.

# License
Use of this project is governed by the MIT License found at [LICENSE](./LICENSE).
