# java-offshore-maintenance-critical-path

Calculate dependency-safe maintenance order, earliest completion and task float for shutdown planning.

```sh
javac Main.java
java Main --self-test
java Main < examples/input.txt
```

Offline analysis of supplied records. Results depend on the installation data and configured engineering assumptions.

MIT. Built by [brunnodev](https://brunnodev.store).

## Execution proof

[![Executable proof](https://github.com/brunnojob/java-offshore-maintenance-critical-path/actions/workflows/proof.yml/badge.svg)](https://github.com/brunnojob/java-offshore-maintenance-critical-path/actions/workflows/proof.yml)

[Recorded execution and downloadable evidence](https://github.com/brunnojob/java-offshore-maintenance-critical-path/actions/workflows/proof.yml)

Run `python .proof/record.py` after installing the prerequisites above. The scenarios execute repository code and verify exit codes and expected output. CI publishes `execution-proof` with the transcript, input fingerprints and source commit. The downloadable report identifies the exact tested version; the workflow badge tracks the latest run.
