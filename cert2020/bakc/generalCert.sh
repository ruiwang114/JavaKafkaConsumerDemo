openssl genrsa -des3 -out root.key 2048

openssl req -new -out root-req.csr -key root.key -keyform PEM -subj "/C=CN/ST=Beijing/L=Haidian/O=FRI/OU=FRI/CN=localhost"

openssl x509 -req -in root-req.csr -out root-cert.cer -signkey root.key -CAcreateserial -days 7300

openssl pkcs12 -export -clcerts -in root-cert.cer -inkey root.key -out root.p12

openssl genrsa -des3 -out server-key.key 2048

openssl req -new -out server-req.csr -key server-key.key -subj "/C=CN/ST=Beijing/L=Haidian/O=FRI/OU=FRI/CN=localhost"

openssl x509 -req -in server-req.csr -out server-cert.cer -signkey server-key.key -CA root-cert.cer -CAkey root.key -CAcreateserial -days 7300

openssl pkcs12 -export -clcerts -in server-cert.cer -inkey server-key.key -out server.p12

openssl genrsa -des3 -out client-key.key 2048

openssl req -new -out client-req.csr -key client-key.key -subj "/C=CN/ST=Beijing/L=Haidian/O=FRI/OU=FRI/CN=localhost"

openssl x509 -req -in client-req.csr -out client-cert.cer -signkey client-key.key -CA root-cert.cer -CAkey root.key -CAcreateserial -days 7300

openssl pkcs12 -export -clcerts -in client-cert.cer -inkey client-key.key -out client.p12