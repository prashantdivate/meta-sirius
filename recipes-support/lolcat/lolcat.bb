DESCRIPTION = "A a program that concatenates files, or standard input, to \
standard output (like the generic cat), and adds rainbow coloring to it."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

HOMEPAGE = "https://github.com/jaseg/lolcat"

SRC_URI = "git://github.com/jaseg/lolcat.git;branch=master;rev=35dca3d0a381496d7195cd78f5b24aa7b62f2154"

S = "${WORKDIR}/git"

do_compile() {
    CFLAGS="${CFLAGS} ${LDFLAGS}"
    base_do_compile
}

do_install() {
	cd ${S}
	install -d ${D}${bindir}
	install -m755 lolcat ${D}${bindir}
}