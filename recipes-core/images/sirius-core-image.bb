DESCRIPTION = "Minimal configuration os image."
LICENSE = "MIT"

inherit core-image buildhistory

IMAGE_INSTALL= "\
	base-files \
        base-passwd \
        busybox \
        sysvinit \
        sysvinit-launch \
        helloworld \
	lolcat \
	bluez5 \
"

BUILDHISTORY_COMMIT = "5"
IMAGE_LINGUAS = "en-us"

# Set default password for 'root' user
inherit extrausers
ROOTPASSWORD = "root"
ROOTUSERNAME = "root"
EXTRA_USERS_PARAMS ?= "usermod -P ${ROOTPASSWORD} ${ROOTUSERNAME};"

IMAGE_ROOTFS_SIZE ?= "8192"
