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
	// FIXME: ...BECAUSE OF ASSIGNMENT IN WHILE. IT IS NOT SUPPORTED.
	def i = null;
	while ((i = g()) != null) {
		io.print(i, "\n");
	}
	return 0;
}

