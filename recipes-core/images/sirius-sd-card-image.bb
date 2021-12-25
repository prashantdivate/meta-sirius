DESCRIPTION = "Minimal configuration os image for SD card"

LICENSE = "MIT"

include sirius-image.inc

IMAGE_INSTALL = "packagegroup-core-boot "

IMAGE_FSTYPES = "wic"
WKS_FILE = "sd-card.wks"

export IMAGE_BASENAME = "sirius-core-image-sd-card"
