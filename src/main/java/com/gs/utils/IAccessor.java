package com.gs.utils;

public interface IAccessor {
	public Object get() throws Exception;
	public void set(Object o) throws Exception;
	public Object invoke(Object[] args) throws Throwable;
	public void release();
}

