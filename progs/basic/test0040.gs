// this test checks whether it is viable to assign multiple times to the same variable
def main() {
	def x = 10;
	x = 11;
	x = 12;
	io.print(x);
}

