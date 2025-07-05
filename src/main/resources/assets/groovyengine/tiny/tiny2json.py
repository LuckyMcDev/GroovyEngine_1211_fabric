#!/usr/bin/env python3
import json
import sys

def parse_tiny_v2(lines):
    root = {"type": "root", "children": []}
    stack = [( -1, root )]  # (indent_level, node)

    for raw in lines:
        line = raw.rstrip("\n")
        if not line or line.startswith("#"):
            continue
        indent = len(line) - len(line.lstrip("\t"))
        parts = line.lstrip("\t").split("\t")
        node = {"tag": parts[0], "fields": parts[1:], "children": []}

        # pop until parent indent < current
        while stack and stack[-1][0] >= indent:
            stack.pop()
        parent = stack[-1][1]
        parent["children"].append(node)
        stack.append((indent, node))

    return root

def main():
    if len(sys.argv) != 3:
        print("Usage: tiny2json.py input.tiny output.json")
        sys.exit(1)

    with open(sys.argv[1], encoding="utf-8") as f:
        lines = f.readlines()

    tree = parse_tiny_v2(lines)

    with open(sys.argv[2], "w", encoding="utf-8") as out:
        json.dump(tree, out, indent=2, ensure_ascii=False)

if __name__ == "__main__":
    main()
