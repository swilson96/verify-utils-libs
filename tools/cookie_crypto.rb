#!/usr/bin/env ruby

# This script can encode and decode data in the same way as CryptoHelper.java

require 'base64'
require 'OpenSSL'

BASE64_KEY="base64keyhere"
KEY=Base64.decode64(BASE64_KEY)
IV=Random.new.bytes(16)

def _encrypt data
  cipher = OpenSSL::Cipher::AES.new(128, :CBC)
  cipher.encrypt
  cipher.key = KEY
  cipher.iv = IV
  encrypted = cipher.update(data) + cipher.final
  return encrypted
end

def _decrypt data
  decipher = OpenSSL::Cipher::AES.new(128, :CBC)
  decipher.decrypt
  decipher.key = KEY
  decipher.iv = IV
  plain = decipher.update(data) + decipher.final
  return plain
end

def encrypt data
  to_encode = (Random.new.bytes(16)+data).ljust(528)
  puts("encrypting:\n#{to_encode}")  
  puts("output:\n")
  enc = _encrypt(to_encode)
  puts(Base64.encode64(enc).delete("\n"))
end

def decrypt data
  to_decode = Base64.decode64(data)
  puts("decrypting:\n#{data}")
  puts("output:\n")
  dec = _decrypt(to_decode)
  puts(dec)
end

if (ARGV.size>1)
  if (ARGV[0]=="enc")
    encrypt(ARGV[1])
  end
  if (ARGV[0]=="dec")
    decrypt(ARGV[1])
  end
else
  puts("usage: \n\t./enc.rb enc string\n\t./enc.rb dec base64encodedstring")
end
