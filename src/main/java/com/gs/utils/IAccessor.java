package com.gs.utils;

public interface IAccessor {
	public Object get() throws Throwable;
	public void set(Object o) throws Throwable;
	public Object invoke(Object[] args) throws Throwable;
	public void release();
}

