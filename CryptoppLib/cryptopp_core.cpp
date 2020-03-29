// Created by Utsab Singha Roy.

#include "cryptopp_core.h"
#include "cryptopp_inc/osrng.h"
#include "cryptopp_inc/secblock.h"
#include "cryptopp_inc/camellia.h"
#include "cryptopp_inc/modes.h"
#include "cryptopp_inc/filters.h"
#include "cryptopp_inc/hex.h"

namespace cryptopp_core {
    std::vector<CryptoPP::byte> PasswordHashSHA256(const std::string& password) {
        std::vector<CryptoPP::byte> password_hash;
        password_hash.resize(CryptoPP::Camellia::MAX_KEYLENGTH);
        static_assert(CryptoPP::Camellia::MAX_KEYLENGTH == 256/8, "size dont match");
        CryptoPP::SHA256().CalculateDigest(password_hash.data(), (CryptoPP::byte*)(password.data()), password.size());
        return password_hash;
    }

    std::string GetEncryptedResult(const std::string& password, const std::string& plain_text) {

        std::string result;
        // 1. Generate SHA256 hash out of the password and save it into key
        auto password_hash = PasswordHashSHA256(password);
        CryptoPP::SecByteBlock key(password_hash.data(), CryptoPP::Camellia::MAX_KEYLENGTH);

        // 2. Generate the initialization vector
        CryptoPP::AutoSeededRandomPool prng;
        CryptoPP::byte iv[CryptoPP::Camellia::BLOCKSIZE];
        prng.GenerateBlock(iv, sizeof(iv));

        // 3. Encrypt using CBC method
        try {
            CryptoPP::CBC_Mode< CryptoPP::Camellia >::Encryption encryption;
            encryption.SetKeyWithIV(key, key.size(), iv);

            // The StreamTransformationFilter adds padding
            // as required. ECB and CBC Mode must be padded
            // to the block size of the cipher.
            CryptoPP::StringSource(plain_text, true,
                                   new CryptoPP::StreamTransformationFilter(encryption,
                                                                            new CryptoPP::StringSink(result)));

        }
        catch(const CryptoPP::Exception& e) {
            result = "Encryption Failed " + e.GetWhat();
            return result;
        }

        std::string iv_string;
        CryptoPP::ArraySource(iv, sizeof(iv), true,
                              new CryptoPP::HexEncoder(
                                      new CryptoPP::StringSink(iv_string)));

        // Append the initialization vector with the cipher
        // to save them together
        result = iv_string + result;
        return result;
    }

    std::string GetDecryptedResult(const std::string& password, const std::string& encrypted) {

        std::string recovered;
        // Generate SHA256 hash out of the password and save it into key
        auto password_hash = PasswordHashSHA256(password);
        CryptoPP::SecByteBlock key(password_hash.data(), password_hash.size());

        CryptoPP::byte iv[CryptoPP::Camellia::BLOCKSIZE];

        // Initialization vector which was appended during encryption with the cipher
        // Each hex byte need two bytes to be represented as string.
        auto iv_str = encrypted.substr(0, CryptoPP::Camellia::BLOCKSIZE * 2);
        CryptoPP::StringSource(iv_str, true,
                               new CryptoPP::HexDecoder(
                                       new CryptoPP::ArraySink(iv, CryptoPP::Camellia::BLOCKSIZE)));

        // The remaining string is the actual cipher.
        auto cipher = encrypted.substr(CryptoPP::Camellia::BLOCKSIZE * 2);

        // Perform decryption
        try {
            CryptoPP::CBC_Mode< CryptoPP::Camellia >::Decryption decryption;
            decryption.SetKeyWithIV(key, key.size(), iv);

            // The StreamTransformationFilter removes
            // padding as required.
            CryptoPP::StringSource s(cipher, true,
                                     new CryptoPP::StreamTransformationFilter(decryption,
                                                                              new CryptoPP::StringSink(recovered)));

        }
        catch(const CryptoPP::Exception& e) {
            recovered =  "Decryption Failed " + e.GetWhat();
            return recovered;
        }
        return recovered;

    }

}