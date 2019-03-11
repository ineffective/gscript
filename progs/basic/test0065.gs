def f(a) { 
        return \() { return a(); };
}

def main() {
        return f(\() { return 1; })();
}

