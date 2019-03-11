def main() {
	def x = 1;
	{
		x = 2;
	}
	return x; // this should return 1, but will return 2. FIXME in next release. probably.
}

