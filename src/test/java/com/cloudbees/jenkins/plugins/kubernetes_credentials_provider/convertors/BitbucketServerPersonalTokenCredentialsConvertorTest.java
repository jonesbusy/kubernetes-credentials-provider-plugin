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

import java.io.InputStream;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.utils.Serialization;
import static org.junit.Assert.*;
import com.atlassian.bitbucket.jenkins.internal.config.BitbucketTokenCredentialsImpl;
import org.junit.Test;
import com.cloudbees.jenkins.plugins.kubernetes_credentials_provider.CredentialsConvertionException;
import com.cloudbees.plugins.credentials.CredentialsScope;

import static org.hamcrest.CoreMatchers.*;

/**
 * Tests BitbucketServerPersonalTokenCredentialsConvertor
 */
public class BitbucketServerPersonalTokenCredentialsConvertorTest {


    @Test
    public void canConvert() throws Exception {
        BitbucketServerPersonalTokenCredentialsConvertor convertor = new BitbucketServerPersonalTokenCredentialsConvertor();
        assertThat("correct registration of valid type", convertor.canConvert("bitbucketServerPersonalToken"), is(true));
        assertThat("incorrect type is rejected", convertor.canConvert("something"), is(false));
    }

    @Test
    public void canConvertAValidSecret() throws Exception {
        BitbucketServerPersonalTokenCredentialsConvertor convertor = new BitbucketServerPersonalTokenCredentialsConvertor();

        try (InputStream is = get("valid.yaml")) {
            Secret secret = Serialization.unmarshal(is, Secret.class);
            assertThat("The Secret was loaded correctly from disk", notNullValue());
            BitbucketTokenCredentialsImpl credential = convertor.convert(secret);
            assertThat(credential, notNullValue());
            assertThat("credential id is mapped correctly", credential.getId(), is("a-test-secret"));
            assertThat("credential description is mapped correctly", credential.getDescription(), is("secret bitbucket personal token credential from Kubernetes"));
            assertThat("credential token is mapped correctly", credential.getSecret().getPlainText(), is("mySecret!"));
        }
    }

    @Test
    public void canConvertAValidMappedSecret() throws Exception {
        BitbucketServerPersonalTokenCredentialsConvertor convertor = new BitbucketServerPersonalTokenCredentialsConvertor();

        try (InputStream is = get("valid.yaml")) {
            Secret secret = Serialization.unmarshal(is, Secret.class);
            assertThat("The Secret was loaded correctly from disk", notNullValue());
            BitbucketTokenCredentialsImpl credential = convertor.convert(secret);
            assertThat(credential, notNullValue());
            assertThat("credential id is mapped correctly", credential.getId(), is("a-test-secret"));
            assertThat("credential description is mapped correctly", credential.getDescription(), is("secret bitbucket personal token credential from Kubernetes"));
            assertThat("credential scope is mapped correctly", credential.getScope(), is(CredentialsScope.SYSTEM));
            assertThat("credential text is mapped correctly", credential.getSecret().getPlainText(), is("mySecret!"));
        }
    }

    @Test
    public void failsToConvertWhenTextMissing() throws Exception {
        BitbucketServerPersonalTokenCredentialsConvertor convertor = new BitbucketServerPersonalTokenCredentialsConvertor();

        try (InputStream is = get("missingText.yaml")) {
            Secret secret = Serialization.unmarshal(is, Secret.class);
            convertor.convert(secret);
            fail("Exception should have been thrown");
        } catch (CredentialsConvertionException cex) {
            assertThat(cex.getMessage(), containsString("missing the token"));
        }
    }

    @Test
    public void failsToConvertWhenUsernameCorrupt() throws Exception {
        BitbucketServerPersonalTokenCredentialsConvertor convertor = new BitbucketServerPersonalTokenCredentialsConvertor();

        try (InputStream is = get("corruptText.yaml")) {
            Secret secret = Serialization.unmarshal(is, Secret.class);
            convertor.convert(secret);
            fail("Exception should have been thrown");
        } catch (CredentialsConvertionException cex) {
            assertThat(cex.getMessage(), containsString("invalid token"));
        }
    }

    @Test
    public void failsToConvertWhenDataEmpty() throws Exception {
        BitbucketServerPersonalTokenCredentialsConvertor convertor = new BitbucketServerPersonalTokenCredentialsConvertor();

        try (InputStream is = get("void.yaml")) {
            Secret secret = Serialization.unmarshal(is, Secret.class);
            convertor.convert(secret);
            fail("Exception should have been thrown");
        } catch (CredentialsConvertionException cex) {
            assertThat(cex.getMessage(), containsString("contains no data"));
        }
    }

    private static final InputStream get(String resource) {
        InputStream is = StringCredentialCredentialsConvertorTest.class.getResourceAsStream("BitbucketServerPersonalTokenCredentialsConvertorTest/" + resource);
        if (is == null) {
            fail("failed to load resource " + resource);
        }
        return is;
    }
}