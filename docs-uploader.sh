#!/bin/bash

if [ "$TRAVIS_REPO_SLUG" == "Vonathar/Boxshare" ] && [ "$TRAVIS_JDK_VERSION" == "openjdk11" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "gh-pages" ]; then

  echo -e "Publishing javadoc...\n"

  cp -R target/site/apidocs $HOME/javadoc-latest

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone https://${GH_TOKEN}@github.com/Vonathar/Boxshare.github.io

  cd Boxshare.github.io
  git rm -rf ./javadoc/latest
  cp -Rf $HOME/javadoc-latest ./javadoc/latest
  git add -f .
  git commit -m "Auto-push latest documentation: $TRAVIS_BUILD_NUMBER"
  git push origin gh-pages

  echo -e "Published Javadoc to gh-pages.\n"

fi
