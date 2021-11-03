IMAGE_INSTALL += "\
	base-files \
        base-passwd \
        busybox \
        sysvinit \
        sysvinit-launch \
        helloworld \
	wolfssl \
"
IMAGE_ROOTFS_SIZE ?= "8192"

# Set default password for 'root' user
inherit extrausers
ROOTPASSWORD = "root"
ROOTUSERNAME = "root"
EXTRA_USERS_PARAMS ?= "usermod -P ${ROOTPASSWORD} ${ROOTUSERNAME};"
