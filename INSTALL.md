For the Java Native Interface C++ code to compile and run correctly, you 
need to have jdk1.3.1 or equivalent libraries. I do not think jdk1.4.0
or higher will work. Also remember to update the `JDKPATH` in `src/Makefile`
and also the paths in `src/qmorph.cpp`.

To compile on Linux or any other UNIX system, all you have to do is
execute:

```bash
make
```
