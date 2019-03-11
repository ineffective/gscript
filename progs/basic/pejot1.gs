def main() {
	def cns = io.getClass().forName("java.util.HashMap").getConstructor();
	def hm = cns.newInstance();
	hm.put("a", 1);
	hm.put("b", 2);
	io.print(hm, "\n");
	return hm.get("a");
}

