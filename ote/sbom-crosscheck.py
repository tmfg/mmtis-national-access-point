#!/usr/bin/env python3
"""Cross-check lein deps :tree output against a Syft CycloneDX SBOM JSON."""

import json
import re
import sys
from pathlib import Path

def parse_lein_deps(path: str) -> dict[tuple[str, str], dict]:
    """Parse lein deps :tree output into {(group, artifact): {version, scope, depth}}."""
    deps = {}
    # Matches: [group/artifact "version" ...] or [artifact "version" ...]
    pat = re.compile(
        r'^(\s*)\[(\S+)\s+"([^"]+)"'
        r'(?:\s+:exclusions\s+\[[^\]]*\])*'
        r'(?:\s+:scope\s+"([^"]+)")?'
    )
    for line in Path(path).read_text().splitlines():
        m = pat.match(line)
        if not m:
            continue
        indent, coord, version, scope = m.group(1), m.group(2), m.group(3), m.group(4)
        depth = len(indent) // 2
        if "/" in coord:
            group, artifact = coord.split("/", 1)
        else:
            group = artifact = coord
        deps[(group, artifact)] = {
            "version": version,
            "scope": scope or "compile",
            "depth": depth,
        }
    return deps


def parse_sbom(path: str) -> dict[tuple[str, str], dict]:
    """Parse CycloneDX JSON into {(group, artifact): {version}}."""
    data = json.loads(Path(path).read_text())
    comps = {}
    for c in data.get("components", []):
        group = c.get("group", "")
        name = c.get("name", "")
        version = c.get("version", "")
        if group and name:
            comps[(group, name)] = {"version": version}
    return comps


def main():
    lein_path = sys.argv[1] if len(sys.argv) > 1 else "lein-deps.txt"
    sbom_path = sys.argv[2] if len(sys.argv) > 2 else "sift-out/sbom.json"

    lein = parse_lein_deps(lein_path)
    sbom = parse_sbom(sbom_path)

    lein_keys = set(lein.keys())
    sbom_keys = set(sbom.keys())

    lein_compile = {k for k in lein_keys if lein[k]["scope"] == "compile"}
    lein_test = {k for k in lein_keys if lein[k]["scope"] == "test"}

    matched = lein_compile & sbom_keys
    version_ok = {k for k in matched if lein[k]["version"] == sbom[k]["version"]}
    version_mismatch = matched - version_ok
    missing_from_sbom = lein_compile - sbom_keys
    extra_in_sbom = sbom_keys - lein_keys

    # --- Report ---
    W = 55  # column width for dep coordinate

    print("=" * 72)
    print("SBOM Cross-Check: lein deps :tree  vs  Syft CycloneDX")
    print("=" * 72)
    print(f"  Lein compile deps : {len(lein_compile)}")
    print(f"  Lein test deps    : {len(lein_test)}")
    print(f"  SBOM components   : {len(sbom_keys)}")
    print(f"  Exact matches     : {len(version_ok)}")
    print()

    if version_mismatch:
        print(f"⚠️  VERSION MISMATCHES ({len(version_mismatch)})")
        print("-" * 72)
        print(f"  {'Dependency':{W}} {'Lein':>14} {'SBOM':>14}")
        print(f"  {'-'*(W-2):>{W}} {'-'*14:>14} {'-'*14:>14}")
        for k in sorted(version_mismatch):
            coord = f"{k[0]}/{k[1]}"
            print(f"  {coord:{W}} {lein[k]['version']:>14} {sbom[k]['version']:>14}")
        print()

    if missing_from_sbom:
        print(f"❌ IN LEIN BUT MISSING FROM SBOM ({len(missing_from_sbom)})")
        print("-" * 72)
        print(f"  {'Dependency':{W}} {'Version':>14}")
        print(f"  {'-'*(W-2):>{W}} {'-'*14:>14}")
        for k in sorted(missing_from_sbom):
            coord = f"{k[0]}/{k[1]}"
            print(f"  {coord:{W}} {lein[k]['version']:>14}")
        print()

    if extra_in_sbom:
        print(f"➕ IN SBOM BUT NOT IN LEIN ({len(extra_in_sbom)})")
        print("-" * 72)
        print(f"  {'Dependency':{W}} {'Version':>14}")
        print(f"  {'-'*(W-2):>{W}} {'-'*14:>14}")
        for k in sorted(extra_in_sbom):
            coord = f"{k[0]}/{k[1]}"
            print(f"  {coord:{W}} {sbom[k]['version']:>14}")
        print()

    if lein_test:
        print(f"ℹ️  TEST-SCOPED (excluded from comparison) ({len(lein_test)})")
        print("-" * 72)
        for k in sorted(lein_test):
            coord = f"{k[0]}/{k[1]}"
            print(f"  {coord:{W}} {lein[k]['version']:>14}")
        print()

    # Summary
    coverage = len(version_ok) / len(lein_compile) * 100 if lein_compile else 0
    print("=" * 72)
    print(f"Coverage: {len(version_ok)}/{len(lein_compile)} compile deps matched exactly ({coverage:.1f}%)")
    if version_mismatch:
        print(f"          {len(version_mismatch)} version mismatch(es) — investigate stale jars")
    if missing_from_sbom:
        print(f"          {len(missing_from_sbom)} deps missing from SBOM — no META-INF/maven POM in jar")
    print("=" * 72)


if __name__ == "__main__":
    main()
