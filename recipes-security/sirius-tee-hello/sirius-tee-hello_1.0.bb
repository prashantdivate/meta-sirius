SUMMARY = "Sirius OP-TEE hello world example"
DESCRIPTION = "Minimal Normal World client plus OP-TEE Trusted Application example"
HOMEPAGE = "https://github.com/prashantdivate/meta-sirius"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9eeec730f372bfab540f938bf4b4c055"

DEPENDS = "python3-cryptography-native optee-os optee-client"
RDEPENDS:${PN} += "optee-client"

SRC_URI = " \
    file://host/main.c \
    file://ta/hello_ta.c \
    file://ta/user_ta_header_defines.h \
    file://ta/sub.mk \
    file://ta/Makefile \
    file://include/sirius_tee_hello_ta.h \
    file://LICENSE \
"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

inherit python3native features_check

REQUIRED_MACHINE_FEATURES = "optee"

OPTEE_ARCH:arm = "arm32"
OPTEE_ARCH:aarch64 = "arm64"

TA_UUID = "7f8c9d52-61aa-4a9f-9c4e-8c7d3fb84221"
TA_DEV_KIT_DIR = "${STAGING_INCDIR}/optee/export-user_ta_${OPTEE_ARCH}"

CFLAGS:append = " --sysroot=${STAGING_DIR_HOST}"


DEPENDS += " \
    optee-client \
    optee-os \
    openssl-native \
    python3-cryptography-native \
"

# Keep the package compatible with usrmerge distros by installing into
# ${bindir} (/usr/bin) and ${libdir} (/usr/lib), not legacy /bin or /lib.
do_compile() {
    # OP-TEE sign_encrypt.py uses python cryptography + native OpenSSL.
    # Explicitly point OpenSSL to the provider modules from the recipe sysroot.
    export OPENSSL_MODULES="${STAGING_LIBDIR_NATIVE}/ossl-modules"

    # Avoid host/system OpenSSL configuration interfering with Yocto's
    # recipe-sysroot-native OpenSSL.
    export OPENSSL_CONF="/dev/null"

    oe_runmake -C ${S}/ta \
        O=${B}/ta \
        TA_DEV_KIT_DIR=${STAGING_INCDIR}/optee/export-user_ta_${OPTEE_ARCH} \
        CROSS_COMPILE=${TARGET_PREFIX}

    ${CC} ${CFLAGS} ${LDFLAGS} \
        -I${S}/include \
        -I${STAGING_INCDIR} \
        ${S}/host/main.c \
        -lteec \
        -o ${B}/sirius-tee-hello
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/host/sirius-tee-hello ${D}${bindir}/sirius-tee-hello

    install -d ${D}${libdir}/optee_armtz
    install -m 0444 ${B}/ta/${TA_UUID}.ta \
        ${D}${libdir}/optee_armtz/${TA_UUID}.ta
}

FILES:${PN} += "${libdir}/optee_armtz/${TA_UUID}.ta"
