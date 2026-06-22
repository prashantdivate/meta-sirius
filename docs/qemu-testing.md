# Build and Run sirius-dev-image in QEMU

This guide shows a simple local Yocto setup for building `sirius-dev-image`
with `meta-sirius` and running it with QEMU.

The GitHub Actions workflow intentionally does not build or boot the full image,
because that is slow and expensive for every push. Use this guide when you want
to validate the image locally.

## 1. Install Host Packages

On Ubuntu or Debian:

```sh
sudo apt update
sudo apt install -y \
  gawk wget git diffstat unzip texinfo gcc build-essential chrpath socat cpio \
  python3 python3-pip python3-pexpect xz-utils debianutils iputils-ping \
  python3-git python3-jinja2 python3-subunit zstd liblz4-tool file locales \
  qemu-system-x86
```

## 2. Clone Yocto Layers

Example using the `kirkstone` branch:

```sh
mkdir -p ~/yocto
cd ~/yocto

git clone -b kirkstone https://git.yoctoproject.org/poky
git clone -b kirkstone https://git.openembedded.org/meta-openembedded
git clone https://github.com/prashantdivate/meta-sirius.git
```

If your local `meta-sirius` checkout is already elsewhere, use that path instead
of cloning it again.

## 3. Create a Build Directory

```sh
cd ~/yocto
source poky/oe-init-build-env build-sirius-qemu
```

## 4. Configure local.conf

Add this to `conf/local.conf`:

```conf
MACHINE = "qemux86-64"
DISTRO = "poky"
PACKAGE_CLASSES = "package_ipk"
EXTRA_IMAGE_FEATURES += "debug-tweaks ssh-server-openssh"
```

## 5. Add Layers

From the build directory:

```sh
bitbake-layers add-layer ../meta-openembedded/meta-oe
bitbake-layers add-layer ../meta-openembedded/meta-python
bitbake-layers add-layer ../meta-openembedded/meta-networking
bitbake-layers add-layer ../meta-openembedded/meta-filesystems
bitbake-layers add-layer ../meta-sirius
```

Check the configured layers:

```sh
bitbake-layers show-layers
```

## 6. Build the Image

```sh
bitbake sirius-dev-image
```

This confirms that the recipes installed into `sirius-dev-image` can be fetched,
configured, compiled, packaged, and assembled into an image.

## 7. Run the Image in QEMU

```sh
runqemu qemux86-64 sirius-dev-image nographic slirp
```

To exit QEMU in `nographic` mode, use:

```text
Ctrl-a x
```

## Optional Runtime Test

If you want Yocto to boot the image in QEMU and run basic runtime tests, add this
to `conf/local.conf`:

```conf
IMAGE_CLASSES += "testimage"
TEST_SUITES = "ping ssh df date"
TEST_RUNQEMUPARAMS = "nographic slirp"
```

Then run:

```sh
bitbake sirius-dev-image -c testimage
```
