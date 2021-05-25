DESCRIPTION = "Minimal configuration console image."

LICENSE = "MIT"

inherit core-image

IMAGE_INSTALL= "\
        base-files \
        base-passwd \
        busybox \
        sysvinit \
        initscripts \
        sysvinit-launch \
        helloworld \
	 lolcat \
	 bluez5 \
"

IMAGE_LINGUAS = " "



IMAGE_ROOTFS_SIZE ?= "8192"
