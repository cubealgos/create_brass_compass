# The task surface: the fleet's standard recipe names.

main_checkout := parent_directory(`git rev-parse --path-format=absolute --git-common-dir`)
vault_spec := env("BC_VAULT_SPEC", main_checkout / ".." / "heimathafen" / "vault" / "projects" / "create_brass_compass" / "spec")

default:
    @just --list

# Resolve every dependency and prove the toolchain.
bootstrap:
    ./gradlew --version

# Static analysis and the project's own rules, without the tests.
lint:
    ./gradlew check -x test -x runGameTest

# Everything with a build step, including the jar.
build:
    ./gradlew build

# Unit tests, then the repository tools as commands.
test: test-java test-tools

test-java:
    ./gradlew test

test-tools:
    python3 -m unittest discover -s tools -p 'test_*.py'

# Server-side game tests on a headless dedicated server.
gametest:
    ./gradlew runGameTest

# Minecraft 26.2 with Create Fly and this mod.
client:
    ./gradlew runClient

# Refresh docs/spec/ from the vault; the vault is authoritative.
spec-sync:
    rsync -a --delete "{{vault_spec}}/" docs/spec/

# Regenerate docs/map.md and docs/map/ from the source.
map:
    python3 tools/map.py

map-check:
    python3 tools/map.py --check

# Repository conformance, read-only.
doctor: doctor-repo doctor-toolchain

doctor-repo:
    kontor doctor

doctor-toolchain:
    python3 tools/doctor.py

# Everything a merge must survive.
check: lint map-check test gametest
