FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

# create mountpoints for log partition
dirs755 += " \
	/opt/log \
"
