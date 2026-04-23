#!/bin/bash

OUTPUT="project_kotlin_files.md"
ROOT_DIR="."

> "$OUTPUT"

find "$ROOT_DIR" -type f -name "*.kt" \
  ! -path "*/Pods/*" \
  ! -path "*/.build/*" \
  ! -path "*/build/*" \
  ! -path "*/DerivedData/*" \
  ! -path "*/.git/*" | sort | while read -r file; do
    rel_path="${file#./}"
    {
      echo "# $rel_path"
      echo
      echo '```kotlin'
      awk '!/^[[:space:]]*import[[:space:]]+/' "$file"
      echo
      echo '```'
      echo
      echo "---"
      echo
    } >> "$OUTPUT"
done

echo "Created $OUTPUT"
