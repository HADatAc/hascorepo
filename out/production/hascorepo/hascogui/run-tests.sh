#!/bin/bash

# Starting Xvfb (X virtual framebuffer) in the :99 screen with a 1280x1024 resolution
Xvfb :99 -screen 0 1280x1024x16 &

export DISPLAY=:99

echo "Waiting for drupal"
until curl -s http://drupal:80/ > /dev/null; do
  echo "5 seconds."
  sleep 5
done
# Execution of Java tests with JUnit
java -cp ".:/opt/development/tests/selenium-java-4.26.0/*:/opt/development/tests/selenium-java-4.26.0/libs/*:/opt/development/junit4/*" org.junit.runner.JUnitCore tests.UnitTest