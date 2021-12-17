rm -rf me
javac  -implicit:class  -cp "src/main/java:tg-bridge-1.3.jar" src/main/java/me/fulcanelly/dither/Main.java -d here
mv here/me me/
jar cfev0 dither.jar me.fulcanelly.dither.Main me
