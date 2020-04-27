// Created by Utsab Singha Roy.

#include <jni.h>
#include <string>
#include <android/log.h>
#include "../../../../CryptoppLib/cryptopp_core.h"

std::string JstrToCppstr(JNIEnv* env, jstring str) {
    const char* char_arr = env->GetStringUTFChars(str, NULL);
    size_t size = env->GetStringLength(str);
    std::string cpp_str(char_arr, size);
    env->ReleaseStringUTFChars(str, char_arr);
    return cpp_str;
}
extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_example_simplepasswordkeeper_NativeBridge_encryptString(
        JNIEnv* env,
        jobject,
        jstring password,
        jstring plaintext) {

    std::string encrypted = cryptopp_core::GetEncryptedResult(JstrToCppstr(env, password), JstrToCppstr(env, plaintext));
    //__android_log_print(ANDROID_LOG_ERROR, "CPPLOG", "added stuff of size %d, first byte %c" , (int)encrypted.length(), encrypted[0]);
    jbyteArray  byte_array = env->NewByteArray(encrypted.length());
    env->SetByteArrayRegion(byte_array, 0, encrypted.length(), (const jbyte*)encrypted.data());
    //__android_log_print(ANDROID_LOG_ERROR, "CPPLOG", "size of enc data %s" , env->GetArrayLength(byte_array));
    return byte_array;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_simplepasswordkeeper_NativeBridge_decryptValue(
        JNIEnv* env,
        jobject /* this */,
        jstring password,
        jbyteArray encrypted) {
    auto len = env->GetArrayLength(encrypted);
    const jbyte* buff = env->GetByteArrayElements(encrypted, nullptr);
    auto plain_text = cryptopp_core::GetDecryptedResult(JstrToCppstr(env, password), std::string((char*)buff, len));
    return env->NewStringUTF(plain_text.c_str());
}
