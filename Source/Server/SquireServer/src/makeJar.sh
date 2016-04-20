tmpDir=.GWMakeDirTmp
mkdir "$tmpDir"

currentPath=`pwd`
gwClassPath="";
libJars=`find libs/ -name "*.jar"`
echo "**************** Searching for Jars *******************"
for f in $libJars
do
	echo "FOUND FILE: $f"
	(cd "$tmpDir"; jar -xf "../$f")		# extract innards of each jar into the temp dir (for use in building jar)
	gwClassPath="${gwClassPath}:./$f"	# append the jar file to class path, for use in javac (build class files)
done
echo "********************** DONE ***************************"

echo "";
echo "*************** Build Class Files *********************"

# Build the class files with javac
javac -d $tmpDir -cp "$gwClassPath" squire/*.java

echo "********************** DONE ***************************"
echo "";

dirsToInclude=`(cd $tmpDir; ls -d */)`

dirList="";
for d in $dirsToInclude
do
	dirList="$dirList $d"
done

# use the Jar tool to create the jar, including all directories in the temp dir
(cd $tmpDir; jar cvfe ../Server.jar squire.Server $dirList)

rm -r "$tmpDir"
