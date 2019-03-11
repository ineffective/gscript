// this will fail during execution, because no arguments are passed and single argument is required.
// this will fail during compilation when support for argument count check is implemented.

def fun(a) { return a; }

def main() {
	return fun();
}

