#include <string>
#include <fstream>
#include <iostream>
#include <streambuf>
#include <cstring>

/*
#include "osrng.h"
#include "secblock.h"
#include "camellia.h"
#include "modes.h"
#include "filters.h"
#include "hex.h"
*/

#include "cryptopp_core.h"

/*
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
        result = ENCRYPTION_FAILED + e.GetWhat();
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
        recovered =  DECRYPTION_FAILED + e.GetWhat();
        return recovered;
    }
    return recovered;
}
*/
/**** LINUX SPECIFIC ***/
std::string ReadFromFile(const std::string& filename) {
    std::ifstream fd(filename);
    fd.seekg(0, std::ios::end);
    std::string filecontent;
    filecontent.reserve(fd.tellg());
    fd.seekg(0, std::ios::beg);
    filecontent.assign(std::istreambuf_iterator<char>(fd), std::istreambuf_iterator<char>());
    return filecontent;
}

int PrintDecryptedResultFromFile(const std::string& password, const std::string& filename) {
    
    auto decrypted_text = cryptopp_core::GetDecryptedResult(password, ReadFromFile(filename));
    if (decrypted_text.find(cryptopp_core::DECRYPTION_FAILED) != std::string::npos)
        return 1;
    std::cout << decrypted_text;
    return 0;
}

int WriteEncryptedResultToFile(const std::string& password, 
                                const std::string& plaintext, const std::string& outputfilename) {
    std::string encrypted = cryptopp_core::GetEncryptedResult(password, plaintext);
    if(encrypted.find(cryptopp_core::ENCRYPTION_FAILED) != std::string::npos) {
        //std::cout << encrypted << "\n";
        return 1;
    }
    std::ofstream outf(outputfilename);
    outf << encrypted;
    outf.close();
    return 0;
}

constexpr char HELP_MESSAGE[] = "INVALID USE\n"
                                "For encryption : ./a.out encrypt <inputfile> <outputfile> <password> \n"
                                "For Decryption : ./a.out decrypt <inputfile> <password>\n";
constexpr char ENCRYPT_CMD[] = "encrypt";
constexpr char DECRYPT_CMD[] = "decrypt";
int main(int argc, char** argv) {
    auto HelpPrinter = [](){ std::cout << HELP_MESSAGE; }; 
    
    if(argc < 2) {
        HelpPrinter();
        return 0;
    }
    
    if(!std::strcmp(argv[1], ENCRYPT_CMD)) {
        if(argc == 5) {
            return WriteEncryptedResultToFile(argv[4], argv[2], argv[3]);
        }
        else {
            HelpPrinter();
        }
    }
    else if(!std::strcmp(argv[1],DECRYPT_CMD)) {
        if(argc == 4) {
            return PrintDecryptedResultFromFile(argv[3], argv[2]);
        }
        else {
            HelpPrinter();
        }
    }
    else {
        HelpPrinter();
    }
    return 0;
}
