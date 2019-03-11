def make_generator(from, to)
{
	def i = from;
	return \() {
		def tmp = i;
		i = i + 1 ;
		if (i > to) {
			return null;
		}
		return tmp ;
	};
}
def main()
{
	def g = make_generator(0, 10);
	def i = 0;
	for (i = g(); i != null ; i = g()) {
		io.print(i, "\n");
	}
	return 0;
}

