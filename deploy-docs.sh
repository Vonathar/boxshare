#!/bin/bash

if [ "$TRAVIS_REPO_SLUG" == "Vonathar/boxshare" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

  echo -e "Publishing javadoc...\n"

  cp -R target/apidocs $HOME/docs-latest

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/Vonathar/boxshare gh-pages >/dev/null

  cd gh-pages
  git rm -rf ./docs
  cp -Rf $HOME/docs-latest ./docs
  git add -f .
  git commit -m "Add latest documentation. Travis build #: $TRAVIS_BUILD_NUMBER"
  git push -fq origin gh-pages >/dev/null

  echo -e "Published Javadoc to gh-pages.\n"

fi
