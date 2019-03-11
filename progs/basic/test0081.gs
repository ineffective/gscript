def main() {
	def m = Map[[0, 1], [2, 3]];
	for (def i | m.keySet()) {
		io.print("Key: ", i, " Value: ", m.get(i), "\n");
	}
	return 0;
}
