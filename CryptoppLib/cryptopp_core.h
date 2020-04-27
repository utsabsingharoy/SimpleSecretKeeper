// Created by Utsab Singha Roy.


#ifndef MY_APPLICATION_CRYPTOPP_CORE_H
#define MY_APPLICATION_CRYPTOPP_CORE_H

#include "cryptopp_inc/config_int.h"
#include <string>
#include <vector>

namespace cryptopp_core {
    constexpr char ENCRYPTION_FAILED[] = "Encryption Failed ";
    constexpr char DECRYPTION_FAILED[] = "Decryption Failed ";

    std::vector<CryptoPP::byte> PasswordHashSHA256(const std::string& password);
    std::string GetEncryptedResult(const std::string& password, const std::string& plain_text);
    std::string GetDecryptedResult(const std::string& password, const std::string& encrypted);
}

#endif //MY_APPLICATION_CRYPTOPP_CORE_H
