#!/bin/bash

#variables
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
CYAN='\033[36m'
NC='\033[0m' # No Color

YOCTODIR="Yocto-sirius"

echo -e ""
echo -e "                __                          .__       .__                       "
echo -e "  _____   _____/  |______              _____|__|______|__|__ __  ______          "
echo -e " /     \_/ __ \   __\__  \    ______  /  ___/  \_  __ \  |  |  \/  ___/          "
echo -e "|  Y Y  \  ___/|  |  / __ \_ /_____/  \___ \|  ||  | \/  |  |  /\___ \           "
echo -e "|__|_|  /\___  >__| (____  /         /____  >__||__|  |__|____//____  >          "
echo -e "      \/     \/          \/               \/                        \/           "
echo -e "                                                                                 "
echo -e "${CYAN}[>] Created by : ${NC} Prashant Divate"
echo -e "${CYAN}[>] Version    : ${NC} 1.0.0\n"
echo -e "${CYAN}[>] Tested env : ${NC} Ubuntu 20.04\n"

#Functions
FAIL() {
	echo -e "${RED}[-] ERROR: ${NC}$1\n"
}

PASS() {
	echo -e "${GREEN}[+] PASS: ${NC}$1\n"
}

INFO() {
	echo -e "${YELLOW}$@${NC}\n"
}

do_clean(){
	echo "Starting cleanup process !!!"
	echo "Pressing Enter key will make the cleanup run"
	read

	echo "Nevigating to home dir"
	cd ~

	if [ -d "$YOCTODIR" ]; then
		   echo "'$YOCTODIR' found and now deleting it, please wait ..."
		   INFO "This may take some time."
		   rm -rfv $YOCTODIR/ >> /dev/null
		   PASS "Cleanup process finished"
	else
		FAIL "$YOCTODIR NOT found, will create new one"
	fi
	
}

do_install_pre_requisite(){
	echo -e "${CYAN}[>] ${YELLOW} Output diagnostics, to see where we running and installing pre-requisite:${NC}"

	INFO "Who am I:"
	whoami
	
	INFO "Which directory is currently set:"
	pwd
	echo ""

	echo -e "${CYAN}[>] ${YELLOW} [install packages] - installing repo command...${NC}"
	sudo apt --fix-broken install
	sudo apt-get install repo -y

	echo "install packages needed for yocto build"
	sudo apt-get install gawk wget git diffstat unzip texinfo gcc-multilib \
	build-essential chrpath socat cpio python python3 python3-pip python3-pexpect \
	xz-utils debianutils iputils-ping libsdl1.2-dev xterm -y

	sudo apt-get install autoconf libtool libglib2.0-dev libarchive-dev python-git \
	sed cvs subversion coreutils texi2html docbook-utils python-pysqlite2 \
	help2man make gcc g++ desktop-file-utils libgl1-mesa-dev libglu1-mesa-dev \
	mercurial automake groff curl lzop asciidoc u-boot-tools dos2unix mtd-utils pv \
	libncurses5 libncurses5-dev libncursesw5-dev libelf-dev zlib1g-dev bc rename -y


	echo -e "${CYAN}[>] ${YELLOW} [install packages] - installing pre-requisite finished${NC}"
}

do_configure(){

	git config --global user.name 'Prashant Divate'
	git config --global user.email 'diwateprashant44@gmail.com'
	
	mkdir ~/bin # these 4 step may not be needed if the repo already exists
	curl https://commondatastorage.googleapis.com/git-repo-downloads/repo > ~/bin/repo
	chmod a+x ~/bin/repo
	export PATH=~/bin:$PATH

	# create required dirs
	cd ~
	mkdir $YOCTODIR
	cd $YOCTODIR

	repo init -u https://github.com/varigit/variscite-bsp-platform.git -b thud
	repo sync -j4
		
	cd sources
	git clone https://github.com/prashantdivate/meta-sirius.git
	INFO "Adding meta-sirius layer to yocto environment"
	echo 'BBLAYERS += "${BSPDIR}/sources/meta-sirius"' >> base/conf/bblayers.conf
	cd ..
}

build_image(){
	echo "Setting up the machine for build"
	MACHINE=qemux86-64 DISTRO=poky EULA=1 . setup-environment build
	echo "Building the Sirius image"
	bitbake sirius-core-image # Add more images if you want

	echo "Image build finished"
}

copy_artifacts(){
	mkdir ~/$ARTIFACTS
	echo "Consolidating the artifacts at one place in $ARTIFACTS dir !"
	cp -v ~/$YOCTODIR/build/tmp/deploy/images/qemux86-64/* $ARTIFACTS/.
}

# main starts here
echo ""
INFO "Initiating Yocto-build"
do_clean
do_install_pre_requisite
do_configure
build_image
copy_artifacts
echo " Everything is Done now..."
