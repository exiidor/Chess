#!/bin/bash
rm -r ./certificates/*
sudo cp -a /var/snap/lego/common/.lego/certificates/. ./certificates/
sudo chown -R azureuser ./certificates/
cd ./certificates/

openssl pkcs12 -export \
  -in example.com.crt \
  -inkey example.com.key \
  -out example.com.p12 \
  -name example.com \
  -passout pass:123456
