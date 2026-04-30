#!/bin/bash

# Thrift Build and Test Script

echo "Building Thrift project..."
./gradlew clean build

if [ $? -eq 0 ]; then
    echo ""
    echo "Build successful!"
    echo ""
    echo "You can run the tests with:"
    echo "  ./gradlew test"
    echo ""
    echo "Or run individually:"
    echo "  ./gradlew run --args='server'  # Start server"
    echo "  ./gradlew run --args='client'  # Run client (in another terminal)"
else
    echo "Build failed!"
    exit 1
fi
