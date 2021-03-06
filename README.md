# Introduction

This project is part of my siv.ing. degree at the Dept. of Informatics, the University of Oslo, 2002.

The paper contains an introduction to the Q-Morph algorithm, as described in the paper _"Advancing Front Quadrilateral Meshing Using Triangle Transformations"_ by S.J. Owen, M.L. Staten, S.A. Canann, and S. Saigal, as well as important background material, discussions of aspects of the implementation, important results, figures of example meshes, references, etc.

The project also includes a Java implementation of the Q-Morph algorithm with a GUI I have called 'MeshDitor'. This was my first Java project.

# Directory structure

The paper is found in the `docs/` directory.

The source code is found in the `src/` directory.

Source code documentation can be generated by running `make javadoc` in the root folder.

To ensure that the implementation works as described, it has been tested on a number of different meshes. Illustrations for some of the meshes are found in the paper, including discussion.

The meshes are found in the `examples/` directory.

# Building and running

Run `make` to build the source code and documentation.

To start the MeshDitor code, run `java -jar meshditor.jar` in the `bin/`
directory, or simply run

```bash
make run
```

in the `src/` directory. To test the Java Native Interface code, run

```bash
make jnirun
```

or go to the `bin/` directory and execute

```bash
./qmorph
```

with or without options or parameters.
