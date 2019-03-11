def main() {
	def it = List[1, 2, 3, 4].iterator();
	while (it.hasNext()) {
		io.print(it.next(), "\n");
	}
	return 0;
}

