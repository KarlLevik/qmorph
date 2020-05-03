/*
 * Example code for running java byte code (.class files) from C++
 * Note that you will need to have the java lib files in your LD_RUN_PATH.
 * These files reside in directories named e.g.:
 * /usr/local/java/jdk1.3.1/jre/lib/i386
 * /usr/local/java/jdk1.3.1/jre/lib/i386/classic
 * /usr/local/java/jdk1.3.1/jre/lib/i386/green_threads
 *
 * See http://www.blackdown.org/java-linux/faq/FAQ-java-linux.html
 * for more information.
 *
 * $Id: helloworld.cpp,v 1.1 1999/12/31 16:22:54 kreilede Exp $
 *
 */

#ifdef BORLAND
#include <vcl\vcl.h>
#pragma hdrstop
#endif

#include <stdio.h>
#include <iostream>
#include <stdlib.h>
#include <jni.h>

#define DEBUG
#ifdef DEBUG
#define CHECK_FOR_EXCEPTIONS \
        if (env->ExceptionOccurred()) {\
            env->ExceptionDescribe();\
            exit(1);\
        }
#else
#define CHECK_FOR_EXCEPTIONS
#endif

int main(int argc, char *argv[])
{
    JavaVMInitArgs vm_args;
    JavaVMOption options[4];
    JavaVM* vm;
    JNIEnv* env;
    jstring jstr;
    jobjectArray args= 0;
	
    options[0].optionString = "-Djava.compiler=NONE"; // disable JIT
    options[1].optionString = "-Djava.class.path=/usr/local/java/jdk1.3.1/jre/lib/rt.jar:/local/java/classes/javagently:/usr/local/java/jdk1.3.1/jre/lib/i18n.jar:."; // user classes
    options[2].optionString = "-Djava.library.path=/usr/local/java/jdk1.3.1/jre/lib/i386:/usr/local/java/jdk1.3.1/jre/lib/i386/classic:/usr/local/java/jdk1.3.1/jre/lib/i386/green_threads:.";  /* set native library path */
    options[3].optionString = "-verbose:jni"; // print JNI-related messages
    
    vm_args.version = JNI_VERSION_1_2;
    vm_args.options = options;
    vm_args.nOptions = 4;
    vm_args.ignoreUnrecognized = JNI_FALSE;
    
    /* Note that in the Java 2 SDK, there is no longer any need to
     * call JNI_GetDefaultJavaVMInitArgs.  */
    if (JNI_CreateJavaVM(&vm, (void**) &env, &vm_args) == 0) {
	
	// Find our class
	jclass cls_MeshDitor= env->FindClass("meshditor/MeshDitor");  
	CHECK_FOR_EXCEPTIONS;
	
	// Get the method ID for the default constructor
	jmethodID cid_MeshDitor = env->GetMethodID(cls_MeshDitor, "<init>", "()V");
	CHECK_FOR_EXCEPTIONS;
	
	// Create an instance
	jobject MeshDitor = env->NewObject(cls_MeshDitor, cid_MeshDitor);
	CHECK_FOR_EXCEPTIONS;
	
	if (argc> 1) {
	    jstr = env->NewStringUTF(argv[1]);
	    if (jstr == 0) {
		fprintf(stderr, "Out of memory\n");
		exit(1);
	    }
	    args = env->NewObjectArray(1,env->FindClass("java/lang/String"), jstr);

	    if (args == 0) {
		fprintf(stderr, "Out of memory\n");
		exit(1);
	    }
	}
	else {
	    args = env->NewObjectArray(0, env->FindClass("java/lang/String"), NULL);
	    if (args == 0) {
		fprintf(stderr, "Out of memory\n");
		exit(1);
	    }
	}
	
	// Get the method ID for the main(String[] args) method
	jmethodID mid_MeshDitor_main =
	    env->GetStaticMethodID(cls_MeshDitor, "main", "([Ljava/lang/String;)V");  
	CHECK_FOR_EXCEPTIONS;
	
	// Call the main(String[] args) method
	env->CallStaticVoidMethod(cls_MeshDitor, mid_MeshDitor_main, args);
	CHECK_FOR_EXCEPTIONS;
	while (1==1) {}
    } 
    else {
	cerr << "Failed to create VM!" << endl;
	exit(1);
    }
}
