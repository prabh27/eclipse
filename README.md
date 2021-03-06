# Maxeler Eclipse Plug-ins [![Build Status](https://travis-ci.org/maxeler/eclipse.svg?branch=master)](https://travis-ci.org/maxeler/eclipse)

## [Eclipse Compiler for Java](https://github.com/maxeler/eclipse/tree/master/eclipse.jdt.core/org.eclipse.jdt.core)
Supports **operator overloading**, which is part of Maxeler Java syntax.

## [.MAXJ Editor](https://github.com/maxeler/eclipse/tree/master/eclipse.jdt.ui/org.eclipse.jdt.ui)
Supports **MaxJ syntax** on .maxj files:
  * Syntax highlighting
  * Code proposal, code completion, code templates
  * Refactoring
  * Outline
  * Search features

***
***

### Prerequisites
#### Oracle Java 1.8
* Oracle 1.8 JDK needs to be on PATH
* Verify correct version of java is used
* Set JAVA_HOME to point to your JDK
* Ensure your java is set to run in Server mode

##### Java Server Mode
A parameter used by the build "-XX:-UseLoopPredicate" is not recognized by java when running in Client mode. To check which mode you are running in by default run:
```
$ java -version
java version "1.8.0_60"
Java(TM) SE Runtime Environment (build 1.8.0_60-b27)
Java HotSpot(TM) 64-Bit Server VM (build 25.60-b23, mixed mode)
```
If the last line says "Client VM" instead of "Server VM" then you are running in client mode. In which case you will need to modify the file **/jdk1.8.0/jre/lib/amd64/jvm.cfg** and ensure that the line **-server KNOWN** is the first line in the file.

#### Apache Maven 3.1.1 or higher
* Download from http://maven.apache.org/download.html
* make sure **mvn** is available in your PATH

***
***

### Checkout
Clone the repository and checkout the branch corresponding to your Eclipse version (e.g. Luna SR2):
```
git clone https://github.com/maxeler/eclipse.git
git checkout R4_4_maintenance
cd eclipse
```

### Build each plug-in
```
mvn -f eclipse.jdt.core/org.eclipse.jdt.core/pom.xml -P build-individual-bundles package
mvn -f eclipse.jdt.ui/org.eclipse.jdt.ui/pom.xml -P build-individual-bundles package
```

### Install
Copy the built plug-in .jar files to your Eclipse plug-ins folder (e.g. Luna SR2):
```
cp eclipse.jdt.core/org.eclipse.jdt.core/target/org.eclipse.jdt.core-3.10.2-SNAPSHOT.jar <path-to-eclipse-luna-SR2>/plugins/
cp eclipse.jdt.ui/org.eclipse.jdt.ui/target/org.eclipse.jdt.ui-3.10.2-SNAPSHOT.jar <path-to-eclipse-luna-SR2>/plugins/
```

### Configure
Copy the "Bundle-Version" from the built MANIFEST.MF files:
```
$ grep -e "Bundle-Version: " eclipse.jdt.core/org.eclipse.jdt.core/target/MANIFEST.MF
Bundle-Version: 3.10.2.v20151116-1448
$ grep -e "Bundle-Version: " eclipse.jdt.ui/org.eclipse.jdt.ui/target/MANIFEST.MF
Bundle-Version: 3.10.2.v20151116-1448
```
Edit the following configuration file, in eclipse's configuration directory:
```
<path-to-eclipse-luna-SR2>/configuration/org.eclipse.equinox.simpleconfigurator/bundles.info
```
update the following lines:
```
org.eclipse.jdt.core,3.10.2.v20150120-1634,plugins/org.eclipse.jdt.core_3.10.2.v20150120-1634.jar,4,false
org.eclipse.jdt.ui,3.10.2.v20141014-1419,plugins/org.eclipse.jdt.ui_3.10.2.v20141014-1419.jar,4,false
```
with the correct "Bundle-Version" and file names:
```
org.eclipse.jdt.core,3.10.2.v20151116-1448,plugins/org.eclipse.jdt.core-3.10.2-SNAPSHOT.jar,4,false
org.eclipse.jdt.ui,3.10.2.v20151116-1448,plugins/org.eclipse.jdt.ui-3.10.2-SNAPSHOT.jar,4,false
```

### Run
Launch Eclipse and import your MAX project source code as a Java Project.
