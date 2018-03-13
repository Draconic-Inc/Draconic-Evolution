package com.rwtema.funkylocomotion.compat;

public @interface ModCompat {
	String modid() default "";
	String classname() default "";
}