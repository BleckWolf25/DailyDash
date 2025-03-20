#!/bin/bash

# First build with Maven
mvn clean install

# Run the application
mvn javafx:run