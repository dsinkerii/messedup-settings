using System;
using System.Security.Cryptography;
using System.Text;

public partial class CryptoMessage{
    public static string EncryptMessage(string plainText, string secretKey){
        byte[] key = Encoding.UTF8.GetBytes(secretKey);
        byte[] plainBytes = Encoding.UTF8.GetBytes(plainText);
        key = EnsureKeyLength(key, 32);

        using (Aes aesAlg = Aes.Create()){
            aesAlg.Mode = CipherMode.ECB;
            aesAlg.Padding = PaddingMode.PKCS7;

            ICryptoTransform encryptor = aesAlg.CreateEncryptor(key, null);

            byte[] encryptedBytes = encryptor.TransformFinalBlock(plainBytes, 0, plainBytes.Length);

            return Convert.ToBase64String(encryptedBytes);
        }
    }
    // i kinda realised i dont need this part since we dont need to decrypt anything, so i added this for no reason...
    // eh, still good code, steal it or whatever
    
    /*public static string DecryptMessage(string encryptedText, string secretKey){
        byte[] key = Encoding.UTF8.GetBytes(secretKey);
        byte[] encryptedBytes = Encoding.UTF8.GetBytes(encryptedText);
        key = EnsureKeyLength(key, 32);

        using (Aes aesAlg = Aes.Create()){
            aesAlg.Mode = CipherMode.ECB;
            aesAlg.Padding = PaddingMode.PKCS7;

            ICryptoTransform decryptor = aesAlg.CreateDecryptor();

            byte[] decryptedBytes = decryptor.TransformFinalBlock(encryptedBytes, 0, encryptedBytes.Length);

            return Convert.ToBase64String(decryptedBytes);
        }
    }*/

    private static byte[] EnsureKeyLength(byte[] key, int length)
    {
        if (key.Length == length) return key;
        byte[] newKey = new byte[length];
        Array.Copy(key, newKey, Math.Min(key.Length, length));
        return newKey;
    }
} 
