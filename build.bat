javac Facelimiter.java
jar cvfme Facelimiter.jar manifest.mf Facelimiter *.class
jarsigner -keystore myKeyStore Facelimiter.jar 2mrc
move /Y Facelimiter.jar static