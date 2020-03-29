## **CRYPTOPP Build Instructions**
**Android Build**
1. Downlowd the latest android SDK and NDK from google.
2. In cryptopp-master directory run the follwoing commands to build the library
	 - **export AOSP_API=28**
  	 -	**ANDROID_NDK_ROOT=\<path_to_ndk\>   source ./TestScripts/setenv-android.sh arm8**
   	 	 -	ANDROID_NDK_ROOT is the path which contains toolchains, platforms etc directories
	 	 -	 AOSP_API sets target android API version. 28 is for Pie and above.
	     -	The script takes target architecture as input eg. armv8
	     -	echo $CXX to check if specific compiler is set.
	- Run make command : **make -j8 -f GNUmakefile-cross shared**
		- libcryptopp.so is created in the current directory
3. Run the following to test the build:
	- Build cryptest.exe : **make -j7 -f GNUmakefile-cross cryptest.exe**
	- Create a directory *TEST* from with the following content
		- copy cryptest.exe and libcryptopp.so and TestData directory
		- Find libc++_shared.so from ndk location which is appropriate for the target architecture. Here we are using aarch64 variant for armv8.
		- The contents of *TEST* should look like : 
		  ~~cryptest.exe  libcryptopp.so  libc++_shared.so  TestData~~
		- Push *TEST* to device : **adb push TEST /data/local/tmp**
		- Go to the directory : 
			- **adb shell**
			- **cd /data/local/tmp/TEST**
		- Setup so path : **export LD_LIBRARY_PATH=.**
		- make cryptest.exe executable : **chmod +x cryptest.exe**
		- Run the executable and follow instructions: **./cryptest.exe**
 4. Strip the library to reduce size:
	- Find the platform specific strip binaries from ndk. Here we are using: *aarch64-linux-android-strip* and run :
	- **\<ndk_path\>/aarch64-linux-android-strip libcryptopp.so**
	- **\<ndk_path\>/aarch64-linux-android-strip libc++_shared.so**
	- These two libraries can directly be used in the android app.

 5. [Read the official build description here](https://www.cryptopp.com/wiki/Android_%28Command_Line%29)

 **Linux Build**
 1. To build the code goto cryptopp-master directory arn run  : **make -j8 -f GNUmakefile**
 2. This will create libcryptopp.a in the current directory. To make shared library pass *shared* argument to make command.
 3. Note: clean all the previous .o, .a, .so files before build.

 ## **Creating the App**
 1. This is an android app and as well as Linux commandline app.
 2. The shared code is :
	- cryptopp include files for build. All the header files in cryptopp-master are selected and saved in a dir.
	- Encryption and Decryption routines. 
 3. Platform specific codes are:
	- Platform specific libraries.
	- UI code.
		- In android the platform specific code is the UI and app behavior written in kotlin and JNI to communicate with native code.
		- In Linux, its just some helper function for file io.

 ## **Using the app**
 1. An encrypted file can be created both on the Android device as well as in the linux terminal. The file can be decryped from both the app as well.
 2. The saved encrypted file can be shared between the devices via email, cloud based storage such as google drive etc.
 3. After any modification in the encrypted file, it must be shared/uploaded to cloud again as during each encryption the initialization vector value changes.
 4. The app does not store the decrypted file anywhere. Only prints it in the terminal.

 ## **Acknowledgement**
 -  This project uses [cryptopp](https://github.com/weidai11/cryptopp) 
 -  Icon of the Android app is sourced from [freeicons.io](https://freeicons.io/)

 
