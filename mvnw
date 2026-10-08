#!/bin/sh
set -e
MAVEN_WRAPPER_PROPERTIES=".mvn/wrapper/maven-wrapper.properties"
if [ -r "$MAVEN_WRAPPER_PROPERTIES" ]; then
  DISTRIBUTION_URL=$(grep -E '^distributionUrl=' "$MAVEN_WRAPPER_PROPERTIES" | sed 's/distributionUrl=//')
fi
MAVEN_USER_HOME="${MAVEN_USER_HOME:-${HOME}/.m2}"
MAVEN_WRAPPER_HOME="${MAVEN_USER_HOME}/wrapper"
DISTRIBUTION_NAME=$(basename "$DISTRIBUTION_URL")
DISTRIBUTION_DIR="${MAVEN_WRAPPER_HOME}/dists/${DISTRIBUTION_NAME%%-bin.zip}"
if [ ! -d "${DISTRIBUTION_DIR}" ]; then
  echo "Downloading Maven from ${DISTRIBUTION_URL}"
  mkdir -p "${DISTRIBUTION_DIR}"
  TMP_DOWNLOAD="${DISTRIBUTION_DIR}/download.zip"
  if command -v curl > /dev/null; then
    curl -fsSL -o "${TMP_DOWNLOAD}" "${DISTRIBUTION_URL}"
  elif command -v wget > /dev/null; then
    wget -q -O "${TMP_DOWNLOAD}" "${DISTRIBUTION_URL}"
  else
    echo "ERROR: Neither curl nor wget found." >&2; exit 1
  fi
  unzip -q "${TMP_DOWNLOAD}" -d "${DISTRIBUTION_DIR}"
  rm "${TMP_DOWNLOAD}"
fi
MAVEN_HOME=$(find "${DISTRIBUTION_DIR}" -maxdepth 2 -name "mvn" -type f | head -1 | xargs -I{} dirname {} | xargs -I{} dirname {})
exec "${MAVEN_HOME}/bin/mvn" "$@"
