#!/bin/bash

# Check if oelint-adv is installed
if ! command -v oelint-adv &> /dev/null; then
  echo "🌟 Hey there! Just a quick heads-up: as per the latest meta-sirius policies, we're giving our code a little extra love. To make sure your changes shine bright, remember to have the charming oelint-adv by your side before making any tweaks. It's like having a trusty companion on your coding journey! 🚀✨"
  echo ""
  echo "oelint-adv found issues. Please fix them before committing."
  echo "Please run below command to install oelint-adv"
  echo "sudo pip install oelint_adv"
  exit 1
else
  # Define the path to the oelint-adv executable
  OELINT_ADV=$(which oelint-adv)

  # Run oelint-adv on all changed yocto recipe files (.bb and .bbclass)
  git diff --cached --name-only | grep -E '\.(bb|bbclass)$' | xargs "$OELINT_ADV" --color --quiet --fix --noinfo --nowarn --nobackup --suppress oelint.file.underscores 

  # Check the exit code of oelint-adv
  if [ $? -ne 0 ]; then
    echo ""
    echo "oelint-adv found issues. Please fix them before committing."
    exit 1
  fi
fi
