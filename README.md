# Introduction

This project is part of my siv.ing. degree at the Dept. of Informatics, the University of Oslo, 2002.

The paper contains an introduction to the Q-Morph algorithm, as described in the paper _"Advancing Front Quadrilateral Meshing Using Triangle Transformations"_ by S.J. Owen, M.L. Staten, S.A. Canann, and S. Saigal, as well as important background material, discussions of aspects of the implementation, important results, figures of example meshes, references, etc.

The project also includes a Java implementation of the Q-Morph algorithm with a GUI I have called `MeshDitor`. This was my first Java project.

# Directory structure

The paper is found in the `docs/` directory.

The source code is found in the `src/` directory.

To ensure that the implementation works as described, it has been tested on a number of different meshes. Illustrations for some of the meshes are found in the paper, including discussion.

The meshes are found in the `examples/` directory.

# Building and Running

This is a Maven project, so it can be easily built and run using standard Maven commands.

To build the project, run:

```bash
mvn clean install
```

To launch the `MeshDitor` GUI, run:

```bash
mvn exec:java -Dexec.mainClass="com.github.karllevik.qmorph.viewer.MeshDitor"
```

This will start the application, allowing you to load mesh files from the `examples/` directory and interact with the Q-Morph algorithm.