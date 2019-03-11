PATH=$PATH:$(cygpath 'c:/stuff/maven/bin/')

export PATH

do_gensrc() { ## generate sources
	mvn generate-sources
}
do_clean() { ## clean source tree
	rm -rf src/main/java/com/gs/parser/GameScript*
	mvn clean
	rm -f *.dot *.raw *.svg *.html *.png *.asm
	bash bin/gsprof clean
}

do_build() { ## build everything
	mvn package && ( unzip -c target/gamescript.jar META-INF/MANIFEST.MF 2>/dev/null )
}
do_help() { ## show help
echo "Help for ${0}:"
grep -E '^do_[a-z]+[(][)] [{] ##' "$0" | while read cmd junk1 junk2 msg ; do echo $(echo $cmd | cut -f2 -d_ | cut -f1 -d\() - $msg ; done
}
for x ; do
	cmd=do_$x
	if [ "$(type -t $cmd)" = "function" ] ; then
		$cmd
	else
		do_help
	fi
done

