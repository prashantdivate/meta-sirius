DESCRIPTION = "Fastfetch is a neofetch-like tool for fetching system information and displaying them in a pretty way.\
                It is written mainly in C, with performance and customizability in mind. \
                "
SUMMARY =   "System Information. \
            This recipe is intended to take FASTFETCH tool - as is - to YOCTO projects.\
            "
SECTION = "console/utils"

HOMEPAGE = "https://github.com/fastfetch-cli/fastfetch"
BUGTRACKER = "https://github.com/fastfetch-cli/fastfetch/issues"

LICENSE = "CLOSED"

SRC_URI = " \
            git://github.com/fastfetch-cli/fastfetch;protocol=https;branch=master \
            file://config.jsonc \
            "

SRCREV = "563f9598be7f966007c640559c695ac47fe821f3"
S = "${WORKDIR}/git"

# Inherit the cmake class for building the project.
inherit cmake

do_install(){
    install -d ${D}${bindir}
    install -m 0755 ${B}/fastfetch ${D}${bindir}

    install -d ${D}${sysconfdir}/profile.d
    echo "fastfetch" > ${D}${sysconfdir}/profile.d/fastfetch.sh
    chmod 0755 ${D}${sysconfdir}/profile.d/fastfetch.sh

    #Add the custom configuration
    install -d ${D}${sysconfdir}/fastfetch
    install -m 0644 ${WORKDIR}/config.jsonc ${D}${sysconfdir}/fastfetch/config.jsonc
}

FILES:${PN} += " \
    ${sysconfdir}/profile.d/fastfetch.sh \
    ${sysconfdir}/fastfetch/config.jsonc \
    ${bindir} \
    "
