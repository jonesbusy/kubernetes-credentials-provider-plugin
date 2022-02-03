/*
 * The MIT License
 *
 * Copyright 2022 CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.cloudbees.jenkins.plugins.kubernetes_credentials_provider.convertors;

import com.cloudbees.jenkins.plugins.kubernetes_credentials_provider.CredentialsConvertionException;
import com.cloudbees.jenkins.plugins.kubernetes_credentials_provider.SecretToCredentialConverter;
import com.cloudbees.jenkins.plugins.kubernetes_credentials_provider.SecretUtils;
import com.atlassian.bitbucket.jenkins.internal.config.BitbucketTokenCredentialsImpl;
import io.fabric8.kubernetes.api.model.Secret;
import org.jenkinsci.plugins.variant.OptionalExtension;

/**
 * SecretToCredentialConvertor that converts {@link BitbucketTokenCredentialsImpl}.
 */
@OptionalExtension(requirePlugins={"atlassian-bitbucket-server-integration"})
public class BitbucketServerPersonalTokenCredentialsConvertor extends SecretToCredentialConverter {

    @Override
    public boolean canConvert(String type) {
        return "bitbucketServerPersonalToken".equals(type);
    }

    @Override
    public BitbucketTokenCredentialsImpl convert(Secret secret) throws CredentialsConvertionException {

        // ensure we have some data
        SecretUtils.requireNonNull(secret.getData(), "bitbucketServerPersonalToken definition contains no data");

        String tokenBase64 = SecretUtils.getNonNullSecretData(secret, "token", "bitbucketServerPersonalToken credential is missing the token");

        String secretText = SecretUtils.requireNonNull(SecretUtils.base64DecodeToString(tokenBase64), "bitbucketServerPersonalToken credential has an invalid token (must be base64 encoded UTF-8)");

        return new BitbucketTokenCredentialsImpl(SecretUtils.getCredentialId(secret), SecretUtils.getCredentialDescription(secret), hudson.util.Secret.fromString(secretText));

    }

}
