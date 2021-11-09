DESCRIPTION = "Development os image."

LICENSE = "MIT"

inherit core-image buildhistory

include sirius-image.inc

IMAGE_INSTALL += "\
		packagegroup-debug-utils \
		nano \
		vim \
"

BUILDHISTORY_COMMIT = "2"

IMAGE_LINGUAS = "en-us"
IMAGE_FEATURES += "splash"
