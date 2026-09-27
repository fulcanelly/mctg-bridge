#!/usr/bin/env bash
set -euo pipefail

release_tag="${1:?Usage: validate-tag-version.sh <tag>}"
current="${release_tag#v}"
previous=""

while IFS= read -r tag; do
  if [[ "$tag" != "$release_tag" ]]; then
    previous="$tag"
    break
  fi
done < <(git tag --list '*' --sort=-v:refname)

if [[ -z "$previous" ]]; then
  exit 0
fi

previous="${previous#v}"
highest=$(printf '%s\n%s\n' "$previous" "$current" | sort -V | tail -n 1)

if [[ "$highest" != "$current" || "$current" == "$previous" ]]; then
  echo "::error::Version v$current must be greater than v$previous" >&2
  exit 1
fi
