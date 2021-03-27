#!/bin/bash

if [ "$TRAVIS_REPO_SLUG" == "Vonathar/Boxshare" ] && [ "$TRAVIS_JDK_VERSION" == "openjdk11" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

  echo -e "Publishing javadoc...\n"

  cp -R target/site/apidocs $HOME/javadoc-latest

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/Vonathar/Boxshare gh-pages >/dev/null

  cd gh-pages
  git rm -rf ./javadoc
  cp -Rf $HOME/javadoc-latest ./javadoc
  git add -f .
  git commit -m "Add latest documentation. Travis build #: $TRAVIS_BUILD_NUMBER"
  git push -fq origin gh-pages >/dev/null

  echo -e "Published Javadoc to gh-pages.\n"

fi
