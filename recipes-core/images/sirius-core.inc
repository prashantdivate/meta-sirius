IMAGE_INSTALL += "\
	base-files \
        base-passwd \
        busybox \
        e2fsprogs \
        helloworld \
        wolfssl \
        cloud-utils-growpart \
        logrotate \
"
IMAGE_ROOTFS_SIZE ?= "8192"


inherit OSversion extrausers

# Set default password for 'root' user
ROOTPASSWORD = "root"
ROOTUSERNAME = "root"
EXTRA_USERS_PARAMS ?= "usermod -P ${ROOTPASSWORD} ${ROOTUSERNAME};"
