FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
	file://0001-ARM-sirius-defconfig-Add-initial-defconfig.patch \
"

# IMPORTANT: Increase version for each change
PR = "5"

KERNEL_DEFCONFIG = "${S}/arch/arm/configs/sirius_defconfig"
DEPENDS += "lz4-native"
EXTRA_OEMAKE += "LOCALVERSION=-${PR}"

