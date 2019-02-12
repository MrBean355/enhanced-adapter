#!/bin/bash

set -e

if [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
  echo "Skipping snapshot deployment: was pull request."
elif [ "$TRAVIS_BRANCH" != "master" -a "$TRAVIS_BRANCH" != "develop" ]; then
  echo "Skipping snapshot deployment: wrong branch. Expected 'master' or 'develop' but was '$TRAVIS_BRANCH'."
else
  ./gradlew --no-daemon publish
fi
