def main () {
	switch (1) {
	case 2: ;
	};

	def sum = 0;
	for (def x = 0 ; x < 20; x += 1)
		sum += x;
	io.print(sum,'\n');
	return sum;
}

