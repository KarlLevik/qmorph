# Introduction

This thesis is part of my siv.ing. degree project at the Dept. of
Informatics, the University of Oslo. Apart from an introduction to
the Q-Morph algorithm, as described in the paper
"Advancing Front Quadrilateral Meshing Using Triangle Transformations"
by S.J. Owen, M.L. Staten, S.A. Canann, and S. Saigal,
the thesis consists of important background material, discussions of
every little aspect of the implementation, important results are
stated, figures of some of the example meshes, references, etc.

# Directory structure

The thesis is found in the `docs/` directory.

The project also includes a Java implementation of the Q-Morph
algorithm with a GUI called MeshDitor.

The source code is found in the `src/` directory.

Source code documentation can be generated with `javadoc`.

To ensure that the implementation works as described, it has been
tested on a number of different meshes. A discussion of these and
figures illustrating some of them are found in the thesis.

The meshes are found in the examples/ directory.

# Building and running

Run `make` to build the source, source documentation and the thesis.

To start the MeshDitor code, run `java -jar meshditor.jar` in the `bin/`
directory, or simply run

```bash
make run
```

in the `src/` directory. To test the Java Native Interface code, run

```bash
make jnirun
```

or go to the bin/ directory and execute

```bash
./qmorph
```

with or without options or parameters.
