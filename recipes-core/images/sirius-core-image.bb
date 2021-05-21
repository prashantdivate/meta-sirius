DESCRIPTION = "Minimal configuration console image."

LICENSE = "MIT"

inherit core-image

IMAGE_INSTALL= "\
        base-files \
        base-passwd \
        busybox \
        sysvinit \
        initscripts \
        helloworld \
        sysvinit-launch \
	lolcat \
"

IMAGE_LINGUAS = " "



IMAGE_ROOTFS_SIZE ?= "8192"
