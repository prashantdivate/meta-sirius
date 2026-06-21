# QEMU testing sirius-dev-image

This layer validates `sirius-dev-image` with Yocto runtime tests using the
`qemux86-64` machine.

The CI workflow builds the image and then runs:

```sh
bitbake sirius-dev-image -c testimage
```

That boots the image in QEMU and runs the selected runtime test suites:

```conf
TEST_SUITES = "ping ssh df date"
```

These tests confirm that the image boots, networking works, SSH is reachable,
the filesystem is mounted, and basic target commands run successfully.

Because the workflow builds `sirius-dev-image` before booting it, every recipe
installed by that image must fetch, configure, compile, install, package, and
assemble into the root filesystem successfully. Recipes that are present in the
layer but not included in `sirius-dev-image` are not covered by this runtime
test.

## Local test

From a Poky build environment, add `meta-sirius` and the required
`meta-openembedded` layers, then use:

```conf
MACHINE = "qemux86-64"
DISTRO = "poky"
PACKAGE_CLASSES = "package_ipk"
EXTRA_IMAGE_FEATURES += "debug-tweaks ssh-server-openssh"
IMAGE_CLASSES += "testimage"
TEST_SUITES = "ping ssh df date"
TEST_RUNQEMUPARAMS = "nographic slirp"
```

Build and run the QEMU runtime test:

```sh
bitbake sirius-dev-image
bitbake sirius-dev-image -c testimage
```

For manual inspection after a successful build:

```sh
runqemu qemux86-64 sirius-dev-image nographic slirp
```
