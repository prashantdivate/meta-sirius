SUMMARY = "Waypipe - A Wayland proxy for remote desktop connections"
LICENSE = "CLOSED"

SRCBRANCH = "master"
#SRC_URI = "https://gitlab.freedesktop.org/mstoeckl/waypipe/-/archive/master/waypipe-master.tar"
SRC_URI = "git://gitlab.freedesktop.org/mstoeckl/waypipe.git;protocol=https;branch=master"
SRCREV = "${AUTOREV}"
SRC_URI[sha256sum] = "8f13d501118505a3f47c236837c7af472ad79177674827b88ac077014ebbca56"

S = "${WORKDIR}/git"

DEPENDS = "weston wayland-native"
#DEPENDS = "wayland liblz4 libzstd libgbm libdrm ffmpeg libva scdoc ssh x264"

inherit meson

EXTRA_OEMESON += " \
    --buildtype debugoptimized \
    "

do_install() {
    DESTDIR=${D} ninja -C ${WORKDIR}/build install
}

FILES:${PN} += "${datadir}"
