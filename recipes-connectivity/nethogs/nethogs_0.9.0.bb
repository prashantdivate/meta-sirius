# Copyright (C) 2026 Prashant Divate <prashant.divate@madelevator.com>
# Released under the MIT license (see COPYING.MIT for the terms)

SUMMARY = "NetHogs is a small net top tool that groups bandwidth by process"
DESCRIPTION = "Instead of breaking the traffic down per protocol or per subnet, \
like most tools do, it groups bandwidth by process ID (PID)."
HOMEPAGE = "https://github.com/raboof/nethogs"
SECTION = "net"
LICENSE = "CLOSED"

# Official dependencies required to build nethogs binaries
DEPENDS = "ncurses libpcap"

# Standard repository pointer appending the explicit version tag requested by maintainers
SRC_URI = "git://github.com/raboof/nethogs.git;branch=main;protocol=https"

# Matching commit ID hash for release version 0.8.8
SRCREV = "2d5e96c5af8edc9dbdfa61ca71464f636a2af291"

S = "${WORKDIR}/git"

# Automatically handle the updated build matrix architecture
inherit meson pkgconfig

DEPENDS = "ncurses libpcap"

RDEPENDS:${PN} += "libpcap"

# Explicitly packages the binary path into the system configuration payload
FILES:${PN} += "${sbindir}/nethogs"
