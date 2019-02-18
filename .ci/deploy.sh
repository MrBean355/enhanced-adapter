#!/bin/bash

if [ "$TRAVIS_BRANCH" != "develop" ]; then
  echo "Skipping snapshot deployment: wrong branch. Expected 'develop' but was '$TRAVIS_BRANCH'."
  exit 0
else
  ./gradlew --no-daemon uploadArchives
  exit $?
fi
