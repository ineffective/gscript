package com.gs;

import java.util.HashMap;
import com.gs.GSVM.GSVMJustUnwindStackException;

public class Script {
	public static class stuff {
	public HashMap<TestEnum, Object> hmap = new HashMap<TestEnum, Object>();
	}
	public static class io {
		public io pthis;
		public io() { pthis = this; }
		public void print(Object... i) {
			for (Object o: i)
				System.out.print(o);
		
		}
		public void format(String s, Object... o) {
			int arg = 0;
			for (int i =0 ; i < s.length() ; ++i) {
				if (s.charAt(i) != '%') {
					System.out.print(s.charAt(i));
				} else {
					System.out.print(o[arg++]);
				}
			}
		}
	}
	public static class quiet_io {
		public quiet_io pthis;
		public quiet_io() { pthis = this; }
		public void print(Object... i) {
		}
		public void format(String s, Object... o) {
		}
	}

	public static class gs {
		public final Runtime runtime;
		public gs(Runtime r) {
			this.runtime = r;
		}
	}
	private static enum TestEnum {
		VAL_A, VAL_B, VAL_C;
	}
	private static boolean permanentRuntime = false;
	private static Runtime getRuntime() throws Exception {
		if (permanentRuntime == true && r != null) {
			return r;
		}
		Runtime rt = new Runtime();
		if (quiet) {
			rt.put("io", new quiet_io());
		} else {
			rt.put("io", new io());
		}
		rt.put("gs", new gs(r));
		rt.put("TestEnum", TestEnum.class);
		stuff s = new stuff();
		s.hmap.put(TestEnum.VAL_A, new HashMap<String, Object>());
		rt.put("stuff", s);
		return rt;
	}
	public static class MyTimer {
		private long start;
		private long timed_iters;
		public MyTimer(long count) { timed_iters = count; }
		public void start() { start = System.nanoTime(); }
		private final long MINUTE_IN_NS = 60 * 1000000000L;
		private final long SECOND_IN_NS = 1000000000L;
		private final long MILISECOND_IN_NS = 1000000L;
		private final long MICROSECOND_IN_NS = 1000L;
		public void end(GSVM vm) {
			long delta = (System.nanoTime() - start);
			long tmpDelta = delta;
			long minutes = tmpDelta / MINUTE_IN_NS;
			tmpDelta -= minutes * MINUTE_IN_NS;
			long secs = tmpDelta / SECOND_IN_NS;
			tmpDelta -= secs * SECOND_IN_NS;
			long milis = tmpDelta / MILISECOND_IN_NS;
			tmpDelta -= milis * MILISECOND_IN_NS;
			long usecs = tmpDelta / MICROSECOND_IN_NS;
			tmpDelta -= usecs * MICROSECOND_IN_NS;
			long nsecs = tmpDelta; // it's in nanoseconds already
			long instrPerSec = (long)(((double)vm.getIECount() / (double)delta) * 1000000000L);
			System.out.println("-- TIMING");
			System.out.println("total time: " + minutes + "m" + secs + "s" + milis
				+ "ms" + usecs + "us" + nsecs + "ns");
			System.out.println("calls     : " + timed_iters );
			System.out.println("time/call : " + (delta / (timed_iters * 1000))
				+ " usecs.");
			System.out.println("instructs : " + vm.getIECount());
			System.out.println("instr/sec : " + instrPerSec);
			System.out.println("tot usec  : " + delta);
		}
	}
	private static long count = 1;
	private static boolean time = false;
	private static Object run() {
		Object o = null;
		MyTimer t = new MyTimer(count);
		if (time)
			t.start();
		try {
			for (int i = 0 ; i < count ; ++i) {
				o = r.getFunction("main").call(r.getVM());
			}
		}
		catch (GSVMJustUnwindStackException e) {
			System.err.println("Exception: " + e.getCause());
			for (String s: e.gsvmStackTrace) {
				System.err.println(s);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			System.err.println("Execution of function 'main' failed:");
			System.err.println(e.getMessage());
		}
		if (time)
			t.end(r.getVM());
		return o;
	}
	private static Runtime r;
	private static boolean quiet = false;
	public static void showHelp() {
		System.out.println(
			"Parameters accepted by this GameScript runner:\n\n" +
			"\t-d - turn OFF .dot file creation\n" +
			"\t+d - turn  ON .dot file creation\n\n" +
			"\t-q - quiet run\n\n" +
			"\t-c N - run 'main' function N times\n\n" +
			"\t-t [true | false ] - time execution\n\n" +
			"\t-s dump - dump instructions statistic\n" +
			"\t-s reset - reset instructions statistics\n\n" +
			"\t-a - turn OFF disassembling\n" +
			"\t+a FILE - turn ON dissassembling, writer result to FILE\n\n" +
			"\t-b FILE - compile FILE\n\n" +
			"\t-f fILE - compile and run FILE\n\n" +
			"\t+g - generate debug info\n" + 
			"\t-g - DON'T generate debug info\n\n" +
			"\t-R - normal Runtime retention\n" +
			"\t+R - permantent Runtime retention\n\n" +
			"\tSTRING - treat STRING as a program, compile it and execute\n\n" +
			"Command line in scanned from left to right, options take effect " +
			" immediately. This means that \n" +
			"it is possible to compile few files, execute them, time, create .dot " +
			"files and disassemble during single execution.\n\n" +
			"EXAMPLE:\n\n" +
			"com.gs.Script +d out -b prog1.gs -d +a out.gsas -b prog2.gs\n\n" +
			"This creates out.dot and out.opt.dot files for prog1.gs and out.gsas " +
			"file with instruction dump for prog2.gs\n\n"
		);
	}
	public static void main(String[] args) throws Exception {
		int i = 0;
		Runtime.CompilationOpts opts = new Runtime.CompilationOpts();
		try {
			while (i < args.length) {
				if (args[i].equals("-h")) {
					showHelp();
					return;
				} else if (args[i].equals("-d")) {
					opts.makeDot(false);
					i++;
				} else if (args[i].equals("+d")) {
					opts.makeDotFile(args[i + 1]);
					opts.makeDot(true);
					i += 2;
				} else if (args[i].equals("-q")) {
					quiet = true;
					i++;
				} else if (args[i].equals("-c")) {
					count = Integer.parseInt(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-t")) {
					time = Boolean.parseBoolean(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-s")) {
					if (args[i + 1].equals("dump")) {
						r.getVM().printStats();
					} else if (args[i + 1].equals("reset")) {
						r.getVM().resetStats();
					} else {
						throw new Exception("unknown operator for '-s' switch: "
							+ args[i + 1]);
					}
					i += 2;
				} else if (args[i].equals("-f")) {
					opts.setInFile(args[i + 1]);
					r = getRuntime();
					if (r.compileFile(args[i + 1], opts) == false) {
						System.err.println("Compilation of file " + args[i + 1] + " failed.");
						return;
					}
					Object o = run();
					if (!quiet) {
						System.out.println(o);
					}
					i += 2;
				} else if (args[i].equals("-a")) {
					opts.setDisassemble(false);
					i++;
				} else if (args[i].equals("+a")) {
					opts.setDisassemble(true);
					opts.setAsmFile(args[i + 1]);
					i += 2;
				} else if (args[i].equals("-g")) {
					opts.setGenDebug(false);
					i++;
				} else if (args[i].equals("+g")) {
					opts.setGenDebug(true);
					i++;
				} else if (args[i].equals("-o")) {
					opts.setOptimize(false);
					i++;
				} else if (args[i].equals("+o")) {
					opts.setOptimize(true);
					i++;
				} else if (args[i].equals("-R")) {
					permanentRuntime = false;
					i++;
				} else if (args[i].equals("+R")) {
					permanentRuntime = true;
					i++;
				} else if (args[i].equals("-b")) {
					opts.setInFile(args[i + 1]);
					r = getRuntime();
					if (r.compileFile(args[i + 1], opts) == false) {
						System.err.println("Compilation of file " + args[i + 1] + " failed");
						return;
					}
					i += 2;
				} else {
					System.out.println("RUNNING PROGRAM FROM COMMANDLINE");
					opts.setInFile("stdin");
					r = getRuntime();
					if (r.compileString(args[i], opts) == false) {
						System.err.println("Compilation of program " + args[i] + " failed");
						return;
					}
					Object o = run();
					if (!quiet) {
						System.out.println(o);
					}
					i++;
				}
			}
		} catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Input error: " + e.getMessage());
		}
	}
}

