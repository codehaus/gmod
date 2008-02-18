/*
 * Copyright 2007-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.junctions

import org.apache.commons.httpclient.*
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.*

class HttpHelper {
    HttpClient httpClient = new HttpClient()

    def post(url, data, parse = true) {
        def postData = []
        for (pair in data.keySet()) {
            def nameValuePair = new NameValuePair(pair, String.valueOf(data[pair]))
            postData += nameValuePair
        }

        PostMethod post = null
        try {
            post = new PostMethod(url)
            post.setRequestBody(postData as NameValuePair[])
            httpClient.executeMethod(post)
            def result = post.getResponseBodyAsStream().text
            if (parse) {
                return new XmlSlurper().parseText(result)
            }
        } catch (Exception e) {
            e.printStackTrace()
        } finally {
            post.releaseConnection()
        }
        return null
    }

    def get(url) {
        def data = url.toURL().text
        return new XmlSlurper().parseText(data)
    }
}
