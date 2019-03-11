PATH=$PATH:$(cygpath 'c:/stuff/maven/bin/')

export PATH
#echo RUNNING WITH PATH: $PATH
#java -version

g() {
	mvn generate-sources
}
c() {
	rm -rf src/main/java/com/gs/parser/GameScript*
	mvn clean
	rm -f *.dot *.raw *.svg *.html *.png *.asm
	bash bin/gsprof clean
}

maven_build() {
	mvn package && ( unzip -c target/gamescript.jar META-INF/MANIFEST.MF 2>/dev/null )
}
case "$1"
in
	b) b; ;;
	B) j; ;;
	c) c; ;;
	g) g; exit; ;;
	j) j; exit; ;;
	a) j ;;
	*) maven_build ;;
esac

