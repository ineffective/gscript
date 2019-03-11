def main() {
	def x = 1;
	x = "a"; // this fails, and rightly so - x has now type Integer, so why String?
	return x;
}

