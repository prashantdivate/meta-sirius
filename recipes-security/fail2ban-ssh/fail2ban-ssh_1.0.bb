# Copyright (C) 2026 Prashant Divate <prashant.divate@madelevator.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "Fail2Ban OpenSSH brute-force protection config"
LICENSE = "CLOSED"

SRC_URI = " \
    file://jail.local \
    file://sshd-custom.conf \
    file://nftables.conf \
    file://nftables-sshd.conf \
    file://sirius-nftables.service \
    file://fail2ban.service \
"

inherit systemd

do_install() {
    install -d ${D}${sysconfdir}/fail2ban
    install -m 0644 ${WORKDIR}/jail.local ${D}${sysconfdir}/fail2ban/jail.local

    install -d ${D}${sysconfdir}/fail2ban/filter.d
    install -m 0644 ${WORKDIR}/sshd-custom.conf ${D}${sysconfdir}/fail2ban/filter.d/sshd-custom.conf

    install -d ${D}${sysconfdir}/fail2ban/action.d
    install -m 0644 ${WORKDIR}/nftables-sshd.conf ${D}${sysconfdir}/fail2ban/action.d/nftables-sshd.conf

    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/nftables.conf ${D}${sysconfdir}/nftables.conf

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/sirius-nftables.service ${D}${systemd_system_unitdir}/sirius-nftables.service
    install -m 0644 ${WORKDIR}/fail2ban.service ${D}${systemd_system_unitdir}/fail2ban.service
}

RDEPENDS:${PN} += "python3-fail2ban nftables openssh-sshd"

SYSTEMD_SERVICE:${PN} = "sirius-nftables.service fail2ban.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

FILES:${PN} += " \
    ${sysconfdir}/fail2ban \
    ${sysconfdir}/nftables.conf \
    ${systemd_system_unitdir}/sirius-nftables.service \
    ${systemd_system_unitdir}/fail2ban.service \
"
