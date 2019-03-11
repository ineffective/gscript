def main() {
	def ar = Array['a', 'b', 'c', 'd', 'e', 'f'];
	for (def a | ar) {
		for (def b | ar) {
			for (def c | ar) {
				io.print(a, b, c, "\n");
			}
		}
	}
}

