# connectfour
A re-implementation of the popular Connect 4 game in Java

## Dependencies
 - JDK 8 (it probably works with Java 9-13 too, but that's untested)
 - (Optional) Git

## Build Instructions
This should work on Windows, macOS, and Linux.

```bash
git clone https://github.com/potatoeggy/connectfour.git # or download and unzip the repo manually
cd connectfour/src
javac MainWindow.java
```
At this point, you can either run MainWindow alone with `java MainWindow`, or build the whole thing into a jar:
```bash
jar cfe ConnectFour.jar MainWindow *.class resources
java -jar ConnectFour.jar
```
