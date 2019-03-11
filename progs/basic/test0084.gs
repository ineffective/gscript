def main() {
	def n = "Napis";
	for (def char | n.toCharArray()) {
		io.print("'", char, "'", "\n");
	}
	return 0;
}
