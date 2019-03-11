def main() {
	def l = List[1, 2, 3, 4];
	io.print("l[3]: ", l[3], "\n");
	l[3] = "a";
	io.print("l[3]: ", l[3], "\n");
	return l;
}

