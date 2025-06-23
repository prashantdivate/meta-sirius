FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

# This implementation configures automated log rotation using logrotate and systemd. Logs in /var/log are rotated every 4 hours or when they exceed a defined size limit, with compression and backup retention. A timer unit schedules regular rotation, while a shutdown service ensures temporary logs are saved before power-off. The setup includes low CPU and I/O priority settings to avoid impacting real-time system performance and handles invalid timestamps for RTC-less devices.

SRC_URI:append = " file://logrotate.conf \
                   file://logrotate.service \
                   file://logrotate.timer \
                   file://shutdown-savelog.service \
                   file://cleanlog \
                   "

do_install:append() {
    install -d ${D}${sysconfdir}
    install -m 644 ${WORKDIR}/logrotate.conf ${D}${sysconfdir}

    install -d ${D}${systemd_unitdir}/system
    install -m 644 ${WORKDIR}/logrotate.service ${D}${systemd_unitdir}/system
    install -m 644 ${WORKDIR}/logrotate.timer ${D}${systemd_unitdir}/system
    install -m 644 ${WORKDIR}/shutdown-savelog.service ${D}${systemd_unitdir}/system

    install -d ${D}/usr/local/bin
    install -m 755  ${WORKDIR}/cleanlog ${D}/usr/local/bin/

}

FILES:${PN} += "${sysconfdir} \
                ${systemd_unitdir}/system \
                /usr/local/bin \
              "

inherit systemd

SYSTEMD_SERVICE:${PN} = "logrotate.service logrotate.timer shutdown-savelog.service"
