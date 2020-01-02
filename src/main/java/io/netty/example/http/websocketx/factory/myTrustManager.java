package io.netty.example.http.websocketx.factory;

import lombok.SneakyThrows;

import javax.net.ssl.X509TrustManager;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

class myTrustManager implements X509TrustManager {



    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @SneakyThrows
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        Exception error = null;

        Certificate g_ca;

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = new BufferedInputStream(
                new FileInputStream(authType));
        g_ca = cf.generateCertificate(caInput);

        if (null == chain || 0 == chain.length)
        {
            error = new CertificateException("Certificate chain is invalid.");
        }
        else if (null == authType || 0 == authType.length())
        {
            error = new CertificateException("Authentication type is invalid.");
        }
        else
        {
            try
            {
                /* 自签名，服务端只发一个证书，可以不用检查证书链 */

                // 检查证书是否过期
                chain[0].checkValidity();

                // 验证是否使用了指定公钥相对应的私钥签署了此证书
                chain[0].verify(g_ca.getPublicKey());

            } catch (NoSuchAlgorithmException e) {
                error = e;
            } catch (NoSuchProviderException e) {
                error = e;
            } catch (SignatureException e) {
                error = e;
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        if (null != error)
        {
            throw new CertificateException(error);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
