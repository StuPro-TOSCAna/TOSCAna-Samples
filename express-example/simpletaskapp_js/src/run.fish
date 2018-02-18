#!/usr/bin/env fish

# Environment Setup
echo "Setting Environment Variables"
# Set the redis URL to use a Local instance (Running in docker) on port 6379 Db 0 is used
set -x REDIS_URL "redis://127.0.0.1:6379/0"

# Start Backing Services (If not running)
if not docker container top stajs_redis > /dev/null
  echo "Creating Redis Container"
  docker run -d -p 6379:6379 --name stajs_redis redis
end

echo "Running 'npm start'"
npm start
