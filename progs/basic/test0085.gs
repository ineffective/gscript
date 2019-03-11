def top() {
	def x = null;
	return x.getClass();
}

def medium() {
	return top();
}

def low() {
	return medium();
}

def main() {
	medium();
}

