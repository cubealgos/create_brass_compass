#!/bin/sh
# Installs the tools `just check` needs into the pipeline image when they are missing. Idempotent,
# so the same script runs on a developer machine as a no-op. Everything needing shell syntax lives
# here, not in the pipeline file, because Woodpecker interpolates its own variable syntax there.
# POSIX sh only: the image runs this with dash.
set -eu

# eclipse-temurin:25-jdk ships neither curl, git nor python3; the justfile calls git to find the
# checkout, `just check` needs python3 for tools/map.py and the tool tests, and curl fetches just.
for tool in curl git python3; do
  if ! command -v "$tool" >/dev/null 2>&1 && command -v apt-get >/dev/null 2>&1; then
    echo "installing curl, git and python3"
    export DEBIAN_FRONTEND=noninteractive
    apt-get update -qq >/dev/null
    apt-get install -y -qq --no-install-recommends curl git python3 ca-certificates >/dev/null 2>&1
    break
  fi
done

if command -v just >/dev/null 2>&1; then
  echo "just $(just --version | awk '{print $2}') already present"
  exit 0
fi

# just's install.sh is a bash script, which the image lacks; the release tarball needs only tar.
JUST_VERSION="${JUST_VERSION:-1.58.0}"
case "$(uname -m)" in
  x86_64) JUST_TARGET="x86_64-unknown-linux-musl" ;;
  aarch64|arm64) JUST_TARGET="aarch64-unknown-linux-musl" ;;
  *) echo "no just build for $(uname -m)" >&2; exit 1 ;;
esac
echo "installing just ${JUST_VERSION} for ${JUST_TARGET}"
curl --proto '=https' --tlsv1.2 -sSfL "https://github.com/casey/just/releases/download/${JUST_VERSION}/just-${JUST_VERSION}-${JUST_TARGET}.tar.gz" | tar -xz -C /usr/local/bin just
just --version
