# Copyright (C) 2025 Prashant Divate <diwateprashant44@gmail.com.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Script for growing a partition"
DESCRIPTION = "\
This package provides the growpart script for growing a partition. It is \
primarily used in cloud images in conjunction with the dracut-modules-growroot \
package to grow the root partition on first boot. \
"
HOMEPAGE = "https://github.com/canonical/cloud-utils"
LICENSE = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://README.md;md5=5b581193c7721283bac8d9d1466ab6af"

SRC_URI = "https://github.com/canonical/cloud-utils/archive/refs/tags/${PV}.tar.gz;downloadfilename=cloud-utils-${PV}.tar.gz"
SRC_URI[sha256sum] = "338770d637788466aacfcbcec17a8d0046f92a13cc3b25fce8fceadb02a7339f"

S = "${WORKDIR}/cloud-utils-${PV}"

#inherit autotools

do_install:append() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/bin/growpart ${D}${bindir}/growpart
}

RDEPENDS:${PN} = " \
    gawk \
    util-linux \
"
