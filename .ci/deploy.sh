#!/bin/bash

if [[ "$TRAVIS_BRANCH" != "develop" ]]; then
  echo "Skipping snapshot deployment: wrong branch. Expected 'develop' but was '$TRAVIS_BRANCH'."
  exit 0
else
  ./gradlew --no-daemon uploadArchives -PSONATYPE_NEXUS_USERNAME=$SONATYPE_NEXUS_USERNAME -PSONATYPE_NEXUS_PASSWORD=$SONATYPE_NEXUS_PASSWORD; gradlew_return_code=$?
  echo "exit code = $gradlew_return_code"
  exit ${gradlew_return_code}
fi
