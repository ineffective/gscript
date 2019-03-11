package com.gs.utils;

import java.lang.Iterable;
import java.util.Iterator;
import java.util.Arrays;

public final class VMIterators {
	public static final Iterable<?> getIterable(Object o) {
//		System.out.println("CHECKING: " + o.getClass());
		if (o == null) {
			return null;
		}
		if (o instanceof Iterable) {
//			System.out.println("ALREADY ITERABLE: " + o.getClass());
			return (Iterable<?>)o;
		}
		Class<?> cls = o.getClass();
		if (cls.isArray()) {
//			System.out.println("IS ARRAY...");
			if (cls.getComponentType().isPrimitive()) {
//				System.out.println("OF PRIMITIVE TYPES");
				return get(cls.getComponentType(), o);
			} else {
//				System.out.println("OF OBJECTS!");
				return Arrays.asList((Object[])o);
			}
		}
//		System.out.println("NOT ITERABLE, NOT AN ARRAY: " + o.getClass());
		return null;
	}
	public static final Iterable<?> get(Class<?> cls, final Object o) {

		if (cls == boolean.class) {
			return new Iterable<Boolean>() {
				@Override
				public Iterator<Boolean> iterator() {
					return new Iterator<Boolean>() {
						private int iter = 0;

						@Override
						public boolean hasNext() {
							if (iter < ((boolean[]) o).length)
								return true;
							return false;
						}

						@Override
						public Boolean next() {
							return ((boolean[]) o)[iter++];
						}
					};
				}
			};
		}

		if (cls == byte.class) {
			return new Iterable<Byte>() {
				@Override
				public Iterator<Byte> iterator() {
					return new Iterator<Byte>() {
						private int iter = 0;

						@Override
						public boolean hasNext() {
							if (iter < ((byte[]) o).length)
								return true;
							return false;
						}

						@Override
						public Byte next() {
							return ((byte[]) o)[iter++];
						}
					};
				}
			};
		}

		if (cls == char.class) {
			return new Iterable<Character>() {
				@Override
				public Iterator<Character> iterator() {
					return new Iterator<Character>() {
						private int iter = 0;

						@Override
						public boolean hasNext() {
							if (iter < ((char[]) o).length)
								return true;
							return false;
						}

						@Override
						public Character next() {
							return ((char[]) o)[iter++];
						}
					};
				}
			};
		}

		if (cls == double.class) {
			return new Iterable<Double>() {
				@Override
				public Iterator<Double> iterator() {
					return new Iterator<Double>() {
						private int iter = 0;

						@Override
						public boolean hasNext() {
							if (iter < ((double[]) o).length)
								return true;
							return false;
						}

						@Override
						public Double next() {
							return ((double[]) o)[iter++];
						}
					};
				}
			};
		}

		if (cls == float.class) {
			return new Iterable<Float>() {
				@Override
				public Iterator<Float> iterator() {
					return new Iterator<Float>() {
						private int iter = 0;

						@Override
						public boolean hasNext() {
							if (iter < ((float[]) o).length)
								return true;
							return false;
						}

						@Override
						public Float next() {
							return ((float[]) o)[iter++];
						}
					};
				}
			};
		}

		if (cls == int.class) {
			return new Iterable<Integer>() {
				@Override
				public Iterator<Integer> iterator() {
					return new Iterator<Integer>() {
						private int iter = 0;

						@Override
						public boolean hasNext() {
							if (iter < ((int[]) o).length)
								return true;
							return false;
						}

						@Override
						public Integer next() {
							return ((int[]) o)[iter++];
						}
					};
				}
			};
		}

		if (cls == long.class) {
			return new Iterable<Long>() {
				@Override
				public Iterator<Long> iterator() {
					return new Iterator<Long>() {
						private int iter = 0;

						@Override
						public boolean hasNext() {
							if (iter < ((long[]) o).length)
								return true;
							return false;
						}

						@Override
						public Long next() {
							return ((long[]) o)[iter++];
						}
					};
				}
			};
		}

		if (cls == short.class) {
			return new Iterable<Short>() {
				@Override
				public Iterator<Short> iterator() {
					return new Iterator<Short>() {
						private int iter = 0;

						@Override
						public boolean hasNext() {
							if (iter < ((short[]) o).length)
								return true;
							return false;
						}

						@Override
						public Short next() {
							return ((short[]) o)[iter++];
						}
					};
				}
			};
		}

		return null;
	}
}
