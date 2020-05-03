#
# Makefile for the Java implementation of the Q-Morph algorithm 
# 
# 	author: Karl Erik Levik,	karll@ifi.uio.no 
# 	date: 26.06.2001
#

PACKAGE= meshditor
CLASSPATH= ../bin
BINPATH= $(CLASSPATH)/$(PACKAGE)
SRCDOCPATH= ../srcdoc
TESTDIR= testing
VERSIONSTRING = v1.0
JDKPATH= /usr/local/java/jdk1.3.1

QM_TARGETS = $(BINPATH)/Constants.class $(BINPATH)/Msg.class \
$(BINPATH)/GeomBasics.class $(BINPATH)/QMorph.class \
$(BINPATH)/DelaunayMeshGen.class $(BINPATH)/TopoCleanup.class \
$(BINPATH)/GlobalSmooth.class $(BINPATH)/Element.class $(BINPATH)/Quad.class \
$(BINPATH)/Triangle.class $(BINPATH)/Edge.class $(BINPATH)/Node.class \
$(BINPATH)/Dart.class $(BINPATH)/Ray.class $(BINPATH)/MyVector.class \
$(BINPATH)/MyLine.class

MESHDITOR_TARGET = $(BINPATH)/MeshDitor.class $(BINPATH)/GCanvas.class \
$(BINPATH)/GControls.class $(BINPATH)/GUI.class $(BINPATH)/QMorphOptionsDialog.class \
$(BINPATH)/HelpDialog.class $(BINPATH)/AboutDialog.class \
$(BINPATH)/ExportToLaTeXOptionsDialog.class $(BINPATH)/MyFilterOutputStream.class \
$(BINPATH)/MsgDialog.class 

JNI_TARGET = $(CLASSPATH)/qmorph

JAR_TARGET = $(CLASSPATH)/$(PACKAGE).jar

JAVASOURCES = $(PACKAGE)/QMorph.java $(PACKAGE)/Quad.java \
$(PACKAGE)/Triangle.java $(PACKAGE)/Element.java $(PACKAGE)/Ray.java \
$(PACKAGE)/MyVector.java $(PACKAGE)/MyLine.java $(PACKAGE)/Node.java \
$(PACKAGE)/Dart.java $(PACKAGE)/Edge.java $(PACKAGE)/GUI.java \
$(PACKAGE)/DelaunayMeshGen.java \
$(PACKAGE)/GeomBasics.java $(PACKAGE)/TopoCleanup.java $(PACKAGE)/GlobalSmooth.java \
$(PACKAGE)/Constants.java $(PACKAGE)/Msg.java $(PACKAGE)/MeshDitor.java \
$(PACKAGE)/GCanvas.java $(PACKAGE)/GControls.java $(PACKAGE)/QMorphOptionsDialog.java \
$(PACKAGE)/HelpDialog.java $(PACKAGE)/AboutDialog.java \
$(PACKAGE)/ExportToLaTeXOptionsDialog.java $(PACKAGE)/MyFilterOutputStream.java \
$(PACKAGE)/MsgDialog.java 

CPPSOURCES = qmorph.cpp

SOURCES = $(JAVASOURCES) $(CPPSOURCES)

JC = javac
JAVA= java
JAVADOC = javadoc
JAR= jar
JC_FLAGS = -sourcepath . -d $(CLASSPATH)
CXX = egcs++
CFLAGS = -I$(JDKPATH)/include -I$(JDKPATH)/include/linux 
LIBS = -L$(JDKPATH)/jre/lib/i386/classic -ljvm -L$(JDKPATH)/jre/lib/i386/green_threads -lhpi -L$(JDKPATH)/jre/lib/i386 -ljava -lverify

all: $(QM_TARGETS) $(MESHDITOR_TARGET) $(JAR_TARGET) $(JNI_TARGET) 

# These classes are all part of the main program
$(JAR_TARGET): $(QM_TARGETS) $(MESHDITOR_TARGET) manifest.meshditor.txt 
	cd $(CLASSPATH) && $(JAR) cmf ../src/manifest.meshditor.txt $(PACKAGE).jar \
	$(PACKAGE)/*.class

$(BINPATH)/GeomBasics.class : $(PACKAGE)/GeomBasics.java $(PACKAGE)/Constants.java 
	$(JC) $(JC_FLAGS) $(PACKAGE)/GeomBasics.java

$(BINPATH)/QMorph.class : $(PACKAGE)/QMorph.java $(PACKAGE)/GeomBasics.java 
	$(JC) $(JC_FLAGS) $(PACKAGE)/QMorph.java

$(BINPATH)/DelaunayMeshGen.class : $(PACKAGE)/DelaunayMeshGen.java $(PACKAGE)/GeomBasics.java 
	$(JC) $(JC_FLAGS) $(PACKAGE)/DelaunayMeshGen.java

$(BINPATH)/TopoCleanup.class : $(PACKAGE)/TopoCleanup.java $(PACKAGE)/GeomBasics.java 
	$(JC) $(JC_FLAGS) $(PACKAGE)/TopoCleanup.java

$(BINPATH)/GlobalSmooth.class : $(PACKAGE)/GlobalSmooth.java $(PACKAGE)/GeomBasics.java 
	$(JC) $(JC_FLAGS) $(PACKAGE)/GlobalSmooth.java

$(BINPATH)/Element.class: $(PACKAGE)/Element.java 
	$(JC) $(JC_FLAGS) $(PACKAGE)/Element.java

$(BINPATH)/Quad.class: $(PACKAGE)/Quad.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/Quad.java

$(BINPATH)/Triangle.class: $(PACKAGE)/Triangle.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/Triangle.java

$(BINPATH)/Edge.class: $(PACKAGE)/Edge.java $(PACKAGE)/Constants.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/Edge.java

$(BINPATH)/Node.class: $(PACKAGE)/Node.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/Node.java

$(BINPATH)/Dart.class: $(PACKAGE)/Dart.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/Dart.java

$(BINPATH)/Ray.class: $(PACKAGE)/Ray.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/Ray.java

$(BINPATH)/MyVector.class: $(PACKAGE)/MyVector.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/MyVector.java

$(BINPATH)/MyLine.class: $(PACKAGE)/MyLine.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/MyLine.java

$(BINPATH)/Constants.class: $(PACKAGE)/Constants.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/Constants.java

$(BINPATH)/Msg.class: $(PACKAGE)/Msg.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/Msg.java

# Here come the MeshDitor specific classes:
$(BINPATH)/MeshDitor.class: $(PACKAGE)/MeshDitor.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/MeshDitor.java

$(BINPATH)/GCanvas.class: $(PACKAGE)/GCanvas.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/GCanvas.java

$(BINPATH)/GControls.class: $(PACKAGE)/GControls.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/GControls.java

$(BINPATH)/GUI.class: $(PACKAGE)/GUI.java 
	$(JC) $(JC_FLAGS) $(PACKAGE)/GUI.java

$(BINPATH)/QMorphOptionsDialog.class: $(PACKAGE)/QMorphOptionsDialog.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/QMorphOptionsDialog.java

$(BINPATH)/ExportToLaTeXOptionsDialog.class: $(PACKAGE)/ExportToLaTeXOptionsDialog.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/ExportToLaTeXOptionsDialog.java

$(BINPATH)/MyFilterOutputStream.class: $(PACKAGE)/MyFilterOutputStream.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/MyFilterOutputStream.java

$(BINPATH)/HelpDialog.class: $(PACKAGE)/HelpDialog.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/HelpDialog.java

$(BINPATH)/AboutDialog.class: $(PACKAGE)/AboutDialog.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/AboutDialog.java

$(BINPATH)/MsgDialog.class: $(PACKAGE)/MsgDialog.java
	$(JC) $(JC_FLAGS) $(PACKAGE)/MsgDialog.java

# This is the Java Native Interface example file:
jni $(CLASSPATH)/qmorph: qmorph.o
	$(CXX) $(LIBS) -o $(CLASSPATH)/qmorph qmorph.o

qmorph.o: qmorph.cpp 
	$(CXX) $(CFLAGS) -c qmorph.cpp 


# And some useful maintainence commands:
count:
	@echo "wc --lines on all sources yields:"
	wc --lines $(SOURCES)
tar backup:
	mkbackup

testsrc:
	$(JC) $(JC_FLAGS) $(TESTDIR)/*.java

javadoc:
	cd $(PACKAGE) && \
	$(JAVADOC) -author -d ../$(SRCDOCPATH) -private \
	-overview ../overview.meshditor.html \
	-windowtitle 'MeshDitor $(VERSIONSTRING) API Specification' \
	-doctitle 'MeshDitor $(VERSIONSTRING) API Specification' \
	-stylesheetfile ../stylesheet.meshditor.css \
	*.java

run:
	cd $(CLASSPATH) && java -jar $(PACKAGE).jar

jnirun:
	cd $(CLASSPATH) && qmorph

clean: 
	rm -Rf *.o *.lo core $(QM_TARGETS) $(JNI_TARGET) $(MESHDITOR_TARGET) \
	$(JAR_TARGET) 
	rm -rf $(SRCDOCPATH)/* $(TESTDIR)/*.class

