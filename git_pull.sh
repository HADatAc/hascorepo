#!/bin/bash

# Use the current directory as the repository directory
REPO_DIR=$(pwd)

if [ ! -d "$REPO_DIR" ]; then
    echo "Error: Directory $REPO_DIR not found."
    exit 1
fi

cd "$REPO_DIR" || { echo "Error: Could not navigate to the directory $REPO_DIR."; exit 1; }

# Try to fetch all updates from the remote repository
if ! git fetch --all; then
    echo "Error: Could not fetch remote updates. Check your connection or access to the repository."
    exit 1
fi

# Loop through all local branches and do git pull
for branch in $(git branch -a | grep remotes | sed 's|.*origin/||' | uniq); do
    branch=$(echo "$branch" | xargs)  # Remove whitespace

    # Try to checkout the branch
    if ! git checkout "$branch"; then
        echo "Error: Could not switch to branch $branch. Skipping to the next one."
        continue
    fi

    # Try to pull the current branch
    if ! git pull; then
        echo "Error: Could not update branch $branch. Skipping to the next one."
        continue
    fi
done