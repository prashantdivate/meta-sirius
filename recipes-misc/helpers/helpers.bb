# Copyright (C) 2025 Prashant Divate <diwateprashant44@gmail.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "helper scripts"
AUTHOR = "Prashant Divate <diwateprashant44@gmail.com>"
LICENSE = "CLOSED"

SRC_URI = "file://hotfix"

S = "${WORKDIR}"

inherit systemd

do_install() {
    install -d ${D}${base_bindir}
    install -m 755 ${WORKDIR}/hotfix ${D}${base_bindir}/
}

FILES:${PN} += "${base_bindir}/*"
