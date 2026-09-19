#!/bin/sh
# Installs `just` into the pipeline image when it is missing. Idempotent, so the same script runs
# on a developer machine as a no-op. Everything needing shell syntax lives here, not in the
# pipeline file, because Woodpecker interpolates its own variable syntax throughout that file.
set -eu
if command -v just >/dev/null 2>&1; then
  echo "just $(just --version | awk '{print $2}') already present"
  exit 0
fi
JUST_VERSION="${JUST_VERSION:-1.58.0}"
echo "installing just ${JUST_VERSION}"
curl --proto '=https' --tlsv1.2 -sSf https://just.systems/install.sh | sh -s -- --tag "${JUST_VERSION}" --to /usr/local/bin
just --version
