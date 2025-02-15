#!/bin/bash

# Wait for LocalStack to be ready
echo "Waiting for LocalStack to be ready..."
until curl -s http://localstack:4566 > /dev/null; do
  sleep 3
done

# Create S3 Bucket
echo "Creating S3 Bucket: omni-health-bucket"
aws --endpoint-url=http://localstack:4566 s3 mb s3://omni-health-bucket

echo "Bucket created successfully!"
