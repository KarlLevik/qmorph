package com.github.karllevik.qmorph;

public class MyTest {
	public static void main(String[] args) {
		B b = new B();
		C c = new C();
		System.out.println("Setting c.a= 2.0");
		A.a = 2.0;
		b.printMe();
		c.printMe();
	}
}

class A {
	public static double a = 1.0;
}

class B extends A {
	public void printMe() {
		System.out.println("a= " + a);
	}
}

class C extends A {
	public void printMe() {
		System.out.println("a= " + a);
	}
}
