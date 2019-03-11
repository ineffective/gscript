// expected result: failure due to undefined variable "y"
def main() {
	def x = y;
	def y = 1;
	return x + y;
}

